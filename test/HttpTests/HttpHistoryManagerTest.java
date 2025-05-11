package HttpTests;

import com.google.gson.Gson;
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

public class HttpHistoryManagerTest extends HttpManagerTest {

    public HttpHistoryManagerTest() {
    }


    public Task createTask(LocalDateTime localDateTime) {
        return new Task("Task 1", "Task 1 description", TaskStatuses.NEW, Duration.ofSeconds(60), localDateTime);
    }


    @Test
    public void should_return_history() throws IOException, InterruptedException {
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

        List<Task> historyTasks = new ArrayList<>(4);

        historyTasks.add(task3);
        historyTasks.add(task);
        historyTasks.add(task2);

        sendGETRequest("tasks/" + task3.getId());
        sendGETRequest("tasks/" + task.getId());
        sendGETRequest("tasks/" + task2.getId());


        HttpResponse<String> response = sendGETRequest("history");

        assertEquals(200, response.statusCode());


        List<Task> historyTasksFromRequest = gson.fromJson(response.body(), new TaskListTypeToken().getType());

        assertArrayEquals(historyTasks.toArray(), historyTasksFromRequest.toArray());
    }
}