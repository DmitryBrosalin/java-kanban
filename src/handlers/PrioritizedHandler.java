package handlers;

import com.sun.net.httpserver.HttpExchange;
import managers.HttpTaskServer;
import taskclasses.Task;

import java.io.IOException;
import java.util.Set;

public class PrioritizedHandler extends BaseHttpHandler {
    public PrioritizedHandler(HttpTaskServer taskServer) {
        super(taskServer);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        String method = exchange.getRequestMethod();
        if (method.equals("GET")) {
            if (pathParts.length == 2) {
                Set<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
                String jsonHistory = gson.toJson(prioritizedTasks);
                sendText(exchange, 200, jsonHistory);
            } else {
                String text = ("Неверный путь.");
                sendText(exchange, 400, text);
            }
        } else {
            String text = ("Обработка метода " + method + " пока не реализована.");
            sendText(exchange, 405, text);
        }
    }
}
