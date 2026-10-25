package com.Servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import com.DAO.DeleteProductDAO;

@WebServlet({"/delete"})
public class DeleteProductServlet extends HttpServlet {
   private static final long serialVersionUID = 1L;

   public DeleteProductServlet() {
   }

   protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
      HttpSession session = req.getSession(false);
      if (session == null || session.getAttribute("adminbean") == null) {
         System.out.println("Session Expired");
         res.sendRedirect("AdminLogin.html");
      } else {
         int rowcount = (new DeleteProductDAO()).deleteProduct(req.getParameter("pcode"));
         req.setAttribute("data", rowcount > 0 ? "Product deleted" : "Product delete failed");
         req.getRequestDispatcher("DeleteProduct.jsp").forward(req, res);
      }

   }
}
