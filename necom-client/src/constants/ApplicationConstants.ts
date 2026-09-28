const getBackendHost = (): string => {
  if (process.env.REACT_APP_API_URL !== undefined) {
    return process.env.REACT_APP_API_URL;
  }
  // When running behind Nginx reverse proxy (Docker, VPS, Production),
  // use relative path so requests stay on the same origin (no CORS issues).
  if (typeof window !== 'undefined' && window.location.port !== '3000') {
    return '';
  }
  return 'http://localhost:8085';
};

const getWebSocketHost = (): string => {
  if (process.env.REACT_APP_WS_URL) {
    return process.env.REACT_APP_WS_URL;
  }
  if (typeof window !== 'undefined') {
    if (window.location.port === '3000') {
      return 'http://localhost:8085/ws';
    }
    const protocol = window.location.protocol === 'https:' ? 'https:' : 'http:';
    return `${protocol}//${window.location.host}/ws`;
  }
  return 'http://localhost:8085/ws';
};

class ApplicationConstants {
  static HOME_PATH = getBackendHost();
  static API_PATH = ApplicationConstants.HOME_PATH + '/api';
  static CLIENT_API_PATH = ApplicationConstants.HOME_PATH + '/client-api';
  static WEBSOCKET_PATH = getWebSocketHost();

  static DEFAULT_TAX = 0.1;
  static DEFAULT_SHIPPING_COST = 0;

  static DEFAULT_CLIENT_CATEGORY_PAGE_SIZE = 9;
  static DEFAULT_CLIENT_SEARCH_PAGE_SIZE = 12;
  static DEFAULT_CLIENT_WISHLIST_PAGE_SIZE = 5;
  static DEFAULT_CLIENT_PREORDER_PAGE_SIZE = 5;
  static DEFAULT_CLIENT_USER_REVIEW_PAGE_SIZE = 5;
  static DEFAULT_CLIENT_PRODUCT_REVIEW_PAGE_SIZE = 5;
  static DEFAULT_CLIENT_NOTIFICATION_PAGE_SIZE = 5;
  static DEFAULT_CLIENT_ORDER_PAGE_SIZE = 5;
}

export default ApplicationConstants;

