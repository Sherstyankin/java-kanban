package taskmanager.service;

import taskmanager.enums.Status;
import taskmanager.model.Epic;
import taskmanager.model.Subtask;
import taskmanager.model.Task;
import taskmanager.service.TaskManager;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {
    protected T manager;

    @Test
    void addTaskTest() {
        Task task = new Task("Переезд1", "Сбор вещей1", 60,
                LocalDateTime.of(2023, 2, 2, 13, 0));
        manager.addTask(task);
        Task savedTask = manager.getTaskList().get(0);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");
    }

    @Test
    void addTaskTimeCrossedTest() {
        Task task1 = new Task("Переезд1", "Сбор вещей1", 60,
                LocalDateTime.of(2023, 2, 2, 13, 0));
        Task task2 = new Task("Переезд2", "Сбор вещей2", 60,
                LocalDateTime.of(2023, 2, 2, 13, 0));
        manager.addTask(task1);
        manager.addTask(task2);
        boolean isListHasOneSub = manager.getTaskList().size() == 1; // факт
        boolean expected = true; // ожидание
        assertEquals(expected, isListHasOneSub);
    }

    @Test
    void getTaskListTest() {
        Task task = new Task("Переезд1", "Сбор вещей1", 60,
                LocalDateTime.of(2023, 2, 2, 13, 0));
        manager.addTask(task);
        List<Task> tasks = new ArrayList<>();
        tasks.add(task);
        assertEquals(tasks, manager.getTaskList());
    }

    @Test
    void getTaskByIdGeneralTest() {
        Task task = new Task("Переезд1", "Сбор вещей1", 60,
                LocalDateTime.of(2023, 2, 2, 13, 0));
        manager.addTask(task);
        Task task1 = manager.getTaskById(1); // факт
        Map<Integer, Task> tasks = new HashMap<>();
        tasks.put(1, task);
        Task task2 = tasks.get(1); // ожидание
        assertNotNull(task1, "Задача не найдена.");
        assertEquals(task2, task1, "Задачи не совпадают.");
    }

    @Test
    void getTaskByIdFromEmptyListTest() {
        Task task1 = manager.getTaskById(1); // факт, должен быть null
        assertNull(task1);
    }

    void getTaskByIncorrectIdTest() {
        Task task = new Task("Переезд1", "Сбор вещей1", 60,
                LocalDateTime.of(2023, 2, 2, 13, 0));
        manager.addTask(task);
        Task task1 = manager.getTaskById(123); // факт, по некорректному id должны получить null
        assertNull(task1);
    }

    @Test
    void deleteAllTasksTest() {
        manager.deleteAllTasks();
        List<Task> tasks = new ArrayList<>();
        assertEquals(tasks, manager.getTaskList());
    }

    @Test
    void deleteByTaskIdGeneralTest() {
        Task task = new Task("Переезд1", "Сбор вещей1", 60,
                LocalDateTime.of(2023, 2, 2, 13, 0));
        manager.addTask(task);
        manager.deleteByTaskId(1);
        List<Task> tasks = new ArrayList<>();
        assertEquals(tasks, manager.getTaskList());
    }

    @Test
    void deleteByTaskIdFromEmptyListOrWithIncorrectIdTest() {
        List<Task> tasks1 = manager.getTaskList(); // было (пустой лист)
        manager.deleteByTaskId(1);
        List<Task> tasks2 = manager.getTaskList(); // стало (так же пустой лист)
        assertEquals(tasks1, tasks2);
    }

    @Test
    void updateTaskGeneralTest() {
        Task task1 = new Task("Переезд1", "Сбор вещей1", 60,
                LocalDateTime.of(2023, 2, 2, 13, 0));
        manager.addTask(task1);
        Task task2 = new Task("Переезд2", "Сбор вещей2", 30,
                LocalDateTime.of(2023, 2, 4, 13, 0));
        task2.setId(1);
        manager.updateTask(task2);
        Task updatedTask = manager.getTaskById(1);
        assertNotEquals(task1, updatedTask, "Задача не обновлена");
    }

    @Test
    void updateTaskWithIncorrectIDTest() {
        Task task1 = new Task("Переезд1", "Сбор вещей1", 60,
                LocalDateTime.of(2023, 2, 2, 13, 0));
        manager.addTask(task1);
        Task task2 = new Task("Переезд2", "Сбор вещей2", 30,
                LocalDateTime.of(2023, 2, 4, 13, 0));
        task2.setId(123); // присваиваем id отличный от id task1
        manager.updateTask(task2);
        Task updatedTask = manager.getTaskById(1);
        assertEquals(task1, updatedTask); // проверяем обновилась ли задача с task1 на updatedTask
    }

    @Test
    void updateTaskFromEmptyListTest() {
        Task task1 = new Task("Переезд2", "Сбор вещей2", 30,
                LocalDateTime.of(2023, 2, 4, 13, 0));
        task1.setId(1);
        manager.updateTask(task1);
        boolean isTasksListEmpty = manager.getTaskList().isEmpty();
        boolean expected = true;
        assertEquals(expected, isTasksListEmpty);
    }


    //Epic methods
    @Test
    void addEpicTest() {
        Epic epic = new Epic("Aрендовать авто", "Универсал",
                new ArrayList<>());
        manager.addEpic(epic);
        Epic savedEpic = manager.getEpicList().get(0);
        assertNotNull(savedEpic, "Задача не найдена.");
        assertEquals(epic, savedEpic, "Задачи не совпадают.");
    }

    @Test
    void getEpicListTest() {
        Epic epic = new Epic("Aрендовать авто", "Универсал",
                new ArrayList<>());
        manager.addEpic(epic);
        List<Epic> epics = new ArrayList<>();
        epics.add(epic);
        assertEquals(epics, manager.getEpicList());
    }

    @Test
    void getEpicByIdGeneralTest() {
        Epic epic = new Epic("Aрендовать авто", "Универсал",
                new ArrayList<>());
        manager.addEpic(epic);
        Epic epic1 = manager.getEpicById(1); // факт
        Map<Integer, Epic> epics = new HashMap<>();
        epics.put(1, epic);
        Epic epic2 = epics.get(1); // ожидание
        assertNotNull(epic1, "Задача не найдена.");
        assertEquals(epic2, epic1, "Задачи не совпадают.");
    }

    @Test
    void getEpicByIdFromEmptyListTest() {
        Epic epic = manager.getEpicById(1); // факт, должен быть null
        assertNull(epic);
    }

    @Test
    void getEpicByIncorrectIdTest() {
        Epic epic = new Epic("Aрендовать авто", "Универсал",
                new ArrayList<>());
        manager.addEpic(epic);
        Epic epic1 = manager.getEpicById(123); // факт
        assertNull(epic1);
    }

    @Test
    void deleteAllEpicsTest() {
        Epic epic = new Epic("Aрендовать авто", "Универсал",
                new ArrayList<>());
        manager.addEpic(epic);
        Subtask subtask = new Subtask("Выбрать агенство", "Оценить по рейтингу и цене",
                60, LocalDateTime.of(2023, 2, 4, 14, 0),
                epic.getId());
        manager.addSubtask(subtask);
        manager.deleteAllEpics();
        List<Epic> epics = new ArrayList<>();
        List<Subtask> subtasks = new ArrayList<>();
        assertEquals(epics, manager.getEpicList(), "Эпики не удалены");
        assertEquals(subtasks, manager.getSubtaskList(), "Сабтаски не удалены");
    }

    @Test
    void deleteByEpicIdGenaralTest() {
        Epic epic = new Epic("Aрендовать авто", "Универсал",
                new ArrayList<>());
        manager.addEpic(epic);
        Subtask subtask = new Subtask("Выбрать агенство", "Оценить по рейтингу и цене",
                60, LocalDateTime.of(2023, 2, 4, 14, 0),
                epic.getId());
        manager.addSubtask(subtask);
        manager.deleteByEpicId(1);
        List<Epic> epics = new ArrayList<>();
        List<Subtask> subtasks = new ArrayList<>();
        assertEquals(epics, manager.getEpicList());
        assertEquals(subtasks, manager.getSubtaskList());
    }

    @Test
    void deleteByEpicIdFromEmptyListOrIncorrectIdTest() {
        List<Epic> epics1 = manager.getEpicList();
        manager.deleteByEpicId(1);
        List<Epic> epics2 = manager.getEpicList();
        assertEquals(epics1, epics2);
    }

    @Test
    void updateEpicGeneralTest() {
        Epic epic1 = new Epic("Aрендовать авто", "Универсал",
                new ArrayList<>());
        manager.addEpic(epic1);
        Epic epic2 = new Epic("Aрендовать авто", "Седан",
                new ArrayList<>());
        epic2.setId(1);
        manager.updateEpic(epic2);
        Epic updatedEpic = manager.getEpicById(1);
        assertNotEquals(epic1, updatedEpic);
    }

    @Test
    void updateEpicFromEmptyListTest() {
        Epic epic = new Epic("Aрендовать авто", "Седан",
                new ArrayList<>());
        epic.setId(1);
        manager.updateEpic(epic);
        boolean isEpicsListEmpty = manager.getEpicList().isEmpty();
        boolean expected = true;
        assertEquals(expected, isEpicsListEmpty);
    }

    @Test
    void updateEpicWithIncorrectIdTest() {
        Epic epic1 = new Epic("Aрендовать авто", "Универсал",
                new ArrayList<>());
        manager.addEpic(epic1);
        Epic epic2 = new Epic("Aрендовать авто", "Седан",
                new ArrayList<>());
        epic2.setId(123);
        manager.updateEpic(epic2);
        Epic updatedEpic = manager.getEpicById(1);
        assertEquals(epic1, updatedEpic);
    }


    //Subtask methods
    @Test
    void addSubtaskGeneralTest() {
        Epic epic = new Epic("Aрендовать авто", "Универсал",
                new ArrayList<>());
        manager.addEpic(epic);
        Subtask subtask = new Subtask("Выбрать агенство", "Оценить по рейтингу и цене",
                60, LocalDateTime.of(2023, 2, 4, 14, 0),
                epic.getId());
        manager.addSubtask(subtask);
        Subtask savedSubtask = manager.getSubtaskList().get(0);

        assertNotNull(savedSubtask, "Задача не найдена.");
        assertEquals(subtask, savedSubtask, "Задачи не совпадают.");
    }

    @Test
    void addSubtaskWithNullEpicIdTest() {
        Subtask subtask = new Subtask("Выбрать агенство", "Оценить по рейтингу и цене",
                60, LocalDateTime.of(2023, 2, 4, 14, 0),
                0);
        manager.addSubtask(subtask);
        boolean isListEmpty = manager.getSubtaskList().isEmpty(); // факт
        boolean expected = true; // ожидание
        assertEquals(expected, isListEmpty);
    }

    @Test
    void addSubtaskWithIncorrectEpicIdTest() {
        Epic epic = new Epic("Aрендовать авто", "Универсал",
                new ArrayList<>());
        manager.addEpic(epic);
        Subtask subtask = new Subtask("Выбрать агенство", "Оценить по рейтингу и цене",
                60, LocalDateTime.of(2023, 2, 4, 14, 0),
                1234);
        manager.addSubtask(subtask);
        boolean isListEmpty = manager.getSubtaskList().isEmpty(); // факт
        boolean expected = true; // ожидание
        assertEquals(expected, isListEmpty);
    }

    @Test
    void addSubtaskTimeCrossedTest() {
        Epic epic = new Epic("Aрендовать авто", "Универсал",
                new ArrayList<>());
        manager.addEpic(epic);
        Subtask subtask1 = new Subtask("Выбрать агенство", "Оценить по рейтингу и цене",
                60, LocalDateTime.of(2023, 2, 4, 14, 0),
                epic.getId());
        Subtask subtask2 = new Subtask("Оплатить счет", "Оплатить",
                60, LocalDateTime.of(2023, 2, 4, 14, 0),
                epic.getId());
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        boolean isListHasOneSub = manager.getSubtaskList().size() == 1; // факт
        boolean expected = true; // ожидание
        assertEquals(expected, isListHasOneSub);
    }

    @Test
    void getSubtaskListTest() {
        Epic epic = new Epic("Aрендовать авто", "Универсал",
                new ArrayList<>());
        manager.addEpic(epic);
        Subtask subtask = new Subtask("Выбрать агенство", "Оценить по рейтингу и цене",
                60, LocalDateTime.of(2023, 2, 4, 14, 0),
                epic.getId());
        manager.addSubtask(subtask);
        List<Subtask> subtasks = new ArrayList<>();
        subtasks.add(subtask);
        assertEquals(subtasks, manager.getSubtaskList());
    }

    @Test
    void getSubtaskByIdTest() {
        Epic epic = new Epic("Aрендовать авто", "Универсал",
                new ArrayList<>());
        manager.addEpic(epic);
        Subtask subtask = new Subtask("Выбрать агенство", "Оценить по рейтингу и цене",
                60, LocalDateTime.of(2023, 2, 4, 14, 0),
                epic.getId());
        manager.addSubtask(subtask);
        final int subtaskId = manager.getSubtaskList().get(0).getId();
        Subtask subtask1 = manager.getSubtaskById(subtaskId); // факт
        Map<Integer, Subtask> subtasks = new HashMap<>();
        subtasks.put(1, subtask);
        Subtask subtask2 = subtasks.get(1); // ожидание
        assertNotNull(subtask1, "Задача не найдена.");
        assertEquals(subtask2, subtask1, "Задачи не совпадают.");
    }

    @Test
    void deleteAllSubtasksTest() {
        manager.deleteAllSubtasks();
        List<Subtask> subtasks = new ArrayList<>();
        assertEquals(subtasks, manager.getSubtaskList());
    }

    @Test
    void deleteBySubtaskIdTest() {
        Epic epic = new Epic("Aрендовать авто", "Универсал",
                new ArrayList<>());
        manager.addEpic(epic);
        Subtask subtask = new Subtask("Выбрать агенство", "Оценить по рейтингу и цене",
                60, LocalDateTime.of(2023, 2, 4, 14, 0),
                epic.getId());
        manager.addSubtask(subtask);
        manager.deleteBySubtaskId(2);
        List<Subtask> subtasks = new ArrayList<>();
        assertEquals(subtasks, manager.getSubtaskList());
    }

    @Test
    void updateSubtaskTest() {
        Epic epic = new Epic("Aрендовать авто", "Универсал",
                new ArrayList<>());
        manager.addEpic(epic);
        Subtask subtask1 = new Subtask("Выбрать агенство", "Оценить по рейтингу и цене",
                60, LocalDateTime.of(2023, 2, 4, 14, 0),
                epic.getId());
        manager.addSubtask(subtask1);
        Subtask subtask2 = new Subtask("Выбрать агенство", "Оценить по рейтингу и цене",
                60, LocalDateTime.of(2023, 2, 4, 14, 0),
                epic.getId());
        subtask2.setId(2);
        manager.updateTask(subtask2);
        Subtask updatedSubtask = manager.getSubtaskById(1);
        assertNotEquals(subtask1, updatedSubtask);
    }

    @Test
    void getSubtaskListByEpicIdTest() {
        Epic epic = new Epic("Aрендовать авто", "Универсал",
                new ArrayList<>());
        manager.addEpic(epic);
        Subtask subtask1 = new Subtask("Выбрать агенство", "Оценить по рейтингу и цене",
                60, LocalDateTime.of(2023, 2, 4, 14, 0),
                epic.getId());
        manager.addSubtask(subtask1);
        List<Subtask> subtaskList = manager.getSubtaskListByEpicId(epic.getId()); // факт
        List<Subtask> expectedSubtaskList = List.of(subtask1); // ожидание
        assertEquals(expectedSubtaskList, subtaskList);
    }

    @Test
    void getSubtaskListByEpicIdNullTest() {
        boolean isEmptyListReturned = manager.getSubtaskListByEpicId(0).isEmpty(); // факт
        assertTrue(isEmptyListReturned, "Лист не пуст");
    }

    @Test
    void getSubtaskListByIncorrectEpicIdTest() {
        boolean isEmptyListReturned = manager.getSubtaskListByEpicId(123).isEmpty(); // факт
        assertTrue(isEmptyListReturned, "Лист не пуст");
    }

    @Test
    void getHistoryTest() {
        Task task = new Task("Переезд1", "Сбор вещей1", 60,
                LocalDateTime.of(2023, 2, 2, 13, 0));
        manager.addTask(task);
        manager.getTaskById(1);
        List<Task> tasks = manager.getHistory(); // факт
        List<Task> expectedTasks = List.of(task); // ожидание
        assertEquals(expectedTasks, tasks);
    }

    @Test
    void getPrioritizedTasksListGeneralTest() {
        Task task1 = new Task("Переезд1", "Сбор вещей1", 60,
                LocalDateTime.of(2023, 2, 2, 13, 0));
        Task task2 = new Task("Переезд2", "Сбор вещей2", 60,
                LocalDateTime.of(2023, 2, 3, 14, 0));
        manager.addTask(task2);
        manager.addTask(task1);
        List<Task> prioritizedTasks = manager.getPrioritizedTasksList(); // факт
        List<Task> expectedTasks = List.of(task1, task2); // ожидание
        assertEquals(expectedTasks, prioritizedTasks);
    }

    @Test
    void getPrioritizedTasksListAfterRemovalTest() {
        Task task1 = new Task("Переезд1", "Сбор вещей1", 60,
                LocalDateTime.of(2023, 2, 2, 13, 0));
        Task task2 = new Task("Переезд2", "Сбор вещей2", 60,
                LocalDateTime.of(2023, 2, 3, 14, 0));
        Task task3 = new Task("Переезд3", "Сбор вещей3", 60,
                LocalDateTime.of(2023, 2, 3, 16, 0));
        manager.addTask(task2);
        manager.addTask(task3);
        manager.addTask(task1);
        manager.deleteByTaskId(task3.getId());
        List<Task> prioritizedTasks = manager.getPrioritizedTasksList(); // факт
        List<Task> expectedTasks = List.of(task1, task2); // ожидание
        assertEquals(expectedTasks, prioritizedTasks);
    }

    @Test
    void getPrioritizedTasksListAfterAddTaskWithNullTimeTest() {
        Task task1 = new Task("Переезд1", "Сбор вещей1", 60,
                LocalDateTime.of(2023, 2, 2, 13, 0));
        Task task2 = new Task("Переезд2", "Сбор вещей2", 60,
                LocalDateTime.of(2023, 2, 3, 14, 0));
        Task task3 = new Task("Переезд3", "Сбор вещей3", 0,
                null);
        manager.addTask(task2);
        manager.addTask(task3);
        manager.addTask(task1);
        List<Task> prioritizedTasks = manager.getPrioritizedTasksList(); // факт
        List<Task> expectedTasks = List.of(task1, task2, task3); // ожидание
        assertEquals(expectedTasks, prioritizedTasks);
    }

    @Test
    void updateEpicStatusWithEmptySubtasksListTest() {
        Epic epic = new Epic("Aрендовать авто", "Универсал",
                new ArrayList<>());
        manager.addEpic(epic);
        manager.updateEpicStatus(epic.getId());
        Status statusOfEpic = epic.getStatus(); // факт
        Status expectedStatusOfEpic = Status.NEW; // ожидание
        assertEquals(expectedStatusOfEpic, statusOfEpic);
    }

    @Test
    void updateEpicStatusWithAllNewSubtasksTest() {
        Epic epic = new Epic("Aрендовать авто", "Универсал",
                new ArrayList<>());
        manager.addEpic(epic);
        Subtask subtask1 = new Subtask("Выбрать агенство", "Оценить по рейтингу и цене",
                60, LocalDateTime.of(2023, 2, 4, 14, 0),
                epic.getId());
        Subtask subtask2 = new Subtask("Отправить заявку", "Прикрепить документы",
                0, null,
                epic.getId());
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        manager.updateEpicStatus(epic.getId());
        Status statusOfEpic = epic.getStatus(); // факт
        Status expectedStatusOfEpic = Status.NEW; // ожидание
        assertEquals(expectedStatusOfEpic, statusOfEpic);
    }

    @Test
    void updateEpicStatusWithAllDoneSubtasksTest() {
        Epic epic = new Epic("Aрендовать авто", "Универсал",
                new ArrayList<>());
        manager.addEpic(epic);
        Subtask subtask1 = new Subtask("Выбрать агенство", "Оценить по рейтингу и цене",
                60, LocalDateTime.of(2023, 2, 4, 14, 0),
                epic.getId());
        subtask1.setStatus(Status.DONE);
        Subtask subtask2 = new Subtask("Отправить заявку", "Прикрепить документы",
                0, null,
                epic.getId());
        subtask2.setStatus(Status.DONE);
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        manager.updateEpicStatus(epic.getId());
        Status statusOfEpic = epic.getStatus(); // факт
        Status expectedStatusOfEpic = Status.DONE; // ожидание
        assertEquals(expectedStatusOfEpic, statusOfEpic);
    }

    @Test
    void updateEpicStatusWithDoneAndNewSubtasksTest() {
        Epic epic = new Epic("Aрендовать авто", "Универсал",
                new ArrayList<>());
        manager.addEpic(epic);
        Subtask subtask1 = new Subtask("Выбрать агенство", "Оценить по рейтингу и цене",
                60, LocalDateTime.of(2023, 2, 4, 14, 0),
                epic.getId());
        Subtask subtask2 = new Subtask("Отправить заявку", "Прикрепить документы",
                0, null,
                epic.getId());
        subtask2.setStatus(Status.DONE);
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        manager.updateEpicStatus(epic.getId());
        Status statusOfEpic = epic.getStatus(); // факт
        Status expectedStatusOfEpic = Status.IN_PROGRESS; // ожидание
        assertEquals(expectedStatusOfEpic, statusOfEpic);
    }

    @Test
    void updateEpicStatusWithAllInProgressSubtasksTest() {
        Epic epic = new Epic("Aрендовать авто", "Универсал",
                new ArrayList<>());
        manager.addEpic(epic);
        Subtask subtask1 = new Subtask("Выбрать агенство", "Оценить по рейтингу и цене",
                60, LocalDateTime.of(2023, 2, 4, 14, 0),
                epic.getId());
        subtask1.setStatus(Status.IN_PROGRESS);
        Subtask subtask2 = new Subtask("Отправить заявку", "Прикрепить документы",
                0, null,
                epic.getId());
        subtask2.setStatus(Status.IN_PROGRESS);
        manager.addSubtask(subtask1);
        manager.addSubtask(subtask2);
        manager.updateEpicStatus(epic.getId());
        Status statusOfEpic = epic.getStatus(); // факт
        Status expectedStatusOfEpic = Status.IN_PROGRESS; // ожидание
        assertEquals(expectedStatusOfEpic, statusOfEpic);
    }
}
