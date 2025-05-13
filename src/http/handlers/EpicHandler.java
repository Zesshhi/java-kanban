package http.handlers;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exceptions.NotFoundException;
import http.HttpTaskServer;
import managers.TaskManager;
import task.Epic;
import task.SubTask;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class EpicHandler extends BaseHttpHandler implements HttpHandler {

    private final TaskManager taskManager;
    private final Gson gson;

    public EpicHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }


    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String requestMethod = httpExchange.getRequestMethod();
        String requestPath = httpExchange.getRequestURI().getPath();

        String body = getRequestBody(httpExchange);

        String[] pathParts = requestPath.split("/");
        String id = pathParts[pathParts.length - 1];

        JsonElement jsonElement = JsonParser.parseString(body);

        if (id.equals("epics")) {
            switch (requestMethod) {
                case "GET":
                    handleGetEpics(httpExchange);
                    break;
                case "POST":
                    JsonObject jsonObject = jsonElement.getAsJsonObject();

                    JsonElement taskIdElem = jsonObject.get("id");

                    if (taskIdElem != null && !taskIdElem.getAsString().equals("0")) {
                        handleEpicUpdate(deserialize(jsonObject, taskIdElem.getAsInt()), httpExchange);
                    } else {
                        handleEpicCreate(deserialize(jsonObject, HttpTaskServer.currentIdOfTask), httpExchange);
                    }

                    break;
                default:
                    sendNotFound(httpExchange);
            }
        } else if (id.equals("subtasks")) {
            handleGetEpicSubTasks(Integer.parseInt(pathParts[pathParts.length - 2]), httpExchange);
        } else {
            // Обработчик {id}
            switch (requestMethod) {
                case "GET":
                    handleGetEpic(Integer.parseInt(id), httpExchange);
                    break;
                case "DELETE":
                    handleEpicDelete(Integer.parseInt(id), httpExchange);
                    break;
                default:
                    sendNotFound(httpExchange);
            }

        }
    }


    public void handleEpicCreate(Epic epic, HttpExchange httpExchange) throws IOException {
        taskManager.createEpic(epic);
        HttpTaskServer.currentIdOfTask++;
        sendText(httpExchange, "Успешно", 201);
    }

    public void handleEpicUpdate(Epic epic, HttpExchange httpExchange) throws IOException {
        taskManager.updateEpic(epic);
        sendText(httpExchange, "Успешно", 201);
    }

    public void handleEpicDelete(int id, HttpExchange httpExchange) throws IOException {
        taskManager.deleteEpic(id);
        sendText(httpExchange, "Успешно", 200);
    }

    public void handleGetEpics(HttpExchange httpExchange) throws IOException {
        List<Epic> epics = taskManager.getEpics();

        sendText(httpExchange, gson.toJson(epics), 200);

    }

    public void handleGetEpic(int id, HttpExchange httpExchange) throws IOException {
        try {
            Epic epic = taskManager.getEpic(id);
            sendText(httpExchange, gson.toJson(epic), 200);
        } catch (NotFoundException e) {
            sendText(httpExchange, "Эпик с заданным id отсутствует", 404);
        }

    }

    public void handleGetEpicSubTasks(int id, HttpExchange httpExchange) throws IOException {
        try {
            List<SubTask> subTasks = taskManager.getEpicSubTasks(id);
            sendText(httpExchange, gson.toJson(subTasks), 200);
        } catch (NotFoundException e) {
            sendText(httpExchange, "Эпик с заданным id отсутствует", 404);
        }

    }

    public Epic deserialize(JsonObject jsonObject, int currentIdOfTask) {

        String name = jsonObject.get("name").getAsString();
        String description = jsonObject.get("description").getAsString();

        List<Integer> subTasksIds = new ArrayList<>();
        JsonArray jArray = jsonObject.get("subTasksIds").getAsJsonArray();
        if (jArray != null) {
            for (int i = 0; i < jArray.size(); i++) {
                subTasksIds.add(jArray.get(i).getAsInt());
            }
        }

        Duration duration = Duration.ofMinutes(jsonObject.get("duration").getAsInt());
        LocalDateTime startTime = LocalDateTime.parse(jsonObject.get("startTime").getAsString(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        return new Epic(name, description, currentIdOfTask, subTasksIds, duration, startTime);
    }
}
