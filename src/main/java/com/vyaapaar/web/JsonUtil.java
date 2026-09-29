package com.vyaapaar.web;

import com.vyaapaar.model.*;
import java.util.*;

/**
 * Lightweight JSON utility for serialization and deserialization without third-party dependencies.
 */
public class JsonUtil {

    public static String escape(String s) {
        if (s == null) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '"': sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\b': sb.append("\\b"); break;
                case '\f': sb.append("\\f"); break;
                case '\n': sb.append("\\n"); break;
                case '\r': sb.append("\\r"); break;
                case '\t': sb.append("\\t"); break;
                default:
                    if (c < ' ') {
                        String t = "000" + Integer.toHexString(c);
                        sb.append("\\u").append(t.substring(t.length() - 4));
                    } else {
                        sb.append(c);
                    }
            }
        }
        return sb.toString();
    }

    public static Map<String, String> parseJson(String json) {
        Map<String, String> map = new HashMap<>();
        if (json == null || json.trim().isEmpty()) return map;

        String content = json.trim();
        if (content.startsWith("{")) content = content.substring(1);
        if (content.endsWith("}")) content = content.substring(0, content.length() - 1);

        boolean inQuotes = false;
        StringBuilder key = new StringBuilder();
        StringBuilder value = new StringBuilder();
        boolean readingKey = true;

        for (int i = 0; i < content.length(); i++) {
            char c = content.charAt(i);
            if (c == '"' && (i == 0 || content.charAt(i - 1) != '\\')) {
                inQuotes = !inQuotes;
                continue;
            }

            if (!inQuotes) {
                if (c == ':') {
                    readingKey = false;
                    continue;
                } else if (c == ',') {
                    String k = key.toString().trim();
                    String v = value.toString().trim();
                    if (!k.isEmpty()) {
                        map.put(k, unescape(v));
                    }
                    key.setLength(0);
                    value.setLength(0);
                    readingKey = true;
                    continue;
                }
            }

            if (readingKey) {
                key.append(c);
            } else {
                value.append(c);
            }
        }

        String k = key.toString().trim();
        String v = value.toString().trim();
        if (!k.isEmpty()) {
            map.put(k, unescape(v));
        }

        return map;
    }

    private static String unescape(String s) {
        if (s == null) return "";
        s = s.trim();
        if (s.startsWith("\"") && s.endsWith("\"") && s.length() >= 2) {
            s = s.substring(1, s.length() - 1);
        }
        return s.replace("\\\"", "\"").replace("\\\\", "\\").replace("\\n", "\n").replace("\\r", "\r").replace("\\t", "\t");
    }

    public static String userToJson(User u) {
        if (u == null) return "null";
        return String.format("{\"userId\":%d,\"fullName\":\"%s\",\"email\":\"%s\",\"role\":\"%s\",\"phone\":\"%s\",\"address\":\"%s\",\"createdAt\":\"%s\"}",
                u.getUserId(),
                escape(u.getFullName()),
                escape(u.getEmail()),
                escape(u.getRole()),
                escape(u.getPhone()),
                escape(u.getAddress()),
                u.getCreatedAt() != null ? u.getCreatedAt().toString() : "");
    }

    public static String categoryToJson(Category c) {
        if (c == null) return "null";
        return String.format("{\"categoryId\":%d,\"categoryName\":\"%s\",\"description\":\"%s\"}",
                c.getCategoryId(),
                escape(c.getCategoryName()),
                escape(c.getDescription()));
    }

    public static String categoriesToJson(List<Category> list) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(categoryToJson(list.get(i)));
        }
        sb.append("]");
        return sb.toString();
    }

    public static String productToJson(Product p) {
        if (p == null) return "null";
        return String.format("{\"productId\":%d,\"categoryId\":%d,\"categoryName\":\"%s\",\"name\":\"%s\",\"description\":\"%s\",\"price\":%.2f,\"stockQuantity\":%d,\"createdAt\":\"%s\"}",
                p.getProductId(),
                p.getCategoryId(),
                escape(p.getCategoryName()),
                escape(p.getName()),
                escape(p.getDescription()),
                p.getPrice(),
                p.getStockQuantity(),
                p.getCreatedAt() != null ? p.getCreatedAt().toString() : "");
    }

    public static String productsToJson(List<Product> list) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(productToJson(list.get(i)));
        }
        sb.append("]");
        return sb.toString();
    }

    public static String cartItemToJson(CartItem item) {
        if (item == null) return "null";
        return String.format("{\"cartItemId\":%d,\"cartId\":%d,\"productId\":%d,\"productName\":\"%s\",\"price\":%.2f,\"quantity\":%d,\"subtotal\":%.2f}",
                item.getCartItemId(),
                item.getCartId(),
                item.getProductId(),
                escape(item.getProductName()),
                item.getUnitPrice(),
                item.getQuantity(),
                item.getSubtotal());
    }

    public static String cartToJson(Cart cart, double total) {
        if (cart == null) return "{\"items\":[],\"total\":0.0}";
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("{\"cartId\":%d,\"userId\":%d,\"total\":%.2f,\"items\":[",
                cart.getCartId(), cart.getUserId(), total));
        List<CartItem> items = cart.getItems();
        for (int i = 0; i < items.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(cartItemToJson(items.get(i)));
        }
        sb.append("]}");
        return sb.toString();
    }

    public static String orderItemToJson(OrderItem item) {
        if (item == null) return "null";
        return String.format("{\"orderItemId\":%d,\"orderId\":%d,\"productId\":%d,\"productName\":\"%s\",\"quantity\":%d,\"unitPrice\":%.2f,\"subtotal\":%.2f}",
                item.getOrderItemId(),
                item.getOrderId(),
                item.getProductId(),
                escape(item.getProductName()),
                item.getQuantity(),
                item.getUnitPrice(),
                item.getSubtotal());
    }

    public static String orderToJson(Order order) {
        if (order == null) return "null";
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("{\"orderId\":%d,\"userId\":%d,\"customerName\":\"%s\",\"totalAmount\":%.2f,\"orderStatus\":\"%s\",\"shippingAddress\":\"%s\",\"orderDate\":\"%s\",\"items\":[",
                order.getOrderId(),
                order.getUserId(),
                escape(order.getCustomerName()),
                order.getTotalAmount(),
                escape(order.getOrderStatus()),
                escape(order.getShippingAddress()),
                order.getOrderDate() != null ? order.getOrderDate().toString() : ""));
        List<OrderItem> items = order.getItems();
        if (items != null) {
            for (int i = 0; i < items.size(); i++) {
                if (i > 0) sb.append(",");
                sb.append(orderItemToJson(items.get(i)));
            }
        }
        sb.append("]}");
        return sb.toString();
    }

    public static String ordersToJson(List<Order> orders) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < orders.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(orderToJson(orders.get(i)));
        }
        sb.append("]");
        return sb.toString();
    }
}
