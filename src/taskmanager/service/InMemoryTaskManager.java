package taskmanager.service;

import taskmanager.enums.Status;
import taskmanager.model.Epic;
import taskmanager.model.Subtask;
import taskmanager.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;



public class InMemoryTaskManager implements TaskManager  {
    InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
    private int id;
    public int getId() {
        setId(++id);
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();


    // taskmanager.model.Task methods
    @Override
    public void addTask(Task task) {
        task.setId(getId());
        tasks.put(task.getId(), task);
    }
    @Override
    public List<Task> getTaskList() {
        List<Task> taskList = new ArrayList<>();
        for (Integer key : tasks.keySet()) {
            taskList.add(tasks.get(key));
        }
        return taskList;
    }
    @Override
    public Task getTaskById(int id) {
        historyManager.add(tasks.get(id));
        return tasks.get(id);
    }
    @Override
    public void deleteAllTasks() {
        tasks.clear();
        System.out.println("Все задания удалены!");
    }
    @Override
    public void deleteByTaskId(int id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
        } else {
            System.out.println("Задачи под ID: " + id + " нет в списке");
        }
    }
    @Override
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }



    // taskmanager.model.Epic methods
    @Override
    public void addEpic(Epic epic) {
        epic.setId(getId());
        epics.put(epic.getId(), epic);
    }
    @Override
    public List<Epic> getEpicList() {
        List<Epic> epicList = new ArrayList<>();
        for (Integer key : epics.keySet()) {
            epicList.add(epics.get(key));
        }
        return epicList;
    }
    @Override
    public Epic getEpicById(int id) {
        historyManager.add(epics.get(id));
        return epics.getOrDefault(id, null);
    }
    @Override
    public void deleteAllEpics() {
        epics.clear();
        subtasks.clear();
        System.out.println("Все эпики удалены!");
    }
    @Override
    public void deleteByEpicId(int id) {
        if (epics.containsKey(id)) {
            for (Subtask sub : epics.get(id).getSubtasksList()) {
                subtasks.remove(sub.getId()); // удаляем все subtasks из Map, которые лежат в этом эпике
            }
            epics.remove(id); // удаляем сам Epic из Map
        } else {
            System.out.println("Эпика под ID: " + id + " нет в списке");
        }
    }
    @Override
    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }



    // taskmanager.model.Subtask methods
    @Override
    public void addSubtask(Subtask subtask) {
        subtask.setId(getId());
        epics.get(subtask.getEpicId()).getSubtasksList().add(subtask);
        subtasks.put(subtask.getId(), subtask);
        updateEpicStatus(subtask.getEpicId());
    }
    @Override
    public List<Subtask> getSubtaskList() {
        List<Subtask> subtaskList = new ArrayList<>();
        for (Integer key : subtasks.keySet()) {
            subtaskList.add(subtasks.get(key));
        }
        return subtaskList;
    }
    @Override
    public Subtask getSubtaskById(int id) {
        historyManager.add(subtasks.get(id));
        return subtasks.getOrDefault(id, null);
    }
    @Override
    public void deleteAllSubtasks() {
        subtasks.clear();
        for (Integer key : epics.keySet()) {
            epics.get(key).getSubtasksList().clear(); // очищаем у всех эпиков SubtasksList
            epics.get(key).setStatus(Status.NEW); // всем эпикам присваиваем статус NEW
        }
        System.out.println("Все подзадачи удалены!");
    }
    @Override
    public void deleteBySubtaskId(int id) {
        if (subtasks.containsKey(id)) {
            for (Subtask sub : epics.get(subtasks.get(id).getEpicId()).getSubtasksList()) {
                if (sub.getId() == id) {
                    epics.get(subtasks.get(id).getEpicId()).getSubtasksList().remove(sub); // удаляем из SubtasksList у эпика
                    break;
                }
            }
            updateEpicStatus(subtasks.get(id).getEpicId());
            subtasks.remove(id); // удаляем из Map
        } else {
            System.out.println("Задачи под ID: " + id + " нет в списке");
        }
    }
    @Override
    public void updateSubtask(Subtask subtask) {
        // Обновляем subtask в SubtasksList
        for (Subtask sub : epics.get(subtask.getEpicId()).getSubtasksList()) {
            if (sub.getId().equals(subtask.getId())) {
                epics.get(subtask.getEpicId()).getSubtasksList().remove(sub);
                epics.get(subtask.getEpicId()).getSubtasksList().add(subtask);
                break;
            }
        }
        subtasks.put(subtask.getId(), subtask); // обновляем subtask в Map
        updateEpicStatus(subtask.getEpicId());
    }
    @Override
    public List<Subtask> getSubtaskListByEpic(Integer epicId) {
        return epics.get(epicId).getSubtasksList();
    }

    public boolean isAllStatusEqual(List<Subtask> subtasksList, Status status) {
        for (Subtask sub : subtasksList) {
            if (!sub.getStatus().equals(status)) {
                return false;
            }
        }
        return true;
    }

    public void updateEpicStatus(int id) {
        if ((epics.get(id).getSubtasksList()).isEmpty()
                || isAllStatusEqual((epics.get(id).getSubtasksList()),
                Status.NEW)) {
            epics.get(id).setStatus(Status.NEW);
        } else if (isAllStatusEqual((epics.get(id).getSubtasksList()),
                Status.DONE)) {
            epics.get(id).setStatus(Status.DONE);
        } else {
            epics.get(id).setStatus(Status.IN_PROGRESS);
        }
    }
}

