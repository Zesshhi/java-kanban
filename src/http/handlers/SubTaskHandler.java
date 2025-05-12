package http.handlers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exceptions.InvalidDataException;
import exceptions.NotFoundException;
import http.HttpTaskServer;
import managers.TaskManager;
import task.SubTask;
import task.TaskStatuses;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class SubTaskHandler extends BaseHttpHandler implements HttpHandler {

    private final TaskManager taskManager;
    private final Gson gson;

    public SubTaskHandler(TaskManager taskManager, Gson gson) {
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


        if (id.equals("subtasks")) {
            switch (requestMethod) {
                case "GET":
                    handleGetSubTasks(httpExchange);
                    break;
                case "POST":
                    SubTask subTask = deserialize(JsonParser.parseString(body).getAsJsonObject());


                    if (subTask.getId() != 0) {
                        handleSubTaskUpdate(subTask, httpExchange);
                    } else {
                        subTask.setId(HttpTaskServer.currentIdOfTask);
                        handleSubTaskCreate(subTask, httpExchange);
                    }

                    break;
                default:
                    sendNotFound(httpExchange);
            }
        } else {
            // Обработчик {id}
            switch (requestMethod) {
                case "GET":
                    handleGetSubTask(Integer.parseInt(id), httpExchange);
                    break;
                case "DELETE":
                    handleSubTaskDelete(Integer.parseInt(id), httpExchange);
                    break;
                default:
                    sendNotFound(httpExchange);
            }

        }
    }


    public void handleSubTaskCreate(SubTask subTask, HttpExchange httpExchange) throws IOException {
        try {
            taskManager.createSubTask(subTask);
            HttpTaskServer.currentIdOfTask++;
            sendText(httpExchange, "Успешно", 201);
        } catch (InvalidDataException e) {
            sendText(httpExchange, "Ошибка создания подзадачи", 406);
        }
    }

    public void handleSubTaskUpdate(SubTask subTask, HttpExchange httpExchange) throws IOException {
        try {
            taskManager.updateSubTask(subTask);
            sendText(httpExchange, "Успешно", 201);
        } catch (InvalidDataException e) {
            sendText(httpExchange, "Ошибка обновления задачи", 406);
        }
    }

    public void handleSubTaskDelete(int id, HttpExchange httpExchange) throws IOException {
        taskManager.deleteSubTask(id);
        sendText(httpExchange, "Успешно", 200);
    }

    public void handleGetSubTasks(HttpExchange httpExchange) throws IOException {
        List<SubTask> subTasks = taskManager.getSubTasks();

        sendText(httpExchange, gson.toJson(subTasks), 200);

    }

    public void handleGetSubTask(int id, HttpExchange httpExchange) throws IOException {
        try {
            SubTask subTask = taskManager.getSubTask(id);
            sendText(httpExchange, gson.toJson(subTask), 200);
        } catch (NotFoundException e) {
            sendText(httpExchange, "Подзадача с заданным id отсутствует", 404);
        }

    }

    public SubTask deserialize(JsonObject jsonObject) {
        int id;
        if (jsonObject.get("id") != null && !jsonObject.get("id").getAsString().equals("0")) {
            id = jsonObject.get("id").getAsInt();
        } else {
            id = 0;
        }

        String name = jsonObject.get("name").getAsString();
        TaskStatuses taskStatus = TaskStatuses.valueOf(jsonObject.get("taskStatus").getAsString());
        String description = jsonObject.get("description").getAsString();
        int epicId = jsonObject.get("epicId").getAsInt();
        Duration duration = Duration.ofMinutes(jsonObject.get("duration").getAsInt());
        LocalDateTime startTime = LocalDateTime.parse(jsonObject.get("startTime").getAsString(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        return new SubTask(id, name, taskStatus, description, epicId, duration, startTime);
    }
}
