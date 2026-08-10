package com.icia.delivery.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("하버사인 거리 계산")
class GeoDistanceUtilTest {

    private static final double R = GeoDistanceUtil.EARTH_RADIUS_METERS;

    /**
     * 하버사인과 다른 식으로 같은 값을 구한다.
     * 구면 코사인 법칙은 유도 경로가 달라, 두 값이 맞으면 구현 실수를 걸러낼 수 있다.
     */
    private static double sphericalLawOfCosines(double lat1, double lon1, double lat2, double lon2) {
        double p1 = Math.toRadians(lat1);
        double p2 = Math.toRadians(lat2);
        double dl = Math.toRadians(lon2 - lon1);
        double cos = Math.sin(p1) * Math.sin(p2) + Math.cos(p1) * Math.cos(p2) * Math.cos(dl);
        return R * Math.acos(Math.min(1.0, Math.max(-1.0, cos)));
    }

    @Test
    @DisplayName("위도 1도는 자오선 호길이와 같다")
    void oneDegreeOfLatitudeMatchesMeridianArc() {
        double expected = R * Math.PI / 180.0;
        assertEquals(111_194.9266, expected, 1e-3, "기준값 자체 확인");

        assertEquals(expected, GeoDistanceUtil.haversineMeters(0.0, 126.65, 1.0, 126.65), 1e-6);
        assertEquals(expected, GeoDistanceUtil.haversineMeters(37.0, 126.65, 38.0, 126.65), 1e-6);
        assertEquals(expected, GeoDistanceUtil.haversineMeters(60.0, 0.0, 61.0, 0.0), 1e-6);
    }

    @Test
    @DisplayName("경도 1도는 위도가 올라갈수록 짧아진다")
    void oneDegreeOfLongitudeShrinksWithLatitude() {
        double atEquator = GeoDistanceUtil.haversineMeters(0.0, 0.0, 0.0, 1.0);
        double atSixty = GeoDistanceUtil.haversineMeters(60.0, 0.0, 60.0, 1.0);

        assertEquals(R * Math.PI / 180.0, atEquator, 1e-6);
        // 위도 60도에서 경도 1도는 적도의 약 절반이다. 대권 경로라 위도선 호보다 아주 조금 짧다.
        assertEquals(55_596.9, atSixty, 0.5);
        assertTrue(atSixty < atEquator * 0.5, "대권 거리는 위도선 호길이보다 짧아야 한다");
    }

    @Test
    @DisplayName("서비스 구역 위도에서 경도 차이는 cos(위도)만큼 줄어든다")
    void longitudeIsScaledByCosineOfLatitude() {
        double lat = 37.44;
        double eastWest = GeoDistanceUtil.haversineMeters(lat, 126.65, lat, 126.66);
        double northSouth = GeoDistanceUtil.haversineMeters(lat, 126.65, lat + 0.01, 126.65);

        assertEquals(Math.cos(Math.toRadians(lat)), eastWest / northSouth, 1e-4);

        // 도 단위 유클리드는 이 보정이 없어 동서 거리를 약 26% 부풀린다.
        double euclideanRatio = Math.hypot(0.01, 0.0) / Math.hypot(0.0, 0.01);
        assertEquals(1.0, euclideanRatio, 1e-12);
        assertTrue(northSouth / eastWest > 1.25, "실제로는 위도 방향이 더 길다");
    }

    @Test
    @DisplayName("독립 구현인 구면 코사인 법칙과 값이 일치한다")
    void matchesSphericalLawOfCosines() {
        double[][] pairs = {
                {37.4388938204128, 126.675113024566, 37.4563, 126.7052},
                {37.4563, 126.7052, 37.5665, 126.9780},
                {37.415, 126.620, 37.480, 126.700},
                {33.4996, 126.5312, 37.5665, 126.9780},
        };
        for (double[] pair : pairs) {
            double haversine = GeoDistanceUtil.haversineMeters(pair[0], pair[1], pair[2], pair[3]);
            double reference = sphericalLawOfCosines(pair[0], pair[1], pair[2], pair[3]);
            assertEquals(reference, haversine, 0.01);
            assertTrue(haversine > 0.0);
        }
    }

    @Test
    @DisplayName("같은 점은 0이고 방향을 바꿔도 값이 같다")
    void zeroAndSymmetry() {
        assertEquals(0.0, GeoDistanceUtil.haversineMeters(37.44, 126.65, 37.44, 126.65), 1e-9);

        double forward = GeoDistanceUtil.haversineMeters(37.44, 126.65, 37.46, 126.69);
        double backward = GeoDistanceUtil.haversineMeters(37.46, 126.69, 37.44, 126.65);
        assertEquals(forward, backward, 1e-9);
    }

    @Test
    @DisplayName("경도·위도 순서 오버로드는 인자만 바꾼 같은 계산이다")
    void lonLatOverloadMatches() {
        assertEquals(
                GeoDistanceUtil.haversineMeters(37.44, 126.65, 37.46, 126.69),
                GeoDistanceUtil.haversineMetersByLonLat(126.65, 37.44, 126.69, 37.46),
                1e-9);
    }

    @Test
    @DisplayName("통행 시간은 거리를 속도로 나눈 값이다")
    void travelSecondsUsesSpeed() {
        assertEquals(72.0, GeoDistanceUtil.travelSeconds(1000.0, 50.0), 1e-9);
        assertEquals(144.0, GeoDistanceUtil.travelSeconds(1000.0, 25.0), 1e-9);
    }
}
