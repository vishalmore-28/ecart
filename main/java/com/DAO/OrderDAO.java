package com.DAO;

import com.Bean.OrderBean;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class OrderDAO {

    // customer = null returns every order, otherwise only that customer's orders
    public ArrayList<OrderBean> retrieveOrders(String customer) {

        ArrayList<OrderBean> list = new ArrayList<>();

        try (
            Connection con = DBConnect.getCon();
            PreparedStatement pstmt = con.prepareStatement("SELECT * FROM ORDERS");
            ResultSet rs = pstmt.executeQuery()
        ) {

            while (rs.next()) {

                if (customer != null && !customer.equals(rs.getString(1))) {
                    continue;
                }

                OrderBean ob = new OrderBean();

                ob.setCustomer(rs.getString(1));
                ob.setPcode(rs.getString(2));
                ob.setPname(rs.getString(3));
                ob.setPcompany(rs.getString(4));
                ob.setPrice(rs.getString(5));
                ob.setQty(rs.getString(6));

                list.add(ob);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}
