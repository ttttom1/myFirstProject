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

    @Lob // LONGTEXT 대신 Lob 사용 권장
    @Column(name = "chat_content", columnDefinition = "LONGTEXT")
    private String content;

    @Column(name = "node_depth") // depth 대신 node_depth로 이름 변경 (예약어 회피)
    private Integer depth;

    @ManyToOne
    @JoinColumn(name = "root_node_id")
    private ChatNode rootNode; //각 대화의 뿌리 채(최상위 노드)

    private LocalDateTime createdAt;

    @Column(name = "is_closed") //브렌치 닫힘 상태
    private Boolean closed = false;

    @Column(name = "branch_order")//형제 브랜치 간 순서
    private Integer branchOrder = 0;

    @Column(name = "branch_title",length = 100)//브렌치 제목/요약
    private String branchTitle;


    @Builder
    public ChatNode(Member member, ChatNode parent, ChatNode rootNode, NodeType nodeType,
                    String content, Integer depth, String branchTitle) {
        this.member = member;
        this.parent = parent;
        this.rootNode = rootNode;
        this.nodeType = nodeType;
        this.content = content;
        this.depth = depth;
        this.branchTitle = branchTitle;
        this.createdAt = LocalDateTime.now();
    }



    //브랜치 닫기/열기
    public void close() {
        this.closed = true;
    }

    public void open() {
        this.closed = false;
    }

    //브렌치 순서 설정
    public void updateBranchOrder(Integer order){
        this.branchOrder = order;
    }

    //브렌치 제목 설정
    public void updateBranchTitle(String title) {
        this.branchTitle = title;
    }
}