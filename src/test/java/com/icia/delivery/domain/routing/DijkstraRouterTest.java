package com.icia.delivery.domain.routing;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("다익스트라 최단경로 탐색")
class DijkstraRouterTest {

    /**
     * 손으로 그린 검증용 그래프.
     *
     * <pre>
     *   0 --(100초, 900m)-----------> 3        직행이지만 느린 길
     *   0 --(10)--> 1 --(10)--> 2 --(10)--> 3  세 홉을 거치는 빠른 길, 합 1200m
     *   4 는 들어오는 간선이 없어 어디서도 닿지 않는다
     *   0 --(5)--> 5 는 있고 5 -> 0 은 없다     일방통행
     * </pre>
     */
    private static RoadGraph sampleGraph() {
        RoadGraph.Builder builder = RoadGraph.builder();
        for (int i = 0; i < 6; i++) {
            builder.addNode(126.60 + i * 0.01, 37.40 + i * 0.01);
        }
        builder.addEdge(0, 3, 900.0, 100.0);
        builder.addEdge(0, 1, 400.0, 10.0);
        builder.addEdge(1, 2, 400.0, 10.0);
        builder.addEdge(2, 3, 400.0, 10.0);
        builder.addEdge(4, 0, 300.0, 8.0);
        builder.addEdge(0, 5, 300.0, 5.0);
        return builder.build();
    }

    @Test
    @DisplayName("여러 홉을 거치는 경로가 직행보다 짧으면 그쪽을 고른다")
    void multiHopBeatsDirectEdge() {
        RoadGraph graph = sampleGraph();
        DijkstraRouter router = new DijkstraRouter(graph);

        DijkstraRouter.Result result = router.shortestPaths(0);

        // 직행 간선은 100초, 0-1-2-3 은 30초다.
        assertEquals(30.0, result.secondsTo(3), 1e-9);
        assertEquals(List.of(0, 1, 2, 3), router.reconstructNodePath(result, 0, 3));
        // 거리는 최단 시간 경로를 따라 잰다. 직행 900m 보다 길어도 시간이 짧으면 그쪽이 답이다.
        assertEquals(1200.0, result.metersTo(3), 1e-9);
        assertEquals(4, router.reconstructPolyline(result, 0, 3).size());
        assertTrue(graph.edgeCount() < graph.nodeCount() * (graph.nodeCount() - 1),
                "완전 그래프가 아니어야 탐색이 의미를 가진다");
    }

    @Test
    @DisplayName("들어오는 간선이 없는 노드는 도달 불가로 남는다")
    void unreachableNodeStaysUnreachable() {
        RoadGraph graph = sampleGraph();
        DijkstraRouter router = new DijkstraRouter(graph);

        DijkstraRouter.Result result = router.shortestPaths(0);

        assertFalse(result.isReachable(4));
        assertEquals(Double.POSITIVE_INFINITY, result.secondsTo(4));
        assertEquals(Double.POSITIVE_INFINITY, result.metersTo(4));
        assertTrue(router.reconstructNodePath(result, 0, 4).isEmpty());
        assertTrue(router.reconstructPolyline(result, 0, 4).isEmpty());
    }

    @Test
    @DisplayName("일방통행이면 반대 방향 비용이 다르다")
    void onewayMakesCostAsymmetric() {
        RoadGraph graph = sampleGraph();
        DijkstraRouter router = new DijkstraRouter(graph);

        assertEquals(5.0, router.shortestPaths(0).secondsTo(5), 1e-9);
        assertFalse(router.shortestPaths(5).isReachable(0));
    }

    @Test
    @DisplayName("목적지가 모두 확정되면 탐색을 끊어도 값이 같다")
    void earlyTerminationKeepsSameCost() {
        RoadGraph graph = sampleGraph();
        DijkstraRouter router = new DijkstraRouter(graph);

        DijkstraRouter.Result full = router.shortestPaths(0);
        DijkstraRouter.Result partial = router.oneToMany(0, new int[]{2});

        assertEquals(full.secondsTo(2), partial.secondsTo(2), 1e-9);
        assertTrue(partial.isReachable(2));
        // 2 가 확정된 시점에 멈추므로 그보다 먼 3 은 확정되지 않는다.
        assertFalse(partial.isReachable(3));
    }

    @Test
    @DisplayName("출발지 자신의 비용은 0이다")
    void sourceCostIsZero() {
        DijkstraRouter router = new DijkstraRouter(sampleGraph());
        DijkstraRouter.Result result = router.shortestPaths(2);

        assertEquals(0.0, result.secondsTo(2), 1e-9);
        assertEquals(List.of(2), router.reconstructNodePath(result, 2, 2));
    }

    @Test
    @DisplayName("우회로가 더 싸면 갱신된 값으로 덮는다")
    void relaxationOverwritesWorseCandidate() {
        RoadGraph.Builder builder = RoadGraph.builder();
        for (int i = 0; i < 4; i++) {
            builder.addNode(126.60 + i * 0.01, 37.40);
        }
        // 0 -> 2 는 50 이지만 0 -> 1 -> 2 는 12 다. 큐에 두 후보가 모두 들어간다.
        builder.addEdge(0, 2, 100.0, 50.0);
        builder.addEdge(0, 1, 100.0, 6.0);
        builder.addEdge(1, 2, 100.0, 6.0);
        builder.addEdge(2, 3, 100.0, 1.0);
        RoadGraph graph = builder.build();

        DijkstraRouter router = new DijkstraRouter(graph);
        DijkstraRouter.Result result = router.shortestPaths(0);

        assertEquals(12.0, result.secondsTo(2), 1e-9);
        assertEquals(13.0, result.secondsTo(3), 1e-9);
        assertEquals(List.of(0, 1, 2, 3), router.reconstructNodePath(result, 0, 3));
    }
}
