package managers;

import exceptions.NoParentEpicException;
import exceptions.TimeConflictException;
import taskclasses.Epic;
import taskclasses.State;
import taskclasses.Subtask;
import taskclasses.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected TreeSet<Task> prioritizedTasks;
    protected HashMap<Integer, Task> tasks;
    protected HashMap<Integer, Epic> epics;
    protected HashMap<Integer, Subtask> subtasks;
    protected int counter;
    protected HistoryManager historyManager;
    Comparator<Task> comparator = new Comparator<Task>() {
        @Override
        public int compare(Task t1, Task t2) {
            return t1.getStartTime().compareTo(t2.getStartTime());
        }
    };

    public InMemoryTaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
        counter = 1;
        historyManager = Managers.getDefaultHistory();
        prioritizedTasks = new TreeSet<>(comparator);
    }

    @Override
    public Map<Integer, Task> getTasks() {
        return tasks;
    }

    @Override
    public Map<Integer, Epic> getEpics() {
        return epics;
    }

    @Override
    public Map<Integer, Subtask> getSubtasks() {
        return subtasks;
    }

    @Override
    public void removeAllTasks() {
        tasks.clear();
        epics.clear();
        subtasks.clear();
        prioritizedTasks.clear();
    }

    @Override
    public Task getTask(int id) {
        addToHistory(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public Epic getEpic(int id) {
        addToHistory(epics.get(id));
        return epics.get(id);
    }

    @Override
    public Subtask getSubtask(int id) {
        addToHistory(subtasks.get(id));
        return subtasks.get(id);
    }

    @Override
    public List<Subtask> getSubtasksForEpic(int id) {
        Epic parentEpic = epics.get(id);
        return parentEpic.getSubtasksID().stream()
                .map(subID -> subtasks.get(subID))
                .toList();
    }

    @Override
    public void removeTask(int id) {
        Task task = tasks.get(id);
        prioritizedTasks.remove(task);
        tasks.remove(id);
        historyManager.removeTask(id);
    }

    @Override
    public void removeEpic(int id) {
        for (Integer subtaskID : epics.get(id).getSubtasksID()) {
            Subtask subtask = subtasks.get(subtaskID);
            prioritizedTasks.remove(subtask);
            subtasks.remove(subtaskID);
            historyManager.removeTask(subtaskID);
        }
        epics.remove(id);
        historyManager.removeTask(id);
    }

    @Override
    public void removeSubtask(int id) {
        Subtask subtask = subtasks.get(id);
        prioritizedTasks.remove(subtask);
        Epic parentEpic = epics.get(subtask.getParentEpicID());
        List<Integer> subtasksID = parentEpic.getSubtasksID();
        for (Integer subID: subtasksID) {
            if (subID == id) {
                subtasksID.remove(subID);
                break;
            }
        }
        subtasks.remove(id);
        historyManager.removeTask(id);
        checkEpicState(parentEpic);
        checkEpicTime(parentEpic);
    }

    @Override
    public void addNewTask(Task task) {
        if (prioritizedTasks.stream()
                .noneMatch(t -> causesTimeConflict(task, t))) {
            task.setId(generateID());
            tasks.put(task.getId(), task);
            prioritizedTasks.add(task);
        } else throw new TimeConflictException("Пересечение по времени");
    }

    @Override
    public void addNewEpic(Epic epic) {
        epic.setId(generateID());
        epics.put(epic.getId(), epic);
    }

    @Override
    public void addNewSubtask(Subtask subtask) {
        if (!epics.containsKey(subtask.getParentEpicID())) {
            throw new NoParentEpicException("Неверно указан id родительского эпика.");
        } else {
            if (prioritizedTasks.stream()
                    .noneMatch(t -> causesTimeConflict(subtask, t))) {
                subtask.setId(generateID());
                Epic parentEpic = epics.get(subtask.getParentEpicID());
                List<Integer> subtasksID = parentEpic.getSubtasksID();
                subtasksID.add(subtask.getId());
                subtasks.put(subtask.getId(), subtask);
                prioritizedTasks.add(subtask);
                checkEpicState(parentEpic);
                checkEpicTime(parentEpic);
            } else throw new TimeConflictException("Пересечение по времени");
        }
    }

    @Override
    public void updateTask(Task task) {
        prioritizedTasks.remove(tasks.get(task.getId()));
        if (prioritizedTasks.stream()
                .noneMatch(t -> causesTimeConflict(task, t))) {
            prioritizedTasks.add(task);
            tasks.replace(task.getId(), task);
        } else {
            prioritizedTasks.add(tasks.get(task.getId()));
            throw new TimeConflictException("Пересечение по времени");
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        Epic oldEpic = epics.get(epic.getId());
        epic.setSubtasksID(oldEpic.getSubtasksID());
        epic.setState(oldEpic.getState());
        epics.replace(epic.getId(), epic);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (!epics.containsKey(subtask.getParentEpicID())) {
            throw new NoParentEpicException("Неверно указан id родительского эпика.");
        } else {
            prioritizedTasks.remove(subtasks.get(subtask.getId()));
            if (prioritizedTasks.stream()
                    .noneMatch(t -> causesTimeConflict(subtask, t))) {
                Subtask oldSubtask = subtasks.get(subtask.getId());
                Epic oldParentEpic = epics.get(oldSubtask.getParentEpicID());
                oldParentEpic.getSubtasksID().remove((Object) subtask.getId());
                prioritizedTasks.add(subtask);
                subtasks.replace(subtask.getId(), subtask);
                Epic newParentEpic = epics.get(subtask.getParentEpicID());
                newParentEpic.getSubtasksID().add(subtask.getId());
                checkEpicState(oldParentEpic);
                checkEpicTime(oldParentEpic);
                checkEpicState(newParentEpic);
                checkEpicTime(newParentEpic);
            } else {
                prioritizedTasks.add(subtasks.get(subtask.getId()));
                throw new TimeConflictException("Пересечение по времени");
            }
        }
    }

    @Override
    public int generateID() {
        return counter++;
    }

    protected void checkEpicState(Epic epic) {
        List<State> epicSubtasksStates = epic.getSubtasksID().stream()
                .map(subID -> subtasks.get(subID).getState())
                .toList();
        if (!(epicSubtasksStates.contains(State.DONE) ||
                epicSubtasksStates.contains(State.IN_PROGRESS))) {
            epic.setState(State.NEW);
        } else if (!(epicSubtasksStates.contains(State.NEW) ||
                epicSubtasksStates.contains(State.IN_PROGRESS))) {
            epic.setState(State.DONE);
        } else epic.setState(State.IN_PROGRESS);
    }

    protected void checkEpicTime(Epic epic) {
        if (!epic.getSubtasksID().isEmpty()) {
            List<Subtask> epicSubtasks = epic.getSubtasksID().stream()
                    .map(subID -> subtasks.get(subID))
                    .toList();
            LocalDateTime earliestStart = epicSubtasks.getFirst().getStartTime();
            LocalDateTime latestEnd = epicSubtasks.getFirst().getEndTime();;
            Duration durationSum = Duration.ZERO;
            for (Subtask subtask : epicSubtasks) {
                durationSum = durationSum.plus(subtask.getDuration());
                if (earliestStart.isAfter(subtask.getStartTime())) {
                    earliestStart = subtask.getStartTime();
                }
                if (latestEnd.isBefore(subtask.getEndTime())) {
                    latestEnd = subtask.getEndTime();
                }
            }
            epic.setDuration(durationSum);
            epic.setStartTime(earliestStart);
            epic.setEndTime(latestEnd);
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public void addToHistory(Task task) {
        historyManager.addToHistory(task);
    }

    @Override
    public TreeSet<Task> getPrioritizedTasks() {
        TreeSet<Task> copyOfPrioritizedTasks = new TreeSet<>(comparator);
        copyOfPrioritizedTasks.addAll(prioritizedTasks);
        return copyOfPrioritizedTasks;
    }

    protected boolean causesTimeConflict(Task t1, Task t2) {
        return t2.getStartTime().isAfter(t1.getStartTime()) && t2.getStartTime().isBefore(t1.getEndTime())
                || t2.getEndTime().isAfter(t1.getStartTime()) && t2.getEndTime().isBefore(t1.getEndTime())
                || t1.getStartTime().isAfter(t2.getStartTime()) && t1.getStartTime().isBefore(t2.getEndTime())
                || t1.getStartTime().isEqual(t2.getStartTime())
                || t1.getEndTime().isEqual(t2.getEndTime());
    }
}

