package com.icia.delivery.domain.routing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 웹 계층이 쓰는 경로 계획 진입점.
 */
@Service
public class RoutePlanService {

    private static final Logger log = LoggerFactory.getLogger(RoutePlanService.class);

    private final RoutePlanner planner;

    public RoutePlanService() {
        this(RoutePlanner.demo());
    }

    RoutePlanService(RoutePlanner planner) {
        this.planner = planner;
    }

    public RoutePlan plan(GeoPoint start, List<GeoPoint> destinations) {
        RoutePlan plan = planner.plan(start, destinations);
        log.debug("경로 계획 완료. stops={}, totalSeconds={}, exact={}",
                plan.order().length, plan.totalSeconds(), plan.exact());
        return plan;
    }

    public int[] orderDestinations(GeoPoint start, List<GeoPoint> destinations) {
        return planner.orderDestinations(start, destinations);
    }
}
