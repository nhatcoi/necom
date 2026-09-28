import React from 'react';
import { Box, useMantineTheme } from '@mantine/core';
import { getChatPalette } from 'components/ChatWidget/chat-palette';

/**
 * Sơ đồ tài liệu vẽ bằng SVG thuần, dùng bảng màu thương hiệu (xanh rừng, cát, kem, gỗ) và tự đổi theo dark mode.
 */

interface DiagramColors {
  forest: string;
  brand: string;
  brandSoft: string;
  sand: string;
  sandBorder: string;
  cream: string;
  wood: string;
  text: string;
  muted: string;
  line: string;
  surface: string;
}

function useDiagramColors(): DiagramColors {
  const theme = useMantineTheme();
  const palette = getChatPalette(theme);
  const dark = theme.colorScheme === 'dark';
  return {
    forest: palette.forest,
    brand: theme.colors[theme.primaryColor][dark ? 4 : 6],
    brandSoft: palette.forestSoft,
    sand: palette.sand,
    sandBorder: palette.sandBorder,
    cream: palette.cream,
    wood: palette.wood,
    text: palette.text,
    muted: dark ? theme.colors.dark[2] : '#6B6358',
    line: dark ? theme.colors.dark[3] : '#9C8F7C',
    surface: dark ? theme.colors.dark[7] : '#FFFFFF',
  };
}

function DiagramFrame({ viewBox, title, children, maxWidth }: {
  viewBox: string,
  title: string,
  children: React.ReactNode,
  maxWidth?: number,
}) {
  const c = useDiagramColors();
  return (
    <Box
      sx={{
        border: `1px solid ${c.sandBorder}`,
        borderRadius: 12,
        backgroundColor: c.surface,
        padding: 16,
        overflowX: 'auto',
      }}
    >
      <svg
        viewBox={viewBox}
        role="img"
        aria-label={title}
        style={{ width: '100%', maxWidth: maxWidth, minWidth: 640, display: 'block', margin: '0 auto', fontFamily: 'inherit' }}
      >
        <title>{title}</title>
        <defs>
          <marker id="doc-arrow" viewBox="0 0 10 10" refX="9" refY="5" markerWidth="7" markerHeight="7" orient="auto-start-reverse">
            <path d="M 0 0 L 10 5 L 0 10 z" fill={c.line}/>
          </marker>
          <marker id="doc-arrow-brand" viewBox="0 0 10 10" refX="9" refY="5" markerWidth="7" markerHeight="7" orient="auto-start-reverse">
            <path d="M 0 0 L 10 5 L 0 10 z" fill={c.forest}/>
          </marker>
          <marker id="doc-generalization" viewBox="0 0 12 12" refX="11" refY="6" markerWidth="12" markerHeight="12" orient="auto">
            <path d="M 0 0 L 12 6 L 0 12 z" fill={c.surface} stroke={c.text} strokeWidth="1"/>
          </marker>
        </defs>
        {children}
      </svg>
    </Box>
  );
}

// ========== Primitive ==========

function Node({ x, y, w, h, title, subtitle, variant = 'default', align = 'middle' }: {
  x: number, y: number, w: number, h: number,
  title: string,
  subtitle?: string | string[],
  variant?: 'default' | 'brand' | 'sand' | 'ghost',
  align?: 'middle' | 'start',
}) {
  const c = useDiagramColors();
  const fill = variant === 'brand' ? c.forest : variant === 'sand' ? c.sand : variant === 'ghost' ? 'none' : c.cream;
  const stroke = variant === 'brand' ? c.forest : c.sandBorder;
  const color = variant === 'brand' ? '#FFFFFF' : c.text;
  const subColor = variant === 'brand' ? 'rgba(255,255,255,0.8)' : c.muted;
  const lines = subtitle ? (Array.isArray(subtitle) ? subtitle : [subtitle]) : [];
  const tx = align === 'middle' ? x + w / 2 : x + 14;
  const firstY = y + h / 2 - (lines.length * 16) / 2 + 5;
  return (
    <g>
      <rect x={x} y={y} width={w} height={h} rx={10} fill={fill} stroke={stroke} strokeDasharray={variant === 'ghost' ? '5 4' : undefined}/>
      <text x={tx} y={firstY} textAnchor={align} fontSize={14} fontWeight={600} fill={color}>{title}</text>
      {lines.map((line, i) => (
        <text key={line} x={tx} y={firstY + 18 + i * 16} textAnchor={align} fontSize={12} fill={subColor}>{line}</text>
      ))}
    </g>
  );
}

function Arrow({ x1, y1, x2, y2, label, both, brand, dashed, labelDx = 0, labelDy = -6 }: {
  x1: number, y1: number, x2: number, y2: number,
  label?: string, both?: boolean, brand?: boolean, dashed?: boolean, labelDx?: number, labelDy?: number,
}) {
  const c = useDiagramColors();
  const marker = brand ? 'url(#doc-arrow-brand)' : 'url(#doc-arrow)';
  return (
    <g>
      <line
        x1={x1} y1={y1} x2={x2} y2={y2}
        stroke={brand ? c.forest : c.line}
        strokeWidth={1.5}
        strokeDasharray={dashed ? '5 4' : undefined}
        markerEnd={marker}
        markerStart={both ? marker : undefined}
      />
      {label && (
        <text x={(x1 + x2) / 2 + labelDx} y={(y1 + y2) / 2 + labelDy} textAnchor="middle" fontSize={11.5} fill={c.muted}>
          {label}
        </text>
      )}
    </g>
  );
}

function Label({ x, y, text, size = 12, weight = 400, anchor = 'start', color }: {
  x: number, y: number, text: string, size?: number, weight?: number, anchor?: 'start' | 'middle' | 'end', color?: string,
}) {
  const c = useDiagramColors();
  return <text x={x} y={y} fontSize={size} fontWeight={weight} textAnchor={anchor} fill={color || c.muted}>{text}</text>;
}

function Actor({ x, y, label, sublabel, external }: { x: number, y: number, label: string, sublabel?: string, external?: boolean }) {
  const c = useDiagramColors();
  const stroke = external ? c.wood : c.forest;
  return (
    <g>
      <circle cx={x} cy={y} r={11} fill={c.cream} stroke={stroke} strokeWidth={1.8}/>
      <line x1={x} y1={y + 11} x2={x} y2={y + 40} stroke={stroke} strokeWidth={1.8}/>
      <line x1={x - 16} y1={y + 22} x2={x + 16} y2={y + 22} stroke={stroke} strokeWidth={1.8}/>
      <line x1={x} y1={y + 40} x2={x - 13} y2={y + 60} stroke={stroke} strokeWidth={1.8}/>
      <line x1={x} y1={y + 40} x2={x + 13} y2={y + 60} stroke={stroke} strokeWidth={1.8}/>
      <text x={x} y={y + 80} textAnchor="middle" fontSize={13} fontWeight={600} fill={c.text}>{label}</text>
      {sublabel && <text x={x} y={y + 96} textAnchor="middle" fontSize={11} fill={c.muted}>{sublabel}</text>}
    </g>
  );
}

// ========== Kiến trúc ==========

export function ArchitectureDiagram() {
  const c = useDiagramColors();
  return (
    <DiagramFrame viewBox="0 0 980 560" title="Kiến trúc 3 tầng của Necom">
      {/* Nhãn tầng */}
      {[
        { y: 20, h: 120, label: 'Presentation' },
        { y: 200, h: 220, label: 'Application' },
        { y: 470, h: 80, label: 'Data' },
      ].map(tier => (
        <g key={tier.label}>
          <rect x={10} y={tier.y} width={6} height={tier.h} rx={3} fill={c.brand} opacity={0.6}/>
          <text x={24} y={tier.y + 18} fontSize={11} fontWeight={700} fill={c.forest}>{tier.label.toUpperCase()}</text>
        </g>
      ))}

      {/* Presentation */}
      <rect x={120} y={20} width={560} height={120} rx={12} fill={c.sand} stroke={c.sandBorder}/>
      <Label x={136} y={42} text="React 17 · TypeScript · Mantine (SPA phục vụ bởi Nginx)" size={12.5} weight={600} color={c.text}/>
      <Node x={136} y={56} w={170} h={68} title="Storefront" subtitle={['Sản phẩm, giỏ, đơn hàng', '/']}/>
      <Node x={320} y={56} w={170} h={68} title="Trang quản trị" subtitle={['Kho, đơn, vận đơn…', '/admin']}/>
      <Node x={504} y={56} w={160} h={68} title="Chat widget" subtitle={['FAB, modal, inbox', 'STOMP + SSE']}/>

      <Arrow x1={330} y1={144} x2={330} y2={196} label="REST · JSON" labelDx={-46} labelDy={4}/>
      <Arrow x1={470} y1={196} x2={470} y2={144} label="STOMP · SSE" labelDx={48} labelDy={4}/>

      {/* Application */}
      <rect x={120} y={200} width={560} height={220} rx={12} fill={c.sand} stroke={c.sandBorder}/>
      <Label x={136} y={222} text="Spring Boot 3.5 · Java 17" size={12.5} weight={600} color={c.text}/>
      <Node x={136} y={236} w={126} h={50} title="Controller" variant="brand"/>
      <Node x={272} y={236} w={126} h={50} title="Service" variant="brand"/>
      <Node x={136} y={296} w={126} h={50} title="Repository" variant="brand"/>
      <Node x={272} y={296} w={126} h={50} title="Entity" variant="brand"/>
      <Node x={136} y={356} w={262} h={50} title="MapStruct DTO · RSQL filter" subtitle="Generic CRUD /api/{resource}"/>
      <Node x={414} y={236} w={250} h={50} title="Security 6 + JWT" subtitle="Filter HTTP · interceptor STOMP"/>
      <Node x={414} y={296} w={250} h={50} title="STOMP broker · SSE" subtitle="Chat realtime, thông báo"/>
      <Node x={414} y={356} w={250} h={50} title="Chatbot service" subtitle="Tri thức + sản phẩm + đơn hàng"/>

      <Arrow x1={400} y1={424} x2={400} y2={466} both label="JPA / Hibernate 6" labelDx={62} labelDy={4}/>

      {/* Data */}
      <rect x={120} y={470} width={560} height={80} rx={12} fill={c.sand} stroke={c.sandBorder}/>
      <Node x={250} y={482} w={300} h={56} title="MySQL 8.0" subtitle="63 bảng · utf8mb4 · Docker volume"/>

      {/* Dịch vụ ngoài */}
      <rect x={750} y={180} width={220} height={256} rx={12} fill="none" stroke={c.sandBorder} strokeDasharray="6 5"/>
      <Label x={766} y={202} text="DỊCH VỤ BÊN NGOÀI" size={12} weight={700} color={c.wood}/>
      {[
        { title: 'Giao Hàng Nhanh', sub: 'Tạo vận đơn · webhook' },
        { title: 'PayPal Sandbox', sub: 'Tạo & capture giao dịch' },
        { title: 'LLM (OpenAI API)', sub: 'Trợ lý AI, gợi ý trả lời' },
        { title: 'SMTP Gmail', sub: 'Xác minh, đặt lại mật khẩu' },
      ].map((service, i) => {
        const y = 214 + i * 54;
        return (
          <g key={service.title}>
            <Node x={766} y={y} w={188} h={46} title={service.title} subtitle={service.sub}/>
            <Arrow x1={684} y1={y + 23} x2={762} y2={y + 23} both/>
          </g>
        );
      })}
    </DiagramFrame>
  );
}

// ========== Triển khai ==========

export function DeploymentDiagram() {
  const c = useDiagramColors();
  return (
    <DiagramFrame viewBox="0 0 980 360" title="Sơ đồ triển khai trên VPS">
      <Node x={10} y={88} w={130} h={64} title="Trình duyệt" subtitle="Khách · Nhân viên"/>
      <Arrow x1={144} y1={120} x2={196} y2={120} label="HTTPS" labelDy={-8}/>
      <Node x={200} y={88} w={130} h={64} title="Cloudflare" subtitle="DNS · TLS · proxy"/>

      <rect x={390} y={20} width={580} height={330} rx={14} fill={c.sand} stroke={c.sandBorder}/>
      <Label x={408} y={44} text="VPS · necom.vnhat.dev" size={13} weight={700} color={c.forest}/>
      <Node x={408} y={88} w={120} h={64} title="Nginx host" subtitle=":443 → :8080"/>

      <rect x={578} y={60} width={378} height={184} rx={12} fill={c.cream} stroke={c.sandBorder} strokeDasharray="6 5"/>
      <Label x={594} y={82} text="Docker network: necom-network" size={12} weight={600} color={c.wood}/>
      <Node x={594} y={92} w={170} h={56} title="necom-client" subtitle="Nginx + React build" variant="brand"/>
      <Node x={594} y={176} w={170} h={56} title="necom-server" subtitle="Spring Boot · :8085" variant="brand"/>
      <Node x={786} y={176} w={154} h={56} title="necom-database" subtitle="MySQL 8 · :3306" variant="brand"/>
      <Arrow x1={679} y1={150} x2={679} y2={172}/>
      <Label x={690} y={166} text="/api · /ws · /client-api"/>
      <Arrow x1={766} y1={204} x2={782} y2={204}/>

      <Node x={786} y={254} w={154} h={46} title="AI proxy (trên host)" subtitle=":20128 /v1" variant="ghost"/>
      <Arrow x1={700} y1={234} x2={782} y2={272} dashed/>
      <Label x={594} y={266} text="host.docker.internal"/>

      <Arrow x1={334} y1={120} x2={404} y2={120}/>
      <Arrow x1={532} y1={120} x2={590} y2={120}/>
      <Label x={408} y={318} text="Bí mật (JWT, GHN, PayPal, CHATBOT_*) nằm trong .env trên VPS, không commit." size={12}/>
      <Label x={408} y={334} text="Backup CSDL: /var/www/necom/backups · rollback: branch legacy/spring-boot-2." size={12}/>
    </DiagramFrame>
  );
}

// ========== Tác nhân ==========

export function ActorsDiagram() {
  const c = useDiagramColors();
  return (
    <DiagramFrame viewBox="0 0 980 330" title="Các tác nhân của hệ thống" maxWidth={980}>
      <Actor x={170} y={20} label="Khách hàng"/>
      <Actor x={70} y={200} label="Khách vãng lai" sublabel="chưa đăng nhập"/>
      <Actor x={270} y={200} label="Khách đã đăng ký" sublabel="role CUSTOMER"/>
      <line x1={88} y1={196} x2={156} y2={126} stroke={c.text} strokeWidth={1.2} markerEnd="url(#doc-generalization)"/>
      <line x1={252} y1={196} x2={184} y2={126} stroke={c.text} strokeWidth={1.2} markerEnd="url(#doc-generalization)"/>

      <Actor x={460} y={20} label="Nhân viên" sublabel="role EMPLOYEE"/>
      <Actor x={460} y={200} label="Người quản trị" sublabel="role ADMIN"/>
      <line x1={460} y1={194} x2={460} y2={138} stroke={c.text} strokeWidth={1.2} markerEnd="url(#doc-generalization)"/>

      <line x1={590} y1={20} x2={590} y2={310} stroke={c.sandBorder} strokeDasharray="5 5"/>
      <Label x={610} y={24} text="HỆ THỐNG NGOÀI" size={12} weight={700} color={c.wood}/>
      <Actor x={680} y={50} label="Dịch vụ GHN" sublabel="vận chuyển" external/>
      <Actor x={800} y={50} label="PayPal" sublabel="thanh toán" external/>
      <Actor x={920} y={50} label="Trợ lý AI" sublabel="LLM" external/>
      <Label x={610} y={230} text="Người quản trị kế thừa toàn bộ quyền của nhân viên" size={12}/>
      <Label x={610} y={250} text="và có thêm các chức năng quản trị hệ thống." size={12}/>
      <Label x={610} y={282} text="Tác nhân ngoài chỉ giao tiếp với backend qua API." size={12}/>
    </DiagramFrame>
  );
}

// ========== Use case ==========

interface UseCaseSpec {
  label: string;
  actors: string[];
}

interface ActorSpec {
  key: string;
  label: string;
  sublabel?: string;
  side: 'left' | 'right';
  y: number;
}

function UseCaseDiagram({ title, system, actors, useCases, generalization }: {
  title: string,
  system: string,
  actors: ActorSpec[],
  useCases: UseCaseSpec[],
  generalization?: { from: string, to: string },
}) {
  const c = useDiagramColors();
  const rowH = 46;
  const top = 60;
  const height = top + useCases.length * rowH + 30;
  const boxX = 300;
  const boxW = 380;
  const ellipseW = 300;
  const cx = boxX + boxW / 2;

  const actorPos = (a: ActorSpec) => ({ x: a.side === 'left' ? 120 : 860, y: a.y });

  return (
    <DiagramFrame viewBox={`0 0 980 ${height}`} title={title}>
      <rect x={boxX} y={20} width={boxW} height={height - 34} rx={14} fill={c.sand} stroke={c.sandBorder}/>
      <text x={cx} y={44} textAnchor="middle" fontSize={13} fontWeight={700} fill={c.forest}>{system}</text>

      {useCases.map((uc, i) => {
        const y = top + i * rowH + rowH / 2;
        return uc.actors.map(key => {
          const actor = actors.find(a => a.key === key);
          if (!actor) {
            return null;
          }
          const p = actorPos(actor);
          const fromX = actor.side === 'left' ? p.x + 22 : p.x - 22;
          const toX = actor.side === 'left' ? cx - ellipseW / 2 : cx + ellipseW / 2;
          return <line key={uc.label + key} x1={fromX} y1={p.y + 30} x2={toX} y2={y} stroke={c.line} strokeWidth={1} opacity={0.75}/>;
        });
      })}

      {useCases.map((uc, i) => {
        const y = top + i * rowH + rowH / 2;
        return (
          <g key={uc.label}>
            <ellipse cx={cx} cy={y} rx={ellipseW / 2} ry={17} fill={c.cream} stroke={c.sandBorder}/>
            <text x={cx} y={y + 4.5} textAnchor="middle" fontSize={13} fill={c.text}>{uc.label}</text>
          </g>
        );
      })}

      {actors.map(a => {
        const p = actorPos(a);
        return <Actor key={a.key} x={p.x} y={p.y} label={a.label} sublabel={a.sublabel}/>;
      })}

      {generalization && (() => {
        const from = actors.find(a => a.key === generalization.from);
        const to = actors.find(a => a.key === generalization.to);
        if (!from || !to) {
          return null;
        }
        const pf = actorPos(from);
        const pt = actorPos(to);
        return <line x1={pf.x} y1={pf.y - 6} x2={pt.x} y2={pt.y + 104} stroke={c.text} strokeWidth={1.2} markerEnd="url(#doc-generalization)"/>;
      })()}
    </DiagramFrame>
  );
}

export function ClientUseCaseDiagram() {
  const G = 'guest';
  const M = 'member';
  return (
    <UseCaseDiagram
      title="Use case phía khách hàng"
      system="Client side – Necom Storefront"
      actors={[
        { key: M, label: 'Khách đã đăng ký', side: 'left', y: 330 },
        { key: G, label: 'Khách vãng lai', side: 'right', y: 120 },
      ]}
      useCases={[
        { label: 'Đăng ký tài khoản', actors: [G] },
        { label: 'Xem, tìm kiếm & lọc sản phẩm', actors: [G, M] },
        { label: 'Xem trung tâm trợ giúp', actors: [G, M] },
        { label: 'Đăng nhập / đăng xuất', actors: [M] },
        { label: 'Quên & đặt lại mật khẩu', actors: [M] },
        { label: 'Quản lý giỏ hàng', actors: [M] },
        { label: 'Đặt hàng (COD / PayPal)', actors: [M] },
        { label: 'Theo dõi & hủy đơn hàng', actors: [M] },
        { label: 'Đánh giá sản phẩm', actors: [M] },
        { label: 'Yêu thích & đặt trước', actors: [M] },
        { label: 'Xem điểm thưởng', actors: [M] },
        { label: 'Chat trợ lý AI / tư vấn viên', actors: [M] },
        { label: 'Nhận thông báo realtime', actors: [M] },
        { label: 'Quản lý thông tin cá nhân', actors: [M] },
      ]}
    />
  );
}

export function AdminUseCaseDiagram() {
  const E = 'employee';
  const A = 'admin';
  return (
    <UseCaseDiagram
      title="Use case phía quản trị"
      system="Admin side – Necom Back office"
      actors={[
        { key: E, label: 'Nhân viên', side: 'left', y: 150 },
        { key: A, label: 'Người quản trị', side: 'left', y: 500 },
      ]}
      generalization={{ from: A, to: E }}
      useCases={[
        { label: 'Đăng nhập / đăng xuất', actors: [E] },
        { label: 'Xem thống kê', actors: [E] },
        { label: 'Quản lý đơn hàng', actors: [E] },
        { label: 'Tạo & theo dõi vận đơn GHN', actors: [E] },
        { label: 'Quản lý tồn kho, phiếu nhập/xuất', actors: [E] },
        { label: 'Quản lý đánh giá', actors: [E] },
        { label: 'Inbox CSKH & ghi chú nội bộ', actors: [E] },
        { label: 'Gợi ý trả lời bằng AI', actors: [E] },
        { label: 'Quản lý sản phẩm & danh mục', actors: [A] },
        { label: 'Quản lý người dùng & khách hàng', actors: [A] },
        { label: 'Quản lý nhân viên', actors: [A] },
        { label: 'Quản lý khuyến mãi', actors: [A] },
        { label: 'Chiến lược điểm thưởng', actors: [A] },
        { label: 'Hình thức thanh toán', actors: [A] },
      ]}
    />
  );
}

// ========== Vòng đời đơn hàng ==========

function StateNode({ x, y, w = 150, title, code, tone = 'default' }: {
  x: number, y: number, w?: number, title: string, code: string, tone?: 'default' | 'success' | 'danger',
}) {
  const c = useDiagramColors();
  const stroke = tone === 'success' ? c.forest : tone === 'danger' ? '#B4574A' : c.sandBorder;
  const fill = tone === 'success' ? c.brandSoft : c.cream;
  return (
    <g>
      <rect x={x} y={y} width={w} height={56} rx={28} fill={fill} stroke={stroke} strokeWidth={tone === 'default' ? 1 : 1.6}/>
      <text x={x + w / 2} y={y + 24} textAnchor="middle" fontSize={11} fontWeight={700} fill={c.wood}>{code}</text>
      <text x={x + w / 2} y={y + 41} textAnchor="middle" fontSize={13.5} fontWeight={600} fill={c.text}>{title}</text>
    </g>
  );
}

export function OrderLifecycleDiagram() {
  const c = useDiagramColors();
  const w = 140;
  const xs = [56, 296, 536, 776];
  const y = 70;
  return (
    <DiagramFrame viewBox="0 0 980 320" title="Vòng đời đơn hàng và vận đơn">
      <Label x={56} y={36} text="Trạng thái vận đơn đồng bộ tự động qua webhook PUT /api/waybills/callback-ghn" size={12}/>
      <circle cx={24} cy={y + 28} r={9} fill={c.forest}/>
      <Arrow x1={34} y1={y + 28} x2={52} y2={y + 28}/>
      <StateNode x={xs[0]} y={y} w={w} title="Đơn hàng mới" code="STATUS 1"/>
      <StateNode x={xs[1]} y={y} w={w} title="Đang xử lý" code="STATUS 2"/>
      <StateNode x={xs[2]} y={y} w={w} title="Đang giao hàng" code="STATUS 3"/>
      <StateNode x={xs[3]} y={y} w={w} title="Đã giao hàng" code="STATUS 4" tone="success"/>
      {[
        { from: 0, label: 'Tạo vận đơn GHN' },
        { from: 1, label: 'GHN: picked' },
        { from: 2, label: 'GHN: delivered' },
      ].map(t => (
        <Arrow key={t.label} x1={xs[t.from] + w + 4} y1={y + 28} x2={xs[t.from + 1] - 4} y2={y + 28} label={t.label} labelDy={-10}/>
      ))}
      <Arrow x1={xs[3] + w + 4} y1={y + 28} x2={943} y2={y + 28}/>
      <circle cx={956} cy={y + 28} r={11} fill="none" stroke={c.forest} strokeWidth={1.6}/>
      <circle cx={956} cy={y + 28} r={6} fill={c.forest}/>

      <StateNode x={380} y={236} w={170} title="Hủy bỏ" code="STATUS 5" tone="danger"/>
      <Arrow x1={126} y1={y + 58} x2={390} y2={246} label="Khách / nhân viên hủy" labelDx={-70} labelDy={10}/>
      <Arrow x1={366} y1={y + 58} x2={440} y2={232}/>
      <Arrow x1={606} y1={y + 58} x2={500} y2={232} label="GHN: fail / return" labelDx={90} labelDy={6}/>

      <Label x={776} y={170} text="Khi giao thành công:" size={12} weight={600} color={c.text}/>
      <Label x={776} y={190} text="• thanh toán → Đã thanh toán" size={12}/>
      <Label x={776} y={208} text="• cộng điểm SUCCESS_ORDER" size={12}/>
      <Label x={776} y={226} text="• ghi WaybillLog" size={12}/>
    </DiagramFrame>
  );
}

// ========== Sequence thanh toán ==========

export function CheckoutSequenceDiagram() {
  const c = useDiagramColors();
  const lanes = [
    { x: 90, label: 'Khách hàng' },
    { x: 280, label: 'Frontend' },
    { x: 480, label: 'Backend' },
    { x: 680, label: 'MySQL' },
    { x: 880, label: 'PayPal' },
  ];
  const steps: { from: number, to: number, y: number, label: string, reply?: boolean }[] = [
    { from: 0, to: 1, y: 100, label: 'Chọn địa chỉ, phương thức thanh toán' },
    { from: 1, to: 2, y: 140, label: 'POST /client-api/orders' },
    { from: 2, to: 3, y: 180, label: 'Lưu Order (status 1, chưa thanh toán)' },
    { from: 2, to: 4, y: 220, label: 'Tạo giao dịch (nếu PayPal)' },
    { from: 4, to: 2, y: 260, label: 'Link phê duyệt', reply: true },
    { from: 2, to: 1, y: 300, label: 'Mã đơn / URL PayPal', reply: true },
    { from: 0, to: 4, y: 340, label: 'Đăng nhập PayPal & phê duyệt' },
    { from: 4, to: 2, y: 380, label: 'Redirect /client-api/orders/success', reply: true },
    { from: 2, to: 3, y: 420, label: 'Capture → Đã thanh toán' },
    { from: 2, to: 1, y: 460, label: 'Chuyển tới /payment/success', reply: true },
  ];
  return (
    <DiagramFrame viewBox="0 0 980 500" title="Sequence đặt hàng và thanh toán">
      {lanes.map(lane => (
        <g key={lane.label}>
          <rect x={lane.x - 70} y={20} width={140} height={40} rx={8} fill={c.cream} stroke={c.sandBorder}/>
          <text x={lane.x} y={45} textAnchor="middle" fontSize={13} fontWeight={600} fill={c.text}>{lane.label}</text>
          <line x1={lane.x} y1={60} x2={lane.x} y2={490} stroke={c.sandBorder} strokeDasharray="4 5"/>
        </g>
      ))}
      {steps.map(step => {
        const x1 = lanes[step.from].x;
        const x2 = lanes[step.to].x;
        const dir = x2 > x1 ? 1 : -1;
        return (
          <g key={step.y}>
            <line
              x1={x1 + dir * 4} y1={step.y} x2={x2 - dir * 6} y2={step.y}
              stroke={step.reply ? c.line : c.forest}
              strokeWidth={1.5}
              strokeDasharray={step.reply ? '5 4' : undefined}
              markerEnd={step.reply ? 'url(#doc-arrow)' : 'url(#doc-arrow-brand)'}
            />
            <text x={(x1 + x2) / 2} y={step.y - 7} textAnchor="middle" fontSize={11.5} fill={c.text}>{step.label}</text>
          </g>
        );
      })}
    </DiagramFrame>
  );
}

// ========== Chat ==========

export function ChatStateDiagram() {
  const c = useDiagramColors();
  return (
    <DiagramFrame viewBox="0 0 980 300" title="Máy trạng thái phiên chat">
      <circle cx={30} cy={120} r={9} fill={c.forest}/>
      <Arrow x1={40} y1={120} x2={86} y2={120}/>
      <Label x={14} y={84} text="Khách mở chat"/>
      <StateNode x={90} y={92} w={170} title="Trợ lý AI trả lời" code="BOT"/>
      <Arrow x1={264} y1={112} x2={366} y2={112} label="Gặp tư vấn viên" labelDy={-8}/>
      <Arrow x1={366} y1={132} x2={264} y2={132} label="NV trả lại bot" labelDy={20}/>
      <StateNode x={370} y={92} w={180} title="Chờ tư vấn viên" code="WAITING_AGENT"/>
      <Arrow x1={554} y1={120} x2={646} y2={120} label="Nhân viên nhận" labelDy={-10}/>
      <StateNode x={650} y={92} w={170} title="Tư vấn viên hỗ trợ" code="AGENT" tone="success"/>
      <Arrow x1={735} y1={92} x2={175} y2={60} dashed label="Trả lại bot" labelDy={-8}/>
      <StateNode x={410} y={220} w={170} title="Đã kết thúc" code="RESOLVED"/>
      <Arrow x1={175} y1={152} x2={430} y2={228} label="Kết thúc" labelDx={-60} labelDy={4}/>
      <Arrow x1={735} y1={152} x2={560} y2={228} label="Kết thúc" labelDx={60} labelDy={4}/>
      <Arrow x1={410} y1={250} x2={110} y2={152} dashed label="Khách nhắn lại" labelDx={-70} labelDy={34}/>
      <Label x={840} y={112} text="Bot im lặng" size={12} weight={600} color={c.text}/>
      <Label x={840} y={130} text="khi có người" size={12}/>
      <Label x={840} y={148} text="phụ trách." size={12}/>
    </DiagramFrame>
  );
}

export function ChatbotPipelineDiagram() {
  const c = useDiagramColors();
  return (
    <DiagramFrame viewBox="0 0 980 300" title="Pipeline trả lời của trợ lý AI">
      <Node x={10} y={110} w={140} h={70} title="Tin của khách" subtitle={['STOMP, lưu DB', 'trước khi xử lý']}/>
      <Arrow x1={154} y1={145} x2={194} y2={145} label="gộp tin 1,2s" labelDy={-40}/>
      <Node x={198} y={110} w={150} h={70} title="Dựng ngữ cảnh" subtitle={['12 tin gần nhất', 'chỉ khi phòng = BOT']} variant="brand"/>

      <Node x={400} y={16} w={210} h={62} title="Tri thức cửa hàng" subtitle="knowledge.md: ship, đổi trả…"/>
      <Node x={400} y={114} w={210} h={62} title="Chỉ mục sản phẩm" subtitle="Từ khóa bỏ dấu + lọc giá"/>
      <Node x={400} y={212} w={210} h={62} title="Dữ liệu cá nhân" subtitle="Đơn, điểm theo chủ phòng"/>
      <Arrow x1={396} y1={47} x2={352} y2={128}/>
      <Arrow x1={396} y1={145} x2={352} y2={145}/>
      <Arrow x1={396} y1={243} x2={352} y2={162}/>

      <Arrow x1={614} y1={145} x2={654} y2={145} label="prompt"/>
      <Node x={658} y={110} w={130} h={70} title="LLM" subtitle={['JSON: reply,', 'productIds, handoff']}/>
      <Arrow x1={792} y1={145} x2={832} y2={145}/>
      <Node x={836} y={100} w={136} h={90} title="Hydrate & gửi" subtitle={['Thẻ SP/đơn chỉ', 'từ ngữ cảnh đã cấp', 'handoff → chờ NV']} variant="brand"/>
      <Label x={400} y={296} text="Prompt chặn prompt-injection; không bịa giá, tồn kho, mã giảm giá." size={12} color={c.muted}/>
    </DiagramFrame>
  );
}

export function ChatErdDiagram() {
  const c = useDiagramColors();
  const table = (x: number, y: number, name: string, fields: string[], w = 220) => (
    <g>
      <rect x={x} y={y} width={w} height={34 + fields.length * 20} rx={8} fill={c.cream} stroke={c.sandBorder}/>
      <rect x={x} y={y} width={w} height={30} rx={8} fill={c.forest}/>
      <rect x={x} y={y + 22} width={w} height={8} fill={c.forest}/>
      <text x={x + 12} y={y + 20} fontSize={13} fontWeight={700} fill="#FFFFFF">{name}</text>
      {fields.map((f, i) => (
        <text key={f} x={x + 12} y={y + 50 + i * 20} fontSize={12} fill={f.includes('PK') || f.includes('FK') ? c.text : c.muted}>{f}</text>
      ))}
    </g>
  );
  return (
    <DiagramFrame viewBox="0 0 980 320" title="Quan hệ dữ liệu chat">
      {table(20, 30, 'user', ['id  PK', 'username', 'fullname', 'email'], 190)}
      {table(300, 20, 'room', ['id  PK', 'user_id  FK (unique)', 'status  BOT | WAITING | AGENT | RESOLVED', 'assignee_id  FK → user', 'last_message_id  FK', 'customer_last_read_id', 'agent_last_read_id'], 300)}
      {table(690, 20, 'message', ['id  PK', 'room_id  FK', 'user_id  FK (null: bot / system)', 'type  TEXT | SYSTEM | INTERNAL_NOTE', 'sender_type', 'content  TEXT', 'payload  JSON', 'client_msg_id  UNIQUE'], 280)}
      {table(20, 200, 'notification', ['id  PK', 'user_id  FK', 'type  CHAT | ORDER | …', 'anchor  /user/chat'], 190)}
      <Arrow x1={214} y1={70} x2={296} y2={70} label="1 : 1 khách" labelDy={-6}/>
      <Arrow x1={214} y1={96} x2={296} y2={132} label="phụ trách" labelDx={-6} labelDy={20}/>
      <Arrow x1={604} y1={70} x2={686} y2={70} label="1 : n" labelDy={-6}/>
      <Arrow x1={115} y1={138} x2={115} y2={196} label="1 : n" labelDx={24} labelDy={4}/>
    </DiagramFrame>
  );
}
