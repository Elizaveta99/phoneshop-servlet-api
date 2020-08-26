package com.es.phoneshop.dao;

import com.es.phoneshop.exception.ItemNotFoundException;
import com.es.phoneshop.model.order.Order;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ArrayListOrderDaoTest {
    @InjectMocks
    private final ArrayListOrderDao orderDao = ArrayListOrderDao.getInstance();

    private List<Order> testOrders;

    @Mock
    private Order order1;

    @Before
    public void setup() {
        testOrders = new ArrayList<>();
        when(order1.getSecureId()).thenReturn("secureId1");
        testOrders.add(order1);

        orderDao.setItemList(testOrders);

    }

    @Test
    public void testGetOrderBySecureId() throws ItemNotFoundException {
        Order actualOrder = orderDao.getOrderBySecureId("secureId1");
        assertEquals(order1, actualOrder);

    }

    @Test(expected = ItemNotFoundException.class)
    public void testGetOrderBySecureIdException() throws ItemNotFoundException {
        orderDao.getOrderBySecureId("secureId2");

    }
}
