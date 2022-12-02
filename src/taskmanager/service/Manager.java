package taskmanager.service;

import taskmanager.model.Epic;
import taskmanager.enums.Status;
import taskmanager.model.Subtask;
import taskmanager.model.Task;

import java.util.List;
import java.util.HashMap;

public class Manager {
    private int id;
    public int getId() {
        setId(++id);
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    HashMap<Integer, Task> tasks = new HashMap<>();
    HashMap<Integer, Epic> epics = new HashMap<>();
    HashMap<Integer, Subtask> subtasks = new HashMap<>();

    // taskmanager.model.Task methods
    public void addTask(Task task) {
        task.setId(getId());
        tasks.put(task.getId(), task);
    }

    public String getTaskList() {
        String result = "";
        for (Integer key : tasks.keySet()) {
            result += tasks.get(key) + "\n";
        }
        return result;
    }

    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public void deleteAllTasks() {
        tasks.clear();
        System.out.println("Все задания удалены!");
    }

    public void deleteByTaskId(int id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
        } else {
            System.out.println("Задачи под ID: " + id + " нет в списке");
        }
    }

    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }



    // taskmanager.model.Epic methods
    public void addEpic(Epic epic) {
        epic.setId(getId());
        epics.put(epic.getId(), epic);
    }

    public String getEpicList() {
        String result = "";
        for (Integer key : epics.keySet()) {
            result += epics.get(key) + "\n";
        }
        return result;
    }

    public Epic getEpicById(int id) {
        return epics.getOrDefault(id, null);
    }

    public void deleteAllEpics() {
        epics.clear();
        subtasks.clear();
        System.out.println("Все эпики удалены!");
    }

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

    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }



    // taskmanager.model.Subtask methods
    public void addSubtask(Subtask subtask) {
        subtask.setId(getId());
        epics.get(subtask.getEpicId()).getSubtasksList().add(subtask);
        subtasks.put(subtask.getId(), subtask);
        updateSubtask(subtask);
    }

    public String getSubtaskList() {
        String result = "";
        for (Integer key : subtasks.keySet()) {
            result += subtasks.get(key) + "\n";
        }
        return result;
    }

    public Subtask getSubtaskById(int id) {
        return subtasks.getOrDefault(id, null);
    }

    public void deleteAllSubtasks() {
        subtasks.clear();
        for (Integer key : epics.keySet()) {
            epics.get(key).getSubtasksList().clear(); // очищаем у всех эпиков SubtasksList
            epics.get(key).setStatus(Status.NEW); // всем эпикам присваиваем статус NEW
        }
        System.out.println("Все подзадачи удалены!");
    }

    public void deleteBySubtaskId(int id) {
        if (subtasks.containsKey(id)) {
            for (Subtask sub : epics.get(subtasks.get(id).getEpicId()).getSubtasksList()) {
                if (sub.getId() == id) {
                    epics.get(subtasks.get(id).getEpicId()).getSubtasksList().remove(sub); // удаляем из SubtasksList у эпика
                    break;
                }
            }
            // Обновляем статус эпика
            if ((epics.get(subtasks.get(id).getEpicId()).getSubtasksList()).isEmpty()
                    || isAllStatusEqual((epics.get(subtasks.get(id).getEpicId()).getSubtasksList()),
                    Status.NEW)) {
                epics.get(subtasks.get(id).getEpicId()).setStatus(Status.NEW);
            } else if (isAllStatusEqual((epics.get(subtasks.get(id).getEpicId()).getSubtasksList()),
                    Status.DONE)) {
                epics.get(subtasks.get(id).getEpicId()).setStatus(Status.DONE);
            } else {
                epics.get(subtasks.get(id).getEpicId()).setStatus(Status.IN_PROGRESS);
            }
            subtasks.remove(id); // удаляем из Map
        } else {
            System.out.println("Задачи под ID: " + id + " нет в списке");
        }
    }

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
        // Обновляем статус эпика
        if (isAllStatusEqual((epics.get(subtask.getEpicId()).getSubtasksList()), Status.DONE)) {
            epics.get(subtask.getEpicId()).setStatus(Status.DONE);
        } else if (isAllStatusEqual((epics.get(subtask.getEpicId()).getSubtasksList()), Status.NEW)) {
            epics.get(subtask.getEpicId()).setStatus(Status.NEW);
        } else {
            epics.get(subtask.getEpicId()).setStatus(Status.IN_PROGRESS);
        }
    }

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
}
