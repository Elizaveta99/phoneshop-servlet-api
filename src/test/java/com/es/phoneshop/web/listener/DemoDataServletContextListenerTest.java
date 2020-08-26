package com.es.phoneshop.web.listener;

import com.es.phoneshop.dao.ArrayListProductDao;
import com.es.phoneshop.model.product.Product;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import java.text.ParseException;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DemoDataServletContextListenerTest {
    @Mock
    private ServletContextEvent event;
    @Mock
    private ArrayListProductDao productDao;
    @Mock
    private ServletContext servletContext;
    @Mock
    private ParseException e;

    @InjectMocks
    private final DemoDataServletContextListener listener = Mockito.spy(new DemoDataServletContextListener());

    @Before
    public void setup() {
        when(event.getServletContext()).thenReturn(servletContext);

    }

    @Test
    public void testContextInitializedTrue() {
        when(event.getServletContext().getInitParameter("insertDemoData")).thenReturn("true");

        listener.contextInitialized(event);

        verify(productDao, times(13)).save(any(Product.class));

    }

    @Test
    public void testContextInitializedFalse() {
        when(event.getServletContext().getInitParameter("insertDemoData")).thenReturn("false");

        listener.contextInitialized(event);

        verify(productDao, never()).save(any(Product.class));

    }

    @Test
    public void testContextInitializedError() throws ParseException {
        when(event.getServletContext().getInitParameter("insertDemoData")).thenReturn("true");
        doThrow(e).when(listener).getDate("23-10-2000");

        listener.contextInitialized(event);

        verify(e).printStackTrace();

    }
}