package com.icia.delivery.domain.admin.controller;

import com.icia.delivery.domain.admin.dto.AdvertisementDTO;
import com.icia.delivery.domain.admin.service.AdvertisementService;
import com.icia.delivery.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 광고 목록을 JSON으로 반환하는 API 컨트롤러
 */
@RestController
@RequestMapping("/api/advertisements")
@RequiredArgsConstructor
public class AdvertisementApiController {

    private final AdvertisementService advertisementService;

    /**
     * GET /api/advertisements
     * → JSON 배열: [{advId:..., advTitle:..., advImageUrl:...}, ...]
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<AdvertisementDTO>>> getAllAds() {
        return ResponseEntity.ok(ApiResponse.success(advertisementService.getAllAdvertisements()));
    }
}
