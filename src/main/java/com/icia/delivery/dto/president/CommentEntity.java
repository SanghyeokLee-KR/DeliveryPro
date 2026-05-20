package com.icia.delivery.dto.president;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "comments")
@NoArgsConstructor
@AllArgsConstructor
public class CommentEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long commentId;

    @Column(name = "pre_mem_id", nullable = false)
    private Long preMemId;

    @Column(name = "review_id", nullable = false)
    private Long reviewId;

    @Column(name = "board_id", nullable = false)
    private Long boardId;

    @Column(name = "admin_id", nullable = false)
    private Long adminId;


    @Column(name = "comment_contents", length = 1000)
    private String commentContents;


    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Column(name = "comment_date", nullable = false, columnDefinition = "DATE DEFAULT SYSDATE")
    private LocalDateTime commentDate;


    public static CommentEntity toEntity(CommentDTO dto) {
        CommentEntity entity = new CommentEntity();
        entity.setCommentId(dto.getCommentId());
        entity.setPreMemId(dto.getPreMemId());
        entity.setReviewId(dto.getReviewId());
        entity.setAdminId(dto.getAdminId());
        entity.setBoardId(dto.getBoardId());
        entity.setCommentContents(dto.getCommentContents());
        entity.setCommentDate(dto.getCommentDate());
        return entity;
    }
}
