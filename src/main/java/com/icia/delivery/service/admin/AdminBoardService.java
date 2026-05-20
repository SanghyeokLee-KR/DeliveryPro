package com.icia.delivery.service.admin;

import com.icia.delivery.dao.common.BoardRepository;
import com.icia.delivery.dao.president.CommentRepository;
import com.icia.delivery.dto.common.BoardDTO;
import com.icia.delivery.dto.common.BoardEntity;
import com.icia.delivery.dto.president.CommentDTO;
import com.icia.delivery.dto.president.CommentEntity;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminBoardService {

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private CommentRepository commentRepository;


    @Autowired
    private HttpSession session;

    /**
     * 전체 게시글 목록을 페이징하여 조회하는 메서드
     *
     * @param pageable 페이징 정보
     * @return 페이징된 게시글 리스트 DTO
     */
    public Page<BoardDTO> getAllBoardsList(Pageable pageable) {
        Page<BoardEntity> boardEntities = boardRepository.findAll(pageable);
        return boardEntities.map(BoardDTO::toDTO);
    }

    /**
     * 검색어(제목 또는 내용)를 기반으로 게시글을 조회하는 메서드
     *
     * @param searchQuery 검색어
     * @param pageable    페이징 및 정렬 정보
     * @return 페이징된 검색된 게시글 리스트 DTO
     */
    public Page<BoardDTO> searchBoards(String searchQuery, Pageable pageable) {
        Page<BoardEntity> boardEntities = boardRepository.findByBoardTitleContainingOrBoardContentContaining(searchQuery, searchQuery, pageable);
        return boardEntities.map(BoardDTO::toDTO);
    }

    /**
     * 특정 ID를 가진 게시글을 조회하는 메서드
     *
     * @param id 게시글 ID
     * @return 게시글 DTO 또는 null
     */
    public BoardDTO getBoardById(Long id) {
        return boardRepository.findById(id)
                .map(BoardDTO::toDTO)
                .orElse(null);
    }

    /**
     * 게시글 정보를 업데이트하는 메서드
     *
     * @param id        게시글 ID
     * @param boardForm 수정된 게시글 정보가 담긴 BoardDTO
     * @return 업데이트 성공 여부
     */
    @Transactional
    public boolean updateBoardInfo(Long id, BoardDTO boardForm) {
        return boardRepository.findById(id).map(board -> {
            // 필수 필드 체크 (예: 제목이 비어있으면 예외 처리)
            if (boardForm.getBoardTitle() == null || boardForm.getBoardTitle().isEmpty()) {
                throw new IllegalArgumentException("게시글 제목은 필수입니다.");
            }

            // 수정 가능한 필드 업데이트 (값이 null인 경우 기존 값 유지)
            board.setBoardTitle(boardForm.getBoardTitle() != null ? boardForm.getBoardTitle() : board.getBoardTitle());
            board.setBoardContent(boardForm.getBoardContent() != null ? boardForm.getBoardContent() : board.getBoardContent());
            board.setBoardUpdatedAt(boardForm.getBoardUpdatedAt() != null ? boardForm.getBoardUpdatedAt() : board.getBoardUpdatedAt());
            board.setBoardAnswerStatus(boardForm.getBoardAnswerStatus() != null ? boardForm.getBoardAnswerStatus() : board.getBoardAnswerStatus());

            boardRepository.save(board);
            return true;
        }).orElse(false);
    }

    /**
     * 게시글 삭제 처리
     *
     * @param id 게시글 ID
     */
    @Transactional
    public void deleteBoard(Long id) {
        boardRepository.deleteById(id);
    }

    /**
     * 게시글 상세정보 조회
     */
    public BoardDTO getBoardDetail(Long id) {
        return boardRepository.findById(id)
                .map(BoardDTO::toDTO) // 엔티티를 DTO로 변환
                .orElse(null); // 게시글이 없으면 null 반환
    }

    public boolean addComment(Long id, CommentDTO commentDTO) {
        // 이미 댓글이 존재하는 경우 추가 불가
        if (commentRepository.existsByBoardId(id)) {
            return false;
        }

        Long adminId = (Long) session.getAttribute("admin_id");
        // 댓글 저장
        CommentEntity commentEntity = CommentEntity.toEntity(commentDTO);
        commentEntity.setBoardId(id);
        commentEntity.setAdminId(adminId);
        commentEntity.setCommentDate(LocalDateTime.now());
        commentRepository.save(commentEntity);

        // 댓글이 추가되면 게시글의 답변 상태를 "답변완료"로 변경
        boardRepository.findById(id).ifPresent(board -> {
            board.setBoardAnswerStatus("답변완료");
            boardRepository.save(board);
        });

        return true;
    }

    public List<CommentDTO> getCommentsByBoardId(Long id) {
        List<CommentEntity> comments = commentRepository.findByBoardId(id);
        return comments.stream().map(CommentDTO::toDTO).collect(Collectors.toList());
    }
}