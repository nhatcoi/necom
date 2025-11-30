I'll scan key client files to identify libraries and frameworks used: `package.json`, `tsconfig.json`, `src/index.tsx`, `src/App.tsx`, and `README.md`.

[5 tools called]

I just scanned `package.json`, `tsconfig.json`, `src/index.tsx`, `src/App.tsx`, và `README.md`. Bây giờ mình sẽ tóm tắt tech stack phía client.

### Công nghệ/stack chính
- **Nền tảng**: React 17 + TypeScript (CRA `react-scripts` 5)
- **Routing**: `react-router-dom` v6 (cấu trúc `Routes`/`Route`, `BrowserRouter`)
- **Data fetching/caching**: `react-query` v3 + `ReactQueryDevtools`
- **UI kit & theming**: Mantine v4 (`@mantine/core`, `hooks`, `form`, `modals`, `notifications`, `dates`, `dropzone`, `prism`, `rte`, `spotlight`) với `MantineProvider`, `ColorSchemeProvider` (dark/light theme), `withNormalizeCSS`, `withGlobalStyles`
- **State management**: `zustand` + `immer` và `react-tracked` (tối ưu re-render)
- **Validation**: `zod`
- **Datetime**: `dayjs` (thiết lập locale `vi`)
- **Charts**: `recharts`
- **Carousel**: `react-responsive-carousel`
- **Icons**: `tabler-icons-react`
- **Rating**: `@smastrom/react-rating`
- **STOMP/WebSocket**: `react-stomp-hooks` (chat/realtime, URL từ `ApplicationConstants.WEBSOCKET_PATH`)
- **Utility**: `lodash.isequal`
- **Performance metrics**: `web-vitals`
- **Testing**: `@testing-library/react`, `@testing-library/user-event`, `@testing-library/jest-dom` (+ types Jest)
- **Linting**: ESLint với `@typescript-eslint` plugins, cấu hình `react-app`/`react-app/jest`
- **TypeScript config**: strict mode, `jsx: react-jsx`, `baseUrl: src`, `skipLibCheck`, `noEmit`

### Kiến trúc/khởi tạo
- `src/index.tsx` bọc `App` bởi `BrowserRouter` và `React.StrictMode`.
- `src/App.tsx` bọc toàn bộ app bởi `QueryClientProvider` → `ColorSchemeProvider` → `MantineProvider` → `NotificationsProvider` → `ModalsProvider`, sau đó định nghĩa routing dày đặc cho khu Client/Admin; phần chat bọc trong `StompSessionProvider`.
- Tổ chức code theo module pages/components rõ ràng; có `stores/` (phù hợp với Zustand), `constants/`, `hooks/`, `utils/`, `models/`.

### Build/run
- CRA scripts: `start`, `build`, `test`, `eject`.
- `browserslist` cho prod/dev.

Tóm lại: Client là ứng dụng React 17 + TypeScript dựng bằng CRA, dùng React Router v6 cho điều hướng, React Query cho data caching, Mantine làm UI/theming và tiện ích (form/modals/notifications…), Zustand (+immer/react-tracked) cho state cục bộ, Zod cho validation, Dayjs cho thời gian, Recharts/Carousel/Icon cho UI, và STOMP hooks cho realtime.

Tôi đã đọc các file chính và tổng hợp stack; nếu bạn muốn mình vẽ sơ đồ dependency hoặc rà soát cách các `stores` Zustand đang được dùng trong `pages/components`, mình có thể tiếp tục.                                                                                            