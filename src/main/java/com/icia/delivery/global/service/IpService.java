package com.icia.delivery.global.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

@Service
public class IpService {

    private static final String UNKNOWN = "unknown";

    public String getClientIp(HttpServletRequest request) {
        if (request == null) {
            return "";
        }

        String clientIp = getFirstValidIp(request.getHeader("X-Forwarded-For"));
        if (!isInvalidIp(clientIp)) {
            return clientIp;
        }

        clientIp = getFirstValidIp(request.getHeader("X-Real-IP"));
        if (!isInvalidIp(clientIp)) {
            return clientIp;
        }

        clientIp = getFirstValidIp(request.getHeader("Proxy-Client-IP"));
        if (!isInvalidIp(clientIp)) {
            return clientIp;
        }

        clientIp = getFirstValidIp(request.getHeader("WL-Proxy-Client-IP"));
        if (!isInvalidIp(clientIp)) {
            return clientIp;
        }

        clientIp = request.getRemoteAddr();
        return isInvalidIp(clientIp) ? "" : clientIp.trim();
    }

    private String getFirstValidIp(String headerValue) {
        if (isInvalidIp(headerValue)) {
            return null;
        }

        String firstIp = headerValue.split(",")[0].trim();
        return isInvalidIp(firstIp) ? null : firstIp;
    }

    private boolean isInvalidIp(String ip) {
        return ip == null || ip.trim().isEmpty() || UNKNOWN.equalsIgnoreCase(ip.trim());
    }
}
