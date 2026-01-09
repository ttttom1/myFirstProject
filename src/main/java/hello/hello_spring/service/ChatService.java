package hello.hello_spring.service;

import hello.hello_spring.domain.chat.*;
import hello.hello_spring.domain.Member;
import hello.hello_spring.domain.chat.ChatNodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ChatService {

    private final ChatNodeRepository chatNodeRepository;
    private final AiClient aiClient;

    // application.properties에 넣을 API 키를 가져옵니다.
    @Value("${ai.api.key}")
    private String apiKey;

    public ChatNode ask(Member member, Long parentId, String content, DomainField field) {

        // 1. 부모 노드(이전 대화)가 있는지 확인
        ChatNode parent = (parentId != null) ?
                chatNodeRepository.findById(parentId).orElse(null) : null;

        // 2. 사용자의 질문을 ChatNode로 만들어 저장
        ChatNode question = ChatNode.builder()
                .member(member)
                .parent(parent)
                .content(content)
                .nodeType(NodeType.QUESTION)
                .domainField(field)
                .depth(parent == null ? 0 : parent.getDepth() + 1)
                .build();
        chatNodeRepository.save(question);

        // 3. Gemini API 양식에 맞춰 요청 객체 생성
        GeminiRequest request = new GeminiRequest(List.of(
                new GeminiRequest.Content(List.of(new GeminiRequest.Part(content)))
        ));

        // 4. AI에게 물어보고 답변 받기
        GeminiResponse response = aiClient.getCompletion(apiKey, request);

        // 응답에서 텍스트 추출 (Gemini JSON 구조가 깊어서 이렇게 가져옵니다)
        String aiAnswer;
        try {
            aiAnswer = response.getCandidates().get(0).getContent().getParts().get(0).getText();
        }catch (Exception e){
            System.out.println("응답 구조 분석 실패: " + response);
            aiAnswer = "AI 응답을 해석하는 데 실패했습니다.";
        }
        // 5. AI의 답변을 ChatNode로 만들어 저장 (부모를 위에서 만든 '질문'으로 설정)
        ChatNode answer = ChatNode.builder()
                .member(member)
                .parent(question)
                .content(aiAnswer)
                .nodeType(NodeType.ANSWER)
                .domainField(field)
                .depth(question.getDepth() + 1)
                .build();

        return chatNodeRepository.save(answer);
    }
}