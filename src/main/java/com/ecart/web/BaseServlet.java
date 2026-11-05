package com.ecart.web;

import com.ecart.dao.CartDAO;
import com.ecart.dao.ProductDAO;
import com.ecart.dao.SellerDAO;
import com.ecart.dao.WishlistDAO;
import com.ecart.model.User;
import com.ecart.util.Security;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import javax.servlet.ServletException;
import javax.servlet.http.*;

/** Shared helpers: error handling, views, flash messages, safe redirects, parameter parsing. */
public abstract class BaseServlet extends HttpServlet {

    protected void get(HttpServletRequest rq, HttpServletResponse rs) throws Exception { rs.sendError(405); }
    protected void post(HttpServletRequest rq, HttpServletResponse rs) throws Exception { rs.sendError(405); }

    @Override protected final void doGet(HttpServletRequest rq, HttpServletResponse rs) throws ServletException, IOException { run(rq, rs, false); }
    @Override protected final void doPost(HttpServletRequest rq, HttpServletResponse rs) throws ServletException, IOException { run(rq, rs, true); }

    private void run(HttpServletRequest rq, HttpServletResponse rs, boolean post) throws IOException {
        try {
            if (post) post(rq, rs); else get(rq, rs);
        } catch (Exception e) {
            log("Request failed: " + rq.getRequestURI(), e);
            if (!rs.isCommitted()) rs.sendError(500);
        }
    }

    protected User user(HttpServletRequest rq) {
        HttpSession s = rq.getSession(false);
        return s == null ? null : (User) s.getAttribute("user");
    }

    protected void view(HttpServletRequest rq, HttpServletResponse rs, String name) throws Exception {
        HttpSession s = rq.getSession();
        rq.setAttribute("csrf", Security.csrf(s));
        Object f = s.getAttribute("flash");
        if (f != null) {
            rq.setAttribute("flash", f);
            rq.setAttribute("flashType", s.getAttribute("flashType"));
            s.removeAttribute("flash"); s.removeAttribute("flashType");
        }
        User u = user(rq);
        if (u != null) {
            rq.setAttribute("cartCount", new CartDAO().count(u.getId()));
            rq.setAttribute("wishCount", new WishlistDAO().ids(u.getId()).size());
            if (u.isAdmin()) rq.setAttribute("pendingSellers", new SellerDAO().pending());
        }
        rq.setAttribute("navCategories", new ProductDAO().categories());
        rq.getRequestDispatcher("/WEB-INF/views/" + name + ".jsp").forward(rq, rs);
    }

    protected void flash(HttpServletRequest rq, String type, String msg) {
        HttpSession s = rq.getSession();
        s.setAttribute("flash", msg);
        s.setAttribute("flashType", type);
    }

    protected void redirect(HttpServletRequest rq, HttpServletResponse rs, String path) throws IOException {
        rs.sendRedirect(rq.getContextPath() + path);
    }

    /** Go back to the page the user came from (same host only), else to the default path. */
    protected void back(HttpServletRequest rq, HttpServletResponse rs, String def) throws IOException {
        String ref = rq.getHeader("Referer");
        if (ref != null) {
            try {
                URI u = new URI(ref);
                if (rq.getServerName().equals(u.getHost()) && u.getPath() != null && u.getPath().startsWith(rq.getContextPath() + "/")) {
                    rs.sendRedirect(u.getPath() + (u.getRawQuery() == null ? "" : "?" + u.getRawQuery()));
                    return;
                }
            } catch (Exception ignored) { }
        }
        redirect(rq, rs, def);
    }

    /** Accepts only local paths like /orders; blocks open-redirects such as //evil.com. */
    protected String safeNext(String n, String def) {
        if (n != null && n.startsWith("/") && !n.startsWith("//") && !n.contains("\\") && !n.contains("\r") && !n.contains("\n")) return n;
        return def;
    }

    protected String s(HttpServletRequest rq, String name) {
        String v = rq.getParameter(name);
        return v == null ? "" : v.trim();
    }

    protected int intp(HttpServletRequest rq, String name, int def) {
        try { return Integer.parseInt(s(rq, name)); } catch (NumberFormatException e) { return def; }
    }

    protected BigDecimal bd(HttpServletRequest rq, String name) {
        try {
            BigDecimal v = new BigDecimal(s(rq, name));
            return v.signum() < 0 ? null : v;
        } catch (Exception e) { return null; }
    }
}
