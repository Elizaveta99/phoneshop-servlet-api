package com.es.phoneshop.web;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.CartService;
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
public class MiniCartServletTest {
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
    private CartService cartService;

    @InjectMocks
    private MiniCartServlet servlet = new MiniCartServlet();

    @Before
    public void setup() {
        when(request.getSession()).thenReturn(session);
        when(cartService.getCart(session)).thenReturn(cart);
        when(request.getRequestDispatcher(MiniCartServlet.MINICART_JSP)).thenReturn(requestDispatcher);

    }

    @Test
    public void testMiniCartServletDoGet() throws IOException, ServletException {
        servlet.doGet(request, response);

        verify(request).setAttribute("cart", cart);
        verify(requestDispatcher).include(request, response);

    }

    @Test
    public void testMiniCartServletDoPost() throws IOException, ServletException {
        servlet.doPost(request, response);

        verify(request).setAttribute("cart", cart);
        verify(requestDispatcher).include(request, response);

    }
}