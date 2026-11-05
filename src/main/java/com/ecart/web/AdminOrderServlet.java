package com.ecart.web;

import com.ecart.dao.*;
import com.ecart.model.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/admin/orders")
public class AdminOrderServlet extends BaseServlet {
    private final OrderDAO dao = new OrderDAO();

    @Override protected void get(HttpServletRequest rq, HttpServletResponse rs) throws Exception {
        rq.setAttribute("adminTab", "orders");
        if (rq.getParameter("id") != null) {
            Order o = dao.find(intp(rq, "id", 0), 0);
            if (o == null) { rs.sendError(404); return; }
            rq.setAttribute("o", o);
            view(rq, rs, "admin/order");
            return;
        }
        String status = s(rq, "status");
        rq.setAttribute("status", status);
        rq.setAttribute("orders", dao.adminList(status));
        view(rq, rs, "admin/orders");
    }

    @Override protected void post(HttpServletRequest rq, HttpServletResponse rs) throws Exception {
        User u = user(rq);
        int id = intp(rq, "id", 0);
        String next = s(rq, "status");
        if (!next.matches("CONFIRMED|SHIPPED|DELIVERED|CANCELLED")) { rs.sendError(400); return; }
        boolean ok = dao.adminUpdate(id, next, s(rq, "note"));
        AuditDAO.log(u.getId(), "ORDER_STATUS", "id=" + id + " -> " + next + " ok=" + ok, rq.getRemoteAddr());
        flash(rq, ok ? "ok" : "err", ok ? "Order updated to " + next + "." : "That status change is not allowed for this order.");
        redirect(rq, rs, "/admin/orders?id=" + id);
    }
}
