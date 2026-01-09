package hello.hello_spring.domain.chat;

import lombok.*;
import java.util.List;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class GeminiRequest {
    private List<Content> contents;

    @Getter @Setter @AllArgsConstructor @NoArgsConstructor
    public static class Content {
        private List<Part> parts;
    }

    @Getter @Setter @AllArgsConstructor @NoArgsConstructor
    public static class Part {
        private String text;
    }
}