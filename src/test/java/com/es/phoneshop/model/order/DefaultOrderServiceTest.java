package com.es.phoneshop.model.order;

import com.es.phoneshop.dao.OrderDao;
import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.CartItem;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DefaultOrderServiceTest {
    @Mock
    private OrderDao orderDao;
    @Mock
    private Cart cart;
    @Mock
    private Order order1;
    @Mock
    private CartItem cartItem1;

    private List<CartItem> testItems = new ArrayList<>();

    @InjectMocks
    private DefaultOrderService defaultOrderService = DefaultOrderService.getInstance();

    @Before
    public void setup() {
        testItems.add(cartItem1);
        when(cart.getItems()).thenReturn(testItems);
    }

    @Test
    public void testGetOrder() throws CloneNotSupportedException {
        when(cartItem1.clone()).thenReturn(cartItem1);
        when(cart.getTotalCost()).thenReturn(new BigDecimal(100));

        assertEquals(testItems, defaultOrderService.getOrder(cart).getItems());
        assertEquals(new BigDecimal(110), defaultOrderService.getOrder(cart).getTotalCost());

    }

    @Test(expected = RuntimeException.class)
    public void testGetOrderException() throws CloneNotSupportedException {
        when(cartItem1.clone()).thenThrow(new CloneNotSupportedException());

        defaultOrderService.getOrder(cart);

    }

    @Test
    public void testGetPaymentMethods() {
        List<PaymentMethod> testItems = Arrays.asList(PaymentMethod.CACHE, PaymentMethod.CREDIT_CARD);

        assertEquals(defaultOrderService.getPaymentMethods(), testItems);

    }

    @Test
    public void testPlaceOrder() {
        defaultOrderService.placeOrder(order1);

        verify(order1).setSecureId(anyString());
        verify(orderDao).save(order1);
    }


}
