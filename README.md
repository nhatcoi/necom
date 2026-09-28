# NECOM - Needs eCommerce Platform

## Overview
**NECOM** (**Needs eCommerce**) is a full-stack e-commerce platform built with Spring Boot backend and React frontend. It provides comprehensive features for managing products, orders, inventory, customers, and business operations.

## Tech Stack

### Backend
- **Framework**: Spring Boot
- **Language**: Java
- **Database**: MySQL
- **Security**: Spring Security with JWT authentication
- **API Documentation**: SpringDoc OpenAPI
- **Real-time**: WebSocket for chat and notifications
- **Mapping**: MapStruct
- **Query**: RSQL for dynamic filtering

### Frontend
- **Framework**: React 17 with TypeScript
- **UI Library**: Mantine UI
- **State Management**: Zustand, React Query
- **Routing**: React Router DOM
- **Build Tool**: Create React App

## Features

### Product Management
- Product catalog with variants, specifications, and properties
- Category, brand, supplier, and tag management
- Product images and media handling
- Inventory tracking and limits

### Inventory Management
- Warehouse management
- Purchase orders and variants
- Stock dockets (in/out)
- Inventory transfers between warehouses
- Stock counting and adjustments
- Storage location tracking

### Order Management
- Order processing and fulfillment
- Order variants tracking
- Order cancellation reasons
- Waybill and shipping management
- Order resources tracking

### Customer & User Management
- User authentication and authorization
- Role-based access control (RBAC)
- Customer groups, status, and resources
- Employee management with offices, departments, job titles
- Address management (provinces, districts, wards)

### E-commerce Features
- Shopping cart
- Wishlist
- Product reviews and ratings
- Preorders
- Promotions and vouchers
- Payment methods
- Reward system and loyalty points

### Communication
- Real-time chat with WebSocket
- Notification system
- Email integration

### Administration
- Admin dashboard for managing all entities
- Statistics and reporting
- Image upload and management

## Project Structure
- `necom-server/` - Spring Boot backend application
- `necom-client/` - React frontend application
- `docker-compose.yml` - Docker containerization setup
- `run.sh` - Automated sequential orchestration script

## Khởi Chạy Nhanh (Quick Start)

Dự án cung cấp script điều phối tuần tự `run.sh` tự động chuẩn bị môi trường, kiểm tra Docker và khởi chạy hệ thống theo đúng thứ tự phụ thuộc:

```bash
# 1. Khởi chạy tuần tự toàn bộ hệ thống
./run.sh

# 2. Hoặc build lại mã nguồn và khởi chạy
./run.sh start --build

# 3. Kiểm tra trạng thái và sức khỏe các dịch vụ
./run.sh status

# 4. Xem logs thời gian thực
./run.sh logs        # Xem tất cả
./run.sh logs server # Xem riêng backend Spring Boot
./run.sh logs client # Xem riêng frontend
./run.sh logs db     # Xem riêng MySQL

# 5. Dừng hệ thống
./run.sh stop
```

### Các Địa Chỉ Truy Cập (Access URLs)

| Dịch vụ | URL | Mô tả |
|---|---|---|
| **Storefront** | [http://localhost](http://localhost) | Giao diện mua sắm khách hàng (React) |
| **Admin Portal** | [http://localhost/admin](http://localhost/admin) | Trang quản trị hệ thống |
| **Admin Login** | [http://localhost/admin/signin](http://localhost/admin/signin) | Đăng nhập tài khoản quản trị |
| **Backend API** | [http://localhost:8085/api](http://localhost:8085/api) | Spring Boot REST API |
| **Swagger UI** | [http://localhost:8085/swagger-ui/index.html](http://localhost:8085/swagger-ui/index.html) | Tài liệu OpenAPI tương tác |
| **MySQL DB** | `localhost:3306` | Database (User: `necom`, Pass: `necom`, DB: `necom`) |

### Tài Khoản Mẫu (Default Credentials)

- **Quản trị viên (Admin):** `admin` / `admin123`
- **Khách hàng (Customer):** `customer` / `admin123`

## Author
- [nhatcoi aka jackie](https://github.com/nhatcoi)