package eello.elpring.app.controller;

import eello.elpring.app.Application;
import eello.elpring.boot.ElpringBootApplication;
import eello.elpring.boot.core.EmbeddedTomcatRunner;
import eello.elpring.di.context.ConfigurableApplicationContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;

class TodoControllerTest {

    private ConfigurableApplicationContext context;
    private EmbeddedTomcatRunner tomcatRunner;
    private HttpClient httpClient;
    private final String baseUrl = "http://localhost:8080";

    @BeforeEach
    void setUp() throws Exception {
        // 애플리케이션 실행 (내장 톰캣 기동)
        context = ElpringBootApplication.run(Application.class);
        tomcatRunner = context.getBean(EmbeddedTomcatRunner.class);
        httpClient = HttpClient.newHttpClient();
    }

    @AfterEach
    void tearDown() throws Exception {
        if (tomcatRunner != null) {
            tomcatRunner.close();
        }
    }

    @Test
    void testTodoRestApiFlow() throws Exception {
        // 1. POST /todos - 할 일 생성 (@RequestBody 검증)
        String requestBody = "{\"title\":\"Build web framework\",\"completed\":false}";
        HttpRequest createReq = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/todos"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> createRes = httpClient.send(createReq, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, createRes.statusCode());
        String createJson = createRes.body();
        assertTrue(createJson.contains("\"title\":\"Build web framework\""));
        assertTrue(createJson.contains("\"completed\":false"));
        assertTrue(createJson.contains("\"id\":"));

        // 생성된 ID 추출
        // 단순 JSON 파싱 (정규표현식 활용)
        java.util.regex.Matcher matcher = java.util.regex.Pattern.compile("\"id\":(\\d+)").matcher(createJson);
        assertTrue(matcher.find());
        String todoId = matcher.group(1);

        // 2. GET /todos/{id} - 단건 조회 (@PathVariable 검증)
        HttpRequest getReq = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/todos/" + todoId))
                .GET()
                .build();

        HttpResponse<String> getRes = httpClient.send(getReq, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, getRes.statusCode());
        assertTrue(getRes.body().contains("\"title\":\"Build web framework\""));

        // 3. PUT /todos/{id} - 상태 변경 (@PathVariable + @RequestBody 검증)
        String updateBody = "{\"title\":\"Build web framework\",\"completed\":true}";
        HttpRequest updateReq = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/todos/" + todoId))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(updateBody))
                .build();

        HttpResponse<String> updateRes = httpClient.send(updateReq, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, updateRes.statusCode());
        assertTrue(updateRes.body().contains("\"completed\":true"));

        // 4. GET /todos - 상태별 필터 검색 (@RequestParam 검증)
        // 4-1. 쿼리 파라미터 아예 없이 전체 목록 조회 (required = false 정상 작동 검증)
        HttpRequest listAllReq = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/todos"))
                .GET()
                .build();

        HttpResponse<String> listAllRes = httpClient.send(listAllReq, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, listAllRes.statusCode());
        assertTrue(listAllRes.body().contains("\"completed\":true"));

        // 4-2. 완료(completed) 필터로 검색
        HttpRequest listCompletedReq = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/todos?status=completed"))
                .GET()
                .build();

        HttpResponse<String> listCompletedRes = httpClient.send(listCompletedReq, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, listCompletedRes.statusCode());
        assertTrue(listCompletedRes.body().contains("\"completed\":true"));

        // 미완료(active) 필터로 검색 (방금 완료 처리했으므로 빈 리스트여야 함)
        HttpRequest listActiveReq = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/todos?status=active"))
                .GET()
                .build();

        HttpResponse<String> listActiveRes = httpClient.send(listActiveReq, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, listActiveRes.statusCode());
        assertEquals("[]", listActiveRes.body().trim());

        // 5. DELETE /todos/{id} - 삭제 (@PathVariable 검증)
        HttpRequest deleteReq = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/todos/" + todoId))
                .DELETE()
                .build();

        HttpResponse<String> deleteRes = httpClient.send(deleteReq, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, deleteRes.statusCode());

        // 삭제 후 다시 단건 조회 시도 -> 에러(500 혹은 NotFound) 확인
        HttpRequest getAfterDeleteReq = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/todos/" + todoId))
                .GET()
                .build();

        HttpResponse<String> getAfterDeleteRes = httpClient.send(getAfterDeleteReq, HttpResponse.BodyHandlers.ofString());
        // 존재하지 않는 Todo 조회 시 IllegalArgumentException이 발생해 500 에러 코드가 떨어질 것
        assertEquals(500, getAfterDeleteRes.statusCode());
    }
}
