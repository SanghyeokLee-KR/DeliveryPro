// src/main/java/com/icia/delivery/controller/admin/OperatorController.java
package com.icia.delivery.controller.admin;

import com.icia.delivery.dto.member.LoginHistoryDTO;
import com.icia.delivery.dto.member.MemberDTO;
import com.icia.delivery.service.admin.OperatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin/members")
public class OperatorController {

    @Autowired
    private OperatorService operatorService;

    /**
     * 회원 리스트 페이지를 표시하는 메서드
     *
     * @param page        페이지 번호 (기본값: 0)
     * @param size        페이지당 크기 (기본값: 10)
     * @param searchQuery 검색어
     * @param sortField   정렬 필드 (기본값: mId)
     * @param sortDir     정렬 방향 (asc 또는 desc, 기본값: asc)
     * @param gender      성별 필터
     * @param grade       등급 필터
     * @param status      상태 필터
     * @param model       Spring의 Model 객체
     * @return admin.html 템플릿
     */
    @GetMapping
    public String viewMemberList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String searchQuery,
            @RequestParam(defaultValue = "mId") String sortField,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) String grade,
            @RequestParam(required = false) String status,
            Model model) {

        // 정렬 방향 설정
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();

        // 페이징 객체 생성
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<MemberDTO> membersPage;

        // 검색 및 필터링 조건에 따라 데이터 조회
        if ((searchQuery != null && !searchQuery.trim().isEmpty()) ||
                (gender != null && !gender.trim().isEmpty()) ||
                (grade != null && !grade.trim().isEmpty()) ||
                (status != null && !status.trim().isEmpty())) {
            membersPage = operatorService.searchMembers(searchQuery, gender, grade, status, pageable);
        } else {
            membersPage = operatorService.getAllMembers(pageable);
        }

        // 모델에 데이터 추가
        model.addAttribute("membersPage", membersPage); // 페이징 처리된 회원 데이터
        model.addAttribute("members", membersPage.getContent()); // 현재 페이지 회원 데이터
        model.addAttribute("currentPage", page); // 현재 페이지 번호
        model.addAttribute("totalPages", membersPage.getTotalPages()); // 전체 페이지 수
        model.addAttribute("totalElements", membersPage.getTotalElements()); // 전체 회원 수
        model.addAttribute("searchQuery", searchQuery);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc"); // 정렬 방향 토글
        model.addAttribute("gender", gender);
        model.addAttribute("grade", grade);
        model.addAttribute("status", status);

        // content 속성은 회원 리스트 템플릿을 포함하도록 설정
        model.addAttribute("content", "members");

        // admin.html 렌더링
        return "admin/admin";
    }

    /**
     * 회원 편집 페이지를 표시하는 메서드
     *
     * @param id    회원 ID
     * @param model Spring의 Model 객체
     * @return admin.html 템플릿
     */
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        MemberDTO member = operatorService.getMemberById(id);
        if (member == null) {
            // 회원이 존재하지 않을 경우, 에러 페이지로 이동하거나 적절한 처리
            return "redirect:/admin/members?error=MemberNotFound";
        }

        model.addAttribute("member", member);
        model.addAttribute("content", "member-edit"); // content 변수 추가
        return "admin/admin"; // admin.html 템플릿 렌더링
    }

    /**
     * 회원 정보 수정을 처리하는 메서드
     *
     * @param id         회원 ID
     * @param memberForm 수정된 회원 정보가 담긴 MemberDTO
     * @param model      Spring의 Model 객체
     * @return 회원 리스트 페이지로 리다이렉트 또는 편집 페이지로 이동
     */
    @PostMapping("/{id}/edit")
    public String processEditForm(@PathVariable("id") Long id,
                                  @ModelAttribute("member") MemberDTO memberForm,
                                  Model model) {
        // 입력값 유효성 검사 (추가적인 검증 로직 필요 시 추가)

        // 회원 정보 업데이트
        boolean isUpdated = operatorService.updateMemberInfo(id, memberForm);
        if (!isUpdated) {
            // 업데이트 실패 시, 에러 메시지와 함께 편집 페이지로 이동
            model.addAttribute("error", "회원 정보 수정에 실패했습니다.");
            MemberDTO member = operatorService.getMemberById(id);
            if (member != null) {
                model.addAttribute("member", member);
            }
            model.addAttribute("content", "member-edit"); // content 변수 추가
            return "admin/admin"; // admin.html 템플릿 렌더링
        }

        // 성공 시, 회원 리스트 페이지로 리다이렉트
        return "redirect:/admin/members?success=MemberUpdated";
    }

    // 추가적인 회원 관리 메서드들...
    @GetMapping("/logs")
    public String viewMemberLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String searchQuery,
            @RequestParam(defaultValue = "hisLoginId") String sortField,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String hisDeviceOs,
            @RequestParam(required = false) String hisBrowser,
            Model model) {

        // 정렬 방향 설정
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();

        // 페이징 객체 생성
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<LoginHistoryDTO> memberLogPage;

        // 검색 및 필터링 조건에 따라 데이터 조회
        if (searchQuery == null || searchQuery.trim().isEmpty() &&
                (hisDeviceOs == null || hisDeviceOs.trim().isEmpty()) &&
                (hisBrowser == null || hisBrowser.trim().isEmpty())) {
            memberLogPage = operatorService.getAllMemberLogs(pageable);
        } else {
            memberLogPage = operatorService.searchMemberLogs(searchQuery, hisDeviceOs, hisBrowser, pageable);
        }


        // 회원의 userId만 가져와서 model에 담기
        List<String> userIds = new ArrayList<>();
        for (LoginHistoryDTO log : memberLogPage.getContent()) {
            Long memberId = Long.parseLong(String.valueOf(log.getHisMid()));
            List<String> memberUserIds = operatorService.getMemberUserIdById(memberId);
            if (!memberUserIds.isEmpty()) {
                userIds.add(memberUserIds.get(0));  // userId만 가져옴
            }
        }


        // 모델에 데이터 추가
        model.addAttribute("membersPage", memberLogPage); // 페이징 처리된 회원 데이터
        model.addAttribute("logs", memberLogPage.getContent()); // 현재 페이지 회원 데이터
        model.addAttribute("userIds", userIds); // 각 회원의 userId 리스트
        model.addAttribute("currentPage", page); // 현재 페이지 번호
        model.addAttribute("totalPages", memberLogPage.getTotalPages()); // 전체 페이지 수
        model.addAttribute("totalElements", memberLogPage.getTotalElements()); // 전체 회원 수
        model.addAttribute("searchQuery", searchQuery);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc"); // 정렬 방향 토글
        model.addAttribute("hisDeviceOs", hisDeviceOs);
        model.addAttribute("hisBrowser", hisBrowser);

        // content 속성은 회원 리스트 템플릿을 포함하도록 설정
        model.addAttribute("content", "logs");

        // admin.html 렌더링
        return "admin/admin";
    }


}
