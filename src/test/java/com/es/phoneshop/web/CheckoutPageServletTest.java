package com.es.phoneshop.web;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.CartService;
import com.es.phoneshop.model.order.Order;
import com.es.phoneshop.model.order.OrderService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CheckoutPageServletTest {
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private RequestDispatcher requestDispatcher;
    @Mock
    private HttpSession session;
    @Mock
    private Cart cart;
    @Mock
    private Order order;
    @Mock
    private CartService cartService;
    @Mock
    private OrderService orderService;

    @InjectMocks
    @Spy
    private CheckoutPageServlet servlet;

    @Before
    public void setup() {
        when(request.getSession()).thenReturn(session);
        when(cartService.getCart(session)).thenReturn(cart);
        when(orderService.getOrder(cart)).thenReturn(order);
        when(order.getSecureId()).thenReturn("secureId1");
        when(request.getRequestDispatcher(CheckoutPageServlet.CHECKOUT_JSP)).thenReturn(requestDispatcher);
        when(request.getContextPath()).thenReturn("/phoneshop-servlet-api");
        when(request.getParameter("lastName")).thenReturn("lastName");
        when(request.getParameter("deliveryAddress")).thenReturn("deliveryAddress");
        when(request.getParameter("paymentMethod")).thenReturn("CACHE");
        doNothing().when(orderService).placeOrder(order);
        doNothing().when(cartService).clearCart(session);

    }

    @Test
    public void testCheckoutPageServletDoGet() throws ServletException, IOException {
        servlet.doGet(request, response);

        verify(request).setAttribute("order", order);
        verify(request).setAttribute(eq("paymentMethods"), anyCollection());
        verify(requestDispatcher).forward(request, response);

    }

    @Test
    public void testCartPageDoPostOk() throws ServletException, IOException {
        when(request.getParameter("firstName")).thenReturn("firstName");
        when(request.getParameter("phone")).thenReturn("+375291112233");
        when(request.getParameter("deliveryDate")).thenReturn("22-08-2020");

        servlet.doPost(request, response);

        verify(response).sendRedirect(request.getContextPath() + "/order/overview/secureId1");

    }

    @Test
    public void testCartPageDoPostBlankParameter() throws ServletException, IOException {
        when(request.getParameter("firstName")).thenReturn("");
        when(request.getParameter("phone")).thenReturn("+375291112233");
        when(request.getParameter("deliveryDate")).thenReturn("22-08-2020");

        servlet.doPost(request, response);

        verify(request).setAttribute(eq("errors"), anyMap());

    }

    @Test
    public void testCartPageDoPostWrongPhone() throws ServletException, IOException {
        when(request.getParameter("firstName")).thenReturn("firstName");
        when(request.getParameter("phone")).thenReturn("+375291");
        when(request.getParameter("deliveryDate")).thenReturn("22-08-2020");

        servlet.doPost(request, response);

        verify(request).setAttribute(eq("errors"), anyMap());

    }

    @Test
    public void testCartPageDoPostWrongDate() throws ServletException, IOException {
        when(request.getParameter("firstName")).thenReturn("firstName");
        when(request.getParameter("phone")).thenReturn("+375291112233");
        when(request.getParameter("deliveryDate")).thenReturn("22082020");

        servlet.doPost(request, response);

        verify(request).setAttribute(eq("errors"), anyMap());

    }
}