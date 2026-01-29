# TODO - Deep Dive Navigator 브랜치 기능 개발

## 프로젝트 목표
나뭇가지처럼 메인 → 브랜치1 → 브랜치2 → ... → 브랜치20까지 깊이 학습하다가,
모르는 게 없으면 다시 메인으로 돌아오는 학습용 채팅 앱

---

## 완료된 작업 ✅

### Phase 1: 브랜치 삭제 개선 (완료)

| # | 작업 | 파일 |
|---|------|------|
| 1-1 | 자식 브랜치 있는지 확인 API | Repository, Service, Controller |
| 1-2 | 자식 포함 일괄 닫기 API | Repository, Service, Controller |
| 1-3 | 삭제 전 경고창 표시 | chat-history.html |
| 1-4 | UI에서 해당 페이지 + 자식 모두 제거 | chat-history.html |

#### 추가된 API
- `GET /api/chat/has-children/{nodeId}` - 자식 브랜치 있는지 확인
- `POST /api/chat/close/{nodeId}` - 브랜치 닫기
- `POST /api/chat/open/{nodeId}` - 브랜치 열기
- `POST /api/chat/close-with-children/{nodeId}` - 자식 포함 일괄 닫기

#### ChatNode에 추가된 필드
```java
private Boolean closed = false;      // 브랜치 닫힘 상태
private Integer branchOrder = 0;     // 형제 브랜치 간 순서
private String branchTitle;          // 브랜치 제목/요약
```

#### Member에 추가된 필드
```java
private ChatNode currentNode;        // 현재 대화 중인 노드
```

---

## 남은 작업 📋

### Phase 2: 페이지 이동 & 포커스

| # | 작업 | 설명 | 파일 |
|---|------|------|------|
| 2-1 | 기본 포커스 오른쪽 | 브랜치 열면 새 브랜치(오른쪽)에 포커스 | chat-history.html |
| 2-2 | Deep Dive 클릭 이동 | 브랜치 상단 "Deep Dive:" 클릭 → 원본 페이지로 이동 | chat-history.html |
| 2-3 | 이전 브랜치에서 질문 | 이전 브랜치에서 추가 질문 → 기존 스택에 추가 | chat-history.html |

#### 2-1 수정 방법
`openBranch` 함수에서:
```javascript
// 기존
currentPageIndex = pageStack.length - 2;
// 수정
currentPageIndex = pageStack.length - 1;
```

---

### Phase 3: 메인 브랜치 토글

| # | 작업 | 설명 | 파일 |
|---|------|------|------|
| 3-1 | 토글 UI 컴포넌트 | CSS/HTML로 토글 스타일 생성 | chat-history.html |
| 3-2 | 브랜치 닫을 때 토글 추가 | 메인 페이지에 토글 형태로 브랜치 요약 추가 | chat-history.html |
| 3-3 | 토글 내용 | 헤더: 사용자 질문 / 바디: 첫 답변 | chat-history.html |

#### 토글 동작 방식
- 브랜치에서 질문 → 메인 페이지 해당 노드 하단에 토글 생성
- 토글 헤더: 사용자의 질문
- 토글 바디: 브랜치 닫을 때 첫 번째 답변만 저장

---

## 핵심 개념 정리

### JavaScript 전역 변수
| 변수 | 역할 |
|------|------|
| `pageStack` | 열린 페이지 ID 목록 `['mainPage', 'branch-5-xxx', ...]` |
| `currentPageIndex` | 현재 보고 있는 페이지 위치 (0 = 메인) |
| `branchNodeMap` | 페이지ID → 노드ID 매핑 `{ pageId: nodeId }` |

### 주요 JavaScript 함수
| 함수 | 역할 |
|------|------|
| `openBranch(parentId, text)` | 새 브랜치 페이지 생성 |
| `closeCurrentBranch()` | 현재 브랜치 닫기 (API + UI) |
| `sendQuestion(type, parentId, pageId)` | AI에게 질문 전송 |
| `appendMessage(el, chat)` | 채팅창에 메시지 DOM 추가 |
| `loadSessions()` | 사이드바 세션 목록 로드 |

### 데이터 흐름
```
사용자 드래그 → openBranch() → 새 페이지 생성 → branchNodeMap에 저장
      ↓
sendQuestion() → /api/chat/ask → appendMessage()로 표시
      ↓
closeCurrentBranch() → has-children 확인 → 경고창 → close API → UI 제거
```

---

## 테스트 방법

```bash
# 앱 실행
gradlew.bat bootRun

# 브라우저
http://localhost:8080

# H2 콘솔 (DB 확인)
http://localhost:8080/h2-console
SQL: SELECT id, chat_content, is_closed FROM chat_nodes;
```

### 테스트 시나리오
1. 브랜치 1개 열고 닫기 → 정상 닫힘
2. 브랜치 3개 열고 맨 끝 닫기 → 마지막만 제거
3. 브랜치 3개 열고 중간 닫기 → 경고창 → 중간+오른쪽 모두 제거
4. 경고창에서 취소 → 아무것도 안 닫힘

---

## 버그 수정 완료
- [x] 브랜치창 엔터로 질문 안 됨 → `onkeydown` 이벤트 추가
- [x] 메인 브랜치가 닫히는 문제 → `currentPageIndex <= 0` 체크 추가
- [x] 첫 질문 시 사이드바 세션 안 생김 → `loadSessions()` 호출 추가

---

## 파일 구조
```
src/main/java/hello/hello_spring/
├── controller/ChatController.java    # API 엔드포인트
├── service/ChatService.java          # 비즈니스 로직
├── repository/ChatNodeRepository.java # DB 쿼리
├── domain/
│   ├── chat/ChatNode.java            # 계층형 대화 엔티티
│   └── Member.java                   # 사용자 엔티티
└── ...

src/main/resources/templates/
└── chat-history.html                 # 메인 채팅 UI (JavaScript 포함)
```

---

*마지막 업데이트: 2026-01-29*
*커밋: 7b7605b - feat: 브랜치 닫기 기능 구현*
