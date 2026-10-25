package com.Servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import com.Bean.ProductBean;
import com.DAO.UpdateProductDAO;

@WebServlet("/update")
public class UpdateProductServlet extends HttpServlet {
	   private static final long serialVersionUID = 1L;

	   public UpdateProductServlet() {
	   }

	   private static String clean(String s) {
	      return s == null ? "" : s.trim();
	   }

	   protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
	      HttpSession session = req.getSession(false);
	      if (session == null || session.getAttribute("adminbean") == null) {
	         System.out.println("Session Expired");
	         res.sendRedirect("AdminLogin.html");
	      } else {
	         // a new bean is used, so the session list is not changed unless the database is
	         ProductBean pb = new ProductBean();
	         pb.setPcode(clean(req.getParameter("pcode")));
	         pb.setPrice(clean(req.getParameter("price")));
	         pb.setPqty(clean(req.getParameter("pqty")));

	         boolean valid = !pb.getPcode().isEmpty();
	         try {
	            valid = valid && Double.parseDouble(pb.getPrice()) >= 0 && Integer.parseInt(pb.getPqty()) >= 0;
	         } catch (NumberFormatException e) {
	            valid = false;
	         }

	         if (!valid) {
	            req.setAttribute("data", "Product Update Failed, enter valid price and quantity");
	         } else {
	            int rowcount = (new UpdateProductDAO()).updateProduct(pb);
	            if (rowcount > 0) {
	               req.setAttribute("data", "Product Updated");
	            } else {
	               req.setAttribute("data", "Product Update Failed");
	            }
	         }
	         req.getRequestDispatcher("ProductUpdated.jsp").forward(req, res);
	      }

	   }
	}
