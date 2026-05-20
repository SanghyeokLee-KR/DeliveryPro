package com.icia.delivery.dto.common;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@Entity
@NoArgsConstructor // 기본 생성자
@AllArgsConstructor
@Builder
@Table(name = "boards")
public class BoardEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id")
    private Long boardId;
    @Column(name = "mem_id", nullable = false)
    private Long memId;
    @Column(name = "board_title")
    private String boardTitle;
    @Column(name = "board_content")
    private String boardContent;


    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd")
    @Column(name = "board_createdAt",columnDefinition = "TIMESTAMP DEFAULT TRUNC(SYSDATE)")
    private LocalDateTime boardCreatedAt;



    @Column(name = "board_updatedAt")
    private LocalDateTime boardUpdatedAt;

    @Column(name ="board_answer_status")
    private String boardAnswerStatus;

    public static BoardEntity toEntity(BoardDTO dto) {

        BoardEntity entity = new BoardEntity();

        entity.setBoardId(dto.getBoardId());
        entity.setMemId(dto.getMemId());
        entity.setBoardTitle(dto.getBoardTitle());
        entity.setBoardContent(dto.getBoardContent());
        entity.setBoardCreatedAt(dto.getBoardCreatedAt());
        entity.setBoardUpdatedAt(dto.getBoardUpdatedAt());
        entity.setBoardAnswerStatus(dto.getBoardAnswerStatus());
        return entity;
    }
}
