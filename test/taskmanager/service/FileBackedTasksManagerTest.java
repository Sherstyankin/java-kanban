package taskmanager.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import taskmanager.model.Epic;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {

    @BeforeEach
    void beforeEach() {
        manager = new FileBackedTasksManager(Path.of("./resources/fileToSave.csv"));
    }
    @Test
    void saveEmptyTasksListTest() throws IOException {
        manager.save(); // сохраняем с пустыми списками задач и пустой историей
        String actualContent = Files.readString(Paths.get("./resources/fileToSave.csv"));
        String expectedContent = "id,type,name,status,description,duration,startTime,endTime,epic\n\n";
        assertEquals(expectedContent, actualContent);
    }

    @Test
    void saveEpicWithoutSubtasksAndWithEmptyHistoryTest() throws IOException {
        Epic epic = new Epic("Aрендовать авто", "Универсал",
                new ArrayList<>());
        manager.addEpic(epic); // внутри вызовется метод save()
        String actualContent = Files.readString(Paths.get("./resources/fileToSave.csv"));
        String expectedContent = "id,type,name,status,description,duration,startTime,endTime,epic\n" +
                "1,EPIC,Aрендовать авто,NEW,Универсал,0,null,null\n\n";
        assertEquals(expectedContent, actualContent);
    }

    @Test
    void loadFromFileEmptyListsAndEmptyHistoryTest() {
        manager = FileBackedTasksManager.loadFromFile(Paths.get("./resources/fileToSave.csv"));
        boolean isTasksListEmpty = manager.getTaskList().isEmpty();
        boolean isHistoryEmpty = manager.getHistory().isEmpty();
        assertTrue(isTasksListEmpty, "Список задач подгруженный из файла не пустой");
        assertTrue(isHistoryEmpty, "История просмотров подгруженная из файла не пустая");
    }

    @Test
    void loadFromFileEpicWithoutSubtasksTest() {
        Epic epic = new Epic("Aрендовать авто", "Универсал",
                new ArrayList<>());
        manager.addEpic(epic);
        Epic epic1 = manager.getEpicById(epic.getId()); // ожидание (эпик из стартового менеджера)
        manager = FileBackedTasksManager.loadFromFile(Paths.get("./resources/fileToSave.csv"));
        Epic epic2 = manager.getEpicById(epic.getId()); // факт (эпик из менеджера после подгрузки из файла)
        assertEquals(epic1, epic2, "Эпики не совпадают");
    }

}