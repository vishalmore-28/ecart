package com.ecart.web;

import com.ecart.dao.*;
import com.ecart.model.*;
import com.ecart.util.PriceCalc;
import com.ecart.util.Security;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

/**
 * Checkout. Totals are ALWAYS recomputed on the server from the database cart - nothing price-related is trusted from the form.
 * UPI / card use a built-in DEMO gateway (no real money). Card numbers are validated but never stored (only the last 4 digits).
 */
@WebServlet("/checkout")
public class CheckoutServlet extends BaseServlet {

    @Override protected void get(HttpServletRequest rq, HttpServletResponse rs) throws Exception {
        User u = user(rq);
        List<CartItem> items = new CartDAO().items(u.getId());
        if (items.isEmpty()) { flash(rq, "err", "Your cart is empty."); redirect(rq, rs, "/cart"); return; }
        List<Address> addresses = new AddressDAO().list(u.getId());
        if (addresses.isEmpty()) { flash(rq, "ok", "Add a delivery address to continue."); redirect(rq, rs, "/addresses?next=/checkout"); return; }
        Coupon c = new CouponDAO().find((String) rq.getSession().getAttribute("coupon"));
        Totals t = PriceCalc.compute(items, c);
        rq.setAttribute("items", items);
        rq.setAttribute("addresses", addresses);
        rq.setAttribute("t", t);
        rq.setAttribute("codAllowed", t.getTotal().compareTo(PriceCalc.COD_LIMIT) <= 0);
        view(rq, rs, "checkout");
    }

    @Override protected void post(HttpServletRequest rq, HttpServletResponse rs) throws Exception {
        User u = user(rq);
        List<CartItem> items = new CartDAO().items(u.getId());
        if (items.isEmpty()) { redirect(rq, rs, "/cart"); return; }
        Address a = new AddressDAO().find(intp(rq, "addressId", 0), u.getId());
        if (a == null) { fail(rq, rs, "Please choose a delivery address."); return; }
        Totals t = PriceCalc.compute(items, new CouponDAO().find((String) rq.getSession().getAttribute("coupon")));

        String method = s(rq, "method");
        String payStatus, txn = null, last4 = null;
        switch (method) {
            case "COD":
                if (t.getTotal().compareTo(PriceCalc.COD_LIMIT) > 0) { fail(rq, rs, "Cash on delivery is not available above \u20B950,000."); return; }
                payStatus = "PENDING";
                break;
            case "UPI":
                if (!s(rq, "upi").matches("^[\\w.\\-]{2,}@[A-Za-z]{2,}$")) { fail(rq, rs, "Enter a valid UPI ID, e.g. name@bank."); return; }
                payStatus = "PAID"; txn = "UPI" + Security.token(6).toUpperCase().replaceAll("[^A-Z0-9]", "7");
                break;
            case "CARD": {
                String num = s(rq, "cardNumber").replaceAll("[\\s-]", "");
                String err = null;
                if (!Security.luhn(num)) err = "Card number is not valid.";
                else if (s(rq, "cardName").length() < 2) err = "Enter the name on the card.";
                else if (!validExpiry(s(rq, "cardExpiry"))) err = "Card expiry is invalid or expired (use MM/YY).";
                else if (!s(rq, "cardCvv").matches("\\d{3,4}")) err = "Enter the 3 or 4 digit CVV.";
                if (err != null) { fail(rq, rs, err); return; }
                payStatus = "PAID"; last4 = num.substring(num.length() - 4);
                txn = "CRD" + Security.token(6).toUpperCase().replaceAll("[^A-Z0-9]", "7");
                break;
            }
            default: fail(rq, rs, "Please choose a payment method."); return;
        }

        try {
            Order o = new OrderDAO().place(u.getId(), items, t, a, method, payStatus, txn, last4);
            rq.getSession().removeAttribute("coupon");
            AuditDAO.log(u.getId(), "ORDER", o.getOrderNo() + " " + t.getTotal() + " " + method, rq.getRemoteAddr());
            redirect(rq, rs, "/order?id=" + o.getId() + "&placed=1");
        } catch (StockException se) {
            flash(rq, "err", se.getMessage() + ". Please update your cart.");
            redirect(rq, rs, "/cart");
        }
    }

    private void fail(HttpServletRequest rq, HttpServletResponse rs, String msg) throws Exception {
        flash(rq, "err", msg);
        redirect(rq, rs, "/checkout");
    }

    private boolean validExpiry(String v) {
        if (!v.matches("^(0[1-9]|1[0-2])\\s*/\\s*\\d{2}$")) return false;
        String[] p = v.replace(" ", "").split("/");
        int m = Integer.parseInt(p[0]), y = 2000 + Integer.parseInt(p[1]);
        java.util.Calendar now = java.util.Calendar.getInstance();
        int cy = now.get(java.util.Calendar.YEAR), cm = now.get(java.util.Calendar.MONTH) + 1;
        return y > cy || (y == cy && m >= cm);
    }
}
