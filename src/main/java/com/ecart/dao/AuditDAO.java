package com.ecart.dao;

import com.ecart.util.DB;
import java.sql.*;

/** Security / business audit trail. Never throws: logging must not break a request. */
public class AuditDAO {
    public static void log(Integer userId, String action, String detail, String ip) {
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement(
                "INSERT INTO audit_log(id,user_id,action,detail,ip) VALUES (" + DB.nextval("seq_audit_log") + ",?,?,?,?)")) {
            if (userId == null) ps.setNull(1, Types.INTEGER); else ps.setInt(1, userId);
            ps.setString(2, action);
            ps.setString(3, detail != null && detail.length() > 250 ? detail.substring(0, 250) : detail);
            ps.setString(4, ip);
            ps.executeUpdate();
        } catch (SQLException ignored) { }
    }
}
