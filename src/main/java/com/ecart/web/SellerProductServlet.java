package com.ecart.web;

import com.ecart.dao.AuditDAO;
import com.ecart.dao.ProductDAO;
import com.ecart.model.Product;
import com.ecart.model.Seller;
import com.ecart.util.Gst;
import java.math.BigDecimal;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

/** A seller manages ONLY their own products: every read and write is limited to products.seller_id = me. */
@WebServlet("/seller/products")
public class SellerProductServlet extends SellerBaseServlet {
    private final ProductDAO dao = new ProductDAO();

    @Override protected void get(HttpServletRequest rq, HttpServletResponse rs) throws Exception {
        Seller me = shop(rq, rs, "products");
        if (me == null || !requireApproved(rq, rs, me)) return;
        if (rq.getParameter("new") != null || rq.getParameter("edit") != null) {
            Product p = null;
            if (rq.getParameter("edit") != null) {
                p = dao.find(intp(rq, "edit", 0));
                if (p == null || p.getSellerId() != me.getUserId()) { rs.sendError(404); return; }
            }
            rq.setAttribute("p", p);
            rq.setAttribute("categories", dao.categories());
            rq.setAttribute("slabs", Gst.SLABS);
            view(rq, rs, "seller/product-form");
            return;
        }
        ProductDAO.Filter f = new ProductDAO.Filter();
        f.q = s(rq, "q"); f.includeInactive = true; f.size = 200; f.sort = "new"; f.sellerId = me.getUserId();
        rq.setAttribute("q", f.q);
        rq.setAttribute("products", dao.search(f));
        view(rq, rs, "seller/products");
    }

    @Override protected void post(HttpServletRequest rq, HttpServletResponse rs) throws Exception {
        Seller me = shop(rq, rs, "products");
        if (me == null || !requireApproved(rq, rs, me)) return;
        int id = intp(rq, "id", 0);

        if ("toggle".equals(s(rq, "action"))) {
            Product p = dao.find(id);
            if (p != null && p.getSellerId() == me.getUserId() && dao.setActiveOwned(id, me.getUserId(), !p.isActive())) {
                AuditDAO.log(me.getUserId(), "SELLER_PRODUCT_TOGGLE", "id=" + id + " active=" + !p.isActive(), rq.getRemoteAddr());
                flash(rq, "ok", p.isActive() ? "Product hidden from the store." : "Product is live again.");
            }
            back(rq, rs, "/seller/products");
            return;
        }

        Product p = new Product();
        p.setId(id); p.setSellerId(me.getUserId());
        p.setName(s(rq, "name")); p.setBrand(s(rq, "brand")); p.setDescription(s(rq, "description"));
        p.setPrice(bd(rq, "price")); p.setMrp(bd(rq, "mrp")); p.setStock(intp(rq, "stock", -1));
        p.setImageUrl(s(rq, "imageUrl")); p.setActive(rq.getParameter("active") != null);
        p.setCategoryId(intp(rq, "categoryId", 0));
        // an unregistered seller cannot charge GST
        p.setGstPercent(me.isGstRegistered() ? bd(rq, "gst") : BigDecimal.ZERO);
        p.setHsnCode(s(rq, "hsn"));

        String err = null;
        if (p.getName().length() < 3 || p.getName().length() > 200) err = "Product name must be 3-200 characters.";
        else if (p.getCategoryId() <= 0) err = "Choose a category.";
        else if (p.getPrice() == null || p.getPrice().signum() <= 0 || p.getMrp() == null) err = "Enter a valid price and MRP.";
        else if (p.getPrice().compareTo(p.getMrp()) > 0) err = "Selling price cannot be higher than MRP.";
        else if (p.getPrice().compareTo(new BigDecimal("10000000")) > 0) err = "Price is too large.";
        else if (p.getDescription().length() > 2000) err = "Description is too long (max 2000 characters).";
        else if (p.getStock() < 0) err = "Stock must be 0 or more.";
        else if (!Gst.validSlab(p.getGstPercent())) err = "Choose a GST rate (0, 5, 12, 18 or 28 percent).";
        else if (me.isGstRegistered() && !Gst.validHsn(p.getHsnCode())) err = "Enter the HSN code (4, 6 or 8 digits) - it is required on a GST invoice.";
        else if (!me.isGstRegistered()) p.setHsnCode("");
        if (err == null && !p.getImageUrl().isEmpty() && !p.getImageUrl().matches("^(https://|/assets/).{3,480}$")) err = "Image URL must start with https:// (or leave blank).";
        if (err != null) {
            flash(rq, "err", err);
            redirect(rq, rs, id > 0 ? "/seller/products?edit=" + id : "/seller/products?new=1");
            return;
        }
        boolean ok = true;
        if (id > 0) ok = dao.updateOwned(p, me.getUserId());   // fails (false) if the product is not theirs
        else dao.save(p);
        if (!ok) { rs.sendError(404); return; }
        AuditDAO.log(me.getUserId(), id > 0 ? "SELLER_PRODUCT_EDIT" : "SELLER_PRODUCT_ADD", p.getName(), rq.getRemoteAddr());
        flash(rq, "ok", "Product saved.");
        redirect(rq, rs, "/seller/products");
    }
}
