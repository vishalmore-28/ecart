package com.ecart.dao;

import com.ecart.model.User;
import com.ecart.util.DB;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    static User map(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getInt("id"));
        u.setName(rs.getString("name"));
        u.setEmail(rs.getString("email"));
        u.setPhone(rs.getString("phone"));
        u.setRole(rs.getString("role"));
        u.setActive(rs.getBoolean("active"));
        u.setPasswordHash(rs.getString("password_hash"));
        u.setFailedAttempts(rs.getInt("failed_attempts"));
        u.setLockedUntil(rs.getTimestamp("locked_until"));
        u.setCreatedAt(rs.getTimestamp("created_at"));
        return u;
    }

    public User findByEmail(String email) throws SQLException {
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement("SELECT * FROM users WHERE email=?")) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? map(rs) : null; }
        }
    }

    public User findById(int id) throws SQLException {
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement("SELECT * FROM users WHERE id=?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? map(rs) : null; }
        }
    }

    public int create(String name, String email, String phone, String hash, String role) throws SQLException {
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement(
                "INSERT INTO users(id,name,email,phone,password_hash,role) VALUES (" + DB.nextval("seq_users") + ",?,?,?,?,?)", DB.keyCols())) {
            ps.setString(1, name); ps.setString(2, email); ps.setString(3, phone); ps.setString(4, hash); ps.setString(5, role);
            ps.executeUpdate();
            try (ResultSet k = ps.getGeneratedKeys()) { k.next(); return k.getInt(1); }
        }
    }

    public boolean hasAdmin() throws SQLException {
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement(DB.top("SELECT 1 FROM users WHERE role='ADMIN'"))) {
            ps.setInt(1, 1);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        }
    }

    /** Count a failed login; 5 failures lock the account for 15 minutes. */
    public void fail(int id) throws SQLException {
        try (Connection c = DB.get()) {
            try (PreparedStatement ps = c.prepareStatement("UPDATE users SET failed_attempts=failed_attempts+1 WHERE id=?")) {
                ps.setInt(1, id); ps.executeUpdate();
            }
            int n = 0;
            try (PreparedStatement ps = c.prepareStatement("SELECT failed_attempts FROM users WHERE id=?")) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) { if (rs.next()) n = rs.getInt(1); }
            }
            if (n >= 5) {   // lock time is computed in Java so it always matches the app server clock
                try (PreparedStatement ps = c.prepareStatement("UPDATE users SET locked_until=? WHERE id=?")) {
                    ps.setTimestamp(1, new Timestamp(System.currentTimeMillis() + 15 * 60 * 1000L)); ps.setInt(2, id); ps.executeUpdate();
                }
            }
        }
    }

    public void resetFails(int id) throws SQLException {
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement("UPDATE users SET failed_attempts=0, locked_until=NULL WHERE id=?")) {
            ps.setInt(1, id); ps.executeUpdate();
        }
    }

    public void updateProfile(int id, String name, String phone) throws SQLException {
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement("UPDATE users SET name=?, phone=? WHERE id=?")) {
            ps.setString(1, name); ps.setString(2, phone); ps.setInt(3, id); ps.executeUpdate();
        }
    }

    public void updatePassword(int id, String hash) throws SQLException {
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement("UPDATE users SET password_hash=? WHERE id=?")) {
            ps.setString(1, hash); ps.setInt(2, id); ps.executeUpdate();
        }
    }

    public void setActive(int id, boolean active) throws SQLException {
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement("UPDATE users SET active=? WHERE id=?")) {
            ps.setInt(1, active ? 1 : 0); ps.setInt(2, id); ps.executeUpdate();
        }
    }

    public List<User> list() throws SQLException {
        List<User> out = new ArrayList<>();
        try (Connection c = DB.get(); Statement st = c.createStatement(); ResultSet rs = st.executeQuery("SELECT * FROM users ORDER BY id DESC")) {
            while (rs.next()) out.add(map(rs));
        }
        return out;
    }
}
