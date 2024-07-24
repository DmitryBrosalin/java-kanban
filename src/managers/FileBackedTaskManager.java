package managers;

import taskclasses.Epic;
import taskclasses.State;
import taskclasses.Subtask;
import taskclasses.Task;

import java.io.*;
import java.nio.file.Paths;

public class FileBackedTaskManager extends InMemoryTaskManager {
    File backedListOfTasks;

    public FileBackedTaskManager() throws IOException {
        super();
        backedListOfTasks = Paths.get("backedListOfTasks.txt").toFile();
        loadFromFile(backedListOfTasks);
    }

    public void loadFromFile(File file) {
        try (BufferedReader bufferReader = new BufferedReader(new FileReader(file))) {
            while (bufferReader.ready()) {
                String task = bufferReader.readLine();
                getTaskFromString(task);
                counter++;
            }
        } catch (IOException ex) {
            System.out.println("Произошла ошибка во время чтения файла.");
        }
    }

    public void getTaskFromString(String stringTask) {
        String[] taskProperties = stringTask.split(",");
        switch (taskProperties[0]) {
            case "TASK" -> {
                Task task = new Task(taskProperties[1], taskProperties[2], Integer.parseInt(taskProperties[3]),
                        State.valueOf(taskProperties[4]));
                tasks.put(task.getId(), task);
                if (!task.getState().equals(State.NEW)) {
                    historyManager.addToHistory(task);
                }
            }
            case "EPIC" -> {
                Epic epic = new Epic(taskProperties[1], taskProperties[2], Integer.parseInt(taskProperties[3]),
                        State.valueOf(taskProperties[4]));
                epics.put(epic.getId(), epic);
                if (!epic.getState().equals(State.NEW)) {
                    historyManager.addToHistory(epic);
                }
            }
            case "SUBTASK" -> {
                Subtask subtask = new Subtask(taskProperties[1], taskProperties[2], Integer.parseInt(taskProperties[3]),
                        State.valueOf(taskProperties[4]), Integer.parseInt(taskProperties[5]));
                Epic parentEpic = epics.get(subtask.getParentEpicID());
                parentEpic.addSubtasksID(subtask.getId());
                subtasks.put(subtask.getId(), subtask);
                if (!subtask.getState().equals(State.NEW)) {
                    historyManager.addToHistory(subtask);
                    historyManager.addToHistory(parentEpic);
                }
            }
        }
    }

    public void save() {
        try (Writer fileWriter = new FileWriter("backedListOfTasks.txt")) {
            for (Task task: tasks.values()) {
                fileWriter.write(task.toString());
            }
            for (Epic epic: epics.values()) {
                fileWriter.write(epic.toString());
            }
            for (Subtask subtask: subtasks.values()) {
                fileWriter.write(subtask.toString());
            }
        } catch (IOException e) {
            System.out.println("Произошла ошибка во время записи файла.");
        }
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public void removeTask(int id) {
        super.removeTask(id);
        save();
    }

    @Override
    public void removeEpic(int id) {
        super.removeEpic(id);
        save();
    }

    @Override
    public void removeSubtask(int id) {
        super.removeSubtask(id);
        save();
    }

    @Override
    public void addNewTask(Task task) {
        super.addNewTask(task);
        save();
    }

    @Override
    public void addNewEpic(Epic epic) {
        super.addNewEpic(epic);
        save();
    }

    @Override
    public void addNewSubtask(Subtask subtask) {
        super.addNewSubtask(subtask);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }
}