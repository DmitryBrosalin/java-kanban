package handlers;

import com.sun.net.httpserver.HttpExchange;
import managers.HttpTaskServer;
import taskclasses.Epic;
import taskclasses.Subtask;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class EpicsHandler extends TasksHandler {
    public EpicsHandler(HttpTaskServer taskServer) {
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
                        if (taskManager.getEpics().containsKey(id)) {
                            Epic epic = taskManager.getEpic(id);
                            String jsonTask = gson.toJson(epic);
                            sendText(exchange, 200, jsonTask);
                        } else {
                            sendNotFound(exchange, id);
                        }
                    } else {
                        String text = ("id эпика должен быть числом.");
                        sendText(exchange, 406, text);
                    }
                } else if (pathParts.length == 2) {
                    Map<Integer, Epic> epics = taskManager.getEpics();
                    String jsonTasks = gson.toJson(epics);
                    sendText(exchange, 200, jsonTasks);
                } else if (pathParts.length == 4 && pathParts[3].equals("subtasks")) {
                    idOpt = getTaskId(pathParts);
                    if (idOpt.isPresent()) {
                        int id = idOpt.get();
                        if (taskManager.getEpics().containsKey(id)) {
                            List<Subtask> subtasksForEpic = taskManager.getSubtasksForEpic(id);
                            String jsonTask = gson.toJson(subtasksForEpic);
                            sendText(exchange, 200, jsonTask);
                        } else {
                            sendNotFound(exchange, id);
                        }
                    } else {
                        String text = ("id эпика должен быть числом.");
                        sendText(exchange, 406, text);
                    }
                } else {
                    String text = ("Неверный путь.");
                    sendText(exchange, 400, text);
                }
            case ("POST"):
                if (pathParts.length == 3) {
                    idOpt = getTaskId(pathParts);
                    if (idOpt.isPresent()) {
                        int id = idOpt.get();
                        if (taskManager.getEpics().containsKey(id)) {
                            try {
                                String body = new String(exchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
                                Epic epic = gson.fromJson(body, Epic.class);
                                int idFromRequest = epic.getId();
                                if (id == idFromRequest) {
                                    taskManager.updateEpic(epic);
                                    exchange.sendResponseHeaders(201, 0);
                                    exchange.close();
                                } else {
                                    String text = ("id введенного эпика не совпадает с id эпика, " +
                                            "который вы хотите обновить");
                                    sendText(exchange, 400, text);
                                }
                            } catch (Exception e) {
                                String text = ("Ошибка при вводе эпика. Проверьте данные.");
                                sendText(exchange, 400, text);
                            }
                        } else {
                            sendNotFound(exchange, id);
                        }
                    } else {
                        String text = ("id эпика должен быть числом.");
                        sendText(exchange, 406, text);
                    }
                } else if (pathParts.length == 2) {
                    try {
                        String body = new String(exchange.getRequestBody().readAllBytes(), DEFAULT_CHARSET);
                        Epic epic = gson.fromJson(body, Epic.class);
                        taskManager.addNewEpic(epic);
                        exchange.sendResponseHeaders(201, 0);
                        exchange.close();
                    } catch (Exception e) {
                        String text = ("Ошибка при вводе эпика. Проверьте данные.");
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
                        if (taskManager.getEpics().containsKey(id)) {
                            taskManager.removeEpic(id);
                            exchange.sendResponseHeaders(200, 0);
                            exchange.close();
                        } else {
                            sendNotFound(exchange, id);
                        }
                    } else {
                        String text = ("id эпика должен быть числом.");
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
