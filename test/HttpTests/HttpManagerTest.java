package HttpTests;

import com.google.gson.Gson;
import http.HttpTaskServer;
import managers.HistoryManager;
import managers.Managers;
import managers.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import task.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class HttpManagerTest {
    HistoryManager historyManager = Managers.getDefaultHistory();
    TaskManager taskManager = Managers.getDefault(historyManager);
    HttpTaskServer httpTaskServer = new HttpTaskServer(taskManager, historyManager);

    @BeforeEach
    public void setUp() {
        taskManager.deleteAllTasks();
        taskManager.deleteAllSubTasks();
        taskManager.deleteAllEpics();
        try {
            httpTaskServer.start();
        } catch (IOException e) {
            httpTaskServer.stop();
        }

    }

    @AfterEach
    public void shutDown() {
        httpTaskServer.stop();
    }

    public HttpResponse<String> sendPOSTRequest(Task task, String uri) throws IOException, InterruptedException {
        Gson gson = httpTaskServer.getGson();

        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/" + uri);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public HttpResponse<String> sendGETRequest(String uri) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/" + uri);

        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public HttpResponse<String> sendDELETERequest(String uri) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/" + uri);

        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
