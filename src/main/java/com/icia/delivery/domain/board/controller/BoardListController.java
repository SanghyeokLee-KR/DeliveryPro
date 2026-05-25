package com.icia.delivery.domain.board.controller;

import com.icia.delivery.domain.board.dto.BoardDTO;
import com.icia.delivery.domain.board.service.AdminBoardService;
import com.icia.delivery.domain.comment.dto.CommentDTO;
import com.icia.delivery.global.exception.BusinessException;
import com.icia.delivery.global.exception.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller("boardsListController")
@RequestMapping("/admin/boardsList")
public class BoardListController {

    private static final Logger log = LoggerFactory.getLogger(BoardListController.class);

    @Autowired
    private AdminBoardService adminboardService;

    @GetMapping
    public String viewBoardList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String searchQuery,
            @RequestParam(defaultValue = "boardId") String sortField,
            @RequestParam(defaultValue = "desc") String sortDir,
            Model model) {

        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<BoardDTO> boardsPage = hasText(searchQuery)
                ? adminboardService.searchBoards(searchQuery, pageable)
                : adminboardService.getAllBoardsList(pageable);

        model.addAttribute("boardsList", boardsPage.getContent());
        model.addAttribute("boardsPage", boardsPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", boardsPage.getTotalPages());
        model.addAttribute("totalElements", boardsPage.getTotalElements());
        model.addAttribute("searchQuery", searchQuery);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        model.addAttribute("content", "boardList");

        return "admin/admin";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        try {
            BoardDTO board = adminboardService.getBoardById(id);
            model.addAttribute("board", board);
            model.addAttribute("content", "boardsList-edit");
            return "admin/admin";
        } catch (BusinessException e) {
            log.warn("Board edit form failed. boardId={}", id, e);
            return "redirect:/admin/boardsList?error=BoardNotFound";
        }
    }

    @PostMapping("/{id}/edit")
    public String processEditForm(@PathVariable("id") Long id,
                                  @ModelAttribute("board") BoardDTO boardForm,
                                  Model model) {
        try {
            adminboardService.updateBoardInfo(id, boardForm);
            return "redirect:/admin/boardsList?success=BoardUpdated";
        } catch (BusinessException e) {
            log.warn("Board edit failed. boardId={}", id, e);
            if (ErrorCode.NOT_FOUND.equals(e.getErrorCode())) {
                return "redirect:/admin/boardsList?error=BoardNotFound";
            }

            boardForm.setBoardId(id);
            model.addAttribute("error", e.getMessage());
            model.addAttribute("board", boardForm);
            model.addAttribute("content", "boardsList-edit");
            return "admin/admin";
        }
    }

    @GetMapping("/{id}")
    public String viewBoardDetail(@PathVariable("id") Long id, Model model) {
        try {
            BoardDTO board = adminboardService.getBoardDetail(id);
            List<CommentDTO> comments = adminboardService.getCommentsByBoardId(id);
            model.addAttribute("board", board);
            model.addAttribute("comments", comments);
            model.addAttribute("content", "boardList-detail");
            return "admin/admin";
        } catch (BusinessException e) {
            log.warn("Board detail failed. boardId={}", id, e);
            return "redirect:/admin/boardsList?error=BoardNotFound";
        }
    }

    @PostMapping("/{id}/delete")
    public String deleteBoard(@PathVariable("id") Long id) {
        try {
            adminboardService.deleteBoard(id);
            return "redirect:/admin/boardsList?success=BoardDeleted";
        } catch (BusinessException e) {
            log.warn("Board delete failed. boardId={}", id, e);
            return "redirect:/admin/boardsList?error=BoardNotFound";
        }
    }

    @PostMapping("/{id}/comment")
    public String addComment(@PathVariable("id") Long id, @ModelAttribute CommentDTO commentDTO) {
        try {
            adminboardService.addComment(id, commentDTO);
            return "redirect:/admin/boardsList/" + id;
        } catch (BusinessException e) {
            log.warn("Board comment failed. boardId={}", id, e);
            if (ErrorCode.CONFLICT.equals(e.getErrorCode())) {
                return "redirect:/admin/boardsList/" + id + "?error=CommentExists";
            }
            return "redirect:/admin/boardsList?error=BoardNotFound";
        }
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
