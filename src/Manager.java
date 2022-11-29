import java.util.ArrayList;
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

    // Task methods
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



    // Epic methods
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
            for (Subtask sub : epics.get(id).getSubtasksArrayList()) {
                    subtasks.remove(sub.getId());
                }
            epics.remove(id);
        } else {
            System.out.println("Задачи под ID: " + id + " нет в списке");
        }
    }

    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }



    // Subtask methods
    public void addSubtask(Subtask subtask) {
        subtask.setId(getId());
        epics.get(subtask.getEpicId()).getSubtasksArrayList().add(subtask);
        subtasks.put(subtask.getId(), subtask);
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
            epics.get(key).getSubtasksArrayList().clear();
        }
        System.out.println("Все задания удалены!");
    }

    public void deleteBySubtaskId(int id) {
        if (subtasks.containsKey(id)) {
            for (Subtask sub : epics.get(subtasks.get(id).getEpicId()).getSubtasksArrayList()) {
                if (sub.getId() == id) {
                    epics.get(subtasks.get(id).getEpicId()).getSubtasksArrayList().remove(sub);
                }
            }
            subtasks.remove(id);
        } else {
            System.out.println("Задачи под ID: " + id + " нет в списке");
        }
    }

    public void updateSubtask(Subtask subtask) {
        epics.get(subtask.getEpicId()).getSubtasksArrayList().add(subtask);
        subtasks.put(subtask.getId(), subtask);
        if (subtask.getStatus().equals("IN_PROGRESS")) {
            epics.get(subtask.getEpicId()).setStatus("IN_PROGRESS");
        }
        if (subtask.getStatus().equals("DONE")) {
            if (isAllTasksDone(epics.get(subtask.getEpicId()).getSubtasksArrayList())) {
                epics.get(subtask.getEpicId()).setStatus("DONE");
            }
        }
    }

    public ArrayList<Subtask> getSubtaskListByEpic(Integer epicId) {
        return epics.get(epicId).getSubtasksArrayList();
    }

    public boolean isAllTasksDone(ArrayList<Subtask> subtasksArrayList) {
        for (Subtask sub : subtasksArrayList) {
            if (!sub.getStatus().equals("DONE")) {
                return false;
            }
        }
    return true;
    }
}
