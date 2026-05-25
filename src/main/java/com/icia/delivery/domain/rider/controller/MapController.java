package com.icia.delivery.domain.rider.controller;

import com.icia.delivery.global.exception.BusinessException;
import com.icia.delivery.global.exception.ErrorCode;
import com.icia.delivery.global.response.ApiResponse;
import com.icia.delivery.util.KakaoApiUtil;
import com.icia.delivery.util.KakaoApiUtil.Point;
import com.icia.delivery.util.KakaoApiUtil.RouteResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
public class MapController {

    private static final Logger log = LoggerFactory.getLogger(MapController.class);

    private final KakaoApiUtil kakaoApiUtil;

    @Autowired
    public MapController(KakaoApiUtil kakaoApiUtil) {
        this.kakaoApiUtil = kakaoApiUtil;
    }

    @PostMapping("/pointAjax")
    public ResponseEntity<ApiResponse<KakaoApiUtil.RouteResult>> getSingleRoute(
            @RequestParam(required = false) Double fromX,
            @RequestParam(required = false) Double fromY,
            @RequestParam(required = false) Double toX,
            @RequestParam(required = false) Double toY
    ) {
        validateRoutePoints(fromX, fromY, toX, toY);

        try {
            RouteResult routeResult = kakaoApiUtil.getRouteResult(fromX, fromY, toX, toY);
            return ResponseEntity.ok(ApiResponse.success(routeResult));
        } catch (IOException | InterruptedException e) {
            throw createRouteException(e);
        }
    }

    @PostMapping("/multiPointAjax")
    public ResponseEntity<ApiResponse<KakaoApiUtil.RouteResult>> getMultiRoute(
            @RequestParam(required = false) Double fromX,
            @RequestParam(required = false) Double fromY,
            @RequestParam(required = false) Double toX,
            @RequestParam(required = false) Double toY,
            @RequestParam(required = false) String waypoints
    ) {
        validateRoutePoints(fromX, fromY, toX, toY);

        List<Point> waypointList = parseWaypoints(waypoints);

        try {
            RouteResult routeResult = kakaoApiUtil.getRouteResultWithWaypoints(fromX, fromY, toX, toY, waypointList);
            return ResponseEntity.ok(ApiResponse.success(routeResult));
        } catch (IOException | InterruptedException e) {
            throw createRouteException(e);
        }
    }

    private void validateRoutePoints(Double fromX, Double fromY, Double toX, Double toY) {
        if (fromX == null || fromY == null || toX == null || toY == null) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "경로 조회에 필요한 좌표값이 누락되었습니다.");
        }
    }

    private List<Point> parseWaypoints(String waypoints) {
        List<Point> waypointList = new ArrayList<>();
        if (waypoints == null || waypoints.trim().isEmpty()) {
            return waypointList;
        }

        String[] waypointValues = waypoints.split("\\|");
        for (String waypoint : waypointValues) {
            String[] xy = waypoint.split(",");
            if (xy.length != 2) {
                throw new BusinessException(ErrorCode.BAD_REQUEST, "경유지 좌표 형식이 올바르지 않습니다.");
            }

            try {
                double x = Double.parseDouble(xy[0]);
                double y = Double.parseDouble(xy[1]);
                waypointList.add(new Point(x, y));
            } catch (NumberFormatException e) {
                throw new BusinessException(ErrorCode.BAD_REQUEST, "경유지 좌표 형식이 올바르지 않습니다.", e);
            }
        }
        return waypointList;
    }

    private BusinessException createRouteException(Exception e) {
        if (e instanceof InterruptedException) {
            Thread.currentThread().interrupt();
        }
        log.error("Failed to get Kakao route result", e);
        return new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "카카오 경로 조회 중 오류가 발생했습니다.", e);
    }
}
