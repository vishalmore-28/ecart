package com.ecart.web;

import com.ecart.dao.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/admin")
public class AdminServlet extends BaseServlet {
    @Override protected void get(HttpServletRequest rq, HttpServletResponse rs) throws Exception {
        rq.setAttribute("stats", new OrderDAO().stats());
        rq.setAttribute("lowStock", new ProductDAO().lowStock(5));
        rq.setAttribute("recent", new OrderDAO().adminList(null));
        rq.setAttribute("adminTab", "dashboard");
        view(rq, rs, "admin/dashboard");
    }
}
