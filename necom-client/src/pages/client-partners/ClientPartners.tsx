import React, { useState } from 'react';
import {
  Anchor,
  Breadcrumbs,
  Button,
  Card,
  Container,
  Grid,
  Paper,
  Select,
  SimpleGrid,
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
  Certificate,
  HeartHandshake,
  Network,
  TruckDelivery
} from 'tabler-icons-react';

function ClientPartners() {
  useTitle('Hợp tác kinh doanh & Đối tác | Necom');
  const theme = useMantineTheme();

  const [partnerName, setPartnerName] = useState('');
  const [contactPerson, setContactPerson] = useState('');
  const [phone, setPhone] = useState('');
  const [category, setCategory] = useState<string | null>('furniture');
  const [note, setNote] = useState('');

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!partnerName.trim() || !contactPerson.trim() || !phone.trim()) {
      NotifyUtils.simpleFailed('Vui lòng điền đầy đủ các thông tin bắt buộc');
      return;
    }
    NotifyUtils.simpleSuccess('Đã gửi thông tin hợp tác! Đội ngũ đối tác Necom sẽ liên hệ trong 24h.');
    setPartnerName('');
    setContactPerson('');
    setPhone('');
    setNote('');
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
                <Text color="dimmed">Hợp tác</Text>
              </Breadcrumbs>
              <Title order={2}>Hợp tác phát triển cùng Necom</Title>
            </Stack>
          </Card>

          {/* Banner */}
          <Paper
            radius="lg"
            p="xl"
            sx={{
              background: 'linear-gradient(135deg, #047857 0%, #059669 50%, #10b981 100%)',
              color: '#ffffff',
            }}
          >
            <Stack spacing="sm" sx={{ maxWidth: 700 }}>
              <Title order={1} sx={{ fontSize: '2.2rem' }}>
                Đồng hành kiến tạo tổ ấm Việt
              </Title>
              <Text size="md" sx={{ opacity: 0.95, lineHeight: 1.6 }}>
                Necom chào đón sự hợp tác cùng các xưởng sản xuất mộc, thương hiệu đồ gia dụng, gốm sứ và các văn phòng kiến trúc sư / studio thiết kế nội thất để mang tới những sản phẩm chất lượng cao nhất cho người tiêu dùng.
              </Text>
            </Stack>
          </Paper>

          {/* Quyền lợi đối tác */}
          <div>
            <Title order={3} mb="md">Lợi thế khi trở thành đối tác Necom</Title>
            <SimpleGrid cols={3} breakpoints={[{ maxWidth: 'sm', cols: 1 }]}>
              <Paper radius="md" p="md" withBorder>
                <ThemeIcon color="green" variant="light" size={40} radius="md" mb="xs">
                  <Network size={22} />
                </ThemeIcon>
                <Text weight={600} mb={4}>Tiếp cận khách hàng tiềm năng</Text>
                <Text size="sm" color="dimmed">
                  Sản phẩm của bạn được trưng bày trên nền tảng e-commerce Necom và Showroom tại Hà Đông với lưu lượng khách hàng ổn định.
                </Text>
              </Paper>

              <Paper radius="md" p="md" withBorder>
                <ThemeIcon color="blue" variant="light" size={40} radius="md" mb="xs">
                  <TruckDelivery size={22} />
                </ThemeIcon>
                <Text weight={600} mb={4}>Hệ thống vận hành chuyên nghiệp</Text>
                <Text size="sm" color="dimmed">
                  Hỗ trợ quy trình kiểm thử, đóng gói chuẩn chống va đập và mạng lưới giao vận toàn quốc qua đối tác GHN.
                </Text>
              </Paper>

              <Paper radius="md" p="md" withBorder>
                <ThemeIcon color="teal" variant="light" size={40} radius="md" mb="xs">
                  <Certificate size={22} />
                </ThemeIcon>
                <Text weight={600} mb={4}>Thanh toán minh bạch, đúng hạn</Text>
                <Text size="sm" color="dimmed">
                  Quy trình đối soát tự động hàng tuần, thanh toán sòng phẳng và hỗ trợ phát triển sản phẩm theo xu hướng thị trường.
                </Text>
              </Paper>
            </SimpleGrid>
          </div>

          {/* Form đăng ký hợp tác */}
          <Paper radius="md" p="xl" withBorder>
            <Grid gutter="xl">
              <Grid.Col md={5}>
                <Stack spacing="md">
                  <Title order={3}>Đăng ký thông tin đối tác</Title>
                  <Text color="dimmed" size="sm" sx={{ lineHeight: 1.6 }}>
                    Nếu quý doanh nghiệp, xưởng sản xuất hoặc studio có mong muốn hợp tác cung ứng sản phẩm, vui lòng điền thông tin vào biểu mẫu. Bộ phận phát triển nguồn hàng của Necom sẽ liên hệ trong 24 giờ làm việc.
                  </Text>
                  <Paper p="md" radius="md" sx={{ backgroundColor: theme.colorScheme === 'dark' ? theme.colors.dark[6] : '#f3f6f3' }}>
                    <Text weight={600} size="sm">Phòng Hợp tác & Phát triển đối tác</Text>
                    <Text size="xs" color="dimmed">Hotline: 1900 6868 (Nhánh 2)</Text>
                    <Text size="xs" color="dimmed">Email: partners@necom.vnhat.dev</Text>
                    <Text size="xs" color="dimmed">Địa chỉ: Số 68 Quang Trung, P. Vạn Phúc, Q. Hà Đông, Hà Nội</Text>
                  </Paper>
                </Stack>
              </Grid.Col>

              <Grid.Col md={7}>
                <form onSubmit={handleSubmit}>
                  <Stack spacing="md">
                    <TextInput
                      required
                      label="Tên doanh nghiệp / Xưởng / Thương hiệu"
                      placeholder="VD: Xưởng Gỗ Mộc An Nhiên"
                      value={partnerName}
                      onChange={(e) => setPartnerName(e.currentTarget.value)}
                    />
                    <Grid>
                      <Grid.Col sm={6}>
                        <TextInput
                          required
                          label="Người liên hệ"
                          placeholder="VD: Nguyễn Văn A"
                          value={contactPerson}
                          onChange={(e) => setContactPerson(e.currentTarget.value)}
                        />
                      </Grid.Col>
                      <Grid.Col sm={6}>
                        <TextInput
                          required
                          label="Số điện thoại liên hệ"
                          placeholder="VD: 0988 123 456"
                          value={phone}
                          onChange={(e) => setPhone(e.currentTarget.value)}
                        />
                      </Grid.Col>
                    </Grid>
                    <Select
                      label="Ngành hàng hợp tác"
                      value={category}
                      onChange={setCategory}
                      data={[
                        { value: 'furniture', label: 'Bàn ghế, Sofa & Kệ tủ gỗ' },
                        { value: 'lighting', label: 'Đèn trang trí & Chiếu sáng' },
                        { value: 'kitchen', label: 'Đồ bếp & Đồ dùng bàn ăn gốm sứ' },
                        { value: 'decor', label: 'Đồ decor, gối nỉ & bình hoa' },
                        { value: 'design', label: 'Thiết kế nội thất & Kiến trúc' },
                      ]}
                    />
                    <Textarea
                      label="Mô tả tóm tắt về năng lực sản xuất / Sản phẩm"
                      placeholder="VD: Quy mô xưởng 1500m2, chuyên gia công gỗ sồi tự nhiên đạt chuẩn xuất khẩu..."
                      minRows={3}
                      value={note}
                      onChange={(e) => setNote(e.currentTarget.value)}
                    />
                    <Button type="submit" color="green" radius="md" leftIcon={<HeartHandshake size={18} />}>
                      Gửi thông tin hợp tác
                    </Button>
                  </Stack>
                </form>
              </Grid.Col>
            </Grid>
          </Paper>
        </Stack>
      </Container>
    </main>
  );
}

export default ClientPartners;
