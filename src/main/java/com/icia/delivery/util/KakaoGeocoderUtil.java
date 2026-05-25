// src/main/java/com/icia/delivery/util/KakaoGeocoderUtil.java
package com.icia.delivery.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * Kakao Geocoding API 유틸리티 클래스.
 * 주소를 좌표(Point)로 변환하는 메서드와,
 * 최적의 경로 순서를 계산하는 RouteOptimizer 내부 클래스를 포함합니다.
 */
public class KakaoGeocoderUtil {

    private static final Logger log = LoggerFactory.getLogger(KakaoGeocoderUtil.class);

    // Kakao Geocoding API의 기본 URL
    private static final String GEOCODE_URL = "https://dapi.kakao.com/v2/local/search/address.json?query=";
    // Kakao REST API 키는 환경 변수에서 읽습니다.
    private static final String API_KEY = System.getenv().getOrDefault("KAKAO_API_KEY", "");

    /**
     * 단일 주소를 지오코딩하여 좌표(Point)를 반환합니다.
     *
     * @param address 변환할 주소 문자열.
     * @return 주소가 변환된 좌표(Point), 실패하면 null을 반환합니다.
     */
    public static KakaoApiUtil.Point geocodeAddress(String address) {
        log.debug("Geocoding address. address={}", address);
        try {
            // 주소를 UTF-8로 인코딩합니다.
            String encodedAddress = URLEncoder.encode(address, "UTF-8");
            log.trace("Encoded address={}", encodedAddress);

            // API 요청 URL 구성
            String url = GEOCODE_URL + encodedAddress;
            log.trace("Kakao geocode request url={}", url);

            // HttpClient를 사용하여 API 요청 생성 및 전송
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .header("Authorization", "KakaoAK " + API_KEY)
                    .uri(URI.create(url))
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            log.trace("Kakao geocode response={}", response.body());

            // 응답 JSON을 Map으로 변환
            ObjectMapper mapper = new ObjectMapper();
            java.util.Map<String, Object> map = mapper.readValue(response.body(), java.util.Map.class);

            // 응답에서 "documents" 항목을 추출 (주소 검색 결과 배열)
            List<java.util.Map<String, Object>> documents = (List<java.util.Map<String, Object>>) map.get("documents");
            if (documents != null && !documents.isEmpty()) {
                // 첫 번째 결과를 사용
                java.util.Map<String, Object> doc = documents.get(0);
                String xStr = (String) doc.get("x");
                String yStr = (String) doc.get("y");
                log.trace("Kakao geocode document x={}, y={}", xStr, yStr);

                // 좌표 값을 double로 변환하여 Point 객체 생성
                double x = Double.parseDouble(xStr);
                double y = Double.parseDouble(yStr);
                KakaoApiUtil.Point point = new KakaoApiUtil.Point(x, y);
                log.debug("Geocoding succeeded. address={}, point={}", address, point);
                return point;
            } else {
                log.debug("No geocode documents found. address={}", address);
            }
        } catch (IOException | InterruptedException e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            log.warn("Failed to geocode address. address={}", address, e);
        }
        return null;
    }

    /**
     * 여러 주소를 지오코딩하여 좌표 목록을 반환합니다.
     *
     * @param addresses 주소 문자열 리스트.
     * @return 각 주소에 대응되는 좌표(Point) 리스트.
     */
    public static List<KakaoApiUtil.Point> geocodeAddresses(List<String> addresses) {
        List<KakaoApiUtil.Point> result = new ArrayList<>();
        log.debug("Geocoding multiple addresses. count={}", addresses.size());
        for (String addr : addresses) {
            KakaoApiUtil.Point point = geocodeAddress(addr);
            if (point != null) {
                result.add(point);
            } else {
                log.debug("Geocoding returned no point. address={}", addr);
            }
        }
        log.debug("Finished geocoding multiple addresses. successCount={}", result.size());
        return result;
    }

    /**
     * RouteOptimizer 내부 클래스.
     * Nearest Neighbor 방식으로 시작점과 여러 목적지 좌표의 최적 방문 순서를 계산합니다.
     */
    public static class RouteOptimizer {
        /**
         * 시작점과 목적지 리스트를 받아서 최적 방문 순서를 산출합니다.
         *
         * @param start 시작점 좌표 (예: 매장 좌표).
         * @param destinations 목적지 좌표들의 리스트.
         * @return 최적 방문 순서로 정렬된 좌표 리스트.
         */
        public static List<KakaoApiUtil.Point> optimizeRouteOrder(KakaoApiUtil.Point start, List<KakaoApiUtil.Point> destinations) {
            log.debug("Optimizing route order. destinationCount={}", destinations.size());
            List<KakaoApiUtil.Point> remaining = new ArrayList<>(destinations);
            List<KakaoApiUtil.Point> ordered = new ArrayList<>();
            KakaoApiUtil.Point current = start;
            while (!remaining.isEmpty()) {
                int nearestIndex = 0;
                double nearestDist = Double.MAX_VALUE;
                for (int i = 0; i < remaining.size(); i++) {
                    KakaoApiUtil.Point p = remaining.get(i);
                    double dist = Math.hypot(current.getX() - p.getX(), current.getY() - p.getY());
                    if (dist < nearestDist) {
                        nearestDist = dist;
                        nearestIndex = i;
                    }
                }
                KakaoApiUtil.Point nextPoint = remaining.get(nearestIndex);
                log.trace("Selected next route point. point={}, distance={}", nextPoint, nearestDist);
                ordered.add(nextPoint);
                current = nextPoint;
                remaining.remove(nearestIndex);
            }
            log.debug("Optimized route order. orderedCount={}", ordered.size());
            return ordered;
        }
    }
}
