package com.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class ChangePasswordDAO {
    public int changePassword(String uName, String newPwd) {
        String sql = "UPDATE CUSTOMER SET CPWD = ? WHERE CNAME = ?";
        try (Connection con = DBConnect.getCon();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, newPwd);
            ps.setString(2, uName);
            return ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
