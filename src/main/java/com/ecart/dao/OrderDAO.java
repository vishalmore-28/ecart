package com.ecart.dao;

import com.ecart.model.*;
import com.ecart.util.DB;
import com.ecart.util.Security;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class OrderDAO {

    static Order map(ResultSet rs) throws SQLException {
        Order o = new Order();
        o.setId(rs.getInt("id")); o.setOrderNo(rs.getString("order_no")); o.setUserId(rs.getInt("user_id"));
        o.setStatus(rs.getString("status")); o.setSubtotal(rs.getBigDecimal("subtotal")); o.setDiscount(rs.getBigDecimal("discount"));
        o.setTax(rs.getBigDecimal("tax")); o.setShipping(rs.getBigDecimal("shipping")); o.setTotal(rs.getBigDecimal("total"));
        o.setCouponCode(rs.getString("coupon_code")); o.setShipName(rs.getString("ship_name")); o.setShipPhone(rs.getString("ship_phone"));
        o.setShipAddress(rs.getString("ship_address")); o.setPaymentMethod(rs.getString("payment_method"));
        o.setPaymentStatus(rs.getString("payment_status")); o.setTxnRef(rs.getString("txn_ref")); o.setCardLast4(rs.getString("card_last4"));
        o.setCreatedAt(rs.getTimestamp("created_at"));
        o.setShipState(rs.getString("ship_state"));
        return o;
    }

    /**
     * Places an order in ONE transaction: reserves stock row by row, writes order, items, timeline and payment,
     * then empties the cart. Any failure rolls everything back.
     */
    public Order place(int userId, List<CartItem> items, Totals t, Address a, String method, String payStatus,
                       String txnRef, String last4) throws SQLException, StockException {
        String orderNo = "EC" + new SimpleDateFormat("yyMMdd").format(new java.util.Date()) + Security.token(4).replaceAll("[^A-Za-z0-9]", "X").toUpperCase();
        try (Connection c = DB.get()) {
            c.setAutoCommit(false);
            try {
                for (CartItem ci : items) {
                    try (PreparedStatement ps = c.prepareStatement("UPDATE products SET stock=stock-? WHERE id=? AND active=1 AND stock>=? AND (seller_id IS NULL OR seller_id IN (SELECT user_id FROM sellers WHERE status='APPROVED'))")) {
                        ps.setInt(1, ci.getQty()); ps.setInt(2, ci.getProduct().getId()); ps.setInt(3, ci.getQty());
                        if (ps.executeUpdate() == 0) throw new StockException(ci.getProduct().getName());
                    }
                }
                int orderId;
                try (PreparedStatement ps = c.prepareStatement(
                        "INSERT INTO shop_orders(id,order_no,user_id,status,subtotal,discount,tax,shipping,total,coupon_code,ship_name,ship_phone,"
                      + "ship_address,payment_method,payment_status,txn_ref,card_last4,ship_state) VALUES (" + DB.nextval("seq_orders") + ",?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
                        DB.keyCols())) {
                    int i = 1;
                    ps.setString(i++, orderNo); ps.setInt(i++, userId); ps.setString(i++, "PLACED");
                    ps.setBigDecimal(i++, t.getSubtotal()); ps.setBigDecimal(i++, t.getDiscount()); ps.setBigDecimal(i++, t.getTax());
                    ps.setBigDecimal(i++, t.getShipping()); ps.setBigDecimal(i++, t.getTotal()); ps.setString(i++, t.getCouponCode());
                    ps.setString(i++, a.getFullName()); ps.setString(i++, a.getPhone());
                    ps.setString(i++, a.getLine1() + (a.getLine2() == null || a.getLine2().isEmpty() ? "" : ", " + a.getLine2())
                            + ", " + a.getCity() + ", " + a.getState() + " - " + a.getPincode());
                    ps.setString(i++, method); ps.setString(i++, payStatus); ps.setString(i++, txnRef); ps.setString(i++, last4);
                    ps.setString(i, a.getState());
                    ps.executeUpdate();
                    try (ResultSet k = ps.getGeneratedKeys()) { k.next(); orderId = k.getInt(1); }
                }
                try (PreparedStatement ps = c.prepareStatement("INSERT INTO order_items(id,order_id,product_id,name,price,qty,seller_id,gst_percent,hsn_code) VALUES (" + DB.nextval("seq_order_items") + ",?,?,?,?,?,?,?,?)")) {
                    for (CartItem ci : items) {
                        ps.setInt(1, orderId); ps.setInt(2, ci.getProduct().getId()); ps.setString(3, ci.getProduct().getName());
                        ps.setBigDecimal(4, ci.getProduct().getPrice()); ps.setInt(5, ci.getQty());
                        if (ci.getProduct().getSellerId() > 0) ps.setInt(6, ci.getProduct().getSellerId()); else ps.setNull(6, Types.INTEGER);
                        ps.setBigDecimal(7, ci.getProduct().getGstPercent());
                        String hsn = ci.getProduct().getHsnCode();
                        if (hsn == null || hsn.isEmpty()) ps.setNull(8, Types.VARCHAR); else ps.setString(8, hsn);
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
                event(c, orderId, "PLACED", "Order placed (" + method + ")");
                try (PreparedStatement ps = c.prepareStatement("DELETE FROM cart_items WHERE user_id=?")) {
                    ps.setInt(1, userId); ps.executeUpdate();
                }
                c.commit();
                Order o = new Order();
                o.setId(orderId); o.setOrderNo(orderNo);
                return o;
            } catch (SQLException | StockException | RuntimeException e) {
                c.rollback();
                throw e;
            }
        }
    }

    public List<Order> forUser(int userId) throws SQLException {
        List<Order> out = new ArrayList<>();
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement("SELECT * FROM shop_orders WHERE user_id=? ORDER BY id DESC")) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) out.add(map(rs)); }
            for (Order o : out) o.setItems(items(c, o.getId()));
        }
        return out;
    }

    /** userId = 0 means "admin: any owner". Otherwise the order must belong to that user. */
    public Order find(int id, int userId) throws SQLException {
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement(
                "SELECT o.*, u.name uname FROM shop_orders o JOIN users u ON u.id=o.user_id WHERE o.id=?" + (userId > 0 ? " AND o.user_id=?" : ""))) {
            ps.setInt(1, id);
            if (userId > 0) ps.setInt(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                Order o = map(rs);
                o.setUserName(rs.getString("uname"));
                o.setItems(items(c, id));
                o.setEvents(events(c, id));
                return o;
            }
        }
    }

    private List<OrderItem> items(Connection c, int orderId) throws SQLException {
        List<OrderItem> out = new ArrayList<>();
        try (PreparedStatement ps = c.prepareStatement("SELECT i.*, s.shop_name sname FROM order_items i LEFT JOIN sellers s ON s.user_id=i.seller_id WHERE i.order_id=? ORDER BY i.id")) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    OrderItem i = new OrderItem();
                    i.setId(rs.getInt("id")); i.setOrderId(orderId); i.setProductId(rs.getInt("product_id"));
                    i.setName(rs.getString("name")); i.setPrice(rs.getBigDecimal("price")); i.setQty(rs.getInt("qty"));
                    readSellerFields(rs, i);
                    out.add(i);
                }
            }
        }
        return out;
    }

    /** Seller / GST / fulfilment columns of an order item (shared with SellerOrderDAO). */
    static void readSellerFields(ResultSet rs, OrderItem i) throws SQLException {
        i.setSellerId(rs.getInt("seller_id"));
        i.setSellerName(rs.getString("sname"));
        java.math.BigDecimal g = rs.getBigDecimal("gst_percent");
        if (g != null) i.setGstPercent(g);
        i.setHsnCode(rs.getString("hsn_code"));
        i.setItemStatus(rs.getString("item_status"));
        i.setCourier(rs.getString("courier"));
        i.setTrackingNo(rs.getString("tracking_no"));
    }

    static void event(Connection c, int orderId, String status, String note) throws SQLException {
        if (note != null && note.length() > 250) note = note.substring(0, 250);
        try (PreparedStatement ps = c.prepareStatement("INSERT INTO order_events(id,order_id,status,note) VALUES (" + DB.nextval("seq_order_events") + ",?,?,?)")) {
            ps.setInt(1, orderId); ps.setString(2, status); ps.setString(3, note); ps.executeUpdate();
        }
    }

    private List<OrderEvent> events(Connection c, int orderId) throws SQLException {
        List<OrderEvent> out = new ArrayList<>();
        try (PreparedStatement ps = c.prepareStatement("SELECT * FROM order_events WHERE order_id=? ORDER BY id")) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    OrderEvent e = new OrderEvent();
                    e.setStatus(rs.getString("status")); e.setNote(rs.getString("note")); e.setCreatedAt(rs.getTimestamp("created_at"));
                    out.add(e);
                }
            }
        }
        return out;
    }

    /** Customer cancel: allowed only before shipping. Stock is restored, paid orders are marked REFUNDED. */
    public boolean cancel(int orderId, int userId) throws SQLException {
        return changeStatus(orderId, userId, "CANCELLED", "Cancelled by customer");
    }

    /** Admin status change. Forward-only flow: PLACED > CONFIRMED > SHIPPED > DELIVERED, or CANCELLED before shipping. */
    public boolean adminUpdate(int orderId, String newStatus, String note) throws SQLException {
        return changeStatus(orderId, 0, newStatus, note == null || note.isEmpty() ? "Status updated by admin" : note);
    }

    private static final List<String> FLOW = Arrays.asList("PLACED", "CONFIRMED", "SHIPPED", "DELIVERED");

    private boolean changeStatus(int orderId, int userId, String next, String note) throws SQLException {
        try (Connection c = DB.get()) {
            c.setAutoCommit(false);
            try {
                String cur, pay, method;
                try (PreparedStatement ps = c.prepareStatement("SELECT status, payment_status, payment_method FROM shop_orders WHERE id=?"
                        + (userId > 0 ? " AND user_id=?" : "") + " FOR UPDATE")) {
                    ps.setInt(1, orderId);
                    if (userId > 0) ps.setInt(2, userId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) { c.rollback(); return false; }
                        cur = rs.getString(1); pay = rs.getString(2); method = rs.getString(3);
                    }
                }
                boolean ok;
                if ("CANCELLED".equals(next)) ok = "PLACED".equals(cur) || "CONFIRMED".equals(cur);
                else ok = FLOW.indexOf(next) == FLOW.indexOf(cur) + 1 && FLOW.indexOf(cur) >= 0;
                if (!ok) { c.rollback(); return false; }

                String newPay = pay;
                if ("CANCELLED".equals(next)) {
                    try (PreparedStatement ps = c.prepareStatement(
                            "UPDATE products p SET stock = stock + (SELECT COALESCE(SUM(i.qty),0) FROM order_items i WHERE i.order_id=? AND i.product_id=p.id) "
                          + "WHERE p.id IN (SELECT product_id FROM order_items WHERE order_id=?)")) {
                        ps.setInt(1, orderId); ps.setInt(2, orderId); ps.executeUpdate();
                    }
                    if ("PAID".equals(pay)) newPay = "REFUNDED";
                    if ("PENDING".equals(pay)) newPay = "VOID";
                } else if ("DELIVERED".equals(next) && "COD".equals(method)) {
                    newPay = "PAID";
                }
                try (PreparedStatement ps = c.prepareStatement("UPDATE shop_orders SET status=?, payment_status=? WHERE id=?")) {
                    ps.setString(1, next); ps.setString(2, newPay); ps.setInt(3, orderId); ps.executeUpdate();
                }
                try (PreparedStatement ps = c.prepareStatement("UPDATE order_items SET item_status=? WHERE order_id=? AND item_status<>'CANCELLED'")) {
                    ps.setString(1, next); ps.setInt(2, orderId); ps.executeUpdate();
                }
                event(c, orderId, next, note);
                c.commit();
                return true;
            } catch (SQLException | RuntimeException e) {
                c.rollback();
                throw e;
            }
        }
    }

    public List<Order> adminList(String status) throws SQLException {
        List<Order> out = new ArrayList<>();
        boolean f = status != null && !status.isEmpty();
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement(
                DB.top("SELECT o.*, u.name uname FROM shop_orders o JOIN users u ON u.id=o.user_id" + (f ? " WHERE o.status=?" : "") + " ORDER BY o.id DESC"))) {
            int i = 1;
            if (f) ps.setString(i++, status);
            ps.setInt(i, 200);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) { Order o = map(rs); o.setUserName(rs.getString("uname")); out.add(o); }
            }
        }
        return out;
    }

    /** "Verified purchase" check for reviews. */
    public boolean hasPurchased(int userId, int productId) throws SQLException {
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement(
                DB.top("SELECT 1 FROM shop_orders o JOIN order_items i ON i.order_id=o.id WHERE o.user_id=? AND i.product_id=? AND o.status<>'CANCELLED'"))) {
            ps.setInt(1, userId); ps.setInt(2, productId); ps.setInt(3, 1);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        }
    }

    /** Dashboard numbers for the admin home page. */
    public Map<String, Object> stats() throws SQLException {
        Map<String, Object> m = new LinkedHashMap<>();
        try (Connection c = DB.get(); Statement st = c.createStatement()) {
            try (PreparedStatement ps = c.prepareStatement("SELECT COUNT(*), COALESCE(SUM(CASE WHEN status<>'CANCELLED' THEN total END),0), "
                    + "COALESCE(SUM(CASE WHEN created_at >= ? THEN 1 ELSE 0 END),0) FROM shop_orders")) {
                ps.setTimestamp(1, DB.startOfToday());
                try (ResultSet rs = ps.executeQuery()) {
                    rs.next(); m.put("orders", rs.getInt(1)); m.put("revenue", rs.getBigDecimal(2)); m.put("today", rs.getInt(3));
                }
            }
            try (ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM users WHERE role='USER'")) { rs.next(); m.put("customers", rs.getInt(1)); }
            Map<String, Integer> byStatus = new LinkedHashMap<>();
            for (String s : new String[]{"PLACED", "CONFIRMED", "SHIPPED", "DELIVERED", "CANCELLED"}) byStatus.put(s, 0);
            try (ResultSet rs = st.executeQuery("SELECT status, COUNT(*) FROM shop_orders GROUP BY status")) {
                while (rs.next()) byStatus.put(rs.getString(1), rs.getInt(2));
            }
            m.put("byStatus", byStatus);
            List<String[]> top = new ArrayList<>();
            try (ResultSet rs = st.executeQuery(DB.top("SELECT i.name, SUM(i.qty) q FROM order_items i JOIN shop_orders o ON o.id=i.order_id "
                    + "WHERE o.status<>'CANCELLED' GROUP BY i.product_id, i.name ORDER BY q DESC").replace("?", "5"))) {
                while (rs.next()) top.add(new String[]{rs.getString(1), String.valueOf(rs.getInt(2))});
            }
            m.put("top", top);
        }
        return m;
    }
}
