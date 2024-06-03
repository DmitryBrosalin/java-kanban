import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    @Test
    void addToHistory() {
        InMemoryHistoryManager inMemoryHistoryManager = new InMemoryHistoryManager();
        Task task = new Task("Test Task", "Test Task description", State.NEW);
        inMemoryHistoryManager.addToHistory(task);
        assertFalse(inMemoryHistoryManager.getHistory().isEmpty(), "Задача не была добавлена в историю просмотра.");
    }

    @Test
    void getHistory() {
        InMemoryHistoryManager inMemoryHistoryManager = new InMemoryHistoryManager();
        Task task = new Task("Test Task", "Test Task description", State.NEW);
        inMemoryHistoryManager.addToHistory(task);
        ArrayList<Task> history = new ArrayList<>();
        history.add(task);
        List<Task> savedHistory = inMemoryHistoryManager.getHistory();
        assertNotNull(savedHistory, "Не удалось создать историю просмотра.");
        assertEquals(savedHistory, history, "Не удалось вернуть историю просмотра корректно.");
    }

    @Test
    void addToHistoryMoreThan10() {
        InMemoryHistoryManager inMemoryHistoryManager = new InMemoryHistoryManager();
        Task task1 = new Task("Test Task1", "Test Task1 description", State.NEW);
        Task task2 = new Task("Test Task2", "Test Task2 description", State.NEW);
        Task task3 = new Task("Test Task3", "Test Task3 description", State.NEW);
        Task task4 = new Task("Test Task4", "Test Task4 description", State.NEW);
        Task task5 = new Task("Test Task5", "Test Task5 description", State.NEW);
        Task task6 = new Task("Test Task6", "Test Task6 description", State.NEW);
        Task task7 = new Task("Test Task7", "Test Task7 description", State.NEW);
        Task task8 = new Task("Test Task8", "Test Task8 description", State.NEW);
        Task task9 = new Task("Test Task9", "Test Task9 description", State.NEW);
        Task task10 = new Task("Test Task10", "Test Task10 description", State.NEW);
        Task task11 = new Task("Test Task11", "Test Task11 description", State.NEW);

        inMemoryHistoryManager.addToHistory(task1);
        inMemoryHistoryManager.addToHistory(task2);
        inMemoryHistoryManager.addToHistory(task3);
        inMemoryHistoryManager.addToHistory(task4);
        inMemoryHistoryManager.addToHistory(task5);
        inMemoryHistoryManager.addToHistory(task6);
        inMemoryHistoryManager.addToHistory(task7);
        inMemoryHistoryManager.addToHistory(task8);
        inMemoryHistoryManager.addToHistory(task9);
        inMemoryHistoryManager.addToHistory(task10);
        inMemoryHistoryManager.addToHistory(task11);

        List<Task> savedHistory = inMemoryHistoryManager.getHistory();

        ArrayList<Task> history = new ArrayList<>();
        history.add(task2);
        history.add(task3);
        history.add(task4);
        history.add(task5);
        history.add(task6);
        history.add(task7);
        history.add(task8);
        history.add(task9);
        history.add(task10);
        history.add(task11);

        assertEquals(savedHistory, history, "Не удалось вернуть историю просмотра корректно.");
    }
}