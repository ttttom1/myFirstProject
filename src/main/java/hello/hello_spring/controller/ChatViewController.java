package hello.hello_spring.controller;


import hello.hello_spring.domain.chat.ChatResponse;
import hello.hello_spring.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestAttribute;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ChatViewController {

    private final ChatService chatService;

    @GetMapping("/chat/view/{memberId}")
    public String chatView(@PathVariable Long memberId, Model model){
        //1.서비스에서 채팅내역 가져오기
        List<ChatResponse> history = chatService.getChatHistory(memberId);

        //2.화면(HTML)에 데이터 전송하기
        model.addAttribute("history",history);
        model.addAttribute("memberId",memberId);

        return "chat-history";
    }
}
