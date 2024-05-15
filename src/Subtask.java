public class Subtask extends Task {
    private int parentEpicID;

    public Subtask (String name, String description, State state, int parentEpicID) {
        super (name, description, state);
        this.parentEpicID = parentEpicID;
    }

    public Subtask (String name, String description, int id, State state, int parentEpicID) {
        super (name, description, id, state);
        this.parentEpicID = parentEpicID;
    }

    public int getParentEpicID() {
        return parentEpicID;
    }

    public void setParentEpicID(int parentEpicID) {
        this.parentEpicID = parentEpicID;
    }

    @Override
    public String toString() {
        return "Подзадача - " +
                "Название: '" + name + '\'' +
                ", Описание: '" + description + '\'' +
                ", id родительского Эпика: " + parentEpicID + ", " +
                ", id: " + id +
                ", Статус: " + state + "\n";
    }
}
