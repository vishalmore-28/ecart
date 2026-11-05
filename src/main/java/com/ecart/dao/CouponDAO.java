package com.ecart.dao;

import com.ecart.model.Coupon;
import com.ecart.util.DB;
import java.sql.*;

public class CouponDAO {
    public Coupon find(String code) throws SQLException {
        if (code == null) return null;
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement("SELECT * FROM coupons WHERE code=? AND active=1")) {
            ps.setString(1, code.trim().toUpperCase());
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                Coupon k = new Coupon();
                k.setCode(rs.getString("code")); k.setPercent(rs.getInt("percent"));
                k.setMinOrder(rs.getBigDecimal("min_order")); k.setMaxDiscount(rs.getBigDecimal("max_discount"));
                k.setActive(true);
                return k;
            }
        }
    }
}
