package taskclasses;

public class Subtask extends Task {
    private int parentEpicID;

    public Subtask(String name, String description, State state, int parentEpicID) {
        super(name, description, state);
        this.parentEpicID = parentEpicID;
        this.taskType = TaskType.SUBTASK;
    }

    public Subtask(String name, String description, int id, State state, int parentEpicID) {
        super(name, description, id, state);
        this.parentEpicID = parentEpicID;
        this.taskType = TaskType.SUBTASK;
    }

    public int getParentEpicID() {
        return parentEpicID;
    }

    public void setParentEpicID(int parentEpicID) {
        this.parentEpicID = parentEpicID;
    }

    @Override
    public String toString() {
        return taskType + "," +
                name + "," +
                description + "," +
                id + "," +
                state + ","+
                parentEpicID + "\n";
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
