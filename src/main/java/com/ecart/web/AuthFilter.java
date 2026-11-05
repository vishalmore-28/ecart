package com.ecart.web;

import java.io.IOException;
import java.net.URLEncoder;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.*;

/** Pages that need a logged-in customer. */
@WebFilter(urlPatterns = {"/cart", "/checkout", "/orders", "/order", "/wishlist", "/addresses", "/profile", "/review"})
public class AuthFilter implements Filter {
    @Override public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest rq = (HttpServletRequest) req;
        HttpServletResponse rs = (HttpServletResponse) res;
        HttpSession s = rq.getSession(false);
        if (s == null || s.getAttribute("user") == null) {
            String next = "";
            if ("GET".equalsIgnoreCase(rq.getMethod())) {
                String path = rq.getRequestURI().substring(rq.getContextPath().length());
                String q = rq.getQueryString();
                next = "?next=" + URLEncoder.encode(path + (q == null ? "" : "?" + q), "UTF-8");
            }
            rs.sendRedirect(rq.getContextPath() + "/login" + next);
            return;
        }
        chain.doFilter(req, res);
    }
}
