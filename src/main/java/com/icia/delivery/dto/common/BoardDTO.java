package com.icia.delivery.dto.common;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor // 기본 생성자
@AllArgsConstructor
public class BoardDTO {

    private Long boardId;
    private Long memId;
    private String boardTitle;
    private String boardContent;
    private String boardAnswerStatus;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDateTime boardCreatedAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDateTime boardUpdatedAt;


    public static BoardDTO toDTO(BoardEntity entity) {
        BoardDTO dto = new BoardDTO();

        dto.setBoardId(entity.getBoardId());
        dto.setMemId(entity.getMemId());
        dto.setBoardTitle(entity.getBoardTitle());
        dto.setBoardContent(entity.getBoardContent());
        dto.setBoardCreatedAt(entity.getBoardCreatedAt());
        dto.setBoardUpdatedAt(entity.getBoardUpdatedAt());
        dto.setBoardAnswerStatus(entity.getBoardAnswerStatus());
        return dto;
    }
}

