package test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Map;

import TaskClasses.*;
import Managers.*;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    @Test
    void removeAllTasks() {
        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();
        Task task = new Task("Test addNewTask", "Test addNewTask description", State.NEW);
        inMemoryTaskManager.addNewTask(task);
        Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description");
        inMemoryTaskManager.addNewEpic(epic);
        Subtask subtask = new Subtask("Test addNewSubtask", "Test addNewSubtask description",
                State.NEW, epic.getId());
        inMemoryTaskManager.addNewSubtask(subtask);

        Assertions.assertNotNull(inMemoryTaskManager.getTasks(), "Задача не была добавлена.");
        Assertions.assertNotNull(inMemoryTaskManager.getEpics(), "Эпик не был добавлен.");
        Assertions.assertNotNull(inMemoryTaskManager.getSubtasks(), "Подзадача не была добавлена.");

        inMemoryTaskManager.removeAllTasks();

        Assertions.assertTrue(inMemoryTaskManager.getTasks().isEmpty(), "Задача не была удалена.");
        Assertions.assertTrue(inMemoryTaskManager.getEpics().isEmpty(), "Эпик не был удален.");
        Assertions.assertTrue(inMemoryTaskManager.getSubtasks().isEmpty(), "Подзадача не была удалена.");
    }

    @Test
    void getSubtasksForEpic() {
        Epic parentEpic = new Epic("Test parentEpic", "Test parentEpic description");
        Subtask subtask1 = new Subtask("Test addNewSubtask1", "Test addNewSubtask1 description",
                State.NEW, 1);
        Subtask subtask2 = new Subtask("Test addNewSubtask2", "Test addNewSubtask2 description",
                State.NEW, 1);
        Subtask subtask3 = new Subtask("Test addNewSubtask3", "Test addNewSubtask3 description",
                State.NEW, 1);
        ArrayList<Subtask> subtasksForEpic = new ArrayList<>();
        subtasksForEpic.add(subtask1);
        subtasksForEpic.add(subtask2);
        subtasksForEpic.add(subtask3);
        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();
        inMemoryTaskManager.addNewEpic(parentEpic);
        inMemoryTaskManager.addNewSubtask(subtask1);
        inMemoryTaskManager.addNewSubtask(subtask2);
        inMemoryTaskManager.addNewSubtask(subtask3);
        ArrayList<Subtask> savedSubtasksForEpic = inMemoryTaskManager.getSubtasksForEpic(parentEpic.getId());
        assertEquals(savedSubtasksForEpic, subtasksForEpic, "Сохраненные подзадачи не совпадают с введенными.");
    }

    @Test
    void removeTask() {
        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();
        Task task = new Task("Test addNewTask", "Test addNewTask description", State.NEW);
        inMemoryTaskManager.addNewTask(task);
        Assertions.assertNotNull(inMemoryTaskManager.getTasks(), "Задача не была добавлена.");
        inMemoryTaskManager.removeTask(task.getId());
        Assertions.assertTrue(inMemoryTaskManager.getTasks().isEmpty(), "Задача не была удалена.");
    }

    @Test
    void removeEpicWithNoSubtasks() {
        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();
        Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description");
        inMemoryTaskManager.addNewEpic(epic);
        Assertions.assertNotNull(inMemoryTaskManager.getEpics(), "Эпик не был добавлен.");
        inMemoryTaskManager.removeEpic(epic.getId());
        Assertions.assertTrue(inMemoryTaskManager.getEpics().isEmpty(), "Эпик не был удален.");
    }

    @Test
    void removeEpicWithSubtask() {
        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();
        Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description");
        inMemoryTaskManager.addNewEpic(epic);
        Subtask subtask = new Subtask("Test addNewSubtask", "Test addNewSubtask description",
                State.NEW, epic.getId());
        inMemoryTaskManager.addNewSubtask(subtask);
        Assertions.assertNotNull(inMemoryTaskManager.getEpics(), "Эпик не был добавлен.");
        Assertions.assertNotNull(inMemoryTaskManager.getSubtasks(), "Подзадача не была добавлена.");
        inMemoryTaskManager.removeEpic(epic.getId());
        Assertions.assertTrue(inMemoryTaskManager.getEpics().isEmpty(), "Эпик не был удален.");
        Assertions.assertTrue(inMemoryTaskManager.getSubtasks().isEmpty(), "Подзадача не была удалена.");
    }

    @Test
    void removeSubtask() {
        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();
        Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description");
        inMemoryTaskManager.addNewEpic(epic);
        Subtask subtask = new Subtask("Test addNewSubtask", "Test addNewSubtask description",
                State.NEW, epic.getId());
        inMemoryTaskManager.addNewSubtask(subtask);
        Assertions.assertNotNull(inMemoryTaskManager.getEpics(), "Эпик не был добавлен.");
        Assertions.assertNotNull(inMemoryTaskManager.getSubtasks(), "Подзадача не была добавлена.");
        inMemoryTaskManager.removeSubtask(subtask.getId());
        Assertions.assertTrue(inMemoryTaskManager.getSubtasks().isEmpty(), "Подзадача не была удалена.");
    }

    @Test
    void addNewTaskAndGetTaskAndGetTasks() {
        Task task = new Task("Test addNewTask", "Test addNewTask description", State.NEW);
        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();
        inMemoryTaskManager.addNewTask(task);
        final int taskId = task.getId();

        final Task savedTask = inMemoryTaskManager.getTask(taskId);

        Assertions.assertNotNull(savedTask, "Задача не найдена.");
        Assertions.assertEquals(task, savedTask, "Задачи не совпадают.");

        final Map<Integer, Task> tasks = inMemoryTaskManager.getTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        Assertions.assertEquals(task, tasks.get(1), "Задачи не совпадают.");
    }

    @Test
    void addNewEpicAndGetEpicAndGetEpics() {
        Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description");
        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();
        inMemoryTaskManager.addNewEpic(epic);
        final int epicId = epic.getId();

        final Task savedEpic = inMemoryTaskManager.getEpic(epicId);

        Assertions.assertNotNull(savedEpic, "Задача не найдена.");
        Assertions.assertEquals(epic, savedEpic, "Задачи не совпадают.");

        final Map<Integer, Epic> epics = inMemoryTaskManager.getEpics();

        assertNotNull(epics, "Задачи не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество задач.");
        Assertions.assertEquals(epic, epics.get(1), "Задачи не совпадают.");
    }

    @Test
    void addNewSubtaskAndGetSubtaskAndGetSubtasks() {
        Epic parentEpic = new Epic("Test parentEpic", "Test parentEpic description");
        Subtask subtask = new Subtask("Test addNewSubtask", "Test addNewSubtask description",
                State.NEW, 1);
        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();
        inMemoryTaskManager.addNewEpic(parentEpic);
        inMemoryTaskManager.addNewSubtask(subtask);
        final int subtaskId = subtask.getId();

        final Task savedSubtask = inMemoryTaskManager.getSubtask(subtaskId);

        Assertions.assertNotNull(savedSubtask, "Задача не найдена.");
        Assertions.assertEquals(subtask, savedSubtask, "Задачи не совпадают.");

        final Map<Integer, Subtask> subtasks = inMemoryTaskManager.getSubtasks();

        assertNotNull(subtasks, "Задачи не возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество задач.");
        Assertions.assertEquals(subtask, subtasks.get(2), "Задачи не совпадают.");
    }

    @Test
    void updateTask() {
        Task task = new Task("Test newTask", "Test newTask description", State.NEW);
        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();
        inMemoryTaskManager.addNewTask(task);
        Task updatedTask = new Task("Test updatedTask", "Test updatedTask description",
                task.getId(), State.DONE);
        inMemoryTaskManager.updateTask(updatedTask);

        final Task savedUpdatedTask = inMemoryTaskManager.getTask(updatedTask.getId());

        Assertions.assertNotNull(savedUpdatedTask, "Задача не найдена.");

        final Map<Integer, Task> tasks = inMemoryTaskManager.getTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        Assertions.assertEquals(savedUpdatedTask, tasks.get(1), "Обновленные задачи не совпадают.");
        Assertions.assertNotEquals(task, tasks.get(1), "Задача не обновилась.");
    }

    @Test
    void updateEpic() {
        Epic epic = new Epic("Test newEpic", "Test newEpic description");
        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();
        inMemoryTaskManager.addNewEpic(epic);
        Epic updatedEpic = new Epic("Test updatedTask", "Test updatedTask description", epic.getId());
        inMemoryTaskManager.updateEpic(updatedEpic);

        final Task savedUpdatedEpic = inMemoryTaskManager.getEpic(updatedEpic.getId());

        Assertions.assertNotNull(savedUpdatedEpic, "Задача не найдена.");

        final Map<Integer, Epic> epics = inMemoryTaskManager.getEpics();

        assertNotNull(epics, "Задачи не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество задач.");
        Assertions.assertEquals(savedUpdatedEpic, epics.get(1), "Обновленные задачи не совпадают.");
        Assertions.assertNotEquals(epic, epics.get(1), "Задача не обновилась.");
    }

    @Test
    void updateOneSubtaskForOneEpic() {
        Epic parentEpic = new Epic("Test parentEpic", "Test parentEpic description");
        Subtask subtask = new Subtask("Test newSubtask", "Test newSubtask description", State.NEW, 1);
        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();
        inMemoryTaskManager.addNewEpic(parentEpic);
        inMemoryTaskManager.addNewSubtask(subtask);
        Subtask updatedSubtask = new Subtask("Test updatedSubtask", "Test updatedSubtask description",
                subtask.getId(), State.DONE, subtask.getParentEpicID());
        inMemoryTaskManager.updateSubtask(updatedSubtask);
        final Subtask savedSubtask = inMemoryTaskManager.getSubtask(updatedSubtask.getId());

        Assertions.assertNotNull(savedSubtask, "Задача не найдена.");
        Assertions.assertEquals(updatedSubtask, savedSubtask, "Обновленные задачи не совпадают.");

        final Map<Integer, Subtask> subtasks = inMemoryTaskManager.getSubtasks();

        assertNotNull(subtasks, "Задачи не возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество задач.");
        Assertions.assertNotEquals(subtask, subtasks.get(2), "Задача не обновилась.");
        Assertions.assertEquals(State.DONE, parentEpic.getState(), "Родительский эпик не обновился.");
    }

    @Test
    void updateTwoSubtasksForOneEpic() {
        Epic parentEpic = new Epic("Test parentEpic", "Test parentEpic description");
        Subtask subtask1 = new Subtask("Test newSubtask1", "Test newSubtask1 description",
                State.NEW, 1);
        Subtask subtask2 = new Subtask("Test newSubtask2", "Test newSubtask2 description",
                State.NEW, 1);
        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();
        inMemoryTaskManager.addNewEpic(parentEpic);
        inMemoryTaskManager.addNewSubtask(subtask1);
        inMemoryTaskManager.addNewSubtask(subtask2);
        Subtask updatedSubtask1 = new Subtask("Test updatedSubtask1",
                "Test updatedSubtask1 description", subtask1.getId(), State.DONE, subtask1.getParentEpicID());
        inMemoryTaskManager.updateSubtask(updatedSubtask1);
        final Subtask savedSubtask1 = inMemoryTaskManager.getSubtask(updatedSubtask1.getId());

        Assertions.assertNotNull(savedSubtask1, "Задача не найдена.");
        Assertions.assertEquals(updatedSubtask1, savedSubtask1, "Обновленные задачи не совпадают.");

        final Map<Integer, Subtask> subtasks = inMemoryTaskManager.getSubtasks();

        assertNotNull(subtasks, "Задачи не возвращаются.");
        assertEquals(2, subtasks.size(), "Неверное количество задач.");
        Assertions.assertNotEquals(subtask1, subtasks.get(2), "Задача не обновилась.");
        Assertions.assertEquals(State.IN_PROGRESS, parentEpic.getState(), "Родительский эпик не обновился.");

        Subtask updatedSubtask2 = new Subtask("Test updatedSubtask2",
                "Test updatedSubtask2 description", subtask2.getId(), State.DONE, subtask2.getParentEpicID());
        inMemoryTaskManager.updateSubtask(updatedSubtask2);
        final Subtask savedSubtask2 = inMemoryTaskManager.getSubtask(updatedSubtask2.getId());

        Assertions.assertNotNull(savedSubtask2, "Задача не найдена.");
        Assertions.assertEquals(updatedSubtask2, savedSubtask2, "Обновленные задачи не совпадают.");
        Assertions.assertNotEquals(subtask2, subtasks.get(3), "Задача не обновилась.");
        Assertions.assertEquals(State.DONE, parentEpic.getState(), "Родительский эпик не обновился.");
    }

    @Test
    void updateOneSubtaskForTwoEpics() {
        Epic parentEpic1 = new Epic("Test parentEpic1", "Test parentEpic1 description");
        Epic parentEpic2 = new Epic("Test parentEpic2", "Test parentEpic2 description");
        Subtask subtask = new Subtask("Test newSubtask", "Test newSubtask description",
                State.DONE, 1);
        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();
        inMemoryTaskManager.addNewEpic(parentEpic1);
        inMemoryTaskManager.addNewEpic(parentEpic2);
        inMemoryTaskManager.addNewSubtask(subtask);

        Assertions.assertEquals(State.DONE, parentEpic1.getState(), "Родительский эпик не обновился.");

        Subtask updatedSubtask = new Subtask("Test updatedSubtask",
                "Test updatedSubtask description", subtask.getId(), State.DONE, parentEpic2.getId());
        inMemoryTaskManager.updateSubtask(updatedSubtask);

        Assertions.assertEquals(State.NEW, parentEpic1.getState(), "Старый родительский эпик не обновился.");
        Assertions.assertEquals(State.DONE, parentEpic2.getState(), "Новый родительский эпик не обновился.");
    }

    @Test
    void updateTwoSubtasksForTwoEpics() {
        Epic parentEpic1 = new Epic("Test parentEpic1", "Test parentEpic1 description");
        Epic parentEpic2 = new Epic("Test parentEpic2", "Test parentEpic2 description");
        Subtask subtask1 = new Subtask("Test newSubtask1", "Test newSubtask1 description",
                State.NEW, 1);
        Subtask subtask2 = new Subtask("Test newSubtask2", "Test newSubtask2 description",
                State.DONE, 1);
        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();
        inMemoryTaskManager.addNewEpic(parentEpic1);
        inMemoryTaskManager.addNewEpic(parentEpic2);
        inMemoryTaskManager.addNewSubtask(subtask1);
        inMemoryTaskManager.addNewSubtask(subtask2);

        Assertions.assertEquals(State.IN_PROGRESS, parentEpic1.getState(), "Родительский эпик не обновился.");

        Subtask updatedSubtask1 = new Subtask("Test updatedSubtask1",
                "Test updatedSubtask1 description", subtask1.getId(), State.IN_PROGRESS, parentEpic2.getId());
        inMemoryTaskManager.updateSubtask(updatedSubtask1);

        Assertions.assertEquals(State.DONE, parentEpic1.getState(), "Старый родительский эпик не обновился.");
        Assertions.assertEquals(State.IN_PROGRESS, parentEpic2.getState(), "Новый родительский эпик не обновился.");

        Subtask updatedSubtask2 = new Subtask("Test updatedSubtask2",
                "Test updatedSubtask2 description", subtask2.getId(), State.DONE, parentEpic2.getId());
        inMemoryTaskManager.updateSubtask(updatedSubtask2);

        Assertions.assertEquals(State.NEW, parentEpic1.getState(), "Старый родительский эпик не обновился.");

        Subtask twiceUpdatedSubtask1 = new Subtask("Test twiceUpdatedSubtask1",
                "Test twiceUpdatedSubtask1 description", subtask1.getId(), State.DONE, parentEpic2.getId());
        inMemoryTaskManager.updateSubtask(twiceUpdatedSubtask1);

        Assertions.assertEquals(State.DONE, parentEpic2.getState(), "Новый родительский эпик не обновился.");
    }
}