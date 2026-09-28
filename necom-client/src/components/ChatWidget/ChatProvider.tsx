import React, { createContext, useCallback, useContext, useEffect, useMemo, useRef, useState } from 'react';
import { StompSessionProvider, useStompClient, useSubscription } from 'react-stomp-hooks';
import ApplicationConstants from 'constants/ApplicationConstants';
import ResourceURL from 'constants/ResourceURL';
import FetchUtils from 'utils/FetchUtils';
import NotifyUtils from 'utils/NotifyUtils';
import useAuthStore from 'stores/use-auth-store';
import { ChatEvent, ChatTyping, MessageResponse } from 'models/Message';
import { RoomResponse } from 'models/Room';
import { ClientRoomExistenceResponse } from 'types';
import { LocalMessageState } from 'components/ChatWidget/ChatMessageItem';

export interface LocalMessage {
  clientMsgId: string;
  content: string;
  createdAt: string;
  state: LocalMessageState;
}

export interface ChatContextValue {
  // Đã đăng nhập (khách vãng lai chỉ thấy các kênh liên hệ)
  enabled: boolean;
  loading: boolean;
  connected: boolean;
  botEnabled: boolean;
  room: RoomResponse | null;
  messages: MessageResponse[];
  pending: LocalMessage[];
  typing: ChatTyping | null;
  hasMore: boolean;
  unread: number;
  opened: boolean;
  preview: MessageResponse | null;
  open: () => void;
  close: () => void;
  // Trang "Yêu cầu tư vấn" đang hiển thị (tính như đang mở để đánh dấu đã đọc)
  setPageVisible: (visible: boolean) => void;
  dismissPreview: () => void;
  send: (content: string) => void;
  retry: (clientMsgId: string) => void;
  requestAgent: () => void;
  resolve: () => void;
  loadOlder: () => Promise<void>;
}

const ChatContext = createContext<ChatContextValue | null>(null);

export function useChat(): ChatContextValue {
  const context = useContext(ChatContext);
  if (!context) {
    throw new Error('useChat must be used inside ChatProvider');
  }
  return context;
}

const PAGE_SIZE = 30;
const SEND_TIMEOUT_MS = 10000;

const uuid = () => (window.crypto && 'randomUUID' in window.crypto)
  ? window.crypto.randomUUID()
  : `${Date.now()}-${Math.random().toString(36).slice(2, 11)}`;

/**
 * Bọc toàn bộ trang khách: kết nối STOMP (JWT trong frame CONNECT) khi đã đăng nhập,
 * cung cấp state chat dùng chung cho FAB, cửa sổ chat nổi và trang "Yêu cầu tư vấn".
 */
function ChatProvider({ children }: { children: React.ReactNode }) {
  const { jwtToken, user } = useAuthStore();

  const connectHeaders = useMemo(() => ({ Authorization: `Bearer ${jwtToken}` }), [jwtToken]);

  // StompSessionProvider tạo lại kết nối khi bất kỳ option nào đổi tham chiếu, nên các callback phải ổn định
  const onStompError = useCallback((frame: { headers: Record<string, string> }) => {
    // eslint-disable-next-line no-console
    console.warn('STOMP error', frame.headers?.message);
  }, []);

  if (!jwtToken || !user) {
    return <GuestChatSession>{children}</GuestChatSession>;
  }

  return (
    <StompSessionProvider
      url={ApplicationConstants.WEBSOCKET_PATH}
      connectHeaders={connectHeaders}
      reconnectDelay={5000}
      onStompError={onStompError}
    >
      <ChatSession>{children}</ChatSession>
    </StompSessionProvider>
  );
}

function GuestChatSession({ children }: { children: React.ReactNode }) {
  const [opened, setOpened] = useState(false);
  const noop = useCallback(() => undefined, []);
  const open = useCallback(() => setOpened(true), []);
  const close = useCallback(() => setOpened(false), []);

  const value = useMemo<ChatContextValue>(() => ({
    enabled: false,
    loading: false,
    connected: false,
    botEnabled: false,
    room: null,
    messages: [],
    pending: [],
    typing: null,
    hasMore: false,
    unread: 0,
    opened,
    preview: null,
    open,
    close,
    setPageVisible: noop,
    dismissPreview: noop,
    send: noop,
    retry: noop,
    requestAgent: noop,
    resolve: noop,
    loadOlder: () => Promise.resolve(),
  }), [opened, noop, open, close]);

  return <ChatContext.Provider value={value}>{children}</ChatContext.Provider>;
}

function ChatSession({ children }: { children: React.ReactNode }) {
  const stompClient = useStompClient();
  const connected = !!stompClient && stompClient.connected;

  const [loading, setLoading] = useState(true);
  const [botEnabled, setBotEnabled] = useState(false);
  const [room, setRoom] = useState<RoomResponse | null>(null);
  const [messages, setMessages] = useState<MessageResponse[]>([]);
  const [pending, setPending] = useState<LocalMessage[]>([]);
  const [typing, setTyping] = useState<ChatTyping | null>(null);
  const [hasMore, setHasMore] = useState(false);
  const [unread, setUnread] = useState(0);
  const [opened, setOpened] = useState(false);
  const [pageVisible, setPageVisible] = useState(false);
  const [preview, setPreview] = useState<MessageResponse | null>(null);

  const roomRef = useRef<RoomResponse | null>(null);
  roomRef.current = room;
  const messagesRef = useRef<MessageResponse[]>([]);
  messagesRef.current = messages;
  const pendingRef = useRef<LocalMessage[]>([]);
  pendingRef.current = pending;
  const wasConnectedRef = useRef(false);
  const typingTimerRef = useRef<number>();

  const viewing = opened || pageVisible;
  const viewingRef = useRef(viewing);
  viewingRef.current = viewing;

  // ========== Tải phòng và tin gần nhất ==========
  useEffect(() => {
    let cancelled = false;
    let retryTimer: number | undefined;
    const load = (attempt: number) => FetchUtils.getWithToken<ClientRoomExistenceResponse>(ResourceURL.CLIENT_CHAT_GET_ROOM)
      .then(response => {
        if (cancelled) {
          return;
        }
        setBotEnabled(response.botEnabled);
        if (response.roomExistence && response.roomResponse) {
          setRoom(response.roomResponse);
          setMessages(response.roomRecentMessages);
          setHasMore(response.roomRecentMessages.length >= PAGE_SIZE);
          setUnread(response.roomResponse.unreadCount || 0);
        }
      })
      // Server đang khởi động lại hoặc mạng chập chờn: thử lại vài lần
      .catch(() => {
        if (!cancelled && attempt < 3) {
          retryTimer = window.setTimeout(() => load(attempt + 1), 3000 * (attempt + 1));
        }
      })
      .finally(() => !cancelled && setLoading(false));
    load(0);
    return () => {
      cancelled = true;
      window.clearTimeout(retryTimer);
    };
  }, []);

  const markRead = useCallback(() => {
    const currentRoom = roomRef.current;
    if (!currentRoom) {
      return;
    }
    setUnread(0);
    setPreview(null);
    FetchUtils.postWithToken(ResourceURL.CLIENT_CHAT_READ(currentRoom.id), {}).catch(() => undefined);
  }, []);

  // Mở cửa sổ chat hoặc trang tư vấn thì đánh dấu đã đọc
  useEffect(() => {
    if (viewing && unread > 0) {
      markRead();
    }
  }, [viewing, unread, markRead]);

  // ========== Nhận sự kiện realtime ==========
  const appendMessage = useCallback((message: MessageResponse) => {
    setMessages(current => current.some(m => m.id === message.id) ? current : [...current, message]);
    if (message.clientMsgId) {
      setPending(current => current.filter(p => p.clientMsgId !== message.clientMsgId));
    }
  }, []);

  useSubscription(room ? ['/chat/receive/' + room.id] : [], (frame) => {
    const event: ChatEvent = JSON.parse(frame.body);
    if (event.kind === 'MESSAGE' && event.message) {
      const message = event.message;
      appendMessage(message);
      if (message.senderType === 'AGENT' || message.senderType === 'BOT') {
        setTyping(null);
        if (!viewingRef.current || document.hidden) {
          setPreview(message);
        }
      }
    } else if (event.kind === 'ROOM' && event.room) {
      setRoom(event.room);
      if (viewingRef.current && !document.hidden) {
        if ((event.room.unreadCount || 0) > 0) {
          markRead();
        }
      } else {
        setUnread(event.room.unreadCount || 0);
      }
    } else if (event.kind === 'TYPING' && event.typing) {
      const typingEvent = event.typing;
      window.clearTimeout(typingTimerRef.current);
      if (typingEvent.active) {
        setTyping(typingEvent);
        // Phòng khi không nhận được sự kiện tắt
        typingTimerRef.current = window.setTimeout(() => setTyping(null), 30000);
      } else {
        setTyping(null);
      }
    }
  });

  // ========== Gửi tin ==========
  const publish = useCallback((roomId: number, local: LocalMessage) => {
    if (!stompClient || !stompClient.connected) {
      // Giữ trong hàng chờ, tự gửi khi kết nối lại
      return;
    }
    stompClient.publish({
      destination: '/chat/send/' + roomId,
      body: JSON.stringify({ content: local.content, clientMsgId: local.clientMsgId }),
    });
    window.setTimeout(() => {
      setPending(current => current.map(p => (
        p.clientMsgId === local.clientMsgId && p.state === 'sending' ? { ...p, state: 'failed' } : p
      )));
    }, SEND_TIMEOUT_MS);
  }, [stompClient]);

  const ensureRoom = useCallback(async (): Promise<RoomResponse | null> => {
    if (roomRef.current) {
      return roomRef.current;
    }
    try {
      const created = await FetchUtils.postWithToken<Record<string, never>, RoomResponse>(ResourceURL.CLIENT_CHAT_CREATE_ROOM, {});
      setRoom(created);
      roomRef.current = created;
      return created;
    } catch {
      NotifyUtils.simpleFailed('Không thể khởi tạo cuộc trò chuyện');
      return null;
    }
  }, []);

  const send = useCallback((rawContent: string) => {
    const content = rawContent.trim();
    if (!content) {
      return;
    }
    const local: LocalMessage = { clientMsgId: uuid(), content, createdAt: new Date().toISOString(), state: 'sending' };
    setPending(current => [...current, local]);
    ensureRoom().then(currentRoom => {
      if (!currentRoom) {
        setPending(current => current.map(p => p.clientMsgId === local.clientMsgId ? { ...p, state: 'failed' } : p));
        return;
      }
      // Phòng vừa tạo: đợi subscription gắn vào rồi mới gửi để không lỡ tin phản hồi
      window.setTimeout(() => publish(currentRoom.id, local), roomRef.current === currentRoom ? 0 : 300);
    });
  }, [ensureRoom, publish]);

  const retry = useCallback((clientMsgId: string) => {
    const local = pendingRef.current.find(p => p.clientMsgId === clientMsgId);
    const currentRoom = roomRef.current;
    if (!local || !currentRoom) {
      return;
    }
    const resent = { ...local, state: 'sending' as LocalMessageState };
    setPending(current => current.map(p => p.clientMsgId === clientMsgId ? resent : p));
    // Cùng clientMsgId nên server không lưu trùng nếu lần trước thực ra đã tới nơi
    publish(currentRoom.id, resent);
  }, [publish]);

  // ========== Kết nối lại: lấy tin bị lỡ và gửi hàng chờ ==========
  useEffect(() => {
    if (!connected) {
      return;
    }
    const currentRoom = roomRef.current;
    if (wasConnectedRef.current && currentRoom) {
      const lastId = messagesRef.current.length ? messagesRef.current[messagesRef.current.length - 1].id : undefined;
      FetchUtils.getWithToken<MessageResponse[]>(ResourceURL.CLIENT_CHAT_MESSAGES, {
        roomId: currentRoom.id,
        after: lastId ?? null,
        size: 100,
      })
        .then(missed => missed.forEach(appendMessage))
        .catch(() => undefined);
    }
    wasConnectedRef.current = true;
    if (currentRoom) {
      pendingRef.current.filter(p => p.state === 'sending').forEach(p => publish(currentRoom.id, p));
    }
  }, [connected, appendMessage, publish]);

  const loadOlder = useCallback(async () => {
    const currentRoom = roomRef.current;
    const first = messagesRef.current[0];
    if (!currentRoom || !first) {
      return;
    }
    const older = await FetchUtils.getWithToken<MessageResponse[]>(ResourceURL.CLIENT_CHAT_MESSAGES, {
      roomId: currentRoom.id,
      before: first.id,
      size: PAGE_SIZE,
    });
    setMessages(current => [...older.filter(o => !current.some(m => m.id === o.id)), ...current]);
    setHasMore(older.length >= PAGE_SIZE);
  }, []);

  const requestAgent = useCallback(() => {
    ensureRoom().then(currentRoom => {
      if (!currentRoom) {
        return;
      }
      FetchUtils.postWithToken<Record<string, never>, RoomResponse>(ResourceURL.CLIENT_CHAT_REQUEST_AGENT, {})
        .then(setRoom)
        .catch(() => NotifyUtils.simpleFailed('Không thể kết nối tư vấn viên, vui lòng thử lại'));
    });
  }, [ensureRoom]);

  const resolve = useCallback(() => {
    const currentRoom = roomRef.current;
    if (!currentRoom) {
      return;
    }
    FetchUtils.postWithToken<Record<string, never>, RoomResponse>(ResourceURL.CLIENT_CHAT_RESOLVE(currentRoom.id), {})
      .then(setRoom)
      .catch(() => NotifyUtils.simpleFailed('Không thể kết thúc cuộc trò chuyện'));
  }, []);

  useTitleBadge(unread);

  const open = useCallback(() => setOpened(true), []);
  const close = useCallback(() => setOpened(false), []);
  const dismissPreview = useCallback(() => setPreview(null), []);

  const value = useMemo<ChatContextValue>(() => ({
    enabled: true,
    loading,
    connected,
    botEnabled,
    room,
    messages,
    pending,
    typing,
    hasMore,
    unread,
    opened,
    preview,
    open,
    close,
    setPageVisible,
    dismissPreview,
    send,
    retry,
    requestAgent,
    resolve,
    loadOlder,
  }), [loading, connected, botEnabled, room, messages, pending, typing, hasMore, unread, opened, preview,
    open, close, dismissPreview, send, retry, requestAgent, resolve, loadOlder]);

  return <ChatContext.Provider value={value}>{children}</ChatContext.Provider>;
}

/**
 * Thêm "(n) " vào tiêu đề tab khi có tin chưa đọc. Theo dõi thẻ <title> vì mỗi trang tự đặt tiêu đề riêng.
 */
function useTitleBadge(unread: number) {
  useEffect(() => {
    const prefixPattern = /^\(\d+\)\s/;
    const apply = () => {
      const base = document.title.replace(prefixPattern, '');
      const next = unread > 0 ? `(${unread}) ${base}` : base;
      if (document.title !== next) {
        document.title = next;
      }
    };
    apply();
    const titleElement = document.querySelector('title');
    if (!titleElement) {
      return;
    }
    const observer = new MutationObserver(apply);
    observer.observe(titleElement, { childList: true, characterData: true, subtree: true });
    return () => {
      observer.disconnect();
      document.title = document.title.replace(prefixPattern, '');
    };
  }, [unread]);
}

export default ChatProvider;
