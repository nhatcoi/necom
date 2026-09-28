SET NAMES 'utf8mb4' COLLATE 'utf8mb4_unicode_ci';
SET CHARACTER SET utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- 1. CLEANUP OLD DATA
TRUNCATE TABLE review;
TRUNCATE TABLE wish;
TRUNCATE TABLE preorder;
TRUNCATE TABLE cart_variant;
TRUNCATE TABLE order_variant;
TRUNCATE TABLE docket_variant;
TRUNCATE TABLE purchase_order_variant;
TRUNCATE TABLE promotion_product;
TRUNCATE TABLE product_tag;
TRUNCATE TABLE product_inventory_limit;
TRUNCATE TABLE variant_inventory_limit;
TRUNCATE TABLE storage_location;
TRUNCATE TABLE count_variant;
TRUNCATE TABLE image;
TRUNCATE TABLE variant;
TRUNCATE TABLE product;
TRUNCATE TABLE category;
TRUNCATE TABLE brand;

-- 2. CATEGORIES (Home & Living)
INSERT INTO category (id, created_at, updated_at, created_by, updated_by, name, slug, description, thumbnail, category_id, status)
VALUES
(1, NOW(), NOW(), null, null, 'Bàn ghế', 'ban-ghe', 'Bàn ăn, ghế gỗ sồi, ghế thư giãn phong cách tối giản', '/images/categories/ban-ghe.jpg', null, 1),
(2, NOW(), NOW(), null, null, 'Sofa', 'sofa', 'Sofa băng, sofa góc, sofa nỉ cao cấp cho phòng khách', '/images/products/sofa-vang.jpg', null, 1),
(3, NOW(), NOW(), null, null, 'Tủ kệ', 'tu-ke', 'Kệ sách, tủ đầu giường, kệ tivi và tủ lưu trữ', '/images/categories/ke-tu.jpg', null, 1),
(4, NOW(), NOW(), null, null, 'Đèn & chiếu sáng', 'den-chieu-sang', 'Đèn thả trần Japandi, đèn cây đứng, đèn ngủ để bàn', '/images/categories/den-trang-tri.jpg', null, 1),
(5, NOW(), NOW(), null, null, 'Đồ decor', 'decor', 'Bình hoa gốm, tranh treo tường, đồng hồ và phụ kiện decor', '/images/categories/decor.jpg', null, 1),
(6, NOW(), NOW(), null, null, 'Đồ bếp & ăn uống', 'do-bep', 'Nồi gang, bộ bát đĩa gốm mộc, ly cốc và dụng cụ nhà bếp', '/images/categories/do-bep.jpg', null, 1),
(7, NOW(), NOW(), null, null, 'Đồ lưu trữ', 'do-luu-tru', 'Hộp vải lưu trữ, giỏ mây tre đan, kệ mini đa năng', '/images/products/hop-vai.jpg', null, 1),
(8, NOW(), NOW(), null, null, 'Văn phòng tại nhà', 'van-phong-tai-nha', 'Bàn làm việc gỗ sồi, kệ nâng màn hình, setup góc làm việc', '/images/categories/van-phong.jpg', null, 1),
(9, NOW(), NOW(), null, null, 'Cây xanh trang trí', 'cay-canh', 'Cây cảnh lọc không khí trong nhà, chậu gốm sứ tinh tế', '/images/products/cay-canh.jpg', null, 1);

-- 3. BRANDS (Top Home & Living Brands)
INSERT INTO brand (id, created_at, updated_at, name, code, description, status)
VALUES
(1, NOW(), NOW(), 'Muji', 'MUJI', 'Thương hiệu phong cách tối giản Wabi-Sabi Nhật Bản', 1),
(2, NOW(), NOW(), 'IKEA', 'IKEA', 'Nội thất & giải pháp không gian sống tiện ích Thụy Điển', 1),
(3, NOW(), NOW(), 'JYSK', 'JYSK', 'Nội thất & trang trí phong cách Scandinavian Đan Mạch', 1),
(4, NOW(), NOW(), 'Nhà Xinh', 'NHAXINH', 'Thương hiệu nội thất phong cách Á Đông hiện đại', 1),
(5, NOW(), NOW(), 'Hay Design', 'HAY', 'Nội thất & phụ kiện kiến trúc đương đại', 1),
(6, NOW(), NOW(), 'Baya', 'BAYA', 'Tổ ấm tiện nghi với đồ gỗ và trang trí tinh xảo', 1),
(7, NOW(), NOW(), 'Make My Home', 'MMH', 'Thương hiệu nội thất trẻ trung cho không gian sống hiện đại', 1),
(8, NOW(), NOW(), 'Nest Originals', 'NEST', 'Dòng sản phẩm độc quyền tuyển chọn từ Nest Commerce', 1);

-- 4. PRODUCTS (16 Curated Home & Living Products)
INSERT INTO product (id, created_at, updated_at, created_by, updated_by, name, code, slug, short_description, description, status, category_id, brand_id, supplier_id, unit_id, specifications, properties, weight, guarantee_id)
VALUES
-- Product 1: Ghế ăn gỗ sồi tự nhiên
(1, NOW(), NOW(), null, null, 'Ghế ăn gỗ tự nhiên Minimalist', 'NEST-CH-01', 'ghe-an-go-tu-nhien-minimalist',
 'Ghế ăn phong cách Bắc Âu gỗ sồi tự nhiên, đệm ngồi êm ái, thiết kế công thái học.',
 'Ghế ăn gỗ tự nhiên Minimalist được chế tác hoàn toàn từ gỗ sồi Bắc Mỹ (White Oak) tuyển chọn, qua quy trình sấy nhiệt tiêu chuẩn chống cong vênh và mối mọt. Lưng tựa uốn cong ôm sát cơ thể, mang lại cảm giác dễ chịu ngay cả khi ngồi lâu. Đệm ngồi bọc vải sợi tự nhiên thoáng khí, tháo giặt dễ dàng.',
 1, 1, 1, 1, 1,
 '{"content":[{"id":1,"code":"material","name":"Chất liệu","value":"Gỗ sồi tự nhiên, vải lanh"},{"id":2,"code":"origin","name":"Xuất xứ","value":"Việt Nam xuất khẩu"},{"id":3,"code":"dimension","name":"Kích thước","value":"48 x 52 x 78 cm"}],"totalElements":3}',
 '{"content":[{"id":1,"code":"color","name":"Màu sắc","value":["Gỗ tự nhiên","Nâu óc chó"]}],"totalElements":1}',
 5500, 2),

-- Product 2: Đèn thả trần phong cách Japandi
(2, NOW(), NOW(), null, null, 'Đèn thả trần phong cách Japandi', 'NEST-LP-01', 'den-tha-tran-phong-cach-japandi',
 'Đèn thả trần kim loại phủ sơn mờ tối giản phong cách Bắc Âu - Nhật Bản (Japandi), ánh sáng vàng ấm.',
 'Đèn thả trần Japandi là điểm nhấn hoàn hảo cho bàn ăn hoặc phòng khách của bạn. Chao đèn bằng nhôm dập nguyên khối phủ sơn tĩnh điện nano chống bám bụi và chống ố màu theo thời gian. Chuẩn đui E27 phổ biến, tặng kèm bóng LED Edison 3000K bảo vệ mắt.',
 1, 4, 5, 2, 1,
 '{"content":[{"id":1,"code":"material","name":"Chất liệu","value":"Nhôm nguyên khối sơn tĩnh điện"},{"id":2,"code":"power","name":"Công suất","value":"12W (Bóng LED 3000K)"},{"id":3,"code":"dimension","name":"Đường kính","value":"35 cm"}],"totalElements":3}',
 '{"content":[{"id":1,"code":"color","name":"Màu sắc","value":["Trắng be mờ","Xanh olive","Xám xi măng"]}],"totalElements":1}',
 1800, 2),

-- Product 3: Sofa văng bọc vải nỉ cao cấp
(3, NOW(), NOW(), null, null, 'Sofa văng bọc vải nỉ cao cấp', 'NEST-SF-01', 'sofa-vang-boc-vai-ni-cao-cap',
 'Sofa băng 2-3 chỗ ngồi bọc vải nỉ cao cấp chống bám bụi, khung gỗ sồi nguyên khối chịu lực 300kg.',
 'Mẫu sofa văng mang phong cách hiện đại với những đường cong bo tròn tinh tế. Nệm mút D40 êm ái kết hợp lò xo túi độc lập giúp phân tán áp lực đồng đều, không bị xẹp lún sau nhiều năm sử dụng. Chân ghế gỗ sồi tự nhiên thanh thoát, tạo khoảng gầm 15cm thuận tiện cho robot hút bụi di chuyển.',
 1, 2, 4, 1, 1,
 '{"content":[{"id":1,"code":"material","name":"Chất liệu","value":"Khung gỗ sồi, nệm D40, vải nỉ nhung"},{"id":2,"code":"capacity","name":"Sức chứa","value":"2 - 3 người (chịu lực 300kg)"},{"id":3,"code":"dimension","name":"Kích thước","value":"180 x 85 x 75 cm"}],"totalElements":3}',
 '{"content":[{"id":1,"code":"color","name":"Màu sắc","value":["Xám khói","Be kem","Xanh rêu"]}],"totalElements":1}',
 35000, 2),

-- Product 4: Kệ sách gỗ 5 tầng Scandinavian
(4, NOW(), NOW(), null, null, 'Kệ sách gỗ 5 tầng Scandinavian', 'NEST-SH-01', 'ke-sach-go-5-tang-scandinavian',
 'Kệ sách đứng 5 ngăn gỗ sồi phủ Melamine chống trầy xước, phong cách tối giản thanh lịch.',
 'Kệ sách Scandinavian 5 tầng với các ô lưu trữ so le tinh tế, vừa để sách vừa trưng bày decor, chậu cây nhỏ. Cấu trúc liên kết ngàm chắc chắn, đế chân tăng chỉnh chống nghiêng ngả trên mọi bề mặt sàn.',
 1, 3, 2, 3, 1,
 '{"content":[{"id":1,"code":"material","name":"Chất liệu","value":"Gỗ sồi tự nhiên, phủ Melamine cao cấp"},{"id":2,"code":"layers","name":"Số tầng","value":"5 tầng phân ngăn"},{"id":3,"code":"dimension","name":"Kích thước","value":"80 x 28 x 160 cm"}],"totalElements":3}',
 '{"content":[{"id":1,"code":"color","name":"Màu sắc","value":["Vân sồi sáng","Trắng Bắc Âu"]}],"totalElements":1}',
 18500, 2),

-- Product 5: Cây xanh trang trí để bàn
(5, NOW(), NOW(), null, null, 'Cây bàng Singapore để bàn & chậu gốm', 'NEST-PL-01', 'cay-bang-singapore-de-ban-chau-gom',
 'Cây cảnh lọc không khí trong nhà trồng chậu gốm sứ mờ tinh tế, mang sinh khí thiên nhiên vào tổ ấm.',
 'Cây bàng Singapore để bàn có lá bản to xanh mướt, dáng đứng khỏe khoắn mang ý nghĩa phong thủy tốt lành về sự tài lộc và bình an. Chậu gốm sứ vuốt tay tráng men mờ theo phong cách tối giản, có đĩa hứng nước bên dưới sạch sẽ.',
 1, 9, 6, 2, 1,
 '{"content":[{"id":1,"code":"type","name":"Loại cây","value":"Cây trồng trong nhà lọc không khí"},{"id":2,"code":"pot","name":"Chậu cây","value":"Gốm Bát Tràng men mờ kèm đĩa lót"},{"id":3,"code":"height","name":"Chiều cao","value":"45 - 55 cm"}],"totalElements":3}',
 '{"content":[{"id":1,"code":"color","name":"Màu sắc","value":["Chậu Trắng","Chậu Xám tro"]}],"totalElements":1}',
 3200, 1),

-- Product 6: Bàn làm việc gỗ sồi Home Office
(6, NOW(), NOW(), null, null, 'Bàn làm việc gỗ sồi Home Office', 'NEST-DK-01', 'ban-lam-viec-go-soi-home-office',
 'Bàn làm việc chữ nhật gỗ sồi bo viền mềm mại, có ngăn kéo trượt êm và lỗ luồn dây điện thông minh.',
 'Chiếc bàn làm việc lý tưởng cho không gian Home Office tinh gọn. Mặt bàn dày 25mm gia công từ gỗ sồi tự nhiên với vân gỗ mộc mạc, sơn phủ gốc nước an toàn không mùi độc hại. 2 ngăn kéo âm lưu trữ tài liệu tiện lợi.',
 1, 8, 3, 4, 1,
 '{"content":[{"id":1,"code":"material","name":"Chất liệu","value":"Gỗ sồi Nga tự nhiên 100%"},{"id":2,"code":"drawer","name":"Ngăn kéo","value":"2 ngăn kéo ray trượt giảm chấn"},{"id":3,"code":"dimension","name":"Kích thước","value":"120 x 60 x 75 cm"}],"totalElements":3}',
 '{"content":[{"id":1,"code":"size","name":"Kích cỡ","value":["120 x 60 cm","140 x 70 cm"]}],"totalElements":1}',
 22000, 2),

-- Product 7: Nồi gang tráng men đúc nguyên khối
(7, NOW(), NOW(), null, null, 'Nồi gang tráng men đúc nguyên khối', 'NEST-KT-01', 'noi-gang-trang-men-duc-nguyen-khoi',
 'Nồi gang đúc tráng men đa lớp giữ nhiệt vượt trội, nắp đậy tuần hoàn hơi nước cho món hầm thơm mềm.',
 'Nồi gang tráng men cao cấp đạt chuẩn an toàn thực phẩm LFGB châu Âu. Lớp men gốm mịn màng chống dính tự nhiên, không phản ứng với axit thực phẩm. Sử dụng được trên mọi loại bếp: bếp từ, bếp gas, bếp hồng ngoại và cả trong lò nướng.',
 1, 6, 1, 5, 1,
 '{"content":[{"id":1,"code":"material","name":"Chất liệu","value":"Gang đúc tráng men sứ cao cấp"},{"id":2,"code":"capacity","name":"Dung tích","value":"3.8 Lít (Đường kính 24cm)"},{"id":3,"code":"weight","name":"Trọng lượng","value":"4.6 kg"}],"totalElements":3}',
 '{"content":[{"id":1,"code":"color","name":"Màu sắc","value":["Đen nhám","Đỏ Bordeaux","Xanh coban"]}],"totalElements":1}',
 4600, 2),

-- Product 8: Bàn trà tròn đôi mặt đá phiến & gỗ
(8, NOW(), NOW(), null, null, 'Bàn trà tròn đôi mặt đá phiến & gỗ', 'NEST-TB-01', 'ban-tra-tron-doi-mat-da-phien-va-go',
 'Bộ 2 bàn trà lồng thông minh mặt đá phiến chống ố chịu nhiệt kết hợp gỗ sồi ấm cúng.',
 'Bộ bàn trà đôi lồng ghép linh hoạt giúp tiết kiệm diện tích tối đa khi không sử dụng. Bàn lớn mặt đá phiến Ceramic chống trầy, chống thấm ố và chịu nhiệt độ cao; bàn nhỏ mặt gỗ sồi tự nhiên bo tròn duyên dáng.',
 1, 1, 4, 1, 1,
 '{"content":[{"id":1,"code":"material","name":"Chất liệu","value":"Đá phiến Ceramic & Gỗ sồi tự nhiên"},{"id":2,"code":"dimension","name":"Kích thước","value":"Bàn lớn D70xH45cm, Bàn nhỏ D50xH40cm"}],"totalElements":2}',
 '{"content":[{"id":1,"code":"color","name":"Màu sắc","value":["Đá trắng vân mây","Đá đen tia chớp"]}],"totalElements":1}',
 16500, 2),

-- Product 9: Đèn ngủ để bàn chân gốm Wabi-Sabi
(9, NOW(), NOW(), null, null, 'Đèn ngủ để bàn chân gốm Wabi-Sabi', 'NEST-LP-02', 'den-ngu-de-ban-chan-gom-wabi-sabi',
 'Đèn bàn phong cách Wabi-Sabi mộc mạc, thân gốm thô vuốt tay kết hợp chao vải lanh khuếch tán ánh sáng êm dịu.',
 'Đèn ngủ để bàn với dáng bầu tròn mộc mạc từ gốm nung nhiệt độ cao, giữ nguyên chất men thô tự nhiên. Chao đèn bọc vải lanh dệt sợi thưa cho ánh sáng vàng tỏa đều nhẹ nhàng, tạo cảm giác thư giãn tuyệt đối cho phòng ngủ.',
 1, 4, 5, 2, 1,
 '{"content":[{"id":1,"code":"material","name":"Chất liệu","value":"Thân gốm thô, chao đèn vải lanh"},{"id":2,"code":"switch","name":"Công tắc","value":"Chiết áp điều chỉnh độ sáng Dimmer"},{"id":3,"code":"dimension","name":"Kích thước","value":"D28 x H42 cm"}],"totalElements":3}',
 '{"content":[{"id":1,"code":"color","name":"Màu sắc","value":["Gốm be mộc","Gốm đất nung"]}],"totalElements":1}',
 2100, 1),

-- Product 10: Hộp vải lưu trữ đa năng gấp gọn
(10, NOW(), NOW(), null, null, 'Hộp vải lưu trữ đa năng gấp gọn', 'NEST-BX-01', 'hop-vai-luu-tru-da-nang-gap-gon',
 'Hộp đựng quần áo, đồ chơi vải Oxford tráng chống thấm, khung thép chịu lực có quai xách hai bên.',
 'Giải pháp sắp xếp không gian sống ngăn nắp, gọn gàng. Hộp có nắp đậy chống bụi, quai xách chắc chắn chịu lực 20kg. Khi không sử dụng có thể gấp phẳng gọn nhẹ chỉ dày 2cm cất vào ngăn kéo.',
 1, 7, 1, 3, 1,
 '{"content":[{"id":1,"code":"material","name":"Chất liệu","value":"Vải Oxford 600D, khung thép mạ kẽm"},{"id":2,"code":"capacity","name":"Dung tích","value":"66 Lít"},{"id":3,"code":"dimension","name":"Kích thước","value":"50 x 40 x 33 cm"}],"totalElements":3}',
 '{"content":[{"id":1,"code":"color","name":"Màu sắc","value":["Xám ghi","Be sữa","Xanh rêu"]}],"totalElements":1}',
 950, 1),

-- Product 11: Gối tựa sofa vỏ dệt thô Cotton Linen
(11, NOW(), NOW(), null, null, 'Gối tựa sofa vỏ dệt thô Cotton Linen', 'NEST-PLW-01', 'goi-tua-sofa-vo-det-tho-cotton-linen',
 'Gối tựa vuông 45x45cm vỏ vải dệt thô cao cấp, ruột bông gòn vi sợi phồng êm và đàn hồi tốt.',
 'Điểm xuyết màu sắc ấm cúng cho bộ sofa phòng khách hoặc giường ngủ. Vỏ gối dệt sợi Cotton Linen bền chắc thấm hút mồ hôi, khóa kéo ẩn thẩm mỹ dễ tháo giặt máy.',
 1, 2, 3, 4, 1,
 '{"content":[{"id":1,"code":"material","name":"Chất liệu","value":"Vỏ Cotton Linen, ruột gòn Microfiber"},{"id":2,"code":"dimension","name":"Kích thước","value":"45 x 45 cm"}],"totalElements":2}',
 '{"content":[{"id":1,"code":"color","name":"Màu sắc","value":["Vàng mù tạt","Xanh rêu","Nâu gạch","Ghi xám"]}],"totalElements":1}',
 500, 1),

-- Product 12: Bình hoa gốm sứ mờ dáng điêu khắc
(12, NOW(), NOW(), null, null, 'Bình hoa gốm sứ mờ dáng điêu khắc', 'NEST-VS-01', 'binh-hoa-gom-su-mo-dang-dieu-khac',
 'Bình hoa dáng trừu tượng tối giản phủ men nhám mờ, điểm nhấn nghệ thuật độc đáo cho tổ ấm.',
 'Bình hoa gốm thủ công với những đường cong uốn lượn lấy cảm hứng từ thiên nhiên. Thích hợp cắm cành hoa tươi, hoa khô hoặc đơn giản là một tác phẩm điêu khắc decor đứng độc lập.',
 1, 5, 6, 2, 1,
 '{"content":[{"id":1,"code":"material","name":"Chất liệu","value":"Gốm sứ cao cấp tráng men mờ"},{"id":2,"code":"technique","name":"Phương pháp","value":"Thủ công nung nhiệt độ cao 1280°C"},{"id":3,"code":"dimension","name":"Kích thước","value":"18 x 25 cm"}],"totalElements":3}',
 '{"content":[{"id":1,"code":"color","name":"Màu sắc","value":["Trắng sữa","Đất nung Terracotta"]}],"totalElements":1}',
 1200, 1),

-- Product 13: Ghế bành thư giãn phong cách Bắc Âu
(13, NOW(), NOW(), null, null, 'Ghế bành thư giãn phong cách Bắc Âu', 'NEST-AR-01', 'ghe-banh-thu-gian-phong-cach-bac-au',
 'Ghế bành thư giãn lưng ngả 105 độ công thái học, nệm mút đúc dày dặn ôm trọn cơ thể khi đọc sách.',
 'Thiết kế sang trọng mang đậm dấu ấn Scandinavian. Khung chân thép sơn tĩnh điện đen mờ kết hợp tay vịn ốp gỗ óc chó ấm áp. Ghế cho cảm giác ngồi thư thái tối đa sau ngày dài làm việc.',
 1, 1, 4, 1, 1,
 '{"content":[{"id":1,"code":"material","name":"Chất liệu","value":"Khung thép, tay ốp gỗ óc chó, nệm mút đúc bọc nỉ"},{"id":2,"code":"capacity","name":"Tải trọng","value":"150 kg"},{"id":3,"code":"dimension","name":"Kích thước","value":"75 x 82 x 85 cm"}],"totalElements":3}',
 '{"content":[{"id":1,"code":"color","name":"Màu sắc","value":["Xám tro","Vàng nghệ","Xanh dương đậm"]}],"totalElements":1}',
 14000, 2),

-- Product 14: Kệ để giày 3 tầng gỗ thông tự nhiên
(14, NOW(), NOW(), null, null, 'Kệ để giày 3 tầng gỗ thông tự nhiên', 'NEST-SR-01', 'ke-de-giay-3-tang-go-thong-tu-nhien',
 'Kệ để giày dép thông thoáng, gỗ thông tự nhiên đã qua xử lý sấy chống ẩm mốc, dễ lắp ráp.',
 'Thiết kế nan hở giúp giày dép luôn khô ráo và thoáng khí. Mặt trên phẳng có thể dùng làm nơi để túi xách, chìa khóa hoặc chậu cây nhỏ trang trí lối vào nhà.',
 1, 3, 2, 3, 1,
 '{"content":[{"id":1,"code":"material","name":"Chất liệu","value":"Gỗ thông New Zealand sấy kỹ"},{"id":2,"code":"capacity","name":"Sức chứa","value":"9 - 12 đôi giày dép"},{"id":3,"code":"dimension","name":"Kích thước","value":"70 x 26 x 50 cm"}],"totalElements":3}',
 '{"content":[{"id":1,"code":"color","name":"Màu sắc","value":["Gỗ tự nhiên","Trắng"]}],"totalElements":1}',
 4800, 1),

-- Product 15: Bộ 4 cốc gốm thủ công men hỏa biến
(15, NOW(), NOW(), null, null, 'Bộ 4 cốc gốm thủ công men hỏa biến', 'NEST-MG-01', 'bo-4-coc-gom-thu-cong-men-hoa-bien',
 'Bộ cốc sứ men hỏa biến dày dặn giữ nhiệt tốt, dung tích 320ml cho cà phê sáng hoặc trà chiều.',
 'Mỗi chiếc cốc mang một vân men độc bản nhờ kỹ thuật nung hỏa biến ở nhiệt độ 1300 độ C. Quai cầm dày vừa vặn tay, viền miệng cốc được bo tròn êm ái khi thưởng thức đồ uống.',
 1, 6, 1, 5, 1,
 '{"content":[{"id":1,"code":"material","name":"Chất liệu","value":"Gốm sứ cao cấp men hỏa biến"},{"id":2,"code":"volume","name":"Dung tích","value":"320 ml / cốc (Bộ gồm 4 cốc)"}],"totalElements":2}',
 '{"content":[{"id":1,"code":"color","name":"Màu sắc","value":["Bộ 4 màu Pastel","Bộ 4 màu Đất"]}],"totalElements":1}',
 1400, 1),

-- Product 16: Kệ nâng màn hình máy tính gỗ sồi
(16, NOW(), NOW(), null, null, 'Kệ nâng màn hình máy tính gỗ sồi', 'NEST-MR-01', 'ke-nang-man-hinh-may-tinh-go-soi',
 'Kệ nâng màn hình giúp điều chỉnh tầm nhìn công thái học, tích hợp khay chứa bàn phím và rãnh để điện thoại.',
 'Vật dụng không thể thiếu cho góc làm việc công thái học. Nâng màn hình cao thêm 9cm giúp cổ và lưng thẳng tự nhiên. Khoảng trống dưới kệ chứa gọn bàn phím fullsize và chuột khi không làm việc.',
 1, 8, 7, 4, 1,
 '{"content":[{"id":1,"code":"material","name":"Chất liệu","value":"Gỗ sồi tự nhiên bo góc CNC"},{"id":2,"code":"capacity","name":"Tải trọng","value":"40 kg (đủ cho 2 màn hình 27 inch)"},{"id":3,"code":"dimension","name":"Kích thước","value":"80 x 22 x 9 cm"}],"totalElements":3}',
 '{"content":[{"id":1,"code":"color","name":"Màu sắc","value":["Gỗ sồi sáng","Gỗ óc chó trầm"]}],"totalElements":1}',
 2800, 2);

-- 5. VARIANTS
INSERT INTO variant (id, created_at, updated_at, created_by, updated_by, product_id, sku, cost, price, properties, images, status)
VALUES
-- Product 1 variants
(1, NOW(), NOW(), null, null, 1, 'NEST-CH01-NAT', 800000, 1290000, '{"content":[{"id":1,"code":"color","name":"Màu sắc","value":"Gỗ tự nhiên"}],"totalElements":1}', null, 1),
(2, NOW(), NOW(), null, null, 1, 'NEST-CH01-WAL', 850000, 1350000, '{"content":[{"id":1,"code":"color","name":"Màu sắc","value":"Nâu óc chó"}],"totalElements":1}', null, 1),

-- Product 2 variants
(3, NOW(), NOW(), null, null, 2, 'NEST-LP01-WHT', 550000, 890000, '{"content":[{"id":1,"code":"color","name":"Màu sắc","value":"Trắng be mờ"}],"totalElements":1}', null, 1),
(4, NOW(), NOW(), null, null, 2, 'NEST-LP01-GRN', 550000, 890000, '{"content":[{"id":1,"code":"color","name":"Màu sắc","value":"Xanh olive"}],"totalElements":1}', null, 1),

-- Product 3 variants
(5, NOW(), NOW(), null, null, 3, 'NEST-SF01-GRY', 3200000, 4990000, '{"content":[{"id":1,"code":"color","name":"Màu sắc","value":"Xám khói"}],"totalElements":1}', null, 1),
(6, NOW(), NOW(), null, null, 3, 'NEST-SF01-BGE', 3200000, 4990000, '{"content":[{"id":1,"code":"color","name":"Màu sắc","value":"Be kem"}],"totalElements":1}', null, 1),

-- Product 4 variants
(7, NOW(), NOW(), null, null, 4, 'NEST-SH01-OAK', 750000, 1190000, '{"content":[{"id":1,"code":"color","name":"Màu sắc","value":"Vân sồi sáng"}],"totalElements":1}', null, 1),
(8, NOW(), NOW(), null, null, 4, 'NEST-SH01-WHT', 750000, 1190000, '{"content":[{"id":1,"code":"color","name":"Màu sắc","value":"Trắng Bắc Âu"}],"totalElements":1}', null, 1),

-- Product 5 variants
(9, NOW(), NOW(), null, null, 5, 'NEST-PL01-WHT', 180000, 350000, '{"content":[{"id":1,"code":"color","name":"Màu sắc","value":"Chậu Trắng"}],"totalElements":1}', null, 1),
(10, NOW(), NOW(), null, null, 5, 'NEST-PL01-GRY', 180000, 350000, '{"content":[{"id":1,"code":"color","name":"Màu sắc","value":"Chậu Xám tro"}],"totalElements":1}', null, 1),

-- Product 6 variants
(11, NOW(), NOW(), null, null, 6, 'NEST-DK01-120', 1800000, 2850000, '{"content":[{"id":1,"code":"size","name":"Kích cỡ","value":"120 x 60 cm"}],"totalElements":1}', null, 1),
(12, NOW(), NOW(), null, null, 6, 'NEST-DK01-140', 2100000, 3250000, '{"content":[{"id":1,"code":"size","name":"Kích cỡ","value":"140 x 70 cm"}],"totalElements":1}', null, 1),

-- Product 7 variants
(13, NOW(), NOW(), null, null, 7, 'NEST-KT01-BLK', 1100000, 1650000, '{"content":[{"id":1,"code":"color","name":"Màu sắc","value":"Đen nhám"}],"totalElements":1}', null, 1),
(14, NOW(), NOW(), null, null, 7, 'NEST-KT01-RED', 1100000, 1650000, '{"content":[{"id":1,"code":"color","name":"Màu sắc","value":"Đỏ Bordeaux"}],"totalElements":1}', null, 1),

-- Product 8 variants
(15, NOW(), NOW(), null, null, 8, 'NEST-TB01-WHT', 1550000, 2450000, '{"content":[{"id":1,"code":"color","name":"Màu sắc","value":"Đá trắng vân mây"}],"totalElements":1}', null, 1),
(16, NOW(), NOW(), null, null, 8, 'NEST-TB01-BLK', 1550000, 2450000, '{"content":[{"id":1,"code":"color","name":"Màu sắc","value":"Đá đen tia chớp"}],"totalElements":1}', null, 1),

-- Product 9 variants
(17, NOW(), NOW(), null, null, 9, 'NEST-LP02-BGE', 420000, 680000, '{"content":[{"id":1,"code":"color","name":"Màu sắc","value":"Gốm be mộc"}],"totalElements":1}', null, 1),

-- Product 10 variants
(18, NOW(), NOW(), null, null, 10, 'NEST-BX01-GRY', 120000, 220000, '{"content":[{"id":1,"code":"color","name":"Màu sắc","value":"Xám ghi"}],"totalElements":1}', null, 1),
(19, NOW(), NOW(), null, null, 10, 'NEST-BX01-BGE', 120000, 220000, '{"content":[{"id":1,"code":"color","name":"Màu sắc","value":"Be sữa"}],"totalElements":1}', null, 1),

-- Product 11 variants
(20, NOW(), NOW(), null, null, 11, 'NEST-PLW01-YLW', 90000, 180000, '{"content":[{"id":1,"code":"color","name":"Màu sắc","value":"Vàng mù tạt"}],"totalElements":1}', null, 1),
(21, NOW(), NOW(), null, null, 11, 'NEST-PLW01-GRN', 90000, 180000, '{"content":[{"id":1,"code":"color","name":"Màu sắc","value":"Xanh rêu"}],"totalElements":1}', null, 1),

-- Product 12 variants
(22, NOW(), NOW(), null, null, 12, 'NEST-VS01-WHT', 160000, 290000, '{"content":[{"id":1,"code":"color","name":"Màu sắc","value":"Trắng sữa"}],"totalElements":1}', null, 1),

-- Product 13 variants
(23, NOW(), NOW(), null, null, 13, 'NEST-AR01-GRY', 2200000, 3490000, '{"content":[{"id":1,"code":"color","name":"Màu sắc","value":"Xám tro"}],"totalElements":1}', null, 1),

-- Product 14 variants
(24, NOW(), NOW(), null, null, 14, 'NEST-SR01-NAT', 260000, 420000, '{"content":[{"id":1,"code":"color","name":"Màu sắc","value":"Gỗ tự nhiên"}],"totalElements":1}', null, 1),

-- Product 15 variants
(25, NOW(), NOW(), null, null, 15, 'NEST-MG01-PST', 150000, 280000, '{"content":[{"id":1,"code":"color","name":"Màu sắc","value":"Bộ 4 màu Pastel"}],"totalElements":1}', null, 1),

-- Product 16 variants
(26, NOW(), NOW(), null, null, 16, 'NEST-MR01-OAK', 280000, 490000, '{"content":[{"id":1,"code":"color","name":"Màu sắc","value":"Gỗ sồi sáng"}],"totalElements":1}', null, 1);

-- 6. IMAGES (Clean Local Isolated Studio Photography)
INSERT INTO image (id, created_at, updated_at, created_by, updated_by, name, path, content_type, size, `group`, is_thumbnail, is_eliminated, product_id)
VALUES
(1, NOW(), NOW(), null, null, 'ghe-an.jpg', '/images/products/ghe-an.jpg', 'image/jpeg', 250, 'P', true, false, 1),
(2, NOW(), NOW(), null, null, 'den-tha.jpg', '/images/products/den-tha.jpg', 'image/jpeg', 220, 'P', true, false, 2),
(3, NOW(), NOW(), null, null, 'sofa-vang.jpg', '/images/products/sofa-vang.jpg', 'image/jpeg', 310, 'P', true, false, 3),
(4, NOW(), NOW(), null, null, 'ke-sach.jpg', '/images/products/ke-sach.jpg', 'image/jpeg', 270, 'P', true, false, 4),
(5, NOW(), NOW(), null, null, 'cay-canh.jpg', '/images/products/cay-canh.jpg', 'image/jpeg', 260, 'P', true, false, 5),
(6, NOW(), NOW(), null, null, 'ban-lam-viec.jpg', '/images/products/ban-lam-viec.jpg', 'image/jpeg', 280, 'P', true, false, 6),
(7, NOW(), NOW(), null, null, 'noi-gang.jpg', '/images/products/noi-gang.jpg', 'image/jpeg', 240, 'P', true, false, 7),
(8, NOW(), NOW(), null, null, 'ban-tra.jpg', '/images/products/ban-tra.jpg', 'image/jpeg', 250, 'P', true, false, 8),
(9, NOW(), NOW(), null, null, 'den-ngu.jpg', '/images/products/den-ngu.jpg', 'image/jpeg', 230, 'P', true, false, 9),
(10, NOW(), NOW(), null, null, 'hop-vai.jpg', '/images/products/hop-vai.jpg', 'image/jpeg', 210, 'P', true, false, 10),
(11, NOW(), NOW(), null, null, 'goi-tua.jpg', '/images/products/goi-tua.jpg', 'image/jpeg', 200, 'P', true, false, 11),
(12, NOW(), NOW(), null, null, 'binh-hoa.jpg', '/images/products/binh-hoa.jpg', 'image/jpeg', 220, 'P', true, false, 12),
(13, NOW(), NOW(), null, null, 'ghe-banh.jpg', '/images/products/ghe-banh.jpg', 'image/jpeg', 290, 'P', true, false, 13),
(14, NOW(), NOW(), null, null, 'ke-giay.jpg', '/images/products/ke-giay.jpg', 'image/jpeg', 240, 'P', true, false, 14),
(15, NOW(), NOW(), null, null, 'coc-gom.jpg', '/images/products/coc-gom.jpg', 'image/jpeg', 210, 'P', true, false, 15),
(16, NOW(), NOW(), null, null, 'ke-man-hinh.jpg', '/images/products/ke-man-hinh.jpg', 'image/jpeg', 260, 'P', true, false, 16);

-- 7. PRODUCT TAGS (Tag 1 = Mới, Tag 2 = Nổi bật)
INSERT INTO product_tag (product_id, tag_id)
VALUES
(1, 2), -- Ghế ăn: Nổi bật
(2, 2), -- Đèn thả trần: Nổi bật
(3, 2), -- Sofa: Nổi bật
(4, 2), -- Kệ sách: Nổi bật
(5, 2), -- Cây cảnh: Nổi bật
(6, 2), -- Bàn làm việc: Nổi bật
(7, 1), -- Nồi gang: Mới
(8, 1), -- Bàn trà: Mới
(9, 1), -- Đèn ngủ: Mới
(10, 1), -- Hộp vải: Mới
(11, 1), -- Gối tựa: Mới
(12, 1), -- Bình hoa: Mới
(13, 2), -- Ghế bành: Nổi bật
(14, 1), -- Kệ để giày: Mới
(15, 1), -- Cốc gốm: Mới
(16, 2); -- Kệ nâng màn hình: Nổi bật

-- 8. INVENTORY: ADD DOCKET_VARIANTS (50 units in stock for every variant in docket 1)
INSERT INTO docket_variant (docket_id, variant_id, quantity)
VALUES
(1, 1, 50),
(1, 2, 50),
(1, 3, 50),
(1, 4, 50),
(1, 5, 30),
(1, 6, 30),
(1, 7, 40),
(1, 8, 40),
(1, 9, 60),
(1, 10, 60),
(1, 11, 35),
(1, 12, 35),
(1, 13, 45),
(1, 14, 45),
(1, 15, 25),
(1, 16, 25),
(1, 17, 50),
(1, 18, 100),
(1, 19, 100),
(1, 20, 80),
(1, 21, 80),
(1, 22, 40),
(1, 23, 20),
(1, 24, 60),
(1, 25, 75),
(1, 26, 50);

-- 9. REVIEWS
INSERT INTO review (created_at, updated_at, user_id, product_id, rating_score, content, status)
VALUES
(NOW(), NOW(), 4, 1, 5, 'Ghế rất chắc chắn, gỗ sồi màu đẹp tự nhiên đúng gu Japandi mình tìm kiếm bấy lâu. Đệm ngồi êm ái, đóng gói cẩn thận 10/10!', 2),
(NOW(), NOW(), 5, 2, 5, 'Đèn treo lên nhìn sang hẳn cả phòng ăn, ánh sáng vàng ấm rất chill. Chất liệu kim loại sơn nhám cầm rất đầm tay.', 2),
(NOW(), NOW(), 4, 3, 5, 'Sofa êm ái vô cùng, form ghế lên rất đẹp, vải nỉ mịn không xù. Cả nhà ai cũng khen khi đến chơi.', 2),
(NOW(), NOW(), 5, 4, 4, 'Kệ sách dễ lắp ráp, gỗ chắc và không có mùi khó chịu. Phân ngăn so le trưng đồ decor nhìn rất hiện đại.', 2),
(NOW(), NOW(), 4, 5, 5, 'Cây bàng lá to xanh tươi tốt, chậu gốm màu xám mờ rất tinh tế. Đặt góc bàn làm việc thấy tràn đầy năng lượng.', 2);

-- 10. PROMOTIONS
INSERT INTO promotion_product (promotion_id, product_id)
VALUES (1, 3); -- Khuyến mãi sofa 10%

SET FOREIGN_KEY_CHECKS = 1;
