package com.es.phoneshop.model.cart;

import com.es.phoneshop.dao.ProductDao;
import com.es.phoneshop.exception.OutOfStockException;
import com.es.phoneshop.model.product.Product;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import javax.servlet.http.HttpSession;
import java.util.*;

@RunWith(MockitoJUnitRunner.class)
public class DefaultCartServiceTest {
    @Mock
    private HttpSession session;
    @Mock
    private Cart cart;
    @Mock
    private ProductDao productDao;
    @Mock
    private Product product1;
    @Mock
    private Product product2;
    @Mock
    private CartItem cartItem1;

    @Spy
    @InjectMocks
    private DefaultCartService defaultCartService;

    @Before
    public void setup() {
        when(productDao.getProduct(1L)).thenReturn(product1);
        when(session.getAttribute(DefaultCartService.CART_SESSION_ATTRIBUTE)).thenReturn(cart);

    }

    @Test
    public void testGetCart() {
        assertEquals(cart, defaultCartService.getCart(session));

    }

    @Test
    public void testGetNewCart() {
        when(session.getAttribute(DefaultCartService.CART_SESSION_ATTRIBUTE)).thenReturn(null);

        defaultCartService.getCart(session);

        verify(session).setAttribute(eq(DefaultCartService.CART_SESSION_ATTRIBUTE), any(Cart.class));

    }

    @Test
    public void testAddOldOk() throws OutOfStockException {
        when(product1.getStock()).thenReturn(5);
        when(cart.getItems()).thenReturn(Collections.singletonList(cartItem1));
        when(cartItem1.getProduct()).thenReturn(product1);

        defaultCartService.add(cart, 1L, 1);

        verify(cartItem1).setQuantity(1);

    }

    @Test
    public void testAddNewOk() throws OutOfStockException {
        when(product1.getStock()).thenReturn(5);
        List<CartItem> testItems = new ArrayList<>();
        testItems.add(cartItem1);
        when(cart.getItems()).thenReturn(testItems);
        when(cartItem1.getProduct()).thenReturn(product2);
        when(product2.getId()).thenReturn(2L);

        int size = testItems.size();
        defaultCartService.add(cart, 1L, 1);

        assertEquals(testItems.size(), size + 1);

    }

    @Test(expected = OutOfStockException.class)
    public void testAddOutOfStockNegative() throws OutOfStockException {
        defaultCartService.add(cart, 1L, -1);

    }

    @Test(expected = OutOfStockException.class)
    public void testAddOutOfStock() throws OutOfStockException {
        when(product1.getStock()).thenReturn(0);

        defaultCartService.add(cart, 1L, 1);

    }

}
