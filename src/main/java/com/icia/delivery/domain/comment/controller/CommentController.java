package com.icia.delivery.domain.comment.controller;


import com.icia.delivery.domain.comment.dto.CommentDTO;
import com.icia.delivery.domain.comment.service.CommentService;
import com.icia.delivery.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private static final Logger log = LoggerFactory.getLogger(CommentController.class);

    private final CommentService csvc;

    @PostMapping("/cWrite")
    public ResponseEntity<ApiResponse<Optional<CommentDTO>>> cWrite(@RequestBody CommentDTO comment) {
        log.debug("Comment write requested. reviewId={}", comment.getReviewId());
        Optional<CommentDTO> dtoList = csvc.cWrite(comment);
        return ResponseEntity.ok(ApiResponse.success(dtoList));
    }

    // cDelete
    @PostMapping("/cDelete")
    public ResponseEntity<ApiResponse<Optional<CommentDTO>>> cDelete(@RequestBody CommentDTO comment) {
        log.debug("Comment delete requested. commentId={}", comment.getCommentId());
        Optional<CommentDTO> commentList = csvc.cDelete(comment);
        return ResponseEntity.ok(ApiResponse.success(commentList));
    }

    @PostMapping("/cModify")
    public ResponseEntity<ApiResponse<Optional<CommentDTO>>> cModify(@RequestBody CommentDTO comment) {
        log.debug("Comment modify requested. commentId={}", comment.getCommentId());
        Optional<CommentDTO> commentList = csvc.cModify(comment);
        return ResponseEntity.ok(ApiResponse.success(commentList));
    }


    @GetMapping("/cList")
    public ResponseEntity<ApiResponse<Optional<CommentDTO>>> cList(@RequestParam("reviewId") Long reviewId) {
        log.debug("Comment list requested. reviewId={}", reviewId);
        Optional<CommentDTO> commentLists = csvc.getSingleOwnerComment(reviewId);
        return ResponseEntity.ok(ApiResponse.success(commentLists));
    }
    @PostMapping("/{boardId}")
    public ResponseEntity<ApiResponse<Optional<CommentDTO>>> singleComment(@PathVariable Long boardId) {
        return ResponseEntity.ok(ApiResponse.success(csvc.singleComment(boardId)));
    }


}


