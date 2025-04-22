## **Giải thích code Spring Security OAuth2 **

Code trên là cấu hình **Spring Security** cho một **OAuth2 Resource Server** sử dụng **JWT (JSON Web Token)** để xác thực người dùng. Dưới đây là giải thích từng phần:

## **1. Các annotation chính**
- `@Configuration`: Đánh dấu đây là một class cấu hình.
- `@EnableWebSecurity`: Kích hoạt Spring Security.
- `@EnableMethodSecurity`: Cho phép bảo mật ở mức **method** (ví dụ: `@PreAuthorize` trên controller).
- `@RequiredArgsConstructor`: Dùng **Lombok** để tạo constructor với các dependency được `@Autowired`.

---

## **2. Biến `jwtSignerKey`**
```java
@Value("${jwt.signer-key}")
private String jwtSignerKey;
```
- Lấy giá trị **JWT secret key** từ file cấu hình (`application.properties` hoặc `application.yml`).
- Key này được dùng để giải mã và xác thực **JWT**.

---

### **3. Cấu hình SecurityFilterChain**
```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
    httpSecurity
            .authorizeHttpRequests(request ->
                request.requestMatchers("/**").permitAll()
                        .anyRequest().authenticated()
    );
    httpSecurity
            .oauth2ResourceServer(oauth2 ->
                oauth2.jwt(jwtConfigurer -> jwtConfigurer
                        .decoder(jwtDecoder())
                        .jwtAuthenticationConverter(jwtAuthenticationConverter())
                )
    );
    httpSecurity.csrf(AbstractHttpConfigurer::disable);
    return httpSecurity.build();
}
```
- `requestMatchers("/**").permitAll()` → Mọi request đều **được phép** truy cập.
- `anyRequest().authenticated()` → Các request còn lại **bắt buộc phải xác thực**.
- `.oauth2ResourceServer(oauth2 -> oauth2.jwt(...))` → Cấu hình server sử dụng OAuth2 với **JWT**.
### Mặc định `.oauth2ResourceServer(oauth2 -> oauth2.jwt())` làm gì?**
✅ **1. Tự động lấy token từ `Authorization` header**  
✅ **2. Dùng `JwtDecoder` để giải mã và xác thực JWT**  
✅ **3. Nếu hợp lệ, tạo `Authentication` và lưu vào `SecurityContextHolder`**  
✅ **4. Kiểm tra quyền truy cập dựa trên `@PreAuthorize` hoặc `.authorizeHttpRequests()`**  
✅ **5. Dùng `JwtAuthenticationConverter` để lấy authorities (nếu có)**

- jwtConfigurer =>
- `.decoder(jwtDecoder())` → Dùng **JWT Decoder** để xác thực token.
- `.jwtAuthenticationConverter(jwtAuthenticationConverter())` → Chuyển đổi **JWT thành quyền hạn (Authorities)**.
- `.csrf(AbstractHttpConfigurer::disable)` → **Tắt CSRF**, thường được tắt khi dùng JWT.


### Tất cả `jwtConfigurer` có thể cấu hình**
| **Cấu hình**                        | **Mô tả** |
|-------------------------------------|----------|
| `.decoder(jwtDecoder())`            | Xác định cách giải mã JWT (HS256, RS256, v.v.) |
| `.jwtAuthenticationConverter(jwtAuthenticationConverter())` | Chuyển đổi claims JWT thành quyền hạn trong Spring Security |
| `.validator(jwtValidator())`        | Thêm điều kiện kiểm tra token (issuer, expiration, audience) |
| `.jwtDecoderFactory(jwtDecoderFactory())` | Dùng factory để tạo `JwtDecoder` khi có nhiều nguồn JWT |
| `.jwsAlgorithm(SignatureAlgorithm.RS256)` | Chỉ chấp nhận thuật toán ký cụ thể |

---

### **Lời khuyên**
🔹 Nếu bạn dùng **JWT của OAuth2 với `issuer-uri`**, **chỉ cần**:
```java
httpSecurity.oauth2ResourceServer(oauth2 -> oauth2.jwt());
```
🔹 Nếu bạn có **JWT ký bằng `HMAC` hoặc cần tùy chỉnh roles**, hãy **cấu hình thêm `jwtConfigurer`**:
```java
httpSecurity.oauth2ResourceServer(oauth2 ->
    oauth2.jwt(jwtConfigurer -> jwtConfigurer
        .decoder(jwtDecoder())  // Giải mã token
        .jwtAuthenticationConverter(jwtAuthenticationConverter()) // Lấy roles từ JWT
        .validator(jwtValidator()) // Kiểm tra JWT hợp lệ
    )
);
```
🔹 Nếu ứng dụng của bạn **hỗ trợ nhiều nguồn cấp JWT**, hãy sử dụng **jwtDecoderFactory**.

💡 **Chỉ tùy chỉnh khi thực sự cần thiết để tránh phức tạp không cần thiết!** 🚀

---

## **4. Cấu hình chuyển đổi quyền hạn từ JWT**
```java
@Bean
public JwtAuthenticationConverter jwtAuthenticationConverter() {
    JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
    jwtGrantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");

    JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
    jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);

    return jwtAuthenticationConverter;
}
```
- **Chuyển đổi quyền hạn từ JWT**:
    - `JwtGrantedAuthoritiesConverter` lấy **"authorities"** từ **JWT Claims** và gán tiền tố `"ROLE_"`.
    - `JwtAuthenticationConverter` sử dụng `JwtGrantedAuthoritiesConverter` để tạo **Spring Security Authorities**.

---

## **5. Cấu hình `JwtDecoder`**
```java
@Bean
public JwtDecoder jwtDecoder() {
    SecretKeySpec secretKeySpec = new SecretKeySpec(jwtSignerKey.getBytes(), "HS512");
    return NimbusJwtDecoder
            .withSecretKey(secretKeySpec)
            .macAlgorithm(MacAlgorithm.HS512)
            .build();
}
```
- `SecretKeySpec` tạo **secret key** từ giá trị `jwtSignerKey`.
- `NimbusJwtDecoder` dùng thuật toán **HS512** để xác thực token.

---

## **6. Cấu hình Password Encoder**
```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(10);
}
```
- Dùng **BCryptPasswordEncoder** để mã hóa mật khẩu với **10 rounds**.

---

## **7. Cấu hình SecurityUtils**
```java
@Bean
public SecurityUtils securityUtils(UserRepository userRepository) {
    return new SecurityUtils(userRepository);
}
```
- **Inject `UserRepository` vào `SecurityUtils`** để hỗ trợ các thao tác bảo mật như lấy thông tin người dùng.

---

## **Spring OAuth2 là gì? Khác gì với Spring Security thông thường?**

### **1. Spring OAuth2 là gì?**
Spring Security OAuth2 là **một phần mở rộng** của Spring Security hỗ trợ **xác thực và ủy quyền** theo chuẩn OAuth2.

OAuth2 là một **giao thức mở** cho phép xác thực người dùng mà không cần chia sẻ mật khẩu, thường sử dụng trong các hệ thống như:
- **Google, Facebook, GitHub** (Login bằng OAuth2).
- **Microservices** (Giao tiếp giữa các service bằng token).
- **API Security** (Bảo vệ API bằng JWT tokens).

Spring Security hỗ trợ OAuth2 để xây dựng:
1. **OAuth2 Authorization Server** (Cấp token).
2. **OAuth2 Resource Server** (Xác thực token, như code trên).

---

### **2. Khác biệt giữa Spring Security thông thường và OAuth2**
| **Tính năng** | **Spring Security** | **Spring Security OAuth2** |
|--------------|----------------------|-----------------------------|
| **Xác thực (Authentication)** | Dùng **Username/Password** từ Database | Dùng **Access Token** (JWT, OAuth2) |
| **Ủy quyền (Authorization)** | Phân quyền dựa trên roles (DB, LDAP) | Phân quyền dựa trên scopes, JWT claims |
| **Session Management** | Dùng **Session hoặc Cookie** | Không dùng session, stateless |
| **API Security** | Dùng **Basic Auth, Session Auth** | Dùng **JWT, Bearer Token** |
| **Bảo mật API** | Không chuyên biệt cho API | **Chuyên biệt cho API/Microservices** |
| **Tương thích OAuth2 Provider** | Không hỗ trợ | **Tích hợp OAuth2 Providers (Google, Facebook, GitHub, etc.)** |

---

## **Tóm tắt**
- **Code trên là cấu hình OAuth2 Resource Server** sử dụng **JWT** để xác thực người dùng.
- **Spring Security OAuth2 giúp xác thực bằng Token thay vì Session**.
- **OAuth2 phù hợp với API & Microservices hơn Spring Security truyền thống**.

Bạn có muốn mình hướng dẫn **cách test API với JWT** không? 🚀