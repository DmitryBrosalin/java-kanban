import managers.FileBackedTaskManager;
import exceptions.ManagerSaveException;
import managers.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import taskclasses.Epic;
import taskclasses.State;
import taskclasses.Subtask;
import taskclasses.Task;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest extends TaskManagerTest {
    @Test
    void testManagerSaveException() {
        assertThrows(ManagerSaveException.class, () -> {
            File file = new File("backedListOfTasksTest.txt");
            TaskManager taskManager = FileBackedTaskManager.loadFromFile(file);
        }, "При загрузке из несуществующего файла должно выбрасываться исключение");
    }

    @AfterEach
    void deleteFile() throws IOException {
        try {
            Files.delete(Path.of("backedListOfTasksTest.txt"));
        } catch (IOException ignored) {}
    }

    @Test
    void correctReadFromFile() throws IOException {
        File file = Files.createFile(Path.of("backedListOfTasksTest.txt")).toFile();
        try (Writer fileWriter = new FileWriter(file)) {
            fileWriter.write("TASK,Test addNewTask1,Test addNewTask1 description,1,NEW,15:00 15.08.24,30" +
                    "\nTASK,Test addNewTask2,Test addNewTask2 description,2,DONE,15:10 15.08.24,30" +
                    "\nTASK,Test addNewTask3,Test addNewTask3 description,2,IN_PROGRESS,16:00 15.08.24,30" +
                    "\nEPIC,Test addNewEpic1,Test addNewEpic1 description,3,DONE" +
                    "\nSUBTASK,Test addNewSubtask1,Test addNewSubtask1 description,4,DONE,3,17:00 15.08.24,50");
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при записи данных");
        }
        TaskManager taskManager = FileBackedTaskManager.loadFromFile(file);
        Task task0 = new Task("Test addNewTask2", "Test addNewTask2 description", 2, State.DONE,
                LocalDateTime.of(2024, 8, 15, 15, 10), Duration.ofMinutes(30));
        Task task1 = new Task("Test addNewTask1", "Test addNewTask1 description", 1, State.NEW,
                LocalDateTime.of(2024, 8, 15, 15, 0), Duration.ofMinutes(30));
        Task task3 = new Task("Test addNewTask3", "Test addNewTask3 description",2, State.IN_PROGRESS,
                LocalDateTime.of(2024, 8, 15, 16, 0), Duration.ofMinutes(30));
        Epic epic1 = new Epic("Test addNewEpic1", "Test addNewEpic1 description", 3, State.DONE);
        Subtask subtask1 = new Subtask("Test addNewSubtask1", "Test addNewSubtask1 description", 4,
                State.DONE, 3, LocalDateTime.of(2024, 8, 15, 17, 0), Duration.ofMinutes(50));
        List<Integer> subtasksID = epic1.getSubtasksID();
        subtasksID.add(subtask1.getId());
        Map<Integer, Task> tasks = new HashMap<>();
        tasks.put(task1.getId(), task1);
        tasks.put(task3.getId(), task3);
        Map<Integer, Epic> epics = new HashMap<>();
        epics.put(epic1.getId(), epic1);
        Map<Integer, Subtask> subtasks = new HashMap<>();
        subtasks.put(subtask1.getId(), subtask1);
        Task savedTask1 = taskManager.getTask(1);
        Task savedTask2 = taskManager.getTask(2);
        Epic savedEpic1 = taskManager.getEpic(3);
        Subtask savedSubtask1 = taskManager.getSubtask(4);
        Map<Integer, Task> savedTasks = taskManager.getTasks();
        Map<Integer, Epic> savedEpics = taskManager.getEpics();
        Map<Integer, Subtask> savedSubtasks = taskManager.getSubtasks();
        assertEquals(task1, savedTask1, "Ошибка при сохранении задачи из файла");
        assertEquals(task3, savedTask2, "Ошибка при сохранении задачи из файла");
        assertEquals(epic1, savedEpic1, "Ошибка при сохранении эпика из файла");
        assertEquals(subtask1, savedSubtask1, "Ошибка при сохранении подзадачи из файла");
        assertEquals(tasks, savedTasks, "Ошибка при возврате задач");
        assertEquals(epics, savedEpics, "Ошибка при возврате эпиков");
        assertEquals(subtasks, savedSubtasks, "Ошибка при возврате подзадач");
        assertFalse(savedTasks.containsValue(task0), "Ошибка при расчете конфликта задач по времени");
    }

    @Test
    void correctSaveToFile() throws IOException {
        File file = Files.createFile(Path.of("backedListOfTasksTest.txt")).toFile();
        TaskManager taskManager = FileBackedTaskManager.loadFromFile(file);
        Task task1 = new Task("Test addNewTask1", "Test addNewTask1 description", 1, State.NEW,
                LocalDateTime.of(2024, 8, 15, 15, 0), Duration.ofMinutes(30));
        Task task2 = new Task("Test addNewTask2", "Test addNewTask2 description",2, State.IN_PROGRESS,
                LocalDateTime.of(2024, 8, 15, 16, 0), Duration.ofMinutes(30));
        Epic epic1 = new Epic("Test addNewEpic1", "Test addNewEpic1 description", 3, State.DONE);
        Subtask subtask1 = new Subtask("Test addNewSubtask1", "Test addNewSubtask1 description", 4,
                State.DONE, 3, LocalDateTime.of(2024, 8, 15, 17, 0), Duration.ofMinutes(50));
        taskManager.addNewTask(task1);
        taskManager.addNewTask(task2);
        taskManager.addNewEpic(epic1);
        taskManager.addNewSubtask(subtask1);

        List<String> expectedStringFile = new ArrayList<>();
        expectedStringFile.add("TASK,Test addNewTask1,Test addNewTask1 description,1,NEW,15:00 15.08.24,30");
        expectedStringFile.add("TASK,Test addNewTask2,Test addNewTask2 description,2,IN_PROGRESS,16:00 15.08.24,30");
        expectedStringFile.add("EPIC,Test addNewEpic1,Test addNewEpic1 description,3,DONE");
        expectedStringFile.add("SUBTASK,Test addNewSubtask1,Test addNewSubtask1 description,4,DONE,3,17:00 15.08.24,50");

        List<String> stringFile = new ArrayList<>();
        try (BufferedReader bufferReader = new BufferedReader(new FileReader(file))) {
            while (bufferReader.ready()) {
                String task = bufferReader.readLine();
                stringFile.add(task);
            }
        } catch (IOException ex) {
            throw new ManagerSaveException("Произошла ошибка при чтении файла.");
        }
        assertEquals(expectedStringFile, stringFile, "Ошибка при записи в файл");
    }

    @Test
    void addNewTaskAndCheckTimeConflictAndGetTaskAndGetTasks() throws IOException {
        File file = Files.createFile(Path.of("backedListOfTasksTest.txt")).toFile();
        TaskManager taskManager = FileBackedTaskManager.loadFromFile(file);
        super.addNewTaskAndCheckTimeConflictAndGetTaskAndGetTasks(taskManager);
    }

    @Test
    void addNewEpicAndGetEpicAndGetEpics() throws IOException {
        File file = Files.createFile(Path.of("backedListOfTasksTest.txt")).toFile();
        TaskManager taskManager = FileBackedTaskManager.loadFromFile(file);
        super.addNewEpicAndGetEpicAndGetEpics(taskManager);
    }

    @Test
    void addNewSubtaskAndGetSubtaskAndGetSubtasks() throws IOException {
        File file = Files.createFile(Path.of("backedListOfTasksTest.txt")).toFile();
        TaskManager taskManager = FileBackedTaskManager.loadFromFile(file);
        super.addNewSubtaskAndGetSubtaskAndGetSubtasks(taskManager);
    }

    @Test
    void removeTask() throws IOException {
        File file = Files.createFile(Path.of("backedListOfTasksTest.txt")).toFile();
        TaskManager taskManager = FileBackedTaskManager.loadFromFile(file);
        super.removeTask(taskManager);
        assertEquals(0, file.length(), "Задача не была удалена из файла");
    }

    @Test
    void removeEpicWithNoSubtasks() throws IOException {
        File file = Files.createFile(Path.of("backedListOfTasksTest.txt")).toFile();
        TaskManager taskManager = FileBackedTaskManager.loadFromFile(file);
        super.removeEpicWithNoSubtasks(taskManager);
        assertEquals(0, file.length(), "Эпик не был удален из файла");
    }

    @Test
    void removeEpicWithSubtask() throws IOException {
        File file = Files.createFile(Path.of("backedListOfTasksTest.txt")).toFile();
        TaskManager taskManager = FileBackedTaskManager.loadFromFile(file);
        super.removeEpicWithSubtask(taskManager);
        assertEquals(0, file.length(), "Эпик или подзадача не были удалены из файла");
    }

    @Test
    void removeSubtask() throws IOException {
        File file = Files.createFile(Path.of("backedListOfTasksTest.txt")).toFile();
        TaskManager taskManager = FileBackedTaskManager.loadFromFile(file);
        super.removeSubtask(taskManager);
    }

    @Test
    void removeAllTasks() throws IOException {
        File file = Files.createFile(Path.of("backedListOfTasksTest.txt")).toFile();
        TaskManager taskManager = FileBackedTaskManager.loadFromFile(file);
        super.removeAllTasks(taskManager);
        assertEquals(0, file.length(), "Задача, эпик или подзадача не были удалены из файла");
    }

    @Test
    void getSubtasksForEpic() throws IOException {
        File file = Files.createFile(Path.of("backedListOfTasksTest.txt")).toFile();
        TaskManager taskManager = FileBackedTaskManager.loadFromFile(file);
        super.getSubtasksForEpic(taskManager);
    }

    @Test
    void updateTask() throws IOException {
        File file = Files.createFile(Path.of("backedListOfTasksTest.txt")).toFile();
        TaskManager taskManager = FileBackedTaskManager.loadFromFile(file);
        super.updateTask(taskManager);
    }

    @Test
    void updateEpicWithNoSubtasks() throws IOException {
        File file = Files.createFile(Path.of("backedListOfTasksTest.txt")).toFile();
        TaskManager taskManager = FileBackedTaskManager.loadFromFile(file);
        super.updateEpicWithNoSubtasks(taskManager);
    }

    @Test
    void updateEpicWithSubtasks() throws IOException {
        File file = Files.createFile(Path.of("backedListOfTasksTest.txt")).toFile();
        TaskManager taskManager = FileBackedTaskManager.loadFromFile(file);
        super.updateEpicWithSubtasks(taskManager);
    }

    @Test
    void updateOneSubtaskForOneEpicAndCheckEpicStateAndTime() throws IOException {
        File file = Files.createFile(Path.of("backedListOfTasksTest.txt")).toFile();
        TaskManager taskManager = FileBackedTaskManager.loadFromFile(file);
        super.updateOneSubtaskForOneEpicAndCheckEpicStateAndTime(taskManager);
    }

    @Test
    void updateTwoSubtasksForOneEpicAndCheckEpicStateAndTime() throws IOException {
        File file = Files.createFile(Path.of("backedListOfTasksTest.txt")).toFile();
        TaskManager taskManager = FileBackedTaskManager.loadFromFile(file);
        super.updateTwoSubtasksForOneEpicAndCheckEpicStateAndTime(taskManager);
    }

    @Test
    void updateOneSubtaskForTwoEpicsAndCheckEpicState() throws IOException {
        File file = Files.createFile(Path.of("backedListOfTasksTest.txt")).toFile();
        TaskManager taskManager = FileBackedTaskManager.loadFromFile(file);
        super.updateOneSubtaskForTwoEpicsAndCheckEpicState(taskManager);
    }

    @Test
    void updateTwoSubtasksForTwoEpicsAndCheckEpicState() throws IOException {
        File file = Files.createFile(Path.of("backedListOfTasksTest.txt")).toFile();
        TaskManager taskManager = FileBackedTaskManager.loadFromFile(file);
        super.updateTwoSubtasksForTwoEpicsAndCheckEpicState(taskManager);
    }

    @Test
    void getPrioritizedTasks() throws IOException {
        File file = Files.createFile(Path.of("backedListOfTasksTest.txt")).toFile();
        TaskManager taskManager = FileBackedTaskManager.loadFromFile(file);
        super.getPrioritizedTasks(taskManager);
    }

    @Test
    void checkTimeConflicts() throws IOException {
        File file = Files.createFile(Path.of("backedListOfTasksTest.txt")).toFile();
        TaskManager taskManager = FileBackedTaskManager.loadFromFile(file);
        super.checkTimeConflicts(taskManager);
    }

    @Test
    void checkEpicState() throws IOException {
        File file = Files.createFile(Path.of("backedListOfTasksTest.txt")).toFile();
        TaskManager taskManager = FileBackedTaskManager.loadFromFile(file);
        super.checkEpicState(taskManager);
    }
}
