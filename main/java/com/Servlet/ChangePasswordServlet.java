package com.Servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.Bean.CustomerBean;
import com.DAO.ChangePasswordDAO;
import com.DAO.CustomerLoginDAO;

@WebServlet("/changepwd")
public class ChangePasswordServlet extends HttpServlet {
   private static final long serialVersionUID = 1L;

   private CustomerBean customer(HttpServletRequest req) {
      HttpSession hs = req.getSession(false);
      return (hs == null) ? null : (CustomerBean) hs.getAttribute("cbean");
   }

   protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
      if (customer(req) == null) {
         res.sendRedirect("CustomerLogin.html");
         return;
      }
      req.getRequestDispatcher("CustomerChangePassword.jsp").forward(req, res);
   }

   protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
      CustomerBean cb = customer(req);
      if (cb == null) {
         res.sendRedirect("CustomerLogin.html");
         return;
      }

      String oldPwd = req.getParameter("oldpwd");
      String newPwd = req.getParameter("newpwd");
      String confirm = req.getParameter("confirmpwd");
      String msg;

      if (oldPwd == null || newPwd == null || confirm == null || newPwd.trim().length() < 4) {
         msg = "New password must have at least 4 characters";
      } else if (!newPwd.equals(confirm)) {
         msg = "New password and confirm password do not match";
      } else if (newPwd.equals(oldPwd)) {
         msg = "New password must be different from the old password";
      } else if (new CustomerLoginDAO().login(cb.getCuName(), oldPwd) == null) {
         msg = "Old password is wrong";
      } else if (new ChangePasswordDAO().changePassword(cb.getCuName(), newPwd) > 0) {
         cb.setcPwd(newPwd);
         msg = "Password changed successfully";
      } else {
         msg = "Password change failed";
      }

      req.setAttribute("msg", msg);
      req.getRequestDispatcher("CustomerChangePassword.jsp").forward(req, res);
   }
}
