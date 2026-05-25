package com.icia.delivery.domain.board.service;

import com.icia.delivery.domain.board.dto.BoardDTO;
import com.icia.delivery.domain.board.entity.BoardEntity;
import com.icia.delivery.domain.board.repository.BoardRepository;
import com.icia.delivery.domain.comment.dto.CommentDTO;
import com.icia.delivery.domain.comment.entity.CommentEntity;
import com.icia.delivery.domain.comment.repository.CommentRepository;
import com.icia.delivery.global.exception.BusinessException;
import com.icia.delivery.global.exception.ErrorCode;
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

    @Transactional(readOnly = true)
    public Page<BoardDTO> getAllBoardsList(Pageable pageable) {
        Page<BoardEntity> boardEntities = boardRepository.findAll(pageable);
        return boardEntities.map(BoardDTO::toDTO);
    }

    @Transactional(readOnly = true)
    public Page<BoardDTO> searchBoards(String searchQuery, Pageable pageable) {
        Page<BoardEntity> boardEntities =
                boardRepository.findByBoardTitleContainingOrBoardContentContaining(searchQuery, searchQuery, pageable);
        return boardEntities.map(BoardDTO::toDTO);
    }

    @Transactional(readOnly = true)
    public BoardDTO getBoardById(Long id) {
        return boardRepository.findById(id)
                .map(BoardDTO::toDTO)
                .orElseThrow(() -> boardNotFound(id));
    }

    @Transactional
    public boolean updateBoardInfo(Long id, BoardDTO boardForm) {
        BoardEntity board = boardRepository.findById(id)
                .orElseThrow(() -> boardNotFound(id));

        if (boardForm.getBoardTitle() == null || boardForm.getBoardTitle().isEmpty()) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "Board title is required.");
        }

        board.setBoardTitle(boardForm.getBoardTitle());
        if (boardForm.getBoardContent() != null) {
            board.setBoardContent(boardForm.getBoardContent());
        }
        if (boardForm.getBoardUpdatedAt() != null) {
            board.setBoardUpdatedAt(boardForm.getBoardUpdatedAt());
        }
        if (boardForm.getBoardAnswerStatus() != null) {
            board.setBoardAnswerStatus(boardForm.getBoardAnswerStatus());
        }

        boardRepository.save(board);
        return true;
    }

    @Transactional
    public void deleteBoard(Long id) {
        if (!boardRepository.existsById(id)) {
            throw boardNotFound(id);
        }
        boardRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public BoardDTO getBoardDetail(Long id) {
        return boardRepository.findById(id)
                .map(BoardDTO::toDTO)
                .orElseThrow(() -> boardNotFound(id));
    }

    @Transactional
    public boolean addComment(Long id, CommentDTO commentDTO) {
        BoardEntity board = boardRepository.findById(id)
                .orElseThrow(() -> boardNotFound(id));

        if (commentRepository.existsByBoardId(id)) {
            throw new BusinessException(ErrorCode.CONFLICT, "A comment already exists for this board.");
        }

        Long adminId = (Long) session.getAttribute("admin_id");
        CommentEntity commentEntity = CommentEntity.toEntity(commentDTO);
        commentEntity.setBoardId(id);
        commentEntity.setAdminId(adminId);
        commentEntity.setCommentDate(LocalDateTime.now());
        commentRepository.save(commentEntity);

        board.setBoardAnswerStatus("답변완료");
        boardRepository.save(board);

        return true;
    }

    @Transactional(readOnly = true)
    public List<CommentDTO> getCommentsByBoardId(Long id) {
        List<CommentEntity> comments = commentRepository.findByBoardId(id);
        return comments.stream().map(CommentDTO::toDTO).collect(Collectors.toList());
    }

    private BusinessException boardNotFound(Long id) {
        return new BusinessException(ErrorCode.NOT_FOUND, "Board not found. boardId=" + id);
    }
}
