Summarizing the backend tech stack from pom.xml and project files.


[2 tools called]

## 🖥️ Backend tech stack — Necom

### 1. Core framework
- Spring Boot 2.6.7 (Java 11)
- Spring MVC, Spring Data JPA, Spring Security
- WebFlux, WebSocket, Actuator

### 2. Database & ORM
- MySQL 8.0 (JDBC, UTF-8 support)
- Hibernate (Dialect: MySQL5)
- JPA with custom converters
- JdbcTemplate/WebFlux

### 3. Security
- Spring Security with JWT (jjwt 0.9.1)
- BCryptPasswordEncoder
- Stateless sessions, role-based access (RSQL over JPA queries)

### 4. Mapping & code generation
- MapStruct 1.4.2.Final (annotation-based mapping)
- Lombok 1.18.24 (getters, setters, builders)
- FreeMarker (email templates)

### 5. Query & filtering
- RSQL-JPA Spring Boot Starter 5.0.19 (dynamic filtering and query support)

### 6. API & docs
- SpringDoc OpenAPI UI 1.6.8 (Swagger)
- REST controllers and Spring Web functions
- Actuator endpoints

### 7. External integrations
- PayPal (v2, sandbox)
- Giao Hàng Nhanh
- SMTP (Gmail)

### 8. Communication & async
- WebSocket (SockJS, STOMP)
- Mail
- Notifications

### 9. Testing
- Spring Boot Test
- JUnit, Spring Security Test
- MockMvc, Maven Surefire

### 10. Build & deployment
- Maven profiles (dev, ci)
- Spring Boot Maven Plugin
- Docker
- DevTools (hot reload)

## Architecture notes
- Generic CRUD with `GenericController`, `GenericService`, and `GenericMapper`
- RSQL-driven filtering/search
- JWT-based stateless auth
- Repository/data layers with Spring Data JPA

## Dependencies summary

| Category | Technology | Version | Purpose |
|----------|------------|---------|---------|
| **Framework** | Spring Boot | 2.6.7 | Core framework |
| **Database** | MySQL Connector | Latest | Database connection |
| **ORM** | Hibernate + JPA | Auto | Object-relational mapping |
| **Mapping** | MapStruct | 1.4.2 | DTO conversion |
| **Security** | Spring Security | Auto | Authentication & Authorization |
| **JWT** | JJWT | 0.9.1 | Token management |
| **Query** | RSQL-JPA | 5.0.19 | Dynamic query building |
| **API Docs** | SpringDoc OpenAPI | 1.6.8 | API documentation |
| **WebSocket** | Spring WebSocket | Auto | Real-time communication |
| **Templates** | FreeMarker | Auto | Email templates |
| **Build Tool** | Maven | - | Dependency management |
| **Code Quality** | Lombok | 1.18.24 | Boilerplate reduction |

Combines Spring ecosystem, JPA/Hibernate, MapStruct/Lombok, JWT, RSQL, and third-party integrations for e-commerce.