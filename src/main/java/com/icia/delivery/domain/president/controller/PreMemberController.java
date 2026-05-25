package com.icia.delivery.domain.president.controller;


import com.icia.delivery.domain.president.dto.PreMemberDTO;
import com.icia.delivery.domain.president.service.PreMemService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequiredArgsConstructor
public class PreMemberController {

    private static final Logger log = LoggerFactory.getLogger(PreMemberController.class);

    private final PreMemService pmsvc;
    private final HttpSession session;

    @GetMapping("/persident")
    public String terms() {
        return "contents/terms";
    }

    @PostMapping("/storeForm")
    public ModelAndView storeForm(@ModelAttribute PreMemberDTO preMem) {
        return pmsvc.storeForm(preMem);
    }

    @PostMapping("/pLogin")
    public ModelAndView pLogin(@ModelAttribute PreMemberDTO preMem) {
        log.debug("President login requested. userId={}", preMem.getPreMemUserId());
        return pmsvc.pLogin(preMem);
    }

    @GetMapping("/pLogout")
    public String mLogout() {
        Long preMemId = (Long) session.getAttribute("preMem_id");
        if(preMemId != null){
            session.removeAttribute("preMem_id");
            session.removeAttribute("preMem_userid");
            session.removeAttribute("preMem_email");
            session.removeAttribute("preMem_username");
        }
        return "/president/president";
    }

}
