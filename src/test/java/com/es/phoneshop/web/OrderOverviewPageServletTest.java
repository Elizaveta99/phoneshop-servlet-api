package com.es.phoneshop.web;

import com.es.phoneshop.dao.OrderDao;
import com.es.phoneshop.exception.ItemNotFoundException;
import com.es.phoneshop.model.order.Order;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OrderOverviewPageServletTest {
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private RequestDispatcher requestDispatcher;
    @Mock
    private OrderDao orderDao;
    @Mock
    private Order order;

    @InjectMocks
    private OrderOverviewPageServlet servlet = new OrderOverviewPageServlet();

    @Before
    public void setup() {
        when(request.getRequestDispatcher(OrderOverviewPageServlet.ORDER_OVERVIEW_JSP)).thenReturn(requestDispatcher);

    }

    @Test
    public void testOrderOverviewPageServletDoGet() throws IOException, ServletException {
        when(request.getPathInfo()).thenReturn("/secureId1");
        when(orderDao.getOrderBySecureId("secureId1")).thenReturn(order);

        servlet.doGet(request, response);

        verify(request).setAttribute("order", order);
        verify(requestDispatcher).forward(request, response);

    }

    @Test
    public void testOrderOverviewPageServletDoGetError() throws IOException, ServletException {
        when(request.getPathInfo()).thenReturn("/secureId2");
        when(orderDao.getOrderBySecureId("secureId2")).thenThrow(new ItemNotFoundException());

        servlet.doGet(request, response);

        verify(request).setAttribute("message", "Order not found");
        verify(response).sendError(404);

    }
}