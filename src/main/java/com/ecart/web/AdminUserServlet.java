package com.ecart.web;

import com.ecart.dao.*;
import com.ecart.model.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/admin/users")
public class AdminUserServlet extends BaseServlet {
    private final UserDAO dao = new UserDAO();

    @Override protected void get(HttpServletRequest rq, HttpServletResponse rs) throws Exception {
        rq.setAttribute("adminTab", "users");
        rq.setAttribute("users", dao.list());
        view(rq, rs, "admin/users");
    }

    @Override protected void post(HttpServletRequest rq, HttpServletResponse rs) throws Exception {
        User me = user(rq);
        User target = dao.findById(intp(rq, "id", 0));
        if (target == null) { rs.sendError(404); return; }
        if (target.getId() == me.getId() || target.isAdmin()) flash(rq, "err", "Admin accounts cannot be disabled here.");
        else {
            dao.setActive(target.getId(), !target.isActive());
            AuditDAO.log(me.getId(), "USER_TOGGLE", target.getEmail() + " active=" + !target.isActive(), rq.getRemoteAddr());
            flash(rq, "ok", target.isActive() ? "User disabled." : "User enabled.");
        }
        redirect(rq, rs, "/admin/users");
    }
}
