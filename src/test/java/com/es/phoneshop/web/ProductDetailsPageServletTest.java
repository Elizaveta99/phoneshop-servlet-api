package com.es.phoneshop.web;

import com.es.phoneshop.dao.ProductDao;
import com.es.phoneshop.exception.OutOfStockException;
import com.es.phoneshop.exception.ItemNotFoundException;
import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.CartService;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.servlethelper.DefaultServletHelperService;
import com.es.phoneshop.model.viewhistory.ViewHistory;
import com.es.phoneshop.model.viewhistory.ViewHistoryService;
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

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ProductDetailsPageServletTest {
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private RequestDispatcher requestDispatcher;
    @Mock
    private HttpSession session;
    @Mock
    private ProductDao productDao;
    @Mock
    private Product product1;
    @Mock
    private Cart cart;
    @Mock
    private CartService cartService;
    @Mock
    private ViewHistory viewHistory;
    @Mock
    private ViewHistoryService viewHistoryService;
    @Mock
    private DefaultServletHelperService servletHelperService;

    @InjectMocks
    @Spy
    private ProductDetailsPageServlet servlet;

    @Before
    public void setup() {
        when(productDao.getItem(1L)).thenReturn(product1);
        when(request.getSession()).thenReturn(session);
        when(cartService.getCart(session)).thenReturn(cart);
        when(viewHistoryService.getViewHistory(session)).thenReturn(viewHistory);
        when(request.getParameter("quantity")).thenReturn("1");
        when(request.getRequestDispatcher(ProductDetailsPageServlet.PRODUCT_DETAILS_JSP)).thenReturn(requestDispatcher);
        when(request.getContextPath()).thenReturn("/phoneshop-servlet-api");

    }

    @Test
    public void testProductDetailsDoGet() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn("/1");

        servlet.doGet(request, response);

        verify(request).setAttribute("product", productDao.getItem(1L));
        verify(request).setAttribute("viewHistory", viewHistory);
        verify(requestDispatcher).forward(request, response);

    }

    @Test
    public void testProductDetailsDoPostOk() throws ServletException, IOException, OutOfStockException {
        when(request.getPathInfo()).thenReturn("/1");
        when(product1.getId()).thenReturn(1L);

        servlet.doPost(request, response);

        verify(response).sendRedirect(request.getContextPath() + "/products?message=Product " + 1 +" added to cart");

    }

    @Test
    public void testProductDetailsDoPostOutOfStock() throws ServletException, IOException, OutOfStockException, ParseException {
        when(request.getPathInfo()).thenReturn("/1");
        when(product1.getId()).thenReturn(1L);
        doReturn(1).when(servletHelperService).getQuantity("1", request);
        doThrow(new OutOfStockException(product1, 1, 10)).when(cartService).add(cart, 1L, 1);

        servlet.doPost(request, response);

        verify(request).setAttribute("error", "Out of stock, max available " + 10);
        verify(servlet).doGet(request, response);

    }

    @Test
    public void testProductDetailsDoPostOutOfStockNegative() throws ServletException, IOException, OutOfStockException, ParseException {
        when(request.getPathInfo()).thenReturn("/1");
        when(product1.getId()).thenReturn(1L);
        doReturn(1).when(servletHelperService).getQuantity("1", request);
        doThrow(new OutOfStockException(product1, -1, 10)).when(cartService).add(cart, 1L, 1);

        servlet.doPost(request, response);

        verify(request).setAttribute("error", "Can't be negative or zero");
        verify(servlet).doGet(request, response);

    }

    @Test
    public void testProductDetailsDoPostParseException() throws ServletException, IOException, ParseException {
        when(request.getPathInfo()).thenReturn("/1");
        when(request.getParameter("quantity")).thenReturn("a");
        doThrow(ParseException.class).when(servletHelperService).getQuantity("a", request);

        servlet.doPost(request, response);

        verify(request).setAttribute("error", "Not a number");
        verify(servlet).doGet(request, response);

    }


    @Test
    public void testErrorProductNotFoundIdDoGet() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn("/100");
        when(productDao.getItem(100L)).thenThrow(ItemNotFoundException.class);

        servlet.doGet(request, response);

        verify(response).sendError(404);

    }

    @Test
    public void testErrorProductNotFoundWrongIdDoGet() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn("/pp");

        servlet.doGet(request, response);

        verify(response).sendError(404);

    }
}