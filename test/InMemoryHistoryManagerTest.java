import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import taskclasses.*;
import managers.*;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    @Test
    void addToHistory() {
        InMemoryHistoryManager inMemoryHistoryManager = new InMemoryHistoryManager();
        Task task = new Task("Test TaskClasses.Task", "Test TaskClasses.Task description", State.NEW,
                LocalDateTime.of(2024, 8, 15, 15, 0), Duration.ofMinutes(30));
        inMemoryHistoryManager.addToHistory(task);
        Assertions.assertFalse(inMemoryHistoryManager.getHistory().isEmpty(), "Задача не была добавлена в историю просмотра.");
    }

    @Test
    void getHistory() {
        InMemoryHistoryManager inMemoryHistoryManager = new InMemoryHistoryManager();
        Task task = new Task("Test TaskClasses.Task", "Test TaskClasses.Task description", State.NEW,
                LocalDateTime.of(2024, 8, 15, 15, 0), Duration.ofMinutes(30));
        inMemoryHistoryManager.addToHistory(task);
        ArrayList<Task> history = new ArrayList<>();
        history.add(task);
        List<Task> savedHistory = inMemoryHistoryManager.getHistory();
        assertNotNull(savedHistory, "Не удалось создать историю просмотра.");
        assertEquals(savedHistory, history, "Не удалось вернуть историю просмотра корректно.");
    }

    @Test
    void getEmptyHistory() {
        InMemoryHistoryManager inMemoryHistoryManager = new InMemoryHistoryManager();
        List<Task> savedHistory = inMemoryHistoryManager.getHistory();
        List<Task> emptyHistory = new ArrayList<>();
        assertEquals(savedHistory, emptyHistory, "Пустая история просмотра возвращается некорректно.");
    }

    @Test
    void getCorrectLinkedHistory() {
        InMemoryHistoryManager inMemoryHistoryManager = new InMemoryHistoryManager();
        Task task1 = new Task("Test TaskClasses.Task 1", "Test TaskClasses.Task description 1", 1, State.NEW,
                LocalDateTime.of(2024, 8, 15, 15, 0), Duration.ofMinutes(30));
        Task task2 = new Task("Test TaskClasses.Task 2", "Test TaskClasses.Task description 2", 2, State.NEW,
                LocalDateTime.of(2024, 8, 15, 16, 0), Duration.ofMinutes(30));
        Task task3 = new Task("Test TaskClasses.Task 3", "Test TaskClasses.Task description 3", 3, State.NEW,
                LocalDateTime.of(2024, 8, 15, 17, 0), Duration.ofMinutes(30));
        inMemoryHistoryManager.addToHistory(task1);
        inMemoryHistoryManager.addToHistory(task2);
        inMemoryHistoryManager.addToHistory(task3);
        ArrayList<Task> history = new ArrayList<>();
        history.add(task1);
        history.add(task2);
        history.add(task3);
        List<Task> savedHistory = inMemoryHistoryManager.getHistory();
        assertNotNull(savedHistory, "Не удалось создать историю просмотра.");
        assertEquals(savedHistory, history, "Не удалось вернуть историю просмотра корректно.");
    }

    @Test
    void getCorrectLinkedHistoryWithRepeat() {
        InMemoryHistoryManager inMemoryHistoryManager = new InMemoryHistoryManager();
        Task task1 = new Task("Test TaskClasses.Task 1", "Test TaskClasses.Task description 1", 1, State.NEW,
                LocalDateTime.of(2024, 8, 15, 15, 0), Duration.ofMinutes(30));
        Task task2 = new Task("Test TaskClasses.Task 2", "Test TaskClasses.Task description 2", 2, State.NEW,
                LocalDateTime.of(2024, 8, 15, 16, 0), Duration.ofMinutes(30));
        Task task3 = new Task("Test TaskClasses.Task 3", "Test TaskClasses.Task description 3", 3, State.NEW,
                LocalDateTime.of(2024, 8, 15, 17, 0), Duration.ofMinutes(30));
        inMemoryHistoryManager.addToHistory(task1);
        inMemoryHistoryManager.addToHistory(task2);
        inMemoryHistoryManager.addToHistory(task3);
        inMemoryHistoryManager.addToHistory(task1);
        ArrayList<Task> history = new ArrayList<>();
        history.add(task2);
        history.add(task3);
        history.add(task1);
        List<Task> savedHistory = inMemoryHistoryManager.getHistory();
        assertNotNull(savedHistory, "Не удалось создать историю просмотра.");
        assertEquals(savedHistory, history, "Не удалось вернуть историю просмотра корректно при повторном запросе задачи.");
    }

    @Test
    void deleteFromHistory() {
        InMemoryHistoryManager inMemoryHistoryManager = new InMemoryHistoryManager();
        Task task1 = new Task("Test TaskClasses.Task 1", "Test TaskClasses.Task description 1", 1, State.NEW,
                LocalDateTime.of(2024, 8, 15, 11, 0), Duration.ofMinutes(30));
        Task task2 = new Task("Test TaskClasses.Task 2", "Test TaskClasses.Task description 2", 2, State.NEW,
                LocalDateTime.of(2024, 8, 15, 12, 0), Duration.ofMinutes(30));
        Task task3 = new Task("Test TaskClasses.Task 3", "Test TaskClasses.Task description 3", 3, State.NEW,
                LocalDateTime.of(2024, 8, 15, 13, 0), Duration.ofMinutes(30));
        Task task4 = new Task("Test TaskClasses.Task 4", "Test TaskClasses.Task description 4", 4, State.NEW,
                LocalDateTime.of(2024, 8, 15, 14, 0), Duration.ofMinutes(30));
        Task task5 = new Task("Test TaskClasses.Task 5", "Test TaskClasses.Task description 5", 5, State.NEW,
                LocalDateTime.of(2024, 8, 15, 15, 0), Duration.ofMinutes(30));
        inMemoryHistoryManager.addToHistory(task1);
        inMemoryHistoryManager.addToHistory(task2);
        inMemoryHistoryManager.addToHistory(task3);
        inMemoryHistoryManager.addToHistory(task4);
        inMemoryHistoryManager.addToHistory(task5);
        ArrayList<Task> history = new ArrayList<>();
        history.add(task1);
        history.add(task2);
        history.add(task3);
        history.add(task4);
        history.add(task5);
        List<Task> savedHistory = inMemoryHistoryManager.getHistory();
        assertNotNull(savedHistory, "Не удалось создать историю просмотра.");
        assertEquals(history, savedHistory, "Не удалось вернуть историю просмотра.");

        inMemoryHistoryManager.removeTask(1);
        savedHistory = inMemoryHistoryManager.getHistory();
        history.remove(0);
        assertEquals(history, savedHistory, "Не удалось корректно вернуть историю просмотра при удалении певрой записи.");

        inMemoryHistoryManager.removeTask(5);
        savedHistory = inMemoryHistoryManager.getHistory();
        history.remove(3);
        assertEquals(history, savedHistory, "Не удалось корректно вернуть историю просмотра при удалении последней записи.");

        inMemoryHistoryManager.removeTask(3);
        savedHistory = inMemoryHistoryManager.getHistory();
        history.remove(1);
        assertEquals(history, savedHistory, "Не удалось корректно вернуть историю просмотра при удалении записи из середины истории.");
    }
}