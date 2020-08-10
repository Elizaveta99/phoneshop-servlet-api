package com.es.phoneshop.web;

import com.es.phoneshop.exception.OutOfStockException;
import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.CartService;
import com.es.phoneshop.model.sortenum.SortField;
import com.es.phoneshop.model.sortenum.SortOrder;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.dao.ProductDao;
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
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ProductListPageServletTest {
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
    private Product product2;
    @Mock
    private Cart cart;
    @Mock
    private CartService cartService;
    @Mock
    private ViewHistory viewHistory;
    @Mock
    private ViewHistoryService viewHistoryService;

    private final Locale locale = Locale.ENGLISH;
    private final NumberFormat numberFormat = NumberFormat.getInstance(locale);

    @InjectMocks
    @Spy
    private ProductListPageServlet servlet;

    @Before
    public void setup() {
        List<Product> testProducts = new ArrayList<>();
        testProducts.add(product1);
        testProducts.add(product2);
        when(request.getParameter("queryProduct")).thenReturn("");
        when(request.getParameter("sort")).thenReturn("description");
        when(request.getParameter("order")).thenReturn("asc");
        when(productDao.findProducts("", SortField.DESCRIPTION, SortOrder.ASC)).thenReturn(testProducts);
        when(request.getRequestDispatcher(ProductListPageServlet.PRODUCTS_LIST_JSP)).thenReturn(requestDispatcher);
        when(request.getSession()).thenReturn(session);
        when(cartService.getCart(session)).thenReturn(cart);
        when(viewHistoryService.getViewHistory(session)).thenReturn(viewHistory);
        when(request.getContextPath()).thenReturn("/phoneshop-servlet-api");

        when(request.getLocale()).thenReturn(locale);
        doReturn(numberFormat).when(servlet).getNumberFormat(locale);

    }

    @Test
    public void testDoGet() throws ServletException, IOException {
        servlet.doGet(request, response);

        verify(request).setAttribute("products", productDao.findProducts("", SortField.DESCRIPTION, SortOrder.ASC));
        verify(request).setAttribute("viewHistory", viewHistory);
        verify(requestDispatcher).forward(request, response);

    }

    @Test
    public void testProductListPageServletDoPostOk() throws ServletException, IOException, OutOfStockException {
        when(request.getParameter("productId")).thenReturn("1");
        when(request.getParameter("quantity")).thenReturn("1");
        doNothing().when(cartService).add(cart, 1L, 1);

        servlet.doPost(request, response);

        verify(response).sendRedirect(request.getContextPath() + "/products?message=Product " + 1 +" added to cart");

    }

    @Test
    public void testProductListPageServletDoPostOutOfStock() throws ServletException, IOException, OutOfStockException {
        when(request.getParameter("productId")).thenReturn("1");
        when(request.getParameter("quantity")).thenReturn("1");
        doThrow(new OutOfStockException(product1, 1, 10)).when(cartService).add(cart, 1L, 1);

        servlet.doPost(request, response);

        verify(request).setAttribute("viewHistory", viewHistory);
        try {
            cartService.add(cart, 1L, 1);
        } catch (OutOfStockException e) {
            verify(request).setAttribute(eq("errors"), any(HashMap.class));
            verify(servlet).doGet(request, response);
        }

    }

    @Test
    public void testProductListPageServletDoPostOutOfStockNegative() throws ServletException, IOException, OutOfStockException {
        when(request.getParameter("productId")).thenReturn("1");
        when(request.getParameter("quantity")).thenReturn("1");
        doThrow(new OutOfStockException(product1, -1, 10)).when(cartService).add(cart, 1L, 1);

        servlet.doPost(request, response);

        verify(request).setAttribute("viewHistory", viewHistory);
        try {
            cartService.add(cart, 1L, 1);
        } catch (OutOfStockException e) {
            verify(request).setAttribute(eq("errors"), any(HashMap.class));
            verify(servlet).doGet(request, response);
        }

    }

    @Test
    public void testProductListPageServletDoPostParseException() throws ServletException, IOException {
        when(request.getParameter("productId")).thenReturn("1");
        when(request.getParameter("quantity")).thenReturn("pp");

        servlet.doPost(request, response);

        verify(request).setAttribute(eq("errors"), any(HashMap.class));
        verify(servlet).doGet(request, response);
    }

}