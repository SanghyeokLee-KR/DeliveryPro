package com.icia.delivery.domain.routing;

import java.util.List;

/**
 * 경로 계획 결과.
 *
 * @param order        방문 순서. 값은 입력 배달지 목록의 인덱스다
 * @param legs         구간별 상세. 크기는 order 와 같고 순서도 같다
 * @param totalSeconds 총 통행 시간(초)
 * @param totalMeters  총 이동 거리(미터)
 * @param exact        방문 순서가 정확해면 true
 */
public record RoutePlan(int[] order,
                        List<Leg> legs,
                        double totalSeconds,
                        double totalMeters,
                        boolean exact) {

    /**
     * 한 정차지에서 다음 정차지까지의 구간.
     *
     * @param destinationIndex 도착 배달지의 입력 인덱스
     * @param seconds          구간 통행 시간(초)
     * @param meters           구간 거리(미터)
     * @param polyline         도로망을 따라간 좌표열. 양 끝에 정차지에서 도로까지의 진입 구간이 붙는다.
     *                         도달 불가면 빈 목록
     * @param reachable        도로망으로 도달 가능한지
     */
    public record Leg(int destinationIndex,
                      double seconds,
                      double meters,
                      List<GeoPoint> polyline,
                      boolean reachable) {
    }

    public static RoutePlan empty() {
        return new RoutePlan(new int[0], List.of(), 0.0, 0.0, true);
    }
}
