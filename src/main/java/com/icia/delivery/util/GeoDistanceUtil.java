package com.icia.delivery.util;

/**
 * 위경도 좌표 사이의 거리와 통행 시간을 계산한다.
 *
 * <p>도 단위 좌표에 유클리드 거리를 그대로 쓰면 경도 1도의 실제 길이가 위도에 따라
 * 달라지는 점이 무시된다. 위도 37.44 에서 경도 1도는 위도 1도의 약 79% 라서
 * 동서 방향 거리가 그만큼 과대평가된다. 그래서 모든 서버 거리 계산은 하버사인으로 통일한다.
 */
public final class GeoDistanceUtil {

    /** 지구 평균 반지름. 기존 DistanceUtil 과 값을 맞춰 회귀를 만들지 않는다. */
    public static final double EARTH_RADIUS_METERS = 6_371_000.0;

    private GeoDistanceUtil() {
    }

    /**
     * 위도·경도 순서로 받는 하버사인 거리.
     *
     * @return 두 좌표 사이의 대권 거리(미터)
     */
    public static double haversineMeters(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double sinLat = Math.sin(dLat / 2.0);
        double sinLon = Math.sin(dLon / 2.0);
        double a = sinLat * sinLat
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * sinLon * sinLon;
        // 대척점 부근에서 sqrt(1-a) 가 0 에 붙어도 atan2 는 발산하지 않는다.
        double c = 2.0 * Math.atan2(Math.sqrt(a), Math.sqrt(1.0 - a));
        return EARTH_RADIUS_METERS * c;
    }

    /**
     * 경도·위도 순서로 받는 하버사인 거리.
     * 카카오 좌표(x=경도, y=위도)와 인자 순서를 맞추려고 둔 오버로드다.
     */
    public static double haversineMetersByLonLat(double lon1, double lat1, double lon2, double lat2) {
        return haversineMeters(lat1, lon1, lat2, lon2);
    }

    /**
     * 거리와 평균 통행 속도로 소요 시간을 구한다.
     *
     * @param meters   구간 길이(미터)
     * @param speedKmh 평균 통행 속도(km/h). 0 이하이면 예외
     * @return 소요 시간(초)
     */
    public static double travelSeconds(double meters, double speedKmh) {
        if (speedKmh <= 0.0) {
            throw new IllegalArgumentException("통행 속도는 양수여야 한다. speedKmh=" + speedKmh);
        }
        return meters / (speedKmh / 3.6);
    }
}
