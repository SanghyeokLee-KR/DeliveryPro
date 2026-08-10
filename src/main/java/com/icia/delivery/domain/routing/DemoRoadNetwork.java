package com.icia.delivery.domain.routing;

import com.icia.delivery.util.GeoDistanceUtil;

/**
 * 데모 서비스 구역의 도로망을 조립한다.
 *
 * <p>출처를 분명히 해 둔다. 이 도로망은 실제 지도 데이터를 내려받아 만든 것이 아니라,
 * 서비스 구역 범위 안에 간선도로·보조간선·이면도로 위계와 일방통행, 통과 불가 축을
 * 재현한 합성 도로망이다. 런타임에 외부 지도 API 를 호출하지 않으려고 이렇게 두었다.
 * 실제 지도로 바꿀 때는 이 클래스만 교체하면 되고, 탐색·정렬 계층은 손대지 않는다.
 *
 * <p>합성이라도 지켜야 하는 성질은 지킨다.
 * 첫째, 두 정차지를 곧장 잇는 간선이 없다. 이동은 반드시 교차점을 거친다.
 * 둘째, 간선 가중치가 도로 위계에 따라 다르다. 모두 같았다면 너비 우선 탐색으로 충분하다.
 * 셋째, 일방통행이 있어 A 에서 B 로 가는 비용과 그 반대가 다르다.
 * 넷째, 통과 불가 축이 있어 직선 방향으로 붙어 있어도 멀리 돌아야 하는 구간이 생긴다.
 */
public final class DemoRoadNetwork {

    /** 서비스 구역 경계. 데모 시드 주소가 모두 이 범위 안에 든다. */
    public static final double MIN_LON = 126.620;
    public static final double MAX_LON = 126.700;
    public static final double MIN_LAT = 37.415;
    public static final double MAX_LAT = 37.480;

    private static final int COLS = 25;
    private static final int ROWS = 21;

    /** 도로 위계별 평균 통행 속도(km/h). 이륜차 기준으로 잡았다. */
    private static final double SPEED_ARTERIAL_KMH = 50.0;
    private static final double SPEED_COLLECTOR_KMH = 35.0;
    private static final double SPEED_LOCAL_KMH = 25.0;

    /** 교차점 통과 지연(초). 거리가 같아도 위계가 낮은 길이 더 걸리게 만드는 항이다. */
    private static final double DELAY_ARTERIAL_SECONDS = 7.0;
    private static final double DELAY_COLLECTOR_SECONDS = 4.0;
    private static final double DELAY_LOCAL_SECONDS = 2.0;

    /** 남북 통과가 막힌 축. 이 두 행 사이는 아래 열에서만 건널 수 있다. */
    private static final int EXPRESSWAY_ROW = 12;
    private static final int[] EXPRESSWAY_CROSSING_COLS = {2, 11, 20};

    /** 동서 통과가 막힌 축. 이 두 열 사이는 아래 행에서만 건널 수 있다. */
    private static final int RAILWAY_COL = 7;
    private static final int[] RAILWAY_CROSSING_ROWS = {4, 13, 19};

    /** 북행 전용 열과 남행 전용 열. 실제 도심의 일방통행 쌍을 흉내낸다. */
    private static final int[] NORTHBOUND_ONLY_COLS = {3, 17};
    private static final int[] SOUTHBOUND_ONLY_COLS = {4, 16};

    /** 동행 전용 행과 서행 전용 행. */
    private static final int[] EASTBOUND_ONLY_ROWS = {8};
    private static final int[] WESTBOUND_ONLY_ROWS = {9};

    /** 막다른 길 비율. 이면도로 간선 일부를 끊어 격자 티를 줄인다. */
    private static final double DEAD_END_RATIO = 0.07;

    private DemoRoadNetwork() {
    }

    private enum RoadClass {
        ARTERIAL(SPEED_ARTERIAL_KMH, DELAY_ARTERIAL_SECONDS),
        COLLECTOR(SPEED_COLLECTOR_KMH, DELAY_COLLECTOR_SECONDS),
        LOCAL(SPEED_LOCAL_KMH, DELAY_LOCAL_SECONDS);

        private final double speedKmh;
        private final double delaySeconds;

        RoadClass(double speedKmh, double delaySeconds) {
            this.speedKmh = speedKmh;
            this.delaySeconds = delaySeconds;
        }
    }

    /** 좌표 흔들기와 막다른 길 선택을 고정 시드로 뽑는다. 빌드마다 같은 그래프가 나와야 한다. */
    private static final class DeterministicNoise {
        private long state;

        DeterministicNoise(long seed) {
            this.state = seed;
        }

        double next() {
            state = state * 6364136223846793005L + 1442695040888963407L;
            return ((state >>> 11) & ((1L << 53) - 1)) / (double) (1L << 53);
        }
    }

    private static RoadClass classOf(int index) {
        if (index % 5 == 0) {
            return RoadClass.ARTERIAL;
        }
        if (index % 5 == 2) {
            return RoadClass.COLLECTOR;
        }
        return RoadClass.LOCAL;
    }

    private static boolean contains(int[] values, int value) {
        for (int v : values) {
            if (v == value) {
                return true;
            }
        }
        return false;
    }

    /**
     * 도로망을 만들고 최대 강한 연결 요소만 남겨 돌려준다.
     */
    public static RoadGraph build() {
        RoadGraph.Builder builder = RoadGraph.builder();
        DeterministicNoise noise = new DeterministicNoise(20260811L);

        double lonStep = (MAX_LON - MIN_LON) / (COLS - 1);
        double latStep = (MAX_LAT - MIN_LAT) / (ROWS - 1);

        int[][] nodeId = new int[ROWS][COLS];
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                // 규칙적인 격자는 실제 도로망과 달리 모든 블록 길이가 같아진다.
                // 교차점을 조금씩 흔들어 구간 길이에 편차를 준다.
                double lon = MIN_LON + c * lonStep + (noise.next() - 0.5) * 0.6 * lonStep;
                double lat = MIN_LAT + r * latStep + (noise.next() - 0.5) * 0.6 * latStep;
                nodeId[r][c] = builder.addNode(lon, lat);
            }
        }

        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                if (c + 1 < COLS) {
                    addRoad(builder, noise, nodeId[r][c], nodeId[r][c + 1], classOf(r),
                            eastAllowed(r, c), westAllowed(r, c));
                }
                if (r + 1 < ROWS) {
                    addRoad(builder, noise, nodeId[r][c], nodeId[r + 1][c], classOf(c),
                            northAllowed(r, c), southAllowed(r, c));
                }
            }
        }
        return builder.buildLargestStronglyConnectedComponent();
    }

    private static boolean crossesRailway(int c) {
        return c == RAILWAY_COL;
    }

    private static boolean crossesExpressway(int r) {
        return r == EXPRESSWAY_ROW;
    }

    private static boolean eastAllowed(int r, int c) {
        if (crossesRailway(c) && !contains(RAILWAY_CROSSING_ROWS, r)) {
            return false;
        }
        return !contains(WESTBOUND_ONLY_ROWS, r);
    }

    private static boolean westAllowed(int r, int c) {
        if (crossesRailway(c) && !contains(RAILWAY_CROSSING_ROWS, r)) {
            return false;
        }
        return !contains(EASTBOUND_ONLY_ROWS, r);
    }

    private static boolean northAllowed(int r, int c) {
        if (crossesExpressway(r) && !contains(EXPRESSWAY_CROSSING_COLS, c)) {
            return false;
        }
        return !contains(SOUTHBOUND_ONLY_COLS, c);
    }

    private static boolean southAllowed(int r, int c) {
        if (crossesExpressway(r) && !contains(EXPRESSWAY_CROSSING_COLS, c)) {
            return false;
        }
        return !contains(NORTHBOUND_ONLY_COLS, c);
    }

    private static void addRoad(RoadGraph.Builder builder,
                                DeterministicNoise noise,
                                int from,
                                int to,
                                RoadClass roadClass,
                                boolean forward,
                                boolean backward) {
        double roll = noise.next();
        if (roadClass == RoadClass.LOCAL && roll < DEAD_END_RATIO) {
            return;
        }
        if (!forward && !backward) {
            return;
        }
        double meters = GeoDistanceUtil.haversineMetersByLonLat(
                builder.lon(from), builder.lat(from), builder.lon(to), builder.lat(to));
        double seconds = GeoDistanceUtil.travelSeconds(meters, roadClass.speedKmh) + roadClass.delaySeconds;
        if (forward) {
            builder.addEdge(from, to, meters, seconds);
        }
        if (backward) {
            builder.addEdge(to, from, meters, seconds);
        }
    }
}
