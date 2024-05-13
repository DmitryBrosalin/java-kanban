public class Subtask extends Task {
    int parentEpicID;

    public Subtask (String name, String description, State state, int parentEpicID) {
        super (name, description, state);
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
