package com.icia.delivery.dao.president;

import com.icia.delivery.dto.president.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<CommentEntity, Long> {


    @Query("SELECT c FROM CommentEntity c WHERE c.reviewId = :reviewId ")
    Optional<CommentEntity> findOwnerCommentByReviewId(@Param("reviewId") Long reviewId);

    boolean existsByBoardId(Long id);

    List<CommentEntity> findByBoardId(Long id);

    @Query("SELECT c FROM CommentEntity c WHERE c.boardId = :boardId ")
    Optional<CommentEntity> findTopByBoardId(Long boardId);
}
