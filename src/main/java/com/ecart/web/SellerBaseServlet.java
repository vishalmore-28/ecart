package com.ecart.web;

import com.ecart.dao.SellerDAO;
import com.ecart.model.Seller;
import javax.servlet.http.*;

/** Base of every /seller/* page: loads the shop profile and shows the approval status. */
public abstract class SellerBaseServlet extends BaseServlet {
    protected final SellerDAO sellers = new SellerDAO();

    /** Loads the logged-in seller's shop. Returns null (and sends 403) if there is no shop profile. */
    protected Seller shop(HttpServletRequest rq, HttpServletResponse rs, String tab) throws Exception {
        Seller s = sellers.find(user(rq).getId());
        if (s == null) { rs.sendError(403); return null; }
        rq.setAttribute("shop", s);
        rq.setAttribute("sellerTab", tab);
        return s;
    }

    /** Sellers can trade only while APPROVED. */
    protected boolean requireApproved(HttpServletRequest rq, HttpServletResponse rs, Seller s) throws Exception {
        if (s.isApproved()) return true;
        flash(rq, "err", "Your shop is " + s.getStatus().toLowerCase() + ". You can use this section after ecart approves your shop.");
        redirect(rq, rs, "/seller");
        return false;
    }
}
