package com.ecart.dao;

import com.ecart.model.Category;
import com.ecart.model.Product;
import com.ecart.util.DB;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    /** Search / filter / sort / paging options for the catalogue. */
    public static class Filter {
        public String q = "";
        public int category = 0;
        public BigDecimal min, max;
        public boolean inStock;
        public boolean includeInactive;
        public int sellerId;   // 0 = any seller
        public String sort = "new";
        public int page = 1;
        public int size = 12;

        // getters so JSP/EL can read the values (EL needs bean properties)
        public String getQ() { return q; }
        public int getCategory() { return category; }
        public BigDecimal getMin() { return min; }
        public BigDecimal getMax() { return max; }
        public boolean isInStock() { return inStock; }
        public String getSort() { return sort; }
        public int getPage() { return page; }

        public String toQuery() {
            StringBuilder b = new StringBuilder();
            try {
                if (!q.isEmpty()) b.append("&q=").append(URLEncoder.encode(q, "UTF-8"));
                if (category > 0) b.append("&cat=").append(category);
                if (min != null) b.append("&min=").append(min.toPlainString());
                if (max != null) b.append("&max=").append(max.toPlainString());
                if (inStock) b.append("&instock=1");
                if (!"new".equals(sort)) b.append("&sort=").append(sort);
            } catch (Exception ignored) { }
            return b.length() == 0 ? "" : b.substring(1);
        }
    }

    private static final String BASE =
        "SELECT p.*, c.name cname, c.icon cicon, "
      + "(SELECT COALESCE(AVG(r.rating),0) FROM reviews r WHERE r.product_id=p.id) avg_r, "
      + "(SELECT COUNT(*) FROM reviews r WHERE r.product_id=p.id) rc, "
      + "sl.shop_name sname, sl.status sstatus "
      + "FROM products p JOIN categories c ON c.id=p.category_id LEFT JOIN sellers sl ON sl.user_id=p.seller_id ";

    /** Visible in the store: active, and (store-owned or the seller is approved). */
    private static final String LIVE = "p.active=1 AND (p.seller_id IS NULL OR sl.status='APPROVED') ";
    private static final String SELLER_JOIN = "LEFT JOIN sellers sl ON sl.user_id=p.seller_id ";

    static Product map(ResultSet rs) throws SQLException {
        Product p = new Product();
        p.setId(rs.getInt("id"));
        p.setCategoryId(rs.getInt("category_id"));
        p.setCategoryName(rs.getString("cname"));
        p.setIcon(rs.getString("cicon"));
        p.setName(rs.getString("name"));
        p.setBrand(rs.getString("brand"));
        p.setDescription(rs.getString("description"));
        p.setPrice(rs.getBigDecimal("price"));
        p.setMrp(rs.getBigDecimal("mrp"));
        p.setStock(rs.getInt("stock"));
        p.setImageUrl(rs.getString("image_url"));
        p.setActive(rs.getBoolean("active"));
        p.setAvgRating(rs.getDouble("avg_r"));
        p.setReviewCount(rs.getInt("rc"));
        p.setSellerId(rs.getInt("seller_id"));
        p.setSellerName(rs.getString("sname"));
        p.setSellerStatus(rs.getString("sstatus"));
        java.math.BigDecimal g = rs.getBigDecimal("gst_percent");
        if (g != null) p.setGstPercent(g);
        p.setHsnCode(rs.getString("hsn_code"));
        return p;
    }

    public static String productSelect() { return BASE; }

    private String where(Filter f, List<Object> args) {
        StringBuilder w = new StringBuilder("WHERE 1=1 ");
        if (!f.includeInactive) w.append("AND " + LIVE);
        if (f.sellerId > 0) { w.append("AND p.seller_id=? "); args.add(f.sellerId); }
        if (!f.q.isEmpty()) {
            w.append("AND (UPPER(p.name) LIKE ? ESCAPE '\\' OR UPPER(p.brand) LIKE ? ESCAPE '\\' OR UPPER(p.description) LIKE ? ESCAPE '\\' OR UPPER(c.name) LIKE ? ESCAPE '\\') ");
            String like = "%" + f.q.toUpperCase(java.util.Locale.ROOT).replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_") + "%";
            for (int i = 0; i < 4; i++) args.add(like);
        }
        if (f.category > 0) { w.append("AND p.category_id=? "); args.add(f.category); }
        if (f.min != null) { w.append("AND p.price>=? "); args.add(f.min); }
        if (f.max != null) { w.append("AND p.price<=? "); args.add(f.max); }
        if (f.inStock) w.append("AND p.stock>0 ");
        return w.toString();
    }

    private static String order(String sort) {
        switch (sort == null ? "" : sort) {
            case "priceasc": return "ORDER BY p.price ASC, p.id DESC";
            case "pricedesc": return "ORDER BY p.price DESC, p.id DESC";
            case "discount": return "ORDER BY (p.mrp-p.price)/p.mrp DESC, p.id DESC";
            case "rating": return "ORDER BY avg_r DESC, rc DESC, p.id DESC";
            default: return "ORDER BY p.id DESC";
        }
    }

    private static void bind(PreparedStatement ps, List<Object> args) throws SQLException {
        for (int i = 0; i < args.size(); i++) ps.setObject(i + 1, args.get(i));
    }

    public List<Product> search(Filter f) throws SQLException {
        List<Object> args = new ArrayList<>();
        String sql = DB.page(BASE + where(f, args) + order(f.sort));
        int offset = (Math.max(f.page, 1) - 1) * f.size;
        List<Product> out = new ArrayList<>();
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            bind(ps, args);
            DB.bindPage(ps, args.size() + 1, offset, f.size);
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) out.add(map(rs)); }
        }
        return out;
    }

    public int count(Filter f) throws SQLException {
        List<Object> args = new ArrayList<>();
        String sql = "SELECT COUNT(*) FROM products p JOIN categories c ON c.id=p.category_id " + SELLER_JOIN + where(f, args);
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement(sql)) {
            bind(ps, args);
            try (ResultSet rs = ps.executeQuery()) { rs.next(); return rs.getInt(1); }
        }
    }

    public Product find(int id) throws SQLException {
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement(BASE + "WHERE p.id=?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? map(rs) : null; }
        }
    }

    public List<Product> topDeals(int n) throws SQLException {
        return query(BASE + "WHERE " + LIVE + "AND p.stock>0 ORDER BY (p.mrp-p.price)/p.mrp DESC, p.id DESC", null, n);
    }

    public List<Product> latest(int n) throws SQLException {
        return query(BASE + "WHERE " + LIVE + "ORDER BY p.id DESC", null, n);
    }

    public List<Product> related(int categoryId, int excludeId, int n) throws SQLException {
        List<Product> out = new ArrayList<>();
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement(
                DB.top(BASE + "WHERE " + LIVE + "AND p.category_id=? AND p.id<>? ORDER BY p.id DESC"))) {
            ps.setInt(1, categoryId); ps.setInt(2, excludeId); ps.setInt(3, n);
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) out.add(map(rs)); }
        }
        return out;
    }

    public List<Product> lowStock(int threshold) throws SQLException {
        return query(BASE + "WHERE p.active=1 AND p.stock<=? ORDER BY p.stock ASC, p.id DESC", threshold, 8);
    }

    /** Low stock for one seller's own active products. */
    public List<Product> lowStock(int threshold, int sellerId) throws SQLException {
        List<Product> out = new ArrayList<>();
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement(DB.top(
                BASE + "WHERE p.active=1 AND p.seller_id=? AND p.stock<=? ORDER BY p.stock ASC, p.id DESC"))) {
            ps.setInt(1, sellerId); ps.setInt(2, threshold); ps.setInt(3, 8);
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) out.add(map(rs)); }
        }
        return out;
    }

    /** Product counts of one seller: {total, live}. */
    public int[] sellerCounts(int sellerId) throws SQLException {
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement(
                "SELECT COUNT(*), COALESCE(SUM(CASE WHEN active=1 THEN 1 ELSE 0 END),0) FROM products WHERE seller_id=?")) {
            ps.setInt(1, sellerId);
            try (ResultSet rs = ps.executeQuery()) { rs.next(); return new int[]{rs.getInt(1), rs.getInt(2)}; }
        }
    }

    /** Runs a query with an optional bind value (arg, may be null) and returns at most 'limit' rows. */
    private List<Product> query(String sql, Integer arg, int limit) throws SQLException {
        List<Product> out = new ArrayList<>();
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement(DB.top(sql))) {
            int i = 1;
            if (arg != null) ps.setInt(i++, arg);
            ps.setInt(i, limit);
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) out.add(map(rs)); }
        }
        return out;
    }

    public List<Category> categories() throws SQLException {
        List<Category> out = new ArrayList<>();
        try (Connection c = DB.get(); Statement st = c.createStatement(); ResultSet rs = st.executeQuery("SELECT * FROM categories ORDER BY id")) {
            while (rs.next()) {
                Category k = new Category();
                k.setId(rs.getInt("id")); k.setName(rs.getString("name")); k.setIcon(rs.getString("icon"));
                out.add(k);
            }
        }
        return out;
    }

    public int addCategory(String name, String icon) throws SQLException {
        try (Connection c = DB.get()) {
            try (PreparedStatement ps = c.prepareStatement("SELECT id FROM categories WHERE UPPER(name)=UPPER(?)")) {
                ps.setString(1, name);
                try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return rs.getInt(1); }
            }
            try (PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO categories(id,name,icon) VALUES (" + DB.nextval("seq_categories") + ",?,?)", DB.keyCols())) {
                ps.setString(1, name); ps.setString(2, icon == null || icon.isEmpty() ? "\uD83D\uDCE6" : icon);
                ps.executeUpdate();
                try (ResultSet k = ps.getGeneratedKeys()) { k.next(); return k.getInt(1); }
            }
        }
    }

    public int countAll() throws SQLException {
        try (Connection c = DB.get(); Statement st = c.createStatement(); ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM products WHERE active=1")) {
            rs.next(); return rs.getInt(1);
        }
    }

    public void save(Product p) throws SQLException {
        try (Connection c = DB.get()) {
            if (p.getId() == 0) {
                try (PreparedStatement ps = c.prepareStatement(
                        "INSERT INTO products(id,category_id,name,brand,description,price,mrp,stock,image_url,active,gst_percent,hsn_code,seller_id) VALUES (" + DB.nextval("seq_products") + ",?,?,?,?,?,?,?,?,?,?,?,?)")) {
                    fill(ps, p);
                    if (p.getSellerId() > 0) ps.setInt(12, p.getSellerId()); else ps.setNull(12, Types.INTEGER);
                    ps.executeUpdate();
                }
            } else {
                try (PreparedStatement ps = c.prepareStatement(
                        "UPDATE products SET category_id=?,name=?,brand=?,description=?,price=?,mrp=?,stock=?,image_url=?,active=?,gst_percent=?,hsn_code=? WHERE id=?")) {
                    fill(ps, p); ps.setInt(12, p.getId()); ps.executeUpdate();
                }
            }
        }
    }

    private void fill(PreparedStatement ps, Product p) throws SQLException {
        ps.setInt(1, p.getCategoryId()); ps.setString(2, p.getName()); ps.setString(3, p.getBrand());
        ps.setString(4, p.getDescription()); ps.setBigDecimal(5, p.getPrice()); ps.setBigDecimal(6, p.getMrp());
        ps.setInt(7, p.getStock()); ps.setString(8, p.getImageUrl()); ps.setInt(9, p.isActive() ? 1 : 0);
        ps.setBigDecimal(10, p.getGstPercent());
        if (p.getHsnCode() == null || p.getHsnCode().isEmpty()) ps.setNull(11, Types.VARCHAR); else ps.setString(11, p.getHsnCode());
    }

    /** Seller edit: only touches a product that belongs to this seller. Returns false when it is not theirs. */
    public boolean updateOwned(Product p, int sellerId) throws SQLException {
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement(
                "UPDATE products SET category_id=?,name=?,brand=?,description=?,price=?,mrp=?,stock=?,image_url=?,active=?,gst_percent=?,hsn_code=? WHERE id=? AND seller_id=?")) {
            fill(ps, p); ps.setInt(12, p.getId()); ps.setInt(13, sellerId);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean setActiveOwned(int id, int sellerId, boolean active) throws SQLException {
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement("UPDATE products SET active=? WHERE id=? AND seller_id=?")) {
            ps.setInt(1, active ? 1 : 0); ps.setInt(2, id); ps.setInt(3, sellerId);
            return ps.executeUpdate() > 0;
        }
    }

    public void setActive(int id, boolean active) throws SQLException {
        try (Connection c = DB.get(); PreparedStatement ps = c.prepareStatement("UPDATE products SET active=? WHERE id=?")) {
            ps.setInt(1, active ? 1 : 0); ps.setInt(2, id); ps.executeUpdate();
        }
    }
}
