package com.icia.delivery.domain.admin.controller;

import com.icia.delivery.domain.admin.service.OperatorService;
import com.icia.delivery.domain.loginhistory.dto.LoginHistoryDTO;
import com.icia.delivery.domain.member.dto.MemberDTO;
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

@Controller
@RequestMapping("/admin/members")
public class OperatorController {

    private static final Logger log = LoggerFactory.getLogger(OperatorController.class);

    @Autowired
    private OperatorService operatorService;

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

        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<MemberDTO> membersPage;
        if (hasText(searchQuery) || hasText(gender) || hasText(grade) || hasText(status)) {
            membersPage = operatorService.searchMembers(searchQuery, gender, grade, status, pageable);
        } else {
            membersPage = operatorService.getAllMembers(pageable);
        }

        model.addAttribute("membersPage", membersPage);
        model.addAttribute("members", membersPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", membersPage.getTotalPages());
        model.addAttribute("totalElements", membersPage.getTotalElements());
        model.addAttribute("searchQuery", searchQuery);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        model.addAttribute("gender", gender);
        model.addAttribute("grade", grade);
        model.addAttribute("status", status);
        model.addAttribute("content", "members");

        return "admin/admin";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        try {
            MemberDTO member = operatorService.getMemberById(id);
            model.addAttribute("member", member);
            model.addAttribute("content", "member-edit");
            return "admin/admin";
        } catch (BusinessException e) {
            log.warn("Member edit form failed. memberId={}", id, e);
            return "redirect:/admin/members?error=MemberNotFound";
        }
    }

    @PostMapping("/{id}/edit")
    public String processEditForm(@PathVariable("id") Long id,
                                  @ModelAttribute("member") MemberDTO memberForm,
                                  Model model) {
        try {
            operatorService.updateMemberInfo(id, memberForm);
            return "redirect:/admin/members?success=MemberUpdated";
        } catch (BusinessException e) {
            log.warn("Member edit failed. memberId={}", id, e);
            if (ErrorCode.NOT_FOUND.equals(e.getErrorCode())) {
                return "redirect:/admin/members?error=MemberNotFound";
            }

            memberForm.setMId(id);
            model.addAttribute("error", e.getMessage());
            model.addAttribute("member", memberForm);
            model.addAttribute("content", "member-edit");
            return "admin/admin";
        }
    }

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

        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<LoginHistoryDTO> memberLogPage;
        if (hasText(searchQuery) || hasText(hisDeviceOs) || hasText(hisBrowser)) {
            memberLogPage = operatorService.searchMemberLogs(searchQuery, hisDeviceOs, hisBrowser, pageable);
        } else {
            memberLogPage = operatorService.getAllMemberLogs(pageable);
        }

        List<String> userIds = operatorService.getMemberUserIdsByLoginHistories(memberLogPage.getContent());

        model.addAttribute("membersPage", memberLogPage);
        model.addAttribute("logs", memberLogPage.getContent());
        model.addAttribute("userIds", userIds);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", memberLogPage.getTotalPages());
        model.addAttribute("totalElements", memberLogPage.getTotalElements());
        model.addAttribute("searchQuery", searchQuery);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        model.addAttribute("hisDeviceOs", hisDeviceOs);
        model.addAttribute("hisBrowser", hisBrowser);
        model.addAttribute("content", "logs");

        return "admin/admin";
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
