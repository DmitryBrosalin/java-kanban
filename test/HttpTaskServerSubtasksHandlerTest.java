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
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HttpTaskServerSubtasksHandlerTest {
    TaskManager taskManager = Managers.getDefault();
    HttpTaskServer taskServer = new HttpTaskServer(taskManager);
    Gson gson = HttpTaskServer.getGson();

    public HttpTaskServerSubtasksHandlerTest() throws IOException {
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
    public void testGetSubtaskSuccess() throws IOException, InterruptedException {
        Epic parentEpic = new Epic("Test ParentEpic", "Test ParentEpic description");
        Subtask subtask = new Subtask("Test", "Testing subtask", State.NEW, 1, LocalDateTime.now(), Duration.ofMinutes(5));
        taskManager.addNewEpic(parentEpic);
        taskManager.addNewSubtask(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        String responseSubtask = response.body();
        String jsonSubtask = gson.toJson(subtask);

        assertNotNull(responseSubtask, "Подзадача не возвращается");
        assertEquals(jsonSubtask, responseSubtask, "Подзадача неверно возвращается сервером");
    }

    @Test
    public void testGetSubtaskNotFound() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());

        String responseMessage = response.body();
        String errorMessage = "Объект с id = 1 не найден.";

        assertEquals(errorMessage, responseMessage, "Ошибка при возврате подзадачи, которой нет в менеджере");
    }

    @Test
    public void testGetSubtaskIdMustBeNumberError() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/i");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response.statusCode());

        String responseMessage = response.body();
        String errorMessage = "id подзадачи должен быть числом.";

        assertEquals(errorMessage, responseMessage, "Ошибка при запросе подзадачи с неверным id (не числом)");
    }

    @Test
    public void testGetSubtasksSuccess() throws IOException, InterruptedException {
        Epic parentEpic = new Epic("Test ParentEpic", "Test ParentEpic description");
        Subtask subtask1 = new Subtask("Test1", "Testing subtask1", State.NEW, 1, LocalDateTime.now(), Duration.ofMinutes(5));
        Subtask subtask2 = new Subtask("Test2", "Testing subtask2", State.NEW, 1,
                LocalDateTime.now().plus(Duration.ofMinutes(10)), Duration.ofMinutes(5));
        taskManager.addNewEpic(parentEpic);
        taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        String responseSubtasks = response.body();
        String jsonSubtasks = gson.toJson(taskManager.getSubtasks());
        assertNotNull(responseSubtasks, "Подзадачи не возвращаются");
        assertEquals(jsonSubtasks, responseSubtasks, "Подзадачи неверно возвращаются сервером");
    }

    @Test
    public void testGetSubtaskWrongPath() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/1/2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());

        String responseMessage = response.body();
        String errorMessage = "Неверный путь.";

        assertEquals(errorMessage, responseMessage, "Ошибка при запросе неверного пути");
    }

    @Test
    public void testAddSubtaskSuccess() throws IOException, InterruptedException {
        Epic parentEpic = new Epic("Test ParentEpic", "Test ParentEpic description");
        Subtask subtask = new Subtask("Test", "Testing subtask", State.NEW, 1, LocalDateTime.now(), Duration.ofMinutes(5));
        taskManager.addNewEpic(parentEpic);
        String jsonSubtask = gson.toJson(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(jsonSubtask)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        Map<Integer, Subtask> subtasksFromManager = taskManager.getSubtasks();

        assertNotNull(subtasksFromManager, "Задачи не возвращаются");
        assertEquals(1, subtasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test", subtasksFromManager.get(2).getName(), "Некорректное имя задачи");
    }

    @Test
    public void testAddTaskTimeInteractions() throws IOException, InterruptedException {
        Task task1 = new Task("Test1", "Testing task1", State.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        Epic parentEpic = new Epic("Test ParentEpic", "Test ParentEpic description");
        Subtask subtask = new Subtask("Test", "Testing subtask", State.NEW, 2, LocalDateTime.now(), Duration.ofMinutes(5));
        taskManager.addNewTask(task1);
        taskManager.addNewEpic(parentEpic);
        String jsonSubtask = gson.toJson(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(jsonSubtask)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response.statusCode());

        String responseMessage = response.body();
        String errorMessage = "Объект невозможно добавить, так как есть пересечения по времени.";

        assertEquals(errorMessage, responseMessage, "Ошибка при попытке добавления подзадачи с пересечением времени");
    }

    @Test
    public void testAddTaskNoParentEpic() throws IOException, InterruptedException {
        String jsonSubtask = "{\"parentEpicID\":1,\"name\":\"Test\",\"description\":\"Testing subtask\",\"duration\":5,\"startTime\":\"17:58 10.09.24\",\"taskType\":\"SUBTASK\",\"id\":0,\"state\":\"NEW\"}";
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(jsonSubtask)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response.statusCode());

        String responseMessage = response.body();
        String errorMessage = "Неверно указан id родительского эпика.";

        assertEquals(errorMessage, responseMessage, "Ошибка при попытке добавления подзадачи без родительского эпика");
    }

    @Test
    public void testAddSubtaskSyntaxError() throws IOException, InterruptedException {
        Epic parentEpic = new Epic("Test ParentEpic", "Test ParentEpic description");
        String jsonSubtaskError = "{\"parentEpicID\":error,\"name\":\"error\",\"description\":\"error\"," +
                "\"duration\":error,\"startTime\":\"error\",\"taskType\":\"error\",\"id\":error,\"state\":\"error\"}";
        taskManager.addNewEpic(parentEpic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(jsonSubtaskError)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());

        String responseMessage = response.body();
        String errorMessage = "Ошибка при вводе подзадачи. Проверьте данные.";

        assertEquals(errorMessage, responseMessage, "Ошибка при попытке добавления подзадачи с ошибками в данных");
    }

    @Test
    public void testAddOrUpdateSubtaskWrongPath() throws IOException, InterruptedException {
        Epic parentEpic = new Epic("Test ParentEpic", "Test ParentEpic description");
        Subtask subtask = new Subtask("Test", "Testing subtask", State.NEW, 1, LocalDateTime.now(), Duration.ofMinutes(5));
        taskManager.addNewEpic(parentEpic);
        String jsonSubtask = gson.toJson(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/1/2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(jsonSubtask)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());

        String responseMessage = response.body();
        String errorMessage = "Неверный путь.";

        assertEquals(errorMessage, responseMessage, "Ошибка при создании или обновлении подзадачи в неверном пути");
    }

    @Test
    public void testUpdateSubtaskSuccess() throws IOException, InterruptedException {
        Epic parentEpic = new Epic("Test ParentEpic", "Test ParentEpic description");
        Subtask subtask = new Subtask("Test", "Testing subtask", State.NEW, 1, LocalDateTime.now(), Duration.ofMinutes(5));
        taskManager.addNewEpic(parentEpic);
        taskManager.addNewSubtask(subtask);
        Subtask updatedSubtask = new Subtask("TestUpdate", "Testing subtaskUpdate",2, State.NEW, 1, LocalDateTime.now(), Duration.ofMinutes(5));
        String updatedSubtaskJson = gson.toJson(updatedSubtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(updatedSubtaskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        Map<Integer, Subtask> subtasksFromManager = taskManager.getSubtasks();

        assertEquals(1, subtasksFromManager.size(), "Некорректное количество подзадач");
        assertEquals("TestUpdate", subtasksFromManager.get(2).getName(), "Подзадача не обновилась");
    }

    @Test
    public void testUpdateSubtaskWrongId() throws IOException, InterruptedException {
        Epic parentEpic = new Epic("Test ParentEpic", "Test ParentEpic description");
        Subtask subtask = new Subtask("Test", "Testing subtask", State.NEW, 1, LocalDateTime.now(), Duration.ofMinutes(5));
        taskManager.addNewEpic(parentEpic);
        taskManager.addNewSubtask(subtask);
        Subtask updatedSubtask = new Subtask("TestUpdate", "Testing subtaskUpdate",3, State.NEW, 1, LocalDateTime.now(), Duration.ofMinutes(5));
        String updatedSubtaskJson = gson.toJson(updatedSubtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(updatedSubtaskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());

        String responseMessage = response.body();
        String errorMessage = "id введенной подзадачи не совпадает с id подзадачи, которую вы хотите обновить";

        assertEquals(errorMessage, responseMessage, "Ошибка при несовпадении id пути и обновляемой подзадачи");
    }

    @Test
    public void testUpdateSubtaskTimeInteraction() throws IOException, InterruptedException {
        Task task = new Task("Test1", "Testing task1", State.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        Epic parentEpic = new Epic("Test ParentEpic", "Test ParentEpic description");
        Subtask subtask = new Subtask("Test", "Testing subtask", State.NEW, 2, LocalDateTime.now().plus(Duration.ofMinutes(10)), Duration.ofMinutes(5));
        taskManager.addNewTask(task);
        taskManager.addNewEpic(parentEpic);
        taskManager.addNewSubtask(subtask);
        Subtask updatedSubtask = new Subtask("TestUpdate", "Testing subtaskUpdate",3, State.NEW, 2, LocalDateTime.now(), Duration.ofMinutes(5));
        String updatedTaskJson = gson.toJson(updatedSubtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/3");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(updatedTaskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response.statusCode());

        String responseMessage = response.body();
        String errorMessage = "Объект невозможно добавить, так как есть пересечения по времени.";

        assertEquals(errorMessage, responseMessage, "Ошибка при попытке обновления подзадачи при пересечении по времени");
    }

    @Test
    public void testUpdateSubtaskNoParentEpic() throws IOException, InterruptedException {
        Epic parentEpic = new Epic("Test ParentEpic", "Test ParentEpic description");
        Subtask subtask = new Subtask("Test", "Testing subtask", State.NEW, 1, LocalDateTime.now(), Duration.ofMinutes(5));
        taskManager.addNewEpic(parentEpic);
        taskManager.addNewSubtask(subtask);
        Subtask updatedSubtask = new Subtask("TestUpdate", "Testing subtaskUpdate", 2, State.NEW, 3, LocalDateTime.now(), Duration.ofMinutes(5));
        String jsonSubtask = gson.toJson(updatedSubtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(jsonSubtask)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response.statusCode());

        String responseMessage = response.body();
        String errorMessage = "Неверно указан id родительского эпика.";

        assertEquals(errorMessage, responseMessage, "Ошибка при попытке обновления подзадачи с указанием неверного родительского эпика");
    }

    @Test
    public void testUpdateSubtaskSyntaxError() throws IOException, InterruptedException {
        Epic parentEpic = new Epic("Test ParentEpic", "Test ParentEpic description");
        Subtask subtask = new Subtask("Test", "Testing subtask", State.NEW, 1, LocalDateTime.now(), Duration.ofMinutes(5));
        String jsonSubtaskError = "{\"parentEpicID\":error,\"name\":\"error\",\"description\":\"error\"," +
                "\"duration\":error,\"startTime\":\"error\",\"taskType\":\"error\",\"id\":error,\"state\":\"error\"}";
        taskManager.addNewEpic(parentEpic);
        taskManager.addNewSubtask(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(jsonSubtaskError)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());

        String responseMessage = response.body();
        String errorMessage = "Ошибка при вводе подзадачи. Проверьте данные.";

        assertEquals(errorMessage, responseMessage, "Ошибка при попытке обновления подзадачи с ошибками в данных");
    }

    @Test
    public void testUpdateSubtaskNotFound() throws IOException, InterruptedException {
        Epic parentEpic = new Epic("Test ParentEpic", "Test ParentEpic description");
        Subtask subtask = new Subtask("Test", "Testing subtask", State.NEW, 1, LocalDateTime.now(), Duration.ofMinutes(5));
        taskManager.addNewEpic(parentEpic);
        taskManager.addNewSubtask(subtask);
        Subtask updatedSubtask = new Subtask("TestUpdate", "Testing subtaskUpdate",2, State.NEW, 1, LocalDateTime.now(), Duration.ofMinutes(5));
        String updatedSubtaskJson = gson.toJson(updatedSubtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/3");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(updatedSubtaskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());

        String responseMessage = response.body();
        String errorMessage = "Объект с id = 3 не найден.";

        assertEquals(errorMessage, responseMessage, "Ошибка при попытке обновления несуществующей подзадачи");
    }

    @Test
    public void testUpdateTaskIdNotNumber() throws IOException, InterruptedException {
        Epic parentEpic = new Epic("Test ParentEpic", "Test ParentEpic description");
        Subtask subtask = new Subtask("Test", "Testing subtask", State.NEW, 1, LocalDateTime.now(), Duration.ofMinutes(5));
        taskManager.addNewEpic(parentEpic);
        taskManager.addNewSubtask(subtask);
        Subtask updatedSubtask = new Subtask("TestUpdate", "Testing subtaskUpdate",2, State.NEW, 1, LocalDateTime.now(), Duration.ofMinutes(5));
        String updatedSubtaskJson = gson.toJson(updatedSubtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/i");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(updatedSubtaskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response.statusCode());

        String responseMessage = response.body();
        String errorMessage = "id подзадачи должен быть числом.";

        assertEquals(errorMessage, responseMessage, "Ошибка при попытке обновления подзадачи с неверным id (не числом)");
    }

    @Test
    public void testDeleteSubtaskSuccess() throws IOException, InterruptedException {
        Epic parentEpic = new Epic("Test ParentEpic", "Test ParentEpic description");
        Subtask subtask = new Subtask("Test", "Testing subtask", State.NEW, 1, LocalDateTime.now(), Duration.ofMinutes(5));
        taskManager.addNewEpic(parentEpic);
        taskManager.addNewSubtask(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Map<Integer, Subtask> subtasks = taskManager.getSubtasks();

        assertEquals(0, subtasks.size(), "Подзадача не была удалена");
    }

    @Test
    public void testDeleteSubtaskNotFound() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());

        String responseMessage = response.body();
        String errorMessage = "Объект с id = 1 не найден.";

        assertEquals(errorMessage, responseMessage, "Ошибка при попытке удаления несуществующей подзадачи");
    }

    @Test
    public void testDeleteSubtaskIdNotNumber() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/i");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response.statusCode());

        String responseMessage = response.body();
        String errorMessage = "id подзадачи должен быть числом.";

        assertEquals(errorMessage, responseMessage, "Ошибка при попытке удаления подзадачи с неверным id (не числом)");
    }

    @Test
    public void testDeleteSubtaskWrongPath() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/1/2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());

        String responseMessage = response.body();
        String errorMessage = "Неверный путь.";

        assertEquals(errorMessage, responseMessage, "Ошибка при удалении подзадачи по неверному пути");
    }

    @Test
    public void testWrongMethod() throws IOException, InterruptedException {
        Epic parentEpic = new Epic("Test ParentEpic", "Test ParentEpic description");
        Subtask subtask = new Subtask("Test", "Testing subtask", State.NEW, 1, LocalDateTime.now(), Duration.ofMinutes(5));
        taskManager.addNewEpic(parentEpic);
        String jsonSubtask = gson.toJson(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).PUT(HttpRequest.BodyPublishers.ofString(jsonSubtask)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(405, response.statusCode());

        String responseMessage = response.body();
        String errorMessage = "Обработка метода PUT пока не реализована.";

        assertEquals(errorMessage, responseMessage, "Ошибка при неверном методе");
    }
}
