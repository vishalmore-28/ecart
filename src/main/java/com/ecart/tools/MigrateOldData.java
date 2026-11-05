package com.ecart.tools;

import com.ecart.dao.ProductDAO;
import com.ecart.dao.UserDAO;
import com.ecart.model.Product;
import com.ecart.util.DB;
import java.math.BigDecimal;
import java.sql.*;
import java.util.*;

/**
 * One-time import of data from the OLD ecart tables (PRODUCT, CUSTOMER, ORDERS) into the new ecart 2.0 tables.
 * Run it AFTER the new app has started once (so the new tables exist), with the same DB_* environment variables:
 *     mvn -q compile exec:java            (or in Eclipse: Run As > Java Application)
 *     add the word  dry  as an argument to only PRINT what would happen.
 * Old tables are only READ - they are never changed or deleted. Safe to run again: products and customers that
 * already exist are skipped and old orders are imported only once.
 */
public class MigrateOldData {
    private static boolean dry;
    private static int addedProducts, addedCustomers, addedOrders, skipped;

    public static void main(String[] args) throws Exception {
        dry = args.length > 0 && "dry".equalsIgnoreCase(args[0]);
        addedProducts = addedCustomers = addedOrders = skipped = 0;
        System.out.println("== ecart old-data import " + (dry ? "(DRY RUN - nothing is written)" : "") + " ==");
        try (Connection c = DB.get()) {
            if (!exists(c, "PRODUCT") && !exists(c, "CUSTOMER")) {
                System.out.println("Old tables PRODUCT / CUSTOMER were not found in this schema. Check DB_URL / DB_USER - nothing to import.");
                return;
            }
            Map<String, Integer> productByCode = importProducts(c);
            Map<String, Integer> userByName = importCustomers(c);
            importOrders(c, productByCode, userByName);
        }
        System.out.println("---------------------------------------------");
        System.out.println("Products added: " + addedProducts + " | Customers added: " + addedCustomers
                + " | Orders added: " + addedOrders + " | Skipped/warnings: " + skipped);
        System.out.println("NOTE: imported customers now log in with their E-MAIL address (same old password).");
    }

    private static boolean exists(Connection c, String table) {
        try (Statement st = c.createStatement()) { st.executeQuery("SELECT * FROM " + table + " WHERE 1=0").close(); return true; }
        catch (SQLException e) { return false; }
    }

    private static BigDecimal num(String s) {
        if (s == null) return BigDecimal.ZERO;
        String t = s.replaceAll("[^0-9.]", "");
        if (t.isEmpty() || t.equals(".")) return BigDecimal.ZERO;
        try { return new BigDecimal(t); } catch (NumberFormatException e) { return BigDecimal.ZERO; }
    }

    private static String clip(String s, int n) { return s == null ? null : (s.length() > n ? s.substring(0, n) : s); }

    // ---------------------------------------------------------------- products (old columns by position: code, name, company, price, qty)
    private static Map<String, Integer> importProducts(Connection c) throws Exception {
        Map<String, Integer> byCode = new HashMap<>();
        if (!exists(c, "PRODUCT")) return byCode;
        ProductDAO pd = new ProductDAO();
        int cat = dry ? 0 : pd.addCategory("Imported", "\uD83D\uDCE6");
        try (Statement st = c.createStatement(); ResultSet rs = st.executeQuery("SELECT * FROM PRODUCT")) {
            while (rs.next()) {
                String code = rs.getString(1), name = rs.getString(2), company = rs.getString(3);
                BigDecimal price = num(rs.getString(4)); int qty = num(rs.getString(5)).intValue();
                if (name == null || name.trim().isEmpty() || price.signum() <= 0) { System.out.println("  ! skip product " + code + " (no name/price)"); skipped++; continue; }
                Integer id = findProduct(c, name.trim(), company);
                if (id == null) {
                    System.out.println("  + product " + code + "  " + name + "  Rs." + price + "  qty " + qty);
                    if (!dry) {
                        Product p = new Product();
                        p.setCategoryId(cat); p.setName(clip(name.trim(), 200)); p.setBrand(clip(company == null ? null : company.trim(), 80));
                        p.setDescription("Imported from the previous version (product code " + code + ").");
                        p.setPrice(price); p.setMrp(price); p.setStock(Math.max(qty, 0)); p.setActive(true);
                        pd.save(p);
                        id = findProduct(c, name.trim(), company);
                    }
                    addedProducts++;
                } else { System.out.println("  = product already imported: " + name); }
                if (id != null) byCode.put(code, id);
            }
        }
        return byCode;
    }

    private static Integer findProduct(Connection c, String name, String brand) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(DB.top("SELECT id FROM products WHERE name=?" + (brand == null || brand.trim().isEmpty() ? "" : " AND brand=?")))) {
            int i = 1; ps.setString(i++, clip(name, 200));
            if (brand != null && !brand.trim().isEmpty()) ps.setString(i++, clip(brand.trim(), 80));
            ps.setInt(i, 1);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? rs.getInt(1) : null; }
        }
    }

    // ---------------------------------------------------------------- customers (old table has named columns)
    private static Map<String, Integer> importCustomers(Connection c) throws Exception {
        Map<String, Integer> byName = new HashMap<>();
        if (!exists(c, "CUSTOMER")) return byName;
        UserDAO ud = new UserDAO();
        System.out.println("Customers (old username -> new login e-mail):");
        try (Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT CNAME, CPWD, CFNAME, CLNAME, CADDR, CMAIL, CMOBILE FROM CUSTOMER")) {
            while (rs.next()) {
                String uname = rs.getString(1), hash = rs.getString(2), fn = rs.getString(3), ln = rs.getString(4), mail = rs.getString(6), mob = rs.getString(7);
                String email = mail == null ? "" : mail.trim().toLowerCase();
                if (!email.matches("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$")) email = (uname + "@imported.local").toLowerCase();
                String name = ((fn == null ? "" : fn.trim()) + " " + (ln == null ? "" : ln.trim())).trim();
                if (name.isEmpty()) name = uname;
                String phone = mob != null && mob.trim().matches("[6-9]\\d{9}") ? mob.trim() : null;
                if (hash == null || hash.split(":", -1).length != 3) { System.out.println("  ! skip " + uname + " (password is not in the hashed format)"); skipped++; continue; }
                com.ecart.model.User existing = ud.findByEmail(email);
                if (existing != null) { System.out.println("  = " + uname + " -> " + email + " (already exists)"); byName.put(uname, existing.getId()); continue; }
                System.out.println("  + " + uname + " -> " + email);
                if (!dry) byName.put(uname, ud.create(clip(name, 100), email, phone, hash, "USER"));
                addedCustomers++;
            }
        }
        return byName;
    }

    // ---------------------------------------------------------------- orders (old columns by position: customer, pcode, pname, pcompany, price, qty)
    private static void importOrders(Connection c, Map<String, Integer> productByCode, Map<String, Integer> userByName) throws Exception {
        if (!exists(c, "ORDERS")) return;
        try (Statement st = c.createStatement(); ResultSet rs = st.executeQuery("SELECT order_no FROM shop_orders WHERE order_no LIKE 'OLD%'")) {
            if (rs.next()) { System.out.println("Old orders were already imported earlier - skipping orders."); return; }
        }
        List<String[]> rows = new ArrayList<>();
        try (Statement st = c.createStatement(); ResultSet rs = st.executeQuery("SELECT * FROM ORDERS")) {
            while (rs.next()) rows.add(new String[]{rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5), rs.getString(6)});
        }
        int n = 0;
        for (String[] r : rows) {
            Integer uid = userByName.get(r[0]);
            Integer pid = productByCode.get(r[1]);
            if (pid == null) pid = findProduct(c, r[2] == null ? "" : r[2].trim(), r[3]);
            if (uid == null || pid == null) { System.out.println("  ! skip order of '" + r[0] + "' for " + r[2] + " (customer or product not imported)"); skipped++; continue; }
            BigDecimal price = num(r[4]); int qty = Math.max(1, num(r[5]).intValue());
            BigDecimal total = price.multiply(BigDecimal.valueOf(qty));
            System.out.println("  + order: " + r[0] + "  " + r[2] + " x" + qty + "  Rs." + total);
            if (!dry) {
                n++;
                String orderNo = "OLD" + String.format("%05d", n);
                int oid;
                try (PreparedStatement ps = c.prepareStatement(
                        "INSERT INTO shop_orders(id,order_no,user_id,status,subtotal,discount,tax,shipping,total,ship_name,ship_phone,ship_address,payment_method,payment_status) "
                      + "VALUES (" + DB.nextval("seq_orders") + ",?,?,'DELIVERED',?,0,0,0,?,?,?,?,'COD','PAID')", DB.keyCols())) {
                    com.ecart.model.User u = new UserDAO().findById(uid);
                    ps.setString(1, orderNo); ps.setInt(2, uid); ps.setBigDecimal(3, total); ps.setBigDecimal(4, total);
                    ps.setString(5, clip(u.getName(), 100)); ps.setString(6, u.getPhone() == null ? "0000000000" : u.getPhone());
                    ps.setString(7, "Imported from the previous version (address not recorded)");
                    ps.executeUpdate();
                    try (ResultSet k = ps.getGeneratedKeys()) { k.next(); oid = k.getInt(1); }
                }
                try (PreparedStatement ps = c.prepareStatement("INSERT INTO order_items(id,order_id,product_id,name,price,qty) VALUES (" + DB.nextval("seq_order_items") + ",?,?,?,?,?)")) {
                    ps.setInt(1, oid); ps.setInt(2, pid); ps.setString(3, clip(r[2] == null ? "Product" : r[2], 200)); ps.setBigDecimal(4, price); ps.setInt(5, qty); ps.executeUpdate();
                }
                try (PreparedStatement ps = c.prepareStatement("INSERT INTO order_events(id,order_id,status,note) VALUES (" + DB.nextval("seq_order_events") + ",?,'DELIVERED','Imported from the previous version')")) {
                    ps.setInt(1, oid); ps.executeUpdate();
                }
            }
            addedOrders++;
        }
    }
}
