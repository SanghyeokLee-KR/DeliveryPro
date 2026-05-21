package com.icia.delivery.domain.president.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class PresidentPageController {

    @GetMapping("/president")
    public String president() {
        return "president/president";
    }

    @GetMapping("/pLoginForm")
    public String pLoginForm() {
        return "president/pLogin";
    }

    @GetMapping("/pJoinForm")
    public String pJoinForm() {
        return "president/pJoin";
    }

    @GetMapping("/businessHours")
    public String businessHours() {
        return "president/management/presidentguide/businessHours";
    }

    @GetMapping("/manageCoupons")
    public String manageCoupons() {
        return "president/management/presidentguide/managementCoupons";
    }

    @GetMapping("/deliveryCost")
    public String deliveryCost() {
        return "president/management/presidentguide/deliveryCost";
    }

    @GetMapping("/cooNotation")
    public String cooNotation() {
        return "president/management/presidentguide/cooNotation";
    }

    @GetMapping("/menuManagement")
    public String menuManagement() {
        return "president/management/presidentguide/menuManagement";
    }

    @GetMapping("/closedDays")
    public String closedDays() {
        return "president/management/presidentguide/closedDays";
    }
}
