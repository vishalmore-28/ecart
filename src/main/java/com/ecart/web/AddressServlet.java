package com.ecart.web;

import com.ecart.dao.AddressDAO;
import com.ecart.model.Address;
import com.ecart.model.User;
import com.ecart.util.Security;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/addresses")
public class AddressServlet extends BaseServlet {
    private final AddressDAO dao = new AddressDAO();

    @Override protected void get(HttpServletRequest rq, HttpServletResponse rs) throws Exception {
        User u = user(rq);
        rq.setAttribute("addresses", dao.list(u.getId()));
        int edit = intp(rq, "edit", 0);
        if (edit > 0) rq.setAttribute("a", dao.find(edit, u.getId()));
        rq.setAttribute("next", safeNext(rq.getParameter("next"), ""));
        view(rq, rs, "addresses");
    }

    @Override protected void post(HttpServletRequest rq, HttpServletResponse rs) throws Exception {
        User u = user(rq);
        String next = safeNext(rq.getParameter("next"), "");
        String action = s(rq, "action");
        int id = intp(rq, "id", 0);
        if ("delete".equals(action)) { dao.delete(id, u.getId()); flash(rq, "ok", "Address deleted."); }
        else if ("default".equals(action)) { dao.setDefault(id, u.getId()); flash(rq, "ok", "Default address updated."); }
        else {
            Address a = new Address();
            a.setId(id); a.setUserId(u.getId());
            a.setFullName(s(rq, "fullName")); a.setPhone(s(rq, "phone")); a.setLine1(s(rq, "line1")); a.setLine2(s(rq, "line2"));
            a.setCity(s(rq, "city")); a.setState(s(rq, "state")); a.setPincode(s(rq, "pincode"));
            String err = null;
            if (a.getFullName().length() < 2) err = "Enter the recipient's name.";
            else if (!Security.validPhone(a.getPhone())) err = "Enter a valid 10-digit mobile number.";
            else if (a.getLine1().length() < 3) err = "Enter the house / street address.";
            else if (a.getCity().isEmpty() || a.getState().isEmpty()) err = "City and state are required.";
            else if (!Security.validPin(a.getPincode())) err = "Pincode must be 6 digits.";
            else if (a.getLine1().length() > 200 || a.getLine2().length() > 200) err = "Address lines are too long.";
            if (err != null) {
                flash(rq, "err", err);
                redirect(rq, rs, "/addresses" + (id > 0 ? "?edit=" + id : "") );
                return;
            }
            if (id > 0 && dao.find(id, u.getId()) == null) { rs.sendError(404); return; }
            dao.save(a);
            flash(rq, "ok", "Address saved.");
            if (!next.isEmpty()) { redirect(rq, rs, next); return; }
        }
        redirect(rq, rs, "/addresses");
    }
}
