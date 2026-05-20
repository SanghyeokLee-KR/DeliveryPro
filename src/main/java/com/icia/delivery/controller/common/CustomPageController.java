package com.icia.delivery.controller.common;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CustomPageController {

    @GetMapping("/404")
    public String error404() {
        return "error/404"; // templates/error-404.html
    }

    @GetMapping("/500")
    public String error500() {
        return "error/500"; // templates/error-500.html
    }
}
