package com.es.phoneshop.web.filter;

import com.es.phoneshop.security.DosProtectionService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DosFilterTest {
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private FilterChain chain;
    @Mock
    private DosProtectionService dosProtectionService;

    @InjectMocks
    private DosFilter filter = new DosFilter();

    @Before
    public void setup() {
        when(request.getRemoteAddr()).thenReturn("ip");

    }

    @Test
    public void testDoFilterOk() throws IOException, ServletException {
        when(dosProtectionService.isAllowed("ip")).thenReturn(true);

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);

    }

    @Test
    public void testDoFilterError() throws IOException, ServletException {
        when(dosProtectionService.isAllowed("ip")).thenReturn(false);

        filter.doFilter(request, response, chain);

        verify(response).setStatus(429);

    }

}