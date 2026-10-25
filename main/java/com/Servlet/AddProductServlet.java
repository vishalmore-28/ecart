package com.Servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.Bean.ProductBean;
import com.DAO.AddProductDAO;



@WebServlet({"/aps"})
public class AddProductServlet extends HttpServlet {
   private static final long serialVersionUID = 1L;

   private static String clean(String s) {
      return s == null ? "" : s.trim();
   }

   private static boolean isValid(ProductBean pb) {
      if (pb.getPcode().isEmpty() || pb.getPname().isEmpty() || pb.getPcompany().isEmpty()) {
         return false;
      }
      try {
         return Double.parseDouble(pb.getPrice()) >= 0 && Integer.parseInt(pb.getPqty()) >= 0;
      } catch (NumberFormatException e) {
         return false;
      }
   }

   protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException 
   {
	   req.setCharacterEncoding("UTF-8");
	   HttpSession session = req.getSession(false);
	   if (session == null || session.getAttribute("adminbean") == null) {
		   res.sendRedirect("AdminLogin.html");
		   return;
	   }
	   try {
		      ProductBean pb = new ProductBean();
		      pb.setPcode(clean(req.getParameter("pcode")));
		      pb.setPname(clean(req.getParameter("pname")));
		      pb.setPcompany(clean(req.getParameter("pcompany")));
		      pb.setPrice(clean(req.getParameter("pprice")));
		      pb.setPqty(clean(req.getParameter("pqty")));

		      if (!isValid(pb)) {
		         req.setAttribute("msg", "Add Product Failed, enter valid details");
		         req.getRequestDispatcher("addProduct.jsp").forward(req, res);
		         return;
		      }

		      int rowcount = (new AddProductDAO()).insertProduct(pb);
		      
		      if (rowcount > 0) {
		         req.setAttribute("msg", "Add Product Successfully");
		      } else {
		         req.setAttribute("msg", "Add Product Failed");
		      }
		      req.getRequestDispatcher("addProduct.jsp").forward(req, res);
	}
	   catch (Exception e) 
	   {
		   req.setAttribute("msg",e.toString());
		   req.getRequestDispatcher("error.jsp").forward(req, res);
	   
	   }
   }
   
}
