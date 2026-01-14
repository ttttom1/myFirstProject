package hello.hello_spring.controller;


import hello.hello_spring.domain.Member;
import hello.hello_spring.domain.chat.ChatResponse;
import hello.hello_spring.repository.MemberRepository;
import hello.hello_spring.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestAttribute;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ChatViewController {

    private final ChatService chatService;
    private final MemberRepository memberRepository;

    @GetMapping("/chat")
    public String chatView(Model model, Principal principal){
        //1. Principal 객체에서 가져온다 아이디를
        String longinId = principal.getName();
        //2.DB에서 해당 아이디로 엔터티를 찾습니당
        Member member = memberRepository.findByLoginId(longinId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다"));
        //3.서비스에서 해당 회원의 채팅 가져오기
        List<ChatResponse> history = chatService.getMainSessions(member.getId());
        //4.화면에 데이터 전달
        model.addAttribute("history",history);
        model.addAttribute("memberId",member.getId());

        return "chat-history";
    }
}
