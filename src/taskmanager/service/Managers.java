package taskmanager.service;

import com.google.gson.*;
import taskmanager.adapter.LocalDateTimeAdapter;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

public final class Managers {
    private Managers() {
    }

    public static TaskManager getDefault() {
        try {
            return new HttpTaskManager(new URL("http://localhost:8080"), "sergey");
        } catch (MalformedURLException exeption) {
            System.out.println("Введенный URL не найден.");
            return null;
        }
    }

    public static TaskManager getFileBackedTasksManager() {
        try {
            return new FileBackedTasksManager(Files.createFile(Path.of("./resources/fileToSave.csv")));
        } catch (IOException exception) {
            return FileBackedTasksManager.loadFromFile(Paths.get("./resources/fileToSave.csv"));
        }
    }

    public static Gson getDefaultGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.serializeNulls();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
        gsonBuilder.setPrettyPrinting();
        return gsonBuilder.create();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
