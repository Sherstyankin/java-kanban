package taskmanager.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import taskmanager.client.KVTaskClient;
import taskmanager.model.Task;
import taskmanager.server.HttpTaskServer;
import taskmanager.server.KVServer;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;


public class HttpTaskManager extends FileBackedTasksManager {

    private transient final KVTaskClient client;
    private final URL url;
    private transient final Gson gson = Managers.getDefaultGson();
    private final String key;

    public HttpTaskManager(URL url, String key) {
        this.url = url;
        client = new KVTaskClient(url);
        this.key = key;
    }

    @Override
    public void save() {
        String value = gson.toJson(this);
        client.getSave(key, value);
    }

    public HttpTaskManager load() {
        String json = client.getLoad(key);
        HttpTaskManager manager = gson.fromJson(json, new TypeToken<HttpTaskManager>() {
        }.getType());
        manager.historyManager = Managers.getDefaultHistory();
        for (Integer id : manager.history) {
            if (manager.getTasksMap().containsKey(id)) {
                manager.historyManager.add(manager.getTasksMap().get(id));
            } else if (manager.getEpicsMap().containsKey(id)) {
                manager.historyManager.add(manager.getEpicsMap().get(id));
            } else if (manager.getSubtasksMap().containsKey(id)) {
                manager.historyManager.add(manager.getSubtasksMap().get(id));
            }
        }
        return manager;
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        new KVServer().start();
        new HttpTaskServer().start();
        HttpClient httpClient = HttpClient.newHttpClient();
        Task task1 = new Task("Переезд1", "Сбор вещей1", 60,
                LocalDateTime.of(2023, 2, 2, 13, 0));
        URI url = URI.create("http://localhost:8078/tasks/task");
        Gson gson = Managers.getDefaultGson();
        String json = gson.toJson(task1);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            System.out.println("Задача добавлена.");
        } else {
            System.out.println("Что-то пошло не так. Сервер вернул код состояния: " + response.statusCode());
        }
    }
}
