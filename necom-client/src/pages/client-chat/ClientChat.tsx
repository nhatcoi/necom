import React, { useEffect } from 'react';
import { Card, Container, Grid, Stack, Text, Title } from '@mantine/core';
import { ClientUserNavbar } from 'components';
import useTitle from 'hooks/use-title';
import ChatWindow from 'components/ChatWidget/ChatWindow';
import { useChat } from 'components/ChatWidget/ChatProvider';

/**
 * Trang "Yêu cầu tư vấn": cùng ChatWindow với cửa sổ nổi nhưng ở chế độ page,
 * dùng chung state nên hội thoại liền mạch khi chuyển qua lại.
 */
function ClientChat() {
  useTitle();

  const chat = useChat();
  const { setPageVisible, close } = chat;

  useEffect(() => {
    // Đang xem trang tư vấn: đóng cửa sổ nổi và tính là đã đọc tin mới
    close();
    setPageVisible(true);
    return () => setPageVisible(false);
  }, [setPageVisible, close]);

  return (
    <main>
      <Container size="xl">
        <Grid gutter="lg">
          <Grid.Col md={3}>
            <ClientUserNavbar/>
          </Grid.Col>

          <Grid.Col md={9}>
            <Card radius="md" shadow="sm" p="lg">
              <Stack>
                <Stack spacing={2}>
                  <Title order={2}>Yêu cầu tư vấn</Title>
                  <Text size="sm" color="dimmed">
                    Trò chuyện với trợ lý AI hoặc tư vấn viên Necom. Lịch sử hội thoại được lưu lại tại đây.
                  </Text>
                </Stack>
                <Card
                  p={0}
                  radius="md"
                  withBorder
                  sx={{ height: 'min(640px, calc(100vh - 220px))', minHeight: 480 }}
                >
                  <ChatWindow mode="page"/>
                </Card>
              </Stack>
            </Card>
          </Grid.Col>
        </Grid>
      </Container>
    </main>
  );
}

export default ClientChat;
