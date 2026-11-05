package com.ecart.web;

import com.ecart.dao.*;
import com.ecart.model.*;
import java.math.BigDecimal;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/admin/products")
public class AdminProductServlet extends BaseServlet {
    private final ProductDAO dao = new ProductDAO();

    @Override protected void get(HttpServletRequest rq, HttpServletResponse rs) throws Exception {
        rq.setAttribute("adminTab", "products");
        rq.setAttribute("categories", dao.categories());
        if (rq.getParameter("new") != null || rq.getParameter("edit") != null) {
            Product p = rq.getParameter("edit") != null ? dao.find(intp(rq, "edit", 0)) : null;
            if (rq.getParameter("edit") != null && p == null) { rs.sendError(404); return; }
            rq.setAttribute("p", p);
            view(rq, rs, "admin/product-form");
            return;
        }
        ProductDAO.Filter f = new ProductDAO.Filter();
        f.q = s(rq, "q"); f.includeInactive = true; f.size = 200; f.sort = "new";
        rq.setAttribute("q", f.q);
        rq.setAttribute("products", dao.search(f));
        view(rq, rs, "admin/products");
    }

    @Override protected void post(HttpServletRequest rq, HttpServletResponse rs) throws Exception {
        User u = user(rq);
        String action = s(rq, "action");
        int id = intp(rq, "id", 0);
        if ("toggle".equals(action)) {
            Product p = dao.find(id);
            if (p != null) {
                dao.setActive(id, !p.isActive());
                AuditDAO.log(u.getId(), "PRODUCT_TOGGLE", "id=" + id + " active=" + !p.isActive(), rq.getRemoteAddr());
                flash(rq, "ok", p.isActive() ? "Product hidden from the store." : "Product is live again.");
            }
            back(rq, rs, "/admin/products");
            return;
        }

        Product p = new Product();
        p.setId(id);
        p.setName(s(rq, "name")); p.setBrand(s(rq, "brand")); p.setDescription(s(rq, "description"));
        p.setPrice(bd(rq, "price")); p.setMrp(bd(rq, "mrp")); p.setStock(intp(rq, "stock", -1));
        p.setImageUrl(s(rq, "imageUrl")); p.setActive(rq.getParameter("active") != null);
        p.setGstPercent(bd(rq, "gst")); p.setHsnCode(s(rq, "hsn"));
        int cat = intp(rq, "categoryId", 0);
        String newCat = s(rq, "newCategory");
        if (!newCat.isEmpty() && newCat.length() <= 80) cat = dao.addCategory(newCat, s(rq, "newIcon"));
        p.setCategoryId(cat);

        String err = null;
        if (p.getName().length() < 3 || p.getName().length() > 200) err = "Product name must be 3-200 characters.";
        else if (cat <= 0) err = "Choose a category.";
        else if (p.getPrice() == null || p.getPrice().signum() <= 0 || p.getMrp() == null) err = "Enter a valid price and MRP.";
        else if (p.getPrice().compareTo(p.getMrp()) > 0) err = "Selling price cannot be higher than MRP.";
        else if (p.getPrice().compareTo(new BigDecimal("10000000")) > 0) err = "Price is too large.";
        else if (p.getDescription().length() > 2000) err = "Description is too long (max 2000 characters).";
        else if (p.getStock() < 0) err = "Stock must be 0 or more.";
        else if (!com.ecart.util.Gst.validSlab(p.getGstPercent())) err = "Choose a GST rate (0, 5, 12, 18 or 28 percent).";
        else if (!p.getHsnCode().isEmpty() && !com.ecart.util.Gst.validHsn(p.getHsnCode())) err = "HSN code must be 4, 6 or 8 digits.";
        else if (!p.getImageUrl().isEmpty() && !p.getImageUrl().matches("^(https://|/assets/).{3,480}$")) err = "Image URL must start with https:// (or leave blank).";
        if (err != null) {
            flash(rq, "err", err);
            redirect(rq, rs, id > 0 ? "/admin/products?edit=" + id : "/admin/products?new=1");
            return;
        }
        dao.save(p);
        AuditDAO.log(u.getId(), id > 0 ? "PRODUCT_EDIT" : "PRODUCT_ADD", p.getName(), rq.getRemoteAddr());
        flash(rq, "ok", "Product saved.");
        redirect(rq, rs, "/admin/products");
    }
}
