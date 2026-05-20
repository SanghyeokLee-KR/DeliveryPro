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
        final int R = 6371000; // 지구 반지름 (미터)
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}
