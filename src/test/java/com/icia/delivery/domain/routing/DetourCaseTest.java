package com.icia.delivery.domain.routing;

import com.icia.delivery.util.GeoDistanceUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 문서가 인용하는 우회 사례를 고정한다.
 *
 * <p>포트폴리오와 README 가 "직선 208m 가 도로로는 1,876m" 라고 적는 근거가 여기다.
 * 좌표를 문서에만 두면 도로망을 손볼 때 문서가 조용히 틀린 값이 된다. 여기서 깨지게 둔다.
 */
class DetourCaseTest {

    private static final RoadGraph GRAPH = RoadNetworkProvider.graph();

    /** 통과 불가 축 반대편에 있어 직선으로는 가깝고 도로로는 멀리 도는 두 지점. */
    private static final GeoPoint A = new GeoPoint(126.692950, 37.453087);
    private static final GeoPoint B = new GeoPoint(126.690714, 37.452494);

    @Test
    @DisplayName("직선으로 가까운 두 지점이 도로로는 아홉 배 돌아간다")
    void detourRatioIsAboutNine() {
        int a = nodeOf(A);
        int b = nodeOf(B);
        DijkstraRouter router = new DijkstraRouter(GRAPH);

        double straight = GeoDistanceUtil.haversineMetersByLonLat(
                GRAPH.lon(a), GRAPH.lat(a), GRAPH.lon(b), GRAPH.lat(b));
        double road = router.oneToMany(a, new int[]{b}).metersTo(b);

        assertEquals(208.0, straight, 1.0, "직선 거리");
        assertEquals(1876.0, road, 5.0, "도로 거리");
        assertTrue(road / straight > 8.5, "우회비 = " + (road / straight));
    }

    @Test
    @DisplayName("같은 두 지점인데 가는 비용과 오는 비용이 다르다")
    void oneWayStreetsMakeTheReturnTripCheaper() {
        int a = nodeOf(A);
        int b = nodeOf(B);
        DijkstraRouter router = new DijkstraRouter(GRAPH);

        double ab = router.oneToMany(a, new int[]{b}).secondsTo(b);
        double ba = router.oneToMany(b, new int[]{a}).secondsTo(a);

        assertEquals(247.7, ab, 1.0, "A 에서 B");
        assertEquals(97.6, ba, 1.0, "B 에서 A");
        assertTrue(ab > ba * 2, "일방통행 때문에 왕복 비용이 두 배 넘게 벌어진다");
    }

    private static int nodeOf(GeoPoint p) {
        int node = RoadNetworkProvider.index()
                .nearest(p.lon(), p.lat(), RoadNetworkProvider.SNAP_LIMIT_METERS);
        assertTrue(node >= 0, "서비스 구역 안 좌표여야 한다");
        return node;
    }
}
