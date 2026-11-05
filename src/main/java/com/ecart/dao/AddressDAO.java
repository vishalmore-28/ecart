package com.ecart.dao;

import com.ecart.model.Address;
import com.ecart.util.DB;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AddressDAO {
    static Address map(ResultSet rs) throws SQLException {
        Address a = new Address();
        a.setId(rs.getInt("id")); a.setUserId(rs.getInt("user_id")); a.setFullName(rs.getString("full_name"));
        a.setPhone(rs.getString("phone")); a.setLine1(rs.getString("line1")); a.setLine2(rs.getString("line2"));
        a.setCity(rs.getString("city")); a.setState(rs.getString("state")); a.setPincode(rs.getString("pincode"));
        a.setDefaultAddress(rs.getBoolean("is_default"));
        return a;
    }

    public List<Address> list(int userId) throws SQLException {
        List<Address> out = new ArrayList<>();
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement("SELECT * FROM addresses WHERE user_id=? ORDER BY is_default DESC, id DESC")) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) out.add(map(rs)); }
        }
        return out;
    }

    /** Always scoped by user id so one customer can never read another's address. */
    public Address find(int id, int userId) throws SQLException {
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement("SELECT * FROM addresses WHERE id=? AND user_id=?")) {
            ps.setInt(1, id); ps.setInt(2, userId);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? map(rs) : null; }
        }
    }

    public void save(Address a) throws SQLException {
        boolean first = a.getId() == 0 && list(a.getUserId()).isEmpty();
        try (Connection c = DB.get()) {
            if (a.getId() == 0) {
                try (PreparedStatement ps = c.prepareStatement(
                        "INSERT INTO addresses(id,user_id,full_name,phone,line1,line2,city,state,pincode,is_default) VALUES (" + DB.nextval("seq_addresses") + ",?,?,?,?,?,?,?,?,?)")) {
                    ps.setInt(1, a.getUserId()); ps.setString(2, a.getFullName()); ps.setString(3, a.getPhone());
                    ps.setString(4, a.getLine1()); ps.setString(5, a.getLine2()); ps.setString(6, a.getCity());
                    ps.setString(7, a.getState()); ps.setString(8, a.getPincode()); ps.setInt(9, first ? 1 : 0);
                    ps.executeUpdate();
                }
            } else {
                try (PreparedStatement ps = c.prepareStatement(
                        "UPDATE addresses SET full_name=?,phone=?,line1=?,line2=?,city=?,state=?,pincode=? WHERE id=? AND user_id=?")) {
                    ps.setString(1, a.getFullName()); ps.setString(2, a.getPhone()); ps.setString(3, a.getLine1());
                    ps.setString(4, a.getLine2()); ps.setString(5, a.getCity()); ps.setString(6, a.getState());
                    ps.setString(7, a.getPincode()); ps.setInt(8, a.getId()); ps.setInt(9, a.getUserId());
                    ps.executeUpdate();
                }
            }
        }
    }

    public void delete(int id, int userId) throws SQLException {
        boolean wasDefault;
        try (Connection c = DB.get()) {
            Address a = find(id, userId);
            wasDefault = a != null && a.isDefaultAddress();
            try (PreparedStatement ps = c.prepareStatement("DELETE FROM addresses WHERE id=? AND user_id=?")) {
                ps.setInt(1, id); ps.setInt(2, userId); ps.executeUpdate();
            }
        }
        if (wasDefault) {
            List<Address> rest = list(userId);
            if (!rest.isEmpty()) setDefault(rest.get(0).getId(), userId);
        }
    }

    public void setDefault(int id, int userId) throws SQLException {
        try (Connection c = DB.get()) {
            try (PreparedStatement ps = c.prepareStatement("UPDATE addresses SET is_default=0 WHERE user_id=?")) {
                ps.setInt(1, userId); ps.executeUpdate();
            }
            try (PreparedStatement ps = c.prepareStatement("UPDATE addresses SET is_default=1 WHERE id=? AND user_id=?")) {
                ps.setInt(1, id); ps.setInt(2, userId); ps.executeUpdate();
            }
        }
    }
}
