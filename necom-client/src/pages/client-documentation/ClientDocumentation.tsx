import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import {
  Anchor,
  Badge,
  Box,
  Code,
  Container,
  Grid,
  Group,
  List,
  Paper,
  SimpleGrid,
  Stack,
  Table,
  Text,
  Title,
  UnstyledButton,
  useMantineTheme
} from '@mantine/core';
import { Check, Minus } from 'tabler-icons-react';
import useTitle from 'hooks/use-title';
import { useChatPalette } from 'components/ChatWidget/chat-palette';
import {
  API_GROUPS,
  DEMO_ACCOUNTS,
  DOC_SECTIONS,
  DOMAIN_MODULES,
  ENV_VARS,
  MESSAGE_TYPES,
  OPERATION_COMMANDS,
  ORDER_STATUSES,
  PAYMENT_STATUSES,
  PERMISSIONS,
  REWARD_STRATEGIES,
  ROOM_STATUSES,
  SEED_FACTS,
  STOMP_CHANNELS,
  TECH_STACK,
  WAYBILL_STATUSES
} from 'pages/client-documentation/doc-data';
import {
  ActorsDiagram,
  AdminUseCaseDiagram,
  ArchitectureDiagram,
  ChatbotPipelineDiagram,
  ChatErdDiagram,
  ChatStateDiagram,
  CheckoutSequenceDiagram,
  ClientUseCaseDiagram,
  DeploymentDiagram,
  OrderLifecycleDiagram
} from 'pages/client-documentation/DocDiagrams';

/**
 * Trang /documentation: tài liệu dự án Necom (kiến trúc, nghiệp vụ, dữ liệu, API, vận hành, tài khoản demo).
 */
function ClientDocumentation() {
  useTitle('Tài liệu dự án');

  const palette = useChatPalette();
  const activeId = useActiveSection(DOC_SECTIONS.map(section => section.id));

  return (
    <Box sx={{ minHeight: '100vh', backgroundColor: palette.cream, color: palette.text }}>
      <DocsTopBar/>
      <main>
        <Container size="xl" pb="xl">
          <Hero/>

          <Grid gutter="xl" mt="lg">
            <Grid.Col md={3} sx={theme => ({ [theme.fn.smallerThan('md')]: { display: 'none' } })}>
              <Box sx={{ position: 'sticky', top: 84 }}>
                <Text size="xs" weight={700} mb="xs" sx={{ color: palette.wood, letterSpacing: 0.6 }}>MỤC LỤC</Text>
                <Stack spacing={2}>
                  {DOC_SECTIONS.map(section => (
                    <UnstyledButton
                      key={section.id}
                      component="a"
                      href={'#' + section.id}
                      sx={theme => ({
                        padding: '6px 12px',
                        borderRadius: theme.radius.sm,
                        fontSize: theme.fontSizes.sm,
                        borderLeft: `2px solid ${activeId === section.id ? palette.forest : 'transparent'}`,
                        color: activeId === section.id ? palette.forest : palette.text,
                        fontWeight: activeId === section.id ? 600 : 400,
                        backgroundColor: activeId === section.id ? palette.forestSoft : 'transparent',
                        '&:hover': { backgroundColor: palette.sand },
                      })}
                    >
                      {section.label}
                    </UnstyledButton>
                  ))}
                </Stack>
              </Box>
            </Grid.Col>

            <Grid.Col md={9}>
              <Stack spacing={56}>
                <OverviewSection/>
                <AccountsSection/>
                <Section id="architecture" title="Kiến trúc hệ thống" lead="Mô hình 3 tầng: SPA React giao tiếp với Spring Boot qua REST (JSON), STOMP và SSE; dữ liệu lưu ở MySQL; tích hợp các dịch vụ vận chuyển, thanh toán, AI và email.">
                  <ArchitectureDiagram/>
                  <SimpleGrid cols={2} breakpoints={[{ maxWidth: 'sm', cols: 1 }]} mt="md">
                    <Callout title="Backend phân lớp">
                      Controller nhận request và kiểm tra quyền, Service chứa nghiệp vụ, Repository (Spring Data JPA) truy cập dữ liệu,
                      MapStruct chuyển Entity ↔ DTO. CRUD quản trị dùng controller generic <Code>/api/{'{resource}'}</Code> với lọc RSQL.
                    </Callout>
                    <Callout title="Realtime">
                      Chat dùng STOMP qua SockJS tại <Code>/ws</Code>, xác thực JWT ở frame CONNECT. Thông báo đơn hàng, đặt trước, đánh giá
                      đẩy qua Server-Sent Events.
                    </Callout>
                  </SimpleGrid>
                </Section>

                <Section id="deployment" title="Triển khai" lead="Bốn container Docker trên một VPS (web, backend, CSDL, embedding), phía trước là Nginx host và Cloudflare.">
                  <DeploymentDiagram/>
                </Section>

                <Section id="actors" title="Tác nhân" lead="Khách hàng chia thành vãng lai và đã đăng ký; người quản trị kế thừa toàn bộ quyền của nhân viên.">
                  <ActorsDiagram/>
                  <PermissionTable/>
                </Section>

                <Section id="usecases" title="Use case" lead="Chức năng theo từng nhóm người dùng.">
                  <ClientUseCaseDiagram/>
                  <Box mt="md"><AdminUseCaseDiagram/></Box>
                </Section>

                <Section id="order-flow" title="Đặt hàng & vận đơn" lead="Đơn hàng đi qua 5 trạng thái; vận đơn Giao Hàng Nhanh cập nhật trạng thái đơn tự động.">
                  <OrderLifecycleDiagram/>
                  <SimpleGrid cols={2} breakpoints={[{ maxWidth: 'sm', cols: 1 }]} mt="md">
                    <Callout title="Tồn kho có thể bán">
                      Số lượng bán được tính từ phiếu nhập/xuất kho (Docket) đã hoàn tất trừ đi hàng đang chờ giao. Sản phẩm hết hàng
                      chỉ cho phép đặt trước.
                    </Callout>
                    <Callout title="Phí vận chuyển">
                      Đơn từ 1.000.000đ được miễn phí giao hàng tiêu chuẩn. Đơn nhỏ hơn tính phí theo biểu phí GHN; thuế mặc định 10%.
                    </Callout>
                  </SimpleGrid>
                </Section>

                <Section id="checkout" title="Luồng thanh toán" lead="COD ghi nhận thanh toán khi giao thành công; PayPal được capture ngay sau khi khách phê duyệt.">
                  <CheckoutSequenceDiagram/>
                </Section>

                <ChatSection/>

                <Section id="reward" title="Điểm thưởng" lead="Điểm được cộng tự động theo chiến lược cấu hình trong trang quản trị (công thức SpEL).">
                  <DocTable
                    head={['Mã', 'Sự kiện', 'Công thức', 'Ví dụ']}
                    rows={REWARD_STRATEGIES.map(r => [<Code key="c">{r.code}</Code>, r.label, r.formula, r.example])}
                  />
                </Section>

                <DomainSection/>
                <StatusSection/>
                <ApiSection/>
                <OperationsSection/>
              </Stack>
            </Grid.Col>
          </Grid>
        </Container>
      </main>
    </Box>
  );
}

/** Thanh điều hướng riêng của trang tài liệu (thay cho header/footer storefront). */
function DocsTopBar() {
  const palette = useChatPalette();
  return (
    <Box
      component="header"
      sx={{
        position: 'sticky',
        top: 0,
        zIndex: 20,
        backgroundColor: palette.cream,
        borderBottom: `1px solid ${palette.sandBorder}`,
      }}
    >
      <Container size="xl">
        <Group position="apart" sx={{ height: 60 }}>
          <Anchor component={Link} to="/documentation" underline={false}>
            <Group spacing={10}>
              <Text weight={700} sx={{ fontSize: 22, color: palette.forest, letterSpacing: -0.5 }}>necom.</Text>
              <Text size="sm" sx={{ color: palette.wood }}>Tài liệu dự án</Text>
            </Group>
          </Anchor>
          <Group spacing="lg">
            <Anchor component={Link} to="/" size="sm" sx={{ color: palette.text }}>Cửa hàng</Anchor>
            <Anchor component={Link} to="/admin" size="sm" sx={{ color: palette.text }}>Trang quản trị</Anchor>
          </Group>
        </Group>
      </Container>
    </Box>
  );
}

// ========== Các mục ==========

function Hero() {
  const palette = useChatPalette();
  return (
    <Paper
      radius="lg"
      p="xl"
      mt="md"
      sx={{ backgroundColor: palette.forest, color: '#FFFFFF' }}
    >
      <Text size="xs" weight={700} sx={{ letterSpacing: 1, opacity: 0.8 }}>NECOM · NEST COMMERCE</Text>
      <Title order={1} mt={6} sx={{ color: '#FFFFFF' }}>Tài liệu dự án</Title>
      <Text mt="xs" sx={{ maxWidth: 720, opacity: 0.9 }}>
        Website thương mại điện tử nội thất & đời sống: storefront, trang quản trị, kho vận, thanh toán, điểm thưởng,
        chat chăm sóc khách hàng có trợ lý AI. Tài liệu gồm kiến trúc, nghiệp vụ, dữ liệu, API và cách vận hành.
      </Text>
      <Group mt="lg" spacing="sm">
        {[
          { label: 'Storefront', to: '/' },
          { label: 'Trang quản trị', to: '/admin' },
          { label: 'Trung tâm trợ giúp', to: '/support/faq' },
        ].map(link => (
          <Anchor
            key={link.label}
            component={Link}
            to={link.to}
            sx={{
              color: '#FFFFFF',
              border: '1px solid rgba(255,255,255,0.35)',
              borderRadius: 999,
              padding: '6px 14px',
              fontSize: 14,
              '&:hover': { backgroundColor: 'rgba(255,255,255,0.1)', textDecoration: 'none' },
            }}
          >
            {link.label}
          </Anchor>
        ))}
        <Anchor
          href="/swagger-ui/index.html"
          target="_blank"
          rel="noopener noreferrer"
          sx={{
            color: '#FFFFFF',
            border: '1px solid rgba(255,255,255,0.35)',
            borderRadius: 999,
            padding: '6px 14px',
            fontSize: 14,
            '&:hover': { backgroundColor: 'rgba(255,255,255,0.1)', textDecoration: 'none' },
          }}
        >
          Swagger API
        </Anchor>
      </Group>
    </Paper>
  );
}

function OverviewSection() {
  const palette = useChatPalette();
  return (
    <Section id="overview" title="Tổng quan" lead="Công nghệ sử dụng và dữ liệu mẫu có sẵn.">
      <SimpleGrid cols={4} breakpoints={[{ maxWidth: 'md', cols: 2 }]} mb="md">
        {SEED_FACTS.map(fact => (
          <Paper key={fact.label} withBorder radius="md" p="md" sx={{ borderColor: palette.sandBorder, backgroundColor: palette.cream }}>
            <Text size="xs" color="dimmed">{fact.label}</Text>
            <Text sx={{ fontSize: 28, fontWeight: 700, color: palette.forest, lineHeight: 1.2 }}>{fact.value}</Text>
            <Text size="xs" color="dimmed">{fact.hint}</Text>
          </Paper>
        ))}
      </SimpleGrid>
      <DocTable
        head={['Tầng', 'Công nghệ']}
        rows={TECH_STACK.map(row => [
          <Text key="l" weight={600} size="sm">{row.layer}</Text>,
          <Group key="i" spacing={6}>
            {row.items.map(item => <Tag key={item}>{item}</Tag>)}
          </Group>,
        ])}
      />
    </Section>
  );
}

function AccountsSection() {
  return (
    <Section
      id="accounts"
      title="Tài khoản demo"
      lead="Mọi tài khoản mẫu dùng chung mật khẩu admin123. Khách hàng đăng nhập tại /signin, nhân viên và quản trị tại /admin."
    >
      <DocTable
        head={['Tài khoản', 'Mật khẩu', 'Vai trò', 'Đăng nhập tại', 'Ghi chú']}
        rows={DEMO_ACCOUNTS.map(account => [
          <Code key="u">{account.username}</Code>,
          <Code key="p">{account.password}</Code>,
          <RoleBadge key="r" role={account.role}/>,
          <Anchor key="e" component={Link} to={account.entry} size="sm">{account.entry}</Anchor>,
          <Text key="n" size="sm" color="dimmed">{account.note}</Text>,
        ])}
      />
      <Text size="sm" color="dimmed" mt="sm">
        Thanh toán PayPal chạy ở chế độ Sandbox: dùng tài khoản buyer sandbox của PayPal Developer, không dùng thẻ thật.
      </Text>
    </Section>
  );
}

function PermissionTable() {
  const palette = useChatPalette();
  const cell = (allowed: boolean) => (
    allowed
      ? <Check size={18} color={palette.forest}/>
      : <Minus size={16} color={palette.sandBorder}/>
  );
  return (
    <Box mt="md">
      <DocTable
        head={['Chức năng', 'Vãng lai', 'Khách hàng', 'Nhân viên', 'Quản trị']}
        rows={PERMISSIONS.map(p => [p.feature, cell(p.guest), cell(p.customer), cell(p.employee), cell(p.admin)])}
        centerFrom={1}
      />
    </Box>
  );
}

function ChatSection() {
  return (
    <Section
      id="chat"
      title="Chat CSKH & trợ lý AI"
      lead="Khách chat qua nút nổi hoặc trang Yêu cầu tư vấn. Trợ lý AI (agent có tool) trả lời trước; khách có thể gặp tư vấn viên bất cứ lúc nào, nhân viên có gợi ý trả lời bằng AI."
    >
      <ChatStateDiagram/>
      <Box mt="md"><ChatbotPipelineDiagram/></Box>
      <SimpleGrid cols={3} breakpoints={[{ maxWidth: 'md', cols: 1 }]} mt="md">
        <Callout title="Agent gọi tool">
          Bot không nhồi dữ liệu vào prompt. LLM tự gọi tool: tìm sản phẩm, xem chi tiết và tồn kho, tra chính sách, đơn
          hàng, hành trình GHN, điểm thưởng, yêu thích, giỏ hàng. Tối đa 5 bước mỗi lượt, khách thấy trạng thái “đang tìm…”.
        </Callout>
        <Callout title="Tìm kiếm hybrid, sẵn sàng scale">
          Điểm từ khóa BM25 (bỏ dấu) + điểm ngữ nghĩa từ embedding multilingual-e5-small, gộp bằng Reciprocal Rank Fusion, lọc
          cứng theo danh mục, giá, còn hàng. Vector lưu trong MySQL, chỉ tính lại khi sản phẩm đổi; chỉ mục trong bộ nhớ
          phù hợp tới vài chục nghìn sản phẩm, lớn hơn thì thay bằng Qdrant.
        </Callout>
        <Callout title="An toàn dữ liệu">
          Người gửi lấy từ JWT; khách chỉ subscribe được phòng của mình; tool dữ liệu cá nhân luôn theo chủ phòng; thẻ sản
          phẩm/đơn hàng chỉ lấy từ kết quả tool; tin trùng bị chặn bằng clientMsgId; chuyển tư vấn viên khi khách yêu cầu
          hoặc khiếu nại.
        </Callout>
      </SimpleGrid>
      <Box mt="md"><ChatErdDiagram/></Box>
    </Section>
  );
}

function DomainSection() {
  const palette = useChatPalette();
  return (
    <Section id="domain" title="Mô hình dữ liệu" lead="Các entity chia theo module nghiệp vụ, mỗi module tương ứng một package trong backend.">
      <SimpleGrid cols={3} breakpoints={[{ maxWidth: 'md', cols: 2 }, { maxWidth: 'sm', cols: 1 }]}>
        {DOMAIN_MODULES.map(module => (
          <Paper
            key={module.key}
            withBorder
            radius="md"
            p="md"
            sx={{ borderColor: palette.sandBorder, backgroundColor: palette.cream }}
          >
            <Group position="apart" mb={6}>
              <Text weight={700} sx={{ color: palette.forest }}>{module.name}</Text>
              <Code>entity.{module.key === 'misc' ? 'general…' : module.key}</Code>
            </Group>
            <Group spacing={6}>
              {module.entities.map(entity => <Tag key={entity}>{entity}</Tag>)}
            </Group>
            <Text size="xs" color="dimmed" mt={8}>{module.note}</Text>
          </Paper>
        ))}
      </SimpleGrid>
      <Text size="sm" color="dimmed" mt="md">
        Mọi entity kế thừa BaseEntity (id, created_at, updated_at, created_by, updated_by). Thuộc tính động của sản phẩm
        (màu, kích thước, thông số) lưu dạng JSON. Schema nằm ở <Code>schema.sql</Code>, dữ liệu mẫu ở <Code>data.sql</Code> và
        <Code>home-living-seed.sql</Code>, thay đổi chat ở <Code>chat-migration.sql</Code>.
      </Text>
    </Section>
  );
}

function StatusSection() {
  return (
    <Section id="statuses" title="Bảng trạng thái" lead="Giá trị trạng thái lưu trong CSDL và ý nghĩa nghiệp vụ.">
      <Stack spacing="lg">
        <SubTitle>Đơn hàng · order.status</SubTitle>
        <DocTable head={['Mã', 'Trạng thái', 'Ý nghĩa']} rows={ORDER_STATUSES.map(s => [<Code key="c">{s.code}</Code>, s.label, s.note])}/>
        <SubTitle>Thanh toán · order.payment_status</SubTitle>
        <DocTable head={['Mã', 'Trạng thái', 'Ý nghĩa']} rows={PAYMENT_STATUSES.map(s => [<Code key="c">{s.code}</Code>, s.label, s.note])}/>
        <SubTitle>Vận đơn · waybill.status</SubTitle>
        <DocTable head={['Mã', 'Trạng thái', 'Mã GHN tương ứng']} rows={WAYBILL_STATUSES.map(s => [<Code key="c">{s.code}</Code>, s.label, <Text key="g" size="sm" color="dimmed">{s.ghn}</Text>])}/>
        <SubTitle>Phiên chat · room.status</SubTitle>
        <DocTable head={['Mã', 'Trạng thái', 'Khi nào']} rows={ROOM_STATUSES.map(s => [<Code key="c">{s.code}</Code>, s.label, s.note])}/>
        <SubTitle>Loại tin nhắn · message.type</SubTitle>
        <DocTable head={['Mã', 'Loại', 'Mô tả']} rows={MESSAGE_TYPES.map(s => [<Code key="c">{s.code}</Code>, s.label, s.note])}/>
      </Stack>
    </Section>
  );
}

function ApiSection() {
  return (
    <Section id="api" title="API & realtime" lead="REST trả JSON; danh sách hỗ trợ page, size, sort (vd. id,desc), filter theo cú pháp RSQL và search.">
      <DocTable
        head={['Đường dẫn', 'Quyền', 'Mô tả']}
        rows={API_GROUPS.map(g => [<Code key="p">{g.prefix}</Code>, <Text key="a" size="sm" weight={500}>{g.access}</Text>, g.note])}
      />
      <Text size="sm" mt="sm" color="dimmed">
        Ví dụ lọc: <Code>/api/orders?page=1&size=10&sort=id,desc&filter=status==1;paymentStatus==2</Code>. Xác thực bằng header
        <Code>Authorization: Bearer &lt;token&gt;</Code> lấy từ <Code>POST /api/auth/login</Code>.
      </Text>
      <SubTitle mt="lg">Kênh STOMP (chat)</SubTitle>
      <DocTable
        head={['Destination', 'Ai dùng', 'Ghi chú']}
        rows={STOMP_CHANNELS.map(ch => [<Code key="d">{ch.destination}</Code>, ch.who, ch.note])}
      />
    </Section>
  );
}

function OperationsSection() {
  const palette = useChatPalette();
  return (
    <Section id="operations" title="Vận hành" lead="Cấu hình qua biến môi trường trong file .env (không commit), deploy bằng script.">
      <DocTable head={['Biến môi trường', 'Mục đích']} rows={ENV_VARS.map(v => [<Code key="n">{v.name}</Code>, v.note])}/>
      <SubTitle mt="lg">Lệnh thường dùng</SubTitle>
      <Stack spacing="xs">
        {OPERATION_COMMANDS.map(cmd => (
          <Paper key={cmd.title} withBorder radius="md" p="sm" sx={{ borderColor: palette.sandBorder, backgroundColor: palette.cream }}>
            <Text size="sm" weight={600} mb={4}>{cmd.title}</Text>
            <Code block>{cmd.command}</Code>
          </Paper>
        ))}
      </Stack>
      <SubTitle mt="lg">Lưu ý khi thay đổi CSDL</SubTitle>
      <List size="sm" spacing={4}>
        <List.Item>Production chạy với <Code>SPRING_SQL_INIT_MODE=never</Code>: file migration phải chạy tay trên VPS trước khi deploy code mới.</List.Item>
        <List.Item>Luôn backup trước: dump lưu tại <Code>/var/www/necom/backups</Code> trên VPS.</List.Item>
        <List.Item>Rollback nhanh về Spring Boot 2: branch <Code>legacy/spring-boot-2</Code> và image <Code>necom-server:boot2-backup</Code>.</List.Item>
      </List>
    </Section>
  );
}

// ========== Thành phần dùng chung ==========

function Section({ id, title, lead, children }: { id: string, title: string, lead?: string, children: React.ReactNode }) {
  const palette = useChatPalette();
  return (
    <Box component="section" id={id} sx={{ scrollMarginTop: 90 }}>
      <Title order={2} sx={{ color: palette.text, fontSize: 26 }}>{title}</Title>
      {lead && <Text color="dimmed" mt={4} mb="md" sx={{ maxWidth: 760 }}>{lead}</Text>}
      {children}
    </Box>
  );
}

function SubTitle({ children, mt }: { children: React.ReactNode, mt?: string }) {
  const palette = useChatPalette();
  return <Text weight={700} mt={mt} mb="xs" sx={{ color: palette.forest }}>{children}</Text>;
}

function Callout({ title, children }: { title: string, children: React.ReactNode }) {
  const palette = useChatPalette();
  return (
    <Paper radius="md" p="md" sx={{ backgroundColor: palette.sand, borderLeft: `3px solid ${palette.wood}` }}>
      <Text weight={700} size="sm" mb={4} sx={{ color: palette.text }}>{title}</Text>
      <Text size="sm" color="dimmed">{children}</Text>
    </Paper>
  );
}

function Tag({ children }: { children: React.ReactNode }) {
  const palette = useChatPalette();
  return (
    <Box
      component="span"
      sx={{
        display: 'inline-block',
        fontSize: 12,
        padding: '2px 8px',
        borderRadius: 6,
        border: `1px solid ${palette.sandBorder}`,
        backgroundColor: palette.sand,
        color: palette.text,
      }}
    >
      {children}
    </Box>
  );
}

function RoleBadge({ role }: { role: string }) {
  const color = role === 'ADMIN' ? 'green' : role === 'EMPLOYEE' ? 'teal' : 'gray';
  return <Badge color={color} variant="light" size="sm">{role}</Badge>;
}

function DocTable({ head, rows, centerFrom }: { head: string[], rows: React.ReactNode[][], centerFrom?: number }) {
  const palette = useChatPalette();
  const theme = useMantineTheme();
  return (
    <Box sx={{ overflowX: 'auto', border: `1px solid ${palette.sandBorder}`, borderRadius: theme.radius.md }}>
      <Table
        verticalSpacing="sm"
        horizontalSpacing="md"
        fontSize="sm"
        sx={{
          minWidth: 560,
          '& thead tr th': {
            backgroundColor: palette.sand,
            color: palette.text,
            fontWeight: 700,
            borderBottom: `1px solid ${palette.sandBorder}`,
          },
          '& tbody tr td': { borderTop: `1px solid ${palette.sandBorder}`, verticalAlign: 'top' },
        }}
      >
        <thead>
          <tr>
            {head.map((h, i) => (
              <th key={h} style={centerFrom !== undefined && i >= centerFrom ? { textAlign: 'center' } : undefined}>{h}</th>
            ))}
          </tr>
        </thead>
        <tbody>
          {rows.map((row, r) => (
            <tr key={r}>
              {row.map((cell, i) => (
                <td key={i} style={centerFrom !== undefined && i >= centerFrom ? { textAlign: 'center' } : undefined}>
                  {centerFrom !== undefined && i >= centerFrom
                    ? <Box sx={{ display: 'flex', justifyContent: 'center' }}>{cell}</Box>
                    : cell}
                </td>
              ))}
            </tr>
          ))}
        </tbody>
      </Table>
    </Box>
  );
}

/**
 * Theo dõi mục đang hiển thị để highlight mục lục.
 */
function useActiveSection(ids: string[]) {
  const [activeId, setActiveId] = useState(ids[0]);

  useEffect(() => {
    const observer = new IntersectionObserver(
      entries => {
        const visible = entries
          .filter(entry => entry.isIntersecting)
          .sort((a, b) => a.boundingClientRect.top - b.boundingClientRect.top);
        if (visible[0]) {
          setActiveId(visible[0].target.id);
        }
      },
      { rootMargin: '-90px 0px -60% 0px' }
    );
    ids.forEach(id => {
      const element = document.getElementById(id);
      if (element) {
        observer.observe(element);
      }
    });
    return () => observer.disconnect();
  }, [ids.join(',')]); // eslint-disable-line react-hooks/exhaustive-deps

  return activeId;
}

export default ClientDocumentation;
