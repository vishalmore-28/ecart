package com.ecart.web;

import com.ecart.dao.ProductDAO;
import com.ecart.dao.SellerOrderDAO;
import com.ecart.model.Seller;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet({"/seller", "/seller/profile"})
public class SellerDashboardServlet extends SellerBaseServlet {
    @Override protected void get(HttpServletRequest rq, HttpServletResponse rs) throws Exception {
        boolean profile = "/seller/profile".equals(rq.getServletPath());
        Seller s = shop(rq, rs, profile ? "profile" : "dashboard");
        if (s == null) return;
        if (profile) { view(rq, rs, "seller/profile"); return; }
        if (s.isApproved()) {
            ProductDAO pd = new ProductDAO();
            rq.setAttribute("counts", pd.sellerCounts(s.getUserId()));
            rq.setAttribute("lowStock", pd.lowStock(5, s.getUserId()));
            rq.setAttribute("stats", new SellerOrderDAO().stats(s.getUserId()));
            rq.setAttribute("recent", new SellerOrderDAO().list(s.getUserId(), null));
        }
        view(rq, rs, "seller/dashboard");
    }
}
