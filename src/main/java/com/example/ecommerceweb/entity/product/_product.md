
1. **`cascade = CascadeType.ALL`**
2. **`orphanRemoval = true`**

---

## **1️⃣ `cascade = CascadeType.ALL` là gì?**
`CascadeType.ALL` có nghĩa là **mọi thao tác** (Create, Read, Update, Delete) trên entity cha (`@OneToMany`, `@ManyToOne`, ...) cũng sẽ **tự động áp dụng** cho entity con.

### **Ví dụ:**
Giả sử có quan hệ **1 sản phẩm có nhiều hình ảnh (`Product` - `ProductImage`)**:

```java
@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<ProductImage> images;
}
```

```java
@Entity
@Table(name = "product_images")
public class ProductImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
}
```

### **Kết quả khi sử dụng `CascadeType.ALL`**
| Hành động trên `Product` | Ảnh hưởng đến `ProductImage` |
|-------------------|--------------------------------|
| **Lưu (`save`)** `Product` mới có danh sách ảnh | Tất cả `ProductImage` trong danh sách sẽ được lưu tự động. |
| **Cập nhật (`update`)** `Product` và danh sách ảnh | Nếu danh sách ảnh thay đổi, các ảnh mới sẽ được lưu, ảnh cũ sẽ được cập nhật. |
| **Xóa (`delete`)** `Product` | Tất cả ảnh trong danh sách cũng sẽ bị xóa theo. |

📌 **Lưu ý:** Nếu không có `cascade = CascadeType.ALL`, bạn phải **tự lưu hoặc xóa từng `ProductImage` thủ công**.

---

## **2️⃣ `orphanRemoval = true` là gì?**
`orphanRemoval = true` đảm bảo rằng **nếu một entity con không còn được tham chiếu từ entity cha**, thì nó sẽ **bị xóa khỏi database**.

📌 **Sự khác biệt so với `CascadeType.REMOVE`**:
- `CascadeType.REMOVE` chỉ xóa **toàn bộ danh sách con khi xóa cha**.
- `orphanRemoval = true` **tự động xóa từng phần tử con** nếu nó bị xóa khỏi danh sách của cha.

### **Ví dụ:**
```java
@OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
private List<ProductImage> images;
```
🔹 **Trường hợp 1: Xóa `Product`**
```java
productRepository.delete(product);
```
➡ **Kết quả**: Toàn bộ `ProductImage` bị xóa theo.

🔹 **Trường hợp 2: Xóa một ảnh khỏi danh sách**
```java
product.getImages().remove(0);
productRepository.save(product);
```
➡ **Kết quả**: Ảnh bị xóa khỏi danh sách cũng **bị xóa khỏi database**.

---

## **🚀 Khi nào nên dùng `CascadeType.ALL` và `orphanRemoval = true`?**
| Trường hợp | Sử dụng `CascadeType.ALL`? | Sử dụng `orphanRemoval = true`? |
|---------------------------|-----------------|--------------------|
| **Quan hệ cha - con mạnh** (VD: `Order - OrderItem`) | ✅ Có | ✅ Có |
| **Quan hệ tham chiếu yếu** (VD: `User - Role`) | ❌ Không nên | ❌ Không nên |
| **Tự động xóa dữ liệu con nếu không còn được tham chiếu** | ✅ Có | ✅ Có |
| **Dữ liệu con vẫn cần tồn tại sau khi xóa cha** | ❌ Không nên | ❌ Không nên |

---

## **🛠 Kết luận**
- `CascadeType.ALL` giúp tự động **lưu/xóa entity con** khi thao tác với entity cha.
- `orphanRemoval = true` giúp tự động **xóa entity con** nếu nó bị loại bỏ khỏi danh sách của cha.
- **Chỉ nên sử dụng khi quan hệ cha - con là chặt chẽ** (1 sản phẩm không còn thì ảnh của nó cũng nên bị xóa).

🔹 Bạn có muốn ví dụ thực tế hơn với **Service & Repository** không? 🚀




