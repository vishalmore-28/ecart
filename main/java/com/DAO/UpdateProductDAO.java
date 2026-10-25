package com.DAO;

import com.Bean.ProductBean;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class UpdateProductDAO {
    public int updateProduct(ProductBean pb) {
        String sql = "UPDATE PRODUCT SET PPRICE = ?, PQTY = ? WHERE PCODE = ?";
        try (Connection con = DBConnect.getCon();
             PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, pb.getPrice());
            pstmt.setString(2, pb.getPqty());
            pstmt.setString(3, pb.getPcode());
            return pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
