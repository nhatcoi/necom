import React, { useEffect, useRef, useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Bell, ChevronDown, Heart, Leaf, Menu2, Search, ShoppingCart, UserCircle } from 'tabler-icons-react';
import { useQuery } from 'react-query';
import useAuthStore from 'stores/use-auth-store';
import useClientSiteStore from 'stores/use-client-site-store';
import useWishlist from 'hooks/use-wishlist';
import FetchUtils, { ErrorMessage } from 'utils/FetchUtils';
import ResourceURL from 'constants/ResourceURL';
import NotifyUtils from 'utils/NotifyUtils';
import MiscUtils from 'utils/MiscUtils';
import { ClientCategoryResponse, CollectionWrapper } from 'types';
import { EventInitiationResponse, NotificationResponse } from 'models/Notification';

function ClientHeader() {
  const { user, resetAuthState, currentTotalCartItems } = useAuthStore();
  const { totalWishes } = useWishlist();
  const { newNotifications } = useClientSiteStore();
  const [search, setSearch] = useState('');
  const [seenNotifications, setSeenNotifications] = useState(0);
  const navigate = useNavigate();
  const location = useLocation();
  const headerRef = useRef<HTMLElement>(null);
  useNotificationEvents();
  const { data: categories } = useQuery<CollectionWrapper<ClientCategoryResponse>, ErrorMessage>(
    ['client-api', 'categories', 'getAllCategories'],
    () => FetchUtils.get(ResourceURL.CLIENT_CATEGORY),
    { staleTime: 300000, refetchOnWindowFocus: false }
  );

  useEffect(() => {
    headerRef.current?.querySelectorAll('details[open]').forEach((item) => item.removeAttribute('open'));
  }, [location.pathname, location.search, location.hash]);

  useEffect(() => {
    const close = (event: MouseEvent) => {
      headerRef.current?.querySelectorAll('details[open]').forEach((item) => {
        if (!item.contains(event.target as Node)) item.removeAttribute('open');
      });
    };
    document.addEventListener('click', close);
    return () => document.removeEventListener('click', close);
  }, []);

  const submitSearch = (event: React.FormEvent) => {
    event.preventDefault();
    if (search.trim()) navigate(`/search?q=${encodeURIComponent(search.trim())}`);
  };
  const closeOnEscape = (event: React.KeyboardEvent<HTMLElement>) => {
    if (event.key !== 'Escape') return;
    const activeMenu = (event.target as HTMLElement).closest('details');
    headerRef.current?.querySelectorAll('details[open]').forEach((item) => item.removeAttribute('open'));
    activeMenu?.querySelector('summary')?.focus();
  };
  const categoryLinks = categories?.content.map((category) => (
    <Link key={category.categorySlug} to={`/category/${category.categorySlug}`}>{category.categoryName}</Link>
  ));

  return (
    <header className="nest-header" ref={headerRef} onKeyDown={closeOnEscape}>
      <a className="nest-skip" href="#main-content">Đến nội dung chính</a>
      <div className="nest-announcement">
        <Link to="/support/shipping"><Leaf size={13}/> Miễn phí giao hàng cho đơn từ 1.000.000₫ <span>→</span></Link>
        <Link to="/contact" className="nest-announcement-contact">Cùng bạn chăm chút tổ ấm</Link>
      </div>
      <div className="nest-nav nest-container">
        <Link to="/" className="nest-logo" aria-label="Nest — Trang chủ">nest<span>.</span></Link>
        <nav className="nest-desktop-nav" aria-label="Điều hướng chính">
          <details className="nest-dropdown nest-categories-menu">
            <summary>Sản phẩm <ChevronDown size={13}/></summary>
            <div className="nest-dropdown-panel nest-mega-menu">
              <div><p className="nest-eyebrow">CHO MỘT TỔ ẤM TRỌN VẸN</p><h2>Những điều bạn yêu,<br/>trong chính ngôi nhà mình.</h2><Link className="nest-text-link" to="/search">Tất cả sản phẩm ↗</Link></div>
              <div className="nest-category-links">{categoryLinks || <Link to="/all-categories">Khám phá danh mục</Link>}</div>
            </div>
          </details>
          <Link to="/#spaces">Không gian</Link>
          <Link to="/#collections">Bộ sưu tập</Link>
          <Link to="/#journal">Cảm hứng</Link>
        </nav>
        <form className="nest-search" role="search" onSubmit={submitSearch}>
          <button type="submit" aria-label="Tìm kiếm"><Search size={17} strokeWidth={1.5}/></button>
          <input aria-label="Tìm sản phẩm" placeholder="Tìm điều bạn yêu cho tổ ấm…" value={search} onChange={(event) => setSearch(event.target.value)}/>
        </form>
        <div className="nest-nav-actions">
          {user && <button type="button" className="nest-icon nest-notification" aria-label="Thông báo" onClick={() => { setSeenNotifications(newNotifications.length); navigate('/user/notification'); }}>
            <Bell size={20} strokeWidth={1.4}/>{newNotifications.length > seenNotifications && <i className="nest-notification-dot"/>}
          </button>}
          <Link className="nest-icon nest-wishlist-link" to="/user/wishlist" aria-label={`Danh sách yêu thích, ${totalWishes} sản phẩm`}><Heart size={20} strokeWidth={1.4}/></Link>
          <details className="nest-dropdown nest-account-menu">
            <summary className="nest-icon" aria-label="Tài khoản"><UserCircle size={22} strokeWidth={1.4}/></summary>
            <div className="nest-dropdown-panel nest-account-links">
              {user ? <>
                <Link to="/user">Tài khoản của tôi</Link><Link to="/order">Đơn hàng</Link>
                <Link to="/user/wishlist">Sản phẩm yêu thích ({totalWishes})</Link>
                <Link to="/user/notification">Thông báo</Link><Link to="/user/setting">Thiết đặt</Link>
                <Link to="/user/review">Đánh giá sản phẩm</Link><Link to="/user/reward">Điểm thưởng</Link>
                <Link to="/user/preorder">Đặt trước sản phẩm</Link><Link to="/user/chat">Yêu cầu tư vấn</Link>
                <button type="button" onClick={() => { resetAuthState(); NotifyUtils.simpleSuccess('Đăng xuất thành công'); headerRef.current?.querySelectorAll('details').forEach((item) => item.removeAttribute('open')); }}>Đăng xuất</button>
              </> : <><Link to="/signin">Đăng nhập</Link><Link to="/signup">Tạo tài khoản</Link></>}
            </div>
          </details>
          <Link className="nest-icon nest-cart-link" to="/cart" aria-label={`Giỏ hàng, ${user ? currentTotalCartItems : 0} sản phẩm`}>
            <ShoppingCart size={21} strokeWidth={1.4}/><span>{user ? currentTotalCartItems : 0}</span>
          </Link>
          <details className="nest-dropdown nest-mobile-menu">
            <summary className="nest-icon" aria-label="Mở menu"><Menu2 size={22}/></summary>
            <nav className="nest-dropdown-panel" aria-label="Điều hướng di động">
              <Link to="/search">Tất cả sản phẩm</Link><Link to="/#spaces">Theo không gian</Link>
              <Link to="/#collections">Bộ sưu tập</Link><Link to="/#journal">Cảm hứng</Link>
              <hr/>{categoryLinks || <Link to="/all-categories">Danh mục sản phẩm</Link>}
              <hr/><Link to="/contact">Liên hệ tư vấn</Link>
            </nav>
          </details>
        </div>
      </div>
    </header>
  );
}

function useNotificationEvents() {
  const { user } = useAuthStore();

  const eventSourceRef = useRef<EventSource | null>(null);

  const { pushNewNotification } = useClientSiteStore();

  useQuery<EventInitiationResponse, ErrorMessage>(
    ['client-api', 'notifications/init-events', 'initNotificationEvents'],
    () => FetchUtils.getWithToken(ResourceURL.CLIENT_NOTIFICATION_INIT_EVENTS),
    {
      onSuccess: (response) => {
        const eventSource = new EventSource(`${ResourceURL.CLIENT_NOTIFICATION_EVENTS}?eventSourceUuid=${response.eventSourceUuid}`);

        eventSource.onopen = () => MiscUtils.console.log('Opening EventSource of Notifications...');

        eventSource.onerror = () => MiscUtils.console.error('Encountered error with Notifications EventSource!');

        eventSource.onmessage = (event) => {
          const notificationResponse = JSON.parse(event.data) as NotificationResponse;
          pushNewNotification(notificationResponse);
        };

        eventSourceRef.current = eventSource;
      },
      onError: () => NotifyUtils.simpleFailed('Lấy dữ liệu không thành công'),
      refetchOnWindowFocus: false,
      keepPreviousData: true,
      enabled: !!user,
    }
  );

  useEffect(() => () => eventSourceRef.current?.close(), []);
}

export default React.memo(ClientHeader);
