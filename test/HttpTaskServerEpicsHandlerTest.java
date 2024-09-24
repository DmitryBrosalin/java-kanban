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

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HttpTaskServerEpicsHandlerTest {
    TaskManager taskManager = Managers.getDefault();
    HttpTaskServer taskServer = new HttpTaskServer(taskManager);
    Gson gson = HttpTaskServer.getGson();

    public HttpTaskServerEpicsHandlerTest() throws IOException {
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
    public void testGetEpicSuccess() throws IOException, InterruptedException {
        Epic epic = new Epic("Test", "Testing epic");
        taskManager.addNewEpic(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        String responseEpic = response.body();
        String jsonEpic = gson.toJson(epic);

        assertNotNull(responseEpic, "Задача не возвращается");
        assertEquals(jsonEpic, responseEpic, "Задача неверно возвращается сервером");
    }

    @Test
    public void testGetEpicNotFound() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());

        String responseMessage = response.body();
        String errorMessage = "Объект с id = 1 не найден.";

        assertEquals(errorMessage, responseMessage, "Ошибка при возврате эпика, которого нет в менеджере");
    }

    @Test
    public void testGetEpicIdMustBeNumberError() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/i");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response.statusCode());

        String responseMessage = response.body();
        String errorMessage = "id эпика должен быть числом.";

        assertEquals(errorMessage, responseMessage, "Ошибка при запросе эпика с неверным id (не числом)");
    }

    @Test
    public void testGetEpicsSuccess() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Test1", "Testing epic1");
        Epic epic2 = new Epic("Test2", "Testing epic2");
        taskManager.addNewEpic(epic1);
        taskManager.addNewEpic(epic2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        String responseEpics = response.body();
        String jsonEpics = gson.toJson(taskManager.getEpics());
        assertNotNull(responseEpics, "Эпики не возвращаются");
        assertEquals(jsonEpics, responseEpics, "Эпики неверно возвращаются сервером");
    }

    @Test
    public void testGetSubtasksForEpicSuccess() throws IOException, InterruptedException {
        Epic epic = new Epic("Test", "Testing epic");
        Subtask subtask1 = new Subtask("Test1", "Test description1", State.NEW, 1, LocalDateTime.now(), Duration.ofMinutes(5));
        Subtask subtask2 = new Subtask("Test2", "Test description2", State.NEW, 1,
                LocalDateTime.now().plus(Duration.ofMinutes(10)), Duration.ofMinutes(5));
        taskManager.addNewEpic(epic);
        taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        String responseSubtasksForEpic = response.body();
        String jsonSubtasksForEpic = gson.toJson(taskManager.getSubtasksForEpic(1));

        assertNotNull(responseSubtasksForEpic, "Не возвращаются подзадачи для эпика");
        assertEquals(jsonSubtasksForEpic, responseSubtasksForEpic, "Подзадачи для эпика неверно возвращаются сервером");
    }

    @Test
    public void testGetSubtasksForEpicNotFound() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());

        String responseMessage = response.body();
        String errorMessage = "Объект с id = 1 не найден.";

        assertEquals(errorMessage, responseMessage, "Ошибка при возврате эпика, которого нет в менеджере");
    }

    @Test
    public void testGetSubtasksForEpicIdMustBeNumber() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/i/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response.statusCode());

        String responseMessage = response.body();
        String errorMessage = "id эпика должен быть числом.";

        assertEquals(errorMessage, responseMessage, "Ошибка при запросе эпика с неверным id (не числом)");
    }

    @Test
    public void testGetEpicsWrongPath() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1/2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());

        String responseMessage = response.body();
        String errorMessage = "Неверный путь.";

        assertEquals(errorMessage, responseMessage, "Ошибка при запросе неверного пути");
    }

    @Test
    public void testAddEpicSuccess() throws IOException, InterruptedException {
        Epic epic = new Epic("Test", "Testing epic");
        String epicJson = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        Map<Integer, Epic> epicsFromManager = taskManager.getEpics();

        assertNotNull(epicsFromManager, "Эпики не возвращаются");
        assertEquals(1, epicsFromManager.size(), "Некорректное количество эпиков");
        assertEquals("Test", epicsFromManager.get(1).getName(), "Некорректное имя эпика");
    }

    @Test
    public void testAddTaskSyntaxError() throws IOException, InterruptedException {
        String jsonEpicError = "{\"name\":\"error\",\"description\":\"error\"}";
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(jsonEpicError)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());

        String responseMessage = response.body();
        String errorMessage = "Ошибка при вводе эпика. Проверьте данные.";

        assertEquals(errorMessage, responseMessage, "Ошибка при попытке добавления эпика с ошибками в данных");
    }

    @Test
    public void testAddOrUpdateEpicWrongPath() throws IOException, InterruptedException {
        Epic epic = new Epic("Test", "Testing epic");
        String epicJson = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1/2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());

        String responseMessage = response.body();
        String errorMessage = "Неверный путь.";

        assertEquals(errorMessage, responseMessage, "Ошибка при создании или обновлении эпика в неверном пути");
    }

    @Test
    public void testUpdateEpicSuccess() throws IOException, InterruptedException {
        Epic epic = new Epic("Test", "Testing epic");
        taskManager.addNewEpic(epic);
        Epic updatedEpic = new Epic("TestUpdate", "Testing epicUpdate", 1, State.NEW);
        String updatedEpicJson = gson.toJson(updatedEpic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(updatedEpicJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        Map<Integer, Epic> epicsFromManager = taskManager.getEpics();

        assertEquals(1, epicsFromManager.size(), "Некорректное количество задач");
        assertEquals("TestUpdate", epicsFromManager.get(1).getName(), "Задача не обновилась");
    }

    @Test
    public void testUpdateEpicWrongId() throws IOException, InterruptedException {
        Epic epic = new Epic("Test", "Testing epic");
        taskManager.addNewEpic(epic);
        Epic updatedEpic = new Epic("TestUpdate", "Testing epicUpdate", 2, State.NEW);
        String updatedEpicJson = gson.toJson(updatedEpic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(updatedEpicJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());

        String responseMessage = response.body();
        String errorMessage = "id введенного эпика не совпадает с id эпика, который вы хотите обновить";

        assertEquals(errorMessage, responseMessage, "Ошибка при несовпадении id пути и обновляемого эпика");
    }

    @Test
    public void testUpdateEpicSyntaxError() throws IOException, InterruptedException {
        Epic epic = new Epic("Test", "Testing epic");
        taskManager.addNewEpic(epic);

        String jsonEpicError = "{\"name\":\"error\",\"description\":\"error\"}";

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(jsonEpicError)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());

        String responseMessage = response.body();
        String errorMessage = "Ошибка при вводе эпика. Проверьте данные.";

        assertEquals(errorMessage, responseMessage, "Ошибка при попытке обновления эпика с ошибками в данных");
    }

    @Test
    public void testUpdateEpicNotFound() throws IOException, InterruptedException {
        Epic epic = new Epic("Test", "Testing epic");
        String epicJson = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(epicJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());

        String responseMessage = response.body();
        String errorMessage = "Объект с id = 1 не найден.";

        assertEquals(errorMessage, responseMessage, "Ошибка при попытке обновления несуществующего эпика");
    }

    @Test
    public void testUpdateEpicIdNotNumber() throws IOException, InterruptedException {
        Epic epic = new Epic("Test", "Testing epic");
        taskManager.addNewEpic(epic);

        Epic updatedEpic = new Epic("TestUpdate", "Testing epicUpdate", 1, State.NEW);
        String updatedEpicJson = gson.toJson(updatedEpic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/i");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(updatedEpicJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response.statusCode());

        String responseMessage = response.body();
        String errorMessage = "id эпика должен быть числом.";

        assertEquals(errorMessage, responseMessage, "Ошибка при попытке обновления эпика с неверным id (не числом)");
    }

    @Test
    public void testDeleteEpicSuccess() throws IOException, InterruptedException {
        Epic epic = new Epic("Test", "Testing epic");
        taskManager.addNewEpic(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Map<Integer, Epic> epics = taskManager.getEpics();

        assertEquals(0, epics.size(), "Эпик не был удален");
    }

    @Test
    public void testDeleteEpicNotFound() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());

        String responseMessage = response.body();
        String errorMessage = "Объект с id = 1 не найден.";

        assertEquals(errorMessage, responseMessage, "Ошибка при попытке удаления несуществующего эпика");
    }

    @Test
    public void testDeleteEpicIdNotNumber() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/i");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response.statusCode());

        String responseMessage = response.body();
        String errorMessage = "id эпика должен быть числом.";

        assertEquals(errorMessage, responseMessage, "Ошибка при попытке удаления эпика с неверным id (не числом)");
    }

    @Test
    public void testDeleteEpicWrongPath() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1/2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());

        String responseMessage = response.body();
        String errorMessage = "Неверный путь.";

        assertEquals(errorMessage, responseMessage, "Ошибка при удалении эпика по неверному пути");
    }

    @Test
    public void testWrongMethod() throws IOException, InterruptedException {
        Epic epic = new Epic("Test", "Testing epic");
        String epicJson = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).PUT(HttpRequest.BodyPublishers.ofString(epicJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(405, response.statusCode());

        String responseMessage = response.body();
        String errorMessage = "Обработка метода PUT пока не реализована.";

        assertEquals(errorMessage, responseMessage, "Ошибка при неверном методе");
    }
}