package com.Servlet;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import com.Bean.ProductBean;

@WebServlet("/edit")
public class EditProductServlet extends HttpServlet {
   private static final long serialVersionUID = 1L;

   public EditProductServlet() { }
   
   @SuppressWarnings("unchecked")
   protected void doGet(HttpServletRequest req, HttpServletResponse res)  throws ServletException, IOException {

	    HttpSession session = req.getSession(false);

	    if (session == null || session.getAttribute("adminbean") == null) {
	        System.out.println("Session Expired");
	        res.sendRedirect("AdminLogin.html");
	    } else {

	        String pcode = req.getParameter("pcode");

	        ArrayList<ProductBean> list =  (ArrayList<ProductBean>) session.getAttribute("ProductList");

	        ProductBean pb = null;

	        if (list != null && pcode != null) {
	            for (ProductBean p : list) {
	                if (pcode.equals(p.getPcode())) {
	                    pb = p;
	                    break;
	                }
	            }
	        }

	        if (pb == null) {
	            res.sendRedirect("view");
	            return;
	        }

	        req.setAttribute("pb", pb);
	        req.getRequestDispatcher("editProduct.jsp").forward(req, res);
      }

   }
}
