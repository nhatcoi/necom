export const nestImages = {
  living: '/images/nest/living.webp',
  bedroom: '/images/nest/bedroom.webp',
  dining: '/images/nest/dining.webp',
  workspace: '/images/nest/workspace.webp',
  materials: '/images/nest/materials.webp',
};

export const rooms = [
  { title: 'Phòng khách', subtitle: 'Nơi những khoảnh khắc sum vầy bắt đầu', image: nestImages.living, href: '/category/sofa' },
  { title: 'Phòng ngủ', subtitle: 'Một khoảng riêng, thật bình yên', image: nestImages.bedroom, href: '/category/phong-ngu' },
  { title: 'Góc làm việc', subtitle: 'Cho những ý tưởng mới', image: nestImages.workspace, href: '/category/van-phong-tai-nha' },
  { title: 'Phòng ăn', subtitle: 'Giữ lại những phút quây quần', image: nestImages.dining, href: '/category/do-bep' },
];

export const journals = [
  {
    tag: 'HƯỚNG DẪN', title: 'Chọn một chiếc sofa vừa với nhà, hợp với bạn.', image: nestImages.living,
    intro: 'Bắt đầu từ thói quen sống, rồi mới đến kiểu dáng và màu sắc.',
    paragraphs: [
      'Đo chiều dài, chiều rộng của vị trí đặt sofa trước khi chọn mẫu. Đánh dấu kích thước dự kiến trên sàn bằng băng dính để hình dung diện tích thật, đồng thời chừa lối đi thoải mái quanh bàn trà và cửa ra vào.',
      'Đừng quên kiểm tra kích thước cửa, cầu thang và thang máy. Một chiếc sofa vừa phòng khách vẫn cần đi qua được toàn bộ lối vận chuyển.',
      'Nếu thường đọc sách hoặc ngồi trò chuyện, hãy chú ý độ nâng đỡ của lưng tựa. Nếu thích nằm nghỉ, chiều sâu mặt ngồi và tay vịn sẽ quan trọng hơn. Ưu tiên thử trực tiếp khi có thể.',
      'Vải dệt tạo cảm giác ấm áp; màu trung tính dễ phối cùng gối và thảm. Luôn đọc hướng dẫn vệ sinh của từng sản phẩm, đặc biệt với gia đình có trẻ nhỏ hoặc thú cưng.',
    ], href: '/category/sofa', cta: 'Khám phá sofa',
  },
  {
    tag: 'CẢM HỨNG', title: 'Một chút ánh sáng, nhiều hơn cảm giác ở nhà.', image: nestImages.dining,
    intro: 'Phối ánh sáng tổng thể, ánh sáng làm việc và những điểm nhấn dịu dàng.',
    paragraphs: [
      'Một căn phòng thường cần nhiều hơn một đèn trần. Kết hợp ánh sáng chung với đèn tại vị trí đọc sách, bàn ăn hoặc bàn làm việc để mỗi góc phục vụ đúng thói quen sinh hoạt.',
      'Ánh sáng vàng ấm thường phù hợp với không gian nghỉ ngơi. Với bàn làm việc, hãy chọn độ sáng đủ để nhìn rõ và bố trí đèn tránh phản chiếu trực tiếp lên màn hình.',
      'Chao đèn vải hoặc giấy giúp ánh sáng dịu hơn. Thử đặt một chiếc đèn nhỏ ở góc phòng để tạo chiều sâu thay vì tăng độ sáng của toàn bộ không gian.',
      'Kiểm tra loại bóng, công suất và yêu cầu lắp đặt trong thông số sản phẩm. Với đèn cần đấu điện cố định, hãy nhờ người có chuyên môn lắp đặt.',
    ], href: '/category/den-chieu-sang', cta: 'Khám phá đèn',
  },
  {
    tag: 'CHĂM SÓC NHÀ', title: 'Giữ nét đẹp của gỗ qua những ngày thường.', image: nestImages.materials,
    intro: 'Những thói quen nhỏ giúp bề mặt gỗ luôn sạch và dễ chịu.',
    paragraphs: [
      'Dùng khăn mềm lau bụi thường xuyên. Khi có nước đổ, thấm khô sớm, tránh để chất lỏng đọng lâu trên bề mặt hoặc tại các mối nối.',
      'Sử dụng lót ly và tấm lót cho đồ nóng. Hạn chế kéo vật sắc hoặc đồ nặng trực tiếp trên mặt bàn để giảm trầy xước.',
      'Tránh đặt nội thất gỗ sát nguồn nhiệt hoặc dưới nắng gắt kéo dài. Giữ môi trường sử dụng ổn định và thông thoáng theo hướng dẫn của nhà sản xuất.',
      'Gỗ phủ dầu, sơn và veneer có cách chăm sóc khác nhau. Không tự dùng dầu dưỡng hoặc hóa chất mạnh khi chưa kiểm tra loại hoàn thiện và hướng dẫn đi kèm sản phẩm.',
    ], href: '/category/ban-ghe', cta: 'Khám phá bàn ghế',
  },
];
