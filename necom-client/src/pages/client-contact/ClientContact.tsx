import React, { useState } from 'react';
import {
  Anchor,
  Breadcrumbs,
  Button,
  Card,
  Container,
  Grid,
  Group,
  Paper,
  Stack,
  Text,
  TextInput,
  Textarea,
  ThemeIcon,
  Title,
  useMantineTheme
} from '@mantine/core';
import { Link } from 'react-router-dom';
import useTitle from 'hooks/use-title';
import NotifyUtils from 'utils/NotifyUtils';
import {
  BuildingStore,
  Clock,
  Mail,
  MapPin,
  Phone,
  Send
} from 'tabler-icons-react';

function ClientContact() {
  useTitle('Liên hệ mua hàng & Showroom Hà Đông | Necom');
  const theme = useMantineTheme();

  const [fullName, setFullName] = useState('');
  const [phone, setPhone] = useState('');
  const [email, setEmail] = useState('');
  const [message, setMessage] = useState('');

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!fullName.trim() || !phone.trim() || !message.trim()) {
      NotifyUtils.simpleFailed('Vui lòng điền họ tên, số điện thoại và nội dung tin nhắn');
      return;
    }
    NotifyUtils.simpleSuccess('Cảm ơn bạn! Necom đã ghi nhận tin nhắn và sẽ phản hồi trong ít phút.');
    setFullName('');
    setPhone('');
    setEmail('');
    setMessage('');
  };

  return (
    <main>
      <Container size="xl" py="xl">
        <Stack spacing={theme.spacing.xl * 1.5}>
          <Card radius="md" shadow="sm" p="lg">
            <Stack spacing="xs">
              <Breadcrumbs>
                <Anchor component={Link} to="/">Trang chủ</Anchor>
                <Text color="dimmed">Giới thiệu</Text>
                <Text color="dimmed">Liên hệ mua hàng</Text>
              </Breadcrumbs>
              <Title order={2}>Liên hệ mua hàng & Trải nghiệm Showroom</Title>
            </Stack>
          </Card>

          <Grid gutter="xl">
            {/* Cột thông tin liên hệ & Showroom */}
            <Grid.Col md={5}>
              <Stack spacing="md">
                <Paper radius="md" p="xl" withBorder>
                  <Title order={3} mb="md">Showroom Necom Hà Đông</Title>
                  <Stack spacing="lg">
                    <Group align="flex-start">
                      <ThemeIcon color="green" variant="light" size="lg" radius="md">
                        <MapPin size={22} />
                      </ThemeIcon>
                      <div>
                        <Text weight={600} size="sm">Địa chỉ Showroom</Text>
                        <Text size="sm" color="dimmed" sx={{ lineHeight: 1.5 }}>
                          Số 68 Đường Quang Trung, Phường Vạn Phúc, Quận Hà Đông, Hà Nội
                        </Text>
                      </div>
                    </Group>

                    <Group align="flex-start">
                      <ThemeIcon color="blue" variant="light" size="lg" radius="md">
                        <Phone size={22} />
                      </ThemeIcon>
                      <div>
                        <Text weight={600} size="sm">Tổng đài hỗ trợ & Đặt hàng</Text>
                        <Text size="sm" color="green" weight={700}>
                          1900 6868
                        </Text>
                        <Text size="xs" color="dimmed">
                          Kỹ thuật & bảo hành: (024) 7300 8899
                        </Text>
                      </div>
                    </Group>

                    <Group align="flex-start">
                      <ThemeIcon color="teal" variant="light" size="lg" radius="md">
                        <Mail size={22} />
                      </ThemeIcon>
                      <div>
                        <Text weight={600} size="sm">Email liên hệ</Text>
                        <Text size="sm" color="dimmed">
                          contact@necom.vnhat.dev
                        </Text>
                      </div>
                    </Group>

                    <Group align="flex-start">
                      <ThemeIcon color="orange" variant="light" size="lg" radius="md">
                        <Clock size={22} />
                      </ThemeIcon>
                      <div>
                        <Text weight={600} size="sm">Giờ mở cửa đón khách</Text>
                        <Text size="sm" color="dimmed">
                          08:30 - 21:30 (Thứ Hai đến Chủ Nhật)
                        </Text>
                        <Text size="xs" color="dimmed">
                          (Có bãi đỗ xe ô tô và xe máy thuận tiện)
                        </Text>
                      </div>
                    </Group>
                  </Stack>
                </Paper>

                {/* Bản đồ định vị */}
                <Paper
                  radius="md"
                  p="lg"
                  withBorder
                  sx={{
                    backgroundColor: theme.colorScheme === 'dark' ? theme.colors.dark[6] : '#f3f6f3',
                  }}
                >
                  <Group mb="xs">
                    <BuildingStore size={20} color={theme.colors.green[6]} />
                    <Text weight={600} size="sm">Vị trí trung tâm Hà Đông</Text>
                  </Group>
                  <Text size="xs" color="dimmed" mb="sm">
                    Gần ngã tư Vạn Phúc - Quang Trung, cách ga tàu điện Cát Linh - Hà Đông (ga La Khê) chỉ 500m.
                  </Text>
                  <Button
                    component="a"
                    href="https://maps.google.com/?q=68+Quang+Trung+Ha+Dong+Ha+Noi"
                    target="_blank"
                    rel="noopener noreferrer"
                    variant="light"
                    color="green"
                    fullWidth
                    size="xs"
                    radius="md"
                    leftIcon={<MapPin size={14} />}
                  >
                    Xem chỉ đường trên Google Maps
                  </Button>
                </Paper>
              </Stack>
            </Grid.Col>

            {/* Cột Form gửi yêu cầu tư vấn */}
            <Grid.Col md={7}>
              <Paper radius="md" p="xl" withBorder sx={{ height: '100%' }}>
                <Title order={3} mb="xs">Gửi tin nhắn tư vấn không gian</Title>
                <Text color="dimmed" size="sm" mb="lg">
                  Bạn cần tư vấn chọn kích thước bàn ghế, chọn mẫu đèn trang trí hay đặt thiết kế theo yêu cầu? Hãy để lại lời nhắn cho chúng tôi.
                </Text>

                <form onSubmit={handleSubmit}>
                  <Stack spacing="md">
                    <Grid>
                      <Grid.Col sm={6}>
                        <TextInput
                          required
                          label="Họ và tên của bạn"
                          placeholder="VD: Trần Hải Yến"
                          value={fullName}
                          onChange={(e) => setFullName(e.currentTarget.value)}
                        />
                      </Grid.Col>
                      <Grid.Col sm={6}>
                        <TextInput
                          required
                          label="Số điện thoại"
                          placeholder="VD: 0912 345 678"
                          value={phone}
                          onChange={(e) => setPhone(e.currentTarget.value)}
                        />
                      </Grid.Col>
                    </Grid>

                    <TextInput
                      label="Địa chỉ Email (tùy chọn)"
                      placeholder="VD: haiyen@gmail.com"
                      value={email}
                      onChange={(e) => setEmail(e.currentTarget.value)}
                    />

                    <Textarea
                      required
                      label="Nội dung cần hỗ trợ / Tư vấn sản phẩm"
                      placeholder="VD: Tôi muốn được tư vấn bộ sofa văng và bàn trà tròn đôi cho phòng khách chung cư diện tích 25m2..."
                      minRows={4}
                      value={message}
                      onChange={(e) => setMessage(e.currentTarget.value)}
                    />

                    <Button type="submit" color="green" radius="md" leftIcon={<Send size={18} />}>
                      Gửi yêu cầu hỗ trợ
                    </Button>
                  </Stack>
                </form>
              </Paper>
            </Grid.Col>
          </Grid>
        </Stack>
      </Container>
    </main>
  );
}

export default ClientContact;
