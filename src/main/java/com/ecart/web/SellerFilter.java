package com.ecart.web;

import com.ecart.model.User;
import java.io.IOException;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.*;

/** Seller portal: only logged-in accounts with the SELLER role get in (checked on the server for every request). */
@WebFilter({"/seller", "/seller/*"})
public class SellerFilter implements Filter {
    @Override public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest rq = (HttpServletRequest) req;
        HttpServletResponse rs = (HttpServletResponse) res;
        HttpSession s = rq.getSession(false);
        User u = s == null ? null : (User) s.getAttribute("user");
        if (u == null) { rs.sendRedirect(rq.getContextPath() + "/login?portal=seller"); return; }
        if (!u.isSeller()) { rs.sendError(403); return; }
        chain.doFilter(req, res);
    }
}
