package com.icia.delivery.domain.routing;

import java.util.Arrays;

/**
 * 방문 순서를 정한다.
 *
 * <p>다익스트라와 분리한 이유를 분명히 해 둔다. 다익스트라는 한 지점에서 다른 지점까지의
 * 최단경로를 구하는 알고리즘이지 방문 순서를 정하지 않는다. 순서 결정은 출발지가 고정된
 * 열린 외판원 문제이고, 여기서는 다익스트라가 만든 비용 행렬 위에서 따로 푼다.
 *
 * <p>묶음배달은 정차지가 많아야 열 곳 남짓이다. 그 범위에서는 비트마스크 동적계획법으로
 * 정확해를 구할 수 있어 근사로 물러설 이유가 없다. 그 위로 넘어가면 상태 수가 감당이
 * 안 되므로 최근접 이웃으로 초기해를 만들고 2-opt 로 개선한다.
 */
public final class VisitOrderSolver {

    /** 정확해로 풀 수 있는 정차지 수의 상한. 상태 수는 2^n * n 이다. */
    public static final int EXACT_LIMIT = 12;

    /**
     * 도달 불가 구간에 매기는 벌점(초). 무한대를 그대로 더하면 모든 경로가 무한대가 되어
     * 순서를 비교할 수 없다. 실제 이동으로는 나올 수 없는 크기를 주어 뒤로 밀어낸다.
     */
    private static final double UNREACHABLE_PENALTY_SECONDS = 1_000_000.0;

    /**
     * @param order        정차지 방문 순서. 값은 비용 행렬의 1 번부터 시작하는 인덱스에서 1 을 뺀 값
     * @param totalSeconds 출발지부터 마지막 정차지까지의 총 통행 시간
     * @param exact        정확해면 true, 휴리스틱 결과면 false
     */
    public record Plan(int[] order, double totalSeconds, boolean exact) {
    }

    private VisitOrderSolver() {
    }

    /**
     * @param seconds 0 번이 출발지인 (n+1)x(n+1) 비용 행렬
     */
    public static Plan solve(double[][] seconds) {
        int stopCount = seconds.length - 1;
        if (stopCount <= 0) {
            return new Plan(new int[0], 0.0, true);
        }
        if (stopCount == 1) {
            return new Plan(new int[]{0}, cost(seconds, 0, 1), true);
        }
        return stopCount <= EXACT_LIMIT ? heldKarp(seconds) : nearestNeighborWithTwoOpt(seconds);
    }

    private static double cost(double[][] seconds, int from, int to) {
        double value = seconds[from][to];
        return Double.isFinite(value) ? value : UNREACHABLE_PENALTY_SECONDS;
    }

    /**
     * Held-Karp 비트마스크 동적계획법.
     * dp[mask][last] = 출발지에서 시작해 mask 에 든 정차지를 모두 들르고 last 에서 끝나는 최소 비용.
     * 출발지로 돌아오지 않는 열린 경로라 복귀 항을 더하지 않는다.
     */
    private static Plan heldKarp(double[][] seconds) {
        int n = seconds.length - 1;
        int full = 1 << n;
        double[][] dp = new double[full][n];
        int[][] parent = new int[full][n];
        for (double[] row : dp) {
            Arrays.fill(row, Double.POSITIVE_INFINITY);
        }
        for (int[] row : parent) {
            Arrays.fill(row, -1);
        }
        for (int i = 0; i < n; i++) {
            dp[1 << i][i] = cost(seconds, 0, i + 1);
        }
        for (int mask = 1; mask < full; mask++) {
            for (int last = 0; last < n; last++) {
                if ((mask & (1 << last)) == 0 || !Double.isFinite(dp[mask][last])) {
                    continue;
                }
                double base = dp[mask][last];
                for (int next = 0; next < n; next++) {
                    if ((mask & (1 << next)) != 0) {
                        continue;
                    }
                    int nextMask = mask | (1 << next);
                    double candidate = base + cost(seconds, last + 1, next + 1);
                    if (candidate < dp[nextMask][next]) {
                        dp[nextMask][next] = candidate;
                        parent[nextMask][next] = last;
                    }
                }
            }
        }
        int bestLast = 0;
        for (int last = 1; last < n; last++) {
            if (dp[full - 1][last] < dp[full - 1][bestLast]) {
                bestLast = last;
            }
        }
        int[] order = new int[n];
        int mask = full - 1;
        int last = bestLast;
        for (int i = n - 1; i >= 0; i--) {
            order[i] = last;
            int previous = parent[mask][last];
            mask ^= (1 << last);
            last = previous;
        }
        return new Plan(order, dp[full - 1][bestLast], true);
    }

    private static Plan nearestNeighborWithTwoOpt(double[][] seconds) {
        int n = seconds.length - 1;
        boolean[] used = new boolean[n];
        int[] order = new int[n];
        int current = 0;
        for (int step = 0; step < n; step++) {
            int best = -1;
            double bestCost = Double.POSITIVE_INFINITY;
            for (int candidate = 0; candidate < n; candidate++) {
                if (used[candidate]) {
                    continue;
                }
                double value = cost(seconds, current, candidate + 1);
                if (value < bestCost) {
                    bestCost = value;
                    best = candidate;
                }
            }
            used[best] = true;
            order[step] = best;
            current = best + 1;
        }

        double bestTotal = totalCost(seconds, order);
        boolean improved = true;
        while (improved) {
            improved = false;
            for (int i = 0; i < n - 1; i++) {
                for (int j = i + 1; j < n; j++) {
                    // 비용 행렬이 비대칭이라 뒤집힌 구간의 비용을 부분식으로 못 구한다.
                    // 구간을 실제로 뒤집고 전체를 다시 재는 편이 안전하다.
                    reverse(order, i, j);
                    double candidate = totalCost(seconds, order);
                    if (candidate < bestTotal - 1e-9) {
                        bestTotal = candidate;
                        improved = true;
                    } else {
                        reverse(order, i, j);
                    }
                }
            }
        }
        return new Plan(order, bestTotal, false);
    }

    private static void reverse(int[] order, int from, int to) {
        while (from < to) {
            int swap = order[from];
            order[from] = order[to];
            order[to] = swap;
            from++;
            to--;
        }
    }

    private static double totalCost(double[][] seconds, int[] order) {
        double total = 0.0;
        int current = 0;
        for (int stop : order) {
            total += cost(seconds, current, stop + 1);
            current = stop + 1;
        }
        return total;
    }
}
