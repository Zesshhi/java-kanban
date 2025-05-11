package http.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managers.TaskManager;
import task.Task;

import java.io.IOException;
import java.util.List;

public class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {

    private final TaskManager taskManager;
    private final Gson gson;

    public PrioritizedHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }



    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String requestMethod = httpExchange.getRequestMethod();

        if (requestMethod.equals("GET")) {
            handleGetPrioritizedTasks(httpExchange);
        } else {
            sendNotFound(httpExchange);
        }

    }


    public void handleGetPrioritizedTasks(HttpExchange httpExchange) throws IOException {
        List<Task> tasks = taskManager.getPrioritizedTasks();
        sendText(httpExchange, gson.toJson(tasks), 200);
    }
}
