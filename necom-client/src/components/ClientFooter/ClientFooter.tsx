import React from 'react';
import { Link } from 'react-router-dom';
import { ArrowUpRight } from 'tabler-icons-react';

function ClientFooter() {
  return (
    <footer className="nest-footer">
      <div className="nest-container">
        <div className="nest-footer-grid">
          <div className="nest-footer-brand">
            <Link to="/" className="nest-logo" aria-label="Nest — Trang chủ">nest.</Link>
            <p>Nội thất cho những tổ ấm thật.</p>
            <p>Cùng bạn kiến tạo một không gian<br/>ấm áp, tinh tế và mang dấu ấn riêng.</p>
            <Link className="nest-text-link" to="/about">Câu chuyện Nest <ArrowUpRight size={16}/></Link>
          </div>
          <div><h2>Khám phá</h2><Link to="/search">Tất cả sản phẩm</Link><Link to="/#spaces">Theo không gian</Link><Link to="/#collections">Bộ sưu tập</Link><Link to="/#journal">Nhật ký Nest</Link><Link to="/about">Về chúng tôi</Link></div>
          <div><h2>Luôn bên bạn</h2><Link to="/support/order-guide">Hướng dẫn mua hàng</Link><Link to="/support/shipping">Giao hàng</Link><Link to="/support/return-policy">Chính sách đổi trả</Link><Link to="/support/payment-policy">Thanh toán</Link><Link to="/support/faq">Câu hỏi thường gặp</Link><Link to="/support/complaint">Giải quyết khiếu nại</Link></div>
          <div><h2>Kết nối với Nest</h2><a href="tel:19006868">1900 6868</a><a href="tel:02473008899">(024) 7300 8899</a><p>Số 68 Đường Quang Trung,<br/>Phường Vạn Phúc, Quận Hà Đông,<br/>Hà Nội</p><Link className="nest-text-link" to="/contact">Gửi lời nhắn <ArrowUpRight size={16}/></Link></div>
        </div>
        <div className="nest-footer-bottom"><span>© {new Date().getFullYear()} NECOM · Nest Commerce</span><div><Link to="/support/privacy">Bảo mật</Link><Link to="/partners">Hợp tác</Link><Link to="/careers">Tuyển dụng</Link><Link to="/documentation">Tài liệu dự án</Link></div><span>Nhà, theo cách bạn yêu.</span></div>
      </div>
    </footer>
  );
}
export default React.memo(ClientFooter);
