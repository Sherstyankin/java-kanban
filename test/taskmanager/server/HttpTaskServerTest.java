package taskmanager.server;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.*;
import taskmanager.model.Epic;
import taskmanager.model.Subtask;
import taskmanager.model.Task;
import taskmanager.service.Managers;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskServerTest {
    private HttpTaskServer httpTaskServer;
    private KVServer kvServer;
    private HttpClient client;
    private Gson gson = Managers.getDefaultGson();
    private Task task1;
    private Task task2;
    private Epic epic1;
    private Epic epic2;
    private Subtask subtask1;
    private Subtask subtask2;

    @BeforeEach
    void init() {
        kvServer = new KVServer();
        kvServer.start();
        httpTaskServer = new HttpTaskServer(); // необходимый менеджер инициализируется внутри сервера
        httpTaskServer.start();
        client = HttpClient.newHttpClient();
        task1 = new Task("Переезд1", "Сбор вещей1", 60, LocalDateTime.now());
        task2 = new Task("Переезд2", "Сбор вещей2", 30, LocalDateTime.now());
        epic1 = new Epic("Aрендовать авто", "Универсал",
                new ArrayList<>());
        epic2 = new Epic("Закупить продукты", "Не забыть про овощи",
                new ArrayList<>());
        subtask1 = new Subtask("Выбрать агенство", "Оценить по рейтингу и цене",
                60, LocalDateTime.of(2023, 2, 4, 14, 0),
                epic1.getId());
        subtask2 = new Subtask("Отправить заявку", "Прикрепить документы",
                0, null,
                epic1.getId());
    }

    @AfterEach
    void stopKVServer() {
        httpTaskServer.stop();
        kvServer.stop();
    }

    @Test
    void addTaskEndpointTest() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8078/tasks/task");
        String json = gson.toJson(task1);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }

    @Test
    void getTaskListEndpointTest() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8078/tasks/task");
        String json = gson.toJson(task1);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        Type taskType = new TypeToken<ArrayList<Task>>() {
        }.getType();
        List<Task> actual = gson.fromJson(response.body(), taskType);
        assertNotNull(actual, "Задачи не возвращаются.");
        task1.setId(1);
        assertEquals(task1, actual.get(0), "Задачи не совпадают.");
    }

    @Test
    void getTaskByIdEndpointTest() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8078/tasks/task");
        String json = gson.toJson(task1);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        url = URI.create("http://localhost:8078/tasks/task/1");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        Type taskType = new TypeToken<Task>() {
        }.getType();
        Task actual = gson.fromJson(response.body(), taskType);
        assertNotNull(actual, "Задачи не возвращаются.");
        task1.setId(1);
        assertEquals(task1, actual, "Задачи не совпадают.");
    }

    @Test
    void deleteAllTasksEndpointTest() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8078/tasks/task");
        String json = gson.toJson(task1);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        url = URI.create("http://localhost:8078/tasks/task");
        request = HttpRequest.newBuilder().uri(url).DELETE().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Type taskType = new TypeToken<ArrayList<Task>>() {
        }.getType();
        List<Task> actual = gson.fromJson(response.body(), taskType);
        boolean isTrue = actual.isEmpty();
        assertTrue(isTrue, "Список задач не очистился.");
    }

    @Test
    void deleteByTaskIdEndpointTest() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8078/tasks/task");
        String json = gson.toJson(task1);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        url = URI.create("http://localhost:8078/tasks/task/1");
        request = HttpRequest.newBuilder().uri(url).DELETE().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        url = URI.create("http://localhost:8078/tasks/task");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Type taskType = new TypeToken<ArrayList<Task>>() {
        }.getType();
        List<Task> actual = gson.fromJson(response.body(), taskType);
        boolean isTrue = actual.isEmpty();
        assertTrue(isTrue, "Список задач не очистился.");
    }

    @Test
    void updateTaskEndpointTest() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8078/tasks/task");
        String json = gson.toJson(task1);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        task2.setId(1);
        json = gson.toJson(task2);
        final HttpRequest.BodyPublisher body2 = HttpRequest.BodyPublishers.ofString(json);
        request = HttpRequest.newBuilder().uri(url).POST(body2).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Type taskType = new TypeToken<ArrayList<Task>>() {
        }.getType();
        List<Task> actual = gson.fromJson(response.body(), taskType);
        Task actualTask = actual.get(0);
        assertEquals(task2, actualTask, "Задача не обновлена.");
    }


    //Epic methods
    @Test
    void addEpicEndpointTest() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8078/tasks/epic");
        String json = gson.toJson(epic1);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }

    @Test
    void getEpicListEndpointTest() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8078/tasks/epic");
        String json = gson.toJson(epic1);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }

    @Test
    void getEpicByIdEndpointTest() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8078/tasks/epic");
        String json = gson.toJson(epic1);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        url = URI.create("http://localhost:8078/tasks/epic/1");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

    }

    @Test
    void deleteAllEpicsEndpointTest() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8078/tasks/epic");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }

    @Test
    void deleteByEpicIdEndpointTest() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8078/tasks/epic");
        String json = gson.toJson(epic1);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        url = URI.create("http://localhost:8078/tasks/epic/1");
        request = HttpRequest.newBuilder().uri(url).DELETE().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }

    @Test
    void updateEpicEndpointTest() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8078/tasks/epic");
        String json = gson.toJson(epic1);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        epic2.setId(1);
        json = gson.toJson(epic2);
        final HttpRequest.BodyPublisher body2 = HttpRequest.BodyPublishers.ofString(json);
        request = HttpRequest.newBuilder().uri(url).POST(body2).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Type taskType = new TypeToken<ArrayList<Epic>>() {
        }.getType();
        List<Epic> actual = gson.fromJson(response.body(), taskType);
        Epic actualEpic = actual.get(0);
        assertEquals(epic2, actualEpic, "Задача не обновлена.");
    }


    //Subtask methods
    @Test
    void addSubtaskEndpointTest() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8078/tasks/epic");
        String json1 = gson.toJson(epic1);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json1);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        url = URI.create("http://localhost:8078/tasks/subtask");
        String json2 = gson.toJson(subtask1);
        final HttpRequest.BodyPublisher body2 = HttpRequest.BodyPublishers.ofString(json2);
        request = HttpRequest.newBuilder().uri(url).POST(body2).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }

    @Test
    void getSubtaskListEndpointTest() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8078/tasks/epic");
        String json1 = gson.toJson(epic1);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json1);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        url = URI.create("http://localhost:8078/tasks/subtask");
        String json2 = gson.toJson(subtask1);
        final HttpRequest.BodyPublisher body2 = HttpRequest.BodyPublishers.ofString(json2);
        request = HttpRequest.newBuilder().uri(url).POST(body2).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }

    @Test
    void getSubtaskByIdEndpointTest() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8078/tasks/epic");
        String json = gson.toJson(epic1);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        url = URI.create("http://localhost:8078/tasks/subtask");
        json = gson.toJson(subtask1);
        final HttpRequest.BodyPublisher body2 = HttpRequest.BodyPublishers.ofString(json);
        request = HttpRequest.newBuilder().uri(url).POST(body2).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        url = URI.create("http://localhost:8078/tasks/subtask/1");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }

    @Test
    void deleteAllSubtasksEndpointTest() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8078/tasks/subtask");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }

    @Test
    void deleteBySubtaskIdEndpointTest() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8078/tasks/epic");
        String json = gson.toJson(epic1);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        url = URI.create("http://localhost:8078/tasks/subtask");
        json = gson.toJson(subtask1);
        final HttpRequest.BodyPublisher body2 = HttpRequest.BodyPublishers.ofString(json);
        request = HttpRequest.newBuilder().uri(url).POST(body2).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        url = URI.create("http://localhost:8078/tasks/subtask/1");
        request = HttpRequest.newBuilder().uri(url).DELETE().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }

    @Test
    void updateSubtaskEndpointTest() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8078/tasks/epic");
        String json = gson.toJson(epic1);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        url = URI.create("http://localhost:8078/tasks/subtask");
        json = gson.toJson(subtask2);
        final HttpRequest.BodyPublisher body1 = HttpRequest.BodyPublishers.ofString(json);
        request = HttpRequest.newBuilder().uri(url).POST(body1).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        subtask1.setId(1);
        json = gson.toJson(subtask1);
        final HttpRequest.BodyPublisher body2 = HttpRequest.BodyPublishers.ofString(json);
        request = HttpRequest.newBuilder().uri(url).POST(body2).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }

    @Test
    void getSubtaskListByEpicIdEndpointTest() throws IOException, InterruptedException {
        Epic epic3 = new Epic("Закупить продукты", "Не забыть про овощи",
                new ArrayList<>());
        epic3.setId(123);
        URI url = URI.create("http://localhost:8078/tasks/subtask/epic/123");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }

    @Test
    void getHistoryEndpointTest() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8078/tasks/task");
        String json = gson.toJson(task1);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        url = URI.create("http://localhost:8078/tasks/task/1");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Type taskType = new TypeToken<Task>() {
        }.getType();
        Task expected = gson.fromJson(response.body(), taskType);

        url = URI.create("http://localhost:8078/tasks/history");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Type taskType1 = new TypeToken<ArrayList<Task>>() {
        }.getType();
        List<Task> historyList = gson.fromJson(response.body(), taskType1);
        Task actual = historyList.get(0);
        assertEquals(expected, actual, "История не совпадает.");
    }

    @Test
    void getPrioritizedTasksListEndpointTest() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8078/tasks/task");
        String json = gson.toJson(task1);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        url = URI.create("http://localhost:8078/tasks/subtask");
        json = gson.toJson(subtask1);
        final HttpRequest.BodyPublisher body2 = HttpRequest.BodyPublishers.ofString(json);
        request = HttpRequest.newBuilder().uri(url).POST(body2).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        url = URI.create("http://localhost:8078/tasks");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }
}