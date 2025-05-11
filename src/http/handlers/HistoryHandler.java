package http.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managers.HistoryManager;
import task.Task;

import java.io.IOException;
import java.util.List;

public class HistoryHandler extends BaseHttpHandler implements HttpHandler {

    private final HistoryManager historyManger;
    private final Gson gson;

    public HistoryHandler(HistoryManager historyManager, Gson gson) {
        this.historyManger = historyManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String requestMethod = httpExchange.getRequestMethod();

        if (requestMethod.equals("GET")) {
            handleGetHistory(httpExchange);
        } else {
            sendNotFound(httpExchange);
        }

    }


    public void handleGetHistory(HttpExchange httpExchange) throws IOException {
        List<Task> tasks = historyManger.getHistory();
        sendText(httpExchange, gson.toJson(tasks), 200);
    }
}
