package hello.hello_spring.repository; // 방법 B라면 패키지 경로를 repository로 수정!

import hello.hello_spring.domain.chat.ChatNode;
import hello.hello_spring.domain.chat.DomainField;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatNodeRepository extends JpaRepository<ChatNode, Long> {
    // 특정 사용자의 모든 대화 내용을 시간순으로 가져오는 기능
    List<ChatNode> findByMemberIdOrderByCreatedAtAsc(Long memberId);

    List<ChatNode> findByParentIdOrderByCreatedAtAsc(Long parentId);

    List<ChatNode> findByMemberIdAndParentIsNullOrderByCreatedAtDesc(Long memberId);

    List<ChatNode> findByRootNodeIdOrderByIdAsc(Long rootNodeId);
}