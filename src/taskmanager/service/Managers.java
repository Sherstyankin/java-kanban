package taskmanager.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public final class Managers {
    private Managers() {}
    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }
    public static TaskManager getFileBackedTasksManager() {
        try {
            return new FileBackedTasksManager(Files.createFile(Paths.get("./resources/fileToSave.csv")));
        } catch (IOException exception) {
            return FileBackedTasksManager.loadFromFile(Paths.get("./resources/fileToSave.csv"));
        }
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
