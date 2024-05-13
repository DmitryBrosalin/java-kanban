public class Task {
    String name;
    String description;
    int id = 0;
    State state;

    public Task(String name, String description, State state) {
        this.name = name;
        this.description = description;
        this.state = state;
    }

    @Override
    public String toString() {
        return "Задача - " +
                "Название: '" + name + '\'' +
                ", Описание: '" + description + '\'' +
                ", id: " + id +
                ", Статус: " + state;
    }
}
