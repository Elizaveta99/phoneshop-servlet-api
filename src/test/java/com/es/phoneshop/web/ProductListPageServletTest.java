package com.es.phoneshop.web;

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
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

    @InjectMocks
    private final ProductListPageServlet servlet = new ProductListPageServlet();

    @Before
    public void setup() {
        List<Product> testProducts = new ArrayList<>();
        testProducts.add(product1);
        testProducts.add(product2);
        when(request.getParameter("queryProduct")).thenReturn("");
        when(request.getParameter("sort")).thenReturn("description");
        when(request.getParameter("order")).thenReturn("asc");
        when(productDao.findProducts("", SortField.DESCRIPTION, SortOrder.ASC)).thenReturn(testProducts);
        when(request.getRequestDispatcher("/WEB-INF/pages/productList.jsp")).thenReturn(requestDispatcher);
        when(request.getSession()).thenReturn(session);
        when(cartService.getCart(session)).thenReturn(cart);
        when(viewHistoryService.getViewHistory(session)).thenReturn(viewHistory);

    }

    @Test
    public void testDoGet() throws ServletException, IOException {
        servlet.doGet(request, response);

        verify(request).setAttribute("products", productDao.findProducts("", SortField.DESCRIPTION, SortOrder.ASC));
        verify(request).setAttribute("cart", cart);
        verify(request).setAttribute("viewHistory", viewHistory);
        verify(requestDispatcher).forward(request, response);

    }

}