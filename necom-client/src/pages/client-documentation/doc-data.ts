// Nội dung tài liệu dự án Necom, tách khỏi phần trình bày để dễ cập nhật.

export const DOC_SECTIONS = [
  { id: 'overview', label: 'Tổng quan' },
  { id: 'accounts', label: 'Tài khoản demo' },
  { id: 'architecture', label: 'Kiến trúc hệ thống' },
  { id: 'deployment', label: 'Triển khai' },
  { id: 'actors', label: 'Tác nhân' },
  { id: 'usecases', label: 'Use case' },
  { id: 'order-flow', label: 'Đặt hàng & vận đơn' },
  { id: 'checkout', label: 'Luồng thanh toán' },
  { id: 'chat', label: 'Chat CSKH & trợ lý AI' },
  { id: 'reward', label: 'Điểm thưởng' },
  { id: 'domain', label: 'Mô hình dữ liệu' },
  { id: 'statuses', label: 'Bảng trạng thái' },
  { id: 'api', label: 'API & realtime' },
  { id: 'operations', label: 'Vận hành' },
] as const;

export const TECH_STACK = [
  { layer: 'Frontend', items: ['React 17', 'TypeScript', 'Mantine UI 4', 'React Query', 'Zustand', 'STOMP over SockJS'] },
  { layer: 'Backend', items: ['Spring Boot 3.5', 'Java 17', 'Spring Security 6 + JWT', 'Spring Data JPA (Hibernate 6)', 'MapStruct', 'RSQL filter', 'WebSocket STOMP', 'SSE'] },
  { layer: 'Dữ liệu', items: ['MySQL 8.0', 'utf8mb4', '63 bảng'] },
  { layer: 'Hạ tầng', items: ['Docker Compose', 'Nginx', 'Cloudflare', 'VPS Ubuntu'] },
  { layer: 'AI', items: ['Agent LLM + function calling', 'multilingual-e5-small (ONNX int8)', 'Hybrid search BM25 + vector'] },
  { layer: 'Tích hợp', items: ['Giao Hàng Nhanh (GHN)', 'PayPal Sandbox', 'LLM tương thích OpenAI', 'SMTP Gmail'] },
];

export const DEMO_ACCOUNTS = [
  { username: 'admin', password: 'admin123', role: 'ADMIN', name: 'Quản trị viên', entry: '/admin', note: 'Toàn quyền quản trị, gồm nhân sự, điểm thưởng, sổ quỹ' },
  { username: 'employee', password: 'admin123', role: 'EMPLOYEE', name: 'Nhân viên', entry: '/admin', note: 'Đơn hàng, vận đơn, tồn kho, đánh giá, inbox CSKH; không vào được sản phẩm, nhân sự, điểm thưởng, sổ quỹ' },
  { username: 'customer', password: 'admin123', role: 'CUSTOMER', name: 'Khách hàng', entry: '/signin', note: 'Mua hàng, theo dõi đơn, chat với trợ lý AI và tư vấn viên' },
  { username: 'dnucator0 · jgratten1', password: 'admin123', role: 'ADMIN', name: 'Dữ liệu mẫu', entry: '/admin', note: 'Tài khoản seed bổ sung' },
  { username: 'ethuillier2', password: 'admin123', role: 'EMPLOYEE', name: 'Dữ liệu mẫu', entry: '/admin', note: 'Tài khoản seed bổ sung' },
  { username: 'dtreat3 · tkorting4', password: 'admin123', role: 'CUSTOMER', name: 'Dữ liệu mẫu', entry: '/signin', note: 'Tài khoản seed bổ sung' },
];

export const SEED_FACTS = [
  { label: 'Sản phẩm', value: '294', hint: '630 phiên bản (variant)' },
  { label: 'Danh mục', value: '14', hint: 'Bàn ghế, Sofa, Phòng ngủ, Rèm & thảm…' },
  { label: 'Thương hiệu', value: '8', hint: 'Muji, IKEA, JYSK, Nhà Xinh…' },
  { label: 'Đánh giá mẫu', value: '339', hint: 'Từ các tài khoản khách hàng seed' },
];

export const PERMISSIONS: { feature: string, guest: boolean, customer: boolean, employee: boolean, admin: boolean }[] = [
  { feature: 'Xem sản phẩm, tìm kiếm, danh mục', guest: true, customer: true, employee: true, admin: true },
  { feature: 'Giỏ hàng, đặt hàng, thanh toán', guest: false, customer: true, employee: false, admin: false },
  { feature: 'Yêu thích, đặt trước, đánh giá, điểm thưởng', guest: false, customer: true, employee: false, admin: false },
  { feature: 'Chat với trợ lý AI / tư vấn viên', guest: false, customer: true, employee: false, admin: false },
  { feature: 'Thống kê, đơn hàng, vận đơn, tồn kho, đánh giá', guest: false, customer: false, employee: true, admin: true },
  { feature: 'Inbox CSKH, ghi chú nội bộ, gợi ý trả lời AI', guest: false, customer: false, employee: true, admin: true },
  { feature: 'Sản phẩm, danh mục, thương hiệu, nhà cung cấp', guest: false, customer: false, employee: false, admin: true },
  { feature: 'Người dùng, nhân viên, khách hàng, địa chỉ', guest: false, customer: false, employee: false, admin: true },
  { feature: 'Chiến lược điểm thưởng, sổ quỹ, khuyến mãi', guest: false, customer: false, employee: false, admin: true },
];

export const ORDER_STATUSES = [
  { code: 1, label: 'Đơn hàng mới', note: 'Khách vừa đặt, chờ cửa hàng xử lý' },
  { code: 2, label: 'Đang xử lý', note: 'Đã tạo vận đơn GHN, chờ lấy hàng' },
  { code: 3, label: 'Đang giao hàng', note: 'GHN đã lấy hàng / đang vận chuyển' },
  { code: 4, label: 'Đã giao hàng', note: 'Giao thành công, tự chuyển "Đã thanh toán" và cộng điểm' },
  { code: 5, label: 'Hủy bỏ', note: 'Khách/nhân viên hủy, hoặc GHN giao thất bại / hoàn hàng' },
];

export const PAYMENT_STATUSES = [
  { code: 1, label: 'Chưa thanh toán', note: 'Mặc định khi tạo đơn (COD) hoặc chưa capture PayPal' },
  { code: 2, label: 'Đã thanh toán', note: 'PayPal capture thành công hoặc COD giao thành công' },
];

export const WAYBILL_STATUSES = [
  { code: 1, label: 'Đợi lấy hàng', ghn: 'ready_to_pick, picking' },
  { code: 2, label: 'Đang giao', ghn: 'picked, transporting, sorting, delivering, money_collect_*' },
  { code: 3, label: 'Đã giao', ghn: 'delivered' },
  { code: 4, label: 'Hủy / thất bại', ghn: 'cancel, delivery_fail, waiting_to_return, return' },
];

export const ROOM_STATUSES = [
  { code: 'BOT', label: 'Trợ lý AI đang trả lời', note: 'Mặc định khi khách mở chat' },
  { code: 'WAITING_AGENT', label: 'Chờ tư vấn viên', note: 'Khách yêu cầu gặp người, khiếu nại, hoặc bot chuyển tiếp' },
  { code: 'AGENT', label: 'Tư vấn viên phụ trách', note: 'Nhân viên nhận phiên hoặc trả lời trực tiếp' },
  { code: 'RESOLVED', label: 'Đã kết thúc', note: 'Khách nhắn lại sẽ mở phiên mới với bot' },
];

export const MESSAGE_TYPES = [
  { code: 'TEXT', label: 'Tin nhắn thường', note: 'Có thể kèm payload: thẻ sản phẩm, thẻ đơn hàng, gợi ý trả lời' },
  { code: 'SYSTEM', label: 'Tin hệ thống', note: '"Tư vấn viên đã tham gia", "Cuộc trò chuyện đã kết thúc"…' },
  { code: 'INTERNAL_NOTE', label: 'Ghi chú nội bộ', note: 'Chỉ nhân viên thấy, không bao giờ gửi tới khách' },
];

export const REWARD_STRATEGIES = [
  { code: 'SUCCESS_ORDER', label: 'Đơn hàng thành công', formula: 'Tổng thanh toán / 1.000', example: 'Đơn 4.990.000đ → 4.990 điểm' },
  { code: 'ADD_REVIEW', label: 'Viết đánh giá', formula: '50 điểm / đánh giá', example: 'Đánh giá sản phẩm đã mua → +50' },
];

export const DOMAIN_MODULES: { name: string, key: string, entities: string[], note: string }[] = [
  { name: 'Xác thực', key: 'authentication', entities: ['User', 'Role', 'RefreshToken', 'Verification'], note: 'JWT, xác minh email, phân quyền' },
  { name: 'Địa chỉ', key: 'address', entities: ['Province', 'District', 'Ward', 'Address'], note: 'Đồng bộ mã GHN' },
  { name: 'Sản phẩm', key: 'product', entities: ['Product', 'Variant', 'Category', 'Brand', 'Supplier', 'Unit', 'Tag', 'Guarantee', 'Property', 'Specification'], note: 'Thuộc tính & thông số lưu JSON' },
  { name: 'Tồn kho', key: 'inventory', entities: ['Warehouse', 'Docket', 'DocketVariant', 'DocketReason', 'PurchaseOrder', 'PurchaseOrderVariant', 'Destination', 'Count', 'CountVariant', 'Transfer', 'StorageLocation'], note: 'Phiếu nhập/xuất quyết định số lượng có thể bán' },
  { name: 'Đơn hàng', key: 'order', entities: ['Order', 'OrderVariant', 'OrderResource', 'OrderCancellationReason'], note: 'COD & PayPal' },
  { name: 'Vận đơn', key: 'waybill', entities: ['Waybill', 'WaybillLog'], note: 'Tạo qua API GHN, cập nhật qua webhook' },
  { name: 'Giỏ hàng', key: 'cart', entities: ['Cart', 'CartVariant'], note: 'Một giỏ hiệu lực / khách' },
  { name: 'Khách hàng', key: 'customer', entities: ['Customer', 'CustomerGroup', 'CustomerStatus', 'CustomerResource'], note: 'Phân nhóm & nguồn khách' },
  { name: 'Nhân viên', key: 'employee', entities: ['Employee', 'Office', 'Department', 'JobTitle', 'JobLevel', 'JobType'], note: 'Hồ sơ nhân sự' },
  { name: 'Chat CSKH', key: 'chat', entities: ['Room', 'Message'], note: 'Trạng thái phiên, payload JSON, chống trùng' },
  { name: 'Điểm thưởng', key: 'reward', entities: ['RewardStrategy', 'RewardLog'], note: 'Công thức SpEL' },
  { name: 'Tương tác', key: 'misc', entities: ['Review', 'Wish', 'Preorder', 'Notification', 'Image', 'Promotion'], note: 'Đánh giá, yêu thích, thông báo SSE' },
  { name: 'Sổ quỹ', key: 'cashbook', entities: ['PaymentMethod'], note: 'CASH, PAYPAL' },
];

export const API_GROUPS = [
  { prefix: '/api/auth/**', access: 'Công khai', note: 'Đăng nhập, đăng ký, quên mật khẩu (info cần ADMIN/EMPLOYEE)' },
  { prefix: '/api/{resource}', access: 'Quản trị', note: 'CRUD generic: products, orders, users… hỗ trợ page, size, sort, filter (RSQL), search' },
  { prefix: '/api/chat/**', access: 'ADMIN, EMPLOYEE', note: 'Inbox CSKH: rooms, messages, claim, release, resolve, notes, suggest, customer' },
  { prefix: '/client-api/products, categories, filters', access: 'Công khai', note: 'Storefront: danh sách, chi tiết, bộ lọc' },
  { prefix: '/client-api/{carts, orders, wishes, …}', access: 'CUSTOMER', note: 'Nghiệp vụ của khách đã đăng nhập' },
  { prefix: '/client-api/chat/**', access: 'CUSTOMER', note: 'Phòng chat của khách: get-room, messages, request-agent, read, resolve' },
  { prefix: '/client-api/notifications/events', access: 'Theo UUID', note: 'Luồng SSE thông báo realtime' },
  { prefix: 'PUT /api/waybills/callback-ghn', access: 'GHN webhook', note: 'Cập nhật trạng thái vận đơn và đơn hàng' },
];

export const STOMP_CHANNELS = [
  { destination: 'CONNECT /ws (SockJS)', who: 'Mọi client', note: 'Bắt buộc header Authorization: Bearer <JWT>' },
  { destination: 'SEND /chat/send/{roomId}', who: 'Chủ phòng, nhân viên', note: 'Body { content, clientMsgId }; người gửi lấy từ JWT' },
  { destination: 'SEND /chat/send/{roomId}/typing', who: 'Chủ phòng, nhân viên', note: 'Chỉ báo đang nhập' },
  { destination: 'SUBSCRIBE /chat/receive/{roomId}', who: 'Chủ phòng, nhân viên', note: 'MESSAGE / ROOM / TYPING, không có ghi chú nội bộ' },
  { destination: 'SUBSCRIBE /chat/receive/admin', who: 'ADMIN, EMPLOYEE', note: 'Mọi sự kiện của mọi phòng cho inbox' },
];

export const ENV_VARS = [
  { name: 'MYSQL_*', note: 'Tài khoản & tên CSDL MySQL' },
  { name: 'JWT_SECRET, JWT_EXPIRATION_MS', note: 'Khóa ký HS512 (ngắn hơn 64 byte sẽ được băm SHA-512)' },
  { name: 'GHN_TOKEN, GHN_SHOP_ID, GHN_API_PATH', note: 'Kết nối Giao Hàng Nhanh' },
  { name: 'PAYPAL_CLIENT_ID, PAYPAL_SECRET', note: 'PayPal Sandbox' },
  { name: 'MAIL_USERNAME, MAIL_PASSWORD', note: 'SMTP gửi email xác minh, đặt lại mật khẩu' },
  { name: 'CHATBOT_BASE_URL, CHATBOT_API_KEY, CHATBOT_MODEL', note: 'Trợ lý AI (API tương thích OpenAI); để trống key để tắt bot' },
];

export const OPERATION_COMMANDS = [
  { title: 'Chạy toàn bộ bằng Docker', command: 'docker compose up -d --build' },
  { title: 'Deploy đầy đủ lên VPS', command: './deploy-vps.sh' },
  { title: 'Chỉ deploy frontend / backend', command: './deploy-vps.sh --only-fe   # hoặc --only-be' },
  { title: 'Xem trạng thái & log VPS', command: './deploy-vps.sh --status && ./deploy-vps.sh --logs server' },
  { title: 'Build backend (Java 17)', command: 'cd necom-server && ./mvn-java17.sh clean package -DskipTests' },
];
