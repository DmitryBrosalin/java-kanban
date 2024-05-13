import java.util.HashMap;
import java.util.ArrayList;

public class TaskManager {
    HashMap<Integer, Task> tasks;
    int counter;

    public TaskManager() {
        tasks = new HashMap<>();
        counter = 1;
    }

    public String getAllTasks() {
        String result;
        result = "Список задач:\n";
        for (Task task : tasks.values()) {
            if (task.getClass() == Task.class) {
                result +=  (Task) task + "\n";
            }
        }
        for (Object o : tasks.values()) {
            if (o.getClass() == Epic.class) {
                result += (Epic) o;
            }
        }
        return result;
    }

    public String removeAllTasks() {
        tasks.clear();
        return "Список задач очищен.";
    }

    public String getTask(int id) {
        String result = "";
        if (tasks.containsKey(id)) {
            result += tasks.get(id);
        } else {
            result = "Задачи с id="+id+" нет.";
        }
        return result;
    }

    public String getSubtasksForEpic(int id) {
        String result = "";
        if (tasks.containsKey(id)) {
            if (tasks.get(id).getClass() == Epic.class) {
                result += "Эпик: " + ((Epic) tasks.get(id)).name + ", состоит из следующих подзадач:\n";
                for (Subtask subtask : ((Epic) tasks.get(id)).subtasks.values()) {
                    result +=  subtask + "\n";
                }
            } else {
                result = "Задача под id="+id+" не является Эпиком.";
            }
        } else {
            result = "Эпика с id="+id+" нет.";
        }
        return result;
    }

    public String removeTask(int id) {
        String result;
        if (tasks.containsKey(id)) {
            Task task = tasks.get(id);
            if (task.getClass() == Subtask.class) {
                int parentEpicID = ((Subtask) task).parentEpicID;
                ((Epic) tasks.get(parentEpicID)).subtasks.remove(task);

                checkEpicState((Epic) tasks.get(parentEpicID));
            }
            tasks.remove(id);

            result = "Задача с id="+id+" успешно удалена.";
        } else {
            result = "Задачи с id="+id+" нет.";
        }
        return result;
    }

    public void addNewTask(Task task) {
        tasks.put(task.id, task);
    }

    public void addNewEpic(Epic epic) {
        tasks.put(epic.id, epic);
    }

    public void addNewSubtask(Subtask subtask) {
        Task parentEpic = tasks.get(subtask.parentEpicID);
        ((Epic) parentEpic).subtasks.put(subtask.id, subtask);
        tasks.put(subtask.id, subtask);

        checkEpicState((Epic) parentEpic);
    }

    public void updateTask(int id, Task task) {
        task.id = id;
        tasks.replace(id, task);
    }

    public void updateSubtask(int id, Subtask subtask) {
        subtask.id = id;
        tasks.replace(id, subtask);
        Epic parentEpic = (Epic) tasks.get(subtask.parentEpicID);
        parentEpic.subtasks.replace(id, subtask);
    }

    public int generateID(){
        return counter++;
    }

    public Epic checkEpicState(Epic epic) {
        ArrayList<State> epicSubtasksStates = new ArrayList<>();
        for (Subtask sub : epic.subtasks.values()) {
            epicSubtasksStates.add(sub.state);
        }
        if (!(epicSubtasksStates.contains(State.DONE) ||
                epicSubtasksStates.contains(State.IN_PROGRESS))) {
            epic.state = State.NEW;
        } else if (!(epicSubtasksStates.contains(State.NEW) ||
                epicSubtasksStates.contains(State.IN_PROGRESS))) {
            epic.state = State.DONE;
        } else epic.state = State.IN_PROGRESS;
        return epic;
    }
}
