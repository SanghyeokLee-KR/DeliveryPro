package com.icia.delivery.controller.admin;

import com.icia.delivery.service.president.StoreService;
import com.icia.delivery.service.rider.RiderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class OperationsController {

    private final StoreService ssvc;
    private final RiderService rsvc;

    @GetMapping("/controller-url/{id}")
    public String approve(@PathVariable Long id) {
        boolean result = ssvc.approve(id);
        if(!result) {
            return "redirect:/admin/dashboard";
        }
        return "redirect:/admin/stores";
    }

    @GetMapping("/rider-status-url/{id}")
    public String riderUrl(@PathVariable Long id) {
        boolean result = rsvc.approve(id);
        if(!result) {
            return "redirect:/admin/dashboard";
        }
        return "redirect:/admin/rider/riderReg";
    }

}
