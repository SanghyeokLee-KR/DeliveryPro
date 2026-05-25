package com.icia.delivery.domain.board.controller;


import com.icia.delivery.domain.board.dto.BoardDTO;
import com.icia.delivery.domain.board.service.BoardService;
import com.icia.delivery.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class BoardController {

    private static final Logger log = LoggerFactory.getLogger(BoardController.class);

    private final BoardService bsvc;


    @PostMapping("/bWrite")
    public ModelAndView bWrite(@ModelAttribute BoardDTO board) {
        return bsvc.bWrite(board);

    }


    // bModify : 게시글 수정
    @PostMapping("/bModify")
    public ModelAndView bModify(@ModelAttribute BoardDTO board) {
        log.debug("Board modify requested. boardId={}", board.getBoardId());
        return bsvc.bModify(board);
    }

    // bDelete : 게시글 삭제
    @GetMapping("/bDelete")
    public ModelAndView bDelete(@ModelAttribute BoardDTO board) {
        log.debug("Board delete requested. boardId={}", board.getBoardId());
        return bsvc.bDelete(board);
    }

    @GetMapping("/qnaview/{boardId}")
    public ModelAndView bView(@PathVariable Long boardId) {
        log.debug("Board view requested. boardId={}", boardId);
        return bsvc.bView(boardId);
    }

    @RestController
    @RequiredArgsConstructor
    public class BoardRestController {


        @PostMapping("/boardList")
        public ResponseEntity<ApiResponse<List<Map<String, Object>>>> boardList() {
            return ResponseEntity.ok(ApiResponse.success(bsvc.boardList()));
        }

        @PostMapping("/searchList")
        public ResponseEntity<ApiResponse<List<BoardDTO>>> searchList(@RequestParam("searchCategory") String searchCategory,
                                                                      @RequestParam("searchKeyword") String searchKeyword) {
            return ResponseEntity.ok(ApiResponse.success(bsvc.searchList(searchCategory, searchKeyword)));
        }
        // bView : 게시글 상세보기

    }

}
