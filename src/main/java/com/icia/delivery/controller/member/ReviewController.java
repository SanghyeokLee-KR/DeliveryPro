package com.icia.delivery.controller.member;

import com.icia.delivery.dto.member.ReviewDTO;
import com.icia.delivery.service.member.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
        public List<Map<String, Object>> getReviewsByPreStoId(@PathVariable Long preStoId) {
            return rsvc.getReviewsByPreStoId(preStoId);
        }


        @PostMapping("member/{memId}")
        public List<Map<String, Object>> getReviewsByMemberId(@PathVariable Long memId) {
            return rsvc.getReviewsByMemberId(memId);

        }



        @PostMapping("/updateReviewCount/{storeId}")
        public ResponseEntity<String> updateReviewCount(
                @PathVariable Long storeId,
                @RequestParam int reviewCount) {
            try {
                rsvc.updateReviewCount(storeId, reviewCount);
                return ResponseEntity.ok("리뷰 개수가 성공적으로 업데이트되었습니다.");
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("리뷰 개수 업데이트 중 오류 발생");
            }
        }
    }
}