package com.icia.delivery.domain.routing;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;

/**
 * 도로망 유향 그래프. 노드는 교차점, 간선은 교차점 사이의 도로 구간이다.
 *
 * <p>배달지끼리 잇는 직행 간선은 존재하지 않는다. 임의의 두 지점 사이 이동 비용은
 * 교차점을 여러 번 거치는 경로 탐색으로만 구해진다. 완전 그래프였다면 출발지와
 * 목적지를 잇는 간선이 늘 최적이라 탐색이 할 일이 없다.
 *
 * <p>인접 리스트는 CSR(Compressed Sparse Row)로 담는다. 노드별 List 를 쓰면
 * 간선 하나마다 객체가 생겨 탐색 중 캐시 지역성이 나빠진다.
 */
public final class RoadGraph {

    private final double[] nodeLon;
    private final double[] nodeLat;
    private final int[] edgeOffset;
    private final int[] edgeTo;
    private final double[] edgeSeconds;
    private final double[] edgeMeters;

    private RoadGraph(double[] nodeLon,
                      double[] nodeLat,
                      int[] edgeOffset,
                      int[] edgeTo,
                      double[] edgeSeconds,
                      double[] edgeMeters) {
        this.nodeLon = nodeLon;
        this.nodeLat = nodeLat;
        this.edgeOffset = edgeOffset;
        this.edgeTo = edgeTo;
        this.edgeSeconds = edgeSeconds;
        this.edgeMeters = edgeMeters;
    }

    public int nodeCount() {
        return nodeLon.length;
    }

    public int edgeCount() {
        return edgeTo.length;
    }

    public double lon(int node) {
        return nodeLon[node];
    }

    public double lat(int node) {
        return nodeLat[node];
    }

    public GeoPoint point(int node) {
        return new GeoPoint(nodeLon[node], nodeLat[node]);
    }

    /** 노드에서 나가는 간선 구간의 시작 인덱스. */
    public int edgeBegin(int node) {
        return edgeOffset[node];
    }

    /** 노드에서 나가는 간선 구간의 끝 인덱스(미포함). */
    public int edgeEnd(int node) {
        return edgeOffset[node + 1];
    }

    public int edgeTarget(int edge) {
        return edgeTo[edge];
    }

    /** 간선 통과에 걸리는 시간(초). 다익스트라의 가중치다. */
    public double edgeSeconds(int edge) {
        return edgeSeconds[edge];
    }

    public double edgeMeters(int edge) {
        return edgeMeters[edge];
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * 그래프 조립기. 노드를 먼저 넣고 간선을 넣은 뒤 build 로 CSR 을 만든다.
     */
    public static final class Builder {

        private final List<double[]> nodes = new ArrayList<>();
        private final List<int[]> edgeEnds = new ArrayList<>();
        private final List<double[]> edgeCosts = new ArrayList<>();

        public int addNode(double lon, double lat) {
            nodes.add(new double[]{lon, lat});
            return nodes.size() - 1;
        }

        public int nodeCount() {
            return nodes.size();
        }

        public double lon(int node) {
            return nodes.get(node)[0];
        }

        public double lat(int node) {
            return nodes.get(node)[1];
        }

        /**
         * 유향 간선 하나를 넣는다. 양방향 도로는 이 메서드를 두 번 부른다.
         * 일방통행은 한 번만 불러 방향을 그래프 구조에 남긴다.
         */
        public Builder addEdge(int from, int to, double meters, double seconds) {
            if (seconds <= 0.0) {
                throw new IllegalArgumentException("간선 가중치는 양수여야 한다. seconds=" + seconds);
            }
            edgeEnds.add(new int[]{from, to});
            edgeCosts.add(new double[]{meters, seconds});
            return this;
        }

        public RoadGraph build() {
            int n = nodes.size();
            int m = edgeEnds.size();
            int[] degree = new int[n];
            for (int[] ends : edgeEnds) {
                degree[ends[0]]++;
            }
            int[] offset = new int[n + 1];
            for (int i = 0; i < n; i++) {
                offset[i + 1] = offset[i] + degree[i];
            }
            int[] cursor = Arrays.copyOf(offset, n);
            int[] to = new int[m];
            double[] seconds = new double[m];
            double[] meters = new double[m];
            for (int i = 0; i < m; i++) {
                int[] ends = edgeEnds.get(i);
                double[] cost = edgeCosts.get(i);
                int slot = cursor[ends[0]]++;
                to[slot] = ends[1];
                meters[slot] = cost[0];
                seconds[slot] = cost[1];
            }
            double[] lons = new double[n];
            double[] lats = new double[n];
            for (int i = 0; i < n; i++) {
                lons[i] = nodes.get(i)[0];
                lats[i] = nodes.get(i)[1];
            }
            return new RoadGraph(lons, lats, offset, to, seconds, meters);
        }

        /**
         * 최대 강한 연결 요소만 남겨 build 한다.
         *
         * <p>일방통행 때문에 A 에서 B 로는 가도 B 에서 A 로는 못 가는 노드가 생긴다.
         * 그런 노드에 배달지를 붙이면 경로 계산이 도달 불가로 끝난다. 어느 두 지점을
         * 잡아도 서로 오갈 수 있는 영역만 남겨 그 상황을 없앤다.
         */
        public RoadGraph buildLargestStronglyConnectedComponent() {
            RoadGraph full = build();
            int[] component = stronglyConnectedComponents(full);
            int componentCount = 0;
            for (int c : component) {
                componentCount = Math.max(componentCount, c + 1);
            }
            int[] size = new int[componentCount];
            for (int c : component) {
                size[c]++;
            }
            int largest = 0;
            for (int c = 1; c < componentCount; c++) {
                if (size[c] > size[largest]) {
                    largest = c;
                }
            }
            int[] remap = new int[full.nodeCount()];
            Arrays.fill(remap, -1);
            Builder kept = new Builder();
            for (int node = 0; node < full.nodeCount(); node++) {
                if (component[node] == largest) {
                    remap[node] = kept.addNode(full.lon(node), full.lat(node));
                }
            }
            for (int node = 0; node < full.nodeCount(); node++) {
                if (remap[node] < 0) {
                    continue;
                }
                for (int e = full.edgeBegin(node); e < full.edgeEnd(node); e++) {
                    int target = full.edgeTarget(e);
                    if (remap[target] >= 0) {
                        kept.addEdge(remap[node], remap[target], full.edgeMeters(e), full.edgeSeconds(e));
                    }
                }
            }
            return kept.build();
        }
    }

    /**
     * 코사라주 알고리즘으로 강한 연결 요소를 구한다.
     * 노드 수가 수백 규모라도 재귀 DFS 는 스택 깊이가 입력에 좌우되므로 반복문으로 쓴다.
     */
    static int[] stronglyConnectedComponents(RoadGraph graph) {
        int n = graph.nodeCount();
        boolean[] visited = new boolean[n];
        int[] order = new int[n];
        int filled = 0;

        for (int start = 0; start < n; start++) {
            if (visited[start]) {
                continue;
            }
            Deque<int[]> stack = new ArrayDeque<>();
            visited[start] = true;
            stack.push(new int[]{start, graph.edgeBegin(start)});
            while (!stack.isEmpty()) {
                int[] frame = stack.peek();
                int node = frame[0];
                if (frame[1] < graph.edgeEnd(node)) {
                    int next = graph.edgeTarget(frame[1]++);
                    if (!visited[next]) {
                        visited[next] = true;
                        stack.push(new int[]{next, graph.edgeBegin(next)});
                    }
                } else {
                    order[filled++] = node;
                    stack.pop();
                }
            }
        }

        int[] reverseOffset = new int[n + 1];
        for (int e = 0; e < graph.edgeCount(); e++) {
            reverseOffset[graph.edgeTarget(e) + 1]++;
        }
        for (int i = 0; i < n; i++) {
            reverseOffset[i + 1] += reverseOffset[i];
        }
        int[] cursor = Arrays.copyOf(reverseOffset, n);
        int[] reverseTo = new int[graph.edgeCount()];
        for (int node = 0; node < n; node++) {
            for (int e = graph.edgeBegin(node); e < graph.edgeEnd(node); e++) {
                reverseTo[cursor[graph.edgeTarget(e)]++] = node;
            }
        }

        int[] component = new int[n];
        Arrays.fill(component, -1);
        int assigned = 0;
        for (int i = n - 1; i >= 0; i--) {
            int root = order[i];
            if (component[root] >= 0) {
                continue;
            }
            Deque<Integer> stack = new ArrayDeque<>();
            component[root] = assigned;
            stack.push(root);
            while (!stack.isEmpty()) {
                int node = stack.pop();
                for (int e = reverseOffset[node]; e < reverseOffset[node + 1]; e++) {
                    int prev = reverseTo[e];
                    if (component[prev] < 0) {
                        component[prev] = assigned;
                        stack.push(prev);
                    }
                }
            }
            assigned++;
        }
        return component;
    }
}
