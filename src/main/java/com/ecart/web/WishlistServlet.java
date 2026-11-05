package com.ecart.web;

import com.ecart.dao.*;
import com.ecart.model.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/wishlist")
public class WishlistServlet extends BaseServlet {
    @Override protected void get(HttpServletRequest rq, HttpServletResponse rs) throws Exception {
        User u = user(rq);
        rq.setAttribute("products", new WishlistDAO().list(u.getId()));
        view(rq, rs, "wishlist");
    }

    @Override protected void post(HttpServletRequest rq, HttpServletResponse rs) throws Exception {
        User u = user(rq);
        int pid = intp(rq, "pid", 0);
        Product p = new ProductDAO().find(pid);
        if (p == null) { rs.sendError(404); return; }
        if ("tocart".equals(s(rq, "action"))) {
            if (p.isActive() && p.getStock() > 0) {
                CartDAO cart = new CartDAO();
                cart.set(u.getId(), pid, Math.min(Math.min(cart.qty(u.getId(), pid) + 1, 10), p.getStock()));
                new WishlistDAO().toggle(u.getId(), pid); // removes it from wishlist (it was in there)
                flash(rq, "ok", "Moved to cart: " + p.getName());
            } else flash(rq, "err", "This product is out of stock.");
        } else {
            boolean added = new WishlistDAO().toggle(u.getId(), pid);
            flash(rq, "ok", added ? "Saved to wishlist." : "Removed from wishlist.");
        }
        back(rq, rs, "/wishlist");
    }
}
