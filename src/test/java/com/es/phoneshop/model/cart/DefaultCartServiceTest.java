package com.es.phoneshop.model.cart;

import com.es.phoneshop.dao.ProductDao;
import com.es.phoneshop.exception.OutOfStockException;
import com.es.phoneshop.model.product.Product;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.*;

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
        when(cartItem1.getQuantity()).thenReturn(1);
        when(product1.getPrice()).thenReturn(new BigDecimal(110));

        defaultCartService.add(cart, 1L, 1);

        verify(cartItem1).setQuantity(2);

    }

    @Test
    public void testAddNewOk() throws OutOfStockException {
        when(product1.getStock()).thenReturn(5);
        List<CartItem> testItems = new ArrayList<>();
        testItems.add(cartItem1);
        when(cart.getItems()).thenReturn(testItems);
        when(cartItem1.getProduct()).thenReturn(product2);
        when(cartItem1.getQuantity()).thenReturn(1);
        when(product2.getId()).thenReturn(2L);
        when(product2.getPrice()).thenReturn(new BigDecimal(100));
        when(product1.getPrice()).thenReturn(new BigDecimal(110));

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

    @Test
    public void testUpdateOk() throws OutOfStockException {
        when(product1.getStock()).thenReturn(5);
        when(cart.getItems()).thenReturn(Collections.singletonList(cartItem1));
        when(cartItem1.getProduct()).thenReturn(product1);
        when(cartItem1.getQuantity()).thenReturn(1);
        when(product1.getPrice()).thenReturn(new BigDecimal(110));

        defaultCartService.update(cart, 1L, 2);

        verify(cartItem1).setQuantity(2);

    }

    @Test(expected = OutOfStockException.class)
    public void testUpdateOutOfStock() throws OutOfStockException {
        when(product1.getStock()).thenReturn(0);

        defaultCartService.update(cart, 1L, 2);

    }

    @Test
    public void testDelete() {
        List<CartItem> testItems = new ArrayList<>();
        testItems.add(cartItem1);
        when(cart.getItems()).thenReturn(testItems);
        when(cartItem1.getProduct()).thenReturn(product1);
        when(product1.getId()).thenReturn(1L);
        defaultCartService.delete(cart, 1L);

        assertFalse(cart.getItems().contains(cartItem1));

    }

}
