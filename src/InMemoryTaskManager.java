import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private HashMap<Integer, Task> tasks;
    private HashMap<Integer, Epic> epics;
    private HashMap<Integer, Subtask> subtasks;
    private int counter;

    public InMemoryTaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
        counter = 1;
    }

    @Override
    public HashMap<Integer, Task> getTasks() {
        return tasks;
    }

    @Override
    public HashMap<Integer, Epic> getEpics() {
        return epics;
    }

    @Override
    public HashMap<Integer, Subtask> getSubtasks() {
        return subtasks;
    }

    @Override
    public void removeAllTasks() {
        tasks.clear();
        epics.clear();
        subtasks.clear();
    }

    @Override
    public Task getTask(int id) {
        return tasks.get(id);
    }

    @Override
    public Epic getEpic(int id) {
        return epics.get(id);
    }

    @Override
    public Subtask getSubtask(int id) {
        return subtasks.get(id);
    }

    @Override
    public ArrayList<Subtask> getSubtasksForEpic(int id) {
        ArrayList<Subtask> subtasksForEpic = new ArrayList<>();
        Epic parentEpic = epics.get(id);
        List<Integer> subtasksID = parentEpic.getSubtasksID();
        for (int subtaskID: subtasksID) {
            subtasksForEpic.add(subtasks.get(subtaskID));
        }
        return subtasksForEpic;
    }
    @Override
    public void removeTask(int id) {
            tasks.remove(id);
    }

    @Override
    public void removeEpic(int id) {
        for (Integer subtaskID : epics.get(id).getSubtasksID()) {
            subtasks.remove(subtaskID);
        }
        epics.remove(id);
    }

    @Override
    public void removeSubtask(int id) {
        Subtask subtask = subtasks.get(id);
        Epic parentEpic = epics.get(subtask.getParentEpicID());
        List<Integer> subtasksID= parentEpic.getSubtasksID();

        for (Integer subID: subtasksID) {
            if (subID == id) {
                subtasksID.remove(subID);
                break;
            }
        }
        subtasks.remove(id);
        checkEpicState(parentEpic);
    }

    @Override
    public void addNewTask(Task task) {
        task.id = generateID();
        tasks.put(task.id, task);
    }

    @Override
    public void addNewEpic(Epic epic) {
        epic.id = generateID();
        epics.put(epic.id, epic);
    }

    @Override
    public void addNewSubtask(Subtask subtask) {
        subtask.id = generateID();
        Epic parentEpic = epics.get(subtask.getParentEpicID());
        List<Integer> subtasksID= parentEpic.getSubtasksID();
        subtasksID.add(subtask.id);
        subtasks.put(subtask.id, subtask);

        checkEpicState(parentEpic);
    }

    @Override
    public void updateTask(Task task) {
        tasks.replace(task.id, task);
    }

    @Override
    public void updateEpic(Epic epic) {
        epics.replace(epic.id, epic);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        Subtask oldSubtask = subtasks.get(subtask.id);
        Epic oldParentEpic = epics.get(oldSubtask.getParentEpicID());
        oldParentEpic.getSubtasksID().remove((Object) subtask.id);
        subtasks.replace(subtask.id, subtask);
        Epic newParentEpic = epics.get(subtask.getParentEpicID());
        newParentEpic.getSubtasksID().add(subtask.id);
        checkEpicState(oldParentEpic);
        checkEpicState(newParentEpic);
    }

    @Override
    public int generateID(){
        return counter++;
    }

    @Override
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

    @Override
    public List<Task> getHistory(HistoryManager historyManager) {
        return historyManager.getHistory();
    }
}
