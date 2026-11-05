package com.ecart.web;

import com.ecart.dao.AuditDAO;
import com.ecart.dao.UserDAO;
import com.ecart.model.User;
import com.ecart.util.Security;
import java.sql.SQLException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet({"/login", "/register", "/logout"})
public class AuthServlet extends BaseServlet {
    private final UserDAO users = new UserDAO();

    @Override protected void get(HttpServletRequest rq, HttpServletResponse rs) throws Exception {
        if (user(rq) != null) { redirect(rq, rs, "/"); return; }
        if ("/logout".equals(rq.getServletPath())) { redirect(rq, rs, "/"); return; }
        rq.setAttribute("next", safeNext(rq.getParameter("next"), ""));
        rq.setAttribute("portal", s(rq, "portal"));
        view(rq, rs, "/register".equals(rq.getServletPath()) ? "register" : "login");
    }

    @Override protected void post(HttpServletRequest rq, HttpServletResponse rs) throws Exception {
        switch (rq.getServletPath()) {
            case "/login": login(rq, rs); break;
            case "/register": register(rq, rs); break;
            default:
                HttpSession s = rq.getSession(false);
                if (s != null) s.invalidate();
                redirect(rq, rs, "/");
        }
    }

    private void login(HttpServletRequest rq, HttpServletResponse rs) throws Exception {
        String email = s(rq, "email").toLowerCase();
        String pass = rq.getParameter("password") == null ? "" : rq.getParameter("password");
        String next = safeNext(rq.getParameter("next"), "");
        User u = users.findByEmail(email);
        String error = null;
        if (u == null) {
            error = "Invalid email or password.";
        } else if (u.getLockedUntil() != null && u.getLockedUntil().getTime() > System.currentTimeMillis()) {
            error = "Too many failed attempts. Try again in a few minutes.";
        } else if (!Security.verify(pass, u.getPasswordHash())) {
            users.fail(u.getId());
            AuditDAO.log(u.getId(), "LOGIN_FAIL", email, rq.getRemoteAddr());
            error = "Invalid email or password.";
        } else if (!u.isActive()) {
            error = "This account has been disabled. Please contact support.";
        }
        if (error != null) {
            rq.setAttribute("error", error);
            rq.setAttribute("email", email);
            rq.setAttribute("next", next);
            rq.setAttribute("portal", s(rq, "portal"));
            view(rq, rs, "login");
            return;
        }
        users.resetFails(u.getId());
        AuditDAO.log(u.getId(), "LOGIN", email, rq.getRemoteAddr());
        startSession(rq, u);
        redirect(rq, rs, !next.isEmpty() ? next : (u.isAdmin() ? "/admin" : u.isSeller() ? "/seller" : "/"));
    }

    private void register(HttpServletRequest rq, HttpServletResponse rs) throws Exception {
        String name = s(rq, "name"), email = s(rq, "email").toLowerCase(), phone = s(rq, "phone");
        String pass = rq.getParameter("password") == null ? "" : rq.getParameter("password");
        String error = null;
        if (name.length() < 2 || name.length() > 100) error = "Please enter your full name.";
        else if (!Security.validEmail(email)) error = "Please enter a valid email address.";
        else if (!phone.isEmpty() && !Security.validPhone(phone)) error = "Phone must be a 10-digit mobile number.";
        else if (!Security.strongPassword(pass)) error = "Password needs at least 8 characters with letters and numbers.";
        else if (!pass.equals(rq.getParameter("confirm"))) error = "Passwords do not match.";
        else if (users.findByEmail(email) != null) error = "An account with this email already exists.";
        if (error != null) {
            rq.setAttribute("error", error);
            rq.setAttribute("name", name); rq.setAttribute("email", email); rq.setAttribute("phone", phone);
            view(rq, rs, "register");
            return;
        }
        int id;
        try { id = users.create(name, email, phone.isEmpty() ? null : phone, Security.hash(pass), "USER"); }
        catch (SQLException dup) { rq.setAttribute("error", "An account with this email already exists."); view(rq, rs, "register"); return; }
        AuditDAO.log(id, "REGISTER", email, rq.getRemoteAddr());
        startSession(rq, users.findById(id));
        flash(rq, "ok", "Welcome to ecart, " + name + "!");
        redirect(rq, rs, "/");
    }

    private void startSession(HttpServletRequest rq, User u) {
        HttpSession old = rq.getSession(true);
        old.invalidate();                       // new session id after login (prevents session fixation)
        HttpSession s = rq.getSession(true);
        u.setPasswordHash(null);
        s.setAttribute("user", u);
        Security.csrf(s);
    }
}
