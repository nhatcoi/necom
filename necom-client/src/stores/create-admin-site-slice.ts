import { SliceCreator } from 'stores/use-app-store';

export interface AdminSiteState {
  opened: boolean;
  toggleOpened: () => void;
  navbarCollapsed: boolean;
  toggleNavbarCollapsed: () => void;
}

const NAVBAR_COLLAPSED_KEY = 'necom-admin-navbar-collapsed';

const readNavbarCollapsed = () => {
  try {
    return localStorage.getItem(NAVBAR_COLLAPSED_KEY) === '1';
  } catch {
    return false;
  }
};

const initialAdminSiteState = {
  opened: false,
  navbarCollapsed: readNavbarCollapsed(),
};

const createAdminSiteSlice: SliceCreator<AdminSiteState> = (set) => ({
  ...initialAdminSiteState,
  toggleOpened: () => set((state) => ({ opened: !state.opened })),
  toggleNavbarCollapsed: () => set((state) => {
    const navbarCollapsed = !state.navbarCollapsed;
    try {
      localStorage.setItem(NAVBAR_COLLAPSED_KEY, navbarCollapsed ? '1' : '0');
    } catch {
      // Trình duyệt chặn storage: chỉ giữ trong phiên hiện tại
    }
    return { navbarCollapsed };
  }),
});

export default createAdminSiteSlice;
