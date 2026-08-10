package com.icia.delivery.domain.routing;

import com.icia.delivery.global.exception.BusinessException;
import com.icia.delivery.global.exception.ErrorCode;
import com.icia.delivery.global.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * 묶음배달 경로 계획 조회.
 *
 * <p>화면이 구간마다 따로 호출하던 것을 한 번으로 모은다. 순서와 구간 정보가 같은 계산에서
 * 나와야 화면에 보이는 경로와 서버가 저장한 order_sequence 가 어긋나지 않는다.
 */
@RestController
public class RoutePlanController {

    /** 화면 좌표 규약을 그대로 받는다. x 가 경도, y 가 위도다. */
    public record PointRequest(Double x, Double y) {
    }

    public record PlanRequest(PointRequest start, List<PointRequest> destinations) {
    }

    public record PointResponse(double x, double y) {
    }

    public record LegResponse(int destinationIndex,
                              double seconds,
                              double meters,
                              boolean reachable,
                              List<PointResponse> path) {
    }

    public record PlanResponse(int[] order,
                               double totalSeconds,
                               double totalMeters,
                               boolean exact,
                               List<LegResponse> legs) {
    }

    private final RoutePlanService routePlanService;

    public RoutePlanController(RoutePlanService routePlanService) {
        this.routePlanService = routePlanService;
    }

    @PostMapping("/rider/route/plan")
    public ResponseEntity<ApiResponse<PlanResponse>> plan(@RequestBody PlanRequest request) {
        if (request == null || request.destinations() == null || request.destinations().isEmpty()) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "배달지 좌표가 없습니다.");
        }

        List<GeoPoint> destinations = new ArrayList<>(request.destinations().size());
        for (PointRequest point : request.destinations()) {
            destinations.add(toGeoPoint(point));
        }

        RoutePlan plan = routePlanService.plan(toGeoPoint(request.start()), destinations);

        List<LegResponse> legs = new ArrayList<>(plan.legs().size());
        for (RoutePlan.Leg leg : plan.legs()) {
            List<PointResponse> path = new ArrayList<>(leg.polyline().size());
            for (GeoPoint point : leg.polyline()) {
                path.add(new PointResponse(point.lon(), point.lat()));
            }
            legs.add(new LegResponse(leg.destinationIndex(), leg.seconds(), leg.meters(), leg.reachable(), path));
        }

        return ResponseEntity.ok(ApiResponse.success(new PlanResponse(
                plan.order(), plan.totalSeconds(), plan.totalMeters(), plan.exact(), legs)));
    }

    private GeoPoint toGeoPoint(PointRequest point) {
        if (point == null || point.x() == null || point.y() == null) {
            return null;
        }
        return new GeoPoint(point.x(), point.y());
    }
}
