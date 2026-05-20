package com.icia.delivery.controller.admin;

import com.icia.delivery.dto.common.BoardDTO;
import com.icia.delivery.dto.president.CommentDTO;
import com.icia.delivery.service.admin.AdminBoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller("boardsListController") // 고유한 빈 이름 지정
@RequestMapping("/admin/boardsList")
public class BoardListController {

    @Autowired
    private AdminBoardService adminboardService;


    /**
     * 게시판 리스트 페이지를 표시하는 메서드
     *
     * @param page        페이지 번호 (기본값: 0)
     * @param size        페이지당 크기 (기본값: 10)
     * @param searchQuery 검색어 (제목 또는 내용)
     * @param sortField   정렬 필드 (기본값: boardId)
     * @param sortDir     정렬 방향 (asc 또는 desc, 기본값: asc)
     * @param model       Spring의 Model 객체
     * @return admin.html 템플릿
     */
    @GetMapping
    public String viewBoardList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String searchQuery,
            @RequestParam(defaultValue = "boardId") String sortField,
            @RequestParam(defaultValue = "desc") String sortDir,
            Model model) {

        // 정렬 방향 설정
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();

        // 페이징 객체 생성
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<BoardDTO> boardsPage;

        // 검색 조건이 있는 경우 필터링
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            boardsPage = adminboardService.searchBoards(searchQuery, pageable);
        } else {
            boardsPage = adminboardService.getAllBoardsList(pageable);
        }

        // 모델에 데이터 추가
        model.addAttribute("boardsList", boardsPage.getContent()); // 현재 페이지 게시글 데이터
        model.addAttribute("boardsPage", boardsPage); // 페이징 처리된 게시글 데이터
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", boardsPage.getTotalPages());
        model.addAttribute("totalElements", boardsPage.getTotalElements());
        model.addAttribute("searchQuery", searchQuery);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");

        // content 속성 추가 (Thymeleaf에서 사용)
        model.addAttribute("content", "boardList");

        return "admin/admin"; // admin.html 템플릿 렌더링
    }

    /**
     * 게시글 편집 페이지를 표시하는 메서드
     *
     * @param id    게시글 ID
     * @param model Spring의 Model 객체
     * @return admin.html 템플릿
     */
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        BoardDTO board = adminboardService.getBoardById(id);
        if (board == null) {
            return "redirect:/admin/boardsList?error=BoardNotFound";
        }

        model.addAttribute("board", board);
        model.addAttribute("content", "boardsList-edit"); // content 변수 추가
        return "admin/admin"; // admin.html 템플릿 렌더링
    }

    /**
     * 게시글 정보 수정을 처리하는 메서드
     *
     * @param id        게시글 ID
     * @param boardForm 수정된 게시글 정보가 담긴 BoardDTO
     * @param model     Spring의 Model 객체
     * @return 게시판 리스트 페이지로 리다이렉트 또는 편집 페이지로 이동
     */
    @PostMapping("/{id}/edit")
    public String processEditForm(@PathVariable("id") Long id,
                                  @ModelAttribute("board") BoardDTO boardForm,
                                  Model model) {
        boolean isUpdated = adminboardService.updateBoardInfo(id, boardForm);
        if (!isUpdated) {
            model.addAttribute("error", "게시글 수정에 실패했습니다.");
            BoardDTO board = adminboardService.getBoardById(id);
            if (board != null) {
                model.addAttribute("board", board);
            }
            model.addAttribute("content", "boardsList-edit");
            return "admin/admin";
        }

        return "redirect:/admin/boardsList?success=BoardUpdated";
    }

    /**
     * 게시글 상세보기
     *
     * @param id    게시글 ID
     * @param model Spring의 Model 객체
     * @return 게시글 상세보기 페이지
     */
    @GetMapping("/{id}")
    public String viewBoardDetail(@PathVariable("id") Long id, Model model) {
        BoardDTO board = adminboardService.getBoardDetail(id);
        if (board == null) {
            return "redirect:/admin/boards?error=BoardNotFound";
        }
        List<CommentDTO> comments = adminboardService.getCommentsByBoardId(id);
        model.addAttribute("board", board);
        model.addAttribute("comments", comments);
        model.addAttribute("content", "boardList-detail");
        return "admin/admin";  // 상세보기 페이지
    }

    /**
     * 게시글 삭제 처리
     *
     * @param id 게시글 ID
     * @return 게시판 리스트 페이지로 리다이렉트
     */
    @PostMapping("/{id}/delete")
    public String deleteBoard(@PathVariable("id") Long id) {
        adminboardService.deleteBoard(id);
        return "redirect:/admin/boardsList?success=BoardDeleted";
    }

        @PostMapping("/{id}/comment")
        public String addComment(@PathVariable("id") Long id, @ModelAttribute CommentDTO commentDTO) {

            boolean success = adminboardService.addComment(id, commentDTO);

            if (!success) {
                return "redirect:/admin/boardsList/" + id + "?error=CommentExists";
            }

            return "redirect:/admin/boardsList/" + id; // 상세보기 페이지로 이동
        }
    }
