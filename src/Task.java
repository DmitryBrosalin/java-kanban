public class Task {
    protected String name;
    protected String description;
    protected int id = 0;
    protected State state;

    public Task(String name, String description, State state) {
        this.name = name;
        this.description = description;
        this.state = state;
    }

    public Task(String name, String description, int id, State state) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.state = state;
    }

    @Override
    public String toString() {
        return "Задача - " +
                "Название: '" + name + '\'' +
                ", Описание: '" + description + '\'' +
                ", id: " + id +
                ", Статус: " + state + "\n";
    }
}
