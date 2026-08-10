package com.icia.delivery.domain.review.controller;

import com.icia.delivery.domain.review.dto.ReviewDTO;
import com.icia.delivery.domain.review.service.ReviewService;
import com.icia.delivery.global.exception.BusinessException;
import com.icia.delivery.global.exception.ErrorCode;
import com.icia.delivery.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService rsvc;

    @PostMapping("/review")
    public ModelAndView createReview(@ModelAttribute ReviewDTO reviewDTO) {
        return rsvc.saveReview(reviewDTO);
    }

    // 비정적 내부 클래스는 컴포넌트 스캔 대상이 아니라 매핑이 등록되지 않았다.
    // 바깥 클래스로 옮기면서 내부 클래스가 갖고 있던 api/reviews 접두사를 각 경로에 직접 붙인다.
    @PostMapping("/api/reviews/{preStoId}")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getReviewsByPreStoId(@PathVariable Long preStoId) {
        return ResponseEntity.ok(ApiResponse.success(rsvc.getReviewsByPreStoId(preStoId)));
    }

    @PostMapping("/api/reviews/member/{memId}")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getReviewsByMemberId(@PathVariable Long memId) {
        return ResponseEntity.ok(ApiResponse.success(rsvc.getReviewsByMemberId(memId)));
    }

    @PostMapping("/api/reviews/updateReviewCount/{storeId}")
    public ResponseEntity<ApiResponse<String>> updateReviewCount(
            @PathVariable Long storeId,
            @RequestParam int reviewCount) {
        try {
            rsvc.updateReviewCount(storeId, reviewCount);
            return ResponseEntity.ok(ApiResponse.success("Review count updated."));
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR, "Failed to update review count.", e);
        }
    }
}
