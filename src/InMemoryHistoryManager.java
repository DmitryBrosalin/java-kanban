import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private LinkedHashMapOfTasks<Integer, Task> history;


    public InMemoryHistoryManager() {
        history = new LinkedHashMapOfTasks<>();
    }

    @Override
    public void addToHistory(Task task) {
        history.put(task.id, task);
    }

    @Override
    public List<Task> getHistory() {
        List<Task> historyList = new ArrayList<>();
        for (Task task: history.values()) {
            historyList.add(task);
        }
        return historyList;
    }

    @Override
    public void removeTask(int id) {
        history.remove(id);
    }

    static class LinkedHashMapOfTasks<Integer, Task> extends LinkedHashMap<Integer, Task> {
        @Override
        public Task put(Integer id, Task task) {
            if (this.containsValue(task)) {
                this.remove(id, task);
                }
            return super.put(id, task);
        }
    }
}
