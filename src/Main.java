import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();
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
                            taskManager.addNewTask(createNewTask(scanner,taskManager));
                            break;
                        case 2:
                            taskManager.addNewEpic(createNewEpic(scanner, taskManager));
                            break;
                        case 3:
                            taskManager.addNewSubtask(createNewSubtask(scanner, taskManager));
                            break;
                    }
                    break;
                case 2:
                    if (taskManager.tasks.isEmpty()) {
                        System.out.println("Список задач пуст.");
                        break;
                    }
                    System.out.print("Введите идентификатор задачи, которую хотите обновить: ");
                    int id = scanID(scanner, taskManager);
                    updateTask(id, taskManager, scanner);
                    break;
                case 3:
                    if (taskManager.tasks.isEmpty()) {
                        System.out.println("Список задач пуст.");
                        break;
                    }
                    System.out.print("Введите идентификатор: ");
                    id = scanID(scanner, taskManager);
                    System.out.println(taskManager.getTask(id));
                    break;
                case 4:
                    if (taskManager.tasks.isEmpty()) {
                        System.out.println("Список задач пуст.");
                        break;
                    }
                    System.out.print("Введите идентификатор: ");
                    id = scanID(scanner, taskManager);
                    System.out.println(taskManager.removeTask(id));
                    break;
                case 5:
                    if (taskManager.tasks.isEmpty()) {
                        System.out.println("Список задач пуст.");
                        break;
                    }
                    System.out.println(taskManager.getAllTasks());
                    break;
                case 6:
                    if (taskManager.tasks.isEmpty()) {
                        System.out.println("Список задач пуст.");
                        break;
                    }
                    System.out.println(taskManager.removeAllTasks());
                    break;
                case 7:
                    if (taskManager.tasks.isEmpty()) {
                        System.out.println("Список задач пуст.");
                        break;
                    }
                    if (containsEpic(taskManager)) {
                        System.out.print("Введите идентификатор: ");
                        id = scanID(scanner, taskManager);
                        System.out.println(taskManager.getSubtasksForEpic(id));
                    } else {
                        System.out.println("В списке задач еще нет ни одного эпика.");
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

    public static Task createNewTask(Scanner scanner, TaskManager taskManager) {
        Task task = scanTask(scanner);
        int id = taskManager.generateID();
        task.id = id;
        System.out.println("Присвоен id="+id);
        return task;
    }

    public static  Epic scanEpic (Scanner scanner) {
        System.out.println("Введите название эпика.");
        String name = scanString(scanner);
        System.out.println("Введите описание эпика.");
        String description = scanString(scanner);
        return new Epic (name, description);
    }

    public static Epic createNewEpic(Scanner scanner, TaskManager taskManager) {
        Epic epic = scanEpic(scanner);
        int id = taskManager.generateID();
        epic.id = id;
        System.out.println("Присвоен id="+id);
        return epic;
    }

    public static Subtask scanSubtask(Scanner scanner, TaskManager taskManager) {
        System.out.println("Введите id эпика, к которому относится подзадача.");
        int parentEpicId;
        while (true) {
        parentEpicId = scanID(scanner, taskManager);
        if (taskManager.tasks.get(parentEpicId).getClass() != Epic.class) {
                System.out.println("Эпика с id=" + parentEpicId + " нет. Попробуйте еще раз.");
            } else {
                break;
            }
        }
        System.out.println("Выбран "+taskManager.tasks.get(parentEpicId));
        System.out.println("Введите название подзадачи.");
        String name = scanString(scanner);
        System.out.println("Введите описание подзадачи.");
        String description = scanString(scanner);
        State state = scanState(scanner);
        return new Subtask(name, description, state, parentEpicId);
    }

    public static Subtask createNewSubtask(Scanner scanner, TaskManager taskManager) {
        Subtask subtask = scanSubtask(scanner, taskManager);
        int id = taskManager.generateID();
        subtask.id = id;
        System.out.println("Присвоен id="+id);
        return subtask;
    }

    public static void updateTask(int id,TaskManager taskManager, Scanner scanner) {
        if (taskManager.tasks.get(id).getClass() == Task.class) {
            Task task = scanTask(scanner);
            taskManager.updateTask(id, task);
        } else if (taskManager.tasks.get(id).getClass() == Epic.class) {
            Epic epic = scanEpic(scanner);
            taskManager.updateTask(id, epic);
        } else if (taskManager.tasks.get(id).getClass() == Subtask.class) {
            Subtask subtask = scanSubtask(scanner, taskManager);
            taskManager.updateSubtask(id, subtask);
            Epic parentEpic = (Epic) taskManager.tasks.get(subtask.parentEpicID);
            taskManager.checkEpicState(parentEpic);
        }
    }

    public static boolean containsEpic(TaskManager taskManager) {
        ArrayList<Boolean> isEpicList = new ArrayList<>();
        for (Task task : taskManager.tasks.values()) {
            if (task.getClass() == Epic.class) {
                isEpicList.add(true);
            } else {
                isEpicList.add(false);
            }
        }
        return isEpicList.contains(true);
    }

    public static int scanCommand(Scanner scanner) {
        while (true) {
        int cmd = scanNumber(scanner);
            if (cmd<0 || cmd>7) {
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

    public static int scanID(Scanner scanner, TaskManager taskManager) {
        while (true) {
            int id = scanNumber(scanner);
            if (!taskManager.tasks.containsKey(id)) {
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
        System.out.println("5. Получить список всех задач.");
        System.out.println("6. Удалить все задачи.");
        System.out.println("7. Получить список подзадач эпика по идентификатору.");
        System.out.println("0. Выход.");
    }

    public static void printIndent() {
        System.out.println();
        System.out.println("-".repeat(20));
        System.out.println();
    }
}
