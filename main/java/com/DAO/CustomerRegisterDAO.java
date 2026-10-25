package com.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import com.Bean.CustomerBean;
import com.Util.PasswordUtil;

public class CustomerRegisterDAO {
    public int register(CustomerBean cb) {
        String sql = "INSERT INTO CUSTOMER (CNAME, CPWD, CFNAME, CLNAME, CADDR, CMAIL, CMOBILE) VALUES (?,?,?,?,?,?,?)";
        try {
            String passwordHash = PasswordUtil.hash(cb.getcPwd());
            try (Connection con = DBConnect.getCon(); PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, cb.getCuName());
                ps.setString(2, passwordHash);
                ps.setString(3, cb.getfName());
                ps.setString(4, cb.getlName());
                ps.setString(5, cb.getAddr());
                ps.setString(6, cb.getMailId());
                ps.setString(7, cb.getPhNo());
                return ps.executeUpdate();
            }
        } catch (Exception e) {
            System.err.println("Customer registration failed: " + e.getClass().getSimpleName());
            return 0;
        }
    }
}
