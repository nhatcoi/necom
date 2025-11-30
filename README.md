# Necom - E-commerce Platform

## Overview
Necom (Jackie Shop) is a full-stack e-commerce platform built with Spring Boot backend and React frontend. It provides comprehensive features for managing products, orders, inventory, customers, and business operations.

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


## Author
- [nhatcoi aka jackie](https://github.com/nhatcoi)