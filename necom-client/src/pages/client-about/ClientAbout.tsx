import React from 'react';
import {
  Anchor,
  Breadcrumbs,
  Card,
  Container,
  Grid,
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
import { Award, Heart, Leaf, ShieldCheck, Star, Sun } from 'tabler-icons-react';

function ClientAbout() {
  useTitle('Về Necom — Nest Commerce | Everything for your space');
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
                <Text color="dimmed">Về Công ty</Text>
              </Breadcrumbs>
              <Title order={2}>Về Necom — Nest Commerce</Title>
            </Stack>
          </Card>

          {/* Hero Banner */}
          <Paper
            radius="lg"
            p="xl"
            sx={{
              background: 'linear-gradient(135deg, #059669 0%, #10b981 50%, #047857 100%)',
              color: '#ffffff',
            }}
          >
            <Stack spacing="md" sx={{ maxWidth: 720 }}>
              <Group spacing="xs">
                <Star size={24} />
                <Text weight={700} size="sm" sx={{ letterSpacing: '0.1em', textTransform: 'uppercase' }}>
                  Câu chuyện thương hiệu
                </Text>
              </Group>
              <Title order={1} sx={{ fontSize: '2.5rem', lineHeight: 1.2 }}>
                Everything for your space.<br />Mọi thứ cho không gian sống.
              </Title>
              <Text size="lg" sx={{ opacity: 0.9, lineHeight: 1.6 }}>
                Chữ <strong>N</strong> trong Necom tượng trưng cho <strong>Nest — Tổ ấm</strong>. Chúng tôi tin rằng nhà không chỉ là nơi để trở về, mà còn là không gian nuôi dưỡng tâm hồn, mang lại sự an yên và nguồn cảm hứng sáng tạo mỗi ngày.
              </Text>
            </Stack>
          </Paper>

          {/* Sứ mệnh & Tầm nhìn */}
          <Grid gutter="xl">
            <Grid.Col md={6}>
              <Paper radius="md" p="xl" withBorder sx={{ height: '100%' }}>
                <ThemeIcon color="green" size={48} radius="md" mb="md">
                  <Sun size={28} />
                </ThemeIcon>
                <Title order={3} mb="sm">Sứ mệnh của chúng tôi</Title>
                <Text color="dimmed" sx={{ lineHeight: 1.6 }}>
                  Đồng hành cùng hàng triệu gia đình Việt Nam kiến tạo không gian sống tiện nghi, ấm cúng và mang đậm dấu ấn cá nhân. Bằng cách kết hợp giữa chất liệu mộc mạc và công năng hiện đại, Necom mang đến các sản phẩm nội thất & đời sống tinh tế với mức giá dễ tiếp cận.
                </Text>
              </Paper>
            </Grid.Col>

            <Grid.Col md={6}>
              <Paper radius="md" p="xl" withBorder sx={{ height: '100%' }}>
                <ThemeIcon color="teal" size={48} radius="md" mb="md">
                  <Star size={28} />
                </ThemeIcon>
                <Title order={3} mb="sm">Tầm nhìn phát triển</Title>
                <Text color="dimmed" sx={{ lineHeight: 1.6 }}>
                  Trở thành nền tảng thương mại hàng đầu về Home & Living tại Việt Nam, kết nối các nghệ nhân, xưởng sản xuất địa phương uy tín với những khách hàng yêu chuộng lối sống tối giản, gần gũi thiên nhiên phong cách Japandi và Scandinavian.
                </Text>
              </Paper>
            </Grid.Col>
          </Grid>

          {/* 4 Giá trị cốt lõi */}
          <div>
            <Title order={3} mb="md" align="center">4 Giá trị cốt lõi tại Necom</Title>
            <Text color="dimmed" align="center" mb="xl">
              Kim chỉ nam định hình mọi sản phẩm và trải nghiệm mà chúng tôi mang đến cho khách hàng.
            </Text>

            <SimpleGrid cols={4} breakpoints={[{ maxWidth: 'md', cols: 2 }, { maxWidth: 'xs', cols: 1 }]}>
              <Paper radius="md" p="lg" withBorder>
                <ThemeIcon color="green" variant="light" size={40} radius="md" mb="sm">
                  <Leaf size={22} />
                </ThemeIcon>
                <Text weight={600} mb="xs">Tối giản & Bền vững</Text>
                <Text size="sm" color="dimmed">
                  Ưu tiên vật liệu gỗ tự nhiên, gốm sứ mộc và bao bì thân thiện môi trường, hạn chế tối đa rác thải nhựa.
                </Text>
              </Paper>

              <Paper radius="md" p="lg" withBorder>
                <ThemeIcon color="blue" variant="light" size={40} radius="md" mb="sm">
                  <Award size={22} />
                </ThemeIcon>
                <Text weight={600} mb="xs">Chất lượng tuyển chọn</Text>
                <Text size="sm" color="dimmed">
                  Mỗi chiếc ghế, ngọn đèn hay chiếc cốc đều trải qua quy trình kiểm định kết cấu và độ an toàn kỹ lưỡng.
                </Text>
              </Paper>

              <Paper radius="md" p="lg" withBorder>
                <ThemeIcon color="pink" variant="light" size={40} radius="md" mb="sm">
                  <Heart size={22} />
                </ThemeIcon>
                <Text weight={600} mb="xs">Tận tâm phục vụ</Text>
                <Text size="sm" color="dimmed">
                  Chăm sóc chu đáo từ khâu tư vấn thiết kế không gian đến giao nhận đồng kiểm tận nhà và bảo hành dài hạn.
                </Text>
              </Paper>

              <Paper radius="md" p="lg" withBorder>
                <ThemeIcon color="teal" variant="light" size={40} radius="md" mb="sm">
                  <ShieldCheck size={22} />
                </ThemeIcon>
                <Text weight={600} mb="xs">Minh bạch & Uy tín</Text>
                <Text size="sm" color="dimmed">
                  Chính sách giá bán minh bạch, freeship đơn trên 1 triệu và bảo hành rõ ràng, lấy sự hài lòng làm thước đo.
                </Text>
              </Paper>
            </SimpleGrid>
          </div>
        </Stack>
      </Container>
    </main>
  );
}

export default ClientAbout;
