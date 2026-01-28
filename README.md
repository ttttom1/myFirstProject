# 🧠 Deep Dive Navigator

An AI-powered educational chat application built with Spring Boot that enables hierarchical, branching conversations with Google's Gemini AI for deep learning and topic exploration.

---

## 📋 Project Overview

**Deep Dive Navigator** is an intelligent learning assistant that allows users to engage in multi-threaded conversations with AI. The key feature is the ability to create "branching conversations" (꼬리물기) - users can select any part of an AI response and ask follow-up questions, creating a tree-structured conversation that facilitates deeper understanding of complex topics.

### ✨ Key Features

- **Hierarchical Conversations**: Tree-structured chat history with parent-child relationships
- **Branching Questions**: Select text from AI responses to start sub-conversations while maintaining context
- **Session Management**: Sidebar displays all root conversations for quick access
- **User Authentication**: Secure login/signup with Spring Security and BCrypt password encoding
- **Real-time AI Integration**: Powered by Google Gemini 2.0 Flash API

---

## 🛠️ Tech Stack

| Technology | Version | Purpose |
|------------|---------|---------|
| **Java** | 21 | Programming Language |
| **Spring Boot** | 3.2.5 | Application Framework |
| **Spring Data JPA** | 3.2.x | Data Persistence |
| **Hibernate** | 6.4.x | ORM Implementation |
| **H2 Database** | Runtime | In-memory Database |
| **Thymeleaf** | 3.1.x | Server-side Template Engine |
| **Spring Security** | 6.2.x | Authentication & Authorization |
| **Spring Cloud OpenFeign** | 2023.0.1 | Declarative HTTP Client |
| **Lombok** | Latest | Boilerplate Code Reduction |
| **Gradle** | 8.x | Build Tool |

---

## 🏗️ Architecture

This project follows the **MVC (Model-View-Controller)** architectural pattern with a clear separation of concerns.

```
src/main/java/hello/hello_spring/
├── 📂 config/                    # Configuration classes
│   ├── SecurityConfig.java       # Spring Security configuration
│   └── auth/                     # Authentication components
│       ├── PrincipalDetails.java
│       └── PrincipalDetailsService.java
│
├── 📂 controller/                # Controllers (Presentation Layer)
│   ├── ChatController.java       # REST API endpoints for chat
│   ├── ChatViewController.java   # Thymeleaf view controller
│   ├── LoginController.java      # Authentication endpoints
│   ├── MemberController.java     # Member management
│   ├── HomeController.java       # Home page navigation
│   └── HelloController.java      # Sample MVC endpoint
│
├── 📂 service/                   # Services (Business Logic Layer)
│   ├── ChatService.java          # Core AI conversation logic
│   ├── MemberService.java        # User management logic
│   └── SpringConfig.java         # Bean configuration
│
├── 📂 repository/                # Repositories (Data Access Layer)
│   ├── ChatNodeRepository.java   # Chat data persistence
│   └── MemberRepository.java     # Member data persistence
│
├── 📂 domain/                    # Domain Models (Entity Layer)
│   ├── Member.java               # User entity
│   └── chat/
│       ├── ChatNode.java         # Hierarchical chat entity
│       ├── ChatResponse.java     # DTO for API responses
│       ├── NodeType.java         # Enum (QUESTION/ANSWER)
│       ├── AiClient.java         # Feign client for Gemini API
│       ├── GeminiRequest.java    # AI request DTO
│       └── GeminiResponse.java   # AI response DTO
│
└── HelloSpringApplication.java   # Main application entry point
```

### 🔄 Request Flow

```
User Request → Controller → Service → Repository → Database
                   ↓
              AI Client → Gemini API (for chat features)
                   ↓
              Thymeleaf Template → HTML Response
```

---

## ⚙️ Prerequisites & Configuration

### Prerequisites

- **Java 21** or higher
- **Gradle 8.x** or use the included Gradle Wrapper
- **Google Gemini API Key** (required)

### 🔑 API Key Configuration (CRITICAL)

> ⚠️ **IMPORTANT**: You **MUST** configure your Google Gemini API key before running the application. The application will fail to start without this configuration.

1. Create the file `src/main/resources/application.properties` if it doesn't exist

2. Add the following configuration:

```properties
# ===========================================
# 🔑 AI API Configuration (REQUIRED)
# ===========================================
ai.api.key=YOUR_GEMINI_API_KEY_HERE

# ===========================================
# Database Configuration (H2)
# ===========================================
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# ===========================================
# JPA Configuration
# ===========================================
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# ===========================================
# Thymeleaf Configuration
# ===========================================
spring.thymeleaf.cache=false
```

3. Replace `YOUR_GEMINI_API_KEY_HERE` with your actual Google Gemini API key

### 🔐 How to Get a Gemini API Key

1. Visit [Google AI Studio](https://aistudio.google.com/)
2. Sign in with your Google account
3. Navigate to "Get API Key"
4. Create a new API key or use an existing one
5. Copy the key and paste it in your `application.properties`

---

## 🔧 Troubleshooting

### ❌ UnsatisfiedDependencyException: Missing API Key

**Error Message:**
```
org.springframework.beans.factory.UnsatisfiedDependencyException:
Error creating bean with name 'chatService':
Unsatisfied dependency expressed through field 'apiKey'
```

**Cause:** The `ai.api.key` property is not configured in `application.properties`.

**Solution:**
1. Ensure `src/main/resources/application.properties` exists
2. Add the line: `ai.api.key=YOUR_ACTUAL_API_KEY`
3. Restart the application

### ❌ H2 Console Access Issues

If you cannot access the H2 console at `/h2-console`:

1. Ensure `spring.h2.console.enabled=true` is set
2. The JDBC URL should be `jdbc:h2:mem:testdb`
3. Username: `sa`, Password: (leave empty)

### ❌ Login Page Not Loading CSS

The security configuration permits static resources. If CSS isn't loading:

1. Ensure static files are in `src/main/resources/static/`
2. Check that `/css/**` and `/js/**` are permitted in `SecurityConfig.java`

---

## 🚀 How to Run

### Using Gradle Wrapper (Recommended)

```bash
# Clone the repository
git clone <repository-url>
cd myFirstProject

# Build the project
./gradlew build

# Run the application
./gradlew bootRun
```

### On Windows

```cmd
# Build the project
gradlew.bat build

# Run the application
gradlew.bat bootRun
```

### Using IDE

1. Import the project as a Gradle project
2. Configure `application.properties` with your API key
3. Run `HelloSpringApplication.java`

### 🌐 Access the Application

Once running, access the application at:

| URL | Description |
|-----|-------------|
| `http://localhost:8080` | Home page |
| `http://localhost:8080/login` | Login page |
| `http://localhost:8080/signup` | Registration page |
| `http://localhost:8080/chat` | Main chat interface (requires login) |
| `http://localhost:8080/h2-console` | Database console |

---

## 📡 API Endpoints

### Chat API

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/chat/ask` | Send a question to AI |
| `GET` | `/api/chat/history/{memberId}` | Get full chat history |
| `GET` | `/api/chat/sub-history/{nodeId}` | Get branched conversation |
| `GET` | `/api/chat/sessions/{memberId}` | Get main chat sessions |

### Request Example

```json
POST /api/chat/ask
{
  "parentId": null,
  "content": "What is machine learning?"
}
```

---

## 📁 Project Structure

```
myFirstProject/
├── 📂 src/
│   ├── 📂 main/
│   │   ├── 📂 java/hello/hello_spring/
│   │   │   └── (Java source files)
│   │   └── 📂 resources/
│   │       ├── 📂 static/          # Static resources (CSS, JS)
│   │       ├── 📂 templates/       # Thymeleaf templates
│   │       └── application.properties
│   └── 📂 test/
├── build.gradle
├── settings.gradle
└── README.md
```

---

## 📄 License

This project is for educational purposes.

---

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

---

<p align="center">
  Built with ❤️ using Spring Boot and Google Gemini AI
</p>
