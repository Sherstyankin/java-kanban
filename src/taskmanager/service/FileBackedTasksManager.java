package taskmanager.service;

import taskmanager.enums.Status;
import taskmanager.enums.TaskType;
import taskmanager.exceptions.ManagerSaveException;
import taskmanager.model.Epic;
import taskmanager.model.Subtask;
import taskmanager.model.Task;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTaskManager {

    private Path path;

    public FileBackedTasksManager() {
    }

    public FileBackedTasksManager(Path path) {
        this.path = path;
    }

    //Task methods
    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public Task getTaskById(int id) {
        Task task = super.getTaskById(id);
        save();
        return task;
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteByTaskId(int id) {
        super.deleteByTaskId(id);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }


    //Epic methods
    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = super.getEpicById(id);
        save();
        return epic;
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void deleteByEpicId(int id) {
        super.deleteByEpicId(id);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }


    //Subtask methods
    @Override
    public void addSubtask(Subtask subtask) {
        super.addSubtask(subtask);
        save();
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = super.getSubtaskById(id);
        save();
        return subtask;
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    @Override
    public void deleteBySubtaskId(int id) {
        super.deleteBySubtaskId(id);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    public void save() {
        try (Writer fileWriter = new FileWriter(path.toFile())) {
            fileWriter.write("id,type,name,status,description,duration,startTime,endTime,epic\n");

            for (Task task : getTaskList()) {
                fileWriter.write(toString(task));
            }
            for (Epic epic : getEpicList()) {
                fileWriter.write(toString(epic));
            }
            for (Subtask subtask : getSubtaskList()) {
                fileWriter.write(toString(subtask));
            }
            fileWriter.write("\n");
            fileWriter.write(historyToString(historyManager));
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при записи в файл " + e.getMessage());
        }
    }

    public static FileBackedTasksManager loadFromFile(Path path) {
        FileBackedTasksManager manager = new FileBackedTasksManager(path);
        try {
            String fileToString = Files.readString(path);
            String[] lines = fileToString.split("\r?\n|\r");
            if (lines.length == 0) {
                return manager; // возвращает менеджер, когда файл создан, но он пустой
            }
            for (int i = 1; i < lines.length; i++) {
                if (!lines[i].isBlank()) {
                    Task task = manager.fromString(lines[i]);
                    if (task.getType() == TaskType.TASK) {
                        manager.getTasksMap().put(task.getId(), task);
                    } else if (task.getType() == TaskType.EPIC) {
                        manager.getEpicsMap().put(task.getId(), (Epic) task);
                    } else {
                        manager.getEpicsMap().get(((Subtask) task).getEpicId()).getSubtasksList().add((Subtask) task);
                        manager.getSubtasksMap().put(task.getId(), (Subtask) task);
                        manager.updateEpicStatus(((Subtask) task).getEpicId());
                    }
                    manager.setId(task.getId());
                } else {
                    List<Integer> tasksIdList = historyFromString(lines[i + 1]);
                    for (Integer id : tasksIdList) {
                        if (manager.getTasksMap().containsKey(id)) {
                            manager.historyManager.add(manager.getTasksMap().get(id));
                        } else if (manager.getEpicsMap().containsKey(id)) {
                            manager.historyManager.add(manager.getEpicsMap().get(id));
                        } else if (manager.getSubtasksMap().containsKey(id)) {
                            manager.historyManager.add(manager.getSubtasksMap().get(id));
                        }
                    }
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return manager;
    }

    private String toString(Task task) {
        return task.toStringForFile();
    }

    private Task fromString(String line) {
        String[] lineContents = line.split(",");
        if (lineContents[1].equals("TASK")) {
            Task task = new Task(
                    lineContents[2],
                    lineContents[4],
                    Long.parseLong(lineContents[5]),
                    lineContents[6].equals("null") ? null : LocalDateTime.parse(lineContents[6]));
            task.setId(Integer.parseInt(lineContents[0]));
            task.setStatus(Status.valueOf(lineContents[3]));
            return task;
        } else if (lineContents[1].equals("EPIC")) {
            Task task = new Epic(lineContents[2], lineContents[4], new ArrayList<>());
            task.setId(Integer.parseInt(lineContents[0]));
            task.setStatus(Status.valueOf(lineContents[3]));
            return task;
        } else {
            Task task = new Subtask(
                    lineContents[2],
                    lineContents[4],
                    Long.parseLong(lineContents[5]),
                    lineContents[6].equals("null") ? null : LocalDateTime.parse(lineContents[6]),
                    Integer.parseInt(lineContents[8])
            );
            task.setId(Integer.parseInt(lineContents[0]));
            task.setStatus(Status.valueOf(lineContents[3]));
            return task;
        }
    }

    private static String historyToString(HistoryManager manager) {
        List<Task> historyList = manager.getHistory();
        StringBuilder sbHistory = new StringBuilder();
        for (Task task : historyList) {
            sbHistory.append(task.getId()).append(",");
        }
        //Удаляем последнюю запятую
        if (sbHistory.length() != 0 && sbHistory.substring(sbHistory.length() - 1).equals(",")) {
            sbHistory.deleteCharAt(sbHistory.length() - 1);
        }
        return sbHistory.toString();
    }

    private static List<Integer> historyFromString(String line) {
        List<Integer> tasksIdList = new ArrayList<>();
        String[] lineContents = line.split(",");
        for (String id : lineContents) {
            tasksIdList.add(Integer.parseInt(id));
        }
        return tasksIdList;
    }
}
