package com.icia.delivery.controller.common;


import com.icia.delivery.dto.common.BoardDTO;
import com.icia.delivery.service.common.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class BoardController {

    private final BoardService bsvc;


    @PostMapping("/bWrite")
    public ModelAndView bWrite(@ModelAttribute BoardDTO board) {
        return bsvc.bWrite(board);

    }


    // bModify : 게시글 수정
    @PostMapping("/bModify")
    public ModelAndView bModify(@ModelAttribute BoardDTO board) {
        System.out.println("\n게시글 수정 메소드\n[1]html → controller : " + board);
        return bsvc.bModify(board);
    }

    // bDelete : 게시글 삭제
    @GetMapping("/bDelete")
    public ModelAndView bDelete(@ModelAttribute BoardDTO board) {
        System.out.println("\n게시글 삭제 메소드\n[1]html → controller : " + board);
        return bsvc.bDelete(board);
    }

    @GetMapping("/qnaview/{boardId}")
    public ModelAndView bView(@PathVariable Long boardId) {
        System.out.println("\n게시글 작성 메소드\n[1]html → controller : " + boardId);
        return bsvc.bView(boardId);
    }

    @RestController
    @RequiredArgsConstructor
    public class BoardRestController {


        @PostMapping("/boardList")
        public List<Map<String, Object>> boardList() {
            return bsvc.boardList();
        }

        @PostMapping("/searchList")
        public List<BoardDTO> searchList(@RequestParam("searchCategory") String searchCategory,
                                         @RequestParam("searchKeyword") String searchKeyword) {
            return bsvc.searchList(searchCategory, searchKeyword);
        }
        // bView : 게시글 상세보기

    }

}