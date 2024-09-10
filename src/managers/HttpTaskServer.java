package managers;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.sun.net.httpserver.HttpServer;
import handlers.*;
import taskclasses.Epic;
import taskclasses.State;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.InetSocketAddress;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HttpTaskServer {
    private static final int PORT = 8080;
    public HttpServer httpServer;
    public static Gson gson;
    public TaskManager taskManager;

    public HttpServer getHttpServer() {
        return httpServer;
    }

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

        httpServer.createContext("/tasks", new TasksHandler(this));
        httpServer.createContext("/epics", new EpicsHandler(this));
        httpServer.createContext("/subtasks", new SubtasksHandler(this));
        httpServer.createContext("/history", new HistoryHandler(this));
        httpServer.createContext("/prioritized", new PrioritizedHandler(this));
    }

    public static void main(String[] args) throws IOException {
        TaskManager taskManager = FileBackedTaskManager.loadFromFile(Paths.get("backedListOfTasks.txt").toFile());
        HttpTaskServer taskServer = new HttpTaskServer(taskManager);
        taskServer.startServer();
    }

    static class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {
        private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm dd.MM.yy");

        @Override
        public void write(final JsonWriter jsonWriter, final LocalDateTime localDateTime) throws IOException {
            try {
                jsonWriter.value(localDateTime.format(dtf));
            } catch (NullPointerException e) {
                jsonWriter.value("");
            }
        }

        @Override
        public LocalDateTime read(final JsonReader jsonReader) throws IOException {
            try {
                return LocalDateTime.parse(jsonReader.nextString(), dtf);
            } catch (NullPointerException e) {
                return null;
            }
        }
    }

    static class DurationAdapter extends TypeAdapter<Duration> {

        @Override
        public void write(final JsonWriter jsonWriter, final Duration duration) throws IOException {
            try {
                jsonWriter.value(duration.toMinutes());
            } catch (NullPointerException e) {
                jsonWriter.value("");
            }
        }

        @Override
        public Duration read(final JsonReader jsonReader) throws IOException {
            try {
                return Duration.ofMinutes(Long.parseLong(jsonReader.nextString()));
            } catch (NullPointerException e) {
                return null;
            }
        }
    }

    static class EpicDeserializer implements JsonDeserializer<Epic> {
        @Override
        public Epic deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            String name = jsonObject.get("name").getAsString();
            String description = jsonObject.get("description").getAsString();
            int id = jsonObject.get("id").getAsInt();
            State state = State.valueOf(jsonObject.get("state").getAsString());
            return new Epic(name, description, id, state);
        }
    }
}