import BaseResponse from 'models/BaseResponse';

export type MessageType = 'TEXT' | 'SYSTEM' | 'INTERNAL_NOTE';

export type SenderType = 'CUSTOMER' | 'AGENT' | 'BOT' | 'SYSTEM';

export interface ChatProductCard {
  id: number;
  name: string;
  slug: string;
  category: string | null;
  thumbnail: string | null;
  minPrice?: number;
  maxPrice?: number;
  // Số lượng còn bán được (bot v3); undefined với tin cũ
  inStock?: number;
}

export interface ChatOrderCard {
  id: number;
  code: string;
  status: number;
  statusLabel: string;
  totalPay: number;
  createdAt: string;
  itemCount: number;
}

export interface MessagePayload {
  products?: ChatProductCard[];
  orders?: ChatOrderCard[];
  quickReplies?: string[];
  event?: 'WAITING_AGENT' | 'AGENT_JOINED';
  agentName?: string;
}

export interface MessageResponse extends BaseResponse {
  content: string;
  status: number;
  // Null với tin của bot và tin hệ thống
  user: UserResponse | null;
  roomId: number;
  type: MessageType;
  senderType: SenderType;
  payload: MessagePayload | null;
  clientMsgId: string | null;
}

interface UserResponse {
  id: number;
  username: string;
  fullname: string;
  email: string;
}

export interface MessageRequest {
  content: string;
  status: number;
  userId: number;
  roomId: number;
}

export interface ChatSendRequest {
  content: string;
  clientMsgId: string;
}

export interface ChatTyping {
  senderType: SenderType;
  name: string;
  active: boolean;
}

export interface ChatEvent {
  kind: 'MESSAGE' | 'ROOM' | 'TYPING';
  roomId: number;
  message?: MessageResponse;
  room?: import('models/Room').RoomResponse;
  typing?: ChatTyping;
}
