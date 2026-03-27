package com.icia.delivery.dao.member;

import com.icia.delivery.dto.member.ReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface reviewRepository extends JpaRepository<ReviewEntity, Long> {
    List<ReviewEntity> findByOrderId(Long orderId);





    @Query("SELECT r.reviewId, r.reviewContent, r.reviewCreatedAt, r.reviewImage, r.reviewRating,r.menuId, " +
            "s.preStoName, pm.menuName, m.nickname, r.orderId " +
            "FROM ReviewEntity r, PreStoreMenuEntity pm, PreStoreEntity s, MemberEntity m " +
            "WHERE r.memId = :memId " +
            "AND pm.menuId = r.menuId " +
            "AND s.preStoId = pm.preStoId " +
            "AND m.mId = r.memId")
    List<Object[]> getReviewsByMemberId(@Param("memId") Long memId);


    @Query("SELECT r.reviewId, r.reviewContent, r.reviewCreatedAt, r.reviewImage, r.reviewRating, " +
            "s.preStoName, pm.menuName, m.nickname, r.orderId " +
            "FROM ReviewEntity r, PreStoreMenuEntity pm, PreStoreEntity s, MemberEntity m " +
            "WHERE r.preStoId = :preStoId " +
            "AND pm.menuId = r.menuId " +
            "AND s.preStoId = r.preStoId " +
            "AND m.mId = r.memId")
    List<Object[]> getReviewsByPreStoId(Long preStoId);



    List<ReviewEntity> findByPreStoId(Long storeId);

    List<ReviewEntity> findByPreStoIdAndReviewCreatedAtBetween(Long preStoId, LocalDateTime startDateTime, LocalDateTime endDateTime);
}