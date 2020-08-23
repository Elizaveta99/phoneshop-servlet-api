package com.es.phoneshop.security;

import javax.servlet.http.HttpSession;

public interface DosProtectionService {
    boolean isAllowed(HttpSession session, String ip);
}
