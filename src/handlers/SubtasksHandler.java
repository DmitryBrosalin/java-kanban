package handlers;

import com.sun.net.httpserver.HttpExchange;
import exceptions.NoParentEpicException;
import managers.HttpTaskServer;
import exceptions.TimeConflictException;
import taskclasses.Subtask;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

public class SubtasksHandler extends TasksHandler {
    public SubtasksHandler(HttpTaskServer taskServer) {
        super(taskServer);
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
                        if (taskManager.getSubtasks().containsKey(id)) {
                            Subtask subtask = taskManager.getSubtask(id);
                            String jsonTask = gson.toJson(subtask);
                            sendText(exchange, 200, jsonTask);
                        } else {
                            sendNotFound(exchange, id);
                        }
                    } else {
                        String text = ("id подзадачи должен быть числом.");
                        sendText(exchange, 406, text);
                    }
                } else if (pathParts.length == 2) {
                    Map<Integer, Subtask> subtasks = taskManager.getSubtasks();
                    String jsonTasks = gson.toJson(subtasks);
                    sendText(exchange, 200, jsonTasks);
                } else {
                    String text = ("Неверный путь.");
                    sendText(exchange, 400, text);
                }
            case ("POST"):
                if (pathParts.length == 3) {
                    idOpt = getTaskId(pathParts);
                    if (idOpt.isPresent()) {
                        int id = idOpt.get();
                        if (taskManager.getSubtasks().containsKey(id)) {
                            try {
                                try {
                                    String body = new String(exchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
                                    Subtask subtask = gson.fromJson(body, Subtask.class);
                                    int idFromRequest = subtask.getId();
                                    if (id == idFromRequest) {
                                        taskManager.updateSubtask(subtask);
                                        exchange.sendResponseHeaders(201, 0);
                                        exchange.close();
                                    } else {
                                        String text = ("id введенной подзадачи не совпадает с id подзадачи, " +
                                                "которую вы хотите обновить");
                                        sendText(exchange, 400, text);
                                    }
                                } catch (TimeConflictException e) {
                                    sendHasInteractions(exchange);
                                } catch (NoParentEpicException e) {
                                    sendText(exchange, 406, e.getMessage());
                                }
                            } catch (Exception e) {
                                String text = ("Ошибка при вводе подзадачи. Проверьте данные.");
                                sendText(exchange, 400, text);
                            }
                        } else {
                            sendNotFound(exchange, id);
                        }
                    } else {
                        String text = ("id подзадачи должен быть числом.");
                        sendText(exchange, 406, text);
                    }
                } else if (pathParts.length == 2) {
                    try {
                        try {
                            String body = new String(exchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
                            Subtask subtask = gson.fromJson(body, Subtask.class);
                            taskManager.addNewSubtask(subtask);
                            exchange.sendResponseHeaders(201, 0);
                            exchange.close();
                        } catch (TimeConflictException e) {
                            sendHasInteractions(exchange);
                        } catch (NoParentEpicException e) {
                            sendText(exchange, 406, e.getMessage());
                        }
                    } catch (Exception e) {
                        String text = ("Ошибка при вводе подзадачи. Проверьте данные.");
                        sendText(exchange, 400, text);
                    }
                } else {
                    String text = ("Неверный путь.");
                    sendText(exchange, 400, text);
                }
                break;
            case ("DELETE"):
                if (pathParts.length == 3) {
                    idOpt = getTaskId(pathParts);
                    if (idOpt.isPresent()) {
                        int id = idOpt.get();
                        if (taskManager.getSubtasks().containsKey(id)) {
                            taskManager.removeSubtask(id);
                            exchange.sendResponseHeaders(200, 0);
                            exchange.close();
                        } else {
                            sendNotFound(exchange, id);
                        }
                    } else {
                        String text = ("id подзадачи должен быть числом.");
                        sendText(exchange, 406, text);
                    }
                } else {
                    String text = ("Неверный путь.");
                    sendText(exchange, 400, text);
                }
                break;
            default:
                String text = ("Обработка метода " + method + " пока не реализована.");
                sendText(exchange, 405, text);
                break;
        }
    }
}
