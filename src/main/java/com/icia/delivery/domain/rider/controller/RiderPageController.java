package com.icia.delivery.domain.rider.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class RiderPageController {

    @GetMapping("/riderReason")
    public String riderReason() {
        return "rider/riderReason";
    }

    @GetMapping("/riderCondition")
    public String riderCondition() {
        return "rider/riderCondition";
    }
}
