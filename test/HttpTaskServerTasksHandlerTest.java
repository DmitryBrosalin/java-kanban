import com.google.gson.Gson;
import managers.HttpTaskServer;
import managers.Managers;
import managers.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import taskclasses.State;
import taskclasses.Task;

import java.io.IOException;
import java.net.http.HttpClient;
import java.time.Duration;
import java.time.LocalDateTime;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskServerTasksHandlerTest {
    TaskManager taskManager = Managers.getDefault();
    HttpTaskServer taskServer = new HttpTaskServer(taskManager);
    Gson gson = HttpTaskServer.getGson();

    public HttpTaskServerTasksHandlerTest() throws IOException {
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
    public void testGetTaskSuccess() throws IOException, InterruptedException {
        Task task = new Task("Test", "Testing task", State.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        taskManager.addNewTask(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        String responseTask = response.body();
        String jsonTask = gson.toJson(task);

        assertNotNull(responseTask, "Задача не возвращается");
        assertEquals(jsonTask, responseTask, "Задача неверно возвращается сервером");
    }

    @Test
    public void testGetTaskNotFound() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());

        String responseMessage = response.body();
        String errorMessage = "Объект с id = 1 не найден.";

        assertEquals(errorMessage, responseMessage, "Ошибка при возврате задачи, которой нет в менеджере");
    }

    @Test
    public void testGetTaskIdMustBeNumberError() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/i");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response.statusCode());

        String responseMessage = response.body();
        String errorMessage = "id задачи должен быть числом.";

        assertEquals(errorMessage, responseMessage, "Ошибка при запросе задачи с неверным id (не числом)");
    }

    @Test
    public void testGetTasksSuccess() throws IOException, InterruptedException {
        Task task1 = new Task("Test1", "Testing task1", State.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        Task task2 = new Task("Test2", "Testing task2", State.NEW, LocalDateTime.now().plus(Duration.ofMinutes(10)), Duration.ofMinutes(5));
        taskManager.addNewTask(task1);
        taskManager.addNewTask(task2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        String responseTasks = response.body();
        String jsonTasks = gson.toJson(taskManager.getTasks());
        assertNotNull(responseTasks, "Задачи не возвращаются");
        assertEquals(jsonTasks, responseTasks, "Задачи неверно возвращаются сервером");
    }

    @Test
    public void testGetTaskWrongPath() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/1/2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());

        String responseMessage = response.body();
        String errorMessage = "Неверный путь.";

        assertEquals(errorMessage, responseMessage, "Ошибка при запросе неверного пути");
    }

    @Test
    public void testAddTaskSuccess() throws IOException, InterruptedException {
        Task task = new Task("Test", "Testing task", State.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        Map<Integer, Task> tasksFromManager = taskManager.getTasks();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("Test", tasksFromManager.get(1).getName(), "Некорректное имя задачи");
    }

    @Test
    public void testAddTaskTimeInteractions() throws IOException, InterruptedException {
        Task task1 = new Task("Test1", "Testing task1", State.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        Task task2 = new Task("Test2", "Testing task2", State.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        taskManager.addNewTask(task1);
        String task2Json = gson.toJson(task2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(task2Json)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response.statusCode());

        String responseMessage = response.body();
        String errorMessage = "Объект невозможно добавить, так как есть пересечения по времени.";

        assertEquals(errorMessage, responseMessage, "Ошибка при попытке добавления задачи с пересечением времени");
    }

    @Test
    public void testAddTaskSyntaxError() throws IOException, InterruptedException {
        String jsonTaskError = "{\"name\":\"error\",\"description\":\"error\",\"duration\":error,\"startTime\":" +
                "\"error\",\"taskType\":\"error\",\"id\":error,\"state\":\"error\"}";
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(jsonTaskError)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());

        String responseMessage = response.body();
        String errorMessage = "Ошибка при вводе задачи. Проверьте данные.";

        assertEquals(errorMessage, responseMessage, "Ошибка при попытке добавления задачи с ошибками в данных");
    }

    @Test
    public void testAddOrUpdateTaskWrongPath() throws IOException, InterruptedException {
        Task task = new Task("Test", "Testing task", State.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/1/2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());

        String responseMessage = response.body();
        String errorMessage = "Неверный путь.";

        assertEquals(errorMessage, responseMessage, "Ошибка при создании или обновлении задачи в неверном пути");
    }

    @Test
    public void testUpdateTaskSuccess() throws IOException, InterruptedException {
        Task task = new Task("Test", "Testing task", State.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        taskManager.addNewTask(task);
        Task updatedTask = new Task("TestUpdate", "Testing taskUpdate", 1, State.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        String updatedTaskJson = gson.toJson(updatedTask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(updatedTaskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        Map<Integer, Task> tasksFromManager = taskManager.getTasks();

        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("TestUpdate", tasksFromManager.get(1).getName(), "Задача не обновилась");
    }

    @Test
    public void testUpdateTaskWrongId() throws IOException, InterruptedException {
        Task task = new Task("Test", "Testing task", State.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        taskManager.addNewTask(task);
        Task updatedTask = new Task("TestUpdate", "Testing taskUpdate", 2, State.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        String updatedTaskJson = gson.toJson(updatedTask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(updatedTaskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());

        String responseMessage = response.body();
        String errorMessage = "id введенной задачи не совпадает с id задачи, которую вы хотите обновить";

        assertEquals(errorMessage, responseMessage, "Ошибка при несовпадении id пути и обновляемой задачи");
    }

    @Test
    public void testUpdateTaskTimeInteraction() throws IOException, InterruptedException {
        Task task1 = new Task("Test1", "Testing task1", State.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        Task task2 = new Task("Test2", "Testing task2", State.NEW, LocalDateTime.now().plus(Duration.ofMinutes(10)), Duration.ofMinutes(5));
        taskManager.addNewTask(task1);
        taskManager.addNewTask(task2);

        Task updatedTask = new Task("TestUpdate", "Testing taskUpdate", 2, State.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        String updatedTaskJson = gson.toJson(updatedTask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(updatedTaskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response.statusCode());

        String responseMessage = response.body();
        String errorMessage = "Объект невозможно добавить, так как есть пересечения по времени.";

        assertEquals(errorMessage, responseMessage, "Ошибка при попытке обновления задачи при пересечении по времени");
    }

    @Test
    public void testUpdateTaskSyntaxError() throws IOException, InterruptedException {
        Task task1 = new Task("Test1", "Testing task1", State.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        taskManager.addNewTask(task1);

        String jsonTaskError = "{\"name\":\"error\",\"description\":\"error\",\"duration\":error,\"startTime\":" +
                "\"error\",\"taskType\":\"error\",\"id\":error,\"state\":\"error\"}";

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(jsonTaskError)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());

        String responseMessage = response.body();
        String errorMessage = "Ошибка при вводе задачи. Проверьте данные.";

        assertEquals(errorMessage, responseMessage, "Ошибка при попытке обновления задачи с ошибками в данных");
    }

    @Test
    public void testUpdateTaskNotFound() throws IOException, InterruptedException {
        Task task = new Task("Test", "Testing task", State.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());

        String responseMessage = response.body();
        String errorMessage = "Объект с id = 1 не найден.";

        assertEquals(errorMessage, responseMessage, "Ошибка при попытке обновления несуществующей задачи");
    }

    @Test
    public void testUpdateTaskIdNotNumber() throws IOException, InterruptedException {
        Task task1 = new Task("Test1", "Testing task1", State.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        taskManager.addNewTask(task1);

        Task updatedTask = new Task("TestUpdate", "Testing taskUpdate", 2, State.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        String updatedTaskJson = gson.toJson(updatedTask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/i");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(updatedTaskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response.statusCode());

        String responseMessage = response.body();
        String errorMessage = "id задачи должен быть числом.";

        assertEquals(errorMessage, responseMessage, "Ошибка при попытке обновления задачи с неверным id (не числом)");
    }

    @Test
    public void testDeleteTaskSuccess() throws IOException, InterruptedException {
        Task task1 = new Task("Test1", "Testing task1", State.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        taskManager.addNewTask(task1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Map<Integer, Task> tasks = taskManager.getTasks();

        assertEquals(0, tasks.size(), "Задача не была удалена");
    }

    @Test
    public void testDeleteTaskNotFound() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());

        String responseMessage = response.body();
        String errorMessage = "Объект с id = 1 не найден.";

        assertEquals(errorMessage, responseMessage, "Ошибка при попытке удаления несуществующей задачи");
    }

    @Test
    public void testDeleteTaskIdNotNumber() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/i");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response.statusCode());

        String responseMessage = response.body();
        String errorMessage = "id задачи должен быть числом.";

        assertEquals(errorMessage, responseMessage, "Ошибка при попытке удаления задачи с неверным id (не числом)");
    }

    @Test
    public void testDeleteTaskWrongPath() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/1/2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());

        String responseMessage = response.body();
        String errorMessage = "Неверный путь.";

        assertEquals(errorMessage, responseMessage, "Ошибка при удалении задачи по неверному пути");
    }

    @Test
    public void testWrongMethod() throws IOException, InterruptedException {
        Task task = new Task("Test", "Testing task", State.NEW, LocalDateTime.now(), Duration.ofMinutes(5));
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).PUT(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(405, response.statusCode());

        String responseMessage = response.body();
        String errorMessage = "Обработка метода PUT пока не реализована.";

        assertEquals(errorMessage, responseMessage, "Ошибка при неверном методе");
    }
}
