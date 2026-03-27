package com.icia.delivery.controller.admin;

import com.icia.delivery.dto.rider.RiderDTO;
import com.icia.delivery.service.admin.AdminRiderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/rider")
public class RiderListContrller {

    @Autowired
    private final AdminRiderService riderService;

    // riderList
    @GetMapping("/riderReg")
    public String riderRegForm(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String searchQuery,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "riderNo") String sortField,
            @RequestParam(defaultValue = "asc") String sortDir,
            Model model) {

        // 정렬 방향 설정
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();

        // 페이징 객체 생성
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<RiderDTO> riderPage;

        // 검색 및 필터링 조건에 따라 데이터 조회
        if ((searchQuery != null && !searchQuery.trim().isEmpty()) ||
                (status != null && !status.trim().isEmpty())) {
             riderPage = riderService.searchRiders(searchQuery, status, pageable);
        } else {
             riderPage = riderService.getAllRidersList1(pageable);
        }



        // 모델에 데이터 추가
        model.addAttribute("riderList", riderPage.getContent()); // 현재 페이지 가게 데이터 (이름을 storesList로 변경)
        model.addAttribute("riderPage", riderPage); // 페이징 처리된 가게 데이터
        model.addAttribute("currentPage", page); // 현재 페이지 번호
        model.addAttribute("totalPages", riderPage.getTotalPages()); // 전체 페이지 수
        model.addAttribute("totalElements", riderPage.getTotalElements()); // 전체 가게 수
        model.addAttribute("searchQuery", searchQuery);
        model.addAttribute("status", status);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc"); // 정렬 방향 토글

        model.addAttribute("content", "riderReg"); // content 변수 추가
        return "admin/admin"; // admin.html 템플릿 렌더링
    }


    // riderList
    @GetMapping("/riderList")
    public String riderListForm(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String searchQuery,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "riderNo") String sortField,
            @RequestParam(defaultValue = "asc") String sortDir,
            Model model) {

        // 정렬 방향 설정
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();

        // 페이징 객체 생성
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<RiderDTO> riderPage;

        // 검색 및 필터링 조건에 따라 데이터 조회
        if ((searchQuery != null && !searchQuery.trim().isEmpty()) ||
                (status != null && !status.trim().isEmpty())) {
            riderPage = riderService.searchRiders(searchQuery, status, pageable);
        } else {
            riderPage = riderService.getAllRidersList2(pageable);
        }



        // 모델에 데이터 추가
        model.addAttribute("riderList", riderPage.getContent()); // 현재 페이지 가게 데이터 (이름을 storesList로 변경)
        model.addAttribute("riderPage", riderPage); // 페이징 처리된 가게 데이터
        model.addAttribute("currentPage", page); // 현재 페이지 번호
        model.addAttribute("totalPages", riderPage.getTotalPages()); // 전체 페이지 수
        model.addAttribute("totalElements", riderPage.getTotalElements()); // 전체 가게 수
        model.addAttribute("searchQuery", searchQuery);
        model.addAttribute("status", status);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc"); // 정렬 방향 토글

        model.addAttribute("content", "riderList"); // content 변수 추가
        return "admin/admin"; // admin.html 템플릿 렌더링
    }


    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        RiderDTO rider = riderService.getRiderById(id);
        System.out.println("라이더 확인 : " + rider);
        if (rider == null) {
            // 가게가 존재하지 않을 경우, 에러 메시지와 함께 가게 리스트 페이지로 리다이렉트
            return "redirect:/admin/rider/riderList?error=RiderNotFound";
        }

        model.addAttribute("rider", rider);
        model.addAttribute("content", "riderList-edit"); // content 변수 추가
        return "admin/admin"; // admin.html 템플릿 렌더링
    }


    @PostMapping("/{id}/edit")
    public String processEditForm(@PathVariable("id") Long id,
                                  @ModelAttribute("rider") RiderDTO riderForm,
                                  Model model) {
        System.out.println("아이디 값 : " + id);
        System.out.println("가게 정보 : " + riderForm);
        // 라이더 정보 업데이트
        boolean isUpdated = riderService.updateRiderInfo(id, riderForm);
        if (!isUpdated) {
            // 업데이트 실패 시, 에러 메시지와 함께 편집 페이지로 이동
            model.addAttribute("error", "가게 정보 수정에 실패했습니다.");
            RiderDTO rider = riderService.getRiderById(id);
            if (rider != null) {
                model.addAttribute("rider", rider);
            }
            model.addAttribute("content", "riderList-edit"); // content 변수 추가
            return "admin/admin"; // admin.html 템플릿 렌더링
        }

        // 성공 시, 가게 리스트 페이지로 리다이렉트
        return "redirect:/admin/rider/riderReg?success=StoreUpdated";
    }


}
