package taskmanager.service;

import taskmanager.model.Epic;
import taskmanager.model.Subtask;
import taskmanager.model.Task;

import java.util.List;

public interface TaskManager {

    //Task methods
    void addTask(Task task);
    List<Task> getTaskList();
    Task getTaskById(int id);
    void deleteAllTasks();
    void deleteByTaskId(int id);
    void updateTask(Task task);


    //Epic methods
    void addEpic(Epic epic);
    List<Epic> getEpicList();
    Epic getEpicById(int id);
    void deleteAllEpics();
    void deleteByEpicId(int id);
    void updateEpic(Epic epic);


    //Subtask methods
    void addSubtask(Subtask subtask);
    List<Subtask> getSubtaskList();
    Subtask getSubtaskById(int id);
    void deleteAllSubtasks();
    void deleteBySubtaskId(int id);
    void updateSubtask(Subtask subtask);
    List<Subtask> getSubtaskListByEpic(Integer epicId);
}
