package com.es.phoneshop.security;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DefaultDosProtectionServiceTest {
    @Mock
    private Map<String, DefaultDosProtectionService.UserState> countMap;

    @Mock
    private DefaultDosProtectionService.UserState userState;

    @Spy
    @InjectMocks
    private DefaultDosProtectionService defaultDosProtectionService;

    @Before
    public void setup() {
        when(countMap.get("ip")).thenReturn(userState);

    }

    @Test
    public void testIsAllowedFirstTime() {
        when(countMap.getOrDefault("ip", null)).thenReturn(null);

        assertTrue(defaultDosProtectionService.isAllowed("ip"));

    }

    @Test
    public void testIsAllowedLessThanThreshold() {
        when(countMap.getOrDefault("ip", null)).thenReturn(new DefaultDosProtectionService.UserState(1, LocalDateTime.now()));
        doReturn(true).when(defaultDosProtectionService).isBefore("ip");

        assertTrue(defaultDosProtectionService.isAllowed("ip"));

    }

    @Test
    public void testIsAllowedMoreThanMinute() {
        when(countMap.getOrDefault("ip", null)).thenReturn(new DefaultDosProtectionService.UserState(1, LocalDateTime.now().minusMinutes(2)));
        doReturn(false).when(defaultDosProtectionService).isBefore("ip");

        assertTrue(defaultDosProtectionService.isAllowed("ip"));

    }

    @Test
    public void testIsNotAllowed() {
        when(countMap.getOrDefault("ip", null)).thenReturn(new DefaultDosProtectionService.UserState(21, LocalDateTime.now()));
        doReturn(true).when(defaultDosProtectionService).isBefore("ip");

        assertFalse(defaultDosProtectionService.isAllowed("ip"));

    }
}
