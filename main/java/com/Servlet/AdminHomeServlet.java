package com.Servlet;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.Bean.AdminBean;
import com.Bean.OrderBean;
import com.Bean.ProductBean;
import com.DAO.OrderDAO;
import com.DAO.ViewProductDAO;
import com.Util.ProductUtil;

@WebServlet("/adminhome")
public class AdminHomeServlet extends HttpServlet {
   private static final long serialVersionUID = 1L;

   protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
      HttpSession hs = req.getSession(false);
      AdminBean ab = (hs == null) ? null : (AdminBean) hs.getAttribute("adminbean");

      if (ab == null) {
         res.sendRedirect("AdminLogin.html");
         return;
      }

      ArrayList<ProductBean> products = new ViewProductDAO().retrieveProduct();
      ArrayList<OrderBean> orders = new OrderDAO().retrieveOrders(null);

      int units = 0;
      int low = 0;
      int out = 0;
      for (ProductBean p : products) {
         int q = ProductUtil.qty(p.getPqty());
         units += q;
         if (q <= 0) {
            out++;
         } else if (q <= ProductUtil.LOW_STOCK) {
            low++;
         }
      }

      double sales = 0;
      for (OrderBean o : orders) {
         sales += ProductUtil.price(o.getPrice()) * ProductUtil.qty(o.getQty());
      }

      req.setAttribute("statProducts", products.size());
      req.setAttribute("statUnits", units);
      req.setAttribute("statLow", low);
      req.setAttribute("statOut", out);
      req.setAttribute("statOrders", orders.size());
      req.setAttribute("statSales", String.format("%.2f", sales));

      req.getRequestDispatcher("AdminHome.jsp").forward(req, res);
   }
}
