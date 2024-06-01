import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {

    @Test
    void getDefault() {
        InMemoryTaskManager inMemoryTaskManager = Managers.getDefault();
        assertNotNull(inMemoryTaskManager, "Не был создан экземпляр менеджера.");
    }

    @Test
    void getDefaultHistory() {
        InMemoryHistoryManager inMemoryHistoryManager = Managers.getDefaultHistory();
        assertNotNull(inMemoryHistoryManager, "Не был создан экземпляр менеджера.");
    }
}