package com.icia.delivery.controller.president;


import com.icia.delivery.dto.president.PreMemberDTO;
import com.icia.delivery.service.president.PreMemService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequiredArgsConstructor
public class PreMemberController {

    private final PreMemService pmsvc;
    private final HttpSession session;

    @GetMapping("/persident")
    public String terms() {
        return "contents/terms";
    }

    @PostMapping("/storeForm")
    public ModelAndView storeForm(@ModelAttribute PreMemberDTO preMem) {
        // System.out.println("사업자 등록 : " + preMem);
        return pmsvc.storeForm(preMem);
    }

    @PostMapping("/pLogin")
    public ModelAndView pLogin(@ModelAttribute PreMemberDTO preMem) {
        System.out.println("사업자 로그인 : " + preMem);
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
