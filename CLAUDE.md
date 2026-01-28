# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/claude-code) when working with this repository.

## Project Overview

**Deep Dive Navigator** - AI 기반 교육용 채팅 애플리케이션. Google Gemini AI와 계층적 브랜칭 대화를 통해 심층 학습을 지원합니다.

## Tech Stack

- **Java 21** + **Spring Boot 3.2.5**
- **Spring Data JPA** + **Hibernate** (ORM)
- **H2 Database** (인메모리 DB)
- **Thymeleaf** (서버사이드 템플릿)
- **Spring Security** (인증/인가)
- **Spring Cloud OpenFeign** (Gemini API 통신)
- **Lombok** (보일러플레이트 코드 감소)
- **Gradle** (빌드 도구)

## Project Structure

```
src/main/java/hello/hello_spring/
├── config/                  # Security 설정, 인증 컴포넌트
├── controller/              # REST API 및 뷰 컨트롤러
├── service/                 # 비즈니스 로직 (ChatService, MemberService)
├── repository/              # JPA 리포지토리
├── domain/                  # 엔티티 및 DTO
│   ├── Member.java          # 사용자 엔티티
│   └── chat/                # 채팅 관련 도메인
│       ├── ChatNode.java    # 계층형 대화 엔티티
│       ├── AiClient.java    # Gemini API Feign 클라이언트
│       └── ...
└── HelloSpringApplication.java
```

## Build & Run Commands

```bash
# 빌드
./gradlew build

# 실행
./gradlew bootRun

# 테스트
./gradlew test

# Windows
gradlew.bat build
gradlew.bat bootRun
```

## Key Configuration

**중요**: `src/main/resources/application.properties`에 API 키 설정 필수

```properties
ai.api.key=YOUR_GEMINI_API_KEY
```

API 키 미설정 시 `UnsatisfiedDependencyException` 발생.

## Key Files

| 파일 | 설명 |
|------|------|
| `ChatService.java` | 핵심 AI 대화 로직, Gemini API 호출 |
| `ChatController.java` | `/api/chat/*` REST 엔드포인트 |
| `ChatNode.java` | 계층형 대화 엔티티 (parent-child 관계) |
| `SecurityConfig.java` | Spring Security 설정 |
| `chat-history.html` | 메인 채팅 UI (Thymeleaf) |

## API Endpoints

- `POST /api/chat/ask` - AI에게 질문
- `GET /api/chat/history/{memberId}` - 전체 대화 히스토리
- `GET /api/chat/sub-history/{nodeId}` - 브랜치 대화 조회
- `GET /api/chat/sessions/{memberId}` - 메인 세션 목록

## Code Conventions

- 커밋 메시지: `feat.`, `feat:`, `docs:` 접두사 사용
- 한국어 주석 사용
- Lombok 어노테이션 활용 (`@Builder`, `@RequiredArgsConstructor`)
- JPA 엔티티에 `@Transactional` 적용

## Architecture Notes

- **MVC 패턴**: Controller → Service → Repository
- **계층형 대화 구조**: ChatNode가 self-referencing으로 트리 구조 형성
- **rootNode 필드**: 대화 세션 그룹핑용
- **Feign Client**: Gemini API 통신용 선언적 HTTP 클라이언트
