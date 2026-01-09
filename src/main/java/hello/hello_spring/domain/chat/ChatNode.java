package hello.hello_spring.domain.chat;

import hello.hello_spring.domain.Member;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "chat_nodes") // 테이블 명에 s를 붙여 예약어 충돌 방지
public class ChatNode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT)) // 외래키 제약조건 무시
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT)) // 외래키 제약조건 무시
    private ChatNode parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<ChatNode> children = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "node_type", nullable = false, length = 50)
    private NodeType nodeType;

    @Enumerated(EnumType.STRING)
    @Column(name = "domain_field", nullable = false, length = 50)
    private DomainField domainField;

    @Lob // LONGTEXT 대신 Lob 사용 권장
    @Column(name = "chat_content", columnDefinition = "LONGTEXT")
    private String content;

    @Column(name = "node_depth") // depth 대신 node_depth로 이름 변경 (예약어 회피)
    private Integer depth;

    private LocalDateTime createdAt;

    @Builder
    public ChatNode(Member member, ChatNode parent, NodeType nodeType,
                    DomainField domainField, String content, Integer depth) {
        this.member = member;
        this.parent = parent;
        this.nodeType = nodeType;
        this.domainField = domainField;
        this.content = content;
        this.depth = depth;
        this.createdAt = LocalDateTime.now();
    }
}