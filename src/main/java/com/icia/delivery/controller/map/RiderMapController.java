package com.icia.delivery.controller.map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RiderMapController {

    @GetMapping("/rider/map")
    public String riderMap() {
        return "rider/map";
    }
}