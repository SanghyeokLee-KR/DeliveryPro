package com.icia.delivery.util;

import jakarta.servlet.http.HttpServletRequest;

public class UserAgentUtil {

    /**
     * User-Agent 헤더에서 OS 정보를 파싱
     *
     * @param request HttpServletRequest
     * @return OS 이름
     */
    public static String getDeviceOs(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        if (userAgent == null) return "Unknown OS";

        if (userAgent.contains("Windows")) return "Windows";
        if (userAgent.contains("Macintosh") || userAgent.contains("Mac OS")) return "MacOS";
        if (userAgent.contains("X11")) return "Unix";
        if (userAgent.contains("Android")) return "Android";
        if (userAgent.contains("iPhone") || userAgent.contains("iPad")) return "iOS";

        return "Unknown OS";
    }

    /**
     * User-Agent 헤더에서 브라우저 정보를 파싱
     *
     * @param request HttpServletRequest
     * @return 브라우저 이름
     */
    public static String getBrowser(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        if (userAgent == null) return "Unknown Browser";

        if (userAgent.contains("Chrome") && !userAgent.contains("Chromium")) return "Chrome";
        if (userAgent.contains("Firefox")) return "Firefox";
        if (userAgent.contains("Safari") && !userAgent.contains("Chrome")) return "Safari";
        if (userAgent.contains("Edge") || userAgent.contains("Edg")) return "Edge";
        if (userAgent.contains("Opera") || userAgent.contains("OPR")) return "Opera";

        return "Unknown Browser";
    }
}
