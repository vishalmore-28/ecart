package com.ecart.web;

import com.ecart.dao.*;
import com.ecart.model.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

/** Admin reviews seller applications: approve, reject, suspend, reactivate. */
@WebServlet("/admin/sellers")
public class AdminSellerServlet extends BaseServlet {
    private final SellerDAO dao = new SellerDAO();

    @Override protected void get(HttpServletRequest rq, HttpServletResponse rs) throws Exception {
        rq.setAttribute("adminTab", "sellers");
        if (rq.getParameter("id") != null) {
            Seller s = dao.find(intp(rq, "id", 0));
            if (s == null) { rs.sendError(404); return; }
            rq.setAttribute("s", s);
            rq.setAttribute("counts", new ProductDAO().sellerCounts(s.getUserId()));
            view(rq, rs, "admin/seller");
            return;
        }
        String status = s(rq, "status");
        if (!status.matches("PENDING|APPROVED|REJECTED|SUSPENDED")) status = "";
        rq.setAttribute("status", status);
        rq.setAttribute("counts", dao.counts());
        rq.setAttribute("sellers", dao.list(status));
        view(rq, rs, "admin/sellers");
    }

    @Override protected void post(HttpServletRequest rq, HttpServletResponse rs) throws Exception {
        User me = user(rq);
        int id = intp(rq, "id", 0);
        Seller s = dao.find(id);
        if (s == null) { rs.sendError(404); return; }
        String action = s(rq, "action"), note = s(rq, "note");
        if (note.length() > 250) note = note.substring(0, 250);
        String from = s.getStatus(), to = null;
        switch (action) {
            case "approve":    if ("PENDING".equals(from)) to = "APPROVED"; break;
            case "reject":     if ("PENDING".equals(from)) to = "REJECTED"; break;
            case "suspend":    if ("APPROVED".equals(from)) to = "SUSPENDED"; break;
            case "reactivate": if ("SUSPENDED".equals(from)) to = "APPROVED"; break;
            case "reopen":     if ("REJECTED".equals(from)) to = "PENDING"; break;
            default: break;
        }
        if (to == null) flash(rq, "err", "That action is not possible while the shop is " + from + ".");
        else if (("REJECTED".equals(to) || "SUSPENDED".equals(to)) && note.length() < 5) flash(rq, "err", "Please write a short reason (it is shown to the seller).");
        else {
            dao.setStatus(id, to, "APPROVED".equals(to) ? null : note);
            AuditDAO.log(me.getId(), "SELLER_" + to, s.getShopName() + " (" + s.getEmail() + ")", rq.getRemoteAddr());
            flash(rq, "ok", s.getShopName() + " is now " + to + ".");
        }
        redirect(rq, rs, "/admin/sellers?id=" + id);
    }
}
