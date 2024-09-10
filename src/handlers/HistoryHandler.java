package handlers;

import com.sun.net.httpserver.HttpExchange;
import managers.HttpTaskServer;
import taskclasses.Task;

import java.io.IOException;
import java.util.List;

public class HistoryHandler extends BaseHttpHandler {
    public HistoryHandler(HttpTaskServer taskServer) {
        super(taskServer);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        String method = exchange.getRequestMethod();
        if (method.equals("GET")) {
            if (pathParts.length == 2) {
                List<Task> history = taskManager.getHistory();
                String jsonHistory = gson.toJson(history);
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
