package taskmanager;

import taskmanager.model.Epic;
import taskmanager.enums.Status;
import taskmanager.model.Subtask;
import taskmanager.model.Task;
import taskmanager.service.Manager;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {

        Manager manager = new Manager();

        //Create tasks
        Task task1 = new Task("Переезд1", "Сбор вещей1");
        Task task2 = new Task("Переезд2", "Сбор вещей2");
        manager.addTask(task1);
        manager.addTask(task2);

        //Create epics
        Epic epicWith2Subtask = new Epic("Aрендовать авто", "Универсал, АКПП",
                new ArrayList<>());
        Epic epicWith1Subtask = new Epic("Закупить продукты", "Не забыть про овощи",
                new ArrayList<>());
        manager.addEpic(epicWith2Subtask);
        manager.addEpic(epicWith1Subtask);

        //Create subtasks
        Subtask subtask1 = new Subtask("Выбрать агенство", "Оценить по рейтингу и цене",
                epicWith2Subtask.getId());
        Subtask subtask2 = new Subtask("Отправить заявку", "Прикрепить документы",
                epicWith2Subtask.getId());
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);

        Subtask subtask3 = new Subtask("Огурцы", "Соленые",
                epicWith1Subtask.getId());
        manager.addSubtask(subtask3);

        //Read tasks, epics and subtasks
        System.out.println(manager.getTaskList());
        System.out.println(manager.getEpicList());
        System.out.println(manager.getSubtaskList());

        //Change status of the task and subtask
        task1.setStatus(Status.IN_PROGRESS);
        manager.updateTask(task1);
        task2.setStatus(Status.DONE);
        manager.updateTask(task2);

        subtask1.setStatus(Status.IN_PROGRESS);
        manager.updateSubtask(subtask1);
        subtask3.setStatus(Status.DONE);
        manager.updateSubtask(subtask3);

        //Read tasks, epics and subtasks
        System.out.println(manager.getTaskList());
        System.out.println(manager.getEpicList());
        System.out.println(manager.getSubtaskList());

        //Delete 1 task and 1 epic
        manager.deleteByTaskId(task1.getId());
        manager.deleteByEpicId(epicWith2Subtask.getId());
        manager.deleteBySubtaskId(subtask3.getId());

        //Read tasks, epics and subtasks
        System.out.println(manager.getTaskList());
        System.out.println(manager.getEpicList());
        System.out.println(manager.getSubtaskList());
    }
}