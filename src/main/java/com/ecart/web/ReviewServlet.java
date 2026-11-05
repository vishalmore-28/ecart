package com.ecart.web;

import com.ecart.dao.*;
import com.ecart.model.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet("/review")
public class ReviewServlet extends BaseServlet {
    @Override protected void post(HttpServletRequest rq, HttpServletResponse rs) throws Exception {
        User u = user(rq);
        int pid = intp(rq, "pid", 0);
        int rating = intp(rq, "rating", 0);
        String title = s(rq, "title"), body = s(rq, "body");
        Product p = new ProductDAO().find(pid);
        if (p == null) { rs.sendError(404); return; }
        if (rating < 1 || rating > 5) flash(rq, "err", "Please choose a rating from 1 to 5.");
        else if (title.length() > 120 || body.length() > 1500) flash(rq, "err", "Your review is too long.");
        else {
            Review r = new Review();
            r.setProductId(pid); r.setUserId(u.getId()); r.setRating(rating); r.setTitle(title); r.setBody(body);
            r.setVerified(new OrderDAO().hasPurchased(u.getId(), pid));
            new ReviewDAO().save(r);
            flash(rq, "ok", "Thanks for your review!");
        }
        redirect(rq, rs, "/product?id=" + pid + "#reviews");
    }
}
