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
import com.DAO.OrderDAO;

@WebServlet("/allorders")
public class AdminOrdersServlet extends HttpServlet {
   private static final long serialVersionUID = 1L;

   protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
      HttpSession hs = req.getSession(false);
      AdminBean ab = (hs == null) ? null : (AdminBean) hs.getAttribute("adminbean");

      if (ab == null) {
         res.sendRedirect("AdminLogin.html");
         return;
      }

      ArrayList<OrderBean> list = new OrderDAO().retrieveOrders(null);
      req.setAttribute("orderList", list);
      req.getRequestDispatcher("AdminOrders.jsp").forward(req, res);
   }
}
