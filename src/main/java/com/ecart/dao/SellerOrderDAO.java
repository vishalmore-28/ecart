package com.ecart.dao;

import com.ecart.model.*;
import com.ecart.util.DB;
import com.ecart.util.PriceCalc;
import java.math.BigDecimal;
import java.sql.*;
import java.util.*;

/** Everything a seller may see or do with orders. Every query is limited to order_items.seller_id = the seller. */
public class SellerOrderDAO {
    private static final List<String> FLOW = Arrays.asList("PLACED", "CONFIRMED", "SHIPPED", "DELIVERED");

    /** itemStatus empty = all. Orders that contain at least one item of this seller, newest first. */
    public List<Order> list(int sellerId, String itemStatus) throws SQLException {
        boolean f = itemStatus != null && !itemStatus.isEmpty();
        List<Order> out = new ArrayList<>();
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement(DB.top(
                "SELECT o.*, u.name uname FROM shop_orders o JOIN users u ON u.id=o.user_id "
              + "WHERE o.id IN (SELECT order_id FROM order_items WHERE seller_id=?" + (f ? " AND item_status=?" : "") + ") ORDER BY o.id DESC"))) {
            int i = 1;
            ps.setInt(i++, sellerId);
            if (f) ps.setString(i++, itemStatus);
            ps.setInt(i, 200);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) { Order o = OrderDAO.map(rs); o.setUserName(rs.getString("uname")); out.add(o); }
            }
            Seller me = null;
            for (Order o : out) fill(c, o, sellerId, f ? itemStatus : null);
        }
        return out;
    }

    /** One order, with ONLY this seller's items (or null if the seller has none in it). */
    public Order find(int orderId, int sellerId, String sellerState) throws SQLException {
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement(
                "SELECT o.*, u.name uname FROM shop_orders o JOIN users u ON u.id=o.user_id WHERE o.id=?")) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                Order o = OrderDAO.map(rs);
                o.setUserName(rs.getString("uname"));
                fill(c, o, sellerId, null);
                if (o.getItems().isEmpty()) return null;
                o.setSameState(sellerState != null && o.getShipState() != null
                        && sellerState.trim().equalsIgnoreCase(o.getShipState().trim()));
                List<OrderEvent> ev = new ArrayList<>();
                try (PreparedStatement p2 = c.prepareStatement("SELECT * FROM order_events WHERE order_id=? ORDER BY id")) {
                    p2.setInt(1, orderId);
                    try (ResultSet r2 = p2.executeQuery()) {
                        while (r2.next()) {
                            OrderEvent e = new OrderEvent();
                            e.setStatus(r2.getString("status")); e.setNote(r2.getString("note")); e.setCreatedAt(r2.getTimestamp("created_at"));
                            ev.add(e);
                        }
                    }
                }
                o.setEvents(ev);
                return o;
            }
        }
    }

    /** Loads this seller's items into the order and totals their taxable value and GST (cancelled items excluded). */
    private void fill(Connection c, Order o, int sellerId, String itemStatus) throws SQLException {
        List<OrderItem> items = new ArrayList<>();
        BigDecimal taxable = BigDecimal.ZERO, tax = BigDecimal.ZERO;
        try (PreparedStatement ps = c.prepareStatement(
                "SELECT i.*, s.shop_name sname FROM order_items i LEFT JOIN sellers s ON s.user_id=i.seller_id "
              + "WHERE i.order_id=? AND i.seller_id=?" + (itemStatus != null ? " AND i.item_status=?" : "") + " ORDER BY i.id")) {
            ps.setInt(1, o.getId()); ps.setInt(2, sellerId);
            if (itemStatus != null) ps.setString(3, itemStatus);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    OrderItem i = new OrderItem();
                    i.setId(rs.getInt("id")); i.setOrderId(o.getId()); i.setProductId(rs.getInt("product_id"));
                    i.setName(rs.getString("name")); i.setPrice(rs.getBigDecimal("price")); i.setQty(rs.getInt("qty"));
                    OrderDAO.readSellerFields(rs, i);
                    BigDecimal line = i.getLineTotal();
                    i.setTaxable(PriceCalc.itemTaxable(line, o.getSubtotal(), o.getDiscount()).setScale(2, java.math.RoundingMode.HALF_UP));
                    i.setTax(PriceCalc.itemTax(line, o.getSubtotal(), o.getDiscount(), i.getGstPercent()));
                    if (!"CANCELLED".equals(i.getItemStatus())) { taxable = taxable.add(i.getTaxable()); tax = tax.add(i.getTax()); }
                    items.add(i);
                }
            }
        }
        o.setItems(items);
        o.setMineTaxable(taxable);
        o.setMineTax(tax);
    }

    /**
     * Moves ALL of this seller's open items in the order one step forward (PLACED > CONFIRMED > SHIPPED > DELIVERED).
     * The customer-facing order status then follows the slowest item, so a multi-seller order only shows
     * SHIPPED / DELIVERED once every seller has done their part.
     * Returns false if the step is not allowed (wrong state, cancelled order, not this seller's order).
     */
    public boolean advance(int orderId, int sellerId, String shopName, String next, String courier, String tracking) throws SQLException {
        int idx = FLOW.indexOf(next);
        if (idx < 1) return false;
        String prev = FLOW.get(idx - 1);
        try (Connection c = DB.get()) {
            c.setAutoCommit(false);
            try {
                String orderStatus, pay, method;
                try (PreparedStatement ps = c.prepareStatement("SELECT status, payment_status, payment_method FROM shop_orders WHERE id=? FOR UPDATE")) {
                    ps.setInt(1, orderId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) { c.rollback(); return false; }
                        orderStatus = rs.getString(1); pay = rs.getString(2); method = rs.getString(3);
                    }
                }
                if ("CANCELLED".equals(orderStatus) || "DELIVERED".equals(orderStatus)) { c.rollback(); return false; }
                int n;
                try (PreparedStatement ps = c.prepareStatement(
                        "UPDATE order_items SET item_status=?, courier=COALESCE(?,courier), tracking_no=COALESCE(?,tracking_no) "
                      + "WHERE order_id=? AND seller_id=? AND item_status=?")) {
                    ps.setString(1, next);
                    if (courier == null) ps.setNull(2, Types.VARCHAR); else ps.setString(2, courier);
                    if (tracking == null) ps.setNull(3, Types.VARCHAR); else ps.setString(3, tracking);
                    ps.setInt(4, orderId); ps.setInt(5, sellerId); ps.setString(6, prev);
                    n = ps.executeUpdate();
                }
                if (n == 0) { c.rollback(); return false; }
                OrderDAO.event(c, orderId, orderStatus, shopName + ": " + next.toLowerCase() + " "
                        + n + " item(s)" + (tracking == null ? "" : " via " + courier + ", tracking " + tracking));

                // order status = slowest open item
                int min = FLOW.size() - 1;
                try (PreparedStatement ps = c.prepareStatement("SELECT item_status FROM order_items WHERE order_id=? AND item_status<>'CANCELLED'")) {
                    ps.setInt(1, orderId);
                    try (ResultSet rs = ps.executeQuery()) { while (rs.next()) min = Math.min(min, FLOW.indexOf(rs.getString(1))); }
                }
                int cur = FLOW.indexOf(orderStatus);
                if (min > cur) {
                    String derived = FLOW.get(min);
                    String newPay = "DELIVERED".equals(derived) && "COD".equals(method) ? "PAID" : pay;
                    try (PreparedStatement ps = c.prepareStatement("UPDATE shop_orders SET status=?, payment_status=? WHERE id=?")) {
                        ps.setString(1, derived); ps.setString(2, newPay); ps.setInt(3, orderId); ps.executeUpdate();
                    }
                    OrderDAO.event(c, orderId, derived, "All sellers have updated their items");
                }
                c.commit();
                return true;
            } catch (SQLException | RuntimeException e) {
                c.rollback();
                throw e;
            }
        }
    }

    /** Dashboard numbers for one seller. */
    public Map<String, Object> stats(int sellerId) throws SQLException {
        Map<String, Object> m = new LinkedHashMap<>();
        BigDecimal sales = BigDecimal.ZERO, gst = BigDecimal.ZERO;
        Set<Integer> orders = new HashSet<>();
        Map<String, Integer> byStatus = new LinkedHashMap<>();
        for (String s : FLOW) byStatus.put(s, 0);
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement(
                "SELECT i.order_id, i.price, i.qty, i.gst_percent, i.item_status, o.subtotal, o.discount "
              + "FROM order_items i JOIN shop_orders o ON o.id=i.order_id WHERE i.seller_id=?")) {
            ps.setInt(1, sellerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String st = rs.getString("item_status");
                    if ("CANCELLED".equals(st)) continue;
                    orders.add(rs.getInt("order_id"));
                    byStatus.merge(st, 1, Integer::sum);
                    BigDecimal line = rs.getBigDecimal("price").multiply(BigDecimal.valueOf(rs.getInt("qty")));
                    BigDecimal sub = rs.getBigDecimal("subtotal"), disc = rs.getBigDecimal("discount");
                    sales = sales.add(PriceCalc.itemTaxable(line, sub, disc).setScale(2, java.math.RoundingMode.HALF_UP));
                    gst = gst.add(PriceCalc.itemTax(line, sub, disc, rs.getBigDecimal("gst_percent")));
                }
            }
        }
        m.put("orders", orders.size());
        m.put("sales", sales);
        m.put("gst", gst);
        m.put("byStatus", byStatus);
        m.put("toShip", byStatus.get("PLACED") + byStatus.get("CONFIRMED"));
        return m;
    }
}
