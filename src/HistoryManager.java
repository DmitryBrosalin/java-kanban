import java.util.List;

public interface HistoryManager {
    void addToHistory(Task task);
    void removeTask(int id);
    List<Task> getHistory();
}
