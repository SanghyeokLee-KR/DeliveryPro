package com.icia.delivery.domain.routing;

import com.icia.delivery.util.GeoDistanceUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 임의 좌표를 가장 가까운 도로 교차점에 붙이는 공간 색인.
 *
 * <p>가게와 배달지는 도로망 노드가 아니다. 경로 탐색을 시작하려면 먼저 도로에 올려야 한다.
 * 전체 노드를 매번 훑으면 정차지 하나마다 O(V) 라서, 좌표를 격자 칸으로 나눠 후보를 줄인다.
 */
public final class NodeIndex {

    private final RoadGraph graph;
    private final double cellDegrees;
    private final Map<Long, int[]> buckets;

    /**
     * @param cellMeters 격자 칸 한 변의 대략적인 길이(미터)
     */
    public NodeIndex(RoadGraph graph, double cellMeters) {
        this.graph = graph;
        // 위도 1도는 어디서나 약 111km 다. 경도는 위도에 따라 짧아지지만 칸 크기는 후보를
        // 추리는 용도라 위도 기준 한 가지로 충분하다.
        this.cellDegrees = cellMeters / 111_320.0;
        Map<Long, List<Integer>> grouped = new HashMap<>();
        for (int node = 0; node < graph.nodeCount(); node++) {
            grouped.computeIfAbsent(cellKey(graph.lon(node), graph.lat(node)), k -> new ArrayList<>()).add(node);
        }
        this.buckets = new HashMap<>();
        for (Map.Entry<Long, List<Integer>> entry : grouped.entrySet()) {
            List<Integer> members = entry.getValue();
            int[] packed = new int[members.size()];
            for (int i = 0; i < packed.length; i++) {
                packed[i] = members.get(i);
            }
            buckets.put(entry.getKey(), packed);
        }
    }

    private long cellKey(double lon, double lat) {
        long col = (long) Math.floor(lon / cellDegrees);
        long row = (long) Math.floor(lat / cellDegrees);
        return (col << 32) ^ (row & 0xFFFF_FFFFL);
    }

    /**
     * 좌표에 가장 가까운 노드를 찾는다.
     *
     * @param maxMeters 이 거리를 넘으면 붙이지 않는다. 서비스 구역 밖 주소를 도로에
     *                  억지로 올려 엉뚱한 경로를 내지 않기 위한 방어선이다
     * @return 노드 번호. 찾지 못하면 -1
     */
    public int nearest(double lon, double lat, double maxMeters) {
        int found = -1;
        double best = maxMeters;
        long baseCol = (long) Math.floor(lon / cellDegrees);
        long baseRow = (long) Math.floor(lat / cellDegrees);
        double cellMeters = cellDegrees * 111_320.0;

        for (int ring = 0; ; ring++) {
            // ring 번째 테두리에 있는 칸은 최소 (ring-1) 칸 거리만큼 떨어져 있다.
            // 그 하한이 지금까지 찾은 최선보다 멀면 더 넓혀도 개선될 수 없다.
            if ((ring - 1) * cellMeters > best) {
                return found;
            }
            for (long dc = -ring; dc <= ring; dc++) {
                for (long dr = -ring; dr <= ring; dr++) {
                    if (ring > 0 && Math.abs(dc) != ring && Math.abs(dr) != ring) {
                        continue;
                    }
                    int[] members = buckets.get(((baseCol + dc) << 32) ^ ((baseRow + dr) & 0xFFFF_FFFFL));
                    if (members == null) {
                        continue;
                    }
                    for (int node : members) {
                        double distance = GeoDistanceUtil.haversineMetersByLonLat(
                                lon, lat, graph.lon(node), graph.lat(node));
                        if (distance < best) {
                            best = distance;
                            found = node;
                        }
                    }
                }
            }
        }
    }
}
