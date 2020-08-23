package com.es.phoneshop.security;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultDosProtectionService implements DosProtectionService {

    private static final long THRESHOLD = 20;
    private Map<String, Long> countMap = new ConcurrentHashMap();

    private static class SingletonHelper {
        private static final DefaultDosProtectionService INSTANCE = new DefaultDosProtectionService();
    }

    public static DefaultDosProtectionService getInstance() {
        return DefaultDosProtectionService.SingletonHelper.INSTANCE;
    }

    @Override
    public boolean isAllowed(HttpSession session, String ip) {
        Long count = countMap.get(ip);
        if (count == null) {
            setFirstTime(ip, session);
        } else {
            long lastTime = session.getAttribute("lastTime") == null ? 0 : (long) session.getAttribute("lastTime");
            long timeElapsed = System.currentTimeMillis() - lastTime;
            if (timeElapsed < 60000) {
                if (count > THRESHOLD) {
                    return false;
                }
                count++;
            } else {
                setFirstTime(ip, session);
                count = 1L;
            }
            countMap.put(ip, count);
        }
        return true;
    }

    private void setFirstTime(String ip, HttpSession session) {
        countMap.put(ip, 1L);
        session.setAttribute("lastTime", System.currentTimeMillis());
    }
}
