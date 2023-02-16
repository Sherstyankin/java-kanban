package taskmanager.server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import taskmanager.model.Epic;
import taskmanager.model.Subtask;
import taskmanager.model.Task;
import taskmanager.service.Managers;
import taskmanager.service.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.regex.Pattern;

import static java.nio.charset.StandardCharsets.UTF_8;

public class HttpTaskServer {
    private static final int PORT = 8078;
    private final TaskManager manager; // HttpTaskManager
    private HttpServer server;
    private final Gson gson = Managers.getDefaultGson(); // преобразовать через GSONBuilder в Managers

    public HttpTaskServer() {
        this(Managers.getDefault());
    }

    public HttpTaskServer(TaskManager manager) {
        this.manager = manager;
        try {
            server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
            server.createContext("/tasks", this::handleTasks);
        } catch (IOException e) {
            System.out.println("Указанный порт занят.");
        }
    }

    public void handleTasks(HttpExchange exchange) {
        try {
            String path = exchange.getRequestURI().getPath();
            String method = exchange.getRequestMethod();
            switch (method) {
                case "GET": {
                    handleGetMethods(exchange, path);
                    break;
                }
                case "POST": {
                    handlePostMethods(exchange, path);
                    break;
                }
                case "DELETE": {
                    handleDeleteMethods(exchange, path);
                    break;
                }
                default:
                    System.out.println("Ждем GET/POST/DELETE запросы, а получили: " + method);
                    exchange.sendResponseHeaders(405, 0);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            exchange.close();
        }
    }

    public void handleGetMethods(HttpExchange exchange, String path) throws IOException {
        if (Pattern.matches("^/tasks/task$", path)) { //получение списка задач
            String response = gson.toJson(manager.getTaskList());
            sendText(exchange, response);
        } else if (Pattern.matches("^/tasks/epic$", path)) { //получение списка эпиков
            String response = gson.toJson(manager.getEpicList());
            sendText(exchange, response);
        } else if (Pattern.matches("^/tasks/subtask$", path)) { //получение списка подзадач
            String response = gson.toJson(manager.getSubtaskList());
            sendText(exchange, response);
        } else if (Pattern.matches("^/tasks/task/\\d+$", path)) { //получение задачи по id
            String pathId = path.replaceFirst("/tasks/task/", "");
            int id = parsePathId(pathId);
            if (id != -1) {
                String response = gson.toJson(manager.getTaskById(id));
                sendText(exchange, response);
            } else {
                System.out.println("Получен некорректный id = " + pathId);
                exchange.sendResponseHeaders(405, 0);
            }
        } else if (Pattern.matches("^/tasks/epic/\\d+$", path)) { //получение эпика по id
            String pathId = path.replaceFirst("/tasks/epic/", "");
            int id = parsePathId(pathId);
            if (id != -1) {
                String response = gson.toJson(manager.getEpicById(id));
                sendText(exchange, response);
            } else {
                System.out.println("Получен некорректный id = " + pathId);
                exchange.sendResponseHeaders(405, 0);
            }
        } else if (Pattern.matches("^/tasks/subtask/\\d+$", path)) { //получение подзадачи по id
            String pathId = path.replaceFirst("/tasks/subtask/", "");
            int id = parsePathId(pathId);
            if (id != -1) {
                String response = gson.toJson(manager.getSubtaskById(id));
                sendText(exchange, response);
            } else {
                System.out.println("Получен некорректный id = " + pathId);
                exchange.sendResponseHeaders(405, 0);
            }
        } else if (Pattern.matches("^/tasks/subtask/epic/\\d+$", path)) { //получение всех подзадач по id эпика
            String pathId = path.replaceFirst("/tasks/subtask/epic/", "");
            int id = parsePathId(pathId);
            if (id != -1) {
                String response = gson.toJson(manager.getSubtaskListByEpicId(id));
                sendText(exchange, response);
            } else {
                System.out.println("Получен некорректный id = " + pathId);
                exchange.sendResponseHeaders(405, 0);
            }
        } else if (Pattern.matches("^/tasks/history$", path)) { //получение истории
            String response = gson.toJson(manager.getHistory());
            sendText(exchange, response);
        } else if (Pattern.matches("^/tasks$", path)) { //получение приоретизированного списка всех задач
            String response = gson.toJson(manager.getPrioritizedTasksList());
            sendText(exchange, response);
        } else {
            System.out.println("Получен некорректный путь = " + path);
            exchange.sendResponseHeaders(404, 0);
        }
    }

    public void handleDeleteMethods(HttpExchange exchange, String path) throws IOException {
        if (Pattern.matches("^/tasks/task$", path)) { //удаление всех задач
            manager.deleteAllTasks();
        } else if (Pattern.matches("^/tasks/epic$", path)) { //удаление всех эпиков
            manager.deleteAllEpics();
        } else if (Pattern.matches("^/tasks/subtask$", path)) { //удаление всех подзадач
            manager.deleteAllSubtasks();
        } else if (Pattern.matches("^/tasks/task/\\d+$", path)) { //получение задачи по id
            String pathId = path.replaceFirst("/tasks/task/", "");
            int id = parsePathId(pathId);
            if (id != -1) {
                manager.deleteByTaskId(id);
                System.out.println("Задача под ID = " + id + " удалена.");
            } else {
                System.out.println("Получен некорректный id = " + pathId);
                exchange.sendResponseHeaders(405, 0);
                return;
            }
        } else if (Pattern.matches("^/tasks/epic/\\d+$", path)) { //получение эпика по id
            String pathId = path.replaceFirst("/tasks/epic/", "");
            int id = parsePathId(pathId);
            if (id != -1) {
                manager.deleteByEpicId(id);
                System.out.println("Эпик под ID = " + id + " удален.");
            } else {
                System.out.println("Получен некорректный id = " + pathId);
                exchange.sendResponseHeaders(405, 0);
                return;
            }
        } else if (Pattern.matches("^/tasks/subtask/\\d+$", path)) { //получение подзадачи по id
            String pathId = path.replaceFirst("/tasks/subtask/", "");
            int id = parsePathId(pathId);
            if (id != -1) {
                manager.deleteBySubtaskId(id);
                System.out.println("Подзадача под ID = " + id + " удалена.");
            } else {
                System.out.println("Получен некорректный id = " + pathId);
                exchange.sendResponseHeaders(405, 0);
                return;
            }
        } else {
            System.out.println("Получен некорректный путь = " + path);
            exchange.sendResponseHeaders(404, 0);
            return;
        }
        exchange.sendResponseHeaders(200, 0);
    }

    public void handlePostMethods(HttpExchange exchange, String path) throws IOException {
        if (Pattern.matches("^/tasks/task$", path)) { //добавление задачи
            String body = readText(exchange);
            Task task = gson.fromJson(body, Task.class);
            if (task.getId() == null) {
                manager.addTask(task);
            } else {
                manager.updateTask(task);
            }
        } else if (Pattern.matches("^/tasks/epic$", path)) { //добавление эпика
            String body = readText(exchange);
            Epic epic = gson.fromJson(body, Epic.class);
            if (epic.getId() == null) {
                manager.addEpic(epic);
            } else {
                manager.updateEpic(epic);
            }
        } else if (Pattern.matches("^/tasks/subtask$", path)) { //добавление подзадачи
            String body = readText(exchange);
            Subtask subtask = gson.fromJson(body, Subtask.class);
            if (subtask.getId() == null) {
                manager.addSubtask(subtask);
            } else {
                manager.updateSubtask(subtask);
            }
        } else {
            System.out.println("Получен некорректный путь = " + path);
            exchange.sendResponseHeaders(404, 0);
            return;
        }
        exchange.sendResponseHeaders(200, 0);
    }

    private int parsePathId(String pathId) {
        try {
            return Integer.parseInt(pathId);
        } catch (NumberFormatException nfe) {
            return -1;
        }
    }

    public void start() {
        System.out.println("Запускаем сервер на порту " + PORT);
        System.out.println("http://localhost:" + PORT + "/tasks");
        server.start();
    }

    public void stop() {
        server.stop(0);
        System.out.println("Остановили сервер на порту " + PORT);
    }

    protected String readText(HttpExchange h) throws IOException {
        return new String(h.getRequestBody().readAllBytes(), UTF_8);
    }

    protected void sendText(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes(UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json");
        h.sendResponseHeaders(200, resp.length);
        h.getResponseBody().write(resp);
    }
}
