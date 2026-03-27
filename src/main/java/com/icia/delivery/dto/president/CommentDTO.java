package com.icia.delivery.dto.president;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentDTO {

    private Long commentId;

    private Long preMemId;

    private Long reviewId;

    private Long adminId;

    private Long boardId;

    private String commentContents;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDateTime commentDate;


    public static CommentDTO toDTO(CommentEntity entity) {
        CommentDTO dto = new CommentDTO();
        dto.setCommentId(entity.getCommentId());
        dto.setPreMemId(entity.getPreMemId());
        dto.setAdminId(entity.getAdminId());
        dto.setReviewId(entity.getReviewId());
        dto.setBoardId(entity.getBoardId());
        dto.setCommentContents(entity.getCommentContents());
        dto.setCommentDate(entity.getCommentDate());
        return dto;
    }
}
