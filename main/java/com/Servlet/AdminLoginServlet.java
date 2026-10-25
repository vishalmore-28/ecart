package com.Servlet;

import com.Bean.AdminBean;
import com.DAO.AdminLoginDAO;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet({"/alogin"})
public class AdminLoginServlet extends HttpServlet {
   private static final long serialVersionUID = 1L;

   public AdminLoginServlet() {
   }

   protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
      AdminLoginDAO dao = new AdminLoginDAO();
      AdminBean ab = dao.CheckAdminDAO(req.getParameter("aname"), req.getParameter("apwd"));
      if (ab == null) {
         res.sendRedirect("AdminLogin.html?error=1");
      } else {
         HttpSession session = req.getSession();
         session.setAttribute("adminbean", ab);
         res.sendRedirect("adminhome");
      }

   }
}
