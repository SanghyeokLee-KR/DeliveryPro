package com.icia.delivery.domain.routing;

import com.icia.delivery.util.GeoDistanceUtil;

import java.util.List;

/**
 * 정차지 사이의 이동 비용 행렬.
 *
 * <p>0 번은 출발지(가게 또는 라이더 현재 위치), 1 번부터는 배달지다.
 * 한 행을 채우는 데 다익스트라 한 번이면 된다. 목적지마다 따로 돌리지 않는 이유다.
 *
 * @param nodes      각 정차지가 붙은 도로 노드 번호. 붙이지 못했으면 -1
 * @param snapMeters 정차지에서 그 노드까지의 거리. 붙이지 못했으면 0
 * @param seconds    [i][j] = i 에서 j 까지의 최소 통행 시간. 도달 불가면 무한대
 * @param meters     같은 경로를 따라 잰 거리
 * @param reachable  도달 가능 여부
 */
public record TravelCostMatrix(int[] nodes,
                               double[] snapMeters,
                               double[][] seconds,
                               double[][] meters,
                               boolean[][] reachable) {

    /**
     * 스냅 구간에 매기는 속도(km/h). 주소에서 도로까지는 차를 세우고 걸어 들어가는 구간이라
     * 주행 속도를 쓰면 안 된다.
     */
    public static final double SNAP_WALK_SPEED_KMH = 4.5;

    public int size() {
        return nodes.length;
    }

    /**
     * 정차지 좌표 목록으로 비용 행렬을 만든다.
     *
     * @param stops 0 번이 출발지인 좌표 목록. null 원소는 좌표를 못 구한 정차지로 본다
     */
    public static TravelCostMatrix build(RoadGraph graph, NodeIndex index, List<GeoPoint> stops) {
        int n = stops.size();
        int[] nodes = new int[n];
        double[] snapMeters = new double[n];
        for (int i = 0; i < n; i++) {
            GeoPoint stop = stops.get(i);
            nodes[i] = stop == null
                    ? -1
                    : index.nearest(stop.lon(), stop.lat(), RoadNetworkProvider.SNAP_LIMIT_METERS);
            if (nodes[i] >= 0) {
                snapMeters[i] = GeoDistanceUtil.haversineMetersByLonLat(
                        stop.lon(), stop.lat(), graph.lon(nodes[i]), graph.lat(nodes[i]));
            }
        }

        double[][] seconds = new double[n][n];
        double[][] meters = new double[n][n];
        boolean[][] reachable = new boolean[n][n];
        for (double[] row : seconds) {
            java.util.Arrays.fill(row, Double.POSITIVE_INFINITY);
        }
        for (double[] row : meters) {
            java.util.Arrays.fill(row, Double.POSITIVE_INFINITY);
        }

        DijkstraRouter router = new DijkstraRouter(graph);
        int[] targets = new int[n];
        for (int from = 0; from < n; from++) {
            if (nodes[from] < 0) {
                continue;
            }
            int targetCount = 0;
            for (int to = 0; to < n; to++) {
                if (to != from && nodes[to] >= 0) {
                    targets[targetCount++] = nodes[to];
                }
            }
            DijkstraRouter.Result result = router.oneToMany(nodes[from], java.util.Arrays.copyOf(targets, targetCount));
            seconds[from][from] = 0.0;
            meters[from][from] = 0.0;
            reachable[from][from] = true;
            for (int to = 0; to < n; to++) {
                if (to == from || nodes[to] < 0) {
                    continue;
                }
                if (result.isReachable(nodes[to])) {
                    // 도로 노드 사이 비용만 쓰면 주소에서 도로까지 들어가고 나오는 거리가 사라진다.
                    // 그러면 같은 교차점에 붙은 두 배달지 사이 이동이 0초로 보고된다.
                    seconds[from][to] = walkSeconds(snapMeters[from])
                            + result.secondsTo(nodes[to])
                            + walkSeconds(snapMeters[to]);
                    meters[from][to] = snapMeters[from] + result.metersTo(nodes[to]) + snapMeters[to];
                    reachable[from][to] = true;
                }
            }
        }
        return new TravelCostMatrix(nodes, snapMeters, seconds, meters, reachable);
    }

    private static double walkSeconds(double meters) {
        return meters <= 0.0 ? 0.0 : GeoDistanceUtil.travelSeconds(meters, SNAP_WALK_SPEED_KMH);
    }
}
