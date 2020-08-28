package com.es.phoneshop.web;

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
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AdvancedSearchPageServletTest {
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private RequestDispatcher requestDispatcher;

    private final Locale locale = Locale.ENGLISH;
    private final NumberFormat numberFormat = NumberFormat.getInstance(locale);

    @Spy
    @InjectMocks
    private AdvancedSearchPageServlet servlet;

    @Before
    public void setup() {
        when(request.getRequestDispatcher(AdvancedSearchPageServlet.ADVANCED_SEARCH_PAGE)).thenReturn(requestDispatcher);

        when(request.getLocale()).thenReturn(locale);
        doReturn(numberFormat).when(servlet).getNumberFormat(locale);

    }

    @Test
    public void testAdvancedSearchPageServletDoGet() throws ServletException, IOException {
        servlet.doGet(request, response);

        verify(request).setAttribute(eq("products"), any(ArrayList.class));
        verify(requestDispatcher).forward(request, response);

    }

    @Test
    public void testAdvancedSearchPageServletDoPostOk() throws ServletException, IOException {
        when(request.getParameter("minPrice")).thenReturn("100");
        when(request.getParameter("maxPrice")).thenReturn("100");
        when(request.getParameter("minStock")).thenReturn("0");
        when(request.getParameter("productCode")).thenReturn("sgs");

        servlet.doPost(request, response);

        verify(request).setAttribute(eq("products"), anyCollection());
        verify(requestDispatcher).forward(request, response);

    }

    @Test
    public void testAdvancedSearchPageServletDoPostPriceParseException() throws ServletException, IOException {
        when(request.getParameter("minPrice")).thenReturn("pp");
        when(request.getParameter("maxPrice")).thenReturn("100");
        when(request.getParameter("minStock")).thenReturn("0");

        servlet.doPost(request, response);

        request.setAttribute(eq("errors"), anyMap());

    }

    @Test
    public void testAdvancedSearchPageServletDoPostStockParseException() throws ServletException, IOException {
        when(request.getParameter("minPrice")).thenReturn("100");
        when(request.getParameter("maxPrice")).thenReturn("100");
        when(request.getParameter("minStock")).thenReturn("pp");

        servlet.doPost(request, response);

        request.setAttribute(eq("errors"), anyMap());

    }

    @Test
    public void testAdvancedSearchPageServletDoPostPriceNegative() throws ServletException, IOException {
        when(request.getParameter("minPrice")).thenReturn("-1");
        when(request.getParameter("maxPrice")).thenReturn("100");
        when(request.getParameter("minStock")).thenReturn("0");

        servlet.doPost(request, response);

        request.setAttribute(eq("errors"), anyMap());

    }

    @Test
    public void testAdvancedSearchPageServletDoPostStockNegative() throws ServletException, IOException {
        when(request.getParameter("minPrice")).thenReturn("100");
        when(request.getParameter("maxPrice")).thenReturn("100");
        when(request.getParameter("minStock")).thenReturn("-1");

        servlet.doPost(request, response);

        request.setAttribute(eq("errors"), anyMap());

    }
}