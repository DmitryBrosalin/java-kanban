import managers.InMemoryTaskManager;
import managers.TaskManager;
import org.junit.jupiter.api.Test;

public class InMemoryTaskManagerTest extends TaskManagerTest {
    @Test
    void addNewTaskAndCheckTimeConflictAndGetTaskAndGetTasks() {
        TaskManager taskManager = new InMemoryTaskManager();
        super.addNewTaskAndCheckTimeConflictAndGetTaskAndGetTasks(taskManager);
    }

    @Test
    void addNewEpicAndGetEpicAndGetEpics() {
        TaskManager taskManager = new InMemoryTaskManager();
        super.addNewEpicAndGetEpicAndGetEpics(taskManager);
    }

    @Test
    void addNewSubtaskAndGetSubtaskAndGetSubtasks() {
        TaskManager taskManager = new InMemoryTaskManager();
        super.addNewSubtaskAndGetSubtaskAndGetSubtasks(taskManager);
    }

    @Test
    void removeTask() {
        TaskManager taskManager = new InMemoryTaskManager();
        super.removeTask(taskManager);
    }

    @Test
    void removeEpicWithNoSubtasks() {
        TaskManager taskManager = new InMemoryTaskManager();
        super.removeEpicWithNoSubtasks(taskManager);
    }

    @Test
    void removeEpicWithSubtask() {
        TaskManager taskManager = new InMemoryTaskManager();
        super.removeEpicWithSubtask(taskManager);
    }

    @Test
    void removeSubtask() {
        TaskManager taskManager = new InMemoryTaskManager();
        super.removeSubtask(taskManager);
    }

    @Test
    void removeAllTasks() {
        TaskManager taskManager = new InMemoryTaskManager();
        super.removeAllTasks(taskManager);
    }

    @Test
    void getSubtasksForEpic() {
        TaskManager taskManager = new InMemoryTaskManager();
        super.getSubtasksForEpic(taskManager);
    }

    @Test
    void updateTask() {
        TaskManager taskManager = new InMemoryTaskManager();
        super.updateTask(taskManager);
    }

    @Test
    void updateEpicWithNoSubtasks() {
        TaskManager taskManager = new InMemoryTaskManager();
        super.updateEpicWithNoSubtasks(taskManager);
    }

    @Test
    void updateEpicWithSubtasks() {
        TaskManager taskManager = new InMemoryTaskManager();
        super.updateEpicWithSubtasks(taskManager);
    }

    @Test
    void updateOneSubtaskForOneEpicAndCheckEpicStateAndTime() {
        TaskManager taskManager = new InMemoryTaskManager();
        super.updateOneSubtaskForOneEpicAndCheckEpicStateAndTime(taskManager);
    }

    @Test
    void updateTwoSubtasksForOneEpicAndCheckEpicStateAndTime() {
        TaskManager taskManager = new InMemoryTaskManager();
        super.updateTwoSubtasksForOneEpicAndCheckEpicStateAndTime(taskManager);
    }

    @Test
    void updateOneSubtaskForTwoEpicsAndCheckEpicState() {
        TaskManager taskManager = new InMemoryTaskManager();
        super.updateOneSubtaskForTwoEpicsAndCheckEpicState(taskManager);
    }

    @Test
    void updateTwoSubtasksForTwoEpicsAndCheckEpicState() {
        TaskManager taskManager = new InMemoryTaskManager();
        super.updateTwoSubtasksForTwoEpicsAndCheckEpicState(taskManager);
    }

    @Test
    void getPrioritizedTasks() {
        TaskManager taskManager = new InMemoryTaskManager();
        super.getPrioritizedTasks(taskManager);
    }

    @Test
    void checkTimeConflicts() {
        TaskManager taskManager = new InMemoryTaskManager();
        super.checkTimeConflicts(taskManager);
    }

    @Test
    void checkEpicState() {
        TaskManager taskManager = new InMemoryTaskManager();
        super.checkEpicState(taskManager);
    }
}
