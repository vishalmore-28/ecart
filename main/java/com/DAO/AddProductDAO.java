package com.DAO;

import com.Bean.ProductBean;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class AddProductDAO {
    public int insertProduct(ProductBean pb) throws Exception {
        try (Connection con = DBConnect.getCon();
             PreparedStatement pstmt = con.prepareStatement("INSERT INTO PRODUCT VALUES(?,?,?,?,?)")) {
            pstmt.setString(1, pb.getPcode());
            pstmt.setString(2, pb.getPname());
            pstmt.setString(3, pb.getPcompany());
            pstmt.setString(4, pb.getPrice());
            pstmt.setString(5, pb.getPqty());
            return pstmt.executeUpdate();
        }
    }
}
