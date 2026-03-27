package com.icia.delivery.dao.common;


import com.icia.delivery.dto.common.BoardDTO;
import com.icia.delivery.dto.common.BoardEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BoardRepository extends JpaRepository<BoardEntity, Long> {


    List<BoardEntity> findByboardContentContainingOrderByBoardIdDesc(String keyword);


    List<BoardEntity> findByboardTitleContainingOrderByBoardIdDesc(String keyword);


    @Query(value = "SELECT * FROM boards ORDER BY board_id ASC", nativeQuery = true)
    List<BoardEntity> findAllOrderByboardIdASC();

    Page<BoardEntity> findByBoardTitleContainingOrBoardContentContaining(String searchQuery, String searchQuery1, Pageable pageable);

    @Query("SELECT count(b) FROM BoardEntity b WHERE b.boardAnswerStatus = '미답변'")
    int findByUnansweredCount();

    @Query("SELECT b.boardId, b.boardTitle, b.boardContent, b.boardCreatedAt, b.boardAnswerStatus, m.mId, m.nickname " +
            "FROM BoardEntity b JOIN MemberEntity m ON b.memId = m.mId " +
            "WHERE b.boardId = :boardId")
    Optional<Object[]> findBoardDetails(@Param("boardId") Long boardId);

    @Query("SELECT b.boardId, b.boardTitle, b.boardContent, b.boardCreatedAt, b.boardAnswerStatus, m.mId, m.nickname " +
            "FROM BoardEntity b JOIN MemberEntity m ON b.memId = m.mId " +
            "ORDER BY b.boardId ASC")
    List<Object[]> findBoardDTOList();
}
