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
import com.Bean.OrderBean;
import com.DAO.OrderDAO;

@WebServlet("/myorders")
public class CustomerOrdersServlet extends HttpServlet {
   private static final long serialVersionUID = 1L;

   protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
      HttpSession hs = req.getSession(false);
      CustomerBean cb = (hs == null) ? null : (CustomerBean) hs.getAttribute("cbean");

      if (cb == null) {
         res.sendRedirect("CustomerLogin.html");
         return;
      }

      ArrayList<OrderBean> list = new OrderDAO().retrieveOrders(cb.getCuName());
      req.setAttribute("orderList", list);
      req.getRequestDispatcher("CustomerOrders.jsp").forward(req, res);
   }
}
