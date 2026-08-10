package com.icia.delivery.domain.routing;

import java.util.ArrayList;
import java.util.List;

/**
 * 경로 계획의 조립 지점.
 *
 * <p>순서는 세 단계로 나뉜다.
 * 1. 가게와 배달지를 도로망 교차점에 붙인다.
 * 2. 정차지마다 다익스트라를 한 번씩 돌려 (n+1)x(n+1) 통행 시간 행렬을 만든다.
 * 3. 그 행렬 위에서 방문 순서를 푼다.
 *
 * <p>스프링 컨테이너에 묶지 않는다. 정적 유틸에서 호출하는 경로가 아직 남아 있어서다.
 */
public final class RoutePlanner {

    private final RoadGraph graph;
    private final NodeIndex index;

    public RoutePlanner(RoadGraph graph, NodeIndex index) {
        this.graph = graph;
        this.index = index;
    }

    public static RoutePlanner demo() {
        return new RoutePlanner(RoadNetworkProvider.graph(), RoadNetworkProvider.index());
    }

    /**
     * 방문 순서만 필요할 때 쓴다.
     *
     * @param start        출발지. null 이면 첫 배달지를 출발지로 본다
     * @param destinations 배달지 목록. 좌표를 못 구한 자리는 null 을 넣는다
     * @return 입력 인덱스로 표현한 방문 순서
     */
    public int[] orderDestinations(GeoPoint start, List<GeoPoint> destinations) {
        return plan(start, destinations).order();
    }

    /**
     * 방문 순서와 구간별 경로를 함께 계산한다.
     */
    public RoutePlan plan(GeoPoint start, List<GeoPoint> destinations) {
        if (destinations == null || destinations.isEmpty()) {
            return RoutePlan.empty();
        }

        List<GeoPoint> stops = new ArrayList<>(destinations.size() + 1);
        stops.add(start);
        stops.addAll(destinations);

        TravelCostMatrix matrix = TravelCostMatrix.build(graph, index, stops);
        VisitOrderSolver.Plan visitPlan = VisitOrderSolver.solve(matrix.seconds());

        DijkstraRouter router = new DijkstraRouter(graph);
        List<RoutePlan.Leg> legs = new ArrayList<>(visitPlan.order().length);
        double totalSeconds = 0.0;
        double totalMeters = 0.0;
        int current = 0;
        for (int destination : visitPlan.order()) {
            int next = destination + 1;
            boolean reachable = matrix.reachable()[current][next];
            double seconds = reachable ? matrix.seconds()[current][next] : 0.0;
            double meters = reachable ? matrix.meters()[current][next] : 0.0;
            List<GeoPoint> polyline = List.of();
            if (reachable && matrix.nodes()[current] >= 0 && matrix.nodes()[next] >= 0) {
                DijkstraRouter.Result result = router.oneToMany(
                        matrix.nodes()[current], new int[]{matrix.nodes()[next]});
                polyline = router.reconstructPolyline(result, matrix.nodes()[current], matrix.nodes()[next]);
            }
            legs.add(new RoutePlan.Leg(destination, seconds, meters, polyline, reachable));
            totalSeconds += seconds;
            totalMeters += meters;
            current = next;
        }
        return new RoutePlan(visitPlan.order(), legs, totalSeconds, totalMeters, visitPlan.exact());
    }
}
