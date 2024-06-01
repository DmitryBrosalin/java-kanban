import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {
    private ArrayList<Integer> subtasksID;

    public Epic(String name, String description) {
        super(name, description, State.NEW);
        subtasksID = new ArrayList<>();
    }

    public Epic(String name, String description, int id) {
        super(name, description, id, State.NEW);
        subtasksID = new ArrayList<>();
    }

    public ArrayList<Integer> getSubtasksID() {
        return subtasksID;
    }

    public void setSubtasksID(ArrayList<Integer> subtasksID) {
        this.subtasksID = subtasksID;
    }

    @Override
    public String toString() {
        return "Эпик - " +
                "Название: '" + name + '\'' +
                ", Описание: '" + description + '\'' +
                ", id: " + id +
                ", Статус: " + state +
                ", состоит из следующих подзадач (ID):\n" + subtasksID + "\n";//

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
