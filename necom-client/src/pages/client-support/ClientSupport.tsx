import React from 'react';
import {
  Accordion,
  Alert,
  Anchor,
  Breadcrumbs,
  Card,
  Container,
  Grid,
  Group,
  List,
  Paper,
  Stack,
  Text,
  ThemeIcon,
  Timeline,
  Title,
  UnstyledButton,
  useMantineTheme
} from '@mantine/core';
import { Link, useNavigate, useParams } from 'react-router-dom';
import useTitle from 'hooks/use-title';
import {
  AlertCircle,
  BuildingStore,
  Check,
  CreditCard,
  FileCheck,
  Headset,
  Help,
  Package,
  Phone,
  RotateClockwise,
  ShieldCheck,
  ShoppingCart,
  Truck
} from 'tabler-icons-react';

interface SupportNavSection {
  slug: string;
  title: string;
  icon: React.ReactNode;
}

const SUPPORT_SECTIONS: SupportNavSection[] = [
  { slug: 'faq', title: 'Câu hỏi thường gặp', icon: <Help size={18} /> },
  { slug: 'order-guide', title: 'Hướng dẫn đặt hàng', icon: <ShoppingCart size={18} /> },
  { slug: 'shipping', title: 'Phương thức vận chuyển', icon: <Truck size={18} /> },
  { slug: 'return-policy', title: 'Chính sách đổi trả', icon: <RotateClockwise size={18} /> },
  { slug: 'payment-policy', title: 'Chính sách thanh toán', icon: <CreditCard size={18} /> },
  { slug: 'complaint', title: 'Giải quyết khiếu nại', icon: <AlertCircle size={18} /> },
  { slug: 'privacy', title: 'Chính sách bảo mật', icon: <ShieldCheck size={18} /> },
];

function ClientSupport() {
  const theme = useMantineTheme();
  const { slug } = useParams<{ slug: string }>();
  const navigate = useNavigate();

  const currentSlug = slug && SUPPORT_SECTIONS.some((s) => s.slug === slug) ? slug : 'faq';
  const currentSection = SUPPORT_SECTIONS.find((s) => s.slug === currentSlug) || SUPPORT_SECTIONS[0];

  useTitle(`${currentSection.title} | Necom Hỗ Trợ`);

  const renderContent = () => {
    switch (currentSlug) {
    case 'faq':
      return (
        <Stack spacing="lg">
          <div>
            <Title order={3} mb="xs">Câu hỏi thường gặp (FAQ)</Title>
            <Text color="dimmed">
                Giải đáp nhanh các thắc mắc phổ biến nhất khi mua sắm sản phẩm nội thất & đời sống tại Necom.
            </Text>
          </div>

          <Accordion iconPosition="right" initialItem={0}>
            <Accordion.Item label="1. Làm thế nào để đặt hàng nội thất trên Necom?">
              <Text size="sm" color="dimmed" mb="xs">
                  Bạn có thể tìm kiếm sản phẩm mong muốn qua thanh tìm kiếm hoặc danh mục hàng (Bàn ghế, Đèn trang trí, Đồ bếp...). Sau khi chọn số lượng và màu sắc ưng ý, bấm &quot;Thêm vào giỏ hàng&quot; hoặc &quot;Mua ngay&quot; để điền địa chỉ giao hàng và hoàn tất thanh toán.
              </Text>
            </Accordion.Item>

            <Accordion.Item label="2. Necom hỗ trợ những hình thức thanh toán nào?">
              <Text size="sm" color="dimmed">
                  Necom cung cấp đa dạng phương thức thanh toán an toàn:
                <List size="sm" mt="xs" spacing="xs">
                  <List.Item>Thanh toán khi nhận hàng (COD) toàn quốc.</List.Item>
                  <List.Item>Chuyển khoản ngân hàng 24/7 qua mã VietQR.</List.Item>
                  <List.Item>Thẻ thanh toán quốc tế (Visa, Mastercard).</List.Item>
                  <List.Item>Cổng thanh toán quốc tế PayPal.</List.Item>
                </List>
              </Text>
            </Accordion.Item>

            <Accordion.Item label="3. Chính sách miễn phí vận chuyển áp dụng như thế nào?">
              <Text size="sm" color="dimmed">
                  Đơn hàng có tổng giá trị từ <strong>1.000.000đ trở lên</strong> được tự động áp dụng chính sách <strong>Miễn phí giao hàng tiêu chuẩn toàn quốc</strong>. Với các đơn hàng dưới 1 triệu, phí vận chuyển được tính minh bạch theo biểu phí của đối tác vận chuyển Giao Hàng Nhanh (GHN).
              </Text>
            </Accordion.Item>

            <Accordion.Item label="4. Thời gian nhận hàng thông thường là bao lâu?">
              <Text size="sm" color="dimmed">
                  - Nội thành Hà Nội: Nhận trong vòng 24 - 48 giờ.<br />
                  - Các tỉnh miền Bắc: Từ 2 - 3 ngày làm việc.<br />
                  - Miền Trung và miền Nam (TP.HCM): Từ 3 - 4 ngày làm việc.
              </Text>
            </Accordion.Item>

            <Accordion.Item label="5. Tôi có được đồng kiểm tra hàng trước khi thanh toán không?">
              <Text size="sm" color="dimmed">
                  Hoàn toàn có! Bạn có quyền mở kiện hàng kiểm tra đúng mẫu mã, màu sắc, số lượng và nguyên vẹn về mặt hình thức trước khi ký nhận và thanh toán cho shipper.
              </Text>
            </Accordion.Item>

            <Accordion.Item label="6. Sản phẩm nội thất và đồ gia dụng được bảo hành ra sao?">
              <Text size="sm" color="dimmed">
                  Các sản phẩm bàn, ghế, sofa, kệ tủ được bảo hành kết cấu 12 - 24 tháng. Đèn chiếu sáng và thiết bị gia dụng được bảo hành kỹ thuật 12 tháng theo chính sách từng thương hiệu.
              </Text>
            </Accordion.Item>
          </Accordion>
        </Stack>
      );

    case 'order-guide':
      return (
        <Stack spacing="lg">
          <div>
            <Title order={3} mb="xs">Hướng dẫn đặt hàng 4 bước</Title>
            <Text color="dimmed">
                Mua sắm các món đồ tinh tế cho tổ ấm của bạn chỉ trong vài thao tác đơn giản.
            </Text>
          </div>

          <Timeline active={3} bulletSize={32} lineWidth={2}>
            <Timeline.Item bullet={<BuildingStore size={18} />} title="Bước 1: Tìm kiếm & Lựa chọn sản phẩm">
              <Text color="dimmed" size="sm" mt={4}>
                  Khám phá danh mục phong phú: Bàn ghế, Đèn trang trí, Kệ tủ, Đồ bếp, Decor... Kiểm tra chi tiết kích thước, chất liệu gỗ và giá bán phù hợp.
              </Text>
            </Timeline.Item>

            <Timeline.Item bullet={<ShoppingCart size={18} />} title="Bước 2: Thêm vào giỏ & Kiểm tra giỏ hàng">
              <Text color="dimmed" size="sm" mt={4}>
                  Bấm &quot;Thêm vào giỏ&quot;, tùy chỉnh số lượng hoặc áp dụng mã giảm giá ưu đãi nếu có. Đơn từ 1.000.000đ sẽ được miễn phí vận chuyển.
              </Text>
            </Timeline.Item>

            <Timeline.Item bullet={<CreditCard size={18} />} title="Bước 3: Điền địa chỉ & Chọn thanh toán">
              <Text color="dimmed" size="sm" mt={4}>
                  Nhập thông tin người nhận chính xác (Tỉnh/Thành, Quận/Huyện, Xã/Phường và Số nhà). Chọn hình thức thanh toán COD, Chuyển khoản QR hoặc Thẻ.
              </Text>
            </Timeline.Item>

            <Timeline.Item bullet={<Check size={18} />} title="Bước 4: Xác nhận & Nhận hàng đồng kiểm">
              <Text color="dimmed" size="sm" mt={4}>
                  Hệ thống gửi mã đơn hàng và cập nhật hành trình vận chuyển theo thời gian thực. Khách hàng nhận kiện hàng, đồng kiểm và hoàn tất.
              </Text>
            </Timeline.Item>
          </Timeline>

          <Alert icon={<Phone size={18} />} color="teal" radius="md">
              Cần hỗ trợ đặt hàng gấp? Gọi ngay Hotline <strong>1900 6868</strong> để được tư vấn viên hỗ trợ 24/7.
          </Alert>
        </Stack>
      );

    case 'shipping':
      return (
        <Stack spacing="lg">
          <div>
            <Title order={3} mb="xs">Phương thức vận chuyển & Giao nhận</Title>
            <Text color="dimmed">
                Necom hợp tác cùng các đơn vị vận tải chuyên nghiệp đảm bảo hàng hóa đến tay an toàn, nguyên vẹn.
            </Text>
          </div>

          <Alert icon={<Truck size={20} />} title="CHÍNH SÁCH FREESHIP TOÀN QUỐC" color="green" radius="md">
              Miễn phí giao hàng tiêu chuẩn cho tất cả đơn hàng có giá trị từ <strong>1.000.000đ</strong> trở lên trên toàn quốc.
          </Alert>

          <Grid>
            <Grid.Col sm={6}>
              <Paper p="md" radius="md" withBorder>
                <Group mb="xs">
                  <ThemeIcon color="blue" variant="light" size="lg" radius="md">
                    <Truck size={22} />
                  </ThemeIcon>
                  <Text weight={600}>Thời gian giao hàng</Text>
                </Group>
                <List size="sm" spacing="xs" color="dimmed">
                  <List.Item><strong>Hà Nội & lân cận:</strong> 1 - 2 ngày làm việc</List.Item>
                  <List.Item><strong>Miền Bắc & Miền Trung:</strong> 2 - 3 ngày làm việc</List.Item>
                  <List.Item><strong>TP. Hồ Chí Minh & Miền Nam:</strong> 3 - 4 ngày làm việc</List.Item>
                </List>
              </Paper>
            </Grid.Col>

            <Grid.Col sm={6}>
              <Paper p="md" radius="md" withBorder>
                <Group mb="xs">
                  <ThemeIcon color="teal" variant="light" size="lg" radius="md">
                    <Package size={22} />
                  </ThemeIcon>
                  <Text weight={600}>Tiêu chuẩn đóng gói</Text>
                </Group>
                <List size="sm" spacing="xs" color="dimmed">
                  <List.Item>Thùng carton 5 lớp chuyên dụng chống va đập</List.Item>
                  <List.Item>Gốm sứ & chao đèn được bọc màng khí chống sốc</List.Item>
                  <List.Item>Gỗ tự nhiên được bọc góc xốp định hình an toàn</List.Item>
                </List>
              </Paper>
            </Grid.Col>
          </Grid>
        </Stack>
      );

    case 'return-policy':
      return (
        <Stack spacing="lg">
          <div>
            <Title order={3} mb="xs">Chính sách đổi trả trong vòng 7 ngày</Title>
            <Text color="dimmed">
                An tâm mua sắm với cam kết hỗ trợ đổi trả linh hoạt và minh bạch từ Necom.
            </Text>
          </div>

          <Paper p="md" radius="md" withBorder>
            <Title order={4} mb="xs">1. Điều kiện áp dụng đổi trả</Title>
            <List size="sm" spacing="xs" color="dimmed">
              <List.Item>Sản phẩm bị nứt, vỡ, trầy xước hoặc móp méo do quá trình vận chuyển.</List.Item>
              <List.Item>Sản phẩm giao không đúng phân loại, màu sắc hoặc kích thước đã đặt.</List.Item>
              <List.Item>Sản phẩm lỗi kỹ thuật kết cấu từ phía nhà sản xuất.</List.Item>
              <List.Item>Sản phẩm chưa qua sử dụng, còn nguyên tem mác và bao bì đóng gói ban đầu.</List.Item>
            </List>
          </Paper>

          <Paper p="md" radius="md" withBorder>
            <Title order={4} mb="xs">2. Chi phí đổi trả</Title>
            <Text size="sm" color="dimmed">
                - Lỗi phát sinh từ nhà bán / vận chuyển: <strong>Necom chi trả 100%</strong> cước phí hoàn hàng.<br />
                - Khách hàng có nhu cầu đổi mẫu mã khác vì sở thích cá nhân: Khách hàng thanh toán phí chênh lệch và cước vận chuyển 2 chiều.
            </Text>
          </Paper>
        </Stack>
      );

    case 'payment-policy':
      return (
        <Stack spacing="lg">
          <div>
            <Title order={3} mb="xs">Chính sách thanh toán</Title>
            <Text color="dimmed">
                Necom cung cấp nhiều phương thức thanh toán thuận tiện, tích hợp mã hóa bảo mật SSL 256-bit.
            </Text>
          </div>

          <Grid>
            <Grid.Col sm={6}>
              <Paper p="md" radius="md" withBorder>
                <ThemeIcon color="green" variant="light" size="lg" radius="md" mb="xs">
                  <Truck size={22} />
                </ThemeIcon>
                <Text weight={600} mb={4}>Thanh toán khi nhận hàng (COD)</Text>
                <Text size="sm" color="dimmed">
                    Khách hàng thanh toán tiền mặt trực tiếp cho nhân viên giao hàng sau khi đã kiểm tra kiện hàng.
                </Text>
              </Paper>
            </Grid.Col>

            <Grid.Col sm={6}>
              <Paper p="md" radius="md" withBorder>
                <ThemeIcon color="blue" variant="light" size="lg" radius="md" mb="xs">
                  <CreditCard size={22} />
                </ThemeIcon>
                <Text weight={600} mb={4}>Chuyển khoản nhanh qua VietQR</Text>
                <Text size="sm" color="dimmed">
                    Quét mã QR tự động điền số tài khoản, số tiền và nội dung đơn hàng, xác nhận tức thì.
                </Text>
              </Paper>
            </Grid.Col>

            <Grid.Col sm={6}>
              <Paper p="md" radius="md" withBorder>
                <ThemeIcon color="pink" variant="light" size="lg" radius="md" mb="xs">
                  <FileCheck size={22} />
                </ThemeIcon>
                <Text weight={600} mb={4}>Thẻ Visa / Mastercard & Thẻ nội địa</Text>
                <Text size="sm" color="dimmed">
                    Hỗ trợ thanh toán qua cổng thẻ tín dụng an toàn và chuẩn bảo mật 3D Secure.
                </Text>
              </Paper>
            </Grid.Col>

            <Grid.Col sm={6}>
              <Paper p="md" radius="md" withBorder>
                <ThemeIcon color="cyan" variant="light" size="lg" radius="md" mb="xs">
                  <BuildingStore size={22} />
                </ThemeIcon>
                <Text weight={600} mb={4}>Cổng thanh toán PayPal</Text>
                <Text size="sm" color="dimmed">
                    Phù hợp cho khách hàng quốc tế hoặc giao dịch thanh toán trực tuyến qua tài khoản PayPal.
                </Text>
              </Paper>
            </Grid.Col>
          </Grid>
        </Stack>
      );

    case 'complaint':
      return (
        <Stack spacing="lg">
          <div>
            <Title order={3} mb="xs">Quy trình giải quyết khiếu nại</Title>
            <Text color="dimmed">
                Chúng tôi luôn lắng nghe và cam kết giải quyết mọi phản ánh thỏa đáng vì sự hài lòng của khách hàng.
            </Text>
          </div>

          <Timeline active={2} bulletSize={28} lineWidth={2}>
            <Timeline.Item title="Bước 1: Tiếp nhận phản ánh">
              <Text size="sm" color="dimmed">
                  Khách hàng gửi yêu cầu qua Hotline 1900 6868 hoặc email support@necom.vnhat.dev kèm hình ảnh/video sản phẩm cần phản ánh.
              </Text>
            </Timeline.Item>

            <Timeline.Item title="Bước 2: Xác minh & Thẩm định trong 24 giờ">
              <Text size="sm" color="dimmed">
                  Bộ phận CSKH phối hợp cùng kho và đối tác vận chuyển xác định nguyên nhân và đưa ra giải pháp xử lý.
              </Text>
            </Timeline.Item>

            <Timeline.Item title="Bước 3: Giải quyết thỏa đáng & Bồi thường">
              <Text size="sm" color="dimmed">
                  Thực hiện gửi bù sản phẩm mới, đổi hàng tận nơi hoặc hoàn tiền 100% tùy theo nguyện vọng của quý khách.
              </Text>
            </Timeline.Item>
          </Timeline>

          <Paper p="md" radius="md" sx={{ backgroundColor: theme.colorScheme === 'dark' ? theme.colors.dark[6] : '#f3f6f3' }}>
            <Group>
              <Headset size={36} color={theme.colors.green[6]} />
              <div>
                <Text weight={600}>Bộ phận Chăm sóc & Tiếp nhận khiếu nại</Text>
                <Text size="sm" color="dimmed">Hotline: 1900 6868 | Email: cskh@necom.vnhat.dev</Text>
              </div>
            </Group>
          </Paper>
        </Stack>
      );

    case 'privacy':
      return (
        <Stack spacing="lg">
          <div>
            <Title order={3} mb="xs">Chính sách bảo mật thông tin</Title>
            <Text color="dimmed">
                Necom tôn trọng và bảo vệ tuyệt đối quyền riêng tư cùng dữ liệu cá nhân của mọi khách hàng.
            </Text>
          </div>

          <Paper p="md" radius="md" withBorder>
            <Title order={4} mb="xs">1. Mục đích thu thập thông tin</Title>
            <Text size="sm" color="dimmed">
                Thông tin như họ tên, số điện thoại, địa chỉ nhận hàng và email chỉ được thu thập nhằm phục vụ việc xử lý đơn hàng, điều phối giao vận và gửi các thông báo trạng thái đơn hàng.
            </Text>
          </Paper>

          <Paper p="md" radius="md" withBorder>
            <Title order={4} mb="xs">2. Cam kết không chia sẻ dữ liệu</Title>
            <Text size="sm" color="dimmed">
                Necom cam kết không bán, không chuyển nhượng hoặc chia sẻ thông tin khách hàng cho bất kỳ bên thứ ba nào vì mục đích thương mại ngoài phạm vi phục vụ hoàn tất đơn hàng (như đơn vị vận chuyển GHN).
            </Text>
          </Paper>

          <Paper p="md" radius="md" withBorder>
            <Title order={4} mb="xs">3. Bảo mật giao dịch trực tuyến</Title>
            <Text size="sm" color="dimmed">
                Mọi thông tin thanh toán trực tuyến qua thẻ và chuyển khoản được mã hóa theo tiêu chuẩn an toàn bảo mật SSL/TLS tiên tiến nhất.
            </Text>
          </Paper>
        </Stack>
      );

    default:
      return null;
    }
  };

  return (
    <main>
      <Container size="xl" py="xl">
        <Stack spacing="xl">
          <Card radius="md" shadow="sm" p="lg">
            <Stack spacing="xs">
              <Breadcrumbs>
                <Anchor component={Link} to="/">Trang chủ</Anchor>
                <Anchor component={Link} to="/support/faq">Hỗ trợ khách hàng</Anchor>
                <Text color="dimmed">{currentSection.title}</Text>
              </Breadcrumbs>
              <Title order={2}>Trung tâm trợ giúp khách hàng</Title>
            </Stack>
          </Card>

          <Grid gutter="xl">
            {/* Sidebar điều hướng */}
            <Grid.Col md={3.5} lg={3}>
              <Paper radius="md" p="sm" withBorder sx={{ position: 'sticky', top: 20 }}>
                <Text weight={600} size="sm" color="dimmed" mb="sm" px="xs">
                  DANH MỤC HỖ TRỢ
                </Text>
                <Stack spacing={4}>
                  {SUPPORT_SECTIONS.map((sec) => {
                    const isActive = sec.slug === currentSlug;
                    return (
                      <UnstyledButton
                        key={sec.slug}
                        onClick={() => navigate(`/support/${sec.slug}`)}
                        sx={{
                          display: 'flex',
                          alignItems: 'center',
                          padding: '10px 14px',
                          borderRadius: theme.radius.md,
                          fontWeight: isActive ? 600 : 500,
                          fontSize: 14,
                          color: isActive
                            ? theme.colors.green[7]
                            : theme.colorScheme === 'dark' ? theme.colors.dark[1] : theme.colors.gray[7],
                          backgroundColor: isActive
                            ? theme.colorScheme === 'dark' ? theme.colors.dark[5] : '#edf5ed'
                            : 'transparent',
                          transition: 'all 0.2s ease',
                          '&:hover': {
                            backgroundColor: isActive
                              ? theme.colorScheme === 'dark' ? theme.colors.dark[5] : '#edf5ed'
                              : theme.colorScheme === 'dark' ? theme.colors.dark[6] : theme.colors.gray[1],
                          },
                        }}
                      >
                        <ThemeIcon
                          size="md"
                          radius="md"
                          variant={isActive ? 'filled' : 'light'}
                          color={isActive ? 'green' : 'gray'}
                          mr="sm"
                        >
                          {sec.icon}
                        </ThemeIcon>
                        {sec.title}
                      </UnstyledButton>
                    );
                  })}
                </Stack>
              </Paper>
            </Grid.Col>

            {/* Nội dung chi tiết */}
            <Grid.Col md={8.5} lg={9}>
              <Paper radius="md" p="xl" withBorder>
                {renderContent()}
              </Paper>
            </Grid.Col>
          </Grid>
        </Stack>
      </Container>
    </main>
  );
}

export default ClientSupport;
