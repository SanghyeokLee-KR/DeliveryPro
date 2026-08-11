package com.icia.delivery.domain.routing;

/**
 * 카카오 지오코딩을 쓸 수 없을 때 데모 흐름이 끊기지 않게 하는 대체 좌표 변환.
 *
 * <p>실제 좌표가 아니다. 주소 문자열을 해시해 서비스 구역 안의 한 점으로 사상한다.
 * 값을 무작위로 뽑지 않고 문자열에서 유도하므로 같은 주소는 언제나 같은 점이 되고,
 * 화면과 서버가 같은 좌표를 본다. 카카오 키가 있으면 이 클래스는 호출되지 않는다.
 *
 * <p>구역 경계에서 조금 안쪽으로 밀어 넣는다. 경계 밖으로 나가면 도로망에 붙지 못해
 * 경로 계산이 도달 불가로 떨어진다.
 */
public final class DemoAddressGeocoder {

    /** 경계에서 띄우는 여백 비율. 격자 한 칸보다 넉넉해야 스냅이 실패하지 않는다. */
    private static final double INSET_RATIO = 0.06;

    private static final long FNV_OFFSET_BASIS = 0xcbf29ce484222325L;
    private static final long FNV_PRIME = 0x100000001b3L;

    private DemoAddressGeocoder() {
    }

    /**
     * @param address 주소 문자열
     * @return 서비스 구역 안의 좌표. 주소가 비어 있으면 null
     */
    public static GeoPoint locate(String address) {
        if (address == null || address.isBlank()) {
            return null;
        }
        String normalized = address.trim();
        double lonFraction = fraction(hash(normalized, 0x9e3779b9L));
        double latFraction = fraction(hash(normalized, 0x85ebca6bL));

        double lonInset = (DemoRoadNetwork.MAX_LON - DemoRoadNetwork.MIN_LON) * INSET_RATIO;
        double latInset = (DemoRoadNetwork.MAX_LAT - DemoRoadNetwork.MIN_LAT) * INSET_RATIO;
        double lon = DemoRoadNetwork.MIN_LON + lonInset
                + (DemoRoadNetwork.MAX_LON - DemoRoadNetwork.MIN_LON - 2 * lonInset) * lonFraction;
        double lat = DemoRoadNetwork.MIN_LAT + latInset
                + (DemoRoadNetwork.MAX_LAT - DemoRoadNetwork.MIN_LAT - 2 * latInset) * latFraction;
        return new GeoPoint(lon, lat);
    }

    /** 경도와 위도에 서로 다른 씨앗을 줘야 두 값이 같이 움직이지 않는다. */
    private static long hash(String value, long seed) {
        long hash = FNV_OFFSET_BASIS ^ seed;
        for (int i = 0; i < value.length(); i++) {
            hash ^= value.charAt(i);
            hash *= FNV_PRIME;
        }
        return hash;
    }

    private static double fraction(long hash) {
        return ((hash >>> 11) & ((1L << 53) - 1)) / (double) (1L << 53);
    }
}
