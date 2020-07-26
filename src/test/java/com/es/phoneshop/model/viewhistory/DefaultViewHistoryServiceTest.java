package com.es.phoneshop.model.viewhistory;

import com.es.phoneshop.model.product.Product;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.http.HttpSession;

import java.util.ArrayDeque;
import java.util.Deque;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DefaultViewHistoryServiceTest {
    @Mock
    private HttpSession session;
    @Mock
    private ViewHistory viewHistory;
    @Mock
    private Product product1;

    @InjectMocks
    private final DefaultViewHistoryService defaultViewHistoryService = Mockito.spy(DefaultViewHistoryService.getInstance());

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
        doReturn(viewHistory).when(defaultViewHistoryService).makeNewViewHistory();

        assertEquals(viewHistory, defaultViewHistoryService.getViewHistory(session));

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
