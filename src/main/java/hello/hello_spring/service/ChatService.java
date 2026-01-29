package hello.hello_spring.service;

import hello.hello_spring.domain.chat.*;
import hello.hello_spring.domain.Member;
import hello.hello_spring.repository.ChatNodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ChatService {

    private final ChatNodeRepository chatNodeRepository;
    private final AiClient aiClient;

    @Value("${ai.api.key}")
    private String apiKey;

    /**
     * 핵심 기능: 계층형 질문(Ask)
     * parentId가 있으면 해당 노드의 맥락을 AI에게 전달하여 '브랜치 대화'를 만듭니다.
     */
    public ChatNode ask(Member member, Long parentId, String content) {

        // 1. 부모 노드(드래그한 지점) 가져오기
        ChatNode parent = (parentId != null) ?
                chatNodeRepository.findById(parentId).orElse(null) : null;

        //해당대화 최상단 노드(대화별 구룹핑)
        ChatNode rootNode = (parent == null)? null: (parent.getRootNode() == null ? parent :parent.getRootNode());


        // 2. AI에게 전달할 맥락(Context) 조립 (영어 프롬프트로 정확도 향상)
        // 사용자가 드래그한 원문 내용을 'Immediate Context'로 넣어 학습을 돕습니다.
        String contextContent = (parent != null) ? parent.getContent() : "No prior context.";

        String prompt = String.format(
                "You are a helpful assistant for deep learning.\n" +
                        "The user has selected the following text to ask a follow-up question:\n" +
                        "\"%s\"\n\n" +
                        "User's new question based on that text: \"%s\"\n\n" +
                        "Please explain clearly so the user can understand this specific part better.",
                contextContent, content
        );

        // 3. Gemini API 요청 객체 생성
        GeminiRequest request = new GeminiRequest(List.of(
                new GeminiRequest.Content(List.of(new GeminiRequest.Part(prompt)))
        ));

        // 4. AI 답변 받기
        GeminiResponse response = aiClient.getCompletion(apiKey, request);
        String aiAnswer = extractText(response);

        // 5. 사용자의 질문 저장 (계층 연결)
        ChatNode questionNode = ChatNode.builder()
                .member(member)
                .parent(parent)
                .rootNode(rootNode)
                .content(content)
                .nodeType(NodeType.QUESTION)
                .depth(parent == null ? 0 : parent.getDepth() + 1)
                .build();
        chatNodeRepository.save(questionNode);

        // 질문 저장 후에 rootNode를 다시 결정 (첫 질문이면 본인이 root)
        ChatNode rootForAnswer = (rootNode == null) ? questionNode : rootNode;

        // 6. AI의 답변 저장 (사용자 질문의 자식으로 연결)
        ChatNode answerNode = ChatNode.builder()
                .member(member)
                .parent(questionNode)
                .rootNode(rootForAnswer)
                .content(aiAnswer)
                .nodeType(NodeType.ANSWER)
                .depth(questionNode.getDepth() + 1)
                .build();

        return chatNodeRepository.save(answerNode);
    }

    /**
     * 특정 노드와 그로부터 파생된 자식들(브랜치)만 가져오기
     */
    @Transactional(readOnly = true)
    public List<ChatResponse> getSubHistory(Long nodeId) {
        //노드 아이디 받은 노드 불러옴
        ChatNode node = chatNodeRepository.findById(nodeId)
                .orElseThrow(() -> new RuntimeException("Node not found: " + nodeId));
        //클릭한 노드가, 루트 노드인지 ,아니면 따로 있는지 확인
        Long actualRootId = (node.getParent() == null)? node.getId() : node.getRootNode().getId();

        return chatNodeRepository.findByRootNodeIdAndClosedFalseOrderByIdAsc(actualRootId).
                stream().
                map(ChatResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ChatResponse> getChatHistory(Long memberId) {
        return chatNodeRepository.findByMemberIdOrderByCreatedAtAsc(memberId)
                .stream()
                .map(ChatResponse::from)
                .toList();
    }


    /**
     * 사이드바용: 메인 대화 목록 (최상위 노드들만 조회)
     */
    @Transactional(readOnly = true)
    public List<ChatResponse> getMainSessions(Long memberId) {
        // 부모가 없는(null) 노드들만 가져와서 사이드바에 제목처럼 뿌려줌
        return chatNodeRepository.findByMemberIdAndParentIsNullOrderByCreatedAtDesc(memberId)
                .stream().map(ChatResponse::from).toList();
    }

    // 응답 텍스트 추출 편의 메서드
    private String extractText(GeminiResponse response) {
        try {
            return response.getCandidates().get(0).getContent().getParts().get(0).getText();
        } catch (Exception e) {
            return "AI response parsing failed.";
        }
    }

    //close branch
    public void closeBranch(Long nodeId) {
        ChatNode node = chatNodeRepository.findById(nodeId)
                .orElseThrow(()-> new RuntimeException("Node not found: " + nodeId));
        node.close();
    }

    //open branch
    public void openBranch(Long nodeId){
        ChatNode node = chatNodeRepository.findById(nodeId)
                .orElseThrow(()->new RuntimeException("None not found: " + nodeId));
        node.open();
    }

    @Transactional(readOnly = true)
    public boolean hasOpenChildren(Long nodeId) {
        return chatNodeRepository.existsByParentIdAndClosedFalse(nodeId);
    }

    //해당 노드와 자식들 모두 닫기
    public void closeBranchWithChildren(Long nodeId) {
        ChatNode node = chatNodeRepository.findById(nodeId)
                .orElseThrow(() -> new RuntimeException("Node not found: " + nodeId));

        closeRecursively(node);
    }

    private void closeRecursively(ChatNode node) {
        //자식들 먼저 닫기
        List<ChatNode> children = chatNodeRepository.findByParentIdAndClosedFalse(node.getId());
        for (ChatNode child : children) {
            closeRecursively(child);
        }
        //본인 닫기
        node.close();
    }
}