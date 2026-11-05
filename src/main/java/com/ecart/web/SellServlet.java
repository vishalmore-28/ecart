package com.ecart.web;

import com.ecart.dao.AuditDAO;
import com.ecart.dao.SellerDAO;
import com.ecart.model.Seller;
import com.ecart.model.User;
import com.ecart.util.DB;
import com.ecart.util.Gst;
import com.ecart.util.Security;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

/** "Sell on ecart": information page + seller (distributor) registration with GST / KYC details. */
@WebServlet({"/sell", "/sell/register"})
public class SellServlet extends BaseServlet {
    private final SellerDAO sellers = new SellerDAO();

    @Override protected void get(HttpServletRequest rq, HttpServletResponse rs) throws Exception {
        if ("/sell".equals(rq.getServletPath())) { view(rq, rs, "sell"); return; }
        if (blocked(rq, rs)) return;
        form(rq, rs, new LinkedHashMap<>(), null);
    }

    @Override protected void post(HttpServletRequest rq, HttpServletResponse rs) throws Exception {
        if (blocked(rq, rs)) return;
        Map<String, String> f = new LinkedHashMap<>();
        for (String k : new String[]{"ownerName", "email", "phone", "shopName", "businessType", "pan", "gstin", "addressLine1", "addressLine2",
                "city", "state", "pincode", "bankHolder", "ifsc"}) f.put(k, s(rq, k));
        f.put("email", f.get("email").toLowerCase());
        f.put("pan", f.get("pan").toUpperCase());
        f.put("gstin", f.get("gstin").toUpperCase());
        f.put("ifsc", f.get("ifsc").toUpperCase());
        boolean noGst = rq.getParameter("noGst") != null;
        if (noGst) f.put("noGst", "1");
        String pass = rq.getParameter("password") == null ? "" : rq.getParameter("password");
        String account = s(rq, "account").replaceAll("[\\s-]", "");

        String err = validate(f, noGst, pass, account, rq);
        if (err != null) { form(rq, rs, f, err); return; }

        Seller sl = new Seller();
        sl.setShopName(f.get("shopName")); sl.setBusinessType(f.get("businessType")); sl.setPan(f.get("pan"));
        sl.setGstRegistered(!noGst); sl.setGstin(noGst ? null : f.get("gstin"));
        sl.setAddressLine1(f.get("addressLine1")); sl.setAddressLine2(f.get("addressLine2").isEmpty() ? null : f.get("addressLine2"));
        sl.setCity(f.get("city")); sl.setState(f.get("state")); sl.setPincode(f.get("pincode"));
        sl.setBankHolder(f.get("bankHolder")); sl.setBankIfsc(f.get("ifsc")); sl.setBankLast4(account.substring(account.length() - 4));
        int id;
        try { id = sellers.register(f.get("ownerName"), f.get("email"), f.get("phone"), Security.hash(pass), sl); }
        catch (SQLException dup) {
            if (!DB.isDuplicate(dup)) throw dup;
            form(rq, rs, f, "This email or GSTIN is already registered.");
            return;
        }
        AuditDAO.log(id, "SELLER_REGISTER", f.get("shopName") + " / " + f.get("email"), rq.getRemoteAddr());
        HttpSession old = rq.getSession(true);
        old.invalidate();
        HttpSession ns = rq.getSession(true);
        User u = new com.ecart.dao.UserDAO().findById(id);
        u.setPasswordHash(null);
        ns.setAttribute("user", u);
        Security.csrf(ns);
        flash(rq, "ok", "Application submitted! The ecart team will verify your GST and business details. You can add products once the shop is approved.");
        redirect(rq, rs, "/seller");
    }

    private String validate(Map<String, String> f, boolean noGst, String pass, String account, HttpServletRequest rq) throws SQLException {
        if (f.get("ownerName").length() < 2 || f.get("ownerName").length() > 100) return "Enter the owner's full name.";
        if (!Security.validEmail(f.get("email"))) return "Enter a valid email address.";
        if (!Security.validPhone(f.get("phone"))) return "Enter a 10-digit mobile number.";
        if (!Security.strongPassword(pass)) return "Password needs at least 8 characters with letters and numbers.";
        if (!pass.equals(rq.getParameter("confirm"))) return "Passwords do not match.";
        if (f.get("shopName").length() < 3 || f.get("shopName").length() > 120) return "Shop / business name must be 3-120 characters.";
        if (!Gst.BUSINESS_TYPES.contains(f.get("businessType"))) return "Choose the type of business.";
        if (!Gst.validPan(f.get("pan"))) return "PAN must look like ABCDE1234F.";
        if (!Gst.panMatchesBusiness(f.get("pan"), f.get("businessType")))
            return "This PAN does not match the business type (the 4th letter of a PAN shows whether it belongs to a person, firm, company or HUF).";
        String state = f.get("state");
        if (!Gst.STATES.containsValue(state)) return "Choose the state of your shop.";
        if (!noGst) {
            String g = f.get("gstin");
            if (!Gst.validGstin(g)) return "Enter a valid 15-character GSTIN (for example 27AAPFU0939F1ZV).";
            if (!Gst.panOf(g).equals(f.get("pan"))) return "The PAN inside your GSTIN does not match the PAN you entered.";
            if (!state.equals(Gst.stateOfCode(g.substring(0, 2)))) return "Your GSTIN belongs to " + Gst.stateOfCode(g.substring(0, 2)) + ". Choose that state, or use the GSTIN of the state you sell from.";
            if (sellers.gstinExists(g)) return "This GSTIN is already registered on ecart.";
        }
        if (f.get("addressLine1").length() < 5 || f.get("addressLine1").length() > 200) return "Enter the pickup address (building, street).";
        if (f.get("addressLine2").length() > 200) return "Address line 2 is too long.";
        if (f.get("city").length() < 2 || f.get("city").length() > 80) return "Enter the city.";
        if (!Security.validPin(f.get("pincode"))) return "Pincode must be 6 digits.";
        if (f.get("bankHolder").length() < 2 || f.get("bankHolder").length() > 100) return "Enter the bank account holder name.";
        if (!account.matches("^\\d{9,18}$")) return "Bank account number must be 9 to 18 digits.";
        if (!account.equals(s(rq, "account2").replaceAll("[\\s-]", ""))) return "Bank account numbers do not match.";
        if (!Gst.validIfsc(f.get("ifsc"))) return "IFSC must look like HDFC0001234.";
        if (rq.getParameter("terms") == null) return "Please accept the seller terms to continue.";
        return null;
    }

    /** Logged-in people cannot open a second account from the same browser session. */
    private boolean blocked(HttpServletRequest rq, HttpServletResponse rs) throws Exception {
        User u = user(rq);
        if (u == null) return false;
        if (u.isSeller()) { redirect(rq, rs, "/seller"); return true; }
        flash(rq, "err", "You are signed in as " + (u.isAdmin() ? "an admin" : "a customer") + ". Sign out first to register a separate seller account.");
        redirect(rq, rs, "/sell");
        return true;
    }

    private void form(HttpServletRequest rq, HttpServletResponse rs, Map<String, String> f, String error) throws Exception {
        rq.setAttribute("f", f);
        rq.setAttribute("error", error);
        rq.setAttribute("states", Gst.STATES.values());
        rq.setAttribute("types", Gst.BUSINESS_TYPES);
        view(rq, rs, "sell-register");
    }
}
