package handlers;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managers.HttpTaskServer;
import managers.TaskManager;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public abstract class BaseHttpHandler implements HttpHandler {
    protected static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    protected TaskManager taskManager;
    protected Gson gson;

    public BaseHttpHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
        this.gson = HttpTaskServer.getGson();
    }

    protected void sendText(HttpExchange exchange, int code, String text) throws IOException {
        byte[] response = text.getBytes(DEFAULT_CHARSET);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(code, response.length);
        exchange.getResponseBody().write(response);
        exchange.close();
    }

    protected void sendNotFound(HttpExchange exchange, int id) throws IOException {
        String text = "Объект с id = " + id + " не найден.";
        byte[] response = text.getBytes(DEFAULT_CHARSET);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(404, response.length);
        exchange.getResponseBody().write(response);
        exchange.close();
    }

    protected void sendHasInteractions(HttpExchange exchange) throws IOException {
        String text = "Объект невозможно добавить, так как есть пересечения по времени.";
        byte[] response = text.getBytes(DEFAULT_CHARSET);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(406, response.length);
        exchange.getResponseBody().write(response);
        exchange.close();
    }

    protected Optional<Integer> getTaskId(String[] pathParts) {
        try {
            return Optional.of(Integer.parseInt(pathParts[2]));
        } catch (NumberFormatException exception) {
            return Optional.empty();
        }
    }
}
