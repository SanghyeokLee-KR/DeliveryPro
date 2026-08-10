package com.icia.delivery.util;

public class DistanceUtil {
    /**
     * 두 좌표 간 거리를 하버사인 공식으로 계산 (단위: 미터)
     *
     * @param lat1 첫 번째 좌표 위도
     * @param lon1 첫 번째 좌표 경도
     * @param lat2 두 번째 좌표 위도
     * @param lon2 두 번째 좌표 경도
     * @return 두 좌표 간의 거리 (미터)
     */
    public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        // 같은 공식을 두 곳에 두면 상수나 부호를 한쪽만 고치는 사고가 난다. 구현은 GeoDistanceUtil 로 모았다.
        return GeoDistanceUtil.haversineMeters(lat1, lon1, lat2, lon2);
    }
}
