package hello.hello_spring.domain;

import hello.hello_spring.domain.chat.ChatNode;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Column(unique = true, nullable = false)
    private String loginId;
    @Column(nullable = false)
    private String password;
    @OneToMany(mappedBy = "member",cascade = CascadeType.ALL)
    private List<ChatNode> chatNodes = new ArrayList<>();

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<ChatNode> getChatNodes() {
        return chatNodes;
    }

    public void setChatNodes(List<ChatNode> chatNodes) {
        this.chatNodes = chatNodes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
