package com.ecart.web;

import com.ecart.dao.*;
import com.ecart.model.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet({"/orders", "/order"})
public class OrderServlet extends BaseServlet {
    private final OrderDAO dao = new OrderDAO();

    @Override protected void get(HttpServletRequest rq, HttpServletResponse rs) throws Exception {
        User u = user(rq);
        if ("/orders".equals(rq.getServletPath())) {
            rq.setAttribute("orders", dao.forUser(u.getId()));
            view(rq, rs, "orders");
            return;
        }
        Order o = dao.find(intp(rq, "id", 0), u.getId());
        if (o == null) { rs.sendError(404); return; }
        rq.setAttribute("o", o);
        rq.setAttribute("placed", "1".equals(s(rq, "placed")));
        view(rq, rs, "order");
    }

    @Override protected void post(HttpServletRequest rq, HttpServletResponse rs) throws Exception {
        User u = user(rq);
        int id = intp(rq, "id", 0);
        if ("cancel".equals(s(rq, "action"))) {
            boolean ok = dao.cancel(id, u.getId());
            AuditDAO.log(u.getId(), "ORDER_CANCEL", "id=" + id + " ok=" + ok, rq.getRemoteAddr());
            flash(rq, ok ? "ok" : "err", ok ? "Order cancelled. Any online payment will be refunded." : "This order can no longer be cancelled.");
        }
        redirect(rq, rs, "/order?id=" + id);
    }
}
