package http;

import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import http.deserializers.DurationTypeAdapter;
import http.deserializers.LocalDateTimeTypeAdapter;
import http.handlers.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

import com.google.gson.Gson;
import managers.HistoryManager;
import managers.Managers;
import managers.TaskManager;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private static HttpServer httpServer;
    private static Gson gson;
    private static TaskManager taskManager;
    private static HistoryManager historyManager;
    public static int currentIdOfTask = 1;

    public HttpTaskServer() {
        historyManager = Managers.getDefaultHistory();
        taskManager = Managers.getDefault(historyManager);
    }

    public HttpTaskServer(TaskManager taskManager, HistoryManager historyManager) {
        this.taskManager = taskManager;
        this.historyManager = historyManager;
    }


    public static void main(String[] args) {
        HttpTaskServer httpTaskServer = new HttpTaskServer();
        try {
            httpTaskServer.start();
        } catch (IOException e) {
            httpTaskServer.stop();
        }
    }

    public static Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();

        gsonBuilder.registerTypeAdapter(Duration.class, new DurationTypeAdapter());
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter());

        return gsonBuilder.create();
    }

    public void start() throws IOException {
        httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        gson = getGson();
        createRoutes();
        currentIdOfTask = 1;
        httpServer.start();
    }

    public void stop() {
        httpServer.stop(1);
    }

    public void createRoutes() {
        httpServer.createContext("/tasks", new TaskHandler(taskManager, gson));
        httpServer.createContext("/subtasks", new SubTaskHandler(taskManager, gson));
        httpServer.createContext("/epics", new EpicHandler(taskManager, gson));
        httpServer.createContext("/history", new HistoryHandler(historyManager, gson));
        httpServer.createContext("/prioritized", new PrioritizedHandler(taskManager, gson));
    }


}
