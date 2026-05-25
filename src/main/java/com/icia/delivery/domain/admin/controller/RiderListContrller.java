package com.icia.delivery.domain.admin.controller;

import com.icia.delivery.domain.admin.service.AdminRiderService;
import com.icia.delivery.domain.rider.dto.RiderDTO;
import com.icia.delivery.global.exception.BusinessException;
import com.icia.delivery.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/rider")
public class RiderListContrller {

    private static final Logger log = LoggerFactory.getLogger(RiderListContrller.class);

    private final AdminRiderService riderService;

    @GetMapping("/riderReg")
    public String riderRegForm(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String searchQuery,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "riderNo") String sortField,
            @RequestParam(defaultValue = "asc") String sortDir,
            Model model) {

        return renderRiderList(page, size, searchQuery, status, sortField, sortDir, model, true);
    }

    @GetMapping("/riderList")
    public String riderListForm(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String searchQuery,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "riderNo") String sortField,
            @RequestParam(defaultValue = "asc") String sortDir,
            Model model) {

        return renderRiderList(page, size, searchQuery, status, sortField, sortDir, model, false);
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        log.debug("Rider edit form requested. riderId={}", id);
        try {
            RiderDTO rider = riderService.getRiderById(id);
            model.addAttribute("rider", rider);
            model.addAttribute("content", "riderList-edit");
            return "admin/admin";
        } catch (BusinessException e) {
            log.warn("Rider edit form failed. riderId={}", id, e);
            return "redirect:/admin/rider/riderList?error=RiderNotFound";
        }
    }

    @PostMapping("/{id}/edit")
    public String processEditForm(@PathVariable("id") Long id,
                                  @ModelAttribute("rider") RiderDTO riderForm,
                                  Model model) {
        log.debug("Rider edit requested. riderId={}", id);
        try {
            riderService.updateRiderInfo(id, riderForm);
            return "redirect:/admin/rider/riderList?success=RiderUpdated";
        } catch (BusinessException e) {
            log.warn("Rider edit failed. riderId={}", id, e);
            if (ErrorCode.NOT_FOUND.equals(e.getErrorCode())) {
                return "redirect:/admin/rider/riderList?error=RiderNotFound";
            }

            riderForm.setRiderNo(id);
            model.addAttribute("error", e.getMessage());
            model.addAttribute("rider", riderForm);
            model.addAttribute("content", "riderList-edit");
            return "admin/admin";
        }
    }

    private String renderRiderList(int page,
                                   int size,
                                   String searchQuery,
                                   String status,
                                   String sortField,
                                   String sortDir,
                                   Model model,
                                   boolean registrationList) {
        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<RiderDTO> riderPage;
        if (hasText(searchQuery) || hasText(status)) {
            riderPage = riderService.searchRiders(searchQuery, status, pageable);
        } else if (registrationList) {
            riderPage = riderService.getAllRidersList1(pageable);
        } else {
            riderPage = riderService.getAllRidersList2(pageable);
        }

        model.addAttribute("riderList", riderPage.getContent());
        model.addAttribute("riderPage", riderPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", riderPage.getTotalPages());
        model.addAttribute("totalElements", riderPage.getTotalElements());
        model.addAttribute("searchQuery", searchQuery);
        model.addAttribute("status", status);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        model.addAttribute("content", registrationList ? "riderReg" : "riderList");

        return "admin/admin";
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
