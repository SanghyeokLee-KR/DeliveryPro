package com.icia.delivery.domain.routing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 도로망 그래프와 공간 색인을 한 번만 만들어 공유한다.
 *
 * <p>정적 유틸(KakaoGeocoderUtil.RouteOptimizer)과 스프링 빈이 같은 그래프를 써야 해서
 * 컨테이너 밖에서도 닿을 수 있는 홀더로 둔다. 초기화 요청 시점 클래스 로딩을 이용하므로
 * 별도 동기화 없이 한 번만 만들어진다.
 */
public final class RoadNetworkProvider {

    private static final Logger log = LoggerFactory.getLogger(RoadNetworkProvider.class);

    /** 정차지를 도로에 붙일 때 허용하는 최대 거리. 넘으면 서비스 구역 밖으로 본다. */
    public static final double SNAP_LIMIT_METERS = 1_500.0;

    private static final double INDEX_CELL_METERS = 400.0;

    private RoadNetworkProvider() {
    }

    private static final class Holder {
        private static final RoadGraph GRAPH = DemoRoadNetwork.build();
        private static final NodeIndex INDEX = new NodeIndex(GRAPH, INDEX_CELL_METERS);

        static {
            log.info("도로망 그래프 적재 완료. nodes={}, edges={}", GRAPH.nodeCount(), GRAPH.edgeCount());
        }
    }

    public static RoadGraph graph() {
        return Holder.GRAPH;
    }

    public static NodeIndex index() {
        return Holder.INDEX;
    }
}
