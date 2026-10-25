package com.DAO;

import com.Bean.ProductBean;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class BuyProductDAO {
    // pb needs only the product code. Name, company and price are read from the
    // database (never trusted from the browser) and filled back into pb.
    public int buyProduct(ProductBean pb, String customerName, int qty) {
        if (pb == null || customerName == null || qty <= 0) {
            return 0;
        }

        String stockSql = "SELECT * FROM PRODUCT WHERE PCODE = ? FOR UPDATE";
        String updateSql = "UPDATE PRODUCT SET PQTY = PQTY - ? WHERE PCODE = ? AND PQTY >= ?";
        String orderSql = "INSERT INTO ORDERS VALUES (?,?,?,?,?,?)";

        try (Connection con = DBConnect.getCon()) {
            con.setAutoCommit(false);
            try {
                int available;
                try (PreparedStatement chk = con.prepareStatement(stockSql)) {
                    chk.setString(1, pb.getPcode());
                    try (ResultSet rs = chk.executeQuery()) {
                        if (!rs.next()) {
                            con.rollback();
                            return 0;
                        }
                        available = rs.getInt(5);
                        pb.setPname(rs.getString(2));
                        pb.setPcompany(rs.getString(3));
                        pb.setPrice(rs.getString(4));
                        pb.setPqty(rs.getString(5));
                    }
                }
                if (qty > available) {
                    con.rollback();
                    return 0;
                }
                try (PreparedStatement upd = con.prepareStatement(updateSql)) {
                    upd.setInt(1, qty);
                    upd.setString(2, pb.getPcode());
                    upd.setInt(3, qty);
                    if (upd.executeUpdate() != 1) {
                        con.rollback();
                        return 0;
                    }
                }
                try (PreparedStatement ins = con.prepareStatement(orderSql)) {
                    ins.setString(1, customerName);
                    ins.setString(2, pb.getPcode());
                    ins.setString(3, pb.getPname());
                    ins.setString(4, pb.getPcompany());
                    ins.setString(5, pb.getPrice());
                    ins.setInt(6, qty);
                    if (ins.executeUpdate() != 1) {
                        con.rollback();
                        return 0;
                    }
                }
                con.commit();
                return 1;
            } catch (Exception e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
