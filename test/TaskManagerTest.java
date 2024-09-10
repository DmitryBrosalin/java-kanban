import exceptions.TimeConflictException;
import managers.TaskManager;
import taskclasses.Epic;
import taskclasses.State;
import taskclasses.Subtask;
import taskclasses.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

abstract class TaskManagerTest {
    void addNewTaskAndCheckTimeConflictAndGetTaskAndGetTasks(TaskManager taskManager) {
        Task task1 = new Task("Test addNewTask1", "Test addNewTask1 description", 1, State.NEW,
                LocalDateTime.of(2024, 8, 15, 15, 0), Duration.ofMinutes(30));
        Task task2 = new Task("Test addNewTask2", "Test addNewTask2 description", 2, State.DONE,
                LocalDateTime.of(2024, 8, 15, 15, 10), Duration.ofMinutes(30));
        Task task3 = new Task("Test addNewTask3", "Test addNewTask3 description", 2, State.IN_PROGRESS,
                LocalDateTime.of(2024, 8, 15, 16, 0), Duration.ofMinutes(30));
        Map<Integer, Task> tasks = new HashMap<>();
        tasks.put(task1.getId(), task1);
        tasks.put(task3.getId(), task3);
        taskManager.addNewTask(task1);
        try {
            taskManager.addNewTask(task2);
        } catch (TimeConflictException e) {

        }
        try {
            taskManager.addNewTask(task3);
        } catch (TimeConflictException e) {

        }
        Map<Integer, Task> savedTasks = taskManager.getTasks();
        Task savedTask1 = taskManager.getTask(task1.getId());
        assertNotNull(savedTask1, "Ошибка при сохранении или возврате задачи");
        assertEquals(savedTask1, task1, "Задача сохранена некорректно");
        assertEquals(savedTasks, tasks, "Некорректный возврат списка задач");
        assertFalse(savedTasks.containsValue(task2), "Ошибка при расчете конфликта задач по времени");
    }

    void addNewEpicAndGetEpicAndGetEpics(TaskManager taskManager) {
        Epic epic1 = new Epic("Test Epic1", "Test Epic1 description", 1, State.NEW);
        Epic epic2 = new Epic("Test Epic2", "Test Epic2 description", 2, State.NEW);
        Map<Integer, Epic> epics = new HashMap<>();
        epics.put(epic1.getId(), epic1);
        epics.put(epic2.getId(), epic2);
        taskManager.addNewEpic(epic1);
        taskManager.addNewEpic(epic2);
        Map<Integer, Epic> savedEpics = taskManager.getEpics();
        Epic savedEpic1 = taskManager.getEpic(epic1.getId());
        assertNotNull(savedEpic1, "Ошибка при сохранении или возврате эпика");
        assertEquals(savedEpic1, epic1, "Эпик сохранен некорректно");
        assertEquals(savedEpics, epics, "Некорректный возврат списка эпиков");
    }

    void addNewSubtaskAndGetSubtaskAndGetSubtasks(TaskManager taskManager) {
        Epic parentEpic = new Epic("Test ParentEpic1", "Test ParentEpic1 Description", 1, State.NEW);
        Subtask subtask1 = new Subtask("Test addNewSubtask1", "Test addNewSubtask1 description", 2,
                State.DONE, 1, LocalDateTime.of(2024, 8, 15, 17, 0), Duration.ofMinutes(50));
        Subtask subtask2 = new Subtask("Test addNewSubtask2", "Test addNewSubtask2 description", 3,
                State.DONE, 1, LocalDateTime.of(2024, 8, 15, 18, 0), Duration.ofMinutes(20));
        Map<Integer, Subtask> subtasks = new HashMap<>();
        subtasks.put(subtask1.getId(), subtask1);
        subtasks.put(subtask2.getId(), subtask2);
        taskManager.addNewEpic(parentEpic);
        taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask2);
        Subtask savedSubtask1 = taskManager.getSubtask(subtask1.getId());
        Subtask savedSubtask2 = taskManager.getSubtask(subtask2.getId());
        Map<Integer, Subtask> savedSubtasks = taskManager.getSubtasks();
        assertNotNull(savedSubtask1, "Ошибка при сохранении или возврате подзадачи");
        assertNotNull(savedSubtask2, "Ошибка при сохранении или возврате подзадачи");
        assertEquals(subtask1, savedSubtask1, "Подзадача сохранена некорректно");
        assertEquals(subtask2, savedSubtask2, "Подзадача сохранена некорректно");
        assertEquals(subtasks, savedSubtasks, "Некорректный возврат списка подзадач");
    }

    void removeTask(TaskManager taskManager) {
        Task task = new Task("Test addNewTask1", "Test addNewTask1 description", 1, State.NEW,
                LocalDateTime.of(2024, 8, 15, 15, 0), Duration.ofMinutes(30));
        taskManager.addNewTask(task);
        assertNotEquals(new HashMap<Integer, Task>(), taskManager.getTasks(), "Задача не была добавлена.");
        taskManager.removeTask(task.getId());
        assertTrue(taskManager.getTasks().isEmpty(), "Задача не была удалена.");
    }

    void removeEpicWithNoSubtasks(TaskManager taskManager) {
        Epic epic = new Epic("Test Epic1", "Test Epic1 description", 1, State.NEW);
        taskManager.addNewEpic(epic);
        assertNotEquals(new HashMap<Integer, Epic>(), taskManager.getEpics(), "Эпик не был добавлен.");
        taskManager.removeEpic(epic.getId());
        assertTrue(taskManager.getEpics().isEmpty(), "Эпик не был удален.");
    }

    void removeEpicWithSubtask(TaskManager taskManager) {
        Epic parentEpic = new Epic("Test ParentEpic1", "Test ParentEpic1 Description", 1, State.NEW);
        Subtask subtask1 = new Subtask("Test addNewSubtask1", "Test addNewSubtask1 description", 2,
                State.DONE, 1, LocalDateTime.of(2024, 8, 15, 17, 0), Duration.ofMinutes(50));
        taskManager.addNewEpic(parentEpic);
        taskManager.addNewSubtask(subtask1);
        assertNotEquals(new HashMap<Integer, Epic>(), taskManager.getEpics(), "Эпик не был добавлен.");
        assertNotEquals(new HashMap<Integer, Subtask>(), taskManager.getSubtasks(), "Подзадача не была добавлена.");
        taskManager.removeEpic(parentEpic.getId());
        assertTrue(taskManager.getEpics().isEmpty(), "Эпик не был удален.");
        assertTrue(taskManager.getSubtasks().isEmpty(), "При удалении эпика не была удалена его подзадача");
    }

    void removeSubtask(TaskManager taskManager) {
        Epic parentEpic = new Epic("Test ParentEpic1", "Test ParentEpic1 Description", 1, State.NEW);
        Subtask subtask1 = new Subtask("Test addNewSubtask1", "Test addNewSubtask1 description", 2,
                State.DONE, 1, LocalDateTime.of(2024, 8, 15, 17, 0), Duration.ofMinutes(50));
        taskManager.addNewEpic(parentEpic);
        taskManager.addNewSubtask(subtask1);
        assertNotEquals(new HashMap<Integer, Epic>(), taskManager.getEpics(), "Эпик не был добавлен.");
        assertNotEquals(new HashMap<Integer, Subtask>(), taskManager.getSubtasks(), "Подзадача не была добавлена.");
        taskManager.removeSubtask(subtask1.getId());
        assertFalse(taskManager.getEpics().isEmpty(), "Родительский эпик был удален вместе с подзадачей.");
        assertTrue(taskManager.getSubtasks().isEmpty(), "Ошибка при удалении подзадачи.");
    }

    void removeAllTasks(TaskManager taskManager) {
        Task task = new Task("Test addNewTask1", "Test addNewTask1 description", 1, State.NEW,
                LocalDateTime.of(2024, 8, 15, 15, 0), Duration.ofMinutes(30));
        Epic parentEpic = new Epic("Test ParentEpic1", "Test ParentEpic1 Description", 2, State.NEW);
        Subtask subtask = new Subtask("Test addNewSubtask1", "Test addNewSubtask1 description", 3,
                State.DONE, 2, LocalDateTime.of(2024, 8, 15, 17, 0), Duration.ofMinutes(50));
        taskManager.addNewTask(task);
        taskManager.addNewEpic(parentEpic);
        taskManager.addNewSubtask(subtask);
        assertNotEquals(new HashMap<Integer, Epic>(), taskManager.getEpics(), "Эпик не был добавлен.");
        assertNotEquals(new HashMap<Integer, Subtask>(), taskManager.getSubtasks(), "Подзадача не была добавлена.");
        assertNotEquals(new HashMap<Integer, Task>(), taskManager.getTasks(), "Задача не была добавлена.");
        taskManager.removeAllTasks();
        assertTrue(taskManager.getTasks().isEmpty(), "Задача не была удалена.");
        assertTrue(taskManager.getEpics().isEmpty(), "Эпик не был удален.");
        assertTrue(taskManager.getSubtasks().isEmpty(), "Подзадача не была удалена.");
    }

    void getSubtasksForEpic(TaskManager taskManager) {
        Epic parentEpic = new Epic("Test ParentEpic1", "Test ParentEpic1 Description", 1, State.NEW);
        Subtask subtask1 = new Subtask("Test addNewSubtask1", "Test addNewSubtask1 description", 2,
                State.DONE, 1, LocalDateTime.of(2024, 8, 15, 17, 0), Duration.ofMinutes(50));
        Subtask subtask2 = new Subtask("Test addNewSubtask2", "Test addNewSubtask2 description", 3,
                State.DONE, 1, LocalDateTime.of(2024, 8, 15, 18, 0), Duration.ofMinutes(20));
        taskManager.addNewEpic(parentEpic);
        taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask2);
        List<Subtask> subtasks = new ArrayList<>();
        subtasks.add(subtask1);
        subtasks.add(subtask2);
        List<Subtask> returnedSubtasksForEpic = taskManager.getSubtasksForEpic(parentEpic.getId());
        assertNotNull(returnedSubtasksForEpic, "Не удалось получить список подзадач для эпика");
        assertEquals(subtasks, returnedSubtasksForEpic, "Ошибка при получении списка задач для эпика");
    }

    void updateTask(TaskManager taskManager) {
        Task task = new Task("Test addNewTask1", "Test addNewTask1 description", 1, State.NEW,
                LocalDateTime.of(2024, 8, 15, 15, 0), Duration.ofMinutes(30));
        taskManager.addNewTask(task);
        Task updatedTask = new Task("Test updatedTask", "Test updatedTask description",
                task.getId(), State.DONE, LocalDateTime.of(2024, 8, 15, 16, 0),
                Duration.ofMinutes(40));
        taskManager.updateTask(updatedTask);
        Task savedUpdatedTask = taskManager.getTask(updatedTask.getId());
        assertNotNull(savedUpdatedTask, "Задача не найдена.");
        Map<Integer, Task> tasks = taskManager.getTasks();
        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(updatedTask, savedUpdatedTask, "Обновленные задачи не совпадают.");
        assertNotEquals(task, savedUpdatedTask, "Задача не обновилась.");
    }

    void updateEpicWithNoSubtasks(TaskManager taskManager) {
        Epic epic = new Epic("Test newEpic", "Test newEpic description", 1, State.NEW);
        taskManager.addNewEpic(epic);
        Epic updatedEpic = new Epic("Test updatedTask", "Test updatedTask description", epic.getId(), epic.getState());
        taskManager.updateEpic(updatedEpic);
        Task savedUpdatedEpic = taskManager.getEpic(updatedEpic.getId());
        assertNotNull(savedUpdatedEpic, "Задача не найдена.");
        Map<Integer, Epic> epics = taskManager.getEpics();
        assertNotNull(epics, "Задачи не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество задач.");
        assertEquals(updatedEpic, savedUpdatedEpic, "Обновленные задачи не совпадают.");
        assertNotEquals(epic, savedUpdatedEpic, "Задача не обновилась.");
    }

    void updateEpicWithSubtasks(TaskManager taskManager) {
        Epic epic = new Epic("Test newEpic", "Test newEpic description", 1, State.NEW);
        Subtask subtask1 = new Subtask("Test addNewSubtask1", "Test addNewSubtask1 description", 2,
                State.DONE, 1, LocalDateTime.of(2024, 8, 15, 17, 0), Duration.ofMinutes(50));
        Subtask subtask2 = new Subtask("Test addNewSubtask2", "Test addNewSubtask2 description", 3,
                State.DONE, 1, LocalDateTime.of(2024, 8, 15, 18, 0), Duration.ofMinutes(20));
        taskManager.addNewEpic(epic);
        taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask2);
        Epic updatedEpic = new Epic("Test updatedTask", "Test updatedTask description", epic.getId(), epic.getState());
        taskManager.updateEpic(updatedEpic);
        Epic savedUpdatedEpic = taskManager.getEpic(updatedEpic.getId());
        assertNotNull(savedUpdatedEpic, "Задача не найдена.");
        Map<Integer, Epic> epics = taskManager.getEpics();
        assertNotNull(epics, "Задачи не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество задач.");
        assertEquals(updatedEpic, savedUpdatedEpic, "Обновленные задачи не совпадают.");
        assertNotEquals(epic, savedUpdatedEpic, "Задача не обновилась.");
    }

    void updateOneSubtaskForOneEpicAndCheckEpicStateAndTime(TaskManager taskManager) {
        Epic parentEpic = new Epic("Test parentEpic", "Test parentEpic description");
        Subtask subtask = new Subtask("Test newSubtask", "Test newSubtask description", State.NEW, 1,
                LocalDateTime.of(2024, 8, 15, 17, 0), Duration.ofMinutes(50));

        taskManager.addNewEpic(parentEpic);
        taskManager.addNewSubtask(subtask);
        Subtask updatedSubtask = new Subtask("Test updatedSubtask", "Test updatedSubtask description",
                subtask.getId(), State.DONE, subtask.getParentEpicID(),
                LocalDateTime.of(2024, 8, 15, 18, 0), Duration.ofMinutes(20));
        taskManager.updateSubtask(updatedSubtask);
        Subtask savedSubtask = taskManager.getSubtask(updatedSubtask.getId());

        assertNotNull(savedSubtask, "Задача не найдена.");
        assertEquals(updatedSubtask, savedSubtask, "Обновленные задачи не совпадают.");

        Map<Integer, Subtask> subtasks = taskManager.getSubtasks();

        assertNotNull(subtasks, "Задачи не возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество задач.");
        assertNotEquals(subtask, savedSubtask, "Задача не обновилась.");
        assertEquals(State.DONE, parentEpic.getState(), "Родительский эпик не обновился.");

        LocalDateTime expectedStartTimeOfEpic = LocalDateTime.of(2024, 8, 15, 18, 0);
        LocalDateTime expectedEndTimeOfEpic = LocalDateTime.of(2024, 8, 15, 18, 20);
        Duration expectedDurationOfEpic = Duration.ofMinutes(20);

        LocalDateTime savedStartTimeOfEpic = parentEpic.getStartTime();
        LocalDateTime savedEndTimeOfEpic = parentEpic.getEndTime();
        Duration savedDurationOfEpic = parentEpic.getDuration();

        assertEquals(expectedStartTimeOfEpic, savedStartTimeOfEpic, "Ошибка при расчете времени старта эпика");
        assertEquals(expectedEndTimeOfEpic, savedEndTimeOfEpic, "Ошибка при расчете времени завершения эпика");
        assertEquals(expectedDurationOfEpic, savedDurationOfEpic, "Ошибка при расчете длительности эпика");
    }

    void updateTwoSubtasksForOneEpicAndCheckEpicStateAndTime(TaskManager taskManager) {
        Epic parentEpic = new Epic("Test parentEpic", "Test parentEpic description");
        Subtask subtask1 = new Subtask("Test newSubtask1", "Test newSubtask1 description", State.NEW, 1,
                LocalDateTime.of(2024, 8, 15, 17, 0), Duration.ofMinutes(50));
        Subtask subtask2 = new Subtask("Test newSubtask2", "Test newSubtask2 description", State.NEW, 1,
                LocalDateTime.of(2024, 8, 15, 18, 0), Duration.ofMinutes(30));

        taskManager.addNewEpic(parentEpic);
        taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask2);

        Subtask updatedSubtask1 = new Subtask("Test updatedSubtask1", "Test updatedSubtask1 description",
                subtask1.getId(), State.DONE, subtask1.getParentEpicID(),
                LocalDateTime.of(2024, 8, 15, 17, 20), Duration.ofMinutes(30));
        taskManager.updateSubtask(updatedSubtask1);
        Subtask savedSubtask1 = taskManager.getSubtask(updatedSubtask1.getId());

        assertNotNull(savedSubtask1, "Задача не найдена.");

        Map<Integer, Subtask> subtasks = taskManager.getSubtasks();

        assertNotNull(subtasks, "Задачи не возвращаются.");
        assertEquals(2, subtasks.size(), "Неверное количество задач.");
        assertNotEquals(subtask1, savedSubtask1, "Задача не обновилась.");
        assertEquals(updatedSubtask1, savedSubtask1, "Задача обновилась некорректно.");
        assertEquals(State.IN_PROGRESS, parentEpic.getState(), "Родительский эпик не обновился или обновился некорректно.");

        Subtask updatedSubtask2 = new Subtask("Test updatedSubtask2", "Test updatedSubtask2 description",
                subtask2.getId(), State.DONE, subtask1.getParentEpicID(),
                LocalDateTime.of(2024, 8, 15, 16, 0), Duration.ofMinutes(40));
        taskManager.updateSubtask(updatedSubtask2);
        Subtask savedSubtask2 = taskManager.getSubtask(updatedSubtask2.getId());
        subtasks = taskManager.getSubtasks();
        assertNotNull(savedSubtask2, "Задача не найдена.");
        assertNotNull(subtasks, "Задачи не возвращаются.");
        assertEquals(2, subtasks.size(), "Неверное количество задач.");
        assertNotEquals(subtask2, savedSubtask2, "Задача не обновилась.");
        assertEquals(updatedSubtask2, savedSubtask2, "Задача обновилась некорректно.");
        assertEquals(State.DONE, parentEpic.getState(), "Родительский эпик не обновился или обновился некорректно.");

        LocalDateTime expectedStartTimeOfEpic = LocalDateTime.of(2024, 8, 15, 16, 0);
        LocalDateTime expectedEndTimeOfEpic = LocalDateTime.of(2024, 8, 15, 17, 50);
        Duration expectedDurationOfEpic = Duration.ofMinutes(70);

        LocalDateTime savedStartTimeOfEpic = parentEpic.getStartTime();
        LocalDateTime savedEndTimeOfEpic = parentEpic.getEndTime();
        Duration savedDurationOfEpic = parentEpic.getDuration();

        assertEquals(expectedStartTimeOfEpic, savedStartTimeOfEpic, "Ошибка при расчете времени старта эпика");
        assertEquals(expectedEndTimeOfEpic, savedEndTimeOfEpic, "Ошибка при расчете времени завершения эпика");
        assertEquals(expectedDurationOfEpic, savedDurationOfEpic, "Ошибка при расчете длительности эпика");
    }

    void updateOneSubtaskForTwoEpicsAndCheckEpicState(TaskManager taskManager) {
        Epic parentEpic1 = new Epic("Test parentEpic1", "Test parentEpic1 description");
        Epic parentEpic2 = new Epic("Test parentEpic2", "Test parentEpic2 description");
        Subtask subtask = new Subtask("Test newSubtask", "Test newSubtask description", State.DONE,
                1, LocalDateTime.of(2024, 8, 15, 17, 0),Duration.ofMinutes(50));
        taskManager.addNewEpic(parentEpic1);
        taskManager.addNewEpic(parentEpic2);
        taskManager.addNewSubtask(subtask);

        assertEquals(State.DONE, parentEpic1.getState(), "Родительский эпик не обновился.");

        Subtask updatedSubtask = new Subtask("Test updatedSubtask",
                "Test updatedSubtask description", subtask.getId(), State.DONE, parentEpic2.getId(),
                LocalDateTime.of(2024, 8, 15, 17, 0), Duration.ofMinutes(50));
        taskManager.updateSubtask(updatedSubtask);

        assertEquals(State.NEW, parentEpic1.getState(), "Старый родительский эпик не обновился.");
        assertEquals(State.DONE, parentEpic2.getState(), "Новый родительский эпик не обновился.");
    }

    void updateTwoSubtasksForTwoEpicsAndCheckEpicState(TaskManager taskManager) {
        Epic parentEpic1 = new Epic("Test parentEpic1", "Test parentEpic1 description");
        Epic parentEpic2 = new Epic("Test parentEpic2", "Test parentEpic2 description");
        Subtask subtask1 = new Subtask("Test newSubtask1", "Test newSubtask1 description", State.NEW,
                1, LocalDateTime.of(2024, 8, 15, 17, 0), Duration.ofMinutes(50));
        Subtask subtask2 = new Subtask("Test newSubtask2", "Test newSubtask2 description", State.DONE,
                1, LocalDateTime.of(2024, 8, 15, 18, 0), Duration.ofMinutes(50));
        taskManager.addNewEpic(parentEpic1);
        taskManager.addNewEpic(parentEpic2);
        taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask2);

        assertEquals(State.IN_PROGRESS, parentEpic1.getState(), "Родительский эпик не обновился.");

        Subtask updatedSubtask1 = new Subtask("Test updatedSubtask1",
                "Test updatedSubtask1 description", subtask1.getId(), State.IN_PROGRESS, parentEpic2.getId(),
                LocalDateTime.of(2024, 8, 15, 17, 0), Duration.ofMinutes(50));
        taskManager.updateSubtask(updatedSubtask1);

        assertEquals(State.DONE, parentEpic1.getState(), "Старый родительский эпик не обновился.");
        assertEquals(State.IN_PROGRESS, parentEpic2.getState(), "Новый родительский эпик не обновился.");

        Subtask updatedSubtask2 = new Subtask("Test updatedSubtask2",
                "Test updatedSubtask2 description", subtask2.getId(), State.DONE, parentEpic2.getId(),
                LocalDateTime.of(2024, 8, 15, 18, 0), Duration.ofMinutes(50));
        taskManager.updateSubtask(updatedSubtask2);

        assertEquals(State.NEW, parentEpic1.getState(), "Старый родительский эпик не обновился.");

        Subtask twiceUpdatedSubtask1 = new Subtask("Test twiceUpdatedSubtask1",
                "Test twiceUpdatedSubtask1 description", subtask1.getId(), State.DONE, parentEpic2.getId(),
                LocalDateTime.of(2024, 8, 15, 19, 0), Duration.ofMinutes(50));
        taskManager.updateSubtask(twiceUpdatedSubtask1);

        assertEquals(State.DONE, parentEpic2.getState(), "Новый родительский эпик не обновился.");
    }

    void getPrioritizedTasks(TaskManager taskManager) {
        Task task1 = new Task("Test addNewTask1", "Test addNewTask1 description", 1, State.NEW,
                LocalDateTime.of(2024, 8, 15, 15, 0), Duration.ofMinutes(30));
        Epic parentEpic = new Epic("Test ParentEpic1", "Test ParentEpic1 Description", 2, State.NEW);
        Subtask subtask = new Subtask("Test addNewSubtask1", "Test addNewSubtask1 description", 3,
                State.DONE, 2, LocalDateTime.of(2024, 8, 15, 17, 0), Duration.ofMinutes(50));
        Task task2 = new Task("Test addNewTask2", "Test addNewTask2 description", 4, State.NEW,
                LocalDateTime.of(2024, 8, 15, 16, 30), Duration.ofMinutes(10));
        taskManager.addNewTask(task1);
        taskManager.addNewEpic(parentEpic);
        taskManager.addNewSubtask(subtask);
        taskManager.addNewTask(task2);

        Set<Task> expectedPrioritizedTasks = new LinkedHashSet<>();
        expectedPrioritizedTasks.add(task1);
        expectedPrioritizedTasks.add(task2);
        expectedPrioritizedTasks.add(subtask);

        Set<Task> savedPrioritizedTasks = taskManager.getPrioritizedTasks();
        assertEquals(expectedPrioritizedTasks, savedPrioritizedTasks);
    }

    void checkTimeConflicts(TaskManager taskManager) {
        Task task1 = new Task("Test addNewTask1", "Test addNewTask1 description", State.NEW,
                LocalDateTime.of(2024, 8, 15, 15, 0), Duration.ofMinutes(60));
        Task task2 = new Task("Test addNewTask2", "Test addNewTask2 description", State.NEW,
                LocalDateTime.of(2024, 8, 15, 15, 40), Duration.ofMinutes(30));
        Task task3 = new Task("Test addNewTask3", "Test addNewTask3 description", State.NEW,
                LocalDateTime.of(2024, 8, 15, 15, 20), Duration.ofMinutes(30));
        Task task4 = new Task("Test addNewTask4", "Test addNewTask4 description", State.NEW,
                LocalDateTime.of(2024, 8, 15, 14, 50), Duration.ofMinutes(30));
        Task task5 = new Task("Test addNewTask5", "Test addNewTask5 description", State.NEW,
                LocalDateTime.of(2024, 8, 15, 14, 30), Duration.ofMinutes(120));
        Task task6 = new Task("Test addNewTask6", "Test addNewTask6 description", State.NEW,
                LocalDateTime.of(2024, 8, 15, 16, 0), Duration.ofMinutes(30));
        Task task7 = new Task("Test addNewTask7", "Test addNewTask7 description", State.NEW,
                LocalDateTime.of(2024, 8, 15, 14, 30), Duration.ofMinutes(30));
        taskManager.addNewTask(task1);
        try {
            taskManager.addNewTask(task2);
        } catch (TimeConflictException e) {

        }
        try {
        taskManager.addNewTask(task3);
        } catch (TimeConflictException e) {

        }
        try {
        taskManager.addNewTask(task4);
        } catch (TimeConflictException e) {

        }
        try {
        taskManager.addNewTask(task5);
        } catch (TimeConflictException e) {

        }
        try {
        taskManager.addNewTask(task6);
        } catch (TimeConflictException e) {

        }
        try {
        taskManager.addNewTask(task7);
        } catch (TimeConflictException e) {

        }

        Set<Task> expectedPrioritizedTasks = new LinkedHashSet<>();
        expectedPrioritizedTasks.add(task7);
        expectedPrioritizedTasks.add(task1);
        expectedPrioritizedTasks.add(task6);

        Set<Task> savedPrioritizedTasks = taskManager.getPrioritizedTasks();
        assertEquals(expectedPrioritizedTasks, savedPrioritizedTasks);
    }

    void checkEpicState(TaskManager taskManager) {
        Epic parentEpic = new Epic("Test parentEpic", "Test parentEpic description");
        Subtask subtask1 = new Subtask("Test newSubtask1", "Test newSubtask1 description", State.NEW, 1,
                LocalDateTime.of(2024, 8, 15, 17, 0), Duration.ofMinutes(30));
        Subtask subtask2 = new Subtask("Test newSubtask2", "Test newSubtask2 description", State.NEW, 1,
                LocalDateTime.of(2024, 8, 15, 18, 0), Duration.ofMinutes(30));
        Subtask subtask3 = new Subtask("Test newSubtask3", "Test newSubtask3 description", State.NEW, 1,
                LocalDateTime.of(2024, 8, 15, 19, 0), Duration.ofMinutes(30));
        taskManager.addNewEpic(parentEpic);
        taskManager.addNewSubtask(subtask1);
        taskManager.addNewSubtask(subtask2);
        taskManager.addNewSubtask(subtask3);
        assertEquals(State.NEW, parentEpic.getState(), "Ошибка при расчете статуса эпика при всех подзадачах NEW");

        Subtask updatedSubtask1 = new Subtask("Test updatedSubtask1", "Test updatedSubtask1 description",
                subtask1.getId(), State.DONE, 1,
                LocalDateTime.of(2024, 8, 15, 17, 0), Duration.ofMinutes(30));
        taskManager.updateSubtask(updatedSubtask1);
        assertEquals(State.IN_PROGRESS, parentEpic.getState(), "Ошибка при расчете статуса эпика подзадачах NEW и DONE");

        Subtask updatedSubtask2 = new Subtask("Test updatedSubtask2", "Test updatedSubtask2 description",
                subtask2.getId(), State.DONE, 1,
                LocalDateTime.of(2024, 8, 15, 18, 0), Duration.ofMinutes(30));
        Subtask updatedSubtask3 = new Subtask("Test updatedSubtask3", "Test updatedSubtask3 description",
                subtask3.getId(), State.DONE, 1,
                LocalDateTime.of(2024, 8, 15, 19, 0), Duration.ofMinutes(30));
        taskManager.updateSubtask(updatedSubtask2);
        taskManager.updateSubtask(updatedSubtask3);
        assertEquals(State.DONE, parentEpic.getState(), "Ошибка при расчете статуса эпика при всех подзадачах DONE");

        Subtask twiceUpdatedSubtask1 = new Subtask("Test twiceUpdatedSubtask1", "Test twiceUpdatedSubtask1 description",
                subtask1.getId(), State.IN_PROGRESS, 1,
                LocalDateTime.of(2024, 8, 15, 17, 0), Duration.ofMinutes(30));
        Subtask twiceUpdatedSubtask2 = new Subtask("Test twiceUpdatedSubtask2", "Test twiceUpdatedSubtask2 description",
                subtask2.getId(), State.IN_PROGRESS, 1,
                LocalDateTime.of(2024, 8, 15, 18, 0), Duration.ofMinutes(30));
        Subtask twiceUpdatedSubtask3 = new Subtask("Test twiceUpdatedSubtask3", "Test twiceUpdatedSubtask3 description",
                subtask3.getId(), State.IN_PROGRESS, 1,
                LocalDateTime.of(2024, 8, 15, 19, 0), Duration.ofMinutes(30));
        taskManager.updateSubtask(twiceUpdatedSubtask1);
        taskManager.updateSubtask(twiceUpdatedSubtask2);
        taskManager.updateSubtask(twiceUpdatedSubtask3);
        assertEquals(State.IN_PROGRESS, parentEpic.getState(), "Ошибка при расчете статуса эпика при всех подзадачах IN_PROGRESS");
    }
}
