
package com.Servlet;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.Bean.CustomerBean;
import com.Bean.ProductBean;
import com.DAO.ViewCustomerProductDAO;
import com.Util.ProductUtil;

@WebServlet("/viewproducts")
public class ViewCustomerProductServlet extends HttpServlet {

    protected void doGet(HttpServletRequest req,
                         HttpServletResponse res)
            throws ServletException, IOException {

        HttpSession hs = req.getSession(false);

        CustomerBean cb = (hs == null) ? null :
                (CustomerBean) hs.getAttribute("cbean");

        if (cb == null) {
            res.sendRedirect("CustomerLogin.html");
            return;
        }

        ArrayList<ProductBean> list =
                new ViewCustomerProductDAO().retrieveProduct();

        // CustomerViewProduct.jsp and BuyProductServlet read this session attribute.
        hs.setAttribute("CustProductList", list);

        String q = req.getParameter("q");
        req.setAttribute("q", q);
        req.setAttribute("CustView", ProductUtil.filter(list, q));

        req.getRequestDispatcher("CustomerViewProduct.jsp")
           .forward(req, res);
    }
}
