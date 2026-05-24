package com.icia.delivery.domain.rider.controller;

import com.icia.delivery.domain.rider.dto.RiderAccountDTO;
import com.icia.delivery.domain.rider.service.RiderService;
import com.icia.delivery.global.response.ApiResponse;
import com.icia.delivery.util.DistanceUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class RiderRestfulController {

    private final RiderService rsvc;

    // idCheck : 아이디 중복 체크
    @PostMapping("/riderIdCheck")
    public ResponseEntity<ApiResponse<String>> idCheck(@RequestParam("rId") String rId) {
        String result = rsvc.riderIdCheck(rId);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PostMapping("/getRiderAccountList")
    public ResponseEntity<ApiResponse<List<RiderAccountDTO>>> getRiderAccountList(){
        return ResponseEntity.ok(ApiResponse.success(rsvc.getRiderAccountList()));
    }

    @PostMapping("/riderAccountDelete")
    public ResponseEntity<ApiResponse<String>> riderAccountDelete(@RequestParam("accountId") Long accountId){
        System.out.println("들어오는 계좌 고유 번호 : " + accountId);
        return ResponseEntity.ok(ApiResponse.success(rsvc.deleteAccount(accountId)));
    }

    @PostMapping("/calcDistance")
    public ResponseEntity<ApiResponse<Map<String, Object>>> calculateDistance(
            @RequestParam("fromX") double fromX,
            @RequestParam("fromY") double fromY,
            @RequestParam("toX") double toX,
            @RequestParam("toY") double toY) {

        // 주의: DistanceUtil.calculateDistance는 위도/경도의 순서에 따라 계산하므로,
        // 여기서는 fromY, fromX, toY, toX 순으로 전달합니다.
        double distance = DistanceUtil.calculateDistance(fromY, fromX, toY, toX);
        long roundedDistance = Math.round(distance);

        Map<String, Object> result = new HashMap<>();
        result.put("distance", roundedDistance); // 미터 단위
        return ResponseEntity.ok(ApiResponse.success(result));
    }
}
