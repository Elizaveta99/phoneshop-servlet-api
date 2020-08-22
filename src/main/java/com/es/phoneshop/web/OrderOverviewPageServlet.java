package com.es.phoneshop.web;

import com.es.phoneshop.dao.ArrayListOrderDao;
import com.es.phoneshop.dao.OrderDao;
import com.es.phoneshop.exception.ItemNotFoundException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class OrderOverviewPageServlet extends HttpServlet {

    protected static final String ORDER_OVERVIEW_JSP = "/WEB-INF/pages/orderOverview.jsp";
    private OrderDao orderDao;

    public OrderOverviewPageServlet() {
        orderDao = ArrayListOrderDao.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String secureOrderId = request.getPathInfo().substring(1);
        try {
            request.setAttribute("order", orderDao.getOrderBySecureId(secureOrderId));
            request.getRequestDispatcher(ORDER_OVERVIEW_JSP).forward(request, response);
        } catch (ItemNotFoundException e) {
            request.setAttribute("message", "Order not found");
            response.sendError(404);
        }
    }
}
