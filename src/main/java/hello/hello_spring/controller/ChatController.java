package hello.hello_spring.controller;

import hello.hello_spring.domain.chat.ChatNode;
import hello.hello_spring.domain.chat.ChatResponse;
import hello.hello_spring.domain.chat.DomainField;
import hello.hello_spring.domain.Member;
import hello.hello_spring.service.ChatService;
import hello.hello_spring.repository.MemberRepository;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {
    @Enumerated(EnumType.STRING) // 숫자가 아닌 문자열(String)로 저장하는 방식이 훨씬 안전합니다.
    @Column(columnDefinition = "VARCHAR(255)") // JPA가 자동으로 만드는 check 제약 조건을 방지합니다.
    private final ChatService chatService;

    @Enumerated(EnumType.STRING) // 숫자가 아닌 문자열(String)로 저장하는 방식이 훨씬 안전합니다.
    @Column(columnDefinition = "VARCHAR(255)") // JPA가 자동으로 만드는 check 제약 조건을 방지합니다.
    private final MemberRepository memberRepository;

    /**
     * AI에게 질문을 던지는 API
     * @param memberId 질문하는 사용자 ID (아까 등록한 1번 멤버)
     * @param parentId 부모 질문 ID (첫 질문이면 생략 가능)
     * @param content 질문 내용
     * @param field 관심 분야 (BASIC, FINANCE, PAPER)
     */
    @PostMapping("/ask")
    public ResponseEntity<ChatResponse> ask(@RequestParam(name = "memberId") Long memberId,
                                      @RequestParam(name = "parentId", required = false) Long parentId,
                                      @RequestParam(name = "content") String content,
                                      @RequestParam(name = "field") DomainField field) {

        // 1. 멤버를 먼저 조회 (DB에 실제 회원이 있는지 확인)
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다."));

        // 2. 서비스 호출 (질문과 답변이 한 번에 DB에 저장됨)
        ChatNode savedAnswer = chatService.ask(member, parentId, content, field);

        // 3. 반환값 변환 (ChatNode -> ChatResponse)
        ChatResponse response = ChatResponse.from(savedAnswer);

        return ResponseEntity.ok(response);
    }
}