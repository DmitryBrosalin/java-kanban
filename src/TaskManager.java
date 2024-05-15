import java.util.HashMap;
import java.util.ArrayList;

public class TaskManager {
    HashMap<Integer, Task> tasks;
    HashMap<Integer, Epic> epics;
    HashMap<Integer, Subtask> subtasks;
    int counter;

    public TaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
        counter = 1;
    }

    public HashMap<Integer, Task> getTasks() {
        return tasks;
    }

    public HashMap<Integer, Epic> getEpics() {
        return epics;
    }

    public HashMap<Integer, Subtask> getSubtasks() {
        return subtasks;
    }

    public void removeAllTasks() {
        tasks.clear();
        epics.clear();
        subtasks.clear();
    }

    public Task getTask(int id) {
        return tasks.get(id);
    }

    public Epic getEpic(int id) {
        return epics.get(id);
    }

    public Subtask getSubtask(int id) {
        return subtasks.get(id);
    }

    public ArrayList<Subtask> getSubtasksForEpic(int id) {
        ArrayList<Subtask> subtasksForEpic = new ArrayList<>();
        Epic parentEpic = epics.get(id);
        ArrayList<Integer> subtasksID = parentEpic.getSubtasksID();
        for (int subtaskID: subtasksID) {
            subtasksForEpic.add(subtasks.get(subtaskID));
        }
        return subtasksForEpic;
    }
    public void removeTask(int id) {
            tasks.remove(id);
    }

    public void removeEpic(int id) {
        for (Integer subtaskID : epics.get(id).getSubtasksID()) {
            subtasks.remove(subtaskID);
        }
        epics.remove(id);
    }

    public void removeSubtask(int id) {
        Subtask subtask = subtasks.get(id);
        Epic parentEpic = epics.get(subtask.getParentEpicID());
        ArrayList<Integer> subtasksID= parentEpic.getSubtasksID();
        subtasksID.remove(id);
        subtasks.remove(id);
        checkEpicState(parentEpic);
    }

    public void addNewTask(Task task) {
        task.id = generateID();
        tasks.put(task.id, task);
    }

    public void addNewEpic(Epic epic) {
        epic.id = generateID();
        epics.put(epic.id, epic);
    }

    public void addNewSubtask(Subtask subtask) {
        subtask.id = generateID();
        Epic parentEpic = epics.get(subtask.getParentEpicID());
        ArrayList<Integer> subtasksID= parentEpic.getSubtasksID();
        subtasksID.add(subtask.id);
        subtasks.put(subtask.id, subtask);

        checkEpicState(parentEpic);
    }

    public void updateTask(Task task) {
        tasks.replace(task.id, task);
    }

    public void updateEpic(Epic epic) {
        epics.replace(epic.id, epic);
    }

    public void updateSubtask(Subtask subtask) {
        subtasks.replace(subtask.id, subtask);
        Epic parentEpic = epics.get(subtask.getParentEpicID());
        parentEpic.getSubtasksID().add(subtask.id);
        checkEpicState(parentEpic);
    }

    public int generateID(){
        return counter++;
    }

    public void checkEpicState(Epic epic) {
        ArrayList<State> epicSubtasksStates = new ArrayList<>();
        for (Integer subID : epic.getSubtasksID()) {
            epicSubtasksStates.add(subtasks.get(subID).state);
        }
        if (!(epicSubtasksStates.contains(State.DONE) ||
                epicSubtasksStates.contains(State.IN_PROGRESS))) {
            epic.state = State.NEW;
        } else if (!(epicSubtasksStates.contains(State.NEW) ||
                epicSubtasksStates.contains(State.IN_PROGRESS))) {
            epic.state = State.DONE;
        } else epic.state = State.IN_PROGRESS;
    }
}
