package com.icia.delivery.service.common;


import com.icia.delivery.dao.common.BoardRepository;
import com.icia.delivery.dto.common.BoardDTO;
import com.icia.delivery.dto.common.BoardEntity;
import com.icia.delivery.dto.member.MemberDTO;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository brepo;


    private final HttpSession session;

    public ModelAndView bWrite(BoardDTO board) {

        ModelAndView mav = new ModelAndView();

        Long memId = (Long) session.getAttribute("mem_id");

        BoardEntity entity = BoardEntity.toEntity(board);
        entity.setMemId(memId);
        entity.setBoardCreatedAt(LocalDateTime.now());
        entity.setBoardAnswerStatus("미답변");
        brepo.save(entity);
        mav.setViewName("redirect:/qnaForm");
        mav.addObject("board", board);

        return mav;
    }

    public List<Map<String, Object>> boardList() {
        // Repository에서 Object[] 배열 결과를 조회
        List<Object[]> rows = brepo.findBoardDTOList();

        // 각 결과 행을 Map으로 변환
        List<Map<String, Object>> list = new ArrayList<>();

        for (Object[] row : rows) {
            Map<String, Object> map = new HashMap<>();
            map.put("boardId", row[0]);              // BoardEntity의 boardId
            map.put("boardTitle", row[1]);           // BoardEntity의 boardTitle
            map.put("boardContent", row[2]);         // BoardEntity의 boardContent
            map.put("boardCreatedAt", row[3]);       // BoardEntity의 boardCreatedAt
            map.put("boardAnswerStatus", row[4]);    // BoardEntity의 boardAnswerStatus
            map.put("memId", row[5]);                // MemberEntity의 mId
            map.put("nickname", row[6]);             // MemberEntity의 nickname

            list.add(map);
        }

        return list;
    }

    public List<BoardDTO> searchList(String category, String keyword) {

        List<BoardEntity> entityList = new ArrayList<>();
        List<BoardDTO> dtoList = new ArrayList<>();

        if (category.equals("boardTitle")) {
            entityList = brepo.findByboardTitleContainingOrderByBoardIdDesc(keyword);
        } else if (category.equals("boardContent")) {
            entityList = brepo.findByboardContentContainingOrderByBoardIdDesc(keyword);
        }

        for (BoardEntity entity : entityList) {
            dtoList.add(BoardDTO.toDTO(entity));
        }

        return dtoList;
    }

    public ModelAndView bView(Long boardId) {
        ModelAndView mav = new ModelAndView();
        Optional<Object[]> resultOpt = brepo.findBoardDetails(boardId);

        if (resultOpt.isPresent()) {
            // result는 단 하나의 요소를 가지는 배열입니다.
            Object[] result = resultOpt.get();
            // 해당 요소가 실제 데이터를 담고 있는 중첩 배열입니다.
            Object[] nested = (Object[]) result[0];

            // 중첩 배열에서 각 값을 추출합니다.
            Long bId = (Long) nested[0];
            String boardTitle = (String) nested[1];
            String boardContent = (String) nested[2];
            LocalDateTime boardCreatedAt = (LocalDateTime) nested[3];
            String boardAnswerStatus = (String) nested[4];
            Long memId = (Long) nested[5];
            String nickname = (String) nested[6];
            Map<String, Object> view = new HashMap<>();
            view.put("boardId", bId);
            view.put("boardTitle", boardTitle);
            view.put("boardContent", boardContent);
            view.put("boardCreatedAt", boardCreatedAt);
            view.put("boardAnswerStatus", boardAnswerStatus);
            view.put("memId", memId);
            view.put("nickname", nickname);
            mav.addObject("view", view);
        }

        mav.setViewName("common/qnaview");
        return mav;
    }

    public ModelAndView bModify(BoardDTO board) {
        System.out.println("[2] controller → service : " + board);

        ModelAndView mav = new ModelAndView();

        BoardEntity entity = BoardEntity.toEntity(board);

        brepo.save(entity);
        mav.setViewName("redirect:/qnaview/"+board.getBoardId());

        return mav;
    }

    public ModelAndView bDelete(BoardDTO board) {
        ModelAndView mav = new ModelAndView();

        brepo.deleteById(board.getBoardId());
        mav.setViewName("redirect:/qnaForm");

        return mav;
    }
}