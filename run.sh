#!/usr/bin/env bash
# ==============================================================================
# Necom E-Commerce Platform - Automated Sequential Orchestration Script
# ==============================================================================
# Author: Jackie (nhatcoi) & Antigravity
# Description: Tự động khởi chạy, kiểm tra và quản lý tuần tự hệ thống Necom
#              (MySQL 8.0 -> Spring Boot Backend -> React mantine Frontend)
# ==============================================================================

set -eo pipefail

# Color palette & icons
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
PURPLE='\033[0;35m'
CYAN='\033[0;36m'
WHITE='\033[1;37m'
BOLD='\033[1m'
NC='\033[0m' # No Color

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$ROOT_DIR"

log_info() {
    echo -e "${CYAN}ℹ [INFO]${NC} $1"
}

log_success() {
    echo -e "${GREEN}✔ [SUCCESS]${NC} $1"
}

log_warn() {
    echo -e "${YELLOW}⚠ [WARN]${NC} $1"
}

log_error() {
    echo -e "${RED}✖ [ERROR]${NC} $1"
}

print_banner() {
    echo -e "${PURPLE}${BOLD}"
    cat << "EOF"
 _   _  _____  ____  ___   __  __ 
| \ | || ____|/ ___|/ _ \ |  \/  |
|  \| ||  _| | |   | | | || |\/| |
| |\  || |___| |___| |_| || |  | |
|_| \_||_____|\____|\___/ |_|  |_|
EOF
    echo -e "${CYAN}   E-Commerce Platform Orchestrator${NC}\n"
}

# Load environment configuration
load_env() {
    if [ ! -f "$ROOT_DIR/.env" ]; then
        if [ -f "$ROOT_DIR/.env.example" ]; then
            log_warn "Không tìm thấy .env, đang tự động tạo từ .env.example..."
            cp "$ROOT_DIR/.env.example" "$ROOT_DIR/.env"
            log_success "Đã tạo file .env thành công!"
        fi
    fi

    if [ -f "$ROOT_DIR/.env" ]; then
        export $(grep -v '^#' "$ROOT_DIR/.env" | xargs -0 2>/dev/null || grep -v '^#' "$ROOT_DIR/.env" | xargs)
    fi

    MYSQL_PORT="${MYSQL_PORT:-3306}"
    SERVER_PORT="${SERVER_PORT:-8085}"
    CLIENT_PORT="${CLIENT_PORT:-80}"
}

# Check if Docker daemon is running
ensure_docker() {
    log_info "Kiểm tra trạng thái Docker..."
    if ! docker info >/dev/null 2>&1; then
        log_warn "Docker daemon chưa chạy! Đang tự động khởi động Docker..."
        if [[ "$OSTYPE" == "darwin"* ]]; then
            open -a Docker
        elif command -v systemctl >/dev/null 2>&1; then
            sudo systemctl start docker || true
        fi

        log_info "Đang đợi Docker engine sẵn sàng..."
        local count=0
        until docker info >/dev/null 2>&1; do
            sleep 2
            count=$((count + 1))
            if [ $count -ge 30 ]; then
                log_error "Không thể kết nối đến Docker daemon sau 60 giây. Vui lòng bật Docker thủ công."
                exit 1
            fi
            echo -ne "."
        done
        echo ""
    fi
    log_success "Docker engine đã sẵn sàng!"
}

# Resolve Java 11 home if available
get_java11_home() {
    if [[ "$OSTYPE" == "darwin"* ]] && command -v /usr/libexec/java_home >/dev/null 2>&1; then
        /usr/libexec/java_home -v 11 2>/dev/null || echo "$JAVA_HOME"
    else
        echo "${JAVA_HOME:-}"
    fi
}

# Build backend jar if missing or requested
build_backend() {
    local jar_path="$ROOT_DIR/necom-server/target/necom-0.0.1-SNAPSHOT.jar"
    if [ ! -f "$jar_path" ] || [ "${1:-}" == "--force" ]; then
        log_info "Đang build backend Spring Boot (Java 11)..."
        local j11
        j11="$(get_java11_home)"
        if [ -n "$j11" ]; then
            export JAVA_HOME="$j11"
        fi
        
        cd "$ROOT_DIR/necom-server"
        if [ -x "./mvnw" ]; then
            ./mvnw clean package -DskipTests
        else
            mvn clean package -DskipTests
        fi
        cd "$ROOT_DIR"
        log_success "Đã build thành công necom-0.0.1-SNAPSHOT.jar!"
    else
        log_info "Tìm thấy backend jar tại $jar_path. (Bỏ qua build, dùng --build để ép build lại)"
    fi
}

# Build frontend if missing or requested
build_frontend() {
    local build_dir="$ROOT_DIR/necom-client/build"
    if [ ! -d "$build_dir" ] || [ ! -f "$build_dir/index.html" ] || [ "${1:-}" == "--force" ]; then
        log_info "Đang build frontend React CRA..."
        cd "$ROOT_DIR/necom-client"
        if [ ! -d "node_modules" ]; then
            log_info "Đang cài đặt dependencies npm..."
            npm install --legacy-peer-deps
        fi
        npm run build
        cd "$ROOT_DIR"
        log_success "Đã build thành công necom-client/build!"
    else
        log_info "Tìm thấy frontend build tại $build_dir. (Bỏ qua build, dùng --build để ép build lại)"
    fi
}

# Build docker images
build_images() {
    log_info "Đang kiểm tra và build Docker images..."
    docker compose build
    log_success "Docker images đã sẵn sàng!"
}

# Sequential startup function
start_services() {
    ensure_docker
    load_env

    local force_build=false
    if [ "${1:-}" == "--build" ] || [ "${2:-}" == "--build" ]; then
        force_build=true
    fi

    if [ "$force_build" = true ]; then
        build_backend --force
        build_frontend --force
        build_images
    else
        build_backend
        build_frontend
    fi

    echo ""
    log_info "${BOLD}BẮT ĐẦU TRIỂN KHAI TUẦN TỰ HỆ THỐNG NECOM${NC}"
    echo "--------------------------------------------------------"

    # Bước 1: Khởi động MySQL Database
    log_info "1/3. Khởi động [necom-database] (MySQL 8.0)..."
    docker compose up -d necom-database

    log_info "Đang đợi [necom-database] hoàn tất khởi tạo và healthy..."
    local db_ready=false
    for i in {1..60}; do
        local health_status
        health_status="$(docker inspect --format='{{json .State.Health.Status}}' necom-database 2>/dev/null || echo '""')"
        if [ "$health_status" == '"healthy"' ]; then
            db_ready=true
            break
        fi
        sleep 2
        echo -ne "."
    done
    echo ""

    if [ "$db_ready" != true ]; then
        log_error "Database không thể đạt trạng thái healthy sau 120s! Kiểm tra logs bằng: ./run.sh logs db"
        exit 1
    fi
    log_success "Database đã sẵn sàng trên cổng $MYSQL_PORT!"

    # Bước 2: Khởi động Spring Boot Backend
    log_info "2/3. Khởi động [necom-server] (Spring Boot 2.6.7)..."
    docker compose up -d necom-server

    log_info "Đang đợi [necom-server] khởi động và kết nối database..."
    local server_ready=false
    for i in {1..45}; do
        if curl -s -f -o /dev/null "http://localhost:${SERVER_PORT}/api/categories" 2>/dev/null; then
            server_ready=true
            break
        fi
        sleep 2
        echo -ne "."
    done
    echo ""

    if [ "$server_ready" != true ]; then
        log_error "Server không phản hồi sau 90s! Kiểm tra logs bằng: ./run.sh logs server"
        exit 1
    fi
    log_success "Backend đã sẵn sàng trên cổng $SERVER_PORT!"

    # Bước 3: Khởi động React Frontend
    log_info "3/3. Khởi động [necom-client] (Nginx + React 17)..."
    docker compose up -d necom-client

    log_info "Đang kiểm tra kết nối [necom-client]..."
    local client_ready=false
    for i in {1..15}; do
        if curl -s -f -o /dev/null "http://localhost:${CLIENT_PORT}" 2>/dev/null; then
            client_ready=true
            break
        fi
        sleep 1
        echo -ne "."
    done
    echo ""

    if [ "$client_ready" != true ]; then
        log_warn "Client chưa phản hồi ngay lập tức, kiểm tra logs bằng: ./run.sh logs client"
    else
        log_success "Frontend đã sẵn sàng trên cổng $CLIENT_PORT!"
    fi

    echo ""
    echo "========================================================"
    log_success "${BOLD}HỆ THỐNG NECOM ĐÃ ĐƯỢC TRIỂN KHAI THÀNH CÔNG!${NC}"
    echo "========================================================"
    echo -e "${WHITE}${BOLD}📍 CÁC ĐỊA CHỈ TRUY CẬP CHÍNH:${NC}"
    echo -e "   • ${BOLD}Trang mua sắm (Storefront):${NC}  ${BLUE}http://localhost:${CLIENT_PORT}${NC}"
    echo -e "   • ${BOLD}Trang Quản trị (Admin):${NC}      ${BLUE}http://localhost:${CLIENT_PORT}/admin${NC}"
    echo -e "   • ${BOLD}Đăng nhập Admin:${NC}             ${BLUE}http://localhost:${CLIENT_PORT}/admin/signin${NC}"
    echo -e "   • ${BOLD}Backend REST API:${NC}            ${BLUE}http://localhost:${SERVER_PORT}/api${NC}"
    echo -e "   • ${BOLD}Swagger UI Documentation:${NC}    ${BLUE}http://localhost:${SERVER_PORT}/swagger-ui/index.html${NC}"
    echo -e "   • ${BOLD}MySQL Database Host:${NC}         ${BLUE}localhost:${MYSQL_PORT}${NC}"
    echo ""
    echo -e "${WHITE}${BOLD}🔑 TÀI KHOẢN MẪU ĐÃ CẤU HÌNH SẴN:${NC}"
    echo -e "   • ${BOLD}Admin account:${NC}     Username: ${YELLOW}admin${NC}     | Password: ${YELLOW}admin123${NC}"
    echo -e "   • ${BOLD}Customer account:${NC}  Username: ${YELLOW}customer${NC}  | Password: ${YELLOW}admin123${NC}"
    echo "========================================================"
    echo -e "Để theo dõi log thời gian thực: ${CYAN}./run.sh logs${NC}"
    echo -e "Để dừng toàn bộ dịch vụ:        ${CYAN}./run.sh stop${NC}"
    echo ""
}

# Stop all services
stop_services() {
    log_info "Đang dừng toàn bộ dịch vụ Necom..."
    docker compose down
    log_success "Đã dừng và giải phóng tài nguyên thành công!"
}

# Restart all services
restart_services() {
    stop_services
    start_services "$@"
}

# Check real-time status of all components
status_services() {
    ensure_docker
    load_env
    echo -e "\n${BOLD}--- TRẠNG THÁI CÁC DỊCH VỤ NECOM ---${NC}"
    docker compose ps

    echo -e "\n${BOLD}--- KIỂM TRA HEALTH CHECK ENDPOINTS ---${NC}"
    # Check Database
    if docker exec necom-database mysqladmin ping -h localhost -u root -proot >/dev/null 2>&1; then
        echo -e "• [necom-database] (Port $MYSQL_PORT): ${GREEN}✔ KẾT NỐI TỐT (Healthy)${NC}"
    else
        echo -e "• [necom-database] (Port $MYSQL_PORT): ${RED}✖ KHÔNG KẾT NỐI ĐƯỢC${NC}"
    fi

    # Check Backend
    if curl -s -f -o /dev/null "http://localhost:${SERVER_PORT}/api/categories" 2>/dev/null; then
        echo -e "• [necom-server]   (Port $SERVER_PORT): ${GREEN}✔ KẾT NỐI TỐT (HTTP 200)${NC}"
    else
        echo -e "• [necom-server]   (Port $SERVER_PORT): ${RED}✖ KHÔNG PHẢN HỒI${NC}"
    fi

    # Check Frontend
    if curl -s -f -o /dev/null "http://localhost:${CLIENT_PORT}" 2>/dev/null; then
        echo -e "• [necom-client]   (Port $CLIENT_PORT): ${GREEN}✔ KẾT NỐI TỐT (HTTP 200)${NC}"
    else
        echo -e "• [necom-client]   (Port $CLIENT_PORT): ${RED}✖ KHÔNG PHẢN HỒI${NC}"
    fi
    echo ""
}

# Show logs
show_logs() {
    local target="${1:-}"
    case "$target" in
        server|backend|be)
            docker compose logs -f necom-server
            ;;
        client|frontend|fe)
            docker compose logs -f necom-client
            ;;
        db|database|mysql)
            docker compose logs -f necom-database
            ;;
        *)
            docker compose logs -f
            ;;
    esac
}

# Clean reset database
reset_database() {
    log_warn "CẢNH BÁO: Thao tác này sẽ xoá toàn bộ dữ liệu trong database và khởi tạo lại từ đầu!"
    read -r -p "Bạn có chắc chắn muốn reset lại database không? [y/N]: " confirm
    if [[ "$confirm" =~ ^[Yy]$ ]]; then
        log_info "Đang dừng dịch vụ và xóa volume mysql-data..."
        docker compose down -v
        log_success "Đã xóa database volume."
        log_info "Đang khởi tạo lại database với dữ liệu chuẩn..."
        docker compose up -d necom-database
        log_info "Đang chạy các script 01-address.sql, 02-schema.sql, 03-data.sql..."
        
        for i in {1..60}; do
            local health_status
            health_status="$(docker inspect --format='{{json .State.Health.Status}}' necom-database 2>/dev/null || echo '""')"
            if [ "$health_status" == '"healthy"' ]; then
                break
            fi
            sleep 2
            echo -ne "."
        done
        echo ""
        log_success "Database đã được reset và nạp dữ liệu hoàn chỉnh!"
    else
        log_info "Đã huỷ thao tác reset."
    fi
}

show_help() {
    print_banner
    echo -e "${BOLD}CÁCH SỬ DỤNG:${NC}"
    echo "  ./run.sh [lệnh] [tùy chọn]"
    echo ""
    echo -e "${BOLD}CÁC LỆNH KHẢ DỤNG:${NC}"
    echo -e "  ${GREEN}start, up${NC}          Khởi chạy tuần tự toàn bộ hệ thống (Mặc định)"
    echo -e "  ${GREEN}stop, down${NC}         Dừng và dọn dẹp các container"
    echo -e "  ${GREEN}restart${NC}            Khởi động lại tuần tự toàn bộ hệ thống"
    echo -e "  ${GREEN}status, ps${NC}         Xem trạng thái hoạt động và healthcheck của các dịch vụ"
    echo -e "  ${GREEN}logs [dịch vụ]${NC}     Xem log thời gian thực (ví dụ: ./run.sh logs server)"
    echo -e "  ${GREEN}build${NC}              Re-build backend JAR, frontend bundle và Docker images"
    echo -e "  ${GREEN}reset-db${NC}           Reset volume database và tái khởi tạo dữ liệu mẫu sạch"
    echo -e "  ${GREEN}help, -h${NC}           Hiển thị hướng dẫn này"
    echo ""
    echo -e "${BOLD}TÙY CHỌN:${NC}"
    echo -e "  ${CYAN}--build${NC}            Bắt buộc re-build code trước khi khởi chạy (áp dụng cho start/restart)"
    echo ""
    echo -e "${BOLD}VÍ DỤ:${NC}"
    echo "  ./run.sh               # Chạy tuần tự hệ thống"
    echo "  ./run.sh start --build # Build lại code và khởi chạy tuần tự"
    echo "  ./run.sh logs server   # Xem log backend Spring Boot"
    echo "  ./run.sh status        # Kiểm tra sức khỏe các dịch vụ"
    echo ""
}

# Main command dispatcher
COMMAND="${1:-start}"
shift || true

case "$COMMAND" in
    start|up)
        print_banner
        start_services "$@"
        ;;
    stop|down)
        stop_services
        ;;
    restart)
        print_banner
        restart_services "$@"
        ;;
    status|ps)
        status_services
        ;;
    logs|log)
        show_logs "$@"
        ;;
    build)
        print_banner
        build_backend --force
        build_frontend --force
        build_images
        log_success "Build toàn bộ hệ thống hoàn tất!"
        ;;
    reset-db)
        reset_database
        ;;
    help|-h|--help)
        show_help
        ;;
    *)
        log_error "Lệnh không hợp lệ: $COMMAND"
        show_help
        exit 1
        ;;
esac
