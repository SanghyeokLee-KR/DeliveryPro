package com.icia.delivery.controller.president;


import com.icia.delivery.dto.president.CommentDTO;
import com.icia.delivery.service.president.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService csvc;

    @PostMapping("/cWrite")
    public ResponseEntity<Optional<CommentDTO>> cWrite(@RequestBody CommentDTO comment) {
        System.out.println("\n댓글 작성\n[1]html → controller : " + comment);
        Optional<CommentDTO> dtoList = csvc.cWrite(comment);
        return ResponseEntity.ok(dtoList);
    }

    // cDelete
    @PostMapping("/cDelete")
    public ResponseEntity<Optional<CommentDTO>> cDelete(@RequestBody CommentDTO comment) {
        System.out.println("\n댓글 삭제\n[1]html → cㅌontroller : " + comment);
        Optional<CommentDTO> commentList = csvc.cDelete(comment);
        return ResponseEntity.ok(commentList);
    }

    @PostMapping("/cModify")
    public ResponseEntity<Optional<CommentDTO>> cModify(@RequestBody CommentDTO comment) {
        System.out.println("\n댓글 수정\n[1]html → controller : " + comment);
        Optional<CommentDTO> commentList = csvc.cModify(comment);
        return ResponseEntity.ok(commentList);
    }


    @GetMapping("/cList")
    public ResponseEntity<Optional<CommentDTO>> cList(@RequestParam("reviewId") Long reviewId) {
        System.out.println("\n댓글 목록\n[1]html → controller : " + reviewId);
        Optional<CommentDTO> commentLists = csvc.getSingleOwnerComment(reviewId);
        return ResponseEntity.ok(commentLists);
    }
    @PostMapping("/{boardId}")
    public Optional<CommentDTO> singleComment(@PathVariable Long boardId) {
        return csvc.singleComment(boardId);
    }


}


