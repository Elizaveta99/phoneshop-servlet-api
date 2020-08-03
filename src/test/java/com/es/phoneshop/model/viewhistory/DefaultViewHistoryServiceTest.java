package com.es.phoneshop.model.viewhistory;

import com.es.phoneshop.model.product.Product;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.http.HttpSession;

import java.util.ArrayDeque;
import java.util.Deque;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DefaultViewHistoryServiceTest {
    @Mock
    private HttpSession session;
    @Mock
    private ViewHistory viewHistory;
    @Mock
    private Product product1;

    @InjectMocks
    private final DefaultViewHistoryService defaultViewHistoryService = DefaultViewHistoryService.getInstance();

    @Before
    public void setup() {
        when(session.getAttribute(DefaultViewHistoryService.VIEWHISTORY_SESSION_ATTRIBUTE)).thenReturn(viewHistory);

    }

    @Test
    public void testGetViewHistory() {
        assertEquals(viewHistory, defaultViewHistoryService.getViewHistory(session));

    }

    @Test
    public void testGetNewViewHistory() {
        when(session.getAttribute(DefaultViewHistoryService.VIEWHISTORY_SESSION_ATTRIBUTE)).thenReturn(null);

        defaultViewHistoryService.getViewHistory(session);

        verify(session).setAttribute(eq(DefaultViewHistoryService.VIEWHISTORY_SESSION_ATTRIBUTE), any(ViewHistory.class));

    }

    @Test
    public void testAddProductToViewHistory() {
        Deque<Product> testProducts = new ArrayDeque<>();
        testProducts.addFirst(product1);
        testProducts.addFirst(product1);
        testProducts.addFirst(product1);
        testProducts.addFirst(product1);
        when(viewHistory.getLastViewedProducts()).thenReturn(testProducts);

        defaultViewHistoryService.addProductToViewHistory(session, product1);

        assertTrue(testProducts.contains(product1));

    }

}
