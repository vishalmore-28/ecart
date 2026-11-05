package com.ecart.dao;

import com.ecart.model.Seller;
import com.ecart.util.DB;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SellerDAO {
    private static final String SELECT =
        "SELECT s.*, u.name owner_name, u.email, u.phone FROM sellers s JOIN users u ON u.id=s.user_id ";

    static Seller map(ResultSet rs) throws SQLException {
        Seller s = new Seller();
        s.setUserId(rs.getInt("user_id"));
        s.setOwnerName(rs.getString("owner_name")); s.setEmail(rs.getString("email")); s.setPhone(rs.getString("phone"));
        s.setShopName(rs.getString("shop_name")); s.setBusinessType(rs.getString("business_type"));
        s.setPan(rs.getString("pan")); s.setGstin(rs.getString("gstin")); s.setGstRegistered(rs.getBoolean("gst_registered"));
        s.setAddressLine1(rs.getString("address_line1")); s.setAddressLine2(rs.getString("address_line2"));
        s.setCity(rs.getString("city")); s.setState(rs.getString("state")); s.setPincode(rs.getString("pincode"));
        s.setBankHolder(rs.getString("bank_holder")); s.setBankIfsc(rs.getString("bank_ifsc")); s.setBankLast4(rs.getString("bank_last4"));
        s.setStatus(rs.getString("status")); s.setReviewNote(rs.getString("review_note"));
        s.setCreatedAt(rs.getTimestamp("created_at")); s.setReviewedAt(rs.getTimestamp("reviewed_at"));
        return s;
    }

    /** Creates the login (role SELLER) and the shop profile in ONE transaction. Returns the new user id. */
    public int register(String ownerName, String email, String phone, String hash, Seller s) throws SQLException {
        try (Connection c = DB.get()) {
            c.setAutoCommit(false);
            try {
                int id;
                try (PreparedStatement ps = c.prepareStatement(
                        "INSERT INTO users(id,name,email,phone,password_hash,role) VALUES (" + DB.nextval("seq_users") + ",?,?,?,?,'SELLER')", DB.keyCols())) {
                    ps.setString(1, ownerName); ps.setString(2, email); ps.setString(3, phone); ps.setString(4, hash);
                    ps.executeUpdate();
                    try (ResultSet k = ps.getGeneratedKeys()) { k.next(); id = k.getInt(1); }
                }
                try (PreparedStatement ps = c.prepareStatement(
                        "INSERT INTO sellers(user_id,shop_name,business_type,pan,gstin,gst_registered,address_line1,address_line2,city,state,pincode,"
                      + "bank_holder,bank_ifsc,bank_last4) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)")) {
                    int i = 1;
                    ps.setInt(i++, id); ps.setString(i++, s.getShopName()); ps.setString(i++, s.getBusinessType()); ps.setString(i++, s.getPan());
                    if (s.getGstin() == null) ps.setNull(i++, Types.VARCHAR); else ps.setString(i++, s.getGstin());
                    ps.setInt(i++, s.isGstRegistered() ? 1 : 0);
                    ps.setString(i++, s.getAddressLine1()); ps.setString(i++, s.getAddressLine2());
                    ps.setString(i++, s.getCity()); ps.setString(i++, s.getState()); ps.setString(i++, s.getPincode());
                    ps.setString(i++, s.getBankHolder()); ps.setString(i++, s.getBankIfsc()); ps.setString(i, s.getBankLast4());
                    ps.executeUpdate();
                }
                c.commit();
                return id;
            } catch (SQLException | RuntimeException e) {
                c.rollback();
                throw e;
            }
        }
    }

    public Seller find(int userId) throws SQLException {
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement(SELECT + "WHERE s.user_id=?")) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? map(rs) : null; }
        }
    }

    public boolean gstinExists(String gstin) throws SQLException {
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement("SELECT 1 FROM sellers WHERE gstin=?")) {
            ps.setString(1, gstin);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        }
    }

    /** status empty = all. Newest first. */
    public List<Seller> list(String status) throws SQLException {
        List<Seller> out = new ArrayList<>();
        boolean f = status != null && !status.isEmpty();
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement(SELECT + (f ? "WHERE s.status=? " : "") + "ORDER BY s.created_at DESC")) {
            if (f) ps.setString(1, status);
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) out.add(map(rs)); }
        }
        return out;
    }

    public void setStatus(int userId, String status, String note) throws SQLException {
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement(
                "UPDATE sellers SET status=?, review_note=?, reviewed_at=? WHERE user_id=?")) {
            ps.setString(1, status);
            if (note == null || note.isEmpty()) ps.setNull(2, Types.VARCHAR); else ps.setString(2, note);
            ps.setTimestamp(3, new Timestamp(System.currentTimeMillis())); ps.setInt(4, userId);
            ps.executeUpdate();
        }
    }

    public Map<String, Integer> counts() throws SQLException {
        Map<String, Integer> m = new LinkedHashMap<>();
        for (String s : new String[]{"PENDING", "APPROVED", "REJECTED", "SUSPENDED"}) m.put(s, 0);
        try (Connection c = DB.get(); Statement st = c.createStatement(); ResultSet rs = st.executeQuery("SELECT status, COUNT(*) FROM sellers GROUP BY status")) {
            while (rs.next()) m.put(rs.getString(1), rs.getInt(2));
        }
        return m;
    }

    public int pending() throws SQLException {
        try (Connection c = DB.get(); Statement st = c.createStatement(); ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM sellers WHERE status='PENDING'")) {
            rs.next(); return rs.getInt(1);
        }
    }
}
