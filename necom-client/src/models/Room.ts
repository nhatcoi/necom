import BaseResponse from 'models/BaseResponse';
import { MessageResponse } from 'models/Message';

export type RoomStatus = 'BOT' | 'WAITING_AGENT' | 'AGENT' | 'RESOLVED';

export interface RoomResponse extends BaseResponse {
  name: string;
  user: UserResponse;
  lastMessage: MessageResponse | null;
  status: RoomStatus;
  assignee: UserResponse | null;
  unreadCount: number | null;
}

interface UserResponse {
  id: number;
  username: string;
  fullname: string;
  email: string;
}

export interface RoomRequest {
  name: string;
  userId: number;
}

export interface ChatCustomerProfileResponse {
  id: number;
  username: string;
  fullname: string;
  email: string;
  phone: string;
  createdAt: string;
  rewardScore: number;
  totalOrders: number;
  recentOrders: {
    id: number;
    code: string;
    status: number;
    statusLabel: string;
    totalPay: number;
    createdAt: string;
  }[];
}
