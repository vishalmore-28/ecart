package com.ecart.web;

import com.ecart.dao.*;
import com.ecart.model.*;
import com.ecart.util.PriceCalc;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/cart")
public class CartServlet extends BaseServlet {
    private final CartDAO cart = new CartDAO();

    @Override protected void get(HttpServletRequest rq, HttpServletResponse rs) throws Exception {
        User u = user(rq);
        List<CartItem> items = cart.items(u.getId());
        Coupon c = new CouponDAO().find((String) rq.getSession().getAttribute("coupon"));
        Totals t = PriceCalc.compute(items, c);
        rq.setAttribute("items", items);
        rq.setAttribute("t", t);
        rq.setAttribute("freeShipAbove", PriceCalc.FREE_SHIPPING_ABOVE);
        view(rq, rs, "cart");
    }

    @Override protected void post(HttpServletRequest rq, HttpServletResponse rs) throws Exception {
        User u = user(rq);
        String action = s(rq, "action");
        int pid = intp(rq, "pid", 0);
        switch (action) {
            case "add": {
                Product p = new ProductDAO().find(pid);
                int want = Math.max(1, intp(rq, "qty", 1));
                if (p == null || !p.isLive() || p.getStock() < 1) {
                    flash(rq, "err", "Sorry, this product is not available right now.");
                    back(rq, rs, "/products");
                    return;
                }
                int newQty = Math.min(Math.min(cart.qty(u.getId(), pid) + want, 10), p.getStock());
                cart.set(u.getId(), pid, newQty);
                if ("1".equals(s(rq, "buy"))) { redirect(rq, rs, "/checkout"); return; }
                flash(rq, "ok", "Added to cart: " + p.getName());
                back(rq, rs, "/cart");
                return;
            }
            case "update": {
                Product p = new ProductDAO().find(pid);
                int q = intp(rq, "qty", 1);
                if (p != null) cart.set(u.getId(), pid, Math.min(Math.min(q, 10), p.getStock()));
                break;
            }
            case "remove": cart.remove(u.getId(), pid); break;
            case "clear": cart.clear(u.getId()); break;
            case "coupon": {
                String code = s(rq, "code").toUpperCase();
                if (code.isEmpty()) { rq.getSession().removeAttribute("coupon"); flash(rq, "ok", "Coupon removed."); }
                else if (new CouponDAO().find(code) == null) flash(rq, "err", "Invalid or expired coupon code.");
                else { rq.getSession().setAttribute("coupon", code); flash(rq, "ok", "Coupon " + code + " applied."); }
                break;
            }
            default: break;
        }
        redirect(rq, rs, "/cart");
    }
}
