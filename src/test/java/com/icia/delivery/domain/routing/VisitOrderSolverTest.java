package com.icia.delivery.domain.routing;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("방문 순서 결정")
class VisitOrderSolverTest {

    private static double totalOf(double[][] seconds, int[] order) {
        double total = 0.0;
        int current = 0;
        for (int stop : order) {
            total += seconds[current][stop + 1];
            current = stop + 1;
        }
        return total;
    }

    /** 모든 순열을 다 재보는 기준 구현. 정확해가 맞는지 대조하는 용도다. */
    private static double bruteForceBest(double[][] seconds) {
        int n = seconds.length - 1;
        List<Integer> stops = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            stops.add(i);
        }
        return permute(stops, new ArrayList<>(), seconds);
    }

    private static double permute(List<Integer> remaining, List<Integer> prefix, double[][] seconds) {
        if (remaining.isEmpty()) {
            int[] order = prefix.stream().mapToInt(Integer::intValue).toArray();
            return totalOf(seconds, order);
        }
        double best = Double.POSITIVE_INFINITY;
        for (int i = 0; i < remaining.size(); i++) {
            List<Integer> next = new ArrayList<>(remaining);
            int picked = next.remove(i);
            prefix.add(picked);
            best = Math.min(best, permute(next, prefix, seconds));
            prefix.remove(prefix.size() - 1);
        }
        return best;
    }

    private static double[][] randomMatrix(Random random, int size) {
        double[][] seconds = new double[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                // 비대칭으로 만든다. 일방통행이 있는 도로망에서는 왕복 비용이 다르다.
                seconds[i][j] = i == j ? 0.0 : 30.0 + random.nextInt(600);
            }
        }
        return seconds;
    }

    @Test
    @DisplayName("정확해는 완전탐색 결과와 같은 비용을 낸다")
    void exactSolutionMatchesBruteForce() {
        Random random = new Random(20260811L);
        for (int stopCount = 2; stopCount <= 7; stopCount++) {
            for (int trial = 0; trial < 20; trial++) {
                double[][] seconds = randomMatrix(random, stopCount + 1);
                VisitOrderSolver.Plan plan = VisitOrderSolver.solve(seconds);

                assertTrue(plan.exact(), "정차지 " + stopCount + "곳은 정확해 범위다");
                assertEquals(bruteForceBest(seconds), plan.totalSeconds(), 1e-9);
                assertEquals(totalOf(seconds, plan.order()), plan.totalSeconds(), 1e-9);
                assertPermutation(plan.order(), stopCount);
            }
        }
    }

    @Test
    @DisplayName("정차지가 상한을 넘으면 휴리스틱으로 내려가되 최근접 이웃보다 나쁘지 않다")
    void heuristicIsNotWorseThanNearestNeighbor() {
        Random random = new Random(7L);
        int stopCount = VisitOrderSolver.EXACT_LIMIT + 3;
        for (int trial = 0; trial < 10; trial++) {
            double[][] seconds = randomMatrix(random, stopCount + 1);
            VisitOrderSolver.Plan plan = VisitOrderSolver.solve(seconds);

            assertFalse(plan.exact(), "상한을 넘으면 근사임을 표시해야 한다");
            assertPermutation(plan.order(), stopCount);
            assertEquals(totalOf(seconds, plan.order()), plan.totalSeconds(), 1e-6);
            assertTrue(plan.totalSeconds() <= nearestNeighborCost(seconds) + 1e-6);
        }
    }

    private static double nearestNeighborCost(double[][] seconds) {
        int n = seconds.length - 1;
        boolean[] used = new boolean[n];
        int[] order = new int[n];
        int current = 0;
        for (int step = 0; step < n; step++) {
            int best = -1;
            for (int candidate = 0; candidate < n; candidate++) {
                if (!used[candidate] && (best < 0 || seconds[current][candidate + 1] < seconds[current][best + 1])) {
                    best = candidate;
                }
            }
            used[best] = true;
            order[step] = best;
            current = best + 1;
        }
        return totalOf(seconds, order);
    }

    @Test
    @DisplayName("도달 불가 정차지는 순서 뒤로 밀린다")
    void unreachableStopIsPushedToTheEnd() {
        // 행렬 인덱스 2, 곧 정차지 1 번이 오도 가도 못하는 자리다.
        // 중간에 두면 들어가는 비용과 나오는 비용을 둘 다 물어야 해서 마지막이 최선이 된다.
        double[][] seconds = {
                {0.0, 60.0, Double.POSITIVE_INFINITY, 90.0},
                {60.0, 0.0, Double.POSITIVE_INFINITY, 40.0},
                {Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, 0.0, Double.POSITIVE_INFINITY},
                {90.0, 40.0, Double.POSITIVE_INFINITY, 0.0},
        };
        VisitOrderSolver.Plan plan = VisitOrderSolver.solve(seconds);

        assertPermutation(plan.order(), 3);
        assertEquals(1, plan.order()[plan.order().length - 1], "어디서도 못 가는 정차지는 마지막에 온다");
    }

    @Test
    @DisplayName("정차지가 없거나 하나면 그대로 돌려준다")
    void trivialCases() {
        assertArrayEquals(new int[0], VisitOrderSolver.solve(new double[][]{{0.0}}).order());

        double[][] single = {{0.0, 45.0}, {45.0, 0.0}};
        VisitOrderSolver.Plan plan = VisitOrderSolver.solve(single);
        assertArrayEquals(new int[]{0}, plan.order());
        assertEquals(45.0, plan.totalSeconds(), 1e-9);
    }

    private static void assertPermutation(int[] order, int stopCount) {
        assertEquals(stopCount, order.length);
        int[] sorted = Arrays.copyOf(order, order.length);
        Arrays.sort(sorted);
        for (int i = 0; i < stopCount; i++) {
            assertEquals(i, sorted[i], "방문 순서는 정차지 전체의 순열이어야 한다");
        }
    }
}
