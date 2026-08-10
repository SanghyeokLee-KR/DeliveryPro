// src/main/java/com/icia/delivery/util/KakaoGeocoderUtil.java
package com.icia.delivery.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.icia.delivery.domain.routing.GeoPoint;
import com.icia.delivery.domain.routing.RoutePlanner;
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
 * 주소를 좌표로 바꾸는 카카오 지오코딩 호출과, 방문 순서를 정하는 RouteOptimizer 를 담는다.
 *
 * <p>방문 순서 계산은 도로망 그래프 위의 다익스트라로 옮겼다. 자세한 내용은 RouteOptimizer 주석에 있다.
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
     * 방문 순서 산출.
     *
     * <p>예전에는 위경도 도 차이를 Math.hypot 으로 재서 가장 가까운 곳을 차례로 고르는
     * 최근접 이웃이었다. 두 가지가 문제였다. 도 단위 유클리드는 위도 보정이 없어 동서
     * 거리를 약 26% 부풀렸고, 직선 거리라 일방통행과 통과 불가 축을 무시했다.
     *
     * <p>지금은 도로망 그래프에서 다익스트라로 정차지 사이 통행 시간을 재고, 그 행렬 위에서
     * 방문 순서를 푼다. 호출부를 그대로 두려고 클래스와 메서드 이름은 유지한다.
     */
    public static class RouteOptimizer {

        /**
         * 시작점과 목적지 리스트를 받아 방문 순서대로 정렬한 좌표를 돌려준다.
         *
         * @param start        시작점 좌표(가게 또는 라이더 위치)
         * @param destinations 목적지 좌표 목록
         * @return 방문 순서로 재배열한 좌표 목록
         */
        public static List<KakaoApiUtil.Point> optimizeRouteOrder(KakaoApiUtil.Point start,
                                                                  List<KakaoApiUtil.Point> destinations) {
            int[] order = optimizeRouteOrderIndices(start, destinations);
            List<KakaoApiUtil.Point> ordered = new ArrayList<>(order.length);
            for (int index : order) {
                ordered.add(destinations.get(index));
            }
            return ordered;
        }

        /**
         * 방문 순서를 입력 목록의 인덱스로 돌려준다.
         *
         * <p>좌표 대신 인덱스를 쓰면 결과를 원래 항목에 다시 붙일 때 좌표를 비교할 필요가
         * 없다. 좌표 비교는 같은 건물의 다른 호수처럼 좌표가 겹치는 배달지를 구분하지 못한다.
         *
         * @param destinations 목적지 좌표 목록. 좌표를 못 구한 자리는 null 을 넣는다
         * @return 방문 순서. 값은 destinations 의 인덱스
         */
        public static int[] optimizeRouteOrderIndices(KakaoApiUtil.Point start,
                                                      List<KakaoApiUtil.Point> destinations) {
            if (destinations == null || destinations.isEmpty()) {
                return new int[0];
            }
            log.debug("Optimizing route order. destinationCount={}", destinations.size());
            List<GeoPoint> points = new ArrayList<>(destinations.size());
            for (KakaoApiUtil.Point point : destinations) {
                points.add(toGeoPoint(point));
            }
            int[] order = RoutePlanner.demo().orderDestinations(toGeoPoint(start), points);
            log.debug("Optimized route order. orderedCount={}", order.length);
            return order;
        }

        private static GeoPoint toGeoPoint(KakaoApiUtil.Point point) {
            if (point == null || point.getX() == null || point.getY() == null) {
                return null;
            }
            return new GeoPoint(point.getX(), point.getY());
        }
    }
}
