#!/usr/bin/env bash
# ==============================================================================
# 🚀 Necom Platform - Automated Production Deployment Script to VPS
# ==============================================================================
# Target VPS : 103.195.237.112 (root)
# Remote Path: /var/www/necom
# Domain     : https://necom.vnhat.dev (Reverse Proxy -> 127.0.0.1:8080)
# Direct Port: http://103.195.237.112:8080
# ==============================================================================

set -eo pipefail

# ANSI color codes
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
PURPLE='\033[0;35m'
CYAN='\033[0;36m'
WHITE='\033[1;37m'
BOLD='\033[1m'
NC='\033[0m'

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$ROOT_DIR"

# VPS Configuration Defaults
VPS_HOST="${VPS_HOST:-103.195.237.112}"
VPS_USER="${VPS_USER:-root}"
REMOTE_DIR="${REMOTE_DIR:-/var/www/necom}"
DOMAIN="${DOMAIN:-necom.vnhat.dev}"
CLIENT_PORT="${CLIENT_PORT:-8080}"
SERVER_PORT="${SERVER_PORT:-8085}"

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
 _   _  _____  ____  ___   __  __   ____  _____ ____  _     ______   __
| \ | || ____|/ ___|/ _ \ |  \/  | |  _ \| ____|  _ \| |   / _ \ \ / /
|  \| ||  _| | |   | | | || |\/| | | | | |  _| | |_) | |  | | | \ V / 
| |\  || |___| |___| |_| || |  | | | |_| | |___|  __/| |__| |_| || |  
|_| \_||_____|\____|\___/ |_|  |_| |____/|_____|_|   |_____\___/ |_|  
EOF
    echo -e "${CYAN}   Production Deployment Orchestrator for Necom on VPS${NC}\n"
}

# Resolve Java 11
get_java11_home() {
    if [[ "$OSTYPE" == "darwin"* ]] && command -v /usr/libexec/java_home >/dev/null 2>&1; then
        /usr/libexec/java_home -v 11 2>/dev/null || echo "$JAVA_HOME"
    else
        echo "${JAVA_HOME:-}"
    fi
}

# Test SSH connection
check_ssh() {
    log_info "Kiểm tra kết nối SSH tới ${VPS_USER}@${VPS_HOST}..."
    if ! ssh -o BatchMode=yes -o ConnectTimeout=10 "${VPS_USER}@${VPS_HOST}" "echo 'OK'" >/dev/null 2>&1; then
        log_error "Không thể kết nối SSH tới ${VPS_USER}@${VPS_HOST}."
        log_error "Vui lòng đảm bảo SSH Key đã được cấu hình (ví dụ: ssh-copy-id ${VPS_USER}@${VPS_HOST})."
        exit 1
    fi
    log_success "Kết nối SSH tới VPS thành công!"
}

# Build Spring Boot Backend locally (Cross-platform bytecode JAR)
build_backend() {
    log_info "1/2. Biên dịch Spring Boot backend (Java 11)..."
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

    if [ ! -f "$ROOT_DIR/necom-server/target/necom-0.0.1-SNAPSHOT.jar" ]; then
        log_error "Không tìm thấy file necom-0.0.1-SNAPSHOT.jar sau khi build!"
        exit 1
    fi
    log_success "Đã build thành công necom-server/target/necom-0.0.1-SNAPSHOT.jar!"
}

# Build React CRA Frontend locally
build_frontend() {
    log_info "2/2. Biên dịch React frontend bundle..."
    cd "$ROOT_DIR/necom-client"
    if [ ! -d "node_modules" ]; then
        log_info "Cài đặt dependencies npm..."
        npm install --legacy-peer-deps
    fi
    npm run build
    cd "$ROOT_DIR"

    if [ ! -f "$ROOT_DIR/necom-client/build/index.html" ]; then
        log_error "Không tìm thấy file index.html sau khi build frontend!"
        exit 1
    fi
    log_success "Đã build thành công necom-client/build!"
}

# Synchronize files to VPS
sync_to_vps() {
    log_info "Đồng bộ mã nguồn và artifacts lên VPS (${VPS_USER}@${VPS_HOST}:${REMOTE_DIR})..."

    # Tạo thư mục từ xa nếu chưa có
    ssh "${VPS_USER}@${VPS_HOST}" "mkdir -p ${REMOTE_DIR}/necom-server/target ${REMOTE_DIR}/necom-server/src/main/resources ${REMOTE_DIR}/necom-client/build"

    # 1. Đồng bộ cấu hình gốc
    rsync -avz --progress \
        "$ROOT_DIR/docker-compose.yml" \
        "$ROOT_DIR/run.sh" \
        "$ROOT_DIR/nginx-vps.conf" \
        "${VPS_USER}@${VPS_HOST}:${REMOTE_DIR}/"

    # 2. Xử lý file .env trên VPS (không ghi đè nếu đã có trên VPS)
    if ! ssh "${VPS_USER}@${VPS_HOST}" "[ -f ${REMOTE_DIR}/.env ]"; then
        log_info "Khởi tạo file .env trên VPS từ .env.production..."
        rsync -av "$ROOT_DIR/.env.production" "${VPS_USER}@${VPS_HOST}:${REMOTE_DIR}/.env"
    else
        log_info "File ${REMOTE_DIR}/.env đã tồn tại trên VPS (giữ nguyên cấu hình hiện tại)."
    fi

    # 3. Đồng bộ Backend
    log_info "Đồng bộ Backend JAR, Dockerfile và SQL schema..."
    rsync -avP "$ROOT_DIR/necom-server/target/necom-0.0.1-SNAPSHOT.jar" "${VPS_USER}@${VPS_HOST}:${REMOTE_DIR}/necom-server/target/"
    rsync -av "$ROOT_DIR/necom-server/Dockerfile" "${VPS_USER}@${VPS_HOST}:${REMOTE_DIR}/necom-server/"
    rsync -avz "$ROOT_DIR/necom-server/src/main/resources/"*.sql "${VPS_USER}@${VPS_HOST}:${REMOTE_DIR}/necom-server/src/main/resources/"

    # 4. Đồng bộ Frontend
    log_info "Đồng bộ Frontend static build, Dockerfile và nginx.conf..."
    rsync -av "$ROOT_DIR/necom-client/Dockerfile" "$ROOT_DIR/necom-client/nginx.conf" "${VPS_USER}@${VPS_HOST}:${REMOTE_DIR}/necom-client/"
    rsync -avz --delete "$ROOT_DIR/necom-client/build/" "${VPS_USER}@${VPS_HOST}:${REMOTE_DIR}/necom-client/build/"

    log_success "Đã đồng bộ toàn bộ file lên VPS thành công!"
}

# Configure Host Nginx on VPS and firewall
configure_vps_nginx() {
    log_info "Cấu hình Nginx reverse proxy và UFW firewall trên VPS..."

    ssh "${VPS_USER}@${VPS_HOST}" bash << 'EOF'
set -eo pipefail

# 1. Cho phép port 8080 qua firewall UFW nếu đang bật
if command -v ufw >/dev/null 2>&1; then
    if ufw status | grep -q "Status: active"; then
        echo "Mở port 8080 trên firewall UFW..."
        ufw allow 8080/tcp >/dev/null 2>&1 || true
    fi
fi

# 2. Cài đặt file cấu hình Nginx reverse proxy cho necom.vnhat.dev
if [ -f /var/www/necom/nginx-vps.conf ]; then
    cp /var/www/necom/nginx-vps.conf /etc/nginx/sites-available/necom.vnhat.dev.conf
    ln -sf /etc/nginx/sites-available/necom.vnhat.dev.conf /etc/nginx/sites-enabled/necom.vnhat.dev.conf
    
    # Kiểm tra cú pháp cấu hình Nginx
    if nginx -t >/dev/null 2>&1; then
        systemctl reload nginx
        echo "Nginx reverse proxy cho necom.vnhat.dev đã được nạp thành công!"
    else
        echo "CẢNH BÁO: Kiểm tra cú pháp Nginx thất bại, bỏ qua reload."
    fi
fi
EOF
    log_success "Đã cấu hình host Nginx và Firewall trên VPS!"
}

# Run deployment and container lifecycle on VPS
start_vps_services() {
    log_info "Khởi động và build các container Necom trên VPS..."

    ssh "${VPS_USER}@${VPS_HOST}" bash << 'EOF'
set -eo pipefail
cd /var/www/necom

echo "Đang build Docker images trên VPS..."
docker compose build necom-server necom-client

echo "1/3. Khởi động [necom-database] (MySQL 8.0)..."
docker compose up -d necom-database

echo -n "Đang đợi database sẵn sàng..."
for i in {1..45}; do
    health_status="$(docker inspect --format='{{json .State.Health.Status}}' necom-database 2>/dev/null || echo '""')"
    if [ "$health_status" == '"healthy"' ]; then
        echo " OK!"
        break
    fi
    sleep 2
    echo -n "."
done

echo "2/3. Khởi động [necom-server] (Spring Boot)..."
docker compose up -d necom-server

echo -n "Đang đợi backend server khởi động..."
for i in {1..40}; do
    if curl -s -f -o /dev/null "http://127.0.0.1:8085/api/categories" 2>/dev/null; then
        echo " OK!"
        break
    fi
    sleep 2
    echo -n "."
done

echo "3/3. Khởi động [necom-client] (Nginx + React)..."
docker compose up -d necom-client

echo "Kiểm tra trạng thái container:"
docker compose ps
EOF

    log_success "Các container Necom trên VPS đã được khởi động!"
}

# Setup SSL certificate via Certbot on VPS
setup_ssl() {
    log_info "Kiểm tra và cấp phát chứng chỉ SSL Let's Encrypt cho ${DOMAIN}..."

    ssh "${VPS_USER}@${VPS_HOST}" bash << EOF
if command -v certbot >/dev/null 2>&1; then
    echo "Đang yêu cầu chứng chỉ SSL cho domain ${DOMAIN}..."
    certbot --nginx -d "${DOMAIN}" --non-interactive --agree-tos --redirect --register-unsafely-without-email || {
        echo "⚠ Chưa thể cấp chứng chỉ SSL tự động (có thể do bản ghi DNS ${DOMAIN} chưa trỏ về IP VPS)."
        echo "Sau khi trỏ DNS, bạn có thể chạy lại: ./deploy-vps.sh --ssl"
    }
else
    echo "Certbot chưa được cài đặt trên VPS. Vui lòng cài đặt certbot nếu cần SSL."
fi
EOF
}

# View remote logs
show_remote_logs() {
    local target="${1:-}"
    case "$target" in
        server|be|backend)
            ssh -t "${VPS_USER}@${VPS_HOST}" "cd ${REMOTE_DIR} && docker compose logs -f necom-server"
            ;;
        client|fe|frontend)
            ssh -t "${VPS_USER}@${VPS_HOST}" "cd ${REMOTE_DIR} && docker compose logs -f necom-client"
            ;;
        db|mysql|database)
            ssh -t "${VPS_USER}@${VPS_HOST}" "cd ${REMOTE_DIR} && docker compose logs -f necom-database"
            ;;
        *)
            ssh -t "${VPS_USER}@${VPS_HOST}" "cd ${REMOTE_DIR} && docker compose logs -f"
            ;;
    esac
}

# Check remote status
check_remote_status() {
    print_banner
    log_info "Kiểm tra trạng thái dịch vụ trên VPS (${VPS_HOST})..."
    ssh "${VPS_USER}@${VPS_HOST}" bash << 'EOF'
cd /var/www/necom 2>/dev/null || { echo "Necom chưa được triển khai tại /var/www/necom"; exit 1; }

echo "=== DOCKER CONTAINERS ==="
docker compose ps

echo -e "\n=== HEALTH CHECKS ==="
if curl -s -f -o /dev/null "http://127.0.0.1:8085/api/categories" 2>/dev/null; then
    echo "• Backend (port 8085): ✔ HOẠT ĐỘNG (HTTP 200)"
else
    echo "• Backend (port 8085): ✖ KHÔNG PHẢN HỒI"
fi

if curl -s -f -o /dev/null "http://127.0.0.1:8080" 2>/dev/null; then
    echo "• Frontend (port 8080): ✔ HOẠT ĐỘNG (HTTP 200)"
else
    echo "• Frontend (port 8080): ✖ KHÔNG PHẢN HỒI"
fi

echo -e "\n=== BỘ NHỚ HỆ THỐNG VPS ==="
free -h
EOF
}

# Print summary
print_summary() {
    echo ""
    echo "=========================================================================="
    log_success "${BOLD}TRIỂN KHAI NECOM LÊN VPS THÀNH CÔNG!${NC}"
    echo "=========================================================================="
    echo -e "${WHITE}${BOLD}📍 ĐỊA CHỈ TRUY CẬP HỆ THỐNG:${NC}"
    echo -e "   • ${BOLD}Tên miền chính (Domain):${NC}    ${BLUE}https://${DOMAIN}${NC}  (hoặc ${BLUE}http://${DOMAIN}${NC})"
    echo -e "   • ${BOLD}Truy cập trực tiếp IP:Port:${NC}  ${BLUE}http://${VPS_HOST}:${CLIENT_PORT}${NC}"
    echo -e "   • ${BOLD}Trang Quản trị (Admin):${NC}      ${BLUE}http://${VPS_HOST}:${CLIENT_PORT}/admin${NC}"
    echo -e "   • ${BOLD}Đăng nhập Admin:${NC}             ${BLUE}http://${VPS_HOST}:${CLIENT_PORT}/admin/signin${NC}"
    echo -e "   • ${BOLD}Swagger UI API Docs:${NC}         ${BLUE}http://${VPS_HOST}:${CLIENT_PORT}/swagger-ui/index.html${NC}"
    echo ""
    echo -e "${WHITE}${BOLD}🔑 TÀI KHOẢN MẪU KHỞI TẠO:${NC}"
    echo -e "   • ${BOLD}Quản trị viên (Admin):${NC}  ${YELLOW}admin${NC}     | Mật khẩu: ${YELLOW}admin123${NC}"
    echo -e "   • ${BOLD}Khách hàng (Customer):${NC}  ${YELLOW}customer${NC}  | Mật khẩu: ${YELLOW}admin123${NC}"
    echo "=========================================================================="
    echo -e "📌 ${BOLD}GHI CHÚ VỀ TÊN MIỀN & SSL:${NC}"
    echo -e "   - Thêm bản ghi DNS: ${CYAN}A necom.vnhat.dev -> ${VPS_HOST}${NC}"
    echo -e "   - Sau khi DNS cập nhật, kích hoạt SSL miễn phí bằng lệnh:"
    echo -e "     ${BOLD}./deploy-vps.sh --ssl${NC}"
    echo ""
    echo -e "📌 ${BOLD}CÁC LỆNH HỖ TRỢ:${NC}"
    echo -e "   • Xem log thời gian thực:  ${CYAN}./deploy-vps.sh --logs${NC} (hoặc logs server/client/db)"
    echo -e "   • Kiểm tra trạng thái:     ${CYAN}./deploy-vps.sh --status${NC}"
    echo -e "   • Deploy nhanh (bỏ build): ${CYAN}./deploy-vps.sh --skip-build${NC}"
    echo "=========================================================================="
    echo ""
}

show_help() {
    print_banner
    echo -e "${BOLD}CÁCH SỬ DỤNG:${NC}"
    echo "  ./deploy-vps.sh [tùy chọn]"
    echo ""
    echo -e "${BOLD}CÁC TÙY CHỌN CHÍNH:${NC}"
    echo -e "  ${GREEN}(không tham số)${NC}      Biên dịch đầy đủ (BE + FE), đồng bộ và triển khai lên VPS"
    echo -e "  ${GREEN}--skip-build${NC}         Bỏ qua bước biên dịch cục bộ, deploy ngay các artifact hiện có"
    echo -e "  ${GREEN}--only-be${NC}            Chỉ biên dịch và deploy riêng Backend"
    echo -e "  ${GREEN}--only-fe${NC}            Chỉ biên dịch và deploy riêng Frontend"
    echo -e "  ${GREEN}--ssl${NC}                Cấp phát chứng chỉ SSL Let's Encrypt cho ${DOMAIN}"
    echo -e "  ${GREEN}--status${NC}             Kiểm tra tình trạng container và health check trên VPS"
    echo -e "  ${GREEN}--logs [target]${NC}      Theo dõi log trực tiếp từ VPS (server / client / db / all)"
    echo -e "  ${GREEN}--restart${NC}            Khởi động lại các container Necom trên VPS"
    echo -e "  ${GREEN}--help, -h${NC}           Hiển thị trợ giúp này"
    echo ""
}

# Command dispatcher
MAIN_ACTION="full"

while [[ $# -gt 0 ]]; do
    case "$1" in
        --skip-build)
            MAIN_ACTION="skip-build"
            shift
            ;;
        --only-be)
            MAIN_ACTION="only-be"
            shift
            ;;
        --only-fe)
            MAIN_ACTION="only-fe"
            shift
            ;;
        --ssl)
            MAIN_ACTION="ssl"
            shift
            ;;
        --status)
            check_remote_status
            exit 0
            ;;
        --logs)
            shift
            show_remote_logs "${1:-}"
            exit 0
            ;;
        --restart)
            check_ssh
            ssh "${VPS_USER}@${VPS_HOST}" "cd ${REMOTE_DIR} && docker compose restart"
            log_success "Đã khởi động lại dịch vụ trên VPS!"
            exit 0
            ;;
        -h|--help|help)
            show_help
            exit 0
            ;;
        *)
            log_error "Tham số không hợp lệ: $1"
            show_help
            exit 1
            ;;
    esac
done

# Main Execution Flow
print_banner
check_ssh

case "$MAIN_ACTION" in
    full)
        build_backend
        build_frontend
        sync_to_vps
        configure_vps_nginx
        start_vps_services
        print_summary
        ;;
    skip-build)
        sync_to_vps
        configure_vps_nginx
        start_vps_services
        print_summary
        ;;
    only-be)
        build_backend
        log_info "Đồng bộ Backend lên VPS..."
        rsync -avP "$ROOT_DIR/necom-server/target/necom-0.0.1-SNAPSHOT.jar" "${VPS_USER}@${VPS_HOST}:${REMOTE_DIR}/necom-server/target/"
        ssh "${VPS_USER}@${VPS_HOST}" "cd ${REMOTE_DIR} && docker compose build necom-server && docker compose up -d necom-server"
        log_success "Đã cập nhật Backend thành công!"
        ;;
    only-fe)
        build_frontend
        log_info "Đồng bộ Frontend lên VPS..."
        rsync -avz --delete "$ROOT_DIR/necom-client/build/" "${VPS_USER}@${VPS_HOST}:${REMOTE_DIR}/necom-client/build/"
        rsync -av "$ROOT_DIR/necom-client/nginx.conf" "${VPS_USER}@${VPS_HOST}:${REMOTE_DIR}/necom-client/"
        ssh "${VPS_USER}@${VPS_HOST}" "cd ${REMOTE_DIR} && docker compose build necom-client && docker compose up -d necom-client"
        log_success "Đã cập nhật Frontend thành công!"
        ;;
    ssl)
        setup_ssl
        ;;
esac
