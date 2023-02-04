package taskmanager.model;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    Epic epic;
    @BeforeEach
    void beforeEach() {
        epic = new Epic("Aрендовать авто", "Универсал",
                new ArrayList<>());
        Subtask subtask1 = new Subtask("Выбрать агенство", "Оценить по рейтингу и цене",
                60, LocalDateTime.of(2023, 2, 4, 14, 0),
                epic.getId());
        Subtask subtask2 = new Subtask("Отправить заявку", "Прикрепить документы",
                0, null,
                epic.getId());
        Subtask subtask3 = new Subtask("Оплатить счет", "Оплатить",
                60, LocalDateTime.of(2023, 2, 4, 15, 0),
                epic.getId());
        epic.getSubtasksList().add(subtask1);
        epic.getSubtasksList().add(subtask2);
        epic.getSubtasksList().add(subtask3);
    }

    @Test
    void getDurationTest() {
        long actual = epic.getDuration();
        long expected = 120;
        assertEquals(expected, actual, "Продолжительность расситана неверно.");
    }

    @Test
    void getStartTimeTest() {
        LocalDateTime actual = epic.getStartTime();
        LocalDateTime expected = LocalDateTime.of(2023, 2, 4, 14, 0);
        assertEquals(expected, actual, "Дата/время старта рассчитана неверно.");
    }

    @Test
    void getEndTime() {
        LocalDateTime actual = epic.getEndTime();
        LocalDateTime expected = LocalDateTime.of(2023, 2, 4, 16, 0);
        assertEquals(expected, actual, "Дата/время окончания рассчитана неверно.");
    }


}