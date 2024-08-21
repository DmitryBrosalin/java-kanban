package taskclasses;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
    private int parentEpicID;

    public Subtask(String name, String description, State state, int parentEpicID,
                   LocalDateTime startTime, Duration duration) {
        super(name, description, state);
        this.parentEpicID = parentEpicID;
        this.taskType = TaskType.SUBTASK;
        this.startTime = startTime;
        this.duration = duration;
    }

    public Subtask(String name, String description, int id, State state, int parentEpicID) {
        super(name, description, id, state);
        this.parentEpicID = parentEpicID;
        this.taskType = TaskType.SUBTASK;
    }

    public Subtask(String name, String description, State state, int parentEpicID) {
        super(name, description, state);
        this.parentEpicID = parentEpicID;
        this.taskType = TaskType.SUBTASK;
    }

    public Subtask(String name, String description, int id, State state, int parentEpicID,
                   LocalDateTime startTime, Duration duration) {
        super(name, description, id, state);
        this.parentEpicID = parentEpicID;
        this.taskType = TaskType.SUBTASK;
        this.startTime = startTime;
        this.duration = duration;
    }

    public int getParentEpicID() {
        return parentEpicID;
    }

    @Override
    public String toString() {
        return taskType + "," +
                name + "," +
                description + "," +
                id + "," +
                state + "," +
                parentEpicID + "," +
                startTime.format(formatter) + "," +
                duration.toMinutes() + "\n";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Subtask subtask = (Subtask) o;
        return name.equals(subtask.name) &&
                description.equals(subtask.description) &&
                id == subtask.id &&
                state.equals(subtask.state) &&
                parentEpicID == subtask.getParentEpicID();
    }
}
