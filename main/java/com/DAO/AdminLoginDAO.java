package com.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.Bean.AdminBean;

public class AdminLoginDAO {
   public AdminLoginDAO() {
   }

   public AdminBean CheckAdminDAO(String username, String password) {
      AdminBean ab = null;
      String sql = "SELECT * FROM ADMIN WHERE ANAME=? AND APWD=?";
      try (Connection con = DBConnect.getCon();
           PreparedStatement pstmt = con.prepareStatement(sql)) {
         pstmt.setString(1, username);
         pstmt.setString(2, password);
         try (ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
               ab = new AdminBean();
               ab.setAname(rs.getString(1));
               ab.setApwd(null);
               ab.setAfname(rs.getString(3));
               ab.setAlname(rs.getString(4));
               ab.setAddress(rs.getString(5));
               ab.setMailid(rs.getString(6));
               ab.setAphno(rs.getString(7));
            }
         }
      } catch (Exception e) {
         System.err.println("Admin login failed: " + e.getClass().getSimpleName());
      }
      return ab;
   }
}
