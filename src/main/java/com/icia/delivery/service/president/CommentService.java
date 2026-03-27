package com.icia.delivery.service.president;


import com.icia.delivery.dao.president.CommentRepository;
import com.icia.delivery.dto.president.CommentDTO;
import com.icia.delivery.dto.president.CommentEntity;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentService {


    private final CommentRepository crepo;
    private final HttpSession session;


    public Optional<CommentDTO> cWrite(CommentDTO comment) {
        Long preMemId = (Long) session.getAttribute("preMem_id");


        // 댓글 입력
        CommentEntity entity = CommentEntity.toEntity(comment);
        entity.setPreMemId(preMemId);
        entity.setCommentDate(LocalDateTime.now());
        entity.setCommentContents(comment.getCommentContents());
        entity.setReviewId(comment.getReviewId());
        crepo.save(entity);
        // 댓글 입력 후 목록 불러오기
        Optional<CommentDTO> dtoList = getSingleOwnerComment(comment.getReviewId());
        return dtoList;

    }

    @Transactional
    public Optional<CommentDTO> cModify(CommentDTO comment) {

        // 기존 댓글 검색
        Optional<CommentEntity> existingComment = crepo.findById(comment.getCommentId());

        if (existingComment.isPresent()) {
            // 기존 댓글 수정
            CommentEntity updatedComment = existingComment.get();
            updatedComment.setCommentContents(comment.getCommentContents());
            updatedComment.setCommentDate(LocalDateTime.now());

            // 변경된 댓글 저장
            crepo.save(updatedComment);
        } else {
            throw new EntityNotFoundException("수정하려는 댓글이 존재하지 않습니다.");
        }

        // 수정 후 목록 반환
        Optional<CommentDTO> dtoList = getSingleOwnerComment(comment.getReviewId());
        return dtoList;
    }

    public Optional<CommentDTO> cDelete(CommentDTO comment) {

        // 댓글 삭제
        crepo.deleteById(comment.getCommentId());

        // 댓글 입력 후 목록 불러오기
        Optional<CommentDTO> dtoList = getSingleOwnerComment(comment.getReviewId());
        return dtoList;

    }

    public Optional<CommentDTO> getSingleOwnerComment(Long reviewId) {
        return crepo.findOwnerCommentByReviewId(reviewId)
                .map(CommentDTO::toDTO);
    }

    public Optional<CommentDTO> singleComment(Long boardId) {
        return crepo.findTopByBoardId(boardId)
                .map(CommentDTO::toDTO);  // Entity → DTO 변환
    }
}
