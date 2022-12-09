package taskmanager;

import taskmanager.model.Epic;
import taskmanager.enums.Status;
import taskmanager.model.Subtask;
import taskmanager.model.Task;
import taskmanager.service.HistoryManager;
import taskmanager.service.Managers;
import taskmanager.service.TaskManager;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {

        TaskManager taskManager = Managers.getDefault();
        HistoryManager historyManager = Managers.getDefaultHistory();

        //Create tasks
        Task task1 = new Task("Переезд1", "Сбор вещей1");
        Task task2 = new Task("Переезд2", "Сбор вещей2");
        taskManager.addTask(task1);
        taskManager.addTask(task2);

        //Create epics
        Epic epicWith2Subtask = new Epic("Aрендовать авто", "Универсал, АКПП",
                new ArrayList<>());
        Epic epicWith1Subtask = new Epic("Закупить продукты", "Не забыть про овощи",
                new ArrayList<>());
        taskManager.addEpic(epicWith2Subtask);
        taskManager.addEpic(epicWith1Subtask);

        //Create subtasks
        Subtask subtask1 = new Subtask("Выбрать агенство", "Оценить по рейтингу и цене",
                epicWith2Subtask.getId());
        Subtask subtask2 = new Subtask("Отправить заявку", "Прикрепить документы",
                epicWith2Subtask.getId());
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);

        Subtask subtask3 = new Subtask("Огурцы", "Соленые",
                epicWith1Subtask.getId());
        taskManager.addSubtask(subtask3);

        //Read tasks, epics and subtasks
        System.out.println(taskManager.getTaskList());
        System.out.println(taskManager.getEpicList());
        System.out.println(taskManager.getSubtaskList());

        //Change status of the task and subtask
        task1.setStatus(Status.IN_PROGRESS);
        taskManager.updateTask(task1);
        task2.setStatus(Status.DONE);
        taskManager.updateTask(task2);

        subtask1.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubtask(subtask1);
        subtask3.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask3);

        //Read tasks, epics and subtasks
        System.out.println(taskManager.getTaskList());
        System.out.println(taskManager.getEpicList());
        System.out.println(taskManager.getSubtaskList());

        //Check history list
        taskManager.getSubtaskById(subtask1.getId());
        taskManager.getTaskById(task1.getId());
        taskManager.getEpicById(epicWith2Subtask.getId());
        taskManager.getSubtaskById(subtask1.getId());
        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task1.getId());
        System.out.println(historyManager.getHistory());

        //Delete 1 task and 1 epic
        taskManager.deleteByTaskId(task1.getId());
        taskManager.deleteByEpicId(epicWith2Subtask.getId());

        //Read tasks, epics and subtasks
        System.out.println(taskManager.getTaskList());
        System.out.println(taskManager.getEpicList());
        System.out.println(taskManager.getSubtaskList());
    }
}
