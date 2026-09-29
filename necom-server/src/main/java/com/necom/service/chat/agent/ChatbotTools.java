package com.necom.service.chat.agent;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.necom.entity.cart.Cart;
import com.necom.entity.client.Wish;
import com.necom.entity.order.Order;
import com.necom.entity.product.Product;
import com.necom.entity.product.Variant;
import com.necom.entity.review.Review;
import com.necom.entity.waybill.Waybill;
import com.necom.entity.waybill.WaybillLog;
import com.necom.repository.cart.CartRepository;
import com.necom.repository.client.WishRepository;
import com.necom.repository.inventory.DocketVariantRepository;
import com.necom.repository.order.OrderRepository;
import com.necom.repository.product.ProductRepository;
import com.necom.repository.review.ReviewRepository;
import com.necom.repository.reward.RewardLogRepository;
import com.necom.repository.waybill.WaybillRepository;
import com.necom.service.chat.ChatService;
import com.necom.service.chat.search.KnowledgeIndex;
import com.necom.service.chat.search.ProductSearchIndex;
import com.necom.service.chat.search.ProductSearchIndex.ProductDoc;
import com.necom.utils.InventoryUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Bộ tool cho trợ lý AI (OpenAI function calling). Mọi tool dữ liệu cá nhân lấy khách từ AgentContext
 * (chủ phòng chat đã xác thực), không bao giờ từ tham số do LLM truyền vào.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ChatbotTools {

    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm").withZone(ZoneId.of("Asia/Ho_Chi_Minh"));
    private static final Map<String, Integer> ORDER_STATUS_FILTER = Map.of(
            "new", 1, "processing", 2, "shipping", 3, "delivered", 4, "cancelled", 5);
    private static final Map<Integer, String> WAYBILL_STATUS = Map.of(
            1, "Đợi lấy hàng", 2, "Đang giao", 3, "Đã giao", 4, "Hủy / giao thất bại");

    private final ProductSearchIndex searchIndex;
    private final KnowledgeIndex knowledgeIndex;
    private final ProductRepository productRepository;
    private final DocketVariantRepository docketVariantRepository;
    private final ReviewRepository reviewRepository;
    private final OrderRepository orderRepository;
    private final WaybillRepository waybillRepository;
    private final WishRepository wishRepository;
    private final CartRepository cartRepository;
    private final RewardLogRepository rewardLogRepository;
    private final ChatService chatService;
    private final TransactionTemplate transactionTemplate;
    private final ObjectMapper objectMapper;

    // ================= Khai báo tool =================

    public ArrayNode definitions() {
        ArrayNode tools = objectMapper.createArrayNode();
        ArrayNode categoryEnum = objectMapper.createArrayNode();
        searchIndex.categories().keySet().forEach(categoryEnum::add);

        ObjectNode search = function(tools, "search_products",
                "Tìm sản phẩm nội thất & đời sống theo nhu cầu (ngữ nghĩa + từ khóa). Dùng cho mọi câu hỏi tư vấn, gợi ý, so sánh, tìm theo giá/phòng/phong cách.");
        ObjectNode sp = properties(search);
        string(sp, "query", "Nhu cầu của khách bằng tiếng Việt, càng cụ thể càng tốt (loại sản phẩm, chất liệu, màu, phòng, phong cách, kích thước).");
        ObjectNode category = string(sp, "category", "Slug danh mục nếu khách nói rõ loại hàng.");
        category.set("enum", categoryEnum);
        number(sp, "min_price", "Giá tối thiểu (VND).");
        number(sp, "max_price", "Giá tối đa (VND).");
        sp.putObject("in_stock_only").put("type", "boolean").put("description", "Chỉ lấy hàng còn bán được (mặc định true).");
        ObjectNode sort = string(sp, "sort", "Thứ tự: relevance (mặc định), price_asc, price_desc, rating.");
        sort.putArray("enum").add("relevance").add("price_asc").add("price_desc").add("rating");
        sp.putObject("limit").put("type", "integer").put("description", "Số sản phẩm (1-8, mặc định 6).");
        required(search, "query");

        ObjectNode detail = function(tools, "get_product_detail",
                "Chi tiết một sản phẩm: các phân loại (màu/size) kèm giá và tồn kho, thông số, mô tả, điểm đánh giá, vài nhận xét gần đây.");
        properties(detail).putObject("product_id").put("type", "integer");
        required(detail, "product_id");

        ObjectNode similar = function(tools, "similar_products", "Sản phẩm tương tự một sản phẩm (cùng phong cách/công dụng) để gợi ý thay thế hoặc phối cùng.");
        ObjectNode simp = properties(similar);
        simp.putObject("product_id").put("type", "integer");
        simp.putObject("limit").put("type", "integer");
        required(similar, "product_id");

        ObjectNode policy = function(tools, "search_store_policies",
                "Tra chính sách & thông tin cửa hàng: giao hàng, phí ship, đổi trả, bảo hành, thanh toán, điểm thưởng, liên hệ, bảo mật.");
        string(properties(policy), "query", "Câu hỏi của khách.");
        required(policy, "query");

        ObjectNode orders = function(tools, "get_my_orders", "Danh sách đơn hàng gần đây của khách đang chat.");
        ObjectNode op = properties(orders);
        ObjectNode status = string(op, "status", "Lọc theo trạng thái.");
        status.putArray("enum").add("new").add("processing").add("shipping").add("delivered").add("cancelled");
        op.putObject("limit").put("type", "integer");

        ObjectNode track = function(tools, "track_order", "Theo dõi hành trình vận chuyển (GHN) và chi tiết một đơn của khách theo mã đơn.");
        string(properties(track), "order_code", "Mã đơn hàng.");
        required(track, "order_code");

        function(tools, "get_my_account", "Thông tin tài khoản của khách: tên, điểm thưởng, số đơn đã đặt.");
        function(tools, "get_my_wishlist", "Sản phẩm khách đã lưu yêu thích.");
        function(tools, "get_my_cart", "Sản phẩm đang có trong giỏ hàng của khách.");

        ObjectNode human = function(tools, "request_human_agent",
                "Chuyển cuộc trò chuyện cho tư vấn viên. Dùng khi khách muốn gặp người thật, khiếu nại, cần đổi trả/hoàn tiền cho đơn cụ thể, hoặc vấn đề ngoài khả năng của bạn.");
        string(properties(human), "reason", "Tóm tắt ngắn vấn đề để tư vấn viên nắm ngay.");
        required(human, "reason");
        return tools;
    }

    // ================= Thực thi =================

    public String execute(String name, JsonNode args, AgentContext ctx) {
        // Giới hạn cứng để mỗi lượt không kéo dài: kết quả tìm kiếm đã đủ để trả lời
        if ((name.equals("search_products") || name.equals("get_product_detail") || name.equals("similar_products"))
                && ctx.exceeded(name, 2)) {
            return "{\"note\":\"Đã đủ dữ liệu cho lượt này. Hãy trả lời khách ngay bằng kết quả đã có.\"}";
        }
        try {
            switch (name) {
                case "search_products":
                    return searchProducts(args, ctx);
                case "get_product_detail":
                    return transactionTemplate.execute(s -> productDetail(args.path("product_id").asLong(), ctx));
                case "similar_products":
                    return similarProducts(args, ctx);
                case "search_store_policies":
                    return policies(args.path("query").asText(""));
                case "get_my_orders":
                    return transactionTemplate.execute(s -> myOrders(args, ctx));
                case "track_order":
                    return transactionTemplate.execute(s -> trackOrder(args.path("order_code").asText(""), ctx));
                case "get_my_account":
                    return transactionTemplate.execute(s -> myAccount(ctx));
                case "get_my_wishlist":
                    return transactionTemplate.execute(s -> myWishlist(ctx));
                case "get_my_cart":
                    return transactionTemplate.execute(s -> myCart(ctx));
                case "request_human_agent":
                    if (!ChatService.needsHumanSupport(ctx.getLastCustomerMessage() == null ? "" : ctx.getLastCustomerMessage())) {
                        return "{\"ok\":false,\"note\":\"Không chuyển: khách chưa yêu cầu gặp người hay khiếu nại. "
                                + "Hãy tiếp tục tư vấn (gợi ý sản phẩm gần nhất, hỏi thêm nhu cầu) và thêm quickReply 'Gặp tư vấn viên' để khách tự chọn.\"}";
                    }
                    ctx.requestHandoff(args.path("reason").asText(""));
                    return "{\"ok\":true,\"note\":\"Đã chuyển tư vấn viên. Báo khách đang kết nối, không hỏi thêm.\"}";
                default:
                    return "{\"error\":\"Tool không tồn tại\"}";
            }
        } catch (Exception e) {
            log.warn("Tool {} failed: {}", name, e.getMessage());
            return "{\"error\":\"Không lấy được dữ liệu, hãy xin lỗi khách và đề nghị gặp tư vấn viên nếu cần.\"}";
        }
    }

    private String searchProducts(JsonNode args, AgentContext ctx) {
        List<String> categories = args.hasNonNull("category") ? List.of(args.get("category").asText()) : List.of();
        ProductSearchIndex.SearchResult result = searchIndex.search(ProductSearchIndex.SearchQuery.builder()
                .text(args.path("query").asText(""))
                .categorySlugs(categories)
                .minPrice(args.hasNonNull("min_price") ? args.get("min_price").asDouble() : null)
                .maxPrice(args.hasNonNull("max_price") ? args.get("max_price").asDouble() : null)
                .inStockOnly(!args.has("in_stock_only") || args.get("in_stock_only").asBoolean(true))
                .sort(args.path("sort").asText("relevance"))
                .limit(Math.max(1, Math.min(8, args.path("limit").asInt(6))))
                .build());
        ObjectNode out = objectMapper.createObjectNode();
        out.put("matched_candidates", result.getCandidateCount());
        ArrayNode items = out.putArray("products");
        result.getProducts().forEach(doc -> items.add(productSummary(doc, ctx)));
        if (result.getProducts().isEmpty()) {
            out.put("note", "Không có sản phẩm khớp. Gợi ý nới điều kiện (giá, danh mục) hoặc hỏi thêm nhu cầu.");
        }
        return out.toString();
    }

    private String similarProducts(JsonNode args, AgentContext ctx) {
        ArrayNode items = objectMapper.createArrayNode();
        searchIndex.similar(args.path("product_id").asLong(), Math.max(1, Math.min(6, args.path("limit").asInt(4))))
                .forEach(doc -> items.add(productSummary(doc, ctx)));
        ObjectNode out = objectMapper.createObjectNode();
        out.set("products", items);
        return out.toString();
    }

    private ObjectNode productSummary(ProductDoc doc, AgentContext ctx) {
        ctx.allowProduct(doc);
        ObjectNode node = objectMapper.createObjectNode();
        node.put("id", doc.getId());
        node.put("name", doc.getName());
        node.put("category", doc.getCategory());
        if (doc.getBrand() != null) {
            node.put("brand", doc.getBrand());
        }
        node.put("price", priceText(doc.getMinPrice(), doc.getMaxPrice()));
        node.put("in_stock", doc.getStock());
        if (doc.getReviewCount() > 0) {
            node.put("rating", Math.round(doc.getRating() * 10) / 10.0);
            node.put("reviews", doc.getReviewCount());
        }
        node.put("summary", truncate(doc.getShortDescription(), 160));
        if (doc.getAttributes() != null && !doc.getAttributes().isEmpty()) {
            node.put("attributes", truncate(doc.getAttributes(), 220));
        }
        return node;
    }

    private String productDetail(long productId, AgentContext ctx) {
        Product product = productRepository.findById(productId).orElse(null);
        if (product == null || !Integer.valueOf(1).equals(product.getStatus())) {
            return "{\"error\":\"Không tìm thấy sản phẩm\"}";
        }
        ProductDoc doc = searchIndex.get(productId);
        if (doc != null) {
            ctx.allowProduct(doc);
        }
        ObjectNode out = objectMapper.createObjectNode();
        out.put("id", product.getId());
        out.put("name", product.getName());
        out.put("url", "/product/" + product.getSlug());
        out.put("description", truncate(product.getDescription(), 700));
        if (product.getSpecifications() != null) {
            ArrayNode specs = out.putArray("specifications");
            product.getSpecifications().path("content").forEach(s -> specs.add(s.path("name").asText() + ": " + s.path("value").asText()));
        }
        ArrayNode variants = out.putArray("variants");
        for (Variant variant : product.getVariants()) {
            if (!Integer.valueOf(1).equals(variant.getStatus())) {
                continue;
            }
            ObjectNode v = variants.addObject();
            List<String> labels = new ArrayList<>();
            if (variant.getProperties() != null) {
                variant.getProperties().path("content").forEach(p -> labels.add(p.path("name").asText() + " " + p.path("value").asText()));
            }
            v.put("variant", labels.isEmpty() ? variant.getSku() : String.join(", ", labels));
            v.put("price", Math.round(variant.getPrice()));
            v.put("in_stock", InventoryUtils.calculateInventoryIndices(docketVariantRepository.findByVariantId(variant.getId())).get("canBeSold"));
        }
        out.put("rating", reviewRepository.findAverageRatingScoreByProductId(productId));
        out.put("review_count", reviewRepository.countByProductId(productId));
        ArrayNode reviews = out.putArray("recent_reviews");
        product.getReviews().stream()
                .sorted(Comparator.comparing(Review::getCreatedAt).reversed())
                .limit(3)
                .forEach(r -> reviews.add(r.getRatingScore() + "★ " + truncate(r.getContent(), 160)));
        return out.toString();
    }

    private String policies(String query) {
        ArrayNode chunks = objectMapper.createArrayNode();
        knowledgeIndex.search(query, 3).forEach(c -> chunks.addObject().put("section", c.getTitle()).put("content", c.getContent()));
        ObjectNode out = objectMapper.createObjectNode();
        out.set("sections", chunks);
        return out.toString();
    }

    private String myOrders(JsonNode args, AgentContext ctx) {
        String filter = args.hasNonNull("status") && ORDER_STATUS_FILTER.containsKey(args.get("status").asText())
                ? "status==" + ORDER_STATUS_FILTER.get(args.get("status").asText())
                : null;
        int limit = Math.max(1, Math.min(10, args.path("limit").asInt(5)));
        List<Order> orders = orderRepository.findAllByUsername(ctx.getUsername(), "id,desc", filter, PageRequest.of(0, limit)).getContent();
        ArrayNode items = objectMapper.createArrayNode();
        for (Order order : orders) {
            ctx.allowOrder(chatService.orderCard(order));
            items.add(orderSummary(order));
        }
        ObjectNode out = objectMapper.createObjectNode();
        out.set("orders", items);
        if (orders.isEmpty()) {
            out.put("note", "Khách chưa có đơn hàng phù hợp.");
        }
        return out.toString();
    }

    private String trackOrder(String code, AgentContext ctx) {
        Order order = orderRepository.findByCode(code.replace("#", "").trim())
                .filter(o -> o.getUser() != null && o.getUser().getUsername().equals(ctx.getUsername()))
                .orElse(null);
        if (order == null) {
            return "{\"error\":\"Không tìm thấy đơn này trong tài khoản của khách\"}";
        }
        ctx.allowOrder(chatService.orderCard(order));
        ObjectNode out = orderSummary(order);
        out.put("ship_to", order.getToAddress() + ", " + order.getToWardName() + ", " + order.getToProvinceName());
        Waybill waybill = waybillRepository.findByOrderId(order.getId()).orElse(null);
        if (waybill == null) {
            out.put("shipping", "Chưa tạo vận đơn (cửa hàng đang chuẩn bị hàng).");
        } else {
            ObjectNode w = out.putObject("waybill");
            w.put("code", waybill.getCode());
            w.put("status", WAYBILL_STATUS.getOrDefault(waybill.getStatus(), "Không rõ"));
            if (waybill.getExpectedDeliveryTime() != null) {
                w.put("expected_delivery", DATE.format(waybill.getExpectedDeliveryTime()));
            }
            ArrayNode timeline = w.putArray("timeline");
            waybill.getWaybillLogs().stream()
                    .sorted(Comparator.comparing(WaybillLog::getCreatedAt))
                    .forEach(l -> timeline.add(DATE.format(l.getCreatedAt()) + " – " + WAYBILL_STATUS.getOrDefault(l.getCurrentStatus(), "Cập nhật")));
        }
        return out.toString();
    }

    private ObjectNode orderSummary(Order order) {
        ObjectNode node = objectMapper.createObjectNode();
        node.put("code", order.getCode());
        node.put("status", ChatService.orderStatusLabel(order.getStatus()));
        node.put("payment", Integer.valueOf(2).equals(order.getPaymentStatus()) ? "Đã thanh toán" : "Chưa thanh toán");
        node.put("total", order.getTotalPay() == null ? 0 : order.getTotalPay().longValue());
        node.put("ordered_at", DATE.format(order.getCreatedAt()));
        node.put("items", order.getOrderVariants().stream()
                .map(ov -> ov.getVariant().getProduct().getName() + " x" + ov.getQuantity())
                .collect(Collectors.joining(", ")));
        return node;
    }

    private String myAccount(AgentContext ctx) {
        ObjectNode out = objectMapper.createObjectNode();
        out.put("name", ctx.getCustomerName());
        out.put("reward_points", rewardLogRepository.sumScoreByUsername(ctx.getUsername()));
        out.put("orders", orderRepository.findAllByUsername(ctx.getUsername(), "id,desc", null, PageRequest.of(0, 1)).getTotalElements());
        out.put("note", "Điểm thưởng: 1 điểm cho mỗi 1.000đ của đơn giao thành công, 50 điểm mỗi đánh giá.");
        return out.toString();
    }

    private String myWishlist(AgentContext ctx) {
        List<Wish> wishes = wishRepository.findAllByUsername(ctx.getUsername(), "id,desc", null, PageRequest.of(0, 10)).getContent();
        ArrayNode items = objectMapper.createArrayNode();
        wishes.forEach(w -> {
            ProductDoc doc = searchIndex.get(w.getProduct().getId());
            if (doc != null) {
                items.add(productSummary(doc, ctx));
            }
        });
        ObjectNode out = objectMapper.createObjectNode();
        out.set("products", items);
        return out.toString();
    }

    private String myCart(AgentContext ctx) {
        Cart cart = cartRepository.findByUsername(ctx.getUsername()).orElse(null);
        ArrayNode items = objectMapper.createArrayNode();
        if (cart != null) {
            cart.getCartVariants().forEach(cv -> {
                ProductDoc doc = searchIndex.get(cv.getVariant().getProduct().getId());
                if (doc != null) {
                    items.add(productSummary(doc, ctx).put("quantity", cv.getQuantity()));
                }
            });
        }
        ObjectNode out = objectMapper.createObjectNode();
        out.set("items", items);
        return out.toString();
    }

    // ================= Tiện ích =================

    private ObjectNode function(ArrayNode tools, String name, String description) {
        ObjectNode fn = tools.addObject().put("type", "function").putObject("function");
        fn.put("name", name);
        fn.put("description", description);
        fn.putObject("parameters").put("type", "object").putObject("properties");
        return fn;
    }

    private static ObjectNode properties(ObjectNode fn) {
        return (ObjectNode) fn.path("parameters").path("properties");
    }

    private static void required(ObjectNode fn, String... names) {
        ArrayNode req = ((ObjectNode) fn.get("parameters")).putArray("required");
        for (String n : names) {
            req.add(n);
        }
    }

    private static ObjectNode string(ObjectNode props, String name, String description) {
        return props.putObject(name).put("type", "string").put("description", description);
    }

    private static void number(ObjectNode props, String name, String description) {
        props.putObject(name).put("type", "number").put("description", description);
    }

    static String priceText(Double min, Double max) {
        if (min == null) {
            return "liên hệ";
        }
        String minText = String.format("%,.0fđ", min).replace(',', '.');
        if (max == null || max.equals(min)) {
            return minText;
        }
        return minText + " – " + String.format("%,.0fđ", max).replace(',', '.');
    }

    private static String truncate(String text, int max) {
        if (text == null) {
            return "";
        }
        return text.length() <= max ? text : text.substring(0, max) + "…";
    }

}
