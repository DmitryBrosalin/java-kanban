import com.google.gson.Gson;
import managers.HttpTaskServer;
import managers.Managers;
import managers.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import taskclasses.Epic;
import taskclasses.State;
import taskclasses.Subtask;
import taskclasses.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HttpTaskServerHistoryHandlerTest {
    TaskManager taskManager = Managers.getDefault();
    HttpTaskServer taskServer = new HttpTaskServer(taskManager);
    Gson gson = HttpTaskServer.getGson();

    public HttpTaskServerHistoryHandlerTest() throws IOException {
    }

    @BeforeEach
    public void startServer() {
        taskManager.removeAllTasks();
        taskServer.startServer();
    }

    @AfterEach
    public void stopServer() {
        taskServer.stopServer();
    }

    @Test
    public void testGetHistorySuccess() throws IOException, InterruptedException {
        Task task = new Task("Test", "Testing task", State.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        Epic parentEpic = new Epic("Test ParentEpic", "Test ParentEpic description");
        Subtask subtask = new Subtask("Test", "Testing subtask", State.NEW, 2,
                LocalDateTime.now().plus(Duration.ofMinutes(10)), Duration.ofMinutes(5));
        taskManager.addNewTask(task);
        taskManager.addNewEpic(parentEpic);
        taskManager.addNewSubtask(subtask);

        taskManager.getTask(1);
        taskManager.getEpic(2);
        taskManager.getSubtask(3);
        List<Task> history = taskManager.getHistory();

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        String responseHistory = response.body();
        String jsonHistory = gson.toJson(history);

        assertNotNull(responseHistory, "История не возвращается");
        assertEquals(jsonHistory, responseHistory, "История неверно возвращается сервером");
    }

    @Test
    public void testGetHistoryWrongPath() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/history/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());

        String responseMessage = response.body();
        String errorMessage = "Неверный путь.";

        assertEquals(errorMessage, responseMessage, "Ошибка при запросе истории по неверному пути");
    }

    @Test
    public void testWrongMethod() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder().uri(url).PUT(HttpRequest.BodyPublishers.ofString("")).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(405, response.statusCode());

        String responseMessage = response.body();
        String errorMessage = "Обработка метода PUT пока не реализована.";

        assertEquals(errorMessage, responseMessage, "Ошибка при неверном методе");
    }
}
