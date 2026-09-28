import React from 'react';
import { Center, Navbar, ScrollArea, Stack, Tooltip, UnstyledButton, useMantineTheme } from '@mantine/core';
import {
  AddressBook,
  Award,
  Box,
  Building,
  BuildingWarehouse,
  Car,
  ChevronsLeft,
  ChevronsRight,
  CurrencyDollar,
  FileBarcode,
  Fingerprint,
  Home,
  Icon,
  Message,
  Users
} from 'tabler-icons-react';
import { Link, useLocation } from 'react-router-dom';
import useAppStore from 'stores/use-app-store';
import useDefaultNavbarStyles from 'components/DefaultNavbar/DefaultNavbar.styles';
import useAdminAuthStore from 'stores/use-admin-auth-store';

interface NavbarLink {
  link: string;
  label: string;
  icon: Icon;
  childLinks?: NavbarChildLink[];
  disableForEmployee?: boolean;
}

interface NavbarChildLink {
  link: string;
  label: string;
}

const navbarLinks: NavbarLink[] = [
  {
    link: '/admin',
    label: 'Trang chủ',
    icon: Home,
  },
  {
    link: '/admin/address',
    label: 'Địa chỉ',
    icon: AddressBook,
    childLinks: [
      {
        link: '/admin/address/province',
        label: 'Tỉnh thành',
      },
      {
        link: '/admin/address/ward',
        label: 'Phường xã',
      },
    ],
    disableForEmployee: true,
  },
  {
    link: '/admin/user',
    label: 'Người dùng',
    icon: Fingerprint,
    childLinks: [
      {
        link: '/admin/user/role',
        label: 'Quyền',
      },
    ],
    disableForEmployee: true,
  },
  {
    link: '/admin/employee',
    label: 'Nhân viên',
    icon: Building,
    childLinks: [
      {
        link: '/admin/employee/office',
        label: 'Văn phòng',
      },
      {
        link: '/admin/employee/department',
        label: 'Phòng ban',
      },
      {
        link: '/admin/employee/job-type',
        label: 'Loại hình công việc',
      },
      {
        link: '/admin/employee/job-level',
        label: 'Cấp bậc công việc',
      },
      {
        link: '/admin/employee/job-title',
        label: 'Chức danh công việc',
      },
    ],
    disableForEmployee: true,
  },
  {
    link: '/admin/customer',
    label: 'Khách hàng',
    icon: Users,
    childLinks: [
      {
        link: '/admin/customer/group',
        label: 'Nhóm khách hàng',
      },
      {
        link: '/admin/customer/status',
        label: 'Trạng thái khách hàng',
      },
      {
        link: '/admin/customer/resource',
        label: 'Nguồn khách hàng',
      },
    ],
    disableForEmployee: true,
  },
  {
    link: '/admin/product',
    label: 'Sản phẩm',
    icon: Box,
    childLinks: [
      {
        link: '/admin/category',
        label: 'Danh mục sản phẩm',
      },
      {
        link: '/admin/product/brand',
        label: 'Nhãn hiệu',
      },
      {
        link: '/admin/product/supplier',
        label: 'Nhà cung cấp',
      },
      {
        link: '/admin/product/unit',
        label: 'Đơn vị tính',
      },
      {
        link: '/admin/product/tag',
        label: 'Tag',
      },
      {
        link: '/admin/product/guarantee',
        label: 'Bảo hành',
      },
      {
        link: '/admin/product/property',
        label: 'Thuộc tính sản phẩm',
      },
      {
        link: '/admin/product/specification',
        label: 'Thông số sản phẩm',
      },
    ],
    disableForEmployee: true,
  },
  {
    link: '/admin/inventory',
    label: 'Tồn kho',
    icon: BuildingWarehouse,
    childLinks: [
      {
        link: '/admin/inventory/warehouse',
        label: 'Nhà kho',
      },
      {
        link: '/admin/inventory/purchase-order',
        label: 'Đơn mua hàng',
      },
      {
        link: '/admin/inventory/destination',
        label: 'Điểm nhập hàng',
      },
      {
        link: '/admin/inventory/docket',
        label: 'Phiếu nhập xuất kho',
      },
      {
        link: '/admin/inventory/docket-reason',
        label: 'Lý do phiếu NXK',
      },
      {
        link: '/admin/inventory/count',
        label: 'Phiếu kiểm kho',
      },
      {
        link: '/admin/inventory/transfer',
        label: 'Phiếu chuyển kho',
      },
    ],
  },
  {
    link: '/admin/order',
    label: 'Đơn hàng',
    icon: FileBarcode,
    childLinks: [
      {
        link: '/admin/order/resource',
        label: 'Nguồn đơn hàng',
      },
      {
        link: '/admin/order/cancellation-reason',
        label: 'Lý do hủy đơn hàng',
      },
    ],
  },
  {
    link: '/admin/waybill',
    label: 'Vận đơn',
    icon: Car,
    childLinks: [],
  },
  {
    link: '/admin/review',
    label: 'Đánh giá',
    icon: Message,
    childLinks: [],
  },
  {
    link: '/admin/reward-strategy',
    label: 'Điểm thưởng',
    icon: Award,
    childLinks: [],
    disableForEmployee: true,
  },
  {
    link: '/admin/voucher',
    label: 'Sổ quỹ',
    icon: CurrencyDollar,
    childLinks: [
      {
        link: '/admin/payment-method',
        label: 'Hình thức thanh toán',
      },
      {
        link: '/admin/promotion',
        label: 'Khuyến mãi',
      },
    ],
    disableForEmployee: true,
  },
];

// Mục đang active suy ra từ URL (khớp tiền tố dài nhất trong link cha và link con)
function findActiveLabel(pathname: string) {
  let best = { label: 'Trang chủ', length: 0 };
  navbarLinks.forEach(navbarLink => {
    [navbarLink.link, ...(navbarLink.childLinks || []).map(child => child.link)].forEach(link => {
      const matched = link === '/admin'
        ? pathname === '/admin' || pathname === '/admin/'
        : pathname === link || pathname.startsWith(link + '/');
      if (matched && link.length > best.length) {
        best = { label: navbarLink.label, length: link.length };
      }
    });
  });
  return best.label;
}

export function DefaultNavbar() {
  const theme = useMantineTheme();
  const { opened, navbarCollapsed, toggleNavbarCollapsed } = useAppStore();
  const { classes, cx } = useDefaultNavbarStyles();
  const location = useLocation();
  const active = findActiveLabel(location.pathname);

  const { isOnlyEmployee } = useAdminAuthStore();

  // Menu mobile (burger) luôn hiển thị đầy đủ, chỉ thu gọn trên desktop
  const collapsed = navbarCollapsed && !opened;

  const navbarLinksFragment = navbarLinks.map(navbarLink => (
    <Stack
      key={navbarLink.label}
      spacing={0}
      sx={{ borderRadius: theme.radius.sm, overflow: 'hidden' }}
    >
      <Tooltip label={navbarLink.label} position="right" withArrow disabled={!collapsed}>
        <Link
          to={navbarLink.link}
          aria-label={navbarLink.label}
          className={cx(classes.link, {
            [classes.linkActive]: navbarLink.label === active,
            [classes.linkDisabled]: isOnlyEmployee() && navbarLink.disableForEmployee,
            [classes.linkCollapsed]: collapsed,
          })}
        >
          <navbarLink.icon className={classes.linkIcon}/>
          {!collapsed && <span>{navbarLink.label}</span>}
        </Link>
      </Tooltip>
      {!collapsed && navbarLink.label === active && (navbarLink.childLinks || []).map(childLink => (
        <Link
          key={childLink.label}
          to={childLink.link}
          className={cx(classes.link, { [classes.childLinkActive]: navbarLink.label === active })}
        >
          <Center sx={{ width: 24, marginRight: theme.spacing.sm }}>
            <div className={classes.childLinkDot}/>
          </Center>
          <span>{childLink.label}</span>
        </Link>
      ))}
    </Stack>
  ));

  return (
    <Navbar
      p={collapsed ? 'xs' : 'md'}
      width={{ md: collapsed ? 76 : 250 }}
      hidden={!opened}
      hiddenBreakpoint="md"
      sx={{ transition: 'width 150ms ease' }}
    >
      <Navbar.Section grow component={ScrollArea}>
        {navbarLinksFragment}
      </Navbar.Section>
      <Navbar.Section
        pt="xs"
        sx={theme => ({
          borderTop: `1px solid ${theme.colorScheme === 'dark' ? theme.colors.dark[4] : theme.colors.gray[2]}`,
          [theme.fn.smallerThan('md')]: { display: 'none' },
        })}
      >
        <Tooltip label={collapsed ? 'Mở rộng menu' : 'Thu gọn menu'} position="right" withArrow sx={{ width: '100%' }}>
          <UnstyledButton
            onClick={toggleNavbarCollapsed}
            className={cx(classes.link, { [classes.linkCollapsed]: collapsed })}
            sx={{ width: '100%', borderRadius: theme.radius.sm }}
            aria-label={collapsed ? 'Mở rộng menu' : 'Thu gọn menu'}
          >
            {collapsed
              ? <ChevronsRight className={classes.linkIcon}/>
              : <ChevronsLeft className={classes.linkIcon}/>}
            {!collapsed && <span>Thu gọn</span>}
          </UnstyledButton>
        </Tooltip>
      </Navbar.Section>
    </Navbar>
  );
}
