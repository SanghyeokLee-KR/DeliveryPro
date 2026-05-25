package com.icia.delivery.domain.admin.controller;

import com.icia.delivery.domain.admin.service.AdminStoreService;
import com.icia.delivery.domain.store.dto.PreStoreDTO;
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

@Controller("storesListController")
@RequestMapping("/admin/storesList")
public class StoreListController {

    private static final Logger log = LoggerFactory.getLogger(StoreListController.class);

    @Autowired
    private AdminStoreService adminStoreService;

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

        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<PreStoreDTO> storesPage;
        if (hasText(searchQuery) || hasText(category) || hasText(status)) {
            storesPage = adminStoreService.searchStores(searchQuery, category, status, pageable);
        } else {
            storesPage = adminStoreService.getAllStoresList(pageable);
        }

        model.addAttribute("storesList", storesPage.getContent());
        model.addAttribute("storesPage", storesPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", storesPage.getTotalPages());
        model.addAttribute("totalElements", storesPage.getTotalElements());
        model.addAttribute("searchQuery", searchQuery);
        model.addAttribute("category", category);
        model.addAttribute("status", status);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        model.addAttribute("content", "storesList");

        return "admin/admin";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable("id") Long id, Model model) {
        try {
            PreStoreDTO store = adminStoreService.getStoreById(id);
            model.addAttribute("store", store);
            model.addAttribute("content", "storeList-edit");
            return "admin/admin";
        } catch (BusinessException e) {
            log.warn("Store edit form failed. storeId={}", id, e);
            return "redirect:/admin/storesList?error=StoreNotFound";
        }
    }

    @PostMapping("/{id}/edit")
    public String processEditForm(@PathVariable("id") Long id,
                                  @ModelAttribute("store") PreStoreDTO storeForm,
                                  Model model) {
        log.debug("Store edit requested. storeId={}", id);
        try {
            adminStoreService.updateStoreInfo(id, storeForm);
            return "redirect:/admin/storesList?success=StoreUpdated";
        } catch (BusinessException e) {
            log.warn("Store edit failed. storeId={}", id, e);
            if (ErrorCode.NOT_FOUND.equals(e.getErrorCode())) {
                return "redirect:/admin/storesList?error=StoreNotFound";
            }

            storeForm.setPreStoId(id);
            model.addAttribute("error", e.getMessage());
            model.addAttribute("store", storeForm);
            model.addAttribute("content", "storeList-edit");
            return "admin/admin";
        }
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
