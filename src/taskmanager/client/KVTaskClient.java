package taskmanager.client;


import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {
    private final HttpClient client = HttpClient.newHttpClient();
    private final URL url;
    private final String apiToken;

    public KVTaskClient(URL url) {
        this.url = url;
        apiToken = getRegister();
    }

    public String getRegister() {
        URI uri = URI.create(url + "/register");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        try {
            final HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return response.body();
            } else {
                System.out.println("Что-то пошло не так. Сервер вернул код состояния: " + response.statusCode());
            }
        } catch (Exception e) {
            System.out.println("Во время выполнения запроса возникла ошибка.\n" +
                    "Проверьте адрес и повторите попытку.");
            e.printStackTrace();
        }
        return "";
    }

    public void getSave(String key, String json) {
        URI uri = URI.create(url + "/save/" + key + "?API_TOKEN=" + apiToken);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        try {
            final HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                System.out.println("Что-то пошло не так. Сервер вернул код состояния: " + response.statusCode());
            }
        } catch (Exception e) {
            System.out.println("Во время выполнения запроса возникла ошибка.\n" +
                    "Проверьте адрес и повторите попытку.");
            e.printStackTrace();
        }
    }

    public String getLoad(String key) {
        URI uri = URI.create(url + "/load/" + key + "?API_TOKEN=" + apiToken);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        String json = "";
        try {
            final HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                json = response.body();
            } else {
                System.out.println("Что-то пошло не так. Сервер вернул код состояния: " + response.statusCode());
            }
        } catch (Exception e) {
            System.out.println("Во время выполнения запроса возникла ошибка.\n" +
                    "Проверьте адрес и повторите попытку.");
            e.printStackTrace();
        }
        return json;
    }
}
