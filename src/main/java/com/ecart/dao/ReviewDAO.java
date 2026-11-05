package com.ecart.dao;

import com.ecart.model.Review;
import com.ecart.util.DB;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReviewDAO {
    public List<Review> forProduct(int productId) throws SQLException {
        List<Review> out = new ArrayList<>();
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement(
                DB.top("SELECT r.*, u.name uname\n"
                        + "FROM reviews r\n"
                        + "JOIN users u ON u.id = r.user_id\n"
                        + "WHERE r.product_id = ?\n"
                        + "ORDER BY r.created_at DESC, r.id DESC"))) {
            ps.setInt(1, productId); ps.setInt(2, 50);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Review r = new Review();
                    r.setId(rs.getInt("id")); r.setProductId(productId); r.setUserId(rs.getInt("user_id"));
                    r.setUserName(rs.getString("uname")); r.setRating(rs.getInt("rating")); r.setTitle(rs.getString("title"));
                    r.setBody(rs.getString("body")); r.setVerified(rs.getBoolean("verified")); r.setCreatedAt(rs.getTimestamp("created_at"));
                    out.add(r);
                }
            }
        }
        return out;
    }

    /** One review per user per product; submitting again updates it. */
    public void save(Review r) throws SQLException {
        try (Connection c = DB.get()) {
            if (update(c, r)) return;
            try (PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO reviews(id,product_id,user_id,rating,title,body,verified) VALUES (" + DB.nextval("seq_reviews") + ",?,?,?,?,?,?)")) {
                ps.setInt(1, r.getProductId()); ps.setInt(2, r.getUserId()); ps.setInt(3, r.getRating());
                ps.setString(4, r.getTitle()); ps.setString(5, r.getBody()); ps.setInt(6, r.isVerified() ? 1 : 0);
                ps.executeUpdate();
            } catch (SQLException dup) {
                if (!DB.isDuplicate(dup)) throw dup;
                update(c, r);
            }
        }
    }

    private boolean update(Connection c, Review r) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(
                "UPDATE reviews SET rating=?, title=?, body=?, verified=? WHERE product_id=? AND user_id=?")) {
            ps.setInt(1, r.getRating()); ps.setString(2, r.getTitle()); ps.setString(3, r.getBody());
            ps.setInt(4, r.isVerified() ? 1 : 0); ps.setInt(5, r.getProductId()); ps.setInt(6, r.getUserId());
            return ps.executeUpdate() > 0;
        }
    }
}