package com.icia.delivery.domain.common.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class CommonPageController {

    @GetMapping("/index")
    public String index() {
        return "index";
    }

    @GetMapping("/question")
    public String question() {
        return "common/question";
    }
}
