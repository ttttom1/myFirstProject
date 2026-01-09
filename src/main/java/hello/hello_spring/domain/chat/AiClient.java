package hello.hello_spring.domain.chat;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

// url 부분에서 /v1beta를 떼고 기본 도메인만 남깁니다.
@FeignClient(name = "geminiClient", url = "https://generativelanguage.googleapis.com")
public interface AiClient {

    // 경로를 아래와 같이 정확하게 입력해주세요. (v1beta 사용)
    @PostMapping("/v1beta/models/gemini-2.0-flash:generateContent")
    GeminiResponse getCompletion(
            @RequestParam("key") String apiKey,
            @RequestBody GeminiRequest request
    );
}
