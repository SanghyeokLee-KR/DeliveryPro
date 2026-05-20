// src/main/java/com/icia/delivery/controller/admin/AdminStoreController.java
package com.icia.delivery.controller.admin;

import com.icia.delivery.dto.president.PreStoreDTO;
import com.icia.delivery.service.admin.AdminStoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller("storesListController") // 고유한 빈 이름 지정
@RequestMapping("/admin/storesList")
public class StoreListController {

    @Autowired
    private AdminStoreService adminStoreService;

    /**
     * 가게 리스트 페이지를 표시하는 메서드
     *
     * @param page        페이지 번호 (기본값: 0)
     * @param size        페이지당 크기 (기본값: 10)
     * @param searchQuery 검색어
     * @param category    음식 카테고리 필터
     * @param status      가게 상태 필터
     * @param sortField   정렬 필드 (기본값: preStoId)
     * @param sortDir     정렬 방향 (asc 또는 desc, 기본값: asc)
     * @param model       Spring의 Model 객체
     * @return admin.html 템플릿
     */
    @GetMapping
    public String viewStoreList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String searchQuery,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "preStoId") String sortField,
            @RequestParam(defaultValue = "asc") String sortDir,
            Model model) {

        // 정렬 방향 설정
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();

        // 페이징 객체 생성
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<PreStoreDTO> storesPage;

        // 검색 및 필터링 조건에 따라 데이터 조회
        if ((searchQuery != null && !searchQuery.trim().isEmpty()) ||
                (category != null && !category.trim().isEmpty()) ||
                (status != null && !status.trim().isEmpty())) {
            storesPage = adminStoreService.searchStores(searchQuery, category, status, pageable);
        } else {
            storesPage = adminStoreService.getAllStoresList(pageable);
        }

        // 모델에 데이터 추가
        model.addAttribute("storesList", storesPage.getContent()); // 현재 페이지 가게 데이터 (이름을 storesList로 변경)
        model.addAttribute("storesPage", storesPage); // 페이징 처리된 가게 데이터
        model.addAttribute("currentPage", page); // 현재 페이지 번호
        model.addAttribute("totalPages", storesPage.getTotalPages()); // 전체 페이지 수
        model.addAttribute("totalElements", storesPage.getTotalElements()); // 전체 가게 수
        model.addAttribute("searchQuery", searchQuery);
        model.addAttribute("category", category);
        model.addAttribute("status", status);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc"); // 정렬 방향 토글

        // content 속성은 가게 리스트 템플릿을 포함하도록 설정
        model.addAttribute("content", "storesList");

        // admin.html 렌더링
        return "admin/admin";
    }

    /**
     * 가게 편집 페이지를 표시하는 메서드
     *
     * @param id    가게 ID
     * @param model Spring의 Model 객체
     * @return admin.html 템플릿
     */
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        PreStoreDTO store = adminStoreService.getStoreById(id);
        if (store == null) {
            // 가게가 존재하지 않을 경우, 에러 메시지와 함께 가게 리스트 페이지로 리다이렉트
            return "redirect:/admin/stores?error=StoreNotFound";
        }

        model.addAttribute("store", store);
        model.addAttribute("content", "storeList-edit"); // content 변수 추가
        return "admin/admin"; // admin.html 템플릿 렌더링
    }

    /**
     * 가게 정보 수정을 처리하는 메서드
     *
     * @param id        가게 ID
     * @param storeForm 수정된 가게 정보가 담긴 PreStoreDTO
     * @param model     Spring의 Model 객체
     * @return 가게 리스트 페이지로 리다이렉트 또는 편집 페이지로 이동
     */
    @PostMapping("/{id}/edit")
    public String processEditForm(@PathVariable("id") Long id,
                                  @ModelAttribute("store") PreStoreDTO storeForm,
                                  Model model) {
        System.out.println("아이디 값 : " + id);
        System.out.println("가게 정보 : " + storeForm);
        // 가게 정보 업데이트
        boolean isUpdated = adminStoreService.updateStoreInfo(id, storeForm);
        if (!isUpdated) {
            // 업데이트 실패 시, 에러 메시지와 함께 편집 페이지로 이동
            model.addAttribute("error", "가게 정보 수정에 실패했습니다.");
            PreStoreDTO store = adminStoreService.getStoreById(id);
            if (store != null) {
                model.addAttribute("store", store);
            }
            model.addAttribute("content", "storeList-edit"); // content 변수 추가
            return "admin/admin"; // admin.html 템플릿 렌더링
        }

        // 성공 시, 가게 리스트 페이지로 리다이렉트
        return "redirect:/admin/stores?success=StoreUpdated";
    }

    // 필요 시 추가적인 가게 관리 메서드들 (예: 가게 삭제, 가게 생성 등)
}
