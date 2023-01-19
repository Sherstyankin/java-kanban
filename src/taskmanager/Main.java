package taskmanager;

import taskmanager.model.Epic;
import taskmanager.model.Subtask;
import taskmanager.model.Task;
import taskmanager.service.Managers;
import taskmanager.service.TaskManager;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {

        TaskManager taskManager = Managers.getDefault();

        //Create 2 tasks
        Task task1 = new Task("Переезд1", "Сбор вещей1");
        Task task2 = new Task("Переезд2", "Сбор вещей2");
        taskManager.addTask(task1);
        taskManager.addTask(task2);

        //Create 2 epics
        Epic epicWith3Subtask = new Epic("Aрендовать авто", "Универсал, АКПП",
                new ArrayList<>());
        Epic epicWithoutSubtask = new Epic("Закупить продукты", "Не забыть про овощи",
                new ArrayList<>());
        taskManager.addEpic(epicWith3Subtask);
        taskManager.addEpic(epicWithoutSubtask);

        //Create 3 subtasks for epicWith3Subtask
        Subtask subtask1 = new Subtask("Выбрать агенство", "Оценить по рейтингу и цене",
                epicWith3Subtask.getId());
        Subtask subtask2 = new Subtask("Отправить заявку", "Прикрепить документы",
                epicWith3Subtask.getId());
        Subtask subtask3 = new Subtask("Оплатить счет", "Оплатить",
                epicWith3Subtask.getId());
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);
        taskManager.addSubtask(subtask3);

        //Check history list
        taskManager.getSubtaskById(subtask1.getId());
        System.out.println(taskManager.getHistory());
        System.out.println();
        taskManager.getSubtaskById(subtask1.getId());
        System.out.println(taskManager.getHistory());
        System.out.println();

        taskManager.getEpicById(epicWith3Subtask.getId());
        taskManager.getEpicById(epicWith3Subtask.getId());
        taskManager.getEpicById(epicWithoutSubtask.getId());

        System.out.println(taskManager.getHistory());
        System.out.println();


        taskManager.getTaskById(task1.getId());
        System.out.println(taskManager.getHistory());
        System.out.println();

        taskManager.getSubtaskById(subtask2.getId());
        System.out.println(taskManager.getHistory());
        System.out.println();

        taskManager.getTaskById(task1.getId());
        System.out.println(taskManager.getHistory());
        System.out.println();

        taskManager.getSubtaskById(subtask2.getId());
        System.out.println(taskManager.getHistory());
        System.out.println();

        //Delete 1 task and 1 epic
        taskManager.deleteByTaskId(task1.getId());
        System.out.println(taskManager.getHistory());
        System.out.println();

        taskManager.deleteByEpicId(epicWith3Subtask.getId());
        System.out.println(taskManager.getHistory());
    }
}
