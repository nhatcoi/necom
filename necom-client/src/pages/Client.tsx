import React from 'react';
import { Link, Outlet } from 'react-router-dom';
import { ActionIcon, Affix, Anchor, Button, Card, Group, MantineProvider, useMantineColorScheme } from '@mantine/core';
import { ClientFooter, ClientHeader, LoadingMiddleware } from 'components';
import { MoonStars, Sun } from 'tabler-icons-react';
import { useDisclosure, useHotkeys } from '@mantine/hooks';
import { useIsFetching, useQuery } from 'react-query';
import FetchUtils, { ErrorMessage } from 'utils/FetchUtils';
import ResourceURL from 'constants/ResourceURL';
import { UserResponse } from 'models/User';
import useAuthStore from 'stores/use-auth-store';
import ChatProvider from 'components/ChatWidget/ChatProvider';
import ChatLauncher from 'components/ChatWidget/ChatLauncher';
import 'components/Nest/nest.css';
import { nestStyles, nestTheme } from 'components/Nest/nest-theme';

function Client() {
  const isLoading = useIsFetching();
  useSyncUserProfile();

  return (
    <MantineProvider theme={nestTheme} styles={nestStyles}>
      <div className="nest-storefront">
        <ChatProvider>
          <LoadingMiddleware isLoading={!!isLoading}>
            <ClientHeader/>
            <div id="main-content" tabIndex={-1}><Outlet/></div>
            <ClientFooter/>
          </LoadingMiddleware>
          <ChatLauncher/>
        </ChatProvider>
        <Shortcut/>
      </div>
    </MantineProvider>
  );
}

/**
 * Hồ sơ người dùng lưu trong localStorage từ lúc đăng nhập nên có thể cũ (đổi tên, địa chỉ từ thiết bị khác
 * hoặc từ trang quản trị). Tải lại một lần mỗi khi mở storefront.
 */
function useSyncUserProfile() {
  const { user, updateUser } = useAuthStore();
  useQuery<UserResponse, ErrorMessage>(
    ['client-api', 'users', 'getUserInfo', user?.id],
    () => FetchUtils.getWithToken(ResourceURL.CLIENT_USER_INFO),
    {
      enabled: !!user,
      refetchOnWindowFocus: false,
      onSuccess: (userResponse) => updateUser(userResponse),
    }
  );
}

// Only for test
function Shortcut() {
  const { colorScheme, toggleColorScheme } = useMantineColorScheme();
  const dark = colorScheme === 'dark';

  const [opened, handlers] = useDisclosure(false);

  useHotkeys([
    ['alt+Q', () => handlers.toggle()],
  ]);

  const { resetAuthState } = useAuthStore();

  return (
    <Affix position={{ bottom: 20, right: 100 }} sx={{ display: opened ? 'block' : 'none' }}>
      <Card shadow="sm" p="sm">
        <Group>
          <Anchor component={Link} to="/">Client</Anchor>
          <Anchor component={Link} to="/admin">Admin</Anchor>
          <Button color="teal" variant="light" compact onClick={resetAuthState}>
            ResetAuthState
          </Button>
          <ActionIcon
            size="sm"
            variant="outline"
            color={dark ? 'yellow' : 'blue'}
            onClick={() => toggleColorScheme()}
            title="Thay đổi chế độ màu"
          >
            {dark ? <Sun size={14}/> : <MoonStars size={14}/>}
          </ActionIcon>
        </Group>
      </Card>
    </Affix>
  );
}

export default Client;
