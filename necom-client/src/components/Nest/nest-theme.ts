import { MantineProviderProps, Tuple } from '@mantine/core';

// Theme Mantine riêng cho storefront Nest: các trang cũ (danh mục, sản phẩm, giỏ hàng, tài khoản…)
// dùng màu hard-code như pink/teal/blue/green. Thay vì sửa từng chỗ, các dải màu đó được ánh xạ
// về bảng màu Nest: xanh ô liu cho hành động, đất nung cho giá/nhấn, đá ong cho cảnh báo nhẹ.
const olive: Tuple<string, 10> = ['#f4f5ed', '#e8eadb', '#d7dac3', '#bcc3a0', '#9da67d', '#7d8961', '#626b50', '#515a41', '#414934', '#323a28'];
const clay: Tuple<string, 10> = ['#f8f1ec', '#efdfd4', '#e0c3b0', '#cfa488', '#bd8766', '#a86f50', '#885943', '#704836', '#58392b', '#402a20'];
const ochre: Tuple<string, 10> = ['#faf5e8', '#f1e6c7', '#e5d2a0', '#d7bc77', '#c9a655', '#b18f42', '#8f7335', '#6e5829', '#4f401e', '#332913'];
const brick: Tuple<string, 10> = ['#f9eeec', '#f0d6d1', '#e3b3aa', '#d38c80', '#c26a5c', '#a95446', '#8a4438', '#6b352c', '#4d2720', '#321a15'];
const stone: Tuple<string, 10> = ['#fbf9f4', '#f6f2eb', '#eae6de', '#dedbcf', '#c9c5b8', '#a3a095', '#7a786e', '#5c5b52', '#3f3e37', '#292820'];

const LINE = '#dedbcf';
const PAPER = '#fbf9f4';
const SERIF = 'Lora, Georgia, Times New Roman, serif';
const SANS = 'Be Vietnam Pro, -apple-system, Segoe UI, sans-serif';

export const nestTheme: MantineProviderProps['theme'] = {
  colorScheme: 'light',
  primaryColor: 'nest',
  white: PAPER,
  black: '#292820',
  fontFamily: SANS,
  defaultRadius: 2,
  radius: { xs: 2, sm: 2, md: 3, lg: 4, xl: 6 },
  colors: {
    nest: olive,
    teal: olive,
    green: olive,
    blue: olive,
    cyan: olive,
    violet: olive,
    grape: clay,
    pink: clay,
    orange: ochre,
    yellow: ochre,
    red: brick,
    gray: stone,
  },
  shadows: {
    xs: '0 1px 2px rgba(41, 40, 32, .05)',
    sm: '0 1px 3px rgba(41, 40, 32, .06)',
    md: '0 10px 24px rgba(41, 40, 32, .07)',
    lg: '0 16px 32px rgba(41, 40, 32, .08)',
    xl: '0 24px 48px rgba(41, 40, 32, .10)',
  },
  headings: {
    fontFamily: SERIF,
    fontWeight: 400,
    sizes: {
      h1: { fontSize: 40, lineHeight: 1.2 },
      h2: { fontSize: 32, lineHeight: 1.25 },
      h3: { fontSize: 24, lineHeight: 1.3 },
      h4: { fontSize: 19, lineHeight: 1.4 },
      h5: { fontSize: 16, lineHeight: 1.5 },
      h6: { fontSize: 14, lineHeight: 1.5 },
    },
  },
};

// Card/Paper phẳng: viền mảnh thay cho bóng đổ, đúng tinh thần trang chủ Nest
export const nestStyles: MantineProviderProps['styles'] = {
  Card: { root: { backgroundColor: PAPER, border: `1px solid ${LINE}`, boxShadow: 'none !important' } },
  Title: { root: { letterSpacing: '-0.5px' } },
  Button: { root: { fontWeight: 400, letterSpacing: 0.2 } },
  Badge: { root: { textTransform: 'none', fontWeight: 500, letterSpacing: 0 } },
  Input: { input: { backgroundColor: 'transparent', borderColor: LINE } },
  Chips: {
    label: { backgroundColor: 'transparent', border: `1px solid ${LINE}`, borderRadius: 999, fontSize: 12 },
    filled: { backgroundColor: 'transparent', border: `1px solid ${LINE}` },
    checked: { backgroundColor: `${olive[6]} !important`, borderColor: `${olive[6]} !important`, color: '#fff !important' },
    iconWrapper: { color: '#fff' },
  },
  Breadcrumbs: { breadcrumb: { fontSize: 12 }, separator: { color: stone[5] } },
  Tabs: { tabLabel: { fontWeight: 400 } },
  Modal: { title: { fontFamily: SERIF, fontSize: 22 } },
};
