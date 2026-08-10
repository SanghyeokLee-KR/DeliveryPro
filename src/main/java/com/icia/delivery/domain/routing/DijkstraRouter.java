package com.icia.delivery.domain.routing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

/**
 * 단일 출발 최단경로 탐색. 도로망 그래프 위에서 다익스트라를 돌린다.
 *
 * <p>간선 가중치가 통행 시간이라 항상 양수다. 음수 간선이 없으므로 벨만포드가 필요 없고,
 * 우선순위 큐에서 뽑힌 시점에 그 노드의 거리는 확정된다.
 *
 * <p>decrease-key 대신 같은 노드를 갱신될 때마다 큐에 다시 넣고, 뽑을 때 이미 확정된
 * 노드면 버린다. java.util.PriorityQueue 에는 키 감소 연산이 없고, 있더라도 원소 위치를
 * 따로 추적해야 해서 이 규모에서는 중복 삽입이 더 싸다.
 */
public final class DijkstraRouter {

    /** 큐에 넣는 후보. 같은 노드가 여러 번 들어갈 수 있다. */
    private record Candidate(int node, double cost) {
    }

    /**
     * 탐색 결과.
     *
     * @param seconds  출발지에서 각 노드까지의 최소 통행 시간. 도달 불가면 무한대
     * @param meters   위 최소 시간 경로를 따라 잰 누적 거리
     * @param settled  방문 확정 집합. 조기 종료 때문에 확정되지 않은 노드가 남을 수 있다
     * @param prevNode 경로 역추적용 직전 노드
     * @param prevEdge 경로 역추적용 직전 간선
     */
    public record Result(double[] seconds,
                         double[] meters,
                         boolean[] settled,
                         int[] prevNode,
                         int[] prevEdge) {

        public boolean isReachable(int node) {
            return settled[node];
        }

        public double secondsTo(int node) {
            return seconds[node];
        }

        public double metersTo(int node) {
            return meters[node];
        }
    }

    private final RoadGraph graph;

    public DijkstraRouter(RoadGraph graph) {
        this.graph = graph;
    }

    /** 출발지에서 그래프 전체로 뻗는 탐색. */
    public Result shortestPaths(int source) {
        return oneToMany(source, null);
    }

    /**
     * 단일 출발 다중 도착 탐색.
     *
     * <p>배달 묶음은 목적지가 여럿이라 출발지 하나에서 여러 도착지까지의 비용이 한꺼번에
     * 필요하다. 다익스트라는 한 번 돌리면 도달한 모든 노드의 최단 비용이 함께 나오므로
     * 목적지 수만큼 반복할 필요가 없다.
     *
     * @param targets 비용이 필요한 노드 목록. null 이면 전체 탐색
     */
    public Result oneToMany(int source, int[] targets) {
        int n = graph.nodeCount();
        if (source < 0 || source >= n) {
            throw new IllegalArgumentException("출발 노드가 그래프 범위를 벗어났다. source=" + source);
        }

        double[] seconds = new double[n];
        double[] meters = new double[n];
        int[] prevNode = new int[n];
        int[] prevEdge = new int[n];
        boolean[] settled = new boolean[n];
        Arrays.fill(seconds, Double.POSITIVE_INFINITY);
        Arrays.fill(meters, Double.POSITIVE_INFINITY);
        Arrays.fill(prevNode, -1);
        Arrays.fill(prevEdge, -1);

        boolean[] wanted = null;
        int remaining = 0;
        if (targets != null) {
            wanted = new boolean[n];
            for (int target : targets) {
                if (target >= 0 && target < n && !wanted[target]) {
                    wanted[target] = true;
                    remaining++;
                }
            }
        }

        PriorityQueue<Candidate> queue = new PriorityQueue<>(Comparator.comparingDouble(Candidate::cost));
        seconds[source] = 0.0;
        meters[source] = 0.0;
        queue.add(new Candidate(source, 0.0));

        while (!queue.isEmpty()) {
            Candidate head = queue.poll();
            int node = head.node();
            if (settled[node]) {
                continue;
            }
            settled[node] = true;

            if (wanted != null && wanted[node] && --remaining == 0) {
                // 뽑힌 순간 비용이 확정되므로 남은 목적지가 없으면 더 볼 필요가 없다.
                break;
            }

            for (int edge = graph.edgeBegin(node); edge < graph.edgeEnd(node); edge++) {
                int next = graph.edgeTarget(edge);
                if (settled[next]) {
                    continue;
                }
                double candidate = seconds[node] + graph.edgeSeconds(edge);
                if (candidate < seconds[next]) {
                    seconds[next] = candidate;
                    meters[next] = meters[node] + graph.edgeMeters(edge);
                    prevNode[next] = node;
                    prevEdge[next] = edge;
                    queue.add(new Candidate(next, candidate));
                }
            }
        }
        return new Result(seconds, meters, settled, prevNode, prevEdge);
    }

    /**
     * 역추적으로 출발지에서 목적지까지의 노드 목록을 만든다.
     *
     * @return 출발지부터 목적지까지의 노드 번호. 도달 불가면 빈 목록
     */
    public List<Integer> reconstructNodePath(Result result, int source, int target) {
        if (!result.settled()[target]) {
            return List.of();
        }
        List<Integer> path = new ArrayList<>();
        for (int node = target; node != -1; node = result.prevNode()[node]) {
            path.add(node);
            if (node == source) {
                break;
            }
        }
        Collections.reverse(path);
        if (path.isEmpty() || path.get(0) != source) {
            return List.of();
        }
        return path;
    }

    /** 화면에 그릴 좌표열. 노드 경로를 좌표로 바꾼 것이다. */
    public List<GeoPoint> reconstructPolyline(Result result, int source, int target) {
        List<Integer> path = reconstructNodePath(result, source, target);
        List<GeoPoint> polyline = new ArrayList<>(path.size());
        for (int node : path) {
            polyline.add(graph.point(node));
        }
        return polyline;
    }
}
