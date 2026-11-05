package com.ecart.web;

import com.ecart.util.Security;
import java.io.IOException;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.*;

/** UTF-8, hardening headers and CSRF validation for every state-changing (POST) request. */
@WebFilter("/*")
public class SecurityFilter implements Filter {
    @Override public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest rq = (HttpServletRequest) req;
        HttpServletResponse rs = (HttpServletResponse) res;
        rq.setCharacterEncoding("UTF-8");
        rs.setHeader("X-Content-Type-Options", "nosniff");
        rs.setHeader("X-Frame-Options", "DENY");
        rs.setHeader("Referrer-Policy", "same-origin");
        rs.setHeader("Content-Security-Policy",
                "default-src 'self'; img-src 'self' data: https:; style-src 'self' 'unsafe-inline'; script-src 'self'; frame-ancestors 'none'; form-action 'self'");
        String path = rq.getRequestURI();
        if (!path.contains("/assets/")) {
            rs.setHeader("Cache-Control", "no-store");
        }
        if ("POST".equalsIgnoreCase(rq.getMethod())) {
            HttpSession s = rq.getSession(false);
            String expected = s == null ? null : (String) s.getAttribute("csrf");
            String actual = rq.getParameter("_csrf");
            if (expected == null || actual == null || !java.security.MessageDigest.isEqual(expected.getBytes("UTF-8"), actual.getBytes("UTF-8"))) {
                rs.sendError(403);
                return;
            }
        }
        chain.doFilter(req, res);
    }
}
