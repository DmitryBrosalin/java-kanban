package managers;

import com.google.gson.*;
import com.sun.net.httpserver.HttpServer;
import handlers.*;
import taskclasses.Epic;
import adapters.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private final HttpServer httpServer;
    private static Gson gson;
    private final TaskManager taskManager;

    public static Gson getGson() {
        return gson;
    }

    public TaskManager getTaskManager() {
        return taskManager;
    }

    public void startServer() {
        httpServer.start();
    }

    public void stopServer() {
        httpServer.stop(0);
    }

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        this.taskManager = taskManager;
        httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(Epic.class, new EpicDeserializer());
        gson = gsonBuilder.create();

        httpServer.createContext("/tasks", new TasksHandler(taskManager));
        httpServer.createContext("/epics", new EpicsHandler(taskManager));
        httpServer.createContext("/subtasks", new SubtasksHandler(taskManager));
        httpServer.createContext("/history", new HistoryHandler(taskManager));
        httpServer.createContext("/prioritized", new PrioritizedHandler(taskManager));
    }

    public static void main(String[] args) throws IOException {
        TaskManager taskManager = FileBackedTaskManager.loadFromFile(Paths.get("backedListOfTasks.txt").toFile());
        HttpTaskServer taskServer = new HttpTaskServer(taskManager);
        taskServer.startServer();
    }
}