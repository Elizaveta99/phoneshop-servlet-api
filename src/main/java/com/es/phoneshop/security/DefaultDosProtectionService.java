package com.es.phoneshop.security;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultDosProtectionService implements DosProtectionService {

    private static final long THRESHOLD = 20;
    private Map<String, UserState> countMap = new ConcurrentHashMap();

    private static class SingletonHelper {
        private static final DefaultDosProtectionService INSTANCE = new DefaultDosProtectionService();
    }

    public static DefaultDosProtectionService getInstance() {
        return DefaultDosProtectionService.SingletonHelper.INSTANCE;
    }

    protected static class UserState {
        private long count;
        private LocalDateTime lastTime;

        public UserState(long count, LocalDateTime lastTime) {
            this.count = count;
            this.lastTime = lastTime;
        }
    }

    @Override
    public boolean isAllowed(String ip) {
        UserState userState = countMap.getOrDefault(ip, null);
        if (userState == null) {
            return setFirst(ip);
        } else {
            long count = userState.count;
            if (isBefore(ip)) {
                if (count > THRESHOLD) {
                    return false;
                }
                countMap.get(ip).count++;
                return true;
            } else {
                return setFirst(ip);
            }
        }
    }

    protected boolean isBefore (String ip) {
        return LocalDateTime.now().minusMinutes(1).isBefore(countMap.get(ip).lastTime);
    }

    private boolean setFirst(String ip) {
        countMap.put(ip, new UserState(1, LocalDateTime.now()));
        return  true;
    }
}
