import com.sun.net.httpserver.HttpServer;
import managers.*;
import taskclasses.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Paths;
import java.time.format.DateTimeParseException;
import java.util.Scanner;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.Duration;

public class Main {
    public static void main(String[] args) throws IOException {
        TaskManager taskManager = FileBackedTaskManager.loadFromFile(Paths.get("backedListOfTasks.txt").toFile());
        Scanner scanner = new Scanner(System.in);
        boolean isWorking = true;

        while (isWorking) {
            printMenu();
            int cmd = scanCommand(scanner);
            switch (cmd) {
                case 1:
                    System.out.println("Выберите тип задачи: 1 - Задача, 2 - Эпик, 3 - Подзадача");
                    int taskType = scanTaskType(scanner);
                    switch (taskType) {
                        case 1:
                            taskManager.addNewTask(scanTask(scanner));
                            break;
                        case 2:
                            taskManager.addNewEpic(scanEpic(scanner));
                            break;
                        case 3:
                            taskManager.addNewSubtask(scanSubtask(scanner, taskManager));
                            break;
                    }
                    break;
                case 2:
                    if (taskManager.getTasks().isEmpty() &&
                            taskManager.getEpics().isEmpty() &&
                            taskManager.getSubtasks().isEmpty()) {
                        System.out.println("Список всех задач пуст.");
                        break;
                    }
                    System.out.print("Введите идентификатор задачи, которую хотите обновить: ");
                    int id = scanID(scanner, taskManager);
                    updateTask(id, taskManager, scanner);
                    break;
                case 3:
                    if (taskManager.getTasks().isEmpty() &&
                            taskManager.getEpics().isEmpty() &&
                            taskManager.getSubtasks().isEmpty()) {
                        System.out.println("Список всех задач пуст.");
                        break;
                    }
                    System.out.print("Введите идентификатор: ");
                    id = scanID(scanner, taskManager);
                    if (taskManager.getTasks().containsKey(id)) {
                        System.out.println(taskManager.getTask(id));
                    } else if (taskManager.getEpics().containsKey(id)) {
                        System.out.println(taskManager.getEpic(id));
                    } else if (taskManager.getSubtasks().containsKey(id)) {
                        System.out.println(taskManager.getSubtask(id));
                    }

                    break;
                case 4:
                    if (taskManager.getTasks().isEmpty() &&
                            taskManager.getEpics().isEmpty() &&
                            taskManager.getSubtasks().isEmpty()) {
                        System.out.println("Список всех задач пуст.");
                        break;
                    }
                    System.out.print("Введите идентификатор: ");
                    id = scanID(scanner, taskManager);
                    if (taskManager.getTasks().containsKey(id)) {
                        taskManager.removeTask(id);
                    } else if (taskManager.getEpics().containsKey(id)) {
                        taskManager.removeEpic(id);
                    } else if (taskManager.getSubtasks().containsKey(id)) {
                        taskManager.removeSubtask(id);
                    }
                    break;
                case 5:
                    if (taskManager.getTasks().isEmpty() &&
                            taskManager.getEpics().isEmpty() &&
                            taskManager.getSubtasks().isEmpty()) {
                        System.out.println("Список всех задач пуст.");
                        break;
                    }
                    System.out.println("Выберите тип задач: 1 - Задачи, 2 - Эпики, 3 - Подзадачи");
                    taskType = scanTaskType(scanner);
                    switch (taskType) {
                        case 1:
                            if (taskManager.getTasks().isEmpty()) {
                                System.out.println("Список задач пуст.");
                                break;
                            }
                            System.out.println(taskManager.getTasks());
                            break;
                        case 2:
                            if (taskManager.getEpics().isEmpty()) {
                                System.out.println("Список эпиков пуст.");
                                break;
                            }
                            System.out.println(taskManager.getEpics());
                            break;
                        case 3:
                            if (taskManager.getSubtasks().isEmpty()) {
                                System.out.println("Список подзадач пуст.");
                                break;
                            }
                            System.out.println(taskManager.getSubtasks());
                            break;
                    }

                    break;
                case 6:
                    if (taskManager.getTasks().isEmpty() &&
                            taskManager.getEpics().isEmpty() &&
                            taskManager.getSubtasks().isEmpty()) {
                        System.out.println("Список всех задач пуст.");
                        break;
                    }
                    taskManager.removeAllTasks();
                    break;
                case 7:
                    if (taskManager.getEpics().isEmpty()) {
                        System.out.println("Список эпиков пуст.");
                        break;
                    } else {
                        System.out.print("Введите идентификатор: ");
                        id = scanID(scanner, taskManager);
                        System.out.println(taskManager.getSubtasksForEpic(id));
                    }
                    break;
                case 8:
                    if (taskManager.getHistory().isEmpty()) {
                        System.out.println("История просмотров пуста.");
                        break;
                    } else {
                        System.out.println(taskManager.getHistory());
                    }
                    break;
                case 9:
                    if (taskManager.getPrioritizedTasks().isEmpty()) {
                        System.out.println("Список задач в порядке приоритета пуст.");
                        break;
                    } else {
                        System.out.println(taskManager.getPrioritizedTasks());
                    }
                    break;
                case 0:
                    isWorking = false;
                    break;
            }
            printIndent();
        }
    }

    public static Task scanTask(Scanner scanner) {
        System.out.println("Введите название задачи.");
        String name = scanString(scanner);
        System.out.println("Введите описание задачи.");
        String description = scanString(scanner);
        State state = scanState(scanner);
        System.out.println("Введите время и дату старта задачи в формате:\nHH:mm dd.MM.yy");
        LocalDateTime startTime = scanDateTime(scanner);
        System.out.println("Введите длительность задачи в минутах.");
        Duration duration = scanDuration(scanner);
        return new Task(name, description, state,startTime, duration);
    }

    public static  Epic scanEpic(Scanner scanner) {
        System.out.println("Введите название эпика.");
        String name = scanString(scanner);
        System.out.println("Введите описание эпика.");
        String description = scanString(scanner);
        return new Epic(name, description);
    }

    public static Subtask scanSubtask(Scanner scanner, TaskManager inMemoryTaskManager) {
        System.out.println("Введите id эпика, к которому относится подзадача.");
        int parentEpicId;
        while (true) {
        parentEpicId = scanID(scanner, inMemoryTaskManager);
        if (!inMemoryTaskManager.getEpics().containsKey(parentEpicId)) {
                System.out.println("Эпика с id=" + parentEpicId + " нет. Попробуйте еще раз.");
            } else {
                break;
            }
        }
        System.out.println("Выбран " + inMemoryTaskManager.getEpics().get(parentEpicId));
        System.out.println("Введите название подзадачи.");
        String name = scanString(scanner);
        System.out.println("Введите описание подзадачи.");
        String description = scanString(scanner);
        State state = scanState(scanner);
        System.out.println("Введите время и дату старта задачи в формате:\nHH:mm dd.MM.yy");
        LocalDateTime startTime = scanDateTime(scanner);
        System.out.println("Введите длительность задачи в минутах.");
        Duration duration = scanDuration(scanner);
        return new Subtask(name, description, state, parentEpicId, startTime, duration);
    }

    public static void updateTask(int id, TaskManager inMemoryTaskManager, Scanner scanner) {
        if (inMemoryTaskManager.getTasks().containsKey(id)) {
            Task task = scanTask(scanner);
            task.setId(id);
            inMemoryTaskManager.updateTask(task);
        } else if (inMemoryTaskManager.getEpics().containsKey(id)) {
            Epic epic = scanEpic(scanner);
            epic.setId(id);
            inMemoryTaskManager.updateEpic(epic);
        } else if (inMemoryTaskManager.getSubtasks().containsKey(id)) {
            Subtask updatedSubtask = scanSubtask(scanner, inMemoryTaskManager);
            updatedSubtask.setId(id);
            inMemoryTaskManager.updateSubtask(updatedSubtask);
        }
    }

    public static int scanCommand(Scanner scanner) {
        while (true) {
        int cmd = scanNumber(scanner);
            if (cmd < 0 || cmd > 9) {
                System.out.println("Команды " + cmd + " нет. Попробуйте еще раз.");
            } else {
                return cmd;
            }
        }
    }

    public static int scanTaskType(Scanner scanner) {
        while (true) {
            int taskType = scanNumber(scanner);
            if (taskType < 1 || taskType > 3) {
                System.out.println("Типа задачи " + taskType + " нет. Попробуйте еще раз.");
            } else {
                return taskType;
            }
        }
    }

    public static int scanID(Scanner scanner, TaskManager inMemoryTaskManager) {
        while (true) {
            int id = scanNumber(scanner);
            if (!inMemoryTaskManager.getTasks().containsKey(id) &&
                    !inMemoryTaskManager.getEpics().containsKey(id) &&
                    !inMemoryTaskManager.getSubtasks().containsKey(id)) {
                System.out.println("ID задачи " + id + " нет. Попробуйте еще раз.");
            } else {
                return id;
            }
        }
    }

    public static int scanNumber(Scanner scanner) {
        int exceptionTest = 0;
        boolean needToScan = true;
            while (needToScan) {
                String id = scanner.nextLine();
                try {
                    exceptionTest = Integer.parseInt(id);
                    needToScan = false;
                } catch (NumberFormatException ignored) {
                    System.out.println("Необходимо ввести число.");
                }
            }
        return exceptionTest;
    }

    public static LocalDateTime scanDateTime(Scanner scanner) {
        while (true) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd.MM.yy");
            String stringDateTime = scanner.nextLine();
            try {
                LocalDateTime localDateTime = LocalDateTime.parse(stringDateTime, formatter);
                return localDateTime;
            } catch (DateTimeParseException e) {
                System.out.println("Необходимо дату и время в формате:\nHH:mm dd.MM.yy");
            }
        }
    }

    public static Duration scanDuration(Scanner scanner) {
        int durationInt = scanNumber(scanner);
        return Duration.ofMinutes(durationInt);
    }

    public static String scanString(Scanner scanner) {
        while (true) {
            String string = scanner.nextLine();
            if (string.isEmpty()) {
                System.out.println("Вы ничего не ввели. Попробуйте еще раз.");
            } else {
                return string;
            }
        }
    }

    public static State scanState(Scanner scanner) {
        while (true) {
            System.out.println("Введите состояние задачи (NEW / IN_PROGRESS / DONE).");
            String state = scanner.nextLine();
            if (!(state.equals("NEW") || state.equals("IN_PROGRESS") || state.equals("DONE"))) {
                System.out.println("Ошибка при вводе состояния задачи. Попробуйте еще раз.");
            } else {
                return State.valueOf(state);
            }
        }
    }

    public static void printMenu() {
        System.out.println("Выберите команду:");
        System.out.println("1. Создать задачу.");
        System.out.println("2. Обновить задачу.");
        System.out.println("3. Получить задачу по идентификатору.");
        System.out.println("4. Удалить задачу по идентификатору.");
        System.out.println("5. Получить список всех задач (по типу).");
        System.out.println("6. Удалить все задачи.");
        System.out.println("7. Получить список подзадач эпика по идентификатору.");
        System.out.println("8. Получить историю просмотренных задач.");
        System.out.println("9. Получить список задач в порядке приоритета.");
        System.out.println("0. Выход.");
    }

    public static void printIndent() {
        System.out.println();
        System.out.println("-".repeat(20));
        System.out.println();
    }
}
