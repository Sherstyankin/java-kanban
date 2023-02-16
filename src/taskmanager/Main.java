package taskmanager;

import taskmanager.model.Epic;
import taskmanager.model.Subtask;
import taskmanager.model.Task;
import taskmanager.server.KVServer;
import taskmanager.service.HttpTaskManager;
import taskmanager.service.Managers;

import java.time.LocalDateTime;
import java.util.ArrayList;


public class Main {
    public static void main(String[] args) {
        new KVServer().start();

        HttpTaskManager taskManager = (HttpTaskManager) Managers.getDefault();

        //Create 2 tasks
        Task task1 = new Task("Переезд1", "Сбор вещей1", 60,
                LocalDateTime.of(2023, 2, 2, 14, 0));
        Task task2 = new Task("Переезд2", "Сбор вещей2", 30,
                LocalDateTime.of(2023, 2, 2, 13, 0));
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
                60, LocalDateTime.of(2023, 2, 1, 10, 0),
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
        taskManager.getSubtaskById(subtask1.getId());
        taskManager.getSubtaskById(subtask2.getId());
        System.out.println(taskManager.getHistory());
        taskManager.getTaskById(task1.getId());
        taskManager.getSubtaskById(subtask2.getId());
        taskManager.getEpicById(epicWith3Subtask.getId());
        taskManager.getEpicById(epicWithoutSubtask.getId());
        taskManager.getTaskById(task1.getId());
        taskManager.getSubtaskById(subtask2.getId());

        System.out.println(taskManager.getHistory());
        System.out.println();
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

        //Подгружаем менеджер с KV сервера
        HttpTaskManager loadedManager = taskManager.load();
        System.out.println(loadedManager.getTaskList());
        System.out.println(loadedManager.getEpicList());
        System.out.println(loadedManager.getSubtaskList());
        System.out.println();
        System.out.println(loadedManager.getHistory());
    }
}
