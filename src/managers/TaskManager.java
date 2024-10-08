package managers;

import taskclasses.Epic;
import taskclasses.Subtask;
import taskclasses.Task;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface TaskManager {
    Set<Task> getPrioritizedTasks();

    Map<Integer, Task> getTasks();

    Map<Integer, Epic> getEpics();

    Map<Integer, Subtask> getSubtasks();

    void removeAllTasks();

    Task getTask(int id);

    Epic getEpic(int id);

    Subtask getSubtask(int id);

    List<Subtask> getSubtasksForEpic(int id);

    void removeTask(int id);

    void removeEpic(int id);

    void removeSubtask(int id);

    void addNewTask(Task task);

    void addNewEpic(Epic epic);

    void addNewSubtask(Subtask subtask);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubtask(Subtask subtask);

    int generateID();

    List<Task> getHistory();

    void addToHistory(Task task);
}

