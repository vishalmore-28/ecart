package com.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class DeleteProductDAO {
    public int deleteProduct(String pcode) {
        String sql = "DELETE FROM PRODUCT WHERE PCODE = ?";
        try (Connection con = DBConnect.getCon();
             PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, pcode);
            return pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
