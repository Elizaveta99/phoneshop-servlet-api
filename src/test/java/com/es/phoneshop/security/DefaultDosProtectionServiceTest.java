package com.es.phoneshop.security;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.http.HttpSession;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DefaultDosProtectionServiceTest {
    @Mock
    private HttpSession session;
    @Mock
    private Map<String, Long> countMap;

    @InjectMocks
    private DefaultDosProtectionService defaultDosProtectionService = DefaultDosProtectionService.getInstance();

    @Test
    public void testIsAllowedFirstTime() {
        when(countMap.get("ip")).thenReturn(null);

        assertTrue(defaultDosProtectionService.isAllowed(session, "ip"));

    }

    @Test
    public void testIsAllowedLessThanThreshold() {
        when(countMap.get("ip")).thenReturn(10L);
        when(session.getAttribute("lastTime")).thenReturn(System.currentTimeMillis());

        assertTrue(defaultDosProtectionService.isAllowed(session, "ip"));

    }

    @Test
    public void testIsAllowedMoreThanMinute() {
        when(countMap.get("ip")).thenReturn(10L);
        when(session.getAttribute("lastTime")).thenReturn(null);

        assertTrue(defaultDosProtectionService.isAllowed(session, "ip"));

    }

    @Test
    public void testIsNotAllowed() {
        when(countMap.get("ip")).thenReturn(21L);
        when(session.getAttribute("lastTime")).thenReturn(System.currentTimeMillis());

        assertFalse(defaultDosProtectionService.isAllowed(session, "ip"));

    }
}
