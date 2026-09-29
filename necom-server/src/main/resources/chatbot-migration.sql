-- Chatbot v3: lưu embedding sản phẩm (vector float32 little-endian) để tìm kiếm ngữ nghĩa.
-- content_hash giúp chỉ tính lại khi nội dung sản phẩm đổi. Chạy lặp lại được.
CREATE TABLE IF NOT EXISTS product_embedding
(
    product_id   BIGINT       NOT NULL,
    model        VARCHAR(120) NOT NULL,
    content_hash CHAR(64)     NOT NULL,
    dimensions   INT          NOT NULL,
    vector       MEDIUMBLOB   NOT NULL,
    updated_at   DATETIME     NOT NULL,
    CONSTRAINT pk_product_embedding PRIMARY KEY (product_id),
    CONSTRAINT FK_PRODUCT_EMBEDDING_ON_PRODUCT FOREIGN KEY (product_id) REFERENCES product (id) ON DELETE CASCADE
);
