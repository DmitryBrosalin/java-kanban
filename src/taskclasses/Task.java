package taskclasses;

public class Task {
    protected String name;
    protected String description;

    public TaskType getTaskType() {
        return taskType;
    }

    public void setTaskType(TaskType taskType) {
        this.taskType = taskType;
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

    @Override
    public String toString() {
        return taskType + "," +
                name + "," +
                description + "," +
                id + "," +
                state + "\n";
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
