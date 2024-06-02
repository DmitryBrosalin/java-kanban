import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        InMemoryTaskManager inMemoryTaskManager = (InMemoryTaskManager) Managers.getDefault();
        InMemoryHistoryManager inMemoryHistoryManager = (InMemoryHistoryManager) Managers.getDefaultHistory();
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
                            inMemoryTaskManager.addNewTask(scanTask(scanner));
                            break;
                        case 2:
                            inMemoryTaskManager.addNewEpic(scanEpic(scanner));
                            break;
                        case 3:
                            inMemoryTaskManager.addNewSubtask(scanSubtask(scanner, inMemoryTaskManager));
                            break;
                    }
                    break;
                case 2:
                    if (inMemoryTaskManager.getTasks().isEmpty() &&
                            inMemoryTaskManager.getEpics().isEmpty() &&
                            inMemoryTaskManager.getSubtasks().isEmpty()) {
                        System.out.println("Список всех задач пуст.");
                        break;
                    }
                    System.out.print("Введите идентификатор задачи, которую хотите обновить: ");
                    int id = scanID(scanner, inMemoryTaskManager);
                    updateTask(id, inMemoryTaskManager, scanner);
                    break;
                case 3:
                    if (inMemoryTaskManager.getTasks().isEmpty() &&
                            inMemoryTaskManager.getEpics().isEmpty() &&
                            inMemoryTaskManager.getSubtasks().isEmpty()) {
                        System.out.println("Список всех задач пуст.");
                        break;
                    }
                    System.out.print("Введите идентификатор: ");
                    id = scanID(scanner, inMemoryTaskManager);
                    if (inMemoryTaskManager.getTasks().containsKey(id)) {
                        System.out.println(inMemoryTaskManager.getTask(id));
                        inMemoryHistoryManager.addToHistory(inMemoryTaskManager.getTask(id));
                    } else if (inMemoryTaskManager.getEpics().containsKey(id)) {
                        System.out.println(inMemoryTaskManager.getEpic(id));
                        inMemoryHistoryManager.addToHistory(inMemoryTaskManager.getEpic(id));
                    } else if (inMemoryTaskManager.getSubtasks().containsKey(id)) {
                        System.out.println(inMemoryTaskManager.getSubtask(id));
                        inMemoryHistoryManager.addToHistory(inMemoryTaskManager.getSubtask(id));
                    }

                    break;
                case 4:
                    if (inMemoryTaskManager.getTasks().isEmpty() &&
                            inMemoryTaskManager.getEpics().isEmpty() &&
                            inMemoryTaskManager.getSubtasks().isEmpty()) {
                        System.out.println("Список всех задач пуст.");
                        break;
                    }
                    System.out.print("Введите идентификатор: ");
                    id = scanID(scanner, inMemoryTaskManager);
                    if (inMemoryTaskManager.getTasks().containsKey(id)) {
                        inMemoryTaskManager.removeTask(id);
                    } else if (inMemoryTaskManager.getEpics().containsKey(id)) {
                        inMemoryTaskManager.removeEpic(id);
                    } else if (inMemoryTaskManager.getSubtasks().containsKey(id)) {
                        inMemoryTaskManager.removeSubtask(id);
                    }
                    break;
                case 5:
                    if (inMemoryTaskManager.getTasks().isEmpty() &&
                            inMemoryTaskManager.getEpics().isEmpty() &&
                            inMemoryTaskManager.getSubtasks().isEmpty()) {
                        System.out.println("Список всех задач пуст.");
                        break;
                    }
                    System.out.println("Выберите тип задач: 1 - Задачи, 2 - Эпики, 3 - Подзадачи");
                    taskType = scanTaskType(scanner);
                    switch (taskType) {
                        case 1:
                            if (inMemoryTaskManager.getTasks().isEmpty()) {
                                System.out.println("Список задач пуст.");
                                break;
                            }
                            System.out.println(inMemoryTaskManager.getTasks());
                            break;
                        case 2:
                            if (inMemoryTaskManager.getEpics().isEmpty()) {
                                System.out.println("Список эпиков пуст.");
                                break;
                            }
                            System.out.println(inMemoryTaskManager.getEpics());
                            break;
                        case 3:
                            if (inMemoryTaskManager.getSubtasks().isEmpty()) {
                                System.out.println("Список подзадач пуст.");
                                break;
                            }
                            System.out.println(inMemoryTaskManager.getSubtasks());
                            break;
                    }

                    break;
                case 6:
                    if (inMemoryTaskManager.getTasks().isEmpty() &&
                            inMemoryTaskManager.getEpics().isEmpty() &&
                            inMemoryTaskManager.getSubtasks().isEmpty()) {
                        System.out.println("Список всех задач пуст.");
                        break;
                    }
                    inMemoryTaskManager.removeAllTasks();
                    break;
                case 7:
                    if (inMemoryTaskManager.getEpics().isEmpty()) {
                        System.out.println("Список эпиков пуст.");
                        break;
                    } else {
                        System.out.print("Введите идентификатор: ");
                        id = scanID(scanner, inMemoryTaskManager);
                        System.out.println(inMemoryTaskManager.getSubtasksForEpic(id));
                    }
                    break;
                case 8:
                    if (inMemoryHistoryManager.history.isEmpty()) {
                        System.out.println("История просмотров пуста.");
                        break;
                    } else {
                        System.out.println(inMemoryTaskManager.getHistory(inMemoryHistoryManager));
                    }
                    break;
                case 0:
                    isWorking = false;
                    break;
            }
            printIndent();
        }
    }

    public static Task scanTask (Scanner scanner) {
        System.out.println("Введите название задачи.");
        String name = scanString(scanner);
        System.out.println("Введите описание задачи.");
        String description = scanString(scanner);
        State state = scanState(scanner);
        return new Task(name, description, state);
    }

    public static  Epic scanEpic (Scanner scanner) {
        System.out.println("Введите название эпика.");
        String name = scanString(scanner);
        System.out.println("Введите описание эпика.");
        String description = scanString(scanner);
        return new Epic (name, description);
    }

    public static Subtask scanSubtask(Scanner scanner, InMemoryTaskManager inMemoryTaskManager) {
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
        System.out.println("Выбран "+ inMemoryTaskManager.getEpics().get(parentEpicId));
        System.out.println("Введите название подзадачи.");
        String name = scanString(scanner);
        System.out.println("Введите описание подзадачи.");
        String description = scanString(scanner);
        State state = scanState(scanner);
        return new Subtask(name, description, state, parentEpicId);
    }

    public static void updateTask(int id, InMemoryTaskManager inMemoryTaskManager, Scanner scanner) {
        if (inMemoryTaskManager.getTasks().containsKey(id)) {
            Task task = scanTask(scanner);
            task.id = id;
            inMemoryTaskManager.updateTask(task);
        } else if (inMemoryTaskManager.getEpics().containsKey(id)) {
            Epic epic = scanEpic(scanner);
            epic.id = id;
            inMemoryTaskManager.updateEpic(epic);
        } else if (inMemoryTaskManager.getSubtasks().containsKey(id)) {
            Subtask updatedSubtask = scanSubtask(scanner, inMemoryTaskManager);
            updatedSubtask.id = id;
            inMemoryTaskManager.updateSubtask(updatedSubtask);
        }
    }

    public static int scanCommand(Scanner scanner) {
        while (true) {
        int cmd = scanNumber(scanner);
            if (cmd<0 || cmd>8) {
                System.out.println("Команды " + cmd + " нет. Попробуйте еще раз.");
            } else {
                return cmd;
            }
        }
    }

    public static int scanTaskType(Scanner scanner) {
        while (true) {
            int taskType = scanNumber(scanner);
            if (taskType<1 || taskType>3) {
                System.out.println("Типа задачи " + taskType + " нет. Попробуйте еще раз.");
            } else {
                return taskType;
            }
        }
    }

    public static int scanID(Scanner scanner, InMemoryTaskManager inMemoryTaskManager) {
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
        System.out.println("0. Выход.");
    }

    public static void printIndent() {
        System.out.println();
        System.out.println("-".repeat(20));
        System.out.println();
    }
}
