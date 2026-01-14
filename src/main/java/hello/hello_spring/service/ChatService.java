//package hello.hello_spring.service;
//
//import hello.hello_spring.domain.chat.*;
//import hello.hello_spring.domain.Member;
//import hello.hello_spring.repository.ChatNodeRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.server.ResponseStatusException;
//
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Service
//@Transactional
//@RequiredArgsConstructor
//public class ChatService {
//
//    private final ChatNodeRepository chatNodeRepository;
//    private final AiClient aiClient;
//    private final GeminiInterface geminiInterface; // AI 통신 인터페이스
//
//    // application.properties에 넣을 API 키를 가져옵니다.
//    @Value("${ai.api.key}")
//    private String apiKey;
//
//    public ChatNode ask(Member member, Long parentId, String content) {
//
//        String classificationPrompt = String.format(
//                "Class"
//        )
//
//        chatNodeRepository.findTop3ByMemberIdAndFieldOrderByCreatedAtDesc(member.getId(), field);
//
//
//
//        // 1. 부모 노드(이전 대화)가 있는지 확인
//        ChatNode parent = (parentId != null) ?
//                chatNodeRepository.findById(parentId).orElse(null) : null;
//
//        // 2. 사용자의 질문을 ChatNode로 만들어 저장
//        ChatNode question = ChatNode.builder()
//                .member(member)
//                .parent(parent)
//                .content(content)
//                .nodeType(NodeType.QUESTION)
//                .domainField(field)
//                .depth(parent == null ? 0 : parent.getDepth() + 1)
//                .build();
//        chatNodeRepository.save(question);
//
//        // 3. Gemini API 양식에 맞춰 요청 객체 생성
//        GeminiRequest request = new GeminiRequest(List.of(
//                new GeminiRequest.Content(List.of(new GeminiRequest.Part(content)))
//        ));
//
//        // 4. AI에게 물어보고 답변 받기
//        GeminiResponse response = aiClient.getCompletion(apiKey, request);
//
//        // 응답에서 텍스트 추출 (Gemini JSON 구조가 깊어서 이렇게 가져옵니다)
//        String aiAnswer;
//        try {
//            aiAnswer = response.getCandidates().get(0).getContent().getParts().get(0).getText();
//        }catch (Exception e){
//            System.out.println("응답 구조 분석 실패: " + response);
//            aiAnswer = "AI 응답을 해석하는 데 실패했습니다.";
//        }
//        // 5. AI의 답변을 ChatNode로 만들어 저장 (부모를 위에서 만든 '질문'으로 설정)
//        ChatNode answer = ChatNode.builder()
//                .member(member)
//                .parent(question)
//                .content(aiAnswer)
//                .nodeType(NodeType.ANSWER)
//                .domainField(field)
//                .depth(question.getDepth() + 1)
//                .build();
//
//        return chatNodeRepository.save(answer);
//    }
//
//
//
//
//    //해당 회원의 채팅 히스토리 전체 가져옴
//    @Transactional(readOnly = true)
//    public List<ChatResponse> getChatHistory(Long memberId){
//        List<ChatNode> chatNodes = chatNodeRepository.findByMemberIdOrderByCreatedAtAsc(memberId);
//        return chatNodes.stream()
//                .map(ChatResponse::from)
//                .toList();
//    }
//
//    @Transactional(readOnly = true)
//    public List<ChatResponse> getSubHistory(Long nodeId){
//        // 1. 기준이 되는 노드(사용자가 드래그/클릭한 대화) 가져오기
//        ChatNode rootNode = chatNodeRepository.findById(nodeId)
//                .orElseThrow(() -> new RuntimeException("해당 대화 노드를 찾을 수 없습니다. ID: " + nodeId));
//
//        // 2. 해당 노드를 부모로 둔 하위 대화(자식들) 가져오기
//        List<ChatNode> children = chatNodeRepository.findByParentIdOrderByCreatedAtAsc(nodeId);
//
//        // 3. 반환용 리스트 생성 (기준 노드 + 자식들)
//        List<ChatResponse> subHistory = new ArrayList<>();
//        subHistory.add(ChatResponse.from(rootNode));
//
//        subHistory.addAll(children.stream()
//                .map(ChatResponse::from)
//                .toList());
//        return subHistory;
//    }
//
//
//    private String buildContext(Long parentId){
//        if(parentId == null) return "";
//
//        //부모 3개정도만 가져온다 맥락을 위해서이니
//        List<ChatNode> contextNodes = chatNodeRepository.findByParentIdOrderByCreatedAtAsc(parentId);
//
//        StringBuilder sb = new StringBuilder("이전 대화 맥락:\n");
//        for(ChatNode node: contextNodes){
//            String role = (node.getNodeType() == NodeType.QUESTION) ? "사용자" :"AI";
//            sb.append(role).append(": ").append(node.getContent()).append("\n");
//        }
//        return sb.toString();
//    }
//}



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
                .content(content)
                .nodeType(NodeType.QUESTION)
                .depth(parent == null ? 0 : parent.getDepth() + 1)
                .build();
        chatNodeRepository.save(questionNode);

        // 6. AI의 답변 저장 (사용자 질문의 자식으로 연결)
        ChatNode answerNode = ChatNode.builder()
                .member(member)
                .parent(questionNode)
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
        ChatNode root = chatNodeRepository.findById(nodeId)
                .orElseThrow(() -> new RuntimeException("Node not found: " + nodeId));

        // 특정 노드를 부모로 가진 모든 자식 노드들을 생성 순서대로 조회
        List<ChatNode> children = chatNodeRepository.findByParentIdOrderByCreatedAtAsc(nodeId);

        List<ChatResponse> results = new ArrayList<>();
        results.add(ChatResponse.from(root)); // 기준점 추가
        results.addAll(children.stream().map(ChatResponse::from).toList()); // 자식들 추가
        return results;
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
}