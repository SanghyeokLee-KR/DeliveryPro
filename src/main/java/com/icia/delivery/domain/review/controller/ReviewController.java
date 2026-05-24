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

    @RestController
    @RequestMapping("api/reviews")
    public class ReviewRestController {


        @PostMapping("/{preStoId}")
        public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getReviewsByPreStoId(@PathVariable Long preStoId) {
            return ResponseEntity.ok(ApiResponse.success(rsvc.getReviewsByPreStoId(preStoId)));
        }


        @PostMapping("member/{memId}")
        public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getReviewsByMemberId(@PathVariable Long memId) {
            return ResponseEntity.ok(ApiResponse.success(rsvc.getReviewsByMemberId(memId)));

        }



        @PostMapping("/updateReviewCount/{storeId}")
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
}
