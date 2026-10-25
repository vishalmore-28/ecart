
package com.DAO;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnect {

    static {
        try {
            Class.forName(DBInfo.driver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Connection getCon() throws Exception {

        Connection con = DriverManager.getConnection(
                DBInfo.dburl,
                DBInfo.dbuname,
                DBInfo.dbpwd);

        return con;
    }
}
