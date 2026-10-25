package com.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import com.Bean.CustomerBean;
import com.Util.PasswordUtil;

public class CustomerLoginDAO {
    public CustomerBean login(String uName, String pWord) {
        if (uName == null || pWord == null) return null;
        String sql = "SELECT CNAME, CPWD, CFNAME, CLNAME, CADDR, CMAIL, CMOBILE FROM CUSTOMER WHERE CNAME = ?";
        try (Connection con = DBConnect.getCon(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, uName);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next() || !PasswordUtil.verify(pWord, rs.getString("CPWD"))) return null;
                CustomerBean cb = new CustomerBean();
                cb.setCuName(rs.getString("CNAME"));
                // Do not place the stored password hash in the session bean.
                cb.setcPwd(null);
                cb.setfName(rs.getString("CFNAME"));
                cb.setlName(rs.getString("CLNAME"));
                cb.setAddr(rs.getString("CADDR"));
                cb.setMailId(rs.getString("CMAIL"));
                cb.setPhNo(rs.getString("CMOBILE"));
                return cb;
            }
        } catch (Exception e) {
            System.err.println("Customer login failed: " + e.getClass().getSimpleName());
            return null;
        }
    }
}
