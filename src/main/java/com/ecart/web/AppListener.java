package com.ecart.web;

import com.ecart.dao.ProductDAO;
import com.ecart.dao.UserDAO;
import com.ecart.util.DB;
import com.ecart.util.Security;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * On start-up (Oracle or PostgreSQL): create sequences + tables that are missing, VERIFY the table structure,
 * seed the demo catalogue on the first run and create the first admin account.
 */
@WebListener
public class AppListener implements ServletContextListener {

    /** If any of these fail, the schema already contains OLD tables from the previous version of the project. */
    private static final String[] STRUCTURE_CHECKS = {
        "SELECT id,name,email,phone,password_hash,role,active,failed_attempts,locked_until,created_at FROM users WHERE 1=0",
        "SELECT id,name,icon FROM categories WHERE 1=0",
        "SELECT id,category_id,name,brand,description,price,mrp,stock,image_url,active,created_at,seller_id,gst_percent,hsn_code FROM products WHERE 1=0",
        "SELECT user_id,product_id,qty FROM cart_items WHERE 1=0",
        "SELECT user_id,product_id FROM wishlist WHERE 1=0",
        "SELECT id,user_id,full_name,phone,line1,line2,city,state,pincode,is_default FROM addresses WHERE 1=0",
        "SELECT code,percent,min_order,max_discount,active FROM coupons WHERE 1=0",
        "SELECT id,order_no,user_id,status,subtotal,discount,tax,shipping,total,coupon_code,ship_name,ship_phone,ship_address,payment_method,payment_status,txn_ref,card_last4,created_at,ship_state FROM shop_orders WHERE 1=0",
        "SELECT id,order_id,product_id,name,price,qty,seller_id,gst_percent,hsn_code,item_status,courier,tracking_no FROM order_items WHERE 1=0",
        "SELECT id,order_id,status,note,created_at FROM order_events WHERE 1=0",
        "SELECT id,product_id,user_id,rating,title,body,verified,created_at FROM reviews WHERE 1=0",
        "SELECT id,user_id,action,detail,ip,created_at FROM audit_log WHERE 1=0",
        "SELECT user_id,shop_name,business_type,pan,gstin,gst_registered,address_line1,address_line2,city,state,pincode,bank_holder,bank_ifsc,bank_last4,status,review_note,created_at,reviewed_at FROM sellers WHERE 1=0"
    };

    @Override public void contextInitialized(ServletContextEvent e) {
        try {
            String db = DB.isPostgres() ? "postgres" : "oracle";
            runScript("/schema-" + db + ".sql");
            verifyStructure();
            if (new ProductDAO().countAll() == 0) runScript("/seed-" + db + ".sql");
            UserDAO users = new UserDAO();
            if (!users.hasAdmin()) {
                String email = DB.env("ADMIN_EMAIL", "admin@ecart.local");
                String pass = DB.env("ADMIN_PASSWORD", "Admin@123");
                users.create("Store Admin", email, null, Security.hash(pass), "ADMIN");
                e.getServletContext().log("ecart: admin account created: " + email + "  (change the password!)");
            }
            e.getServletContext().log("ecart: database ready (" + (DB.isPostgres() ? "PostgreSQL" : "Oracle") + ")");
        } catch (Exception ex) {
            e.getServletContext().log("ecart: DATABASE INIT FAILED - " + ex.getMessage()
                    + "  | check DB_URL / DB_USER / DB_PASSWORD, that the database is running and the user may CREATE TABLE and CREATE SEQUENCE", ex);
        }
    }

    private void verifyStructure() throws SQLException {
        try (Connection c = DB.get(); Statement st = c.createStatement()) {
            for (String sql : STRUCTURE_CHECKS) {
                try { st.executeQuery(sql).close(); }
                catch (SQLException ex) {
                    throw new SQLException("An ecart 2.0 table in this database has unexpected columns (maybe left over from a test build). "
                            + "Drop the ecart 2.0 tables (users, products, shop_orders, ...) or use an empty schema. Failed check: " + sql.substring(sql.indexOf("FROM")), ex);
                }
            }
        }
    }

    private void runScript(String resource) throws Exception {
        StringBuilder sb = new StringBuilder();
        try (InputStream in = AppListener.class.getResourceAsStream(resource);
             BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) if (!line.trim().startsWith("--")) sb.append(line).append('\n');
        }
        try (Connection c = DB.get(); Statement st = c.createStatement()) {
            for (String sql : sb.toString().split(";\\s*\\n")) {
                sql = sql.trim();
                if (sql.isEmpty()) continue;
                if (sql.endsWith(";")) sql = sql.substring(0, sql.length() - 1);
                try { st.execute(sql); }
                catch (SQLException ex) {
                    if (!DB.isPostgres() && (ex.getErrorCode() == 955 || ex.getErrorCode() == 1430)) continue;   // Oracle ORA-00955: name already used, ORA-01430: column already exists (PostgreSQL scripts use IF NOT EXISTS)
                    throw ex;
                }
            }
        }
    }
}
