package com.ecart.web;

import com.ecart.dao.ProductDAO;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/home")
public class HomeServlet extends BaseServlet {
    @Override protected void get(HttpServletRequest rq, HttpServletResponse rs) throws Exception {
        ProductDAO dao = new ProductDAO();
        rq.setAttribute("deals", dao.topDeals(4));
        rq.setAttribute("latest", dao.latest(8));
        rq.setAttribute("wished", Wished.of(user(rq)));
        view(rq, rs, "home");
    }
}
