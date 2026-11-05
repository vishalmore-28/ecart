package com.ecart.web;

import com.ecart.dao.*;
import com.ecart.model.User;
import com.ecart.util.Security;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/profile")
public class ProfileServlet extends BaseServlet {
    private final UserDAO dao = new UserDAO();

    @Override protected void get(HttpServletRequest rq, HttpServletResponse rs) throws Exception {
        view(rq, rs, "profile");
    }

    @Override protected void post(HttpServletRequest rq, HttpServletResponse rs) throws Exception {
        User u = user(rq);
        if ("password".equals(s(rq, "action"))) {
            User full = dao.findById(u.getId());
            String cur = rq.getParameter("current") == null ? "" : rq.getParameter("current");
            String np = rq.getParameter("password") == null ? "" : rq.getParameter("password");
            if (!Security.verify(cur, full.getPasswordHash())) flash(rq, "err", "Current password is incorrect.");
            else if (!Security.strongPassword(np)) flash(rq, "err", "New password needs 8+ characters with letters and numbers.");
            else if (!np.equals(rq.getParameter("confirm"))) flash(rq, "err", "New passwords do not match.");
            else {
                dao.updatePassword(u.getId(), Security.hash(np));
                AuditDAO.log(u.getId(), "PASSWORD_CHANGE", null, rq.getRemoteAddr());
                flash(rq, "ok", "Password changed.");
            }
        } else {
            String name = s(rq, "name"), phone = s(rq, "phone");
            if (name.length() < 2 || name.length() > 100) flash(rq, "err", "Please enter your name.");
            else if (!phone.isEmpty() && !Security.validPhone(phone)) flash(rq, "err", "Phone must be a 10-digit mobile number.");
            else {
                dao.updateProfile(u.getId(), name, phone.isEmpty() ? null : phone);
                u.setName(name); u.setPhone(phone.isEmpty() ? null : phone);
                flash(rq, "ok", "Profile updated.");
            }
        }
        redirect(rq, rs, "/profile");
    }
}
