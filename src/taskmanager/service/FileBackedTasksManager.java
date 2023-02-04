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

    private final Path path;

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
   /* public static void main(String[] args) {

        TaskManager taskManager = Managers.getFileBackedTasksManager();

        //Create 2 tasks
        Task task1 = new Task("Переезд1", "Сбор вещей1", 60, LocalDateTime.of(2023, 2, 2, 13, 0));
        Task task2 = new Task("Переезд2", "Сбор вещей2", 30, LocalDateTime.of(2023, 2, 2, 13, 0));
        taskManager.addTask(task1);
        taskManager.addTask(task2);

        //Create 2 epics
        Epic epicWith3Subtask = new Epic("Aрендовать авто", "Универсал",
                new ArrayList<>());
        Epic epicWithoutSubtask = new Epic("Закупить продукты", "Не забыть про овощи",
                new ArrayList<>());
        taskManager.addEpic(epicWith3Subtask);
        taskManager.addEpic(epicWithoutSubtask);

        //Create 3 subtasks for epicWith3Subtask
        Subtask subtask1 = new Subtask("Выбрать агенство", "Оценить по рейтингу и цене",
                60, LocalDateTime.of(2023, 2, 4, 14, 0),
                epicWith3Subtask.getId());
        Subtask subtask2 = new Subtask("Отправить заявку", "Прикрепить документы",
                0, null,
                epicWith3Subtask.getId());
        Subtask subtask3 = new Subtask("Оплатить счет", "Оплатить",
                60, LocalDateTime.of(2023, 2, 4, 14, 0),
                epicWith3Subtask.getId());
        taskManager.addSubtask(subtask2);
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask3);

        //Print all tasks
        System.out.println(taskManager.getTaskList());
        System.out.println();
        //Print all epics
        System.out.println(taskManager.getEpicList());
        System.out.println();
        //Print all subtasks
        System.out.println(taskManager.getSubtaskList());
        System.out.println();
        //Print prioritizedTasks
        System.out.println(taskManager.getPrioritizedTasksList());

        //Check history list
        taskManager.getTaskById(task1.getId());
        taskManager.getSubtaskById(subtask1.getId());
        taskManager.getSubtaskById(subtask2.getId());
        taskManager.getEpicById(epicWith3Subtask.getId());
        taskManager.getEpicById(epicWithoutSubtask.getId());
        taskManager.getTaskById(task1.getId());
        taskManager.getSubtaskById(subtask2.getId());

        System.out.println(taskManager.getHistory());
        System.out.println();

        //Delete 1 task and 1 epic
        taskManager.deleteByTaskId(task1.getId());
        System.out.println(taskManager.getHistory());
        System.out.println();

        taskManager.deleteByEpicId(epicWith3Subtask.getId());
        System.out.println(taskManager.getHistory());
        System.out.println();
        //Print prioritizedTasks
        System.out.println(taskManager.getPrioritizedTasksList());


        //Новый менеджер, который подгружает информацию из файла
        TaskManager taskManager1 = Managers.getFileBackedTasksManager();
        System.out.println(taskManager1.getTaskList());
        System.out.println();
        System.out.println(taskManager1.getEpicList());
        System.out.println();
        System.out.println(taskManager1.getSubtaskList());
        System.out.println();
        System.out.println(taskManager1.getHistory());
    }*/
}
