package com.necom.service.chat.agent;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.necom.service.chat.search.ProductSearchIndex.ProductDoc;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Trạng thái một lượt trả lời: khách đang chat (đã xác thực) và những sản phẩm/đơn hàng tool đã trả về.
 * Chỉ các mục này mới được hiện thành thẻ, nên LLM không thể bịa id sản phẩm hay lộ đơn của người khác.
 */
@Getter
@RequiredArgsConstructor
public class AgentContext {
    private final Long roomId;
    private final String username;
    private final String customerName;
    // Tin gần nhất của khách: căn cứ để quyết định có cho chuyển tư vấn viên hay không
    private final String lastCustomerMessage;

    private final Map<Long, ProductDoc> allowedProducts = new LinkedHashMap<>();
    private final Map<String, ObjectNode> allowedOrders = new LinkedHashMap<>();
    private final List<String> toolTrace = new ArrayList<>();
    private final Map<String, Integer> toolCalls = new java.util.HashMap<>();
    private boolean handoff;
    private String handoffReason;

    public void allowProduct(ProductDoc doc) {
        allowedProducts.put(doc.getId(), doc);
    }

    public void allowOrder(ObjectNode card) {
        allowedOrders.put(card.path("code").asText(), card);
    }

    /**
     * Đếm số lần gọi một tool trong lượt; trả về true nếu đã vượt giới hạn.
     */
    public boolean exceeded(String tool, int limit) {
        return toolCalls.merge(tool, 1, Integer::sum) > limit;
    }

    public void requestHandoff(String reason) {
        this.handoff = true;
        this.handoffReason = reason;
    }
}
