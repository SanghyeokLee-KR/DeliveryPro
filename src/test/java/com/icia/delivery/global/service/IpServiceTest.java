package com.icia.delivery.global.service;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.assertj.core.api.Assertions.assertThat;

class IpServiceTest {

    private final IpService ipService = new IpService();

    @Test
    void getClientIpReturnsXForwardedForWhenPresent() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-Forwarded-For", "203.0.113.10");
        request.setRemoteAddr("127.0.0.1");

        String clientIp = ipService.getClientIp(request);

        assertThat(clientIp).isEqualTo("203.0.113.10");
    }

    @Test
    void getClientIpReturnsFirstIpWhenXForwardedForHasMultipleIps() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-Forwarded-For", "203.0.113.10, 198.51.100.20, 192.0.2.30");
        request.setRemoteAddr("127.0.0.1");

        String clientIp = ipService.getClientIp(request);

        assertThat(clientIp).isEqualTo("203.0.113.10");
    }

    @Test
    void getClientIpUsesXRealIpWhenXForwardedForIsUnknown() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-Forwarded-For", "unknown");
        request.addHeader("X-Real-IP", "198.51.100.20");
        request.setRemoteAddr("127.0.0.1");

        String clientIp = ipService.getClientIp(request);

        assertThat(clientIp).isEqualTo("198.51.100.20");
    }

    @Test
    void getClientIpIgnoresBlankHeadersAndReturnsRemoteAddr() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-Forwarded-For", " ");
        request.addHeader("X-Real-IP", " ");
        request.addHeader("Proxy-Client-IP", " ");
        request.addHeader("WL-Proxy-Client-IP", " ");
        request.setRemoteAddr("127.0.0.1");

        String clientIp = ipService.getClientIp(request);

        assertThat(clientIp).isEqualTo("127.0.0.1");
    }

    @Test
    void getClientIpUsesProxyClientIpWhenPriorHeadersAreInvalid() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-Forwarded-For", "unknown");
        request.addHeader("X-Real-IP", " ");
        request.addHeader("Proxy-Client-IP", "192.0.2.30");
        request.setRemoteAddr("127.0.0.1");

        String clientIp = ipService.getClientIp(request);

        assertThat(clientIp).isEqualTo("192.0.2.30");
    }

    @Test
    void getClientIpUsesWlProxyClientIpWhenPriorHeadersAreInvalid() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-Forwarded-For", "unknown");
        request.addHeader("X-Real-IP", " ");
        request.addHeader("Proxy-Client-IP", "unknown");
        request.addHeader("WL-Proxy-Client-IP", "192.0.2.40");
        request.setRemoteAddr("127.0.0.1");

        String clientIp = ipService.getClientIp(request);

        assertThat(clientIp).isEqualTo("192.0.2.40");
    }

    @Test
    void getClientIpReturnsRemoteAddrWhenHeadersAreMissing() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("127.0.0.1");

        String clientIp = ipService.getClientIp(request);

        assertThat(clientIp).isEqualTo("127.0.0.1");
    }

    @Test
    void getClientIpReturnsEmptyStringWhenRemoteAddrIsUnknown() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("unknown");

        String clientIp = ipService.getClientIp(request);

        assertThat(clientIp).isEmpty();
    }

    @Test
    void getClientIpReturnsEmptyStringWhenRemoteAddrIsBlank() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr(" ");

        String clientIp = ipService.getClientIp(request);

        assertThat(clientIp).isEmpty();
    }

    @Test
    void getClientIpReturnsEmptyStringWhenRequestIsNull() {
        String clientIp = ipService.getClientIp(null);

        assertThat(clientIp).isEmpty();
    }
}
