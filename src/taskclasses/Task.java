package taskclasses;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Task {
    protected final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd.MM.yy");
    protected String name;
    protected String description;
    protected Duration duration;
    protected LocalDateTime startTime;

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return startTime.plus(duration);
    }

    public TaskType getTaskType() {
        return taskType;
    }

    protected TaskType taskType;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    protected int id = 0;
    protected State state;

    public Task(String name, String description, State state) {
        this.name = name;
        this.description = description;
        this.state = state;
        this.taskType = TaskType.TASK;
    }

    public Task(String name, String description, int id, State state) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.state = state;
        this.taskType = TaskType.TASK;
    }

    public Task(String name, String description, State state, LocalDateTime startTime, Duration duration) {
        this.name = name;
        this.description = description;
        this.state = state;
        this.taskType = TaskType.TASK;
        this.startTime = startTime;
        this.duration = duration;
    }

    public Task(String name, String description, int id, State state, LocalDateTime startTime, Duration duration) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.state = state;
        this.taskType = TaskType.TASK;
        this.startTime = startTime;
        this.duration = duration;
    }

    @Override
    public String toString() {
        return taskType + "," +
                name + "," +
                description + "," +
                id + "," +
                state + "," +
                startTime.format(formatter) + "," +
                duration.toMinutes() + "\n";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return name.equals(task.name) &&
                description.equals(task.description) &&
                id == task.id &&
                state.equals(task.state);
    }
}
