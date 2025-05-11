package http.handlers;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exceptions.InvalidDataException;
import exceptions.NotFoundException;
import http.HttpTaskServer;
import managers.TaskManager;
import task.Task;
import task.TaskStatuses;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {

    private final TaskManager taskManager;
    private final Gson gson;

    public TaskHandler(TaskManager taskManager, Gson gson) {
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

        if (id.equals("tasks")) {
            switch (requestMethod) {
                case "GET":
                    handleGetTasks(httpExchange);
                    break;
                case "POST":
                    JsonObject jsonObject = jsonElement.getAsJsonObject();

                    JsonElement taskIdElem = jsonObject.get("id");

                    if (taskIdElem != null && !taskIdElem.getAsString().equals("0")) {
                        handleTaskUpdate(deserialize(jsonObject, taskIdElem.getAsInt()), httpExchange);
                    } else {
                        handleTaskCreate(deserialize(jsonObject, HttpTaskServer.currentIdOfTask), httpExchange);
                    }

                    break;
                default:
                    sendNotFound(httpExchange);
            }
        } else {
            // Обработчик {id}
            switch (requestMethod) {
                case "GET":
                    handleGetTask(Integer.parseInt(id), httpExchange);
                    break;
                case "DELETE":
                    handleTaskDelete(Integer.parseInt(id), httpExchange);
                    break;
                default:
                    sendNotFound(httpExchange);
            }

        }
    }


    public void handleTaskCreate(Task task, HttpExchange httpExchange) throws IOException {
        try {
            taskManager.createTask(task);
            HttpTaskServer.currentIdOfTask++;
            sendText(httpExchange, "Успешно", 201);
        } catch (InvalidDataException e) {
            sendText(httpExchange, "Задача пересекается с существующими", 406);
        }
    }

    public void handleTaskUpdate(Task task, HttpExchange httpExchange) throws IOException {
        try {
            taskManager.updateTask(task);
            sendText(httpExchange, "Успешно", 201);
        } catch (InvalidDataException e) {
            sendText(httpExchange, "Задача пересекается с существующими", 406);
        }
    }

    public void handleTaskDelete(int id, HttpExchange httpExchange) throws IOException {
        taskManager.deleteTask(id);
        sendText(httpExchange, "Успешно", 200);
    }

    public void handleGetTasks(HttpExchange httpExchange) throws IOException {
        List<Task> tasks = taskManager.getTasks();

        sendText(httpExchange, gson.toJson(tasks), 200);

    }

    public void handleGetTask(int id, HttpExchange httpExchange) throws IOException {
        try {
            Task task = taskManager.getTask(id);
            sendText(httpExchange, gson.toJson(task), 200);
        } catch (NotFoundException e) {
            sendText(httpExchange, "Задача с заданным id отсутствует", 404);
        }

    }

    public Task deserialize(JsonObject jsonObject, int currentIdOfTask) {

        String name = jsonObject.get("name").getAsString();
        TaskStatuses taskStatus = TaskStatuses.valueOf(jsonObject.get("taskStatus").getAsString());
        String description = jsonObject.get("description").getAsString();
        Duration duration = Duration.ofMinutes(jsonObject.get("duration").getAsInt());
        LocalDateTime startTime = LocalDateTime.parse(jsonObject.get("startTime").getAsString(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        return new Task(currentIdOfTask, name, taskStatus, description, duration, startTime);
    }

}
