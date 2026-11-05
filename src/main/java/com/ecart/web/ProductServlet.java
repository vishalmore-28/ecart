package com.ecart.web;

import com.ecart.dao.*;
import com.ecart.model.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet({"/products", "/product"})
public class ProductServlet extends BaseServlet {
    @Override protected void get(HttpServletRequest rq, HttpServletResponse rs) throws Exception {
        ProductDAO dao = new ProductDAO();
        User u = user(rq);
        rq.setAttribute("wished", Wished.of(u));

        if ("/product".equals(rq.getServletPath())) {
            Product p = dao.find(intp(rq, "id", 0));
            if (p == null || (!p.isLive() && (u == null || !u.isAdmin()))) { rs.sendError(404); return; }
            rq.setAttribute("p", p);
            rq.setAttribute("reviews", new ReviewDAO().forProduct(p.getId()));
            rq.setAttribute("related", dao.related(p.getCategoryId(), p.getId(), 4));
            view(rq, rs, "product");
            return;
        }

        ProductDAO.Filter f = new ProductDAO.Filter();
        f.q = s(rq, "q");
        if (f.q.length() > 80) f.q = f.q.substring(0, 80);
        f.category = intp(rq, "cat", 0);
        f.min = bd(rq, "min");
        f.max = bd(rq, "max");
        f.inStock = "1".equals(s(rq, "instock"));
        f.sort = s(rq, "sort").isEmpty() ? "new" : s(rq, "sort");
        f.page = Math.max(1, intp(rq, "page", 1));
        int total = dao.count(f);
        int pages = Math.max(1, (total + f.size - 1) / f.size);
        if (f.page > pages) f.page = pages;
        rq.setAttribute("f", f);
        rq.setAttribute("qs", f.toQuery());
        rq.setAttribute("products", dao.search(f));
        rq.setAttribute("total", total);
        rq.setAttribute("pages", pages);
        view(rq, rs, "products");
    }
}
