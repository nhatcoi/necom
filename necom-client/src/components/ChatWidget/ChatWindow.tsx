import React, { useEffect, useLayoutEffect, useRef, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import {
  ActionIcon,
  Alert,
  Avatar,
  Box,
  Button,
  Center,
  Divider,
  Group,
  Loader,
  Menu,
  Paper,
  ScrollArea,
  SimpleGrid,
  Stack,
  Text,
  Textarea,
  ThemeIcon,
  Tooltip,
  UnstyledButton,
  useMantineTheme
} from '@mantine/core';
import {
  ArrowsMaximize,
  CircleCheck,
  DotsVertical,
  Headset,
  Login,
  Send,
  Wand,
  WifiOff,
  X
} from 'tabler-icons-react';
import dayjs from 'dayjs';
import useAuthStore from 'stores/use-auth-store';
import { useChat } from 'components/ChatWidget/ChatProvider';
import ChatMessageItem, { BOT_NAME } from 'components/ChatWidget/ChatMessageItem';
import { MessageResponse } from 'models/Message';

export const SUPPORT_HOTLINE = '1900 6868';

// ScrollArea của Mantine v4 bọc nội dung bằng display: table, dòng dài sẽ đẩy rộng khung chat
export const noHorizontalOverflow = {
  flex: 1,
  minHeight: 0,
  '& .mantine-ScrollArea-viewport > div': { display: 'block !important', minWidth: '0 !important' },
};

interface ChatWindowProps {
  mode: 'widget' | 'page';
  onClose?: () => void;
  height?: number | string;
}

const SUGGESTIONS: { label: string, text: string }[] = [
  { label: '📦 Tra cứu đơn hàng', text: 'Đơn hàng gần nhất của mình đang ở đâu rồi?' },
  { label: '🛋️ Tư vấn sản phẩm', text: 'Tư vấn giúp mình sofa cho phòng khách nhỏ' },
  { label: '🔄 Đổi trả & bảo hành', text: 'Chính sách đổi trả và bảo hành như thế nào?' },
  { label: '🚚 Phí vận chuyển', text: 'Phí và thời gian giao hàng về Hà Nội bao lâu?' },
  { label: '🎁 Điểm thưởng', text: 'Mình đang có bao nhiêu điểm thưởng?' },
];

/**
 * Khung chat dùng chung: mode="widget" (cửa sổ nổi mở từ FAB) và mode="page" (trang Yêu cầu tư vấn).
 * State nằm trong ChatProvider nên chuyển qua lại giữa hai chế độ hội thoại vẫn liền mạch.
 */
function ChatWindow({ mode, onClose, height }: ChatWindowProps) {
  const theme = useMantineTheme();
  const chat = useChat();
  const { user } = useAuthStore();

  const header = <ChatHeader mode={mode} onClose={onClose}/>;

  if (!chat.enabled) {
    return (
      <WindowFrame header={header} height={height}>
        <GuestBody/>
      </WindowFrame>
    );
  }

  const isEmpty = chat.messages.length === 0 && chat.pending.length === 0;

  return (
    <WindowFrame header={header} height={height}>
      {chat.room && !chat.connected && !chat.loading && (
        <Group spacing={6} px="md" py={4} sx={{ backgroundColor: theme.colors.yellow[theme.colorScheme === 'dark' ? 9 : 1] }}>
          <WifiOff size={14}/>
          <Text size="xs">Đang kết nối lại… Tin nhắn sẽ tự gửi khi có mạng.</Text>
        </Group>
      )}

      {chat.loading
        ? <Center sx={{ flex: 1 }}><Loader size="sm"/></Center>
        : isEmpty
          ? <WelcomeScreen name={user?.fullname}/>
          : <MessageList/>}

      {chat.room?.status === 'WAITING_AGENT' && (
        <Alert
          icon={<Headset size={16}/>}
          color="orange"
          radius={0}
          py={8}
          title="Đang kết nối tư vấn viên"
          styles={{ title: { marginBottom: 2 } }}
        >
          <Text size="xs">Bạn cứ nhắn tiếp, tư vấn viên sẽ đọc toàn bộ cuộc trò chuyện. Cần gấp? Gọi {SUPPORT_HOTLINE}.</Text>
        </Alert>
      )}

      <Composer/>
    </WindowFrame>
  );
}

function WindowFrame({ header, children, height }: { header: React.ReactNode, children: React.ReactNode, height?: number | string }) {
  return (
    <Box sx={{ display: 'flex', flexDirection: 'column', height: height || '100%', minHeight: 0, overflow: 'hidden' }}>
      {header}
      {children}
    </Box>
  );
}

function ChatHeader({ mode, onClose }: { mode: 'widget' | 'page', onClose?: () => void }) {
  const theme = useMantineTheme();
  const chat = useChat();
  const navigate = useNavigate();
  const status = chat.room?.status;

  const agentName = chat.room?.assignee?.fullname;
  const { title, subtitle, icon } = (() => {
    if (status === 'AGENT' && agentName) {
      return {
        title: agentName,
        subtitle: 'Tư vấn viên · Đang hỗ trợ bạn',
        icon: <Avatar radius="xl" size={40} color="teal">{agentName.charAt(0)}</Avatar>,
      };
    }
    if (status === 'WAITING_AGENT' || (chat.enabled && !chat.botEnabled)) {
      return {
        title: 'Chăm sóc khách hàng',
        subtitle: status === 'WAITING_AGENT' ? 'Đang kết nối tư vấn viên…' : 'Thường phản hồi trong vài phút',
        icon: <ThemeIcon radius="xl" size={40} color="teal" variant="filled"><Headset size={22}/></ThemeIcon>,
      };
    }
    return {
      title: BOT_NAME,
      subtitle: 'Trợ lý AI · Trả lời ngay 24/7',
      icon: <ThemeIcon radius="xl" size={40} variant="gradient" gradient={{ from: 'violet', to: 'grape' }}><Wand size={22}/></ThemeIcon>,
    };
  })();

  const primary = theme.colors[theme.primaryColor];

  return (
    <Box
      px="md"
      py="sm"
      sx={{
        background: `linear-gradient(135deg, ${primary[7]} 0%, ${primary[5]} 100%)`,
        color: theme.white,
        flexShrink: 0,
      }}
    >
      <Group position="apart" noWrap>
        <Group spacing="sm" noWrap>
          <Box sx={{ position: 'relative' }}>
            {icon}
            <Box
              sx={{
                position: 'absolute',
                right: 0,
                bottom: 0,
                width: 11,
                height: 11,
                borderRadius: '50%',
                border: `2px solid ${primary[6]}`,
                backgroundColor: chat.enabled && !chat.connected ? theme.colors.yellow[5] : theme.colors.green[5],
              }}
            />
          </Box>
          <Stack spacing={0}>
            <Text weight={700} size="sm" sx={{ lineHeight: 1.3 }}>{title}</Text>
            <Text size="xs" sx={{ opacity: 0.85 }}>{subtitle}</Text>
          </Stack>
        </Group>

        <Group spacing={4} noWrap>
          {chat.enabled && (status === 'BOT' || status === 'RESOLVED' || !status) && chat.botEnabled && (
            <Tooltip label="Chuyển sang tư vấn viên" withArrow>
              <Button
                size="xs"
                radius="xl"
                compact
                variant="white"
                leftIcon={<Headset size={14}/>}
                onClick={chat.requestAgent}
              >
                Tư vấn viên
              </Button>
            </Tooltip>
          )}
          {chat.enabled && (
            <Menu
              control={<ActionIcon variant="transparent" sx={{ color: theme.white }} aria-label="Tùy chọn"><DotsVertical size={18}/></ActionIcon>}
              position="bottom"
              placement="end"
              withinPortal={mode === 'page'}
            >
              {mode === 'widget' && (
                <Menu.Item icon={<ArrowsMaximize size={14}/>} onClick={() => {
                  onClose?.();
                  navigate('/user/chat');
                }}>
                  Mở toàn trang
                </Menu.Item>
              )}
              <Menu.Item
                icon={<CircleCheck size={14}/>}
                disabled={!chat.room || status === 'RESOLVED'}
                onClick={chat.resolve}
              >
                Kết thúc cuộc trò chuyện
              </Menu.Item>
            </Menu>
          )}
          {onClose && (
            <ActionIcon variant="transparent" sx={{ color: theme.white }} onClick={onClose} aria-label="Đóng cửa sổ chat">
              <X size={20}/>
            </ActionIcon>
          )}
        </Group>
      </Group>
    </Box>
  );
}

function WelcomeScreen({ name }: { name?: string }) {
  const chat = useChat();

  return (
    <ScrollArea sx={noHorizontalOverflow}>
      <Stack p="lg" spacing="md">
        <Box>
          <Text size="lg" weight={700}>Chào {name ? name.split(' ').slice(-1)[0] : 'bạn'} 👋</Text>
          <Text size="sm" color="dimmed">
            {chat.botEnabled
              ? 'Mình là trợ lý AI của Necom. Hỏi mình về sản phẩm, đơn hàng, đổi trả… hoặc chọn nhanh bên dưới.'
              : 'Để lại lời nhắn, tư vấn viên Necom sẽ phản hồi bạn sớm nhất.'}
          </Text>
        </Box>
        <Stack spacing={8}>
          {chat.botEnabled && SUGGESTIONS.map(suggestion => (
            <UnstyledButton
              key={suggestion.label}
              onClick={() => chat.send(suggestion.text)}
              sx={theme => ({
                padding: '10px 14px',
                borderRadius: theme.radius.md,
                border: `1px solid ${theme.colorScheme === 'dark' ? theme.colors.dark[4] : theme.colors.gray[3]}`,
                fontSize: theme.fontSizes.sm,
                transition: 'background-color 120ms ease, border-color 120ms ease',
                '&:hover': {
                  backgroundColor: theme.colorScheme === 'dark' ? theme.colors.dark[5] : theme.colors[theme.primaryColor][0],
                  borderColor: theme.colors[theme.primaryColor][4],
                },
              })}
            >
              {suggestion.label}
            </UnstyledButton>
          ))}
          <UnstyledButton
            onClick={chat.requestAgent}
            sx={theme => ({
              padding: '10px 14px',
              borderRadius: theme.radius.md,
              fontSize: theme.fontSizes.sm,
              fontWeight: 600,
              color: theme.colors.teal[theme.colorScheme === 'dark' ? 4 : 7],
              border: `1px solid ${theme.colors.teal[4]}`,
              '&:hover': { backgroundColor: theme.colorScheme === 'dark' ? theme.colors.dark[5] : theme.colors.teal[0] },
            })}
          >
            👤 Gặp tư vấn viên
          </UnstyledButton>
        </Stack>
      </Stack>
    </ScrollArea>
  );
}

function MessageList() {
  const chat = useChat();
  const viewport = useRef<HTMLDivElement | null>(null);
  const [loadingOlder, setLoadingOlder] = useState(false);
  const stickToBottom = useRef(true);
  const previousHeight = useRef(0);

  const onScroll = () => {
    const el = viewport.current;
    if (el) {
      stickToBottom.current = el.scrollHeight - el.scrollTop - el.clientHeight < 80;
    }
  };

  const itemCount = chat.messages.length + chat.pending.length;

  // Tin mới: cuộn xuống nếu đang ở gần cuối; tải tin cũ: giữ nguyên vị trí đang đọc
  useLayoutEffect(() => {
    const el = viewport.current;
    if (!el) {
      return;
    }
    if (loadingOlder) {
      el.scrollTop = el.scrollHeight - previousHeight.current;
    } else if (stickToBottom.current) {
      el.scrollTop = el.scrollHeight;
    }
  }, [itemCount, chat.typing, loadingOlder]);

  useEffect(() => {
    const el = viewport.current;
    if (el) {
      el.scrollTop = el.scrollHeight;
    }
  }, []);

  const handleLoadOlder = async () => {
    previousHeight.current = viewport.current?.scrollHeight || 0;
    setLoadingOlder(true);
    try {
      await chat.loadOlder();
    } finally {
      setLoadingOlder(false);
    }
  };

  const lastBotMessageId = [...chat.messages].reverse().find(m => m.senderType !== 'CUSTOMER' && m.type !== 'SYSTEM')?.id;
  const lastMessage = chat.messages[chat.messages.length - 1];

  return (
    <ScrollArea
      sx={noHorizontalOverflow}
      viewportRef={viewport}
      onScrollPositionChange={onScroll}
      aria-live="polite"
    >
      <Box pb="sm">
        {chat.hasMore && (
          <Center pt="sm">
            <Button size="xs" variant="subtle" compact loading={loadingOlder} onClick={handleLoadOlder}>
              Xem tin nhắn cũ hơn
            </Button>
          </Center>
        )}

        {chat.messages.map((message, index) => {
          const previous = chat.messages[index - 1];
          return (
            <React.Fragment key={message.id}>
              {(!previous || !dayjs(previous.createdAt).isSame(message.createdAt, 'day')) && (
                <DateDivider date={message.createdAt}/>
              )}
              <ChatMessageItem
                message={message}
                viewer="customer"
                grouped={isGrouped(previous, message)}
                onQuickReply={message.id === lastBotMessageId && lastMessage?.id === message.id ? chat.send : undefined}
                orderLink={order => '/order/detail/' + order.code}
              />
            </React.Fragment>
          );
        })}

        {chat.pending.map(local => (
          <ChatMessageItem
            key={local.clientMsgId}
            viewer="customer"
            localState={local.state}
            onRetry={() => chat.retry(local.clientMsgId)}
            grouped={false}
            message={{
              id: -1,
              createdAt: local.createdAt,
              updatedAt: local.createdAt,
              content: local.content,
              status: 1,
              user: null,
              roomId: chat.room?.id || 0,
              type: 'TEXT',
              senderType: 'CUSTOMER',
              payload: null,
              clientMsgId: local.clientMsgId,
            }}
          />
        ))}

        {chat.typing && <TypingIndicator name={chat.typing.senderType === 'BOT' ? BOT_NAME : 'Tư vấn viên'}/>}
      </Box>
    </ScrollArea>
  );
}

export function isGrouped(previous: MessageResponse | undefined, message: MessageResponse) {
  return !!previous
    && previous.type !== 'SYSTEM'
    && message.type !== 'SYSTEM'
    && previous.senderType === message.senderType
    && previous.user?.id === message.user?.id
    && previous.type === message.type
    && dayjs(message.createdAt).diff(previous.createdAt, 'minute') < 5;
}

export function DateDivider({ date }: { date: string }) {
  const day = dayjs(date);
  const label = day.isSame(dayjs(), 'day')
    ? 'Hôm nay'
    : day.isSame(dayjs().subtract(1, 'day'), 'day') ? 'Hôm qua' : day.format('DD/MM/YYYY');
  return <Divider my="xs" mx="md" label={label} labelPosition="center" labelProps={{ size: 'xs', color: 'dimmed' }}/>;
}

export function TypingIndicator({ name }: { name: string }) {
  const theme = useMantineTheme();
  return (
    <Group spacing={8} px="md" pt={8}>
      <style>{'@keyframes necom-typing{0%,80%,100%{transform:translateY(0);opacity:.4}40%{transform:translateY(-4px);opacity:1}}'}</style>
      <Box
        sx={{
          display: 'flex',
          gap: 4,
          padding: '10px 12px',
          borderRadius: 16,
          backgroundColor: theme.colorScheme === 'dark' ? theme.colors.dark[5] : theme.colors.gray[1],
        }}
      >
        {[0, 1, 2].map(i => (
          <Box
            key={i}
            sx={{
              width: 6,
              height: 6,
              borderRadius: '50%',
              backgroundColor: theme.colors.gray[6],
              animation: `necom-typing 1.2s ${i * 0.15}s infinite ease-in-out`,
            }}
          />
        ))}
      </Box>
      <Text size="xs" color="dimmed">{name} đang nhập…</Text>
    </Group>
  );
}

function Composer() {
  const theme = useMantineTheme();
  const chat = useChat();
  const [value, setValue] = useState('');
  const inputRef = useRef<HTMLTextAreaElement>(null);

  useEffect(() => {
    inputRef.current?.focus();
  }, []);

  const submit = () => {
    if (value.trim()) {
      chat.send(value);
      setValue('');
    }
  };

  return (
    <Box
      p="sm"
      sx={{
        borderTop: `1px solid ${theme.colorScheme === 'dark' ? theme.colors.dark[4] : theme.colors.gray[2]}`,
        flexShrink: 0,
      }}
    >
      <Group spacing="xs" noWrap sx={{ alignItems: 'flex-end' }}>
        <Textarea
          ref={inputRef}
          placeholder={chat.room?.status === 'RESOLVED' ? 'Nhắn tin để bắt đầu cuộc trò chuyện mới' : 'Nhập tin nhắn…'}
          autosize
          minRows={1}
          maxRows={4}
          radius="xl"
          variant="filled"
          sx={{ flex: 1 }}
          value={value}
          aria-label="Nội dung tin nhắn"
          onChange={event => setValue(event.currentTarget.value)}
          onKeyDown={event => {
            // Enter gửi, Shift+Enter xuống dòng; bỏ qua khi đang gõ tiếng Việt (IME composing)
            if (event.key === 'Enter' && !event.shiftKey && !event.nativeEvent.isComposing) {
              event.preventDefault();
              submit();
            }
          }}
        />
        <ActionIcon
          size={36}
          radius="xl"
          variant="filled"
          color={theme.primaryColor}
          disabled={!value.trim()}
          onClick={submit}
          aria-label="Gửi tin nhắn"
        >
          <Send size={18}/>
        </ActionIcon>
      </Group>
      {chat.botEnabled && (
        <Text size="xs" color="dimmed" align="center" mt={6} sx={{ fontSize: 10 }}>
          Trợ lý AI có thể sai sót. Thông tin đơn hàng chỉ hiển thị cho riêng bạn.
        </Text>
      )}
    </Box>
  );
}

function GuestBody() {
  return (
    <Stack p="lg" spacing="md" sx={{ flex: 1 }} justify="center">
      <Center>
        <ThemeIcon size={64} radius="xl" variant="light">
          <Login size={32}/>
        </ThemeIcon>
      </Center>
      <Box>
        <Text weight={700} align="center">Đăng nhập để chat với Necom</Text>
        <Text size="sm" color="dimmed" align="center">
          Trợ lý AI tra cứu đơn hàng, tư vấn sản phẩm và kết nối tư vấn viên cho bạn.
        </Text>
      </Box>
      <SimpleGrid cols={2} spacing="xs">
        <Button component={Link} to="/signin" radius="xl">Đăng nhập</Button>
        <Button component={Link} to="/signup" radius="xl" variant="light">Đăng ký</Button>
      </SimpleGrid>
      <Paper withBorder radius="md" p="sm">
        <Text size="sm">Hoặc gọi hotline <strong>{SUPPORT_HOTLINE}</strong> (24/7)</Text>
      </Paper>
    </Stack>
  );
}

export default ChatWindow;
