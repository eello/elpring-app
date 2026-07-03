# Elpring Framework 기반 Todo REST API 예제 애플리케이션 (`elpring-app`)

본 프로젝트는 직접 개발한 스프링 스타일 프레임워크인 **elpring**(`elpring-di`, `elpring-web`, `elpring-boot`)을 활용하여 간단한 RESTful API 백엔드 서버를 구축한 예제 애플리케이션입니다.

---

## 1. 사용된 주요 기술 및 프레임워크 기능

### 1) 의존성 주입 (elpring-di)
* **어노테이션 활용**: `@Controller`, `@Service`, `@Repository` 어노테이션을 각 컴포넌트 클래스에 적용하여 컴포넌트 스캔 대상에 포함시켰습니다.
* **생성자 의존성 주입**: `TodoController` -> `TodoService` -> `TodoRepository` 의 전형적인 3계층 레이어가 생성자 주입 방식으로 결합되어 결합도를 낮추었습니다.

### 2) 웹 요청 및 파라미터 매핑 (elpring-web)
사용자의 요구 사항에 맞춰 아래와 같이 핵심 HTTP 파라미터 바인딩 어노테이션 3가지를 모두 예제 소스 코드 내에 포함하여 REST API를 구성했습니다:
* **`@RequestBody`**: 클라이언트가 보내는 JSON 형식의 요청 데이터를 DTO 객체(`TodoRequest`)로 변환 및 바인딩
* **`@RequestParam`**: 쿼리 스트링(`?status=completed`) 형태로 전달된 필터링 매개변수를 바인딩 (생략 가능한 `required = false` 옵션 적용)
* **`@PathVariable`**: RESTful API 경로 변수(`/{id}`)로부터 엔티티 식별자 ID를 추출 및 바인딩

### 3) 내장 톰캣 서버 구동 (elpring-boot)
* 복잡한 XML이나 외부 웹 아키텍처 설정 없이 `Application` 클래스의 `main` 메서드를 실행하는 것만으로 **내장 아파치 톰캣(Apache Tomcat/10.x)** 서버가 자동으로 준비 및 기동됩니다.

---

## 2. API 엔드포인트 명세

| HTTP Method | URL Path | 기능 설명 | 주요 사용 기술 / 어노테이션 |
| :--- | :--- | :--- | :--- |
| `POST` | `/todos` | 신규 할 일 등록 | `@RequestBody` |
| `GET` | `/todos` | 할 일 전체 목록 조회 및 조건 필터링 | `@RequestParam(required = false)` |
| `GET` | `/todos/{id}` | 특정 할 일 상세 단건 조회 | `@PathVariable` |
| `PUT` | `/todos/{id}` | 특정 할 일 수정 (제목/완료 여부) | `@PathVariable`, `@RequestBody` |
| `DELETE` | `/todos/{id}` | 특정 할 일 삭제 | `@PathVariable` |

---

## 3. 실행 방법 (How to Run)

### 애플리케이션 실행
터미널에서 아래 Gradle 명령어를 실행하여 애플리케이션을 구동할 수 있습니다.
```bash
./gradlew run
```

### 테스트 코드 실행
JUnit 5 기반으로 작성된 리포지토리 단위 테스트, 서비스 비즈니스 로직 테스트 및 실제 톰캣을 띄워 진행하는 E2E 통합 테스트를 일괄 실행합니다.
```bash
./gradlew test
```

---

## 4. API 수동 검증 가이드 (CURL Command Examples)

서버가 구동된 상태(`http://localhost:8080`)에서 터미널을 열고 다음 curl 명령어를 활용해 각 API의 정상 동작을 테스트해볼 수 있습니다.

### 1) 신규 할 일 생성 (POST)
```bash
curl -i -X POST \
  -H "Content-Type: application/json" \
  -d '{"title":"Study Elpring Framework", "completed":false}' \
  http://localhost:8080/todos
```

### 2) 전체 목록 조회 (GET) - status 쿼리 생략
```bash
curl -i http://localhost:8080/todos
```

### 3) 특정 할 일 상세 단건 조회 (GET) - {id} 경로 변수
```bash
curl -i http://localhost:8080/todos/1
```

### 4) 할 일 상태 정보 변경 (PUT) - {id} 경로 변수 및 Body
```bash
curl -i -X PUT \
  -H "Content-Type: application/json" \
  -d '{"title":"Study Elpring Framework", "completed":true}' \
  http://localhost:8080/todos/1
```

### 5) 조건 필터 조회 (GET) - status 쿼리 파라미터
```bash
# 완료(completed) 상태인 항목만 필터 조회
curl -i "http://localhost:8080/todos?status=completed"

# 미완료(active) 상태인 항목만 필터 조회
curl -i "http://localhost:8080/todos?status=active"
```

### 6) 할 일 삭제 (DELETE)
```bash
curl -i -X DELETE http://localhost:8080/todos/1
```
