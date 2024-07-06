package test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import TaskClasses.*;
import Managers.*;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    @Test
    void addToHistory() {
        InMemoryHistoryManager inMemoryHistoryManager = new InMemoryHistoryManager();
        Task task = new Task("Test TaskClasses.Task", "Test TaskClasses.Task description", State.NEW);
        inMemoryHistoryManager.addToHistory(task);
        Assertions.assertFalse(inMemoryHistoryManager.getHistory().isEmpty(), "Задача не была добавлена в историю просмотра.");
    }

    @Test
    void getHistory() {
        InMemoryHistoryManager inMemoryHistoryManager = new InMemoryHistoryManager();
        Task task = new Task("Test TaskClasses.Task", "Test TaskClasses.Task description", State.NEW);
        inMemoryHistoryManager.addToHistory(task);
        ArrayList<Task> history = new ArrayList<>();
        history.add(task);
        List<Task> savedHistory = inMemoryHistoryManager.getHistory();
        assertNotNull(savedHistory, "Не удалось создать историю просмотра.");
        assertEquals(savedHistory, history, "Не удалось вернуть историю просмотра корректно.");
    }

    @Test
    void getCorrectLinkedHistory() {
        InMemoryHistoryManager inMemoryHistoryManager = new InMemoryHistoryManager();
        Task task1 = new Task("Test TaskClasses.Task 1", "Test TaskClasses.Task description 1", 1, State.NEW);
        Task task2 = new Task("Test TaskClasses.Task 2", "Test TaskClasses.Task description 2", 2, State.NEW);
        Task task3 = new Task("Test TaskClasses.Task 3", "Test TaskClasses.Task description 3", 3, State.NEW);
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
        Task task1 = new Task("Test TaskClasses.Task 1", "Test TaskClasses.Task description 1", 1, State.NEW);
        Task task2 = new Task("Test TaskClasses.Task 2", "Test TaskClasses.Task description 2", 2, State.NEW);
        Task task3 = new Task("Test TaskClasses.Task 3", "Test TaskClasses.Task description 3", 3, State.NEW);
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
        assertEquals(savedHistory, history, "Не удалось вернуть историю просмотра корректно.");
    }
}