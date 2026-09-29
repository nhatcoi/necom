import { authStore as useAuthStore } from 'stores/use-auth-store';
import { adminAuthStore as useAdminAuthStore } from 'stores/use-admin-auth-store';
import NotifyUtils from 'utils/NotifyUtils';

/**
 * Quản lý phiên đăng nhập phía client: JWT có hạn (mặc định vài giờ) nhưng store lưu trong localStorage
 * không tự hết hạn, nên khi token cũ còn lại mọi API cần đăng nhập sẽ trả 401 liên tục.
 */

/** Đọc thời điểm hết hạn (giây) trong payload JWT; null nếu không đọc được. */
function readJwtExpiry(token: string): number | null {
  try {
    const payload = token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/');
    const exp = JSON.parse(atob(payload)).exp;
    return typeof exp === 'number' ? exp : null;
  } catch {
    return null;
  }
}

/** Token đã (hoặc sắp, trong vòng 30 giây) hết hạn. */
export function isJwtExpired(token: string | null | undefined): boolean {
  if (!token) return true;
  const exp = readJwtExpiry(token);
  return exp !== null && exp * 1000 - 30_000 < Date.now();
}

let notified = false;

/** Xóa phiên đăng nhập đã hết hạn; báo cho người dùng một lần nếu họ đang đăng nhập. */
export function expireSession(isAdmin?: boolean, notify = true) {
  const hadSession = isAdmin ? !!useAdminAuthStore.getState().user : !!useAuthStore.getState().user;
  if (isAdmin) {
    useAdminAuthStore.getState().resetAdminAuthState();
  } else {
    useAuthStore.getState().resetAuthState();
  }
  if (hadSession && notify && !notified) {
    notified = true;
    NotifyUtils.simple('Phiên đăng nhập đã hết hạn, vui lòng đăng nhập lại');
    setTimeout(() => { notified = false; }, 5000);
  }
}

/** Gọi khi khởi động ứng dụng: bỏ phiên đã hết hạn trước khi các trang gọi API. */
export function purgeExpiredSessions() {
  if (useAuthStore.getState().jwtToken && isJwtExpired(useAuthStore.getState().jwtToken)) {
    expireSession(false, false);
  }
  if (useAdminAuthStore.getState().jwtToken && isJwtExpired(useAdminAuthStore.getState().jwtToken)) {
    expireSession(true, false);
  }
}

/** Lỗi 401 tạo tại client khi không có token hợp lệ, cùng dạng ErrorMessage của server. */
export function sessionExpiredError(resourceUrl: string) {
  return {
    statusCode: 401,
    timestamp: new Date().toISOString(),
    message: 'Phiên đăng nhập đã hết hạn',
    description: 'uri=' + resourceUrl,
  };
}
