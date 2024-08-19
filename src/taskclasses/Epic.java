package taskclasses;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {
    private List<Integer> subtasksID;

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    LocalDateTime endTime;

    public Epic(String name, String description) {
        super(name, description, State.NEW);
        subtasksID = new ArrayList<>();
        this.taskType = TaskType.EPIC;
    }

    public Epic(String name, String description, int id, State state) {
        super(name, description, id, state);
        subtasksID = new ArrayList<>();
        this.taskType = TaskType.EPIC;
    }

    public void setSubtasksID(List<Integer> subtasksID) {
        this.subtasksID = subtasksID;
    }

    public List<Integer> getSubtasksID() {
        return subtasksID;
    }

    public void addSubtasksID(Integer subtaskID) {
        this.subtasksID.add(subtaskID);
    }

    @Override
    public String toString() {
        return taskType + "," +
                name + "," +
                description + "," +
                id + "," +
                state + "\n";
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Epic epic = (Epic) o;
        return name.equals(epic.name) &&
                description.equals(epic.description) &&
                id == epic.id &&
                state.equals(epic.state) &&
                Objects.equals(subtasksID, epic.subtasksID);
    }
}
