import React, { useEffect, useState } from 'react';
import { Link, useLocation } from 'react-router-dom';
import {
  ActionIcon,
  Badge,
  Box,
  CloseButton,
  Paper,
  Portal,
  Text,
  Transition,
  UnstyledButton,
  useMantineTheme
} from '@mantine/core';
import { useMediaQuery, useWindowEvent } from '@mantine/hooks';
import { BrandMessenger, Lifebuoy, Mail, MessageDots, PhoneCall, X } from 'tabler-icons-react';
import { useChatPalette } from 'components/ChatWidget/chat-palette';
import { useChat } from 'components/ChatWidget/ChatProvider';
import ChatWindow, { SUPPORT_HOTLINE } from 'components/ChatWidget/ChatWindow';
import { BOT_NAME } from 'components/ChatWidget/ChatMessageItem';

const FAB_OFFSET = 24;
const FAB_SIZE = 58;
const NUDGE_SESSION_KEY = 'necom-chat-nudge-shown';

// Kênh ngoài chỉ hiện khi được cấu hình (không đặt link giả)
const ZALO_URL = process.env.REACT_APP_ZALO_URL;
const MESSENGER_URL = process.env.REACT_APP_MESSENGER_URL;

interface SpeedDialItem {
  key: string;
  label: string;
  icon: React.ReactNode;
  // Mục chính (chat) được tô đặc, các kênh còn lại dùng nền kem
  primary?: boolean;
  href?: string;
  to?: string;
  external?: boolean;
  onClick?: () => void;
}

/**
 * FAB góc phải dưới: bấm bung speed dial (chat, hotline, Zalo, Messenger, email, trợ giúp),
 * badge tin chưa đọc, bong bóng xem trước tin mới và lời nhắc theo ngữ cảnh trang.
 */
function ChatLauncher() {
  const theme = useMantineTheme();
  const palette = useChatPalette();
  const chat = useChat();
  const location = useLocation();
  const mobile = useMediaQuery(`(max-width: ${theme.breakpoints.sm}px)`);
  const [dialOpened, setDialOpened] = useState(false);
  const [nudge, setNudge] = useState<string | null>(null);

  const onChatPage = location.pathname === '/user/chat';

  useWindowEvent('keydown', (event) => {
    if (event.key === 'Escape') {
      setDialOpened(false);
      chat.close();
    }
  });

  // Đóng speed dial khi chuyển trang
  useEffect(() => {
    setDialOpened(false);
  }, [location.pathname]);

  // Lời nhắc theo ngữ cảnh, mỗi phiên trình duyệt chỉ hiện 1 lần
  useEffect(() => {
    let shown = false;
    try {
      shown = sessionStorage.getItem(NUDGE_SESSION_KEY) === '1';
    } catch {
      shown = true;
    }
    if (shown || chat.opened) {
      return;
    }
    const text = location.pathname.startsWith('/product/')
      ? 'Cần tư vấn thêm về mẫu này? Hỏi trợ lý ngay ✨'
      : location.pathname.startsWith('/order')
        ? 'Tra cứu đơn hàng nhanh với trợ lý Necom'
        : location.pathname === '/cart'
          ? 'Cần hỗ trợ thanh toán hay phí giao hàng?'
          : null;
    if (!text) {
      return;
    }
    const timer = window.setTimeout(() => {
      setNudge(text);
      try {
        sessionStorage.setItem(NUDGE_SESSION_KEY, '1');
      } catch {
        // Bỏ qua khi trình duyệt chặn storage
      }
    }, 6000);
    return () => window.clearTimeout(timer);
  }, [location.pathname, chat.opened]);

  // Bong bóng xem trước tự ẩn sau 10 giây
  useEffect(() => {
    if (!chat.preview) {
      return;
    }
    const timer = window.setTimeout(chat.dismissPreview, 10000);
    return () => window.clearTimeout(timer);
  }, [chat.preview, chat.dismissPreview]);

  if (onChatPage) {
    return null;
  }

  const openChat = () => {
    setDialOpened(false);
    setNudge(null);
    chat.dismissPreview();
    chat.open();
  };

  const iconProps = { size: 20, strokeWidth: 1.75 };
  const items: SpeedDialItem[] = [
    { key: 'chat', label: 'Chat với Necom', icon: <MessageDots {...iconProps}/>, primary: true, onClick: openChat },
    { key: 'hotline', label: `Gọi ${SUPPORT_HOTLINE}`, icon: <PhoneCall {...iconProps}/>, href: 'tel:19006868' },
    ...(ZALO_URL ? [{
      key: 'zalo', label: 'Chat qua Zalo', href: ZALO_URL, external: true,
      icon: <Text weight={800} size="xs">Zalo</Text>,
    }] : []),
    ...(MESSENGER_URL ? [{
      key: 'messenger', label: 'Messenger', icon: <BrandMessenger {...iconProps}/>, href: MESSENGER_URL, external: true,
    }] : []),
    { key: 'email', label: 'Gửi email hỗ trợ', icon: <Mail {...iconProps}/>, href: 'mailto:support@necom.vnhat.dev' },
    { key: 'help', label: 'Trung tâm trợ giúp', icon: <Lifebuoy {...iconProps}/>, to: '/support/faq' },
  ];

  const handleFabClick = () => {
    if (chat.opened) {
      chat.close();
      return;
    }
    setNudge(null);
    setDialOpened(opened => !opened);
  };

  const showBubble = !chat.opened && !dialOpened;
  const previewSender = chat.preview
    ? (chat.preview.senderType === 'BOT' ? BOT_NAME : chat.preview.user?.fullname || 'Tư vấn viên')
    : '';

  return (
    <Portal zIndex={250}>
      {/* Lớp nền mờ khi mở speed dial, bấm ra ngoài để đóng */}
      <Transition mounted={dialOpened} transition="fade" duration={150}>
        {styles => (
          <Box
            onClick={() => setDialOpened(false)}
            style={styles}
            sx={{ position: 'fixed', inset: 0, backgroundColor: 'rgba(47, 42, 36, 0.22)', backdropFilter: 'blur(1.5px)' }}
          />
        )}
      </Transition>

      {/* Cửa sổ chat: desktop nổi góc phải, mobile toàn màn hình */}
      <Transition mounted={chat.opened} transition="pop-bottom-right" duration={180}>
        {styles => (
          <Paper
            role="dialog"
            aria-label="Cửa sổ chat Necom"
            shadow="xl"
            radius={mobile ? 0 : 'lg'}
            style={styles}
            sx={mobile
              ? { position: 'fixed', inset: 0, overflow: 'hidden' }
              : {
                position: 'fixed',
                right: FAB_OFFSET,
                bottom: FAB_OFFSET + FAB_SIZE + 16,
                width: 380,
                height: 'min(620px, calc(100vh - 120px))',
                overflow: 'hidden',
                border: `1px solid ${theme.colorScheme === 'dark' ? theme.colors.dark[4] : theme.colors.gray[2]}`,
              }}
          >
            <ChatWindow mode="widget" onClose={chat.close}/>
          </Paper>
        )}
      </Transition>

      {/* Các mục speed dial, bung dần từ dưới lên */}
      <Box
        sx={{
          position: 'fixed',
          right: FAB_OFFSET + (FAB_SIZE - 46) / 2,
          bottom: FAB_OFFSET + FAB_SIZE + 14,
          display: 'flex',
          flexDirection: 'column-reverse',
          alignItems: 'flex-end',
          gap: 10,
          pointerEvents: dialOpened ? 'auto' : 'none',
        }}
      >
        {items.map((item, index) => (
          <Transition key={item.key} mounted={dialOpened} transition="slide-up" duration={160} timingFunction="ease">
            {styles => (
              <Box style={{ ...styles, transitionDelay: `${index * 35}ms` }} sx={{ display: 'flex', alignItems: 'center', gap: 10 }}>
                <Paper
                  radius="xl"
                  px="sm"
                  py={5}
                  sx={{
                    backgroundColor: palette.cream,
                    border: `1px solid ${palette.sandBorder}`,
                    boxShadow: '0 2px 8px rgba(47, 42, 36, 0.08)',
                  }}
                >
                  <Text size="sm" weight={item.primary ? 600 : 500} sx={{ color: item.primary ? palette.forest : palette.text }}>
                    {item.label}
                  </Text>
                </Paper>
                <ActionIcon
                  size={46}
                  radius="xl"
                  variant="filled"
                  aria-label={item.label}
                  {...(item.to
                    ? { component: Link, to: item.to }
                    : item.href
                      ? { component: 'a', href: item.href, ...(item.external ? { target: '_blank', rel: 'noopener noreferrer' } : {}) }
                      : { onClick: item.onClick }) as Record<string, unknown>}
                  sx={{
                    backgroundColor: item.primary ? palette.forest : palette.cream,
                    color: item.primary ? theme.white : palette.forest,
                    border: `1px solid ${item.primary ? palette.forest : palette.sandBorder}`,
                    boxShadow: '0 4px 12px rgba(47, 42, 36, 0.12)',
                    transition: 'background-color 120ms ease, transform 120ms ease',
                    '&:hover': {
                      backgroundColor: item.primary ? '#13684F' : palette.sand,
                      transform: 'scale(1.05)',
                    },
                  }}
                >
                  {item.icon}
                </ActionIcon>
              </Box>
            )}
          </Transition>
        ))}
      </Box>

      {/* Bong bóng xem trước tin mới hoặc lời nhắc theo trang */}
      {showBubble && (chat.preview || nudge) && (
        <Paper
          shadow="lg"
          radius="lg"
          p="sm"
          sx={{
            position: 'fixed',
            right: FAB_OFFSET,
            bottom: FAB_OFFSET + FAB_SIZE + 14,
            width: 260,
            border: `1px solid ${theme.colorScheme === 'dark' ? theme.colors.dark[4] : theme.colors.gray[2]}`,
          }}
        >
          <CloseButton
            size="sm"
            sx={{ position: 'absolute', top: 6, right: 6 }}
            aria-label="Ẩn"
            onClick={() => {
              setNudge(null);
              chat.dismissPreview();
            }}
          />
          <UnstyledButton onClick={openChat} sx={{ width: '100%', paddingRight: 20 }}>
            {chat.preview
              ? (
                <>
                  <Text size="xs" weight={700}>{previewSender}</Text>
                  <Text size="sm" lineClamp={2}>{chat.preview.content}</Text>
                </>
              )
              : <Text size="sm">{nudge}</Text>}
          </UnstyledButton>
        </Paper>
      )}

      {/* FAB chính */}
      <Box sx={{ position: 'fixed', right: FAB_OFFSET, bottom: FAB_OFFSET }}>
        <ActionIcon
          size={FAB_SIZE}
          radius="xl"
          variant="filled"
          onClick={handleFabClick}
          aria-label={chat.opened || dialOpened ? 'Đóng' : 'Liên hệ & chat với Necom'}
          aria-expanded={dialOpened}
          sx={{
            backgroundColor: palette.forest,
            color: theme.white,
            boxShadow: '0 8px 24px rgba(11, 79, 60, 0.35)',
            transition: 'transform 200ms ease, background-color 200ms ease',
            '&:hover': { transform: 'scale(1.06)', backgroundColor: '#13684F' },
          }}
        >
          <Box
            sx={{
              display: 'flex',
              transition: 'transform 200ms ease',
              transform: chat.opened || dialOpened ? 'rotate(90deg)' : 'none',
            }}
          >
            {chat.opened || dialOpened ? <X size={26}/> : <MessageDots size={28} strokeWidth={1.75}/>}
          </Box>
        </ActionIcon>
        {chat.unread > 0 && !chat.opened && (
          <Badge
            color="red"
            variant="filled"
            size="sm"
            radius="xl"
            sx={{ position: 'absolute', top: -4, right: -4, pointerEvents: 'none', border: `2px solid ${theme.white}` }}
          >
            {chat.unread > 99 ? '99+' : chat.unread}
          </Badge>
        )}
      </Box>
    </Portal>
  );
}

export default ChatLauncher;
