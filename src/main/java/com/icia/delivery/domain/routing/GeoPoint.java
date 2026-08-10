package com.icia.delivery.domain.routing;

/**
 * 경로 계산에서 쓰는 좌표.
 * 카카오 응답과 순서를 맞추려고 경도를 앞에 둔다.
 */
public record GeoPoint(double lon, double lat) {

    public static GeoPoint of(double lon, double lat) {
        return new GeoPoint(lon, lat);
    }
}
