package com.es.phoneshop.model.servlethelper;

import com.es.phoneshop.exception.OutOfStockException;
import com.es.phoneshop.model.product.Product;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class DefaultServletHelperServiceTest {

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private Product product1;

    private final Locale locale = Locale.ENGLISH;
    private final NumberFormat numberFormat = NumberFormat.getInstance(locale);

    @Spy
    @InjectMocks
    private DefaultServletHelperService defaultServletHelperService;

    @Before
    public void setup() {
        when(request.getLocale()).thenReturn(locale);
        doReturn(numberFormat).when(defaultServletHelperService).getNumberFormat(locale);
    }

    @Test
    public void testMapErrorsParseException() {
        Map<Long, String> testErrorAttributes = defaultServletHelperService.mapErrors(1L, new ParseException("", 1));

        assertEquals(testErrorAttributes.get(1L), "Not a number");
    }

    @Test
    public void testMapErrorsNegative() {
        Map<Long, String> testErrorAttributes = defaultServletHelperService.mapErrors(1L, new OutOfStockException(product1, -1, 5));

        assertEquals(testErrorAttributes.get(1L), "Can't be negative or zero");
    }

    @Test
    public void testMapErrorsOutOfStock() {
        Map<Long, String> testErrorAttributes = defaultServletHelperService.mapErrors(1L, new OutOfStockException(product1, 2, 1));

        assertEquals(testErrorAttributes.get(1L), "Out of stock, max available " + 1);
    }

    @Test
    public void testGetProductIdIfExistOk() throws IOException {
        assertEquals(defaultServletHelperService.getProductIdIfExist(request, response, "1" ), Long.valueOf(1));
    }

    @Test
    public void testGetProductIdIfExistNumberFormatException() throws IOException {
        defaultServletHelperService.getProductIdIfExist(request, response, "pp" );

        verify(request).setAttribute("message", "Product pp" + " not found");
        verify(response).sendError(404);
    }

    @Test
    public void testGetQuantityOk() throws ParseException {
        assertEquals(defaultServletHelperService.getQuantity("1", request), 1L);
    }

    @Test(expected = ParseException.class)
    public void testGetQuantityException() throws ParseException {
        defaultServletHelperService.getQuantity("pp", request);
    }
}
