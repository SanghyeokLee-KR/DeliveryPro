package com.icia.delivery.controller.admin;

import com.icia.delivery.dto.admin.AdvertisementDTO;
import com.icia.delivery.service.admin.AdvertisementService;
import lombok.RequiredArgsConstructor;
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
    public List<AdvertisementDTO> getAllAds() {
        return advertisementService.getAllAdvertisements();
    }
}
