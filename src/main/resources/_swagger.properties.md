### **Giải thích phần tài liệu API trong `pom.xml` và `application.properties`**

---

## **1. Phần `pom.xml`**
Phần `<user__selection><!-- API Documentation --></user__selection>` là một **comment** trong XML, giúp phân tách hoặc đánh dấu các thành phần liên quan đến tài liệu API trong file `pom.xml`.

Ngay sau comment này, có các dependencies sau:

```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>${springdoc.version}</version>
</dependency>
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-common</artifactId>
    <version>${springdoc.version}</version>
</dependency>
```

### **Ý nghĩa các dependency này:**

- **`springdoc-openapi-starter-webmvc-ui`**: Cung cấp giao diện Swagger UI để thử nghiệm các API ngay trên trình duyệt.
- **`springdoc-openapi-starter-common`**: Cung cấp các annotation và công cụ hỗ trợ tạo tài liệu API theo chuẩn OpenAPI Specification.

➡️ Cả hai dependency trên đều thuộc thư viện **SpringDoc**, một công cụ phổ biến thay thế cho Swagger trong các dự án Spring Boot hiện đại.

---

## **2. Phần `application.properties`**
Phần cấu hình sau trong `application.properties` điều chỉnh giao diện và hành vi của Swagger UI:

```properties
springdoc.api-docs.path=/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.swagger-ui.operationsSorter=method
springdoc.swagger-ui.tagsSorter=alpha
springdoc.swagger-ui.tryItOutEnabled=true
springdoc.swagger-ui.filter=true
springdoc.swagger-ui.doc-expansion=none
springdoc.swagger-ui.defaultModelsExpandDepth(-1)
springdoc.swagger-ui.defaultModelExpandDepth(2)
springdoc.swagger-ui.display-request-duration=true
springdoc.swagger-ui.disable-swagger-default-url=true
```

### **Giải thích các thuộc tính chính:**

| **Thuộc tính**                             | **Ý nghĩa** |
|--------------------------------------------|---------------|
| `springdoc.api-docs.path=/api-docs`         | Đường dẫn của tài liệu API theo chuẩn OpenAPI (mặc định là `/v3/api-docs`). |
| `springdoc.swagger-ui.path=/swagger-ui.html`| Đường dẫn để truy cập giao diện Swagger UI (mặc định là `/swagger-ui.html`). |
| `springdoc.swagger-ui.operationsSorter=method`| Sắp xếp các endpoint theo phương thức HTTP (GET, POST, PUT...). |
| `springdoc.swagger-ui.tagsSorter=alpha`      | Sắp xếp các nhóm API (tags) theo thứ tự bảng chữ cái. |
| `springdoc.swagger-ui.tryItOutEnabled=true`  | Kích hoạt nút **"Try it out"** cho phép người dùng thử nghiệm API trực tiếp. |
| `springdoc.swagger-ui.filter=true`           | Bật bộ lọc để tìm kiếm nhanh các endpoint. |
| `springdoc.swagger-ui.doc-expansion=none`    | Mặc định thu gọn các endpoint trong Swagger UI. |
| `springdoc.swagger-ui.defaultModelsExpandDepth(-1)` | Ẩn phần mô tả chi tiết các model trong Swagger UI. |
| `springdoc.swagger-ui.defaultModelExpandDepth(2)` | Hiển thị model với mức độ chi tiết tối đa là 2 cấp. |
| `springdoc.swagger-ui.display-request-duration=true` | Hiển thị thời gian phản hồi của mỗi request. |
| `springdoc.swagger-ui.disable-swagger-default-url=true` | Ẩn URL mặc định của Swagger khi mở giao diện Swagger UI. |

---

##` **3. Cách truy cập Swagger UI**
- Sau khi khởi động ứng dụng, bạn có thể truy cập giao diện Swagger tại địa chỉ:

```
http://localhost:8080/swagger-ui.html
```

- Tài liệu API theo định dạng JSON sẽ có tại:

```
http://localhost:8080/api-docs
```

---

### **Tóm lại:**
- **Trong `pom.xml`**: Các dependency `springdoc-openapi` cung cấp khả năng tạo tài liệu API.
- **Trong `application.properties`**: Các cấu hình tùy chỉnh giúp Swagger UI dễ sử dụng và phù hợp với nhu cầu thực tế.

Nếu bạn muốn thêm tính năng mở rộng hoặc chỉnh sửa Swagger UI, mình có thể hỗ trợ thêm nhé! 🚀