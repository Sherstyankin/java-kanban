package taskmanager.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import taskmanager.model.Epic;
import taskmanager.model.Task;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    HistoryManager history;
    @BeforeEach
    void beforeEach() {
        history = Managers.getDefaultHistory();
    }

    @Test
    void getEmptyHistoryTest() {
        List<Task> historyList = history.getHistory();
        List<Task> expepted = new ArrayList<>();
        assertEquals(expepted, historyList, "Списки историй не совпадают!");
    }

    @Test
    void getHistoryDublicateTest() {
        Task task = new Task("Переезд1", "Сбор вещей1", 60,
                LocalDateTime.of(2023, 2, 2, 13, 0));
        task.setId(1);
        history.add(task);
        history.add(task);
        boolean isHistoryHasNotDublicate = history.getHistory().size() == 1;
        assertTrue(isHistoryHasNotDublicate, "Обнаружен дубликат!");
    }

    @Test
    void getHistoryRemovalFromHeadTest() {
        Task headTask = new Task("Переезд1", "Сбор вещей1", 60,
                LocalDateTime.of(2023, 2, 2, 13, 0));
        headTask.setId(1);
        history.add(headTask);
        Epic middleTask = new Epic("Aрендовать авто", "Универсал",
                new ArrayList<>());
        middleTask.setId(2);
        history.add(middleTask);
        Task tailTask = new Task("Переезд2", "Сбор вещей2", 30,
                LocalDateTime.of(2023, 2, 2, 13, 0));
        tailTask.setId(3);
        history.add(tailTask);
        history.remove(1);
        List<Task> historyList = history.getHistory(); // факт
        List<Task> expected = List.of(middleTask,tailTask); // ожидание
        assertEquals(expected, historyList, "Удаление из начала не прошло."); // Удаление из начала
    }

    @Test
    void getHistoryRemovalFromMiddleTest() {
        Task headTask = new Task("Переезд1", "Сбор вещей1", 60,
                LocalDateTime.of(2023, 2, 2, 13, 0));
        headTask.setId(1);
        history.add(headTask);
        Epic middleTask = new Epic("Aрендовать авто", "Универсал",
                new ArrayList<>());
        middleTask.setId(2);
        history.add(middleTask);
        Task tailTask = new Task("Переезд2", "Сбор вещей2", 30,
                LocalDateTime.of(2023, 2, 2, 13, 0));
        tailTask.setId(3);
        history.add(tailTask);
        history.remove(2);
        List<Task> historyList = history.getHistory(); // факт
        List<Task> expected = List.of(headTask,tailTask); // ожидание
        assertEquals(expected, historyList, "Удаление из начала не прошло."); // Удаление из начала
    }

    @Test
    void getHistoryRemovalFromTailTest() {
        Task headTask = new Task("Переезд1", "Сбор вещей1", 60,
                LocalDateTime.of(2023, 2, 2, 13, 0));
        headTask.setId(1);
        history.add(headTask);
        Epic middleTask = new Epic("Aрендовать авто", "Универсал",
                new ArrayList<>());
        middleTask.setId(2);
        history.add(middleTask);
        Task tailTask = new Task("Переезд2", "Сбор вещей2", 30,
                LocalDateTime.of(2023, 2, 2, 13, 0));
        tailTask.setId(3);
        history.add(tailTask);
        history.remove(3);
        List<Task> historyList = history.getHistory(); // факт
        List<Task> expected = List.of(headTask,middleTask); // ожидание
        assertEquals(expected, historyList, "Удаление из конца не прошло.");
    }

    @Test
    void addNullToHistoryTest() {
        history.add(null);
        List<Task> historyList = history.getHistory(); // факт
        List<Task> expepted = new ArrayList<>();
        assertEquals(expepted, historyList);
    }

    @Test
    void removeTaskWithIncoorectIdTest() {
        Task task = new Task("Переезд1", "Сбор вещей1", 60,
                LocalDateTime.of(2023, 2, 2, 13, 0));
        task.setId(1);
        history.add(task);
        history.remove(2);
        List<Task> historyList = history.getHistory(); // факт
        List<Task> expepted = List.of(task); // ожидание
        assertEquals(expepted, historyList);
    }
}