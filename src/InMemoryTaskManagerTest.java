import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;

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
                State.NEW, epic.id);
        inMemoryTaskManager.addNewSubtask(subtask);

        assertNotNull(inMemoryTaskManager.getTasks(), "Задача не была добавлена.");
        assertNotNull(inMemoryTaskManager.getEpics(), "Эпик не был добавлен.");
        assertNotNull(inMemoryTaskManager.getSubtasks(), "Подзадача не была добавлена.");

        inMemoryTaskManager.removeAllTasks();

        assertTrue(inMemoryTaskManager.getTasks().isEmpty(), "Задача не была удалена.");
        assertTrue(inMemoryTaskManager.getEpics().isEmpty(), "Эпик не был удален.");
        assertTrue(inMemoryTaskManager.getSubtasks().isEmpty(), "Подзадача не была удалена.");
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
        ArrayList<Subtask> savedSubtasksForEpic = inMemoryTaskManager.getSubtasksForEpic(parentEpic.id);
        assertEquals(savedSubtasksForEpic, subtasksForEpic, "Сохраненные подзадачи не совпадают с введенными.");
    }

    @Test
    void removeTask() {
        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();
        Task task = new Task("Test addNewTask", "Test addNewTask description", State.NEW);
        inMemoryTaskManager.addNewTask(task);
        assertNotNull(inMemoryTaskManager.getTasks(), "Задача не была добавлена.");
        inMemoryTaskManager.removeTask(task.id);
        assertTrue(inMemoryTaskManager.getTasks().isEmpty(), "Задача не была удалена.");
    }

    @Test
    void removeEpicWithNoSubtasks() {
        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();
        Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description");
        inMemoryTaskManager.addNewEpic(epic);
        assertNotNull(inMemoryTaskManager.getEpics(), "Эпик не был добавлен.");
        inMemoryTaskManager.removeEpic(epic.id);
        assertTrue(inMemoryTaskManager.getEpics().isEmpty(), "Эпик не был удален.");
    }

    @Test
    void removeEpicWithSubtask() {
        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();
        Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description");
        inMemoryTaskManager.addNewEpic(epic);
        Subtask subtask = new Subtask("Test addNewSubtask", "Test addNewSubtask description",
                State.NEW, epic.id);
        inMemoryTaskManager.addNewSubtask(subtask);
        assertNotNull(inMemoryTaskManager.getEpics(), "Эпик не был добавлен.");
        assertNotNull(inMemoryTaskManager.getSubtasks(), "Подзадача не была добавлена.");
        inMemoryTaskManager.removeEpic(epic.id);
        assertTrue(inMemoryTaskManager.getEpics().isEmpty(), "Эпик не был удален.");
        assertTrue(inMemoryTaskManager.getSubtasks().isEmpty(), "Подзадача не была удалена.");
    }

    @Test
    void removeSubtask() {
        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();
        Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description");
        inMemoryTaskManager.addNewEpic(epic);
        Subtask subtask = new Subtask("Test addNewSubtask", "Test addNewSubtask description",
                State.NEW, epic.id);
        inMemoryTaskManager.addNewSubtask(subtask);
        assertNotNull(inMemoryTaskManager.getEpics(), "Эпик не был добавлен.");
        assertNotNull(inMemoryTaskManager.getSubtasks(), "Подзадача не была добавлена.");
        inMemoryTaskManager.removeSubtask(subtask.id);
        assertTrue(inMemoryTaskManager.getSubtasks().isEmpty(), "Подзадача не была удалена.");
    }

    @Test
    void addNewTaskAndGetTaskAndGetTasks() {
        Task task = new Task("Test addNewTask", "Test addNewTask description", State.NEW);
        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();
        inMemoryTaskManager.addNewTask(task);
        final int taskId = task.id;

        final Task savedTask = inMemoryTaskManager.getTask(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final HashMap<Integer, Task> tasks = inMemoryTaskManager.getTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(1), "Задачи не совпадают.");
    }

    @Test
    void addNewEpicAndGetEpicAndGetEpics() {
        Epic epic = new Epic("Test addNewEpic", "Test addNewEpic description");
        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();
        inMemoryTaskManager.addNewEpic(epic);
        final int epicId = epic.id;

        final Task savedEpic = inMemoryTaskManager.getEpic(epicId);

        assertNotNull(savedEpic, "Задача не найдена.");
        assertEquals(epic, savedEpic, "Задачи не совпадают.");

        final HashMap<Integer, Epic> epics = inMemoryTaskManager.getEpics();

        assertNotNull(epics, "Задачи не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество задач.");
        assertEquals(epic, epics.get(1), "Задачи не совпадают.");
    }

    @Test
    void addNewSubtaskAndGetSubtaskAndGetSubtasks() {
        Epic parentEpic = new Epic("Test parentEpic", "Test parentEpic description");
        Subtask subtask = new Subtask("Test addNewSubtask", "Test addNewSubtask description",
                State.NEW, 1);
        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();
        inMemoryTaskManager.addNewEpic(parentEpic);
        inMemoryTaskManager.addNewSubtask(subtask);
        final int subtaskId = subtask.id;

        final Task savedSubtask = inMemoryTaskManager.getSubtask(subtaskId);

        assertNotNull(savedSubtask, "Задача не найдена.");
        assertEquals(subtask, savedSubtask, "Задачи не совпадают.");

        final HashMap<Integer, Subtask> subtasks = inMemoryTaskManager.getSubtasks();

        assertNotNull(subtasks, "Задачи не возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество задач.");
        assertEquals(subtask, subtasks.get(2), "Задачи не совпадают.");
    }

    @Test
    void updateTask() {
        Task task = new Task("Test newTask", "Test newTask description", State.NEW);
        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();
        inMemoryTaskManager.addNewTask(task);
        Task updatedTask = new Task("Test updatedTask", "Test updatedTask description",
                task.id, State.DONE);
        inMemoryTaskManager.updateTask(updatedTask);

        final Task savedUpdatedTask = inMemoryTaskManager.getTask(updatedTask.id);

        assertNotNull(savedUpdatedTask, "Задача не найдена.");

        final HashMap<Integer, Task> tasks = inMemoryTaskManager.getTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(savedUpdatedTask, tasks.get(1), "Обновленные задачи не совпадают.");
        assertNotEquals(task, tasks.get(1), "Задача не обновилась.");
    }

    @Test
    void updateEpic() {
        Epic epic = new Epic("Test newEpic", "Test newEpic description");
        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();
        inMemoryTaskManager.addNewEpic(epic);
        Epic updatedEpic = new Epic("Test updatedTask", "Test updatedTask description", epic.id);
        inMemoryTaskManager.updateEpic(updatedEpic);

        final Task savedUpdatedEpic = inMemoryTaskManager.getEpic(updatedEpic.id);

        assertNotNull(savedUpdatedEpic, "Задача не найдена.");

        final HashMap<Integer, Epic> epics = inMemoryTaskManager.getEpics();

        assertNotNull(epics, "Задачи не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество задач.");
        assertEquals(savedUpdatedEpic, epics.get(1), "Обновленные задачи не совпадают.");
        assertNotEquals(epic, epics.get(1), "Задача не обновилась.");
    }

    @Test
    void updateOneSubtaskForOneEpic() {
        Epic parentEpic = new Epic("Test parentEpic", "Test parentEpic description");
        Subtask subtask = new Subtask("Test newSubtask", "Test newSubtask description", State.NEW, 1);
        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();
        inMemoryTaskManager.addNewEpic(parentEpic);
        inMemoryTaskManager.addNewSubtask(subtask);
        Subtask updatedSubtask = new Subtask("Test updatedSubtask", "Test updatedSubtask description",
                subtask.id, State.DONE, subtask.getParentEpicID());
        inMemoryTaskManager.updateSubtask(updatedSubtask);
        final Subtask savedSubtask = inMemoryTaskManager.getSubtask(updatedSubtask.id);

        assertNotNull(savedSubtask, "Задача не найдена.");
        assertEquals(updatedSubtask, savedSubtask, "Обновленные задачи не совпадают.");

        final HashMap<Integer, Subtask> subtasks = inMemoryTaskManager.getSubtasks();

        assertNotNull(subtasks, "Задачи не возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество задач.");
        assertNotEquals(subtask, subtasks.get(2), "Задача не обновилась.");
        assertEquals(State.DONE, parentEpic.state, "Родительский эпик не обновился.");
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
                "Test updatedSubtask1 description", subtask1.id, State.DONE, subtask1.getParentEpicID());
        inMemoryTaskManager.updateSubtask(updatedSubtask1);
        final Subtask savedSubtask1 = inMemoryTaskManager.getSubtask(updatedSubtask1.id);

        assertNotNull(savedSubtask1, "Задача не найдена.");
        assertEquals(updatedSubtask1, savedSubtask1, "Обновленные задачи не совпадают.");

        final HashMap<Integer, Subtask> subtasks = inMemoryTaskManager.getSubtasks();

        assertNotNull(subtasks, "Задачи не возвращаются.");
        assertEquals(2, subtasks.size(), "Неверное количество задач.");
        assertNotEquals(subtask1, subtasks.get(2), "Задача не обновилась.");
        assertEquals(State.IN_PROGRESS, parentEpic.state, "Родительский эпик не обновился.");

        Subtask updatedSubtask2 = new Subtask("Test updatedSubtask2",
                "Test updatedSubtask2 description", subtask2.id, State.DONE, subtask2.getParentEpicID());
        inMemoryTaskManager.updateSubtask(updatedSubtask2);
        final Subtask savedSubtask2 = inMemoryTaskManager.getSubtask(updatedSubtask2.id);

        assertNotNull(savedSubtask2, "Задача не найдена.");
        assertEquals(updatedSubtask2, savedSubtask2, "Обновленные задачи не совпадают.");
        assertNotEquals(subtask2, subtasks.get(3), "Задача не обновилась.");
        assertEquals(State.DONE, parentEpic.state, "Родительский эпик не обновился.");
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

        assertEquals(State.DONE, parentEpic1.state, "Родительский эпик не обновился.");

        Subtask updatedSubtask = new Subtask("Test updatedSubtask",
                "Test updatedSubtask description", subtask.id, State.DONE, parentEpic2.id);
        inMemoryTaskManager.updateSubtask(updatedSubtask);

        assertEquals(State.NEW, parentEpic1.state, "Старый родительский эпик не обновился.");
        assertEquals(State.DONE, parentEpic2.state, "Новый родительский эпик не обновился.");
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

        assertEquals(State.IN_PROGRESS, parentEpic1.state, "Родительский эпик не обновился.");

        Subtask updatedSubtask1 = new Subtask("Test updatedSubtask1",
                "Test updatedSubtask1 description", subtask1.id, State.IN_PROGRESS, parentEpic2.id);
        inMemoryTaskManager.updateSubtask(updatedSubtask1);

        assertEquals(State.DONE, parentEpic1.state, "Старый родительский эпик не обновился.");
        assertEquals(State.IN_PROGRESS, parentEpic2.state, "Новый родительский эпик не обновился.");

        Subtask updatedSubtask2 = new Subtask("Test updatedSubtask2",
                "Test updatedSubtask2 description", subtask2.id, State.DONE, parentEpic2.id);
        inMemoryTaskManager.updateSubtask(updatedSubtask2);

        assertEquals(State.NEW, parentEpic1.state, "Старый родительский эпик не обновился.");

        Subtask twiceUpdatedSubtask1 = new Subtask("Test twiceUpdatedSubtask1",
                "Test twiceUpdatedSubtask1 description", subtask1.id, State.DONE, parentEpic2.id);
        inMemoryTaskManager.updateSubtask(twiceUpdatedSubtask1);

        assertEquals(State.DONE, parentEpic2.state, "Новый родительский эпик не обновился.");
    }
}