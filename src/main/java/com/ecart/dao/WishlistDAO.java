package com.ecart.dao;

import com.ecart.model.Product;
import com.ecart.util.DB;
import java.sql.*;
import java.util.*;

public class WishlistDAO {
    public boolean toggle(int userId, int productId) throws SQLException {
        try (Connection c = DB.get()) {
            try (PreparedStatement del = c.prepareStatement("DELETE FROM wishlist WHERE user_id=? AND product_id=?")) {
                del.setInt(1, userId); del.setInt(2, productId);
                if (del.executeUpdate() > 0) return false;
            }
            try (PreparedStatement ins = c.prepareStatement("INSERT INTO wishlist(user_id,product_id) VALUES (?,?)")) {
                ins.setInt(1, userId); ins.setInt(2, productId); ins.executeUpdate();
            } catch (SQLException dup) {
                if (!DB.isDuplicate(dup)) throw dup;     // unique violation = already saved, ignore
            }
            return true;
        }
    }

    public Set<Integer> ids(int userId) throws SQLException {
        Set<Integer> out = new HashSet<>();
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement("SELECT product_id FROM wishlist WHERE user_id=?")) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) out.add(rs.getInt(1)); }
        }
        return out;
    }

    public List<Product> list(int userId) throws SQLException {
        List<Product> out = new ArrayList<>();
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement(
                ProductDAO.productSelect() + "JOIN wishlist w ON w.product_id=p.id WHERE w.user_id=? AND p.active=1 ORDER BY p.name")) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) out.add(ProductDAO.map(rs)); }
        }
        return out;
    }
}
