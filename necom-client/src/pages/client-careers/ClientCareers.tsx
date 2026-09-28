import React from 'react';
import {
  Anchor,
  Badge,
  Breadcrumbs,
  Button,
  Card,
  Container,
  Group,
  Paper,
  SimpleGrid,
  Stack,
  Text,
  ThemeIcon,
  Title,
  useMantineTheme
} from '@mantine/core';
import { Link } from 'react-router-dom';
import useTitle from 'hooks/use-title';
import {
  BuildingArch,
  Coin,
  Mail,
  MapPin,
  Users
} from 'tabler-icons-react';

interface JobPosition {
  title: string;
  department: string;
  location: string;
  type: string;
  salary: string;
  description: string;
}

const OPEN_POSITIONS: JobPosition[] = [
  {
    title: 'Chuyên viên tư vấn phong cách & Thiết kế nội thất',
    department: 'Kinh doanh & Showroom',
    location: 'Hà Đông, Hà Nội',
    type: 'Toàn thời gian',
    salary: '12 - 18 triệu + Thưởng KPI',
    description: 'Tư vấn trực tiếp cho khách hàng tại Showroom Hà Đông về phong cách bài trí, lựa chọn bàn ghế, sofa và phụ kiện decor phù hợp với không gian căn hộ.',
  },
  {
    title: 'Quản lý vận hành kho & Logistics Home & Living',
    department: 'Kho vận',
    location: 'Hà Đông, Hà Nội',
    type: 'Toàn thời gian',
    salary: '14 - 20 triệu',
    description: 'Điều phối xuất nhập kho hàng nội thất, quản lý chất lượng đóng gói chống sốc và kết nối đơn vị vận chuyển GHN giao hàng toàn quốc.',
  },
  {
    title: 'Kỹ sư Frontend (React / TypeScript)',
    department: 'Công nghệ thông tin',
    location: 'Hà Nội / Hybrid',
    type: 'Toàn thời gian',
    salary: '20 - 35 triệu',
    description: 'Phát triển và tối ưu trải nghiệm người dùng trên nền tảng thương mại điện tử Necom (Web/Mobile), tích hợp hệ thống thanh toán và đơn hàng.',
  },
  {
    title: 'Chuyên viên Sáng tạo nội dung & Visual Merchandising',
    department: 'Marketing',
    location: 'Hà Đông, Hà Nội',
    type: 'Toàn thời gian',
    salary: '10 - 15 triệu',
    description: 'Lên ý tưởng concept chụp ảnh sản phẩm studio, sản xuất video ngắn TikTok/Instagram chia sẻ mẹo trang trí nhà cửa và phong cách sống tối giản.',
  },
];

function ClientCareers() {
  useTitle('Tuyển dụng nhân tài | Necom — Nest Commerce');
  const theme = useMantineTheme();

  return (
    <main>
      <Container size="xl" py="xl">
        <Stack spacing={theme.spacing.xl * 1.5}>
          <Card radius="md" shadow="sm" p="lg">
            <Stack spacing="xs">
              <Breadcrumbs>
                <Anchor component={Link} to="/">Trang chủ</Anchor>
                <Text color="dimmed">Giới thiệu</Text>
                <Text color="dimmed">Tuyển dụng</Text>
              </Breadcrumbs>
              <Title order={2}>Cơ hội nghề nghiệp tại Necom</Title>
            </Stack>
          </Card>

          {/* Banner */}
          <Paper
            radius="lg"
            p="xl"
            sx={{
              background: 'linear-gradient(135deg, #10b981 0%, #059669 100%)',
              color: '#ffffff',
            }}
          >
            <Stack spacing="sm" sx={{ maxWidth: 700 }}>
              <Title order={1} sx={{ fontSize: '2.2rem' }}>
                Cùng Necom kiến tạo tổ ấm truyền cảm hứng
              </Title>
              <Text size="md" sx={{ opacity: 0.95, lineHeight: 1.6 }}>
                Tại Necom, chúng tôi tìm kiếm những cộng sự đam mê cái đẹp, trân trọng không gian sống và khao khát xây dựng những trải nghiệm thương mại điện tử đẳng cấp.
              </Text>
            </Stack>
          </Paper>

          {/* Phúc lợi */}
          <div>
            <Title order={3} mb="md">Chế độ & Phúc lợi dành cho bạn</Title>
            <SimpleGrid cols={3} breakpoints={[{ maxWidth: 'sm', cols: 1 }]}>
              <Paper radius="md" p="md" withBorder>
                <ThemeIcon color="green" variant="light" size={40} radius="md" mb="xs">
                  <Coin size={22} />
                </ThemeIcon>
                <Text weight={600} mb={4}>Thu nhập & Thưởng hấp dẫn</Text>
                <Text size="sm" color="dimmed">
                  Lương cạnh tranh theo năng lực, thưởng tháng 13, thưởng hiệu quả kinh doanh và đánh giá tăng lương định kỳ 6 tháng.
                </Text>
              </Paper>

              <Paper radius="md" p="md" withBorder>
                <ThemeIcon color="blue" variant="light" size={40} radius="md" mb="xs">
                  <Users size={22} />
                </ThemeIcon>
                <Text weight={600} mb={4}>Môi trường sáng tạo</Text>
                <Text size="sm" color="dimmed">
                  Không gian làm việc mở, trẻ trung, tự do đề xuất ý tưởng mới và lộ trình thăng tiến rõ ràng lên vị trí quản lý.
                </Text>
              </Paper>

              <Paper radius="md" p="md" withBorder>
                <ThemeIcon color="teal" variant="light" size={40} radius="md" mb="xs">
                  <BuildingArch size={22} />
                </ThemeIcon>
                <Text weight={600} mb={4}>Ưu đãi nhân viên</Text>
                <Text size="sm" color="dimmed">
                  Chính sách chiết khấu mua sắm đồ nội thất, decor Necom dành riêng cho nhân viên để bạn tự do làm đẹp căn nhà của mình.
                </Text>
              </Paper>
            </SimpleGrid>
          </div>

          {/* Danh sách vị trí tuyển dụng */}
          <div>
            <Title order={3} mb="md">Các vị trí đang mở tuyển</Title>
            <Stack spacing="md">
              {OPEN_POSITIONS.map((job, idx) => (
                <Paper key={idx} radius="md" p="lg" withBorder>
                  <Group position="apart" mb="xs">
                    <div>
                      <Title order={4} mb={4}>{job.title}</Title>
                      <Group spacing="xs">
                        <Badge color="green" variant="light">{job.department}</Badge>
                        <Badge color="gray" variant="outline">
                          <Group spacing={4}>
                            <MapPin size={12} />
                            <span>{job.location}</span>
                          </Group>
                        </Badge>
                        <Badge color="blue" variant="outline">{job.type}</Badge>
                      </Group>
                    </div>
                    <Text weight={700} color="green" size="md">
                      {job.salary}
                    </Text>
                  </Group>

                  <Text size="sm" color="dimmed" mb="md" sx={{ lineHeight: 1.6 }}>
                    {job.description}
                  </Text>

                  <Group position="apart">
                    <Text size="xs" color="dimmed">
                      Hạn nộp hồ sơ: <strong>Tuyển liên tục</strong>
                    </Text>
                    <Button
                      component="a"
                      href={`mailto:tuyendung@necom.vnhat.dev?subject=Ung tuyen ${encodeURIComponent(job.title)}`}
                      color="green"
                      radius="md"
                      size="xs"
                      leftIcon={<Mail size={14} />}
                    >
                      Ứng tuyển ngay
                    </Button>
                  </Group>
                </Paper>
              ))}
            </Stack>
          </div>
        </Stack>
      </Container>
    </main>
  );
}

export default ClientCareers;
