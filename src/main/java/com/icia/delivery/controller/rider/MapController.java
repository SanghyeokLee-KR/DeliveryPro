// src/main/java/com/icia/delivery/controller/rider/MapController.java

package com.icia.delivery.controller.rider;

import com.icia.delivery.util.KakaoApiUtil;
import com.icia.delivery.util.KakaoApiUtil.Point;
import com.icia.delivery.util.KakaoApiUtil.RouteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
public class MapController {

    private final KakaoApiUtil kakaoApiUtil;

    @Autowired
    public MapController(KakaoApiUtil kakaoApiUtil) {
        this.kakaoApiUtil = kakaoApiUtil;
    }

    /**
     * 단일 구간(from→to) → RouteResult(주소 간 거리 목록)
     */
    @PostMapping("/pointAjax")
    @ResponseBody
    public ResponseEntity<KakaoApiUtil.RouteResult> getSingleRoute(
            @RequestParam(required = false) Double fromX,
            @RequestParam(required = false) Double fromY,
            @RequestParam(required = false) Double toX,
            @RequestParam(required = false) Double toY
    ) {
        if(fromX == null || fromY == null || toX == null || toY == null){
            return ResponseEntity.badRequest().body(null);
        }
        try{
            RouteResult rr = kakaoApiUtil.getRouteResult(fromX, fromY, toX, toY);
            return ResponseEntity.ok(rr);
        } catch(IOException | InterruptedException e){
            // 로깅 추가 (예: 로그 파일에 에러 기록)
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * 다중 구간(경유지 포함) → RouteResult(주소 간 거리 목록)
     */
    @PostMapping("/multiPointAjax")
    @ResponseBody
    public ResponseEntity<KakaoApiUtil.RouteResult> getMultiRoute(
            @RequestParam(required = false) Double fromX,
            @RequestParam(required = false) Double fromY,
            @RequestParam(required = false) Double toX,
            @RequestParam(required = false) Double toY,
            @RequestParam(required = false) String waypoints
    ) {
        if(fromX == null || fromY == null || toX == null || toY == null){
            return ResponseEntity.badRequest().body(null);
        }

        List<Point> list = new ArrayList<>();
        if(waypoints != null && !waypoints.trim().isEmpty()){
            String[] arr = waypoints.split("\\|");
            for(String w : arr){
                String[] xy = w.split(",");
                if(xy.length == 2){
                    try{
                        double wx = Double.parseDouble(xy[0]);
                        double wy = Double.parseDouble(xy[1]);
                        list.add(new Point(wx, wy));
                    } catch(NumberFormatException e){
                        // 잘못된 좌표 형식 처리
                        e.printStackTrace();
                        return ResponseEntity.badRequest().body(null);
                    }
                }
            }
        }

        try{
            RouteResult rr = kakaoApiUtil.getRouteResultWithWaypoints(fromX, fromY, toX, toY, list);
            return ResponseEntity.ok(rr);
        } catch(IOException | InterruptedException e){
            // 로깅 추가
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
