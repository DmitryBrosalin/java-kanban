package test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import Managers.*;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class ManagersTest {

    @Test
    void getDefault() {
        InMemoryTaskManager inMemoryTaskManager = (InMemoryTaskManager) Managers.getDefault();
        assertNotNull(inMemoryTaskManager, "Не был создан экземпляр менеджера.");
    }

    @Test
    void getDefaultHistory() {
        InMemoryHistoryManager inMemoryHistoryManager = (InMemoryHistoryManager) Managers.getDefaultHistory();
        assertNotNull(inMemoryHistoryManager, "Не был создан экземпляр менеджера.");
    }
}