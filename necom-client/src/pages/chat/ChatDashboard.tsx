import React, { useCallback, useEffect, useLayoutEffect, useMemo, useRef, useState } from 'react';
import { Link } from 'react-router-dom';
import {
  ActionIcon,
  Anchor,
  Avatar,
  Badge,
  Box,
  Button,
  Center,
  Divider,
  Group,
  Loader,
  Paper,
  ScrollArea,
  SegmentedControl,
  Stack,
  Text,
  Textarea,
  TextInput,
  ThemeIcon,
  Tooltip,
  UnstyledButton,
  useMantineTheme
} from '@mantine/core';
import {
  Bell,
  BellOff,
  CircleCheck,
  Headset,
  Notes,
  Refresh,
  Robot,
  Search,
  Send,
  UserCheck,
  Wand
} from 'tabler-icons-react';
import dayjs from 'dayjs';
import { StompSessionProvider, useStompClient, useSubscription } from 'react-stomp-hooks';
import ApplicationConstants from 'constants/ApplicationConstants';
import ResourceURL from 'constants/ResourceURL';
import ManagerPath from 'constants/ManagerPath';
import useAdminAuthStore from 'stores/use-admin-auth-store';
import FetchUtils from 'utils/FetchUtils';
import NotifyUtils from 'utils/NotifyUtils';
import MiscUtils from 'utils/MiscUtils';
import DateUtils from 'utils/DateUtils';
import { ChatEvent, ChatTyping, MessageResponse } from 'models/Message';
import { ChatCustomerProfileResponse, RoomResponse, RoomStatus } from 'models/Room';
import ChatMessageItem, { BOT_NAME } from 'components/ChatWidget/ChatMessageItem';
import { DateDivider, isGrouped, noHorizontalOverflow, TypingIndicator } from 'components/ChatWidget/ChatWindow';

type InboxTab = 'waiting' | 'mine' | 'bot' | 'open' | 'resolved';

const STATUS_META: Record<RoomStatus, { label: string, color: string }> = {
  BOT: { label: 'Bot', color: 'violet' },
  WAITING_AGENT: { label: 'Chờ nhận', color: 'orange' },
  AGENT: { label: 'Đang hỗ trợ', color: 'teal' },
  RESOLVED: { label: 'Đã xong', color: 'gray' },
};

const uuid = () => (window.crypto && 'randomUUID' in window.crypto)
  ? window.crypto.randomUUID()
  : `${Date.now()}-${Math.random().toString(36).slice(2, 11)}`;

/**
 * Inbox CSKH 3 cột: danh sách phòng theo trạng thái · khung chat (trả lời, ghi chú nội bộ, gợi ý AI) · hồ sơ khách.
 */
function ChatDashboard() {
  const { jwtToken } = useAdminAuthStore();

  const connectHeaders = useMemo(() => ({ Authorization: `Bearer ${jwtToken}` }), [jwtToken]);
  const onStompError = useCallback((frame: { headers: Record<string, string> }) => {
    // eslint-disable-next-line no-console
    console.warn('STOMP error', frame.headers?.message);
  }, []);

  return (
    <StompSessionProvider
      url={ApplicationConstants.WEBSOCKET_PATH}
      connectHeaders={connectHeaders}
      reconnectDelay={5000}
      onStompError={onStompError}
    >
      <ChatInbox/>
    </StompSessionProvider>
  );
}

function ChatInbox() {
  const { user: adminUser } = useAdminAuthStore();
  const stompClient = useStompClient();
  const connected = !!stompClient && stompClient.connected;

  const [rooms, setRooms] = useState<RoomResponse[]>([]);
  const [loadingRooms, setLoadingRooms] = useState(true);
  const [activeRoomId, setActiveRoomId] = useState<number | null>(null);
  const [tab, setTab] = useState<InboxTab>('waiting');
  const [search, setSearch] = useState('');
  const [messages, setMessages] = useState<MessageResponse[]>([]);
  const [loadingMessages, setLoadingMessages] = useState(false);
  const [hasMore, setHasMore] = useState(false);
  const [typing, setTyping] = useState<ChatTyping | null>(null);
  const [alertsEnabled, setAlertsEnabled] = useState(
    typeof Notification !== 'undefined' && Notification.permission === 'granted'
  );

  const activeRoomIdRef = useRef<number | null>(null);
  activeRoomIdRef.current = activeRoomId;
  const roomsRef = useRef<RoomResponse[]>([]);
  roomsRef.current = rooms;

  const loadRooms = useCallback(() => {
    setLoadingRooms(true);
    FetchUtils.getWithToken<RoomResponse[]>(ResourceURL.ADMIN_CHAT_ROOMS, undefined, true)
      .then(response => {
        setRooms(response);
        setActiveRoomId(current => current ?? (
          response.find(r => r.status === 'WAITING_AGENT')?.id ?? response[0]?.id ?? null
        ));
        // Mở tab đầu tiên có hội thoại: Chờ nhận → Của tôi → Đang mở → Đã xong
        setTab(current => {
          if (current !== 'waiting') {
            return current;
          }
          if (response.some(r => r.status === 'WAITING_AGENT')) {
            return 'waiting';
          }
          if (response.some(r => r.status === 'AGENT' && r.assignee?.id === adminUser?.id)) {
            return 'mine';
          }
          return response.some(r => r.status !== 'RESOLVED') ? 'open' : 'resolved';
        });
      })
      .catch(() => NotifyUtils.simpleFailed('Không tải được danh sách hội thoại'))
      .finally(() => setLoadingRooms(false));
  }, [adminUser?.id]);

  useEffect(loadRooms, [loadRooms]);

  const markRead = useCallback((roomId: number) => {
    setRooms(current => current.map(r => r.id === roomId ? { ...r, unreadCount: 0 } : r));
    FetchUtils.postWithToken(ResourceURL.ADMIN_CHAT_ROOM(roomId) + '/read', {}, true).catch(() => undefined);
  }, []);

  // Tải tin của phòng đang mở
  useEffect(() => {
    if (!activeRoomId) {
      setMessages([]);
      return;
    }
    let cancelled = false;
    setLoadingMessages(true);
    setTyping(null);
    FetchUtils.getWithToken<MessageResponse[]>(ResourceURL.ADMIN_CHAT_ROOM(activeRoomId) + '/messages', { size: 50 }, true)
      .then(response => {
        if (!cancelled) {
          setMessages(response);
          setHasMore(response.length >= 50);
        }
      })
      .catch(() => NotifyUtils.simpleFailed('Không tải được tin nhắn'))
      .finally(() => !cancelled && setLoadingMessages(false));
    markRead(activeRoomId);
    return () => {
      cancelled = true;
    };
  }, [activeRoomId, markRead]);

  // Kết nối lại: làm mới danh sách để không lỡ sự kiện
  const wasConnected = useRef(false);
  useEffect(() => {
    if (connected && wasConnected.current) {
      loadRooms();
    }
    if (connected) {
      wasConnected.current = true;
    }
  }, [connected, loadRooms]);

  useSubscription(['/chat/receive/admin'], (frame) => {
    const event: ChatEvent = JSON.parse(frame.body);

    if (event.kind === 'MESSAGE' && event.message) {
      const message = event.message;
      if (message.roomId === activeRoomIdRef.current) {
        setMessages(current => current.some(m => m.id === message.id) ? current : [...current, message]);
        if (message.senderType === 'CUSTOMER') {
          setTyping(null);
        }
      }
      if (message.senderType === 'CUSTOMER' && (message.roomId !== activeRoomIdRef.current || document.hidden)) {
        const room = roomsRef.current.find(r => r.id === message.roomId);
        // Chỉ báo động cho phòng đang cần người (bot tự xử lý phòng BOT)
        if (room && room.status !== 'BOT') {
          alertAgent(`${room.user.fullname}: ${message.content}`, alertsEnabled);
        }
      }
    } else if (event.kind === 'ROOM' && event.room) {
      const room = event.room;
      const previous = roomsRef.current.find(r => r.id === room.id);
      if (room.status === 'WAITING_AGENT' && previous?.status !== 'WAITING_AGENT') {
        alertAgent(`${room.user.fullname} đang chờ tư vấn viên`, alertsEnabled);
      }
      setRooms(current => [room, ...current.filter(r => r.id !== room.id)]
        .sort((a, b) => dayjs(b.updatedAt).valueOf() - dayjs(a.updatedAt).valueOf()));
      if (room.id === activeRoomIdRef.current && (room.unreadCount || 0) > 0 && !document.hidden) {
        markRead(room.id);
      }
    } else if (event.kind === 'TYPING' && event.typing && event.roomId === activeRoomIdRef.current) {
      if (event.typing.senderType === 'CUSTOMER' || event.typing.senderType === 'BOT') {
        setTyping(event.typing.active ? event.typing : null);
      }
    }
  });

  const counts = useMemo(() => ({
    waiting: rooms.filter(r => r.status === 'WAITING_AGENT').length,
    mine: rooms.filter(r => r.status === 'AGENT' && r.assignee?.id === adminUser?.id).length,
    bot: rooms.filter(r => r.status === 'BOT').length,
    open: rooms.filter(r => r.status !== 'RESOLVED').length,
  }), [rooms, adminUser]);

  const visibleRooms = useMemo(() => {
    const keyword = search.trim().toLowerCase();
    return rooms
      .filter(r => {
        switch (tab) {
        case 'waiting':
          return r.status === 'WAITING_AGENT';
        case 'mine':
          return r.status === 'AGENT' && r.assignee?.id === adminUser?.id;
        case 'bot':
          return r.status === 'BOT';
        case 'open':
          return r.status !== 'RESOLVED';
        default:
          return r.status === 'RESOLVED';
        }
      })
      .filter(r => !keyword
        || r.user.fullname.toLowerCase().includes(keyword)
        || r.user.username.toLowerCase().includes(keyword)
        || r.user.email.toLowerCase().includes(keyword));
  }, [rooms, tab, search, adminUser]);

  const activeRoom = rooms.find(r => r.id === activeRoomId) || null;

  const enableAlerts = () => {
    if (typeof Notification === 'undefined') {
      NotifyUtils.simpleFailed('Trình duyệt không hỗ trợ thông báo');
      return;
    }
    if (alertsEnabled) {
      setAlertsEnabled(false);
      return;
    }
    Notification.requestPermission().then(permission => setAlertsEnabled(permission === 'granted'));
  };

  return (
    // Khóa chiều cao theo viewport để mỗi cột (danh sách, hội thoại, hồ sơ khách) tự cuộn riêng
    <Box
      sx={theme => ({
        display: 'flex',
        gap: theme.spacing.sm,
        height: `calc(100vh - var(--mantine-header-height, 56px) - ${theme.spacing.md * 2}px)`,
        minHeight: 520,
        [theme.fn.smallerThan('md')]: { flexDirection: 'column', height: 'auto' },
      })}
    >
      <Box sx={theme => ({
        width: 320,
        flexShrink: 0,
        height: '100%',
        minHeight: 0,
        [theme.fn.smallerThan('md')]: { width: '100%', height: 420 },
      })}>
        <Paper shadow="xs" p="sm" sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
          <Group position="apart" mb="xs">
            <Group spacing="xs">
              <Text size="lg" weight={600}>Hội thoại</Text>
              <Tooltip label={connected ? 'Đang kết nối realtime' : 'Mất kết nối, đang thử lại…'} withArrow>
                <Box sx={theme => ({
                  width: 8,
                  height: 8,
                  borderRadius: '50%',
                  backgroundColor: connected ? theme.colors.green[6] : theme.colors.yellow[6],
                })}/>
              </Tooltip>
            </Group>
            <Group spacing={4}>
              <Tooltip label={alertsEnabled ? 'Tắt thông báo desktop' : 'Bật thông báo desktop & âm thanh'} withArrow>
                <ActionIcon variant="light" color={alertsEnabled ? 'teal' : 'gray'} size="sm" onClick={enableAlerts}>
                  {alertsEnabled ? <Bell size={16}/> : <BellOff size={16}/>}
                </ActionIcon>
              </Tooltip>
              <ActionIcon variant="light" color="blue" size="sm" onClick={loadRooms} aria-label="Làm mới">
                <Refresh size={16}/>
              </ActionIcon>
            </Group>
          </Group>

          <TextInput
            size="xs"
            placeholder="Tìm theo tên, username, email"
            icon={<Search size={14}/>}
            value={search}
            onChange={event => setSearch(event.currentTarget.value)}
            mb="xs"
          />

          <SegmentedControl
            size="xs"
            fullWidth
            value={tab}
            onChange={value => setTab(value as InboxTab)}
            data={[
              { value: 'waiting', label: `Chờ (${counts.waiting})` },
              { value: 'mine', label: `Của tôi (${counts.mine})` },
              { value: 'bot', label: `Bot (${counts.bot})` },
            ]}
          />
          <SegmentedControl
            size="xs"
            fullWidth
            mt={4}
            mb="xs"
            value={tab}
            onChange={value => setTab(value as InboxTab)}
            data={[
              { value: 'open', label: `Đang mở (${counts.open})` },
              { value: 'resolved', label: 'Đã xong' },
            ]}
          />

          <ScrollArea sx={noHorizontalOverflow}>
            {loadingRooms && <Center py="md"><Loader size="sm"/></Center>}
            {!loadingRooms && visibleRooms.length === 0 && (
              <Text size="sm" color="dimmed" align="center" py="lg">Không có hội thoại</Text>
            )}
            <Stack spacing={6}>
              {visibleRooms.map(room => (
                <RoomCard
                  key={room.id}
                  room={room}
                  active={room.id === activeRoomId}
                  onClick={() => setActiveRoomId(room.id)}
                />
              ))}
            </Stack>
          </ScrollArea>
        </Paper>
      </Box>

      <Box sx={theme => ({
        flex: 1,
        minWidth: 0,
        height: '100%',
        minHeight: 0,
        [theme.fn.smallerThan('md')]: { height: 'calc(100vh - 120px)' },
      })}>
        <Paper shadow="xs" sx={{ height: '100%', overflow: 'hidden', display: 'flex', flexDirection: 'column' }}>
          {activeRoom
            ? (
              <ChatPanel
                room={activeRoom}
                messages={messages}
                setMessages={setMessages}
                loading={loadingMessages}
                hasMore={hasMore}
                setHasMore={setHasMore}
                typing={typing}
                adminUserId={adminUser?.id}
                onRoomChange={room => setRooms(current => current.map(r => r.id === room.id ? room : r))}
              />
            )
            : <Center sx={{ flex: 1 }}><Text color="dimmed">Chọn một hội thoại</Text></Center>}
        </Paper>
      </Box>

      <Box sx={theme => ({
        width: 300,
        flexShrink: 0,
        height: '100%',
        minHeight: 0,
        [theme.fn.smallerThan('lg')]: { display: 'none' },
      })}>
        <Paper shadow="xs" p="sm" sx={{ height: '100%', overflow: 'hidden' }}>
          {activeRoom && <CustomerSidebar key={activeRoom.id} room={activeRoom}/>}
        </Paper>
      </Box>
    </Box>
  );
}

function RoomCard({ room, active, onClick }: { room: RoomResponse, active: boolean, onClick: () => void }) {
  const theme = useMantineTheme();
  const last = room.lastMessage;
  const unread = room.unreadCount || 0;
  const waitingMinutes = room.status === 'WAITING_AGENT' ? dayjs().diff(room.updatedAt, 'minute') : 0;

  const prefix = !last ? '' : last.senderType === 'BOT' ? 'AI: ' : last.senderType === 'AGENT' ? 'NV: ' : '';
  const time = dayjs(room.updatedAt);

  return (
    <UnstyledButton
      onClick={onClick}
      sx={{
        display: 'block',
        width: '100%',
        padding: theme.spacing.xs,
        borderRadius: theme.radius.sm,
        border: `1px solid ${theme.colorScheme === 'dark' ? theme.colors.dark[4] : theme.colors.gray[2]}`,
        borderLeft: `4px solid ${room.status === 'WAITING_AGENT'
          ? theme.colors.orange[waitingMinutes >= 5 ? 7 : 4]
          : 'transparent'}`,
        backgroundColor: active
          ? (theme.colorScheme === 'dark' ? theme.colors.dark[5] : theme.colors[theme.primaryColor][0])
          : 'transparent',
        '&:hover': { backgroundColor: theme.colorScheme === 'dark' ? theme.colors.dark[5] : theme.colors.gray[0] },
      }}
    >
      <Group spacing="sm" noWrap>
        <Avatar color="cyan" radius="xl">{room.user.fullname.charAt(0).toUpperCase()}</Avatar>
        <Stack spacing={2} sx={{ flex: 1, minWidth: 0 }}>
          <Group position="apart" noWrap spacing={4}>
            <Text size="sm" weight={unread > 0 ? 700 : 500} lineClamp={1}>{room.user.fullname}</Text>
            <Text size="xs" color="dimmed" sx={{ flexShrink: 0 }}>
              {time.isSame(dayjs(), 'day') ? time.format('HH:mm') : time.format('DD/MM')}
            </Text>
          </Group>
          <Group position="apart" noWrap spacing={4}>
            <Text size="xs" color={unread > 0 ? undefined : 'dimmed'} weight={unread > 0 ? 600 : 400} lineClamp={1}>
              {last ? prefix + last.content : 'Chưa có tin nhắn'}
            </Text>
            {unread > 0 && <Badge size="xs" color="red" variant="filled" radius="xl">{unread}</Badge>}
          </Group>
          <Group spacing={4}>
            <Badge size="xs" color={STATUS_META[room.status].color} variant="light">{STATUS_META[room.status].label}</Badge>
            {room.assignee && room.status === 'AGENT' && (
              <Text size="xs" color="dimmed" lineClamp={1}>· {room.assignee.fullname}</Text>
            )}
            {waitingMinutes >= 1 && <Text size="xs" color="orange">· chờ {waitingMinutes} phút</Text>}
          </Group>
        </Stack>
      </Group>
    </UnstyledButton>
  );
}

interface ChatPanelProps {
  room: RoomResponse;
  messages: MessageResponse[];
  setMessages: React.Dispatch<React.SetStateAction<MessageResponse[]>>;
  loading: boolean;
  hasMore: boolean;
  setHasMore: (value: boolean) => void;
  typing: ChatTyping | null;
  adminUserId?: number;
  onRoomChange: (room: RoomResponse) => void;
}

function ChatPanel({
  room,
  messages,
  setMessages,
  loading,
  hasMore,
  setHasMore,
  typing,
  adminUserId,
  onRoomChange,
}: ChatPanelProps) {
  const theme = useMantineTheme();
  const stompClient = useStompClient();
  const viewport = useRef<HTMLDivElement | null>(null);
  const stick = useRef(true);
  const lastTypingSent = useRef(0);
  const [mode, setMode] = useState<'reply' | 'note'>('reply');
  const [value, setValue] = useState('');
  const [suggesting, setSuggesting] = useState(false);
  const [loadingOlder, setLoadingOlder] = useState(false);
  const previousHeight = useRef(0);

  useEffect(() => {
    setValue('');
    setMode('reply');
    stick.current = true;
  }, [room.id]);

  useLayoutEffect(() => {
    const el = viewport.current;
    if (!el) {
      return;
    }
    if (loadingOlder) {
      el.scrollTop = el.scrollHeight - previousHeight.current;
    } else if (stick.current) {
      el.scrollTop = el.scrollHeight;
    }
  }, [messages.length, typing, loadingOlder]);

  const action = (path: string, successMessage?: string) => {
    FetchUtils.postWithToken<Record<string, never>, RoomResponse>(ResourceURL.ADMIN_CHAT_ROOM(room.id) + path, {}, true)
      .then(updated => {
        onRoomChange(updated);
        successMessage && NotifyUtils.simpleSuccess(successMessage);
      })
      .catch(() => NotifyUtils.simpleFailed('Thao tác không thành công'));
  };

  const loadOlder = async () => {
    const first = messages[0];
    if (!first) {
      return;
    }
    previousHeight.current = viewport.current?.scrollHeight || 0;
    setLoadingOlder(true);
    try {
      const older = await FetchUtils.getWithToken<MessageResponse[]>(
        ResourceURL.ADMIN_CHAT_ROOM(room.id) + '/messages', { before: first.id, size: 50 }, true);
      setMessages(current => [...older.filter(o => !current.some(m => m.id === o.id)), ...current]);
      setHasMore(older.length >= 50);
    } finally {
      setLoadingOlder(false);
    }
  };

  const submit = () => {
    const content = value.trim();
    if (!content) {
      return;
    }
    if (mode === 'note') {
      FetchUtils.postWithToken<{ content: string }, MessageResponse>(ResourceURL.ADMIN_CHAT_ROOM(room.id) + '/notes', { content }, true)
        .then(note => setMessages(current => current.some(m => m.id === note.id) ? current : [...current, note]))
        .catch(() => NotifyUtils.simpleFailed('Không lưu được ghi chú'));
      setValue('');
      return;
    }
    if (!stompClient || !stompClient.connected) {
      NotifyUtils.simpleFailed('Đang mất kết nối, vui lòng thử lại');
      return;
    }
    stick.current = true;
    stompClient.publish({
      destination: '/chat/send/' + room.id,
      body: JSON.stringify({ content, clientMsgId: uuid() }),
    });
    stompClient.publish({ destination: `/chat/send/${room.id}/typing`, body: JSON.stringify({ active: false }) });
    setValue('');
  };

  const emitTyping = () => {
    if (mode !== 'reply' || !stompClient || !stompClient.connected) {
      return;
    }
    const now = Date.now();
    if (now - lastTypingSent.current > 3000) {
      lastTypingSent.current = now;
      stompClient.publish({ destination: `/chat/send/${room.id}/typing`, body: JSON.stringify({ active: true }) });
    }
  };

  const suggest = () => {
    setSuggesting(true);
    FetchUtils.getWithToken<{ content: string }>(ResourceURL.ADMIN_CHAT_ROOM(room.id) + '/suggest', undefined, true)
      .then(response => {
        setMode('reply');
        setValue(response.content);
      })
      .catch(() => NotifyUtils.simpleFailed('Không tạo được gợi ý'))
      .finally(() => setSuggesting(false));
  };

  const isMine = room.status === 'AGENT' && room.assignee?.id === adminUserId;

  return (
    <>
      <Group
        position="apart"
        px="md"
        py="sm"
        sx={{ borderBottom: `1px solid ${theme.colorScheme === 'dark' ? theme.colors.dark[4] : theme.colors.gray[2]}` }}
      >
        <Group spacing="sm">
          <Avatar color="cyan" radius="xl">{room.user.fullname.charAt(0).toUpperCase()}</Avatar>
          <Stack spacing={0}>
            <Group spacing={6}>
              <Text weight={600}>{room.user.fullname}</Text>
              <Badge size="xs" color={STATUS_META[room.status].color}>{STATUS_META[room.status].label}</Badge>
            </Group>
            <Text size="xs" color="dimmed">
              {room.user.email}
              {room.assignee && room.status === 'AGENT' && ` · Phụ trách: ${room.assignee.fullname}`}
            </Text>
          </Stack>
        </Group>
        <Group spacing={6}>
          {!isMine && room.status !== 'RESOLVED' && (
            <Button size="xs" leftIcon={<UserCheck size={14}/>} onClick={() => action('/claim', 'Đã nhận phiên')}>
              Nhận phiên
            </Button>
          )}
          {room.status === 'AGENT' && (
            <Button size="xs" variant="light" color="violet" leftIcon={<Robot size={14}/>} onClick={() => action('/release')}>
              Trả lại bot
            </Button>
          )}
          {room.status !== 'RESOLVED' && (
            <Button size="xs" variant="light" color="gray" leftIcon={<CircleCheck size={14}/>} onClick={() => action('/resolve')}>
              Kết thúc
            </Button>
          )}
        </Group>
      </Group>

      <ScrollArea
        sx={noHorizontalOverflow}
        viewportRef={viewport}
        onScrollPositionChange={() => {
          const el = viewport.current;
          if (el) {
            stick.current = el.scrollHeight - el.scrollTop - el.clientHeight < 80;
          }
        }}
      >
        {loading && <Center py="md"><Loader size="sm"/></Center>}
        {hasMore && !loading && (
          <Center pt="sm">
            <Button size="xs" variant="subtle" compact loading={loadingOlder} onClick={loadOlder}>Xem tin cũ hơn</Button>
          </Center>
        )}
        <Box pb="sm">
          {messages.map((message, index) => {
            const previous = messages[index - 1];
            return (
              <React.Fragment key={message.id}>
                {(!previous || !dayjs(previous.createdAt).isSame(message.createdAt, 'day')) && (
                  <DateDivider date={message.createdAt}/>
                )}
                <ChatMessageItem
                  message={message}
                  viewer="agent"
                  grouped={isGrouped(previous, message)}
                  orderLink={order => ManagerPath.ORDER + '/update/' + order.id}
                />
              </React.Fragment>
            );
          })}
          {typing && <TypingIndicator name={typing.senderType === 'BOT' ? BOT_NAME : room.user.fullname}/>}
        </Box>
      </ScrollArea>

      <Box
        p="sm"
        sx={{
          borderTop: `1px solid ${theme.colorScheme === 'dark' ? theme.colors.dark[4] : theme.colors.gray[2]}`,
          backgroundColor: mode === 'note'
            ? (theme.colorScheme === 'dark' ? theme.fn.rgba(theme.colors.yellow[8], 0.15) : theme.colors.yellow[0])
            : undefined,
        }}
      >
        <Group position="apart" mb={6}>
          <SegmentedControl
            size="xs"
            value={mode}
            onChange={value => setMode(value as 'reply' | 'note')}
            data={[
              { value: 'reply', label: 'Trả lời khách' },
              { value: 'note', label: 'Ghi chú nội bộ' },
            ]}
          />
          <Button
            size="xs"
            variant="light"
            color="violet"
            compact
            leftIcon={<Wand size={14}/>}
            loading={suggesting}
            onClick={suggest}
          >
            Gợi ý bằng AI
          </Button>
        </Group>
        <Group spacing="xs" noWrap sx={{ alignItems: 'flex-end' }}>
          <Textarea
            sx={{ flex: 1 }}
            autosize
            minRows={1}
            maxRows={6}
            placeholder={mode === 'note'
              ? 'Ghi chú chỉ nhân viên thấy…'
              : (isMine ? 'Nhập tin nhắn…' : 'Gửi tin sẽ tự nhận phiên này')}
            value={value}
            onChange={event => {
              setValue(event.currentTarget.value);
              emitTyping();
            }}
            onKeyDown={event => {
              if (event.key === 'Enter' && !event.shiftKey && !event.nativeEvent.isComposing) {
                event.preventDefault();
                submit();
              }
            }}
          />
          <ActionIcon
            size={36}
            variant="filled"
            color={mode === 'note' ? 'yellow' : theme.primaryColor}
            onClick={submit}
            disabled={!value.trim()}
            aria-label={mode === 'note' ? 'Lưu ghi chú' : 'Gửi tin nhắn'}
          >
            {mode === 'note' ? <Notes size={18}/> : <Send size={18}/>}
          </ActionIcon>
        </Group>
      </Box>
    </>
  );
}

function CustomerSidebar({ room }: { room: RoomResponse }) {
  const [profile, setProfile] = useState<ChatCustomerProfileResponse | null>(null);

  useEffect(() => {
    let cancelled = false;
    setProfile(null);
    FetchUtils.getWithToken<ChatCustomerProfileResponse>(ResourceURL.ADMIN_CHAT_ROOM(room.id) + '/customer', undefined, true)
      .then(response => !cancelled && setProfile(response))
      .catch(() => undefined);
    return () => {
      cancelled = true;
    };
  }, [room.id]);

  const sendOrderCard = (code: string) => {
    FetchUtils.postWithToken(ResourceURL.ADMIN_CHAT_ROOM(room.id) + '/order-card', { code }, true)
      .then(() => NotifyUtils.simpleSuccess('Đã gửi thẻ đơn hàng cho khách'))
      .catch(() => NotifyUtils.simpleFailed('Không gửi được thẻ đơn hàng'));
  };

  if (!profile) {
    return <Center py="xl"><Loader size="sm"/></Center>;
  }

  return (
    <ScrollArea sx={{ ...noHorizontalOverflow, height: '100%' }}>
      <Stack spacing="sm">
        <Stack align="center" spacing={4}>
          <Avatar size="lg" radius="xl" color="cyan">{profile.fullname.charAt(0).toUpperCase()}</Avatar>
          <Text weight={600}>{profile.fullname}</Text>
          <Text size="xs" color="dimmed">@{profile.username}</Text>
        </Stack>

        <Stack spacing={4}>
          <InfoRow label="Email" value={profile.email}/>
          <InfoRow label="Điện thoại" value={profile.phone}/>
          <InfoRow label="Tham gia" value={DateUtils.isoDateToString(profile.createdAt, 'DD/MM/YYYY')}/>
          <InfoRow label="Điểm thưởng" value={MiscUtils.formatPrice(profile.rewardScore)}/>
          <InfoRow label="Tổng đơn" value={String(profile.totalOrders)}/>
        </Stack>

        <Divider label="Đơn gần đây" labelPosition="center"/>

        {profile.recentOrders.length === 0 && <Text size="sm" color="dimmed" align="center">Chưa có đơn hàng</Text>}
        {profile.recentOrders.map(order => (
          <Paper key={order.code} withBorder p="xs" radius="sm">
            <Group position="apart" spacing={4}>
              <Anchor component={Link} to={ManagerPath.ORDER + '/update/' + order.id} size="sm" weight={600}>
                #{order.code}
              </Anchor>
              <Badge size="xs" variant="light">{order.statusLabel}</Badge>
            </Group>
            <Group position="apart" mt={4}>
              <Text size="xs" color="dimmed">
                {DateUtils.isoDateToString(order.createdAt, 'DD/MM/YYYY')} · {MiscUtils.formatPrice(order.totalPay)}₫
              </Text>
              <Tooltip label="Gửi thẻ đơn hàng vào cuộc trò chuyện" withArrow>
                <ActionIcon size="sm" variant="light" color="blue" onClick={() => sendOrderCard(order.code)}>
                  <Send size={14}/>
                </ActionIcon>
              </Tooltip>
            </Group>
          </Paper>
        ))}

        <Paper withBorder p="xs" radius="sm">
          <Group spacing={6} noWrap>
            <ThemeIcon size="sm" variant="light" color="teal"><Headset size={14}/></ThemeIcon>
            <Text size="xs" color="dimmed">
              Ghi chú nội bộ và tóm tắt ở khung giữa chỉ nhân viên thấy.
            </Text>
          </Group>
        </Paper>
      </Stack>
    </ScrollArea>
  );
}

function InfoRow({ label, value }: { label: string, value: string }) {
  return (
    <Group position="apart" noWrap spacing="xs">
      <Text size="xs" color="dimmed">{label}</Text>
      <Text size="xs" weight={500} lineClamp={1} sx={{ textAlign: 'right' }}>{value}</Text>
    </Group>
  );
}

// Âm báo ngắn bằng WebAudio (không cần file âm thanh)
function beep() {
  try {
    const AudioContextClass = window.AudioContext || (window as unknown as { webkitAudioContext: typeof AudioContext }).webkitAudioContext;
    const context = new AudioContextClass();
    const oscillator = context.createOscillator();
    const gain = context.createGain();
    oscillator.frequency.value = 880;
    gain.gain.setValueAtTime(0.08, context.currentTime);
    gain.gain.exponentialRampToValueAtTime(0.0001, context.currentTime + 0.35);
    oscillator.connect(gain).connect(context.destination);
    oscillator.start();
    oscillator.stop(context.currentTime + 0.35);
  } catch {
    // Trình duyệt chặn âm thanh khi chưa có tương tác
  }
}

function alertAgent(text: string, desktopEnabled: boolean) {
  beep();
  if (desktopEnabled && typeof Notification !== 'undefined' && Notification.permission === 'granted' && document.hidden) {
    new Notification('Necom · Tin nhắn CSKH', { body: text });
  }
}

export default ChatDashboard;
