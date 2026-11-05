package com.ecart.dao;

import com.ecart.model.CartItem;
import com.ecart.model.Product;
import com.ecart.util.DB;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CartDAO {
    public List<CartItem> items(int userId) throws SQLException {
        List<CartItem> out = new ArrayList<>();
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement(
                ProductDAO.productSelect() + "JOIN cart_items ci ON ci.product_id=p.id WHERE ci.user_id=? ORDER BY p.name")) {
            ps.setInt(1, userId);
            // qty is read in a second pass to keep the product mapper reusable
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) { CartItem ci = new CartItem(); ci.setProduct(ProductDAO.map(rs)); out.add(ci); }
            }
        }
        for (CartItem ci : out) ci.setQty(qty(userId, ci.getProduct().getId()));
        return out;
    }

    public int qty(int userId, int productId) throws SQLException {
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement("SELECT qty FROM cart_items WHERE user_id=? AND product_id=?")) {
            ps.setInt(1, userId); ps.setInt(2, productId);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? rs.getInt(1) : 0; }
        }
    }

    public void set(int userId, int productId, int qty) throws SQLException {
        if (qty <= 0) { remove(userId, productId); return; }
        try (Connection c = DB.get()) {
            if (update(c, userId, productId, qty)) return;
            try (PreparedStatement ps = c.prepareStatement("INSERT INTO cart_items(user_id,product_id,qty) VALUES (?,?,?)")) {
                ps.setInt(1, userId); ps.setInt(2, productId); ps.setInt(3, qty); ps.executeUpdate();
            } catch (SQLException dup) {
                if (!DB.isDuplicate(dup)) throw dup;
                update(c, userId, productId, qty);      // another request inserted it first
            }
        }
    }

    private boolean update(Connection c, int userId, int productId, int qty) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("UPDATE cart_items SET qty=? WHERE user_id=? AND product_id=?")) {
            ps.setInt(1, qty); ps.setInt(2, userId); ps.setInt(3, productId);
            return ps.executeUpdate() > 0;
        }
    }

    public void remove(int userId, int productId) throws SQLException {
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement("DELETE FROM cart_items WHERE user_id=? AND product_id=?")) {
            ps.setInt(1, userId); ps.setInt(2, productId); ps.executeUpdate();
        }
    }

    public void clear(int userId) throws SQLException {
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement("DELETE FROM cart_items WHERE user_id=?")) {
            ps.setInt(1, userId); ps.executeUpdate();
        }
    }

    public int count(int userId) throws SQLException {
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement("SELECT COALESCE(SUM(qty),0) FROM cart_items WHERE user_id=?")) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) { rs.next(); return rs.getInt(1); }
        }
    }
}
