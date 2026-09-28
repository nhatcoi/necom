-- Chat CSKH v2: trạng thái phòng, loại tin nhắn, người gửi (khách/nhân viên/bot/hệ thống), đã đọc.
-- File chạy lặp lại được: ALTER lỗi "duplicate column" được bỏ qua nhờ spring.sql.init.continue-on-error,
-- các câu UPDATE chỉ backfill dòng còn NULL.

ALTER TABLE message MODIFY content TEXT NOT NULL;
ALTER TABLE message MODIFY user_id BIGINT NULL;
ALTER TABLE message ADD COLUMN type VARCHAR(30) NULL;
ALTER TABLE message ADD COLUMN sender_type VARCHAR(20) NULL;
ALTER TABLE message ADD COLUMN payload JSON NULL;
ALTER TABLE message ADD COLUMN client_msg_id VARCHAR(64) NULL;
ALTER TABLE message ADD CONSTRAINT uc_message_client_msg_id UNIQUE (client_msg_id);

ALTER TABLE room ADD COLUMN status VARCHAR(20) NULL;
ALTER TABLE room ADD COLUMN assignee_id BIGINT NULL;
ALTER TABLE room ADD COLUMN customer_last_read_id BIGINT NULL;
ALTER TABLE room ADD COLUMN agent_last_read_id BIGINT NULL;
ALTER TABLE room ADD CONSTRAINT FK_ROOM_ON_ASSIGNEE FOREIGN KEY (assignee_id) REFERENCES user (id);

UPDATE message SET type = 'TEXT' WHERE type IS NULL;

UPDATE message m JOIN room r ON m.room_id = r.id
SET m.sender_type = IF(m.user_id = r.user_id, 'CUSTOMER', 'AGENT')
WHERE m.sender_type IS NULL;

-- Phòng cũ (dữ liệu trước v2) coi như đã xử lý xong, khách nhắn lại sẽ tự mở phiên mới với bot
UPDATE room SET status = 'RESOLVED' WHERE status IS NULL;

-- Đánh dấu toàn bộ tin cũ là đã đọc để không hiện badge chưa đọc ảo
UPDATE room r SET r.customer_last_read_id = r.last_message_id WHERE r.customer_last_read_id IS NULL;
UPDATE room r SET r.agent_last_read_id = r.last_message_id WHERE r.agent_last_read_id IS NULL;
