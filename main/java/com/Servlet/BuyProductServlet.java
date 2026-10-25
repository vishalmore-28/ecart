package com.Servlet;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import com.Bean.CustomerBean;
import com.Bean.ProductBean;
import com.DAO.BuyProductDAO;

@WebServlet("/buy")
public class BuyProductServlet extends HttpServlet {
	   private static final long serialVersionUID = 1L;

	   public BuyProductServlet() {
	   }

	   @SuppressWarnings("unchecked")
	   private ProductBean findProduct(HttpSession session, String pcode) {
	      ArrayList<ProductBean> list = (ArrayList<ProductBean>) session.getAttribute("CustProductList");
	      if (list == null || pcode == null) {
	         return null;
	      }
	      for (ProductBean p : list) {
	         if (pcode.equals(p.getPcode())) {
	            return p;
	         }
	      }
	      return null;
	   }

	   protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
	      HttpSession session = req.getSession(false);
	      CustomerBean cb = (session == null) ? null : (CustomerBean) session.getAttribute("cbean");

	      if (cb == null) {
	         System.out.println("Session Expired");
	         res.sendRedirect("CustomerLogin.html");
	      } else {
	         ProductBean pb = findProduct(session, req.getParameter("pcode"));
	         if (pb == null) {
	            res.sendRedirect("viewproducts");
	            return;
	         }
	         req.setAttribute("product", pb);
	         req.getRequestDispatcher("buyProduct.jsp").forward(req, res);
	      }
	   }

	   protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
	      HttpSession session = req.getSession(false);
	      CustomerBean cb = (session == null) ? null : (CustomerBean) session.getAttribute("cbean");

	      if (cb == null) {
	         System.out.println("Session Expired");
	         res.sendRedirect("CustomerLogin.html");
	      } else {
	         String pcode = req.getParameter("pcode");
	         String q = req.getParameter("pqty");
	         int qty = 0;
	         try {
	            qty = Integer.parseInt(q == null ? "" : q.trim());
	         } catch (NumberFormatException e) {
	            qty = 0;
	         }

	         if (qty <= 0) {
	            ProductBean shown = findProduct(session, pcode);
	            if (shown == null) {
	               res.sendRedirect("viewproducts");
	               return;
	            }
	            req.setAttribute("msg", "Enter a valid quantity greater than zero.");
	            req.setAttribute("product", shown);
	            req.getRequestDispatcher("buyProduct.jsp").forward(req, res);
	            return;
	         }

	         // only the code is taken from the form; price etc. come from the database
	         ProductBean pb = new ProductBean();
	         pb.setPcode(pcode);

	         int rowcount = (new BuyProductDAO()).buyProduct(pb, cb.getCuName(), qty);

	         if (rowcount > 0) {
	            double total = 0;
	            try {
	               total = Double.parseDouble(pb.getPrice()) * qty;
	            } catch (Exception e) {
	               e.printStackTrace();
	            }
	            req.setAttribute("total", total);
	            req.getRequestDispatcher("orderconfirmed.jsp").forward(req, res);
	         } else if (pb.getPname() == null) {
	            res.sendRedirect("viewproducts");
	         } else {
	            req.setAttribute("msg", "Order Failed, Insufficient Stock...");
	            req.setAttribute("product", pb);
	            req.getRequestDispatcher("buyProduct.jsp").forward(req, res);
	         }
	      }
	   }
	}
