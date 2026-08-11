package com.icia.delivery.domain.routing;

import com.icia.delivery.util.GeoDistanceUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("데모 도로망과 경로 계획")
class DemoRoadNetworkTest {

    private static final RoadGraph GRAPH = RoadNetworkProvider.graph();
    private static final NodeIndex INDEX = RoadNetworkProvider.index();

    @Test
    @DisplayName("간선은 모두 짧은 도로 구간이라 완전 그래프가 아니다")
    void everyEdgeIsAShortRoadSegment() {
        double longest = 0.0;
        for (int node = 0; node < GRAPH.nodeCount(); node++) {
            for (int edge = GRAPH.edgeBegin(node); edge < GRAPH.edgeEnd(node); edge++) {
                longest = Math.max(longest, GRAPH.edgeMeters(edge));
            }
        }
        // 한 블록 길이를 넘는 간선이 없다는 뜻이다. 멀리 떨어진 두 지점을 곧장 잇는 간선은 없다.
        assertTrue(longest < 700.0, "가장 긴 간선=" + longest);

        long possible = (long) GRAPH.nodeCount() * (GRAPH.nodeCount() - 1);
        assertTrue(GRAPH.edgeCount() < possible / 50, "간선 밀도가 완전 그래프와 거리가 멀어야 한다");
    }

    @Test
    @DisplayName("어느 노드에서 출발해도 모든 노드에 닿는다")
    void graphIsStronglyConnected() {
        assertTrue(GRAPH.nodeCount() > 300, "노드 수=" + GRAPH.nodeCount());

        DijkstraRouter router = new DijkstraRouter(GRAPH);
        for (int source : new int[]{0, GRAPH.nodeCount() / 3, GRAPH.nodeCount() - 1}) {
            DijkstraRouter.Result result = router.shortestPaths(source);
            int reached = 0;
            for (int node = 0; node < GRAPH.nodeCount(); node++) {
                if (result.isReachable(node)) {
                    reached++;
                }
            }
            assertEquals(GRAPH.nodeCount(), reached, "최대 강한 연결 요소만 남겼으므로 전부 닿아야 한다");
        }
    }

    @Test
    @DisplayName("간선 가중치가 균일하지 않다")
    void edgeWeightsAreNotUniform() {
        double[] seconds = new double[GRAPH.edgeCount()];
        for (int node = 0; node < GRAPH.nodeCount(); node++) {
            for (int edge = GRAPH.edgeBegin(node); edge < GRAPH.edgeEnd(node); edge++) {
                seconds[edge] = GRAPH.edgeSeconds(edge);
            }
        }
        Arrays.sort(seconds);
        // 균일 가중치였다면 너비 우선 탐색으로 충분하다는 반박이 성립한다.
        assertTrue(seconds[seconds.length - 1] / seconds[0] > 2.0,
                "최대/최소 가중치 비=" + seconds[seconds.length - 1] / seconds[0]);
    }

    @Test
    @DisplayName("일방통행과 통과 불가 축 때문에 왕복 비용이 다른 구간이 있다")
    void someRoutesAreAsymmetric() {
        DijkstraRouter router = new DijkstraRouter(GRAPH);
        int asymmetric = 0;
        for (int source = 0; source < GRAPH.nodeCount(); source += 37) {
            DijkstraRouter.Result forward = router.shortestPaths(source);
            for (int target = 0; target < GRAPH.nodeCount(); target += 53) {
                if (target == source) {
                    continue;
                }
                DijkstraRouter.Result backward = router.shortestPaths(target);
                if (Math.abs(forward.secondsTo(target) - backward.secondsTo(source)) > 1.0) {
                    asymmetric++;
                }
            }
        }
        assertTrue(asymmetric > 0, "비대칭 구간이 하나도 없으면 방향 정보가 무의미하다");
    }

    @Test
    @DisplayName("도로를 따라간 경로는 직선 거리보다 길고 중간 교차점을 거친다")
    void routeFollowsRoadsRatherThanStraightLine() {
        GeoPoint from = new GeoPoint(126.6500, 37.4350);
        GeoPoint to = new GeoPoint(126.7020, 37.4700);

        int fromNode = INDEX.nearest(from.lon(), from.lat(), RoadNetworkProvider.SNAP_LIMIT_METERS);
        int toNode = INDEX.nearest(to.lon(), to.lat(), RoadNetworkProvider.SNAP_LIMIT_METERS);
        assertTrue(fromNode >= 0 && toNode >= 0, "서비스 구역 안 좌표는 도로에 붙어야 한다");

        DijkstraRouter router = new DijkstraRouter(GRAPH);
        DijkstraRouter.Result result = router.oneToMany(fromNode, new int[]{toNode});
        List<Integer> path = router.reconstructNodePath(result, fromNode, toNode);

        assertTrue(path.size() > 10, "교차점을 여러 번 거쳐야 한다. 홉 수=" + path.size());
        double straight = GeoDistanceUtil.haversineMetersByLonLat(
                GRAPH.lon(fromNode), GRAPH.lat(fromNode), GRAPH.lon(toNode), GRAPH.lat(toNode));
        assertTrue(result.metersTo(toNode) > straight, "도로를 따라가면 직선보다 길어진다");
    }

    @Test
    @DisplayName("서비스 구역 밖 좌표는 도로에 붙이지 않는다")
    void farAwayPointIsNotSnapped() {
        assertEquals(-1, INDEX.nearest(129.0756, 35.1796, RoadNetworkProvider.SNAP_LIMIT_METERS));
    }

    @Test
    @DisplayName("묶음배달 경로 계획이 순서와 구간을 함께 만든다")
    void plannerProducesOrderAndLegs() {
        RoutePlanner planner = RoutePlanner.demo();
        GeoPoint store = new GeoPoint(126.6600, 37.4450);
        List<GeoPoint> destinations = List.of(
                new GeoPoint(126.7000, 37.4720),
                new GeoPoint(126.6520, 37.4340),
                new GeoPoint(126.6650, 37.4500),
                new GeoPoint(126.6560, 37.4650));

        RoutePlan plan = planner.plan(store, destinations);

        assertTrue(plan.exact(), "정차지 4곳은 정확해 범위다");
        assertEquals(destinations.size(), plan.order().length);
        assertEquals(destinations.size(), plan.legs().size());
        int[] sorted = Arrays.copyOf(plan.order(), plan.order().length);
        Arrays.sort(sorted);
        assertArrayEqualsRange(sorted);

        double legSum = 0.0;
        for (RoutePlan.Leg leg : plan.legs()) {
            assertTrue(leg.reachable());
            assertTrue(leg.seconds() > 0.0);
            assertTrue(leg.polyline().size() >= 2, "구간 경로는 최소 두 점이다");
            legSum += leg.seconds();
        }
        assertEquals(legSum, plan.totalSeconds(), 1e-6);

        // 가장 가까운 곳부터 집는 순서와 반드시 같지는 않다. 같기만 하다면 최적화가 의미 없다.
        assertTrue(plan.totalSeconds() > 0.0);
        assertNotEquals(0, plan.totalMeters());
    }

    @Test
    @DisplayName("같은 교차점에 붙는 두 배달지도 이동 비용과 경로가 남는다")
    void stopsOnTheSameNodeStillCost() {
        int node = INDEX.nearest(126.6600, 37.4450, RoadNetworkProvider.SNAP_LIMIT_METERS);
        GeoPoint center = GRAPH.point(node);
        // 위도 0.00036도는 약 40m 다. 격자 간격보다 훨씬 짧아 두 점 모두 같은 교차점에 붙는다.
        GeoPoint north = new GeoPoint(center.lon(), center.lat() + 0.00036);
        GeoPoint south = new GeoPoint(center.lon(), center.lat() - 0.00036);
        assertEquals(node, INDEX.nearest(north.lon(), north.lat(), RoadNetworkProvider.SNAP_LIMIT_METERS));
        assertEquals(node, INDEX.nearest(south.lon(), south.lat(), RoadNetworkProvider.SNAP_LIMIT_METERS));

        RoutePlan plan = RoutePlanner.demo().plan(center, List.of(north, south));

        assertEquals(2, plan.legs().size());
        for (RoutePlan.Leg leg : plan.legs()) {
            assertTrue(leg.reachable());
            assertTrue(leg.seconds() > 0.0, "스냅 거리를 버리면 이동 시간이 0으로 보고된다");
            assertTrue(leg.meters() > 0.0);
            assertTrue(leg.polyline().size() >= 2, "노드가 하나뿐이어도 정차지를 이어 선이 남아야 한다");
        }
        assertTrue(plan.totalSeconds() > 0.0);
    }

    @Test
    @DisplayName("좌표를 못 구한 배달지가 섞여도 순서를 만든다")
    void nullDestinationDoesNotBreakPlanning() {
        RoutePlanner planner = RoutePlanner.demo();
        List<GeoPoint> destinations = Arrays.asList(
                new GeoPoint(126.7000, 37.4720),
                null,
                new GeoPoint(126.6560, 37.4650));

        RoutePlan plan = planner.plan(new GeoPoint(126.6600, 37.4450), destinations);

        assertEquals(3, plan.order().length);
        assertEquals(1, plan.order()[2], "좌표가 없는 배달지는 마지막으로 밀린다");
    }

    private static void assertArrayEqualsRange(int[] sorted) {
        for (int i = 0; i < sorted.length; i++) {
            assertEquals(i, sorted[i], "방문 순서는 배달지 전체의 순열이어야 한다");
        }
    }
}
