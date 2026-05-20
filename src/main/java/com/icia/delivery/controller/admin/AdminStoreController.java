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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller("adminStoreController") // 고유한 빈 이름 지정
@RequestMapping("/admin/stores")
public class AdminStoreController {

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
            storesPage = adminStoreService.getAllStores(pageable);
        }

        // 모델에 데이터 추가
        model.addAttribute("storesPage", storesPage); // 페이징 처리된 가게 데이터
        model.addAttribute("stores", storesPage.getContent()); // 현재 페이지 가게 데이터
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
        model.addAttribute("content", "stores");

        // admin.html 렌더링
        return "admin/admin";
    }
}
