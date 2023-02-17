package taskmanager.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import taskmanager.client.KVTaskClient;

import java.net.URL;


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
}
