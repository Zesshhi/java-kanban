package HttpTests;

import com.google.gson.Gson;
import exceptions.NotFoundException;
import http.deserializers.TaskListTypeToken;
import org.junit.jupiter.api.Test;
import task.Task;
import task.TaskStatuses;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskManagerTest extends HttpManagerTest {

    public HttpTaskManagerTest() {
    }


    public Task createTask(LocalDateTime localDateTime) {
        return new Task("Task 1", "Task 1 description", TaskStatuses.NEW, Duration.ofSeconds(60), localDateTime);
    }

    @Test
    public void should_create_task() throws IOException, InterruptedException {
        Task task = createTask(LocalDateTime.now().minusDays(5).withNano(0));
        HttpResponse<String> response = sendPOSTRequest(task, "tasks");

        assertEquals(201, response.statusCode());

        task.setId(1);

        Task createdTask = taskManager.getTask(task.getId());
        assertEquals(task, createdTask, "Задача не создалась");

    }

    @Test
    public void should_update_task() throws IOException, InterruptedException {
        Task task = createTask(LocalDateTime.now().minusDays(5).withNano(0));
        sendPOSTRequest(task, "tasks");
        task.setId(1);

        task.setName("Updated Task 1 Name");
        task.setDescription("Updated Task 1 description");
        task.setTaskStatus(TaskStatuses.DONE);
        sendPOSTRequest(task, "tasks");

        Task updatedTask = taskManager.getTask(task.getId());
        assertEquals(task, updatedTask, "Задача не создалась");

    }

    @Test
    public void should_delete_task() throws IOException, InterruptedException {
        Task task = createTask(LocalDateTime.now().minusDays(5).withNano(0));
        sendPOSTRequest(task, "tasks");
        task.setId(1);

        HttpResponse<String> response = sendDELETERequest("tasks/" + task.getId());

        assertEquals(200, response.statusCode());
        try {
            assertNull(taskManager.getTask(task.getId()));
        } catch (NotFoundException ignore) {
        }
    }

    @Test
    public void should_return_task() throws IOException, InterruptedException {
        Gson gson = httpTaskServer.getGson();

        Task task = createTask(LocalDateTime.now().minusDays(5).withNano(0));
        sendPOSTRequest(task, "tasks");
        task.setId(1);

        HttpResponse<String> response = sendGETRequest("tasks/" + task.getId());
        assertEquals(200, response.statusCode());


        Task createdTask = gson.fromJson(response.body(), Task.class);

        assertEquals(task, createdTask, "Задача не создалась");
    }


    @Test
    public void should_return_tasks() throws IOException, InterruptedException {
        Gson gson = httpTaskServer.getGson();

        Task task = createTask(LocalDateTime.now().minusDays(5).withNano(0));
        sendPOSTRequest(task, "tasks");
        task.setId(1);

        Task task2 = createTask(LocalDateTime.now().minusDays(6).withNano(0));
        sendPOSTRequest(task2, "tasks");
        task2.setId(2);

        Task task3 = createTask(LocalDateTime.now().minusDays(7).withNano(0));
        sendPOSTRequest(task3, "tasks");
        task3.setId(3);

        HttpResponse<String> response = sendGETRequest("tasks");

        assertEquals(200, response.statusCode());

        List<Task> createdTasks = new ArrayList<>(3);

        createdTasks.add(task);
        createdTasks.add(task2);
        createdTasks.add(task3);

        List<Task> tasksFromRequest = gson.fromJson(response.body(), new TaskListTypeToken().getType());

        assertArrayEquals(createdTasks.toArray(), tasksFromRequest.toArray());

    }
}