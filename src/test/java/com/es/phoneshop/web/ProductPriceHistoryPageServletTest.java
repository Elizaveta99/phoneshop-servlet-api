package com.es.phoneshop.web;

import com.es.phoneshop.dao.ProductDao;
import com.es.phoneshop.exception.ProductNotFoundException;
import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.CartService;
import com.es.phoneshop.model.product.Product;
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
import javax.servlet.http.HttpSession;
import java.io.IOException;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProductPriceHistoryPageServletTest {
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private RequestDispatcher requestDispatcher;
    @Mock
    private ProductDao productDao;
    @Mock
    private Product product1;
    @Mock
    private Cart cart;
    @Mock
    private CartService cartService;
    @Mock
    private HttpSession session;

    @InjectMocks
    private final ProductPriceHistoryPageServlet servlet = new ProductPriceHistoryPageServlet();

    @Before
    public void setup() {
        when(productDao.getProduct(1L)).thenReturn(product1);
        when(request.getRequestDispatcher(ProductPriceHistoryPageServlet.PRODUCT_PRICE_HISTORY_JSP)).thenReturn(requestDispatcher);
        when(request.getPathInfo()).thenReturn("/1");
        when(request.getSession()).thenReturn(session);
        when(cartService.getCart(session)).thenReturn(cart);

    }

    @Test
    public void testProductPriceHistoryDoGet() throws ServletException, IOException {
        servlet.doGet(request, response);

        verify(request).setAttribute("product", productDao.getProduct(1L));
        verify(request).setAttribute("cart", cart);
        verify(requestDispatcher).forward(request, response);

    }

    @Test
    public void testProductPriceHistoryErrorProductNotFoundIdDoGet() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn("/100");
        when(productDao.getProduct(100L)).thenThrow(ProductNotFoundException.class);

        servlet.doGet(request, response);

        verify(response).sendError(404);

    }

    @Test
    public void testProductPriceHistoryErrorProductNotFoundWrongIdDoGet() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn("/pp");

        servlet.doGet(request, response);

        verify(response).sendError(404);

    }

}