package handlers;

import com.sun.net.httpserver.HttpExchange;
import managers.HttpTaskServer;
import exceptions.TimeConflictException;
import managers.TaskManager;
import taskclasses.Task;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

public class TasksHandler extends BaseHttpHandler {
    public TasksHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        String method = exchange.getRequestMethod();
        Optional<Integer> idOpt;
        switch (method) {
            case ("GET"):
                if (pathParts.length == 3) {
                    idOpt = getTaskId(pathParts);
                    if (idOpt.isPresent()) {
                        int id = idOpt.get();
                        if (taskManager.getTasks().containsKey(id)) {
                            Task task = taskManager.getTask(id);
                            String jsonTask = gson.toJson(task);
                            sendText(exchange, 200, jsonTask);
                        } else {
                            sendNotFound(exchange, id);
                        }
                    } else {
                        String text = "id задачи должен быть числом.";
                        sendText(exchange, 406, text);
                    }
                } else if (pathParts.length == 2) {
                    Map<Integer, Task> tasks = taskManager.getTasks();
                    String jsonTasks = gson.toJson(tasks);
                    sendText(exchange, 200, jsonTasks);
                } else {
                    String text = "Неверный путь.";
                    sendText(exchange, 400, text);
                }
            case ("POST"):
                if (pathParts.length == 3) {
                    idOpt = getTaskId(pathParts);
                    if (idOpt.isPresent()) {
                        int id = idOpt.get();
                        if (taskManager.getTasks().containsKey(id)) {
                            try {
                                try {
                                    String body = new String(exchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
                                    Task task = gson.fromJson(body, Task.class);
                                    int idFromRequest = task.getId();
                                    if (id == idFromRequest) {
                                        taskManager.updateTask(task);
                                        exchange.sendResponseHeaders(201, 0);
                                        exchange.close();
                                    } else {
                                        String text = "id введенной задачи не совпадает с id задачи, " +
                                                "которую вы хотите обновить";
                                        sendText(exchange, 400, text);
                                    }
                                } catch (TimeConflictException e) {
                                    sendHasInteractions(exchange);
                                }
                            } catch (Exception e) {
                                String text = "Ошибка при вводе задачи. Проверьте данные.";
                                sendText(exchange, 400, text);
                            }
                        } else {
                            sendNotFound(exchange, id);
                        }
                    } else {
                        String text = "id задачи должен быть числом.";
                        sendText(exchange, 406, text);
                    }
                } else if (pathParts.length == 2) {
                    try {
                        try {
                            String body = new String(exchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
                            Task task = gson.fromJson(body, Task.class);
                            taskManager.addNewTask(task);
                            exchange.sendResponseHeaders(201, 0);
                            exchange.close();
                        } catch (TimeConflictException e) {
                            sendHasInteractions(exchange);
                        }
                    } catch (Exception e) {
                        String text = "Ошибка при вводе задачи. Проверьте данные.";
                        sendText(exchange, 400, text);
                    }
                } else {
                    String text = "Неверный путь.";
                    sendText(exchange, 400, text);
                }
                break;
            case ("DELETE"):
                if (pathParts.length == 3) {
                    idOpt = getTaskId(pathParts);
                    if (idOpt.isPresent()) {
                        int id = idOpt.get();
                        if (taskManager.getTasks().containsKey(id)) {
                            taskManager.removeTask(id);
                            exchange.sendResponseHeaders(200, 0);
                            exchange.close();
                        } else {
                            sendNotFound(exchange, id);
                        }
                    } else {
                        String text = "id задачи должен быть числом.";
                        sendText(exchange, 406, text);
                    }
                } else {
                    String text = "Неверный путь.";
                    sendText(exchange, 400, text);
                }
                break;
            default:
                String text = "Обработка метода " + method + " пока не реализована.";
                sendText(exchange, 405, text);
                break;
        }
    }
}
