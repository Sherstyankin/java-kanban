package taskmanager.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    @Test
    void getEndTimeWithNullStartTimeTest() {
        Task task = new Task("Переезд1", "Сбор вещей1", 60, null);
        assertNull(task.getEndTime());
    }

    @Test
    void getEndTimeGenaralTest() {
        Task task = new Task("Переезд1", "Сбор вещей1", 60,
                LocalDateTime.of(2023, 2, 2, 13, 0));
        LocalDateTime actual = task.getEndTime();
        LocalDateTime expected = LocalDateTime.of(2023, 2, 2, 14, 0);
        assertEquals(expected, actual, "Время окончания рассчитано неверно.");
    }

    @Test
    void toStringForFileTest() {
        Task task = new Task("Переезд1", "Сбор вещей1", 60,
                LocalDateTime.of(2023, 2, 2, 13, 0));
        task.setId(1);
        String actual = task.toStringForFile();
        String expected = "1,TASK,Переезд1,NEW,Сбор вещей1,60,2023-02-02T13:00,2023-02-02T14:00\n";
        assertEquals(expected, actual, "Строки не совпадают.");
    }
}