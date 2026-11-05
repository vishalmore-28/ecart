package com.ecart.web;

import com.ecart.dao.AuditDAO;
import com.ecart.dao.SellerOrderDAO;
import com.ecart.model.Order;
import com.ecart.model.Seller;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/seller/orders")
public class SellerOrderServlet extends SellerBaseServlet {
    private final SellerOrderDAO dao = new SellerOrderDAO();

    @Override protected void get(HttpServletRequest rq, HttpServletResponse rs) throws Exception {
        Seller me = shop(rq, rs, "orders");
        if (me == null || !requireApproved(rq, rs, me)) return;
        if (rq.getParameter("id") != null) {
            Order o = dao.find(intp(rq, "id", 0), me.getUserId(), me.getState());
            if (o == null) { rs.sendError(404); return; }
            rq.setAttribute("o", o);
            String stage = "CANCELLED";   // status of this seller's first open item decides which button is shown
            for (com.ecart.model.OrderItem i : o.getItems()) if (!"CANCELLED".equals(i.getItemStatus())) { stage = i.getItemStatus(); break; }
            rq.setAttribute("stage", stage);
            view(rq, rs, "seller/order");
            return;
        }
        String status = s(rq, "status");
        if (!status.matches("PLACED|CONFIRMED|SHIPPED|DELIVERED")) status = "";
        rq.setAttribute("status", status);
        rq.setAttribute("orders", dao.list(me.getUserId(), status));
        view(rq, rs, "seller/orders");
    }

    @Override protected void post(HttpServletRequest rq, HttpServletResponse rs) throws Exception {
        Seller me = shop(rq, rs, "orders");
        if (me == null || !requireApproved(rq, rs, me)) return;
        int id = intp(rq, "id", 0);
        String next = s(rq, "status");
        if (!next.matches("CONFIRMED|SHIPPED|DELIVERED")) { rs.sendError(400); return; }
        String courier = null, tracking = null;
        if ("SHIPPED".equals(next)) {
            courier = s(rq, "courier"); tracking = s(rq, "tracking");
            if (courier.length() < 2 || courier.length() > 60 || !tracking.matches("^[A-Za-z0-9\\-/]{4,40}$")) {
                flash(rq, "err", "Enter the courier name and a valid tracking / AWB number to ship.");
                redirect(rq, rs, "/seller/orders?id=" + id);
                return;
            }
        }
        boolean ok = dao.advance(id, me.getUserId(), me.getShopName(), next, courier, tracking);
        AuditDAO.log(me.getUserId(), "SELLER_ORDER", "id=" + id + " -> " + next + " ok=" + ok, rq.getRemoteAddr());
        flash(rq, ok ? "ok" : "err", ok ? "Your items are now " + next + "." : "That step is not allowed for this order right now.");
        redirect(rq, rs, "/seller/orders?id=" + id);
    }
}
