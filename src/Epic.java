import java.util.HashMap;

public class Epic extends Task {
    HashMap<Integer, Subtask> subtasks;

    public Epic(String name, String description) {
        super(name, description, State.NEW);
        subtasks = new HashMap<>();
    }

    public String printSubtasks(HashMap<Integer, Subtask> subtasks) {
        String result = "";
        for (Subtask subtask: subtasks.values()) {
            result += subtask;
        }
        return result;
    }

    @Override
    public String toString() {
        return "Эпик - " +
                "Название: '" + name + '\'' +
                ", Описание: '" + description + '\'' +
                ", id: " + id +
                ", Статус: " + state +
                ", состоит из следующих подзадач:\n" + printSubtasks(subtasks) + "\n";
    }
}
