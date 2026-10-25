
package com.DAO;

import com.Bean.ProductBean;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class ViewCustomerProductDAO {

    public ViewCustomerProductDAO() {
    }

    public ArrayList<ProductBean> retrieveProduct() {

        ArrayList<ProductBean> list = new ArrayList<>();

        String query = "SELECT * FROM PRODUCT";

        try (
            Connection con = DBConnect.getCon();
            PreparedStatement pstmt = con.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery()
        ) {

            while (rs.next()) {

                ProductBean pb = new ProductBean();

                pb.setPcode(rs.getString(1));
                pb.setPname(rs.getString(2));
                pb.setPcompany(rs.getString(3));
                pb.setPrice(rs.getString(4));
                pb.setPqty(rs.getString(5));

                list.add(pb);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}
