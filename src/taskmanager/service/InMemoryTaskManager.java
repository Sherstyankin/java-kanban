package taskmanager.service;

import taskmanager.enums.Status;
import taskmanager.model.Epic;
import taskmanager.model.Subtask;
import taskmanager.model.Task;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


public class InMemoryTaskManager implements TaskManager {
    final HistoryManager historyManager = Managers.getDefaultHistory();
    private int id;

    private int getId() {
        setId(++id);
        return id;
    }

    protected void setId(int id) {
        this.id = id;
    }


    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();
    private final Set<Task> prioritizedTasks = new TreeSet<>((t1, t2) -> {
        LocalDateTime startTimeForT1 = t1.getStartTime();
        LocalDateTime startTimeForT2 = t2.getStartTime();
        if (startTimeForT1 == null) {
            return 1;
        }
        if (startTimeForT2 == null) {
            return -1;
        }
        return Integer.compare(startTimeForT1.getYear(), startTimeForT2.getYear()) +
                Integer.compare(startTimeForT1.getMonthValue(), startTimeForT2.getMonthValue()) +
                Integer.compare(startTimeForT1.getDayOfMonth(), startTimeForT2.getDayOfMonth()) +
                Integer.compare(startTimeForT1.getHour(), startTimeForT2.getHour()) +
                Integer.compare(startTimeForT1.getMinute(), startTimeForT2.getMinute());
    });

    protected Map<Integer, Task> getTasksMap() {
        return tasks;
    }

    protected Map<Integer, Epic> getEpicsMap() {
        return epics;
    }

    protected Map<Integer, Subtask> getSubtasksMap() {
        return subtasks;
    }

    @Override
    public List<Task> getPrioritizedTasksList() {
        return new ArrayList<>(prioritizedTasks);
    }


    // taskmanager.model.Task methods
    @Override
    public void addTask(Task task) {
        if (isTimeNotCrossed(task)) {
            task.setId(getId());
            prioritizedTasks.add(task); // добавляем в сет, который сортирует по startTime
            tasks.put(task.getId(), task);
        }
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
        if (tasks.get(id) != null) {
            historyManager.add(tasks.get(id));
        }
        return tasks.getOrDefault(id, null);
    }

    @Override
    public void deleteAllTasks() {
        for (Integer key : tasks.keySet()) {
            historyManager.remove(key);
        }
        for (Task value : tasks.values()) {
            prioritizedTasks.remove(value); // удаление из сета всех тасков
        }
        tasks.clear();
        System.out.println("Все задания удалены!");
    }

    @Override
    public void deleteByTaskId(int id) {
        if (tasks.containsKey(id)) {
            prioritizedTasks.remove(getTaskById(id)); // удаление из сета
            tasks.remove(id);
            historyManager.remove(id);
        } else {
            System.out.println("Задачи под ID: " + id + " нет в списке");
        }
    }

    @Override
    public void updateTask(Task task) {
        if (isTimeNotCrossed(task) && !tasks.isEmpty() &&
                tasks.getOrDefault(task.getId(), null) != null) {
            tasks.put(task.getId(), task);
            prioritizedTasks.add(task); // добавили в сет
        } else {
            System.out.println("Задача не обновлена");
        }
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
        if (epics.get(id) != null) {
            historyManager.add(epics.get(id));
        }
        return epics.getOrDefault(id, null);
    }

    @Override
    public void deleteAllEpics() {
        for (Integer key : epics.keySet()) {
            historyManager.remove(key);
        }
        epics.clear();
        for (Integer key : subtasks.keySet()) {
            historyManager.remove(key);
        }
        for (Subtask value : subtasks.values()) {
            prioritizedTasks.remove(value); // удаляем из сета
        }
        subtasks.clear();
        System.out.println("Все эпики удалены!");
    }

    @Override
    public void deleteByEpicId(int id) {
        if (epics.containsKey(id)) {
            for (Subtask sub : epics.get(id).getSubtasksList()) {
                prioritizedTasks.remove(getSubtaskById(sub.getId())); // Удаляем из сета
                subtasks.remove(sub.getId()); // удаляем все subtasks из Map, которые лежат в этом эпике
                historyManager.remove(sub.getId()); // удаляем subtasks из истории
            }
            epics.remove(id); // удаляем сам Epic из Map
            historyManager.remove(id); // удаляем epic из истории
        } else {
            System.out.println("Эпика под ID: " + id + " нет в списке");
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (!epics.isEmpty() && epics.getOrDefault(epic.getId(), null) != null) {
            epics.put(epic.getId(), epic);
        } else {
            System.out.println("Эпик не обновлен!");
        }
    }


    // taskmanager.model.Subtask methods
    @Override
    public void addSubtask(Subtask subtask) {
        // Проверяем в if: 1. время выполнения не пересекается; 2. id эпика указан; 3. id эпика указан корректно
        if (isTimeNotCrossed(subtask) && subtask.getEpicId() != null &&
                epics.get(subtask.getEpicId()) != null) {
            subtask.setId(getId());
            epics.get(subtask.getEpicId()).getSubtasksList().add(subtask);
            prioritizedTasks.add(subtask); // добавляем в сет, который сортирует по startTime
            subtasks.put(subtask.getId(), subtask);
            updateEpicStatus(subtask.getEpicId());
        }
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
        if (subtasks.get(id) != null) {
            historyManager.add(subtasks.get(id));
        }
        return subtasks.getOrDefault(id, null);
    }

    @Override
    public void deleteAllSubtasks() {
        for (Integer key : subtasks.keySet()) {
            historyManager.remove(key);
        }
        for (Subtask value : subtasks.values()) {
            prioritizedTasks.remove(value); // удаляем из сета
        }
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
            prioritizedTasks.remove(getSubtaskById(id)); // удаляем из сета
            subtasks.remove(id); // удаляем из Map
            historyManager.remove(id); // удаляем из истории
        } else {
            System.out.println("Задачи под ID: " + id + " нет в списке");
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        // Обновляем subtask в SubtasksList
        if (isTimeNotCrossed(subtask) && subtask.getEpicId() != null &&
                epics.get(subtask.getEpicId()) != null) {
            for (Subtask sub : epics.get(subtask.getEpicId()).getSubtasksList()) {
                if (sub.getId().equals(subtask.getId())) {
                    epics.get(subtask.getEpicId()).getSubtasksList().remove(sub);
                    epics.get(subtask.getEpicId()).getSubtasksList().add(subtask);
                    break;
                }
            }
            prioritizedTasks.add(subtask); // добавляем в сет
            subtasks.put(subtask.getId(), subtask); // обновляем subtask в Map
            updateEpicStatus(subtask.getEpicId());
        }
    }

    @Override
    public List<Subtask> getSubtaskListByEpicId(Integer epicId) {
        if (epicId != null && epics.get(epicId) != null) {
            return epics.get(epicId).getSubtasksList();
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
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

    private boolean isAllStatusEqual(List<Subtask> subtasksList, Status status) {
        for (Subtask sub : subtasksList) {
            if (!sub.getStatus().equals(status)) {
                return false;
            }
        }
        return true;
    }

    private boolean isTimeNotCrossed(Task task) {
        if (task.getStartTime() == null) {
            return true;
        }
        for (Task existedTask : prioritizedTasks) {
            if (existedTask.getStartTime() == null) {
                continue;
            }
            if (task.getStartTime().isBefore(existedTask.getStartTime()) &&
                    task.getEndTime().isBefore(existedTask.getStartTime()) ||
                    task.getStartTime().isAfter(existedTask.getEndTime()) &&
                            task.getEndTime().isAfter(existedTask.getEndTime())) {
                continue;
            } else {
                System.out.println("Время выполнения новой задачи пересекается с уже созданной.\n" +
                        "Измените время и попробуйте создать заново.");
                return false;
            }
        }
        return true;
    }
}


