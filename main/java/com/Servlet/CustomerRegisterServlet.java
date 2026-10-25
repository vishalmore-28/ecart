package com.Servlet;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.Bean.CustomerBean;
import com.DAO.CustomerRegisterDAO;
@WebServlet("/creg")
public class CustomerRegisterServlet extends HttpServlet
{
	protected void doPost(HttpServletRequest req,HttpServletResponse res)
	throws ServletException,IOException
	{
		CustomerBean cb = new CustomerBean();

		cb.setCuName(req.getParameter("uname"));
		cb.setcPwd(req.getParameter("pwd"));
		cb.setfName(req.getParameter("fname"));
		cb.setlName(req.getParameter("lname"));
		cb.setAddr(req.getParameter("addr"));
		cb.setMailId(req.getParameter("mail"));
		cb.setPhNo(req.getParameter("phno"));

		int k = 0;

		if(cb.getCuName()==null || cb.getCuName().trim().isEmpty()
				|| cb.getcPwd()==null || cb.getcPwd().trim().length()<4
				|| cb.getfName()==null || cb.getfName().trim().isEmpty())
		{
			req.setAttribute("msg","Registration Failed, user name, first name and a password of at least 4 characters are required...");
		}
		else
		{
			k = new CustomerRegisterDAO().register(cb);

			if(k>0)
			{
				req.setAttribute("msg","Customer Registered Successfully...");
			}
			else
			{
				req.setAttribute("msg","Registration Failed, this user name may already exist...");
			}
		}

		RequestDispatcher rd =
				req.getRequestDispatcher("CustomerRegisterSuccess.jsp");
		rd.forward(req,res);
	}
}