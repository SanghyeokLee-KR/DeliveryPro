package com.icia.delivery.service.member;


import com.icia.delivery.dao.member.reviewRepository;
import com.icia.delivery.dao.president.StoreRepository;
import com.icia.delivery.dto.member.ReviewDTO;
import com.icia.delivery.dto.member.ReviewEntity;
import com.icia.delivery.dto.president.PreStoreEntity;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    Path path = Paths.get(System.getProperty("user.dir"), "src/main/resources/static/review-img");

    private final reviewRepository rvrepo;
    private final HttpSession session;
    private final StoreRepository srepo;

    public List<ReviewDTO> getReviewsByOrderId(Long orderId) {
        List<ReviewEntity> entities = rvrepo.findByOrderId(orderId);
        return entities.stream()
                .map(ReviewDTO::toDTO) // Entity → DTO 변환
                .collect(Collectors.toList());
    }


    public ModelAndView saveReview(ReviewDTO reviewDTO) {

        ModelAndView mav = new ModelAndView();


        try {
            MultipartFile ReviewPicture = reviewDTO.getReviewPicture();
            String savePath = "";

            if (!ReviewPicture.isEmpty()) {
                String filename = ReviewPicture.getOriginalFilename();
                String newFileName = savePath + "_" + filename;

                reviewDTO.setReviewImage(newFileName);

                savePath = path + "\\" + newFileName;
            } else {
                reviewDTO.setReviewImage("default.jpg");
            }


            ReviewPicture.transferTo(new File(savePath));


            Long memId = (Long) session.getAttribute("mem_id");
            Long menuId = (Long) session.getAttribute("menuId");

            reviewDTO.setPreStoId(reviewDTO.getPreStoId());
            reviewDTO.setMemId(memId);
            reviewDTO.setOrderId(reviewDTO.getOrderId());
            reviewDTO.setMenuId(menuId);
            reviewDTO.setReviewRating(reviewDTO.getReviewRating());
            reviewDTO.setReviewContent(reviewDTO.getReviewContent());
            reviewDTO.setReviewCreatedAt(LocalDateTime.now());
            reviewDTO.setReviewUpdateAt(reviewDTO.getReviewUpdateAt());


            ReviewEntity reviewEntity = rvrepo.save(ReviewEntity.toEntity(reviewDTO));
            mav.setViewName("redirect:/customer");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return mav;
    }
    @Transactional
    public List<Map<String, Object>> getReviewsByMemberId(Long memId) {
        List<Object[]> results = rvrepo.getReviewsByMemberId(memId);
        List<Map<String, Object>> reviews = new ArrayList<>();

        for (Object[] row : results) {
            Map<String, Object> review = new HashMap<>();
            review.put("reviewId", row[0]);
            review.put("reviewContent", row[1]);
            review.put("reviewCreatedAt", row[2]);
            review.put("reviewImage", row[3]);
            review.put("reviewRating", row[4]);
            review.put("preStoName", row[5]);
            review.put("menuName", row[6]);
            review.put("nickname", row[7]);
            review.put("orderId", row[8]);
            review.put("menuId", row[9]);
            reviews.add(review);
        }
        return reviews;
    }

    @Transactional
    public List<Map<String, Object>> getReviewsByPreStoId(Long preStoId) {
        List<Object[]> results = rvrepo.getReviewsByPreStoId(preStoId);
        List<Map<String, Object>> reviewsList = new ArrayList<>();

        for (Object[] row : results) {
            Map<String, Object> reviewList = new HashMap<>();
            reviewList.put("reviewId", row[0]);
            reviewList.put("reviewContent", row[1]);
            reviewList.put("reviewCreatedAt", row[2]);
            reviewList.put("reviewImage", row[3]);
            reviewList.put("reviewRating", row[4]);
            reviewList.put("preStoName", row[5]);
            reviewList.put("menuName", row[6]);
            reviewList.put("nickname", row[7]);
            reviewList.put("orderId", row[8]);
            reviewsList.add(reviewList);
        }
        return reviewsList;
    }



    @Transactional
    public void updateReviewCount(Long storeId, int reviewCount) {
        // 필요한 비즈니스 로직이 있다면 추가
        srepo.updatePreStoreCount(storeId, reviewCount);
    }
    public Float reviewStarCount(Long storeId) {
        // 결과를 저장할 변수
        float totalRating = 0;  // totalRating을 float로 선언
        int totalReviews = 0;

        // 리뷰 목록을 가져온다.
        List<ReviewEntity> entityList = rvrepo.findByPreStoId(storeId);
        System.out.println("가게 리뷰 정보(e) : " + entityList);

        // 각 리뷰의 별점 값을 더하고 리뷰의 개수를 셈
        for (ReviewEntity entity : entityList) {
            totalRating += entity.getReviewRating();  // 별점을 더함
            totalReviews++;  // 리뷰 수 증가
        }

        // 평균 별점 계산
        Float averageRating = (totalReviews > 0) ? (totalRating / totalReviews) : 0.0f;  // 평균 계산 (0.0f로 수정)

        // 평균 별점을 가게 테이블에 업데이트
        Optional<PreStoreEntity> entityOp = srepo.findById(storeId);

        PreStoreEntity entity = entityOp.get();

        entity.setPreStoRating(averageRating);  // 평균 별점 저장

        srepo.save(entity);

        return averageRating;  // 계산된 평균 별점 반환
    }
}
