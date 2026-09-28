import React, { useState } from 'react';
import { Button, Card, Group, Stack, Text, TextInput, useMantineTheme } from '@mantine/core';
import { At, Mailbox } from 'tabler-icons-react';
import NotifyUtils from 'utils/NotifyUtils';

function ClientHomeNewsletter() {
  const theme = useMantineTheme();
  const [email, setEmail] = useState('');

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!email || !email.includes('@')) {
      NotifyUtils.simpleFailed('Vui lòng nhập địa chỉ email hợp lệ');
      return;
    }
    NotifyUtils.simpleSuccess('Cảm ơn bạn đã đăng ký nhận bản tin từ NECOM!');
    setEmail('');
  };

  return (
    <Card
      radius="lg"
      p="xl"
      sx={{
        backgroundColor: theme.colorScheme === 'dark' ? theme.colors.dark[7] : '#064e3b',
        color: theme.white,
        boxShadow: '0 4px 20px rgba(6, 78, 59, 0.15)',
      }}
    >
      <Group position="apart" spacing="xl">
        <Group spacing="lg">
          <Mailbox size={42} strokeWidth={1.5} color="#34d399" />
          <Stack spacing={2}>
            <Text weight={700} sx={{ fontSize: 20, color: theme.white }}>
              Đăng ký nhận ưu đãi & cảm hứng tổ ấm
            </Text>
            <Text size="sm" sx={{ color: 'rgba(255, 255, 255, 0.8)' }}>
              Nhận ngay voucher 100.000₫ cho đơn hàng đầu tiên và tin tức thiết kế mới nhất.
            </Text>
          </Stack>
        </Group>

        <form onSubmit={handleSubmit} style={{ minWidth: 360 }}>
          <Group spacing="xs">
            <TextInput
              sx={{ flexGrow: 1 }}
              styles={{
                input: {
                  backgroundColor: 'rgba(255, 255, 255, 0.15)',
                  border: '1px solid rgba(255, 255, 255, 0.25)',
                  color: theme.white,
                  '&:focus': {
                    borderColor: '#34d399',
                  },
                  '&::placeholder': {
                    color: 'rgba(255, 255, 255, 0.6)',
                  },
                },
              }}
              placeholder="Nhập địa chỉ email của bạn..."
              radius="md"
              size="md"
              icon={<At size={16} color="rgba(255,255,255,0.7)" />}
              value={email}
              onChange={(e) => setEmail(e.currentTarget.value)}
            />
            <Button
              type="submit"
              radius="md"
              size="md"
              sx={{
                backgroundColor: '#059669',
                fontWeight: 600,
                '&:hover': {
                  backgroundColor: '#047857',
                },
              }}
            >
              Đăng ký
            </Button>
          </Group>
        </form>
      </Group>
    </Card>
  );
}

export default ClientHomeNewsletter;
