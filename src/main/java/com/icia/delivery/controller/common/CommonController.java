package com.icia.delivery.controller.common;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class CommonController {

    @GetMapping("/terms")
    public String terms() {
        return "common/terms";
    }

    @GetMapping("/privacy")
    public String privacy() {
        return "common/privacy";
    }

    @GetMapping("/location")
    public String location() {
        return "common/location";
    }

    @GetMapping("/support")
    public String support() {
        return "common/support";
    }

    @GetMapping("/qnaForm")
    public String qnaForm() {
        return "common/qna";
    }

    @GetMapping("/story")
    public String story() {
        return "common/story";
    }

    @GetMapping("/qna")
    public String getQnA() {
        return "common/qna";
    }

}
