package com.es.phoneshop.web;

import com.es.phoneshop.exception.OutOfStockException;
import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.CartService;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.servlethelper.DefaultServletHelperService;
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
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CartPageServletTest {
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private RequestDispatcher requestDispatcher;
    @Mock
    private HttpSession session;
    @Mock
    private Product product1;
    @Mock
    private Cart cart;
    @Mock
    private CartService cartService;
    @Mock
    private DefaultServletHelperService servletHelperService;

    @InjectMocks
    @Spy
    private CartPageServlet servlet;

    @Before
    public void setup() {
        when(request.getSession()).thenReturn(session);
        when(cartService.getCart(session)).thenReturn(cart);
        when(request.getRequestDispatcher(CartPageServlet.CART_JSP)).thenReturn(requestDispatcher);
        when(request.getContextPath()).thenReturn("/phoneshop-servlet-api");

    }

    @Test
    public void testCartPageDoGet() throws ServletException, IOException {
        servlet.doGet(request, response);

        verify(request).setAttribute("cart", cart);
        verify(requestDispatcher).forward(request, response);

    }

    @Test
    public void testCartPageDoPostOk() throws ServletException, IOException, OutOfStockException, ParseException {
        String[] productIds = {"1", "2"};
        String[] quantities = {"1", "1"};

        when(request.getParameterValues("productId")).thenReturn(productIds);
        when(request.getParameterValues("quantity")).thenReturn(quantities);
        doReturn(1L).when(servletHelperService).getProductIdIfExist(request, response, "1");
        doReturn(2L).when(servletHelperService).getProductIdIfExist(request, response, "2");

        servlet.doPost(request, response);

        verify(response).sendRedirect(request.getContextPath() + "/cart?message=Cart updated successfully");

    }

    @Test
    public void testCartPageDoPostError() throws ServletException, IOException, OutOfStockException, ParseException {
        String[] productIds = {"1", "2"};
        String[] quantities = {"1", "1"};
        Map<Long, String> errorAttributes = new HashMap<>();
        errorAttributes.put(1L, "");

        when(request.getParameterValues("productId")).thenReturn(productIds);
        when(request.getParameterValues("quantity")).thenReturn(quantities);
        doReturn(1L).when(servletHelperService).getProductIdIfExist(request, response, "1");
        doReturn(2L).when(servletHelperService).getProductIdIfExist(request, response, "2");
        doReturn(1).when(servletHelperService).getQuantity("1", request);
        doThrow(new OutOfStockException(product1, 1, 10)).when(cartService).update(cart, 1L, 1);
        doNothing().when(cartService).update(cart, 2L, 1);
        doReturn(errorAttributes).when(servletHelperService).mapErrors(eq(1L), any(OutOfStockException.class));
        servlet.doPost(request, response);

        verify(request).setAttribute("cart", cart);
        verify(requestDispatcher).forward(request, response);

    }
}