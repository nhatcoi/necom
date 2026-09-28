import React from 'react';
import { Link } from 'react-router-dom';
import {
  Anchor,
  Avatar,
  Badge,
  Box,
  Button,
  Card,
  Group,
  Image,
  Stack,
  Text,
  Tooltip,
  UnstyledButton,
  useMantineTheme
} from '@mantine/core';
import { AlertCircle, Clock, Notes, Package } from 'tabler-icons-react';
import { NecomAssistantAvatar, useChatPalette } from 'components/ChatWidget/chat-palette';
import { ChatOrderCard, ChatProductCard, MessageResponse } from 'models/Message';
import MiscUtils from 'utils/MiscUtils';
import DateUtils from 'utils/DateUtils';

export type ChatViewer = 'customer' | 'agent';

export type LocalMessageState = 'sending' | 'failed';

export interface ChatMessageItemProps {
  message: MessageResponse;
  viewer: ChatViewer;
  // Ẩn avatar/tên khi tin liền trước cùng người gửi
  grouped?: boolean;
  localState?: LocalMessageState;
  onRetry?: () => void;
  // Chip gợi ý chỉ hiện dưới tin bot cuối cùng
  onQuickReply?: (option: string) => void;
  // Đường dẫn chi tiết đơn khác nhau giữa trang khách và admin
  orderLink?: (order: ChatOrderCard) => string;
}

export const BOT_NAME = 'Trợ lý Necom';

/**
 * Render một tin chat theo senderType/type: bubble văn bản, tin hệ thống, ghi chú nội bộ,
 * kèm thẻ sản phẩm / đơn hàng / chip gợi ý từ payload.
 */
function ChatMessageItem({
  message,
  viewer,
  grouped,
  localState,
  onRetry,
  onQuickReply,
  orderLink,
}: ChatMessageItemProps) {
  const theme = useMantineTheme();
  const palette = useChatPalette();
  const dark = theme.colorScheme === 'dark';

  if (message.type === 'SYSTEM') {
    return (
      <Group position="center" px="md" py={6}>
        <Text size="xs" color="dimmed" align="center" sx={{ maxWidth: 320 }}>
          {message.content}
        </Text>
      </Group>
    );
  }

  const isMine = viewer === 'customer'
    ? message.senderType === 'CUSTOMER'
    : message.senderType !== 'CUSTOMER';
  const isBot = message.senderType === 'BOT';
  const isNote = message.type === 'INTERNAL_NOTE';

  const bubbleColors = (() => {
    if (isNote) {
      return { bg: dark ? theme.fn.rgba(theme.colors.yellow[8], 0.25) : theme.colors.yellow[0], fg: undefined };
    }
    // Tin của bot: nền cát ấm; tin của khách/nhân viên (phía mình): xanh rừng thương hiệu
    if (isBot) {
      return { bg: palette.sand, fg: palette.text };
    }
    if (isMine) {
      return { bg: palette.forest, fg: theme.white };
    }
    return { bg: dark ? theme.colors.dark[5] : theme.colors.gray[1], fg: undefined };
  })();

  const senderName = isBot
    ? BOT_NAME
    : (message.user?.fullname || (message.senderType === 'CUSTOMER' ? 'Khách hàng' : 'Tư vấn viên'));

  const showHeader = !grouped && (!isMine || viewer === 'agent');

  const avatar = isBot
    ? <NecomAssistantAvatar size={30}/>
    : (
      <Avatar radius="xl" size={30} color={message.senderType === 'CUSTOMER' ? 'cyan' : 'teal'}>
        {senderName.charAt(0).toUpperCase()}
      </Avatar>
    );

  const payload = message.payload || {};

  return (
    <Box px="md" pt={grouped ? 2 : 10}>
      <Group
        spacing={8}
        position={isMine ? 'right' : 'left'}
        sx={{ flexWrap: 'nowrap', alignItems: 'flex-end' }}
      >
        {!isMine && <Box sx={{ width: 30, flexShrink: 0 }}>{!grouped && avatar}</Box>}

        <Stack spacing={4} sx={{ maxWidth: '82%', alignItems: isMine ? 'flex-end' : 'flex-start' }}>
          {showHeader && (
            <Group spacing={6}>
              <Text size="xs" weight={600}>{senderName}</Text>
              {isBot && (
                <Badge
                  size="xs"
                  variant="outline"
                  sx={{ color: palette.wood, borderColor: palette.sandBorder, backgroundColor: palette.cream }}
                >
                  Trợ lý AI
                </Badge>
              )}
              {message.senderType === 'AGENT' && !isNote && viewer === 'customer' &&
                <Badge size="xs" color="teal" variant="light">Tư vấn viên</Badge>}
              {isNote && (
                <Badge size="xs" color="yellow" variant="filled" leftSection={<Notes size={10}/>}>
                  Ghi chú nội bộ
                </Badge>
              )}
            </Group>
          )}

          <Tooltip label={DateUtils.isoDateToString(message.createdAt, 'HH:mm DD/MM/YYYY')} withArrow position={isMine ? 'left' : 'right'}>
            <Box
              sx={{
                backgroundColor: bubbleColors.bg,
                color: bubbleColors.fg,
                padding: '8px 12px',
                borderRadius: 16,
                borderBottomRightRadius: isMine ? 4 : 16,
                borderBottomLeftRadius: isMine ? 16 : 4,
                border: isNote ? `1px dashed ${theme.colors.yellow[6]}` : undefined,
                opacity: localState === 'sending' ? 0.7 : 1,
                wordBreak: 'break-word',
              }}
            >
              <RichText text={message.content} inverted={!!bubbleColors.fg}/>
            </Box>
          </Tooltip>

          {payload.products && payload.products.length > 0 && <ProductCards products={payload.products}/>}

          {payload.orders && payload.orders.map(order => (
            <OrderCard key={order.code} order={order} link={orderLink ? orderLink(order) : undefined}/>
          ))}

          {onQuickReply && payload.quickReplies && payload.quickReplies.length > 0 && (
            <Group spacing={6} mt={2}>
              {payload.quickReplies.map(option => (
                <Button
                  key={option}
                  size="xs"
                  radius="xl"
                  variant="default"
                  compact
                  onClick={() => onQuickReply(option)}
                  sx={{
                    color: palette.forest,
                    backgroundColor: palette.cream,
                    borderColor: palette.sandBorder,
                    fontWeight: 500,
                    '&:hover': { backgroundColor: palette.sand, borderColor: palette.wood },
                  }}
                >
                  {option}
                </Button>
              ))}
            </Group>
          )}

          {localState === 'sending' && (
            <Group spacing={4}>
              <Clock size={12} color={theme.colors.gray[5]}/>
              <Text size="xs" color="dimmed">Đang gửi…</Text>
            </Group>
          )}
          {localState === 'failed' && (
            <UnstyledButton onClick={onRetry}>
              <Group spacing={4}>
                <AlertCircle size={12} color={theme.colors.red[6]}/>
                <Text size="xs" color="red">Gửi lỗi · Bấm để gửi lại</Text>
              </Group>
            </UnstyledButton>
          )}
        </Stack>
      </Group>
    </Box>
  );
}

// Link nội bộ trong câu trả lời của bot, ví dụ /support/return-policy, /order, /user/reward
const INTERNAL_LINK = /(\/(?:support|order|user|product|category|about)[a-z0-9\-/]*)/g;
const BOLD = /\*\*(.+?)\*\*/g;

function renderInline(text: string, inverted: boolean): React.ReactNode[] {
  const nodes: React.ReactNode[] = [];
  text.split(BOLD).forEach((part, boldIndex) => {
    // Phần lẻ sau split là nội dung trong **...**
    const pieces = part.split(INTERNAL_LINK).map((piece, linkIndex) => (
      linkIndex % 2 === 1
        ? (
          <Anchor
            key={linkIndex}
            component={Link}
            to={piece}
            size="sm"
            sx={inverted ? { color: 'inherit', textDecoration: 'underline' } : undefined}
          >
            {piece}
          </Anchor>
        )
        : <React.Fragment key={linkIndex}>{piece}</React.Fragment>
    ));
    nodes.push(boldIndex % 2 === 1 ? <strong key={boldIndex}>{pieces}</strong> : <React.Fragment key={boldIndex}>{pieces}</React.Fragment>);
  });
  return nodes;
}

/**
 * Markdown tối giản và an toàn (không dùng innerHTML): **đậm**, gạch đầu dòng "- ", xuống dòng, link nội bộ.
 */
export function RichText({ text, inverted = false }: { text: string, inverted?: boolean }) {
  const lines = text.split('\n');
  return (
    <Box sx={{ fontSize: 14, lineHeight: 1.5 }}>
      {lines.map((line, index) => {
        const bullet = /^\s*[-*•]\s+/.test(line);
        const content = bullet ? line.replace(/^\s*[-*•]\s+/, '') : line;
        if (!content.trim()) {
          return <Box key={index} sx={{ height: 6 }}/>;
        }
        return (
          <Box key={index} sx={bullet ? { display: 'flex', gap: 6, paddingLeft: 4 } : undefined}>
            {bullet && <span>•</span>}
            <span>{renderInline(content, inverted)}</span>
          </Box>
        );
      })}
    </Box>
  );
}

function ProductCards({ products }: { products: ChatProductCard[] }) {
  const theme = useMantineTheme();

  return (
    <Box sx={{ display: 'flex', gap: 8, overflowX: 'auto', maxWidth: '100%', paddingBottom: 4 }}>
      {products.map(product => (
        <Card
          key={product.id}
          component={Link}
          to={'/product/' + product.slug}
          withBorder
          radius="md"
          p={8}
          sx={{
            width: 150,
            flexShrink: 0,
            transition: 'box-shadow 150ms ease, transform 150ms ease',
            '&:hover': { boxShadow: theme.shadows.sm, transform: 'translateY(-2px)' },
          }}
        >
          <Image
            radius="sm"
            height={100}
            src={product.thumbnail || undefined}
            alt={product.name}
            withPlaceholder
          />
          <Text size="xs" weight={500} mt={6} lineClamp={2} sx={{ minHeight: 32 }}>{product.name}</Text>
          {product.minPrice != null && (
            <Text size="sm" weight={700} color={theme.primaryColor}>
              {MiscUtils.formatPrice(product.minPrice)}₫
              {product.maxPrice != null && product.maxPrice !== product.minPrice && '+'}
            </Text>
          )}
        </Card>
      ))}
    </Box>
  );
}

const orderStatusColor: Record<number, string> = {
  1: 'gray',
  2: 'blue',
  3: 'teal',
  4: 'green',
  5: 'red',
};

function OrderCard({ order, link }: { order: ChatOrderCard, link?: string }) {
  const steps = ['Đã đặt', 'Xử lý', 'Đang giao', 'Đã nhận'];
  const cancelled = order.status === 5;

  return (
    <Card withBorder radius="md" p="sm" sx={{ width: 260, maxWidth: '100%' }}>
      <Group position="apart" spacing={4} mb={6}>
        <Group spacing={6}>
          <Package size={16}/>
          <Text size="sm" weight={600}>#{order.code}</Text>
        </Group>
        <Badge size="xs" color={orderStatusColor[order.status] || 'gray'} variant="light">{order.statusLabel}</Badge>
      </Group>
      {!cancelled && (
        <Group spacing={4} mb={6} sx={{ flexWrap: 'nowrap' }}>
          {steps.map((step, index) => (
            <Box key={step} sx={{ flex: 1 }}>
              <Box
                sx={theme => ({
                  height: 4,
                  borderRadius: 2,
                  backgroundColor: index < order.status
                    ? theme.colors[theme.primaryColor][6]
                    : (theme.colorScheme === 'dark' ? theme.colors.dark[4] : theme.colors.gray[3]),
                })}
              />
              <Text size="xs" color="dimmed" mt={2} sx={{ fontSize: 10 }}>{step}</Text>
            </Box>
          ))}
        </Group>
      )}
      <Group position="apart">
        <Text size="xs" color="dimmed">
          {order.itemCount} sản phẩm · {DateUtils.isoDateToString(order.createdAt, 'DD/MM/YYYY')}
        </Text>
        <Text size="sm" weight={700}>{MiscUtils.formatPrice(order.totalPay)}₫</Text>
      </Group>
      {link && (
        <Button component={Link} to={link} size="xs" variant="light" fullWidth mt={8} compact>
          Xem chi tiết
        </Button>
      )}
    </Card>
  );
}

export default ChatMessageItem;
