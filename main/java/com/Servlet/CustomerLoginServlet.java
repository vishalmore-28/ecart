package com.Servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.Bean.CustomerBean;
import com.DAO.CustomerLoginDAO;

@WebServlet("/clogin")
public class CustomerLoginServlet extends HttpServlet
{
	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest req,HttpServletResponse res)
	throws ServletException,IOException
	{
		CustomerBean cb =
				new CustomerLoginDAO().login(
						req.getParameter("uname"),
						req.getParameter("pwd"));

		if(cb==null)
		{
			res.sendRedirect("CustomerLogin.html?error=1");
		}
		else
		{
			HttpSession hs = req.getSession();

			hs.setAttribute("cbean", cb);

			res.sendRedirect("CustomerHome.jsp");
		}
	}
}
