package hello.hello_spring.service;

import hello.hello_spring.domain.Member;
import hello.hello_spring.domain.chat.ChatNode;
import hello.hello_spring.repository.ChatNodeRepository;
import hello.hello_spring.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class ChatServiceTest {
    @Autowired ChatService chatService;
    @Autowired
    ChatNodeRepository chatNodeRepository;
    @Autowired
    MemberRepository memberRepository;

    @Test
    @DisplayName("대화가 깊어져도 최상위 노드는 유지되어야함")
    void rootNodePropagationTest(){
        //given
        Member member = memberRepository.save(Member.builder()
                .name("테스터")
                .loginId("testUser")
                .password("1234")
                .build());
        ChatNode firstAnswer = chatService.ask(member, null, "첫 번째 질문입니다.");
        ChatNode rootNode = firstAnswer.getParent(); // 질문 노드가 부모이자 루트가 됨

        // 3. 실행: 첫 번째 답변에 대해 꼬리물기 질문
        ChatNode followUpAnswer = chatService.ask(member, firstAnswer.getId(), "꼬리물기 질문입니다.");
        //when
        ChatNode savedFollowUpQuestion = followUpAnswer.getParent();

        //then
        assertThat(savedFollowUpQuestion.getRootNode()).isEqualTo(rootNode);
        assertThat(followUpAnswer.getRootNode()).isEqualTo(rootNode);
    }

}
