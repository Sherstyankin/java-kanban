package taskmanager.service;

import com.google.gson.Gson;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import taskmanager.model.Task;
import taskmanager.server.HttpTaskServer;
import taskmanager.server.KVServer;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskManagerTest extends TaskManagerTest<HttpTaskManager> {
    private final Gson gson = Managers.getDefaultGson();
    private KVServer kvServer;
    private HttpTaskServer httpTaskServer;
    @BeforeEach
    void init() {
        kvServer = new KVServer();
        kvServer.start();
        manager = (HttpTaskManager) Managers.getDefault();
        httpTaskServer = new HttpTaskServer(manager);
        httpTaskServer.start();
    }
    @AfterEach
    void stopServer() {
        kvServer.stop();
        httpTaskServer.stop();
    }
    @Test
    void saveManagerTest() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        Task task1 = new Task("Переезд1", "Сбор вещей1", 60, LocalDateTime.now());
        URI url = URI.create("http://localhost:8078/tasks/task");
        String json = gson.toJson(task1);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        String managerInJson = kvServer.getData().get("sergey");
        assertNotNull(managerInJson);
    }
    @Test
    void loadManagerTest() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        Task task1 = new Task("Переезд1", "Сбор вещей1", 60, LocalDateTime.now());
        URI url = URI.create("http://localhost:8078/tasks/task");
        String json = gson.toJson(task1);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        HttpTaskManager loadedManager = manager.load();
        List<Task> loadedList = loadedManager.getTaskList();
        List<Task> initialList = manager.getTaskList();
        assertEquals(initialList, loadedList, "Менеджера не равны.");
    }
}