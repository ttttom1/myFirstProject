package hello.hello_spring.domain.chat;

import hello.hello_spring.domain.chat.ChatNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class ChatResponse {
    private Long id;          // DB에 저장된 답변의 ID
    private String content;   // AI의 답변 내용
    private String nodeType;  // ANSWER
    private Integer depth;    // 계층 깊이
    private String createdAt; // 생성 시간

    // DB 객체(ChatNode)를 받아서 이 클래스(ChatResponse)로 바꿔주는 마법의 메서드
    public static ChatResponse from(ChatNode node) {
        return ChatResponse.builder()
                .id(node.getId())
                .content(node.getContent())
                .nodeType(node.getNodeType().name())
                .depth(node.getDepth())
                .createdAt(node.getCreatedAt().toString())
                .build();
    }
}