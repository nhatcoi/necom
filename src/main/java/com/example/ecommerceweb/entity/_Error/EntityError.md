Dưới đây là bản viết lại dễ đọc hơn về cách khắc phục hai lỗi trong Spring Boot khi làm việc với JPA và Hibernate.

---

## **1. Lỗi: `scale has no meaning for SQL floating point types`**
**📌 Nguyên nhân:**
- Lỗi này xảy ra khi bạn sử dụng `@Column(scale = x)` với kiểu dữ liệu `FLOAT`, `REAL`, hoặc `DOUBLE`.
- Tham số `scale` chỉ có ý nghĩa với kiểu số thập phân `DECIMAL` hoặc `NUMERIC`.

**🛠 Cách khắc phục:**
1. Mở terminal và chạy lệnh sau để tìm tất cả các cột có `scale`:
   ```sh
   grep -r "@Column.*scale" src/
   ```
2. Nếu tìm thấy dòng như sau:
   ```java
   @Column(name = "price", precision = 10, scale = 2) // ❌ Lỗi
   private Double price;
   ```
   **Hãy xóa `scale` đi**, vì `Double` không hỗ trợ `scale`:
   ```java
   @Column(name = "price", precision = 10) // ✅ Sửa lại
   private Double price;
   ```
3. Lưu lại và chạy lại ứng dụng.

---

## **2. Lỗi: `Error creating bean with name 'entityManagerFactory': Product`**
**📌 Nguyên nhân:**
- Lỗi thường do ánh xạ sai trong class `Product`.
- Hai vấn đề chính:
    - **Quan hệ `OneToMany` thiếu `cascade` và `fetch`** → có thể gây lỗi khi Hibernate khởi tạo Entity Manager.
    - **Sai tên biến trong ánh xạ `@ManyToOne`** (ví dụ: dùng `categoryId` thay vì `category`).

**🛠 Cách khắc phục:**

- **Sửa lại quan hệ `OneToMany` để thêm `cascade` và `fetch`:**
  ```java
  @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<ProductSpecification> specifications;

  @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<ProductAttribute> attributes;
  ```
- **Sửa lại quan hệ `ManyToOne` với `Category`:**
  ```java
  @ManyToOne
  @JoinColumn(name = "category_id", nullable = false)
  private Category category;  // ✅ Đổi từ categoryId -> category
  ```

### ✨ **Bản sửa hoàn chỉnh của class `Product`**
```java
package com.example.ecommerceweb.entity.product;

import com.example.ecommerceweb.entity.BaseEntity;
import com.example.ecommerceweb.entity.Category;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "products")
public class Product extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "price", nullable = false)  // ✅ Xóa scale để tránh lỗi
    private Float price;

    @Column(name = "thumbnail")
    private String thumbnail;

    @Column(name = "description")
    private String description;

    @Column(name = "quantity_in_stock")
    private Integer quantityInStock;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;  // ✅ Sửa tên từ categoryId -> category

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ProductSpecification> specifications;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ProductAttribute> attributes;
}
```
---

🔹 **Sau khi sửa lỗi, hãy thử chạy lại ứng dụng**. Nếu vẫn còn lỗi, bạn có thể gửi lại stack trace để kiểm tra tiếp nhé! 🚀