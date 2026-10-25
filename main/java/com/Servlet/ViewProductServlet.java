package com.Servlet;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.Bean.AdminBean;
import com.Bean.ProductBean;
import com.DAO.ViewProductDAO;
import com.Util.ProductUtil;

@WebServlet("/view")
public class ViewProductServlet extends HttpServlet
{
    protected void doGet(HttpServletRequest req,HttpServletResponse res)
    throws ServletException,IOException
    {
        HttpSession hs=req.getSession(false);

        AdminBean ab=(hs==null)?null:(AdminBean)hs.getAttribute("adminbean");

        if(ab==null)
        {
            req.getRequestDispatcher("AdminLogin.html").forward(req,res);
        }
        else
        {
            ArrayList<ProductBean> list =
                    new ViewProductDAO().retrieveProduct();

            // the session keeps the full list (used by Edit); the page shows the searched list
            hs.setAttribute("ProductList",list);

            String q=req.getParameter("q");
            req.setAttribute("q",q);
            req.setAttribute("ProductView",ProductUtil.filter(list,q));

            req.getRequestDispatcher("viewProduct.jsp").forward(req,res);
        }
    }
}