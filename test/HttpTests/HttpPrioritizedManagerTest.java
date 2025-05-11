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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpPrioritizedManagerTest extends HttpManagerTest {

    public HttpPrioritizedManagerTest() {
    }



    public Task createTask(LocalDateTime localDateTime) {
        return new Task("Task 1", "Task 1 description", TaskStatuses.NEW, Duration.ofSeconds(60), localDateTime);
    }


    @Test
    public void should_return_prioritized_tasks() throws IOException, InterruptedException {
        Gson gson = httpTaskServer.getGson();

        Task task = createTask(LocalDateTime.now().minusDays(4).withNano(0));
        sendPOSTRequest(task, "tasks");
        task.setId(1);

        Task task2 = createTask(LocalDateTime.now().minusDays(2).withNano(0));
        sendPOSTRequest(task2, "tasks");
        task2.setId(2);

        Task task3 = createTask(LocalDateTime.now().minusDays(5).withNano(0));
        sendPOSTRequest(task3, "tasks");
        task3.setId(3);

        List<Task> prioritizedTasks = new ArrayList<>(4);

        prioritizedTasks.add(task3);
        prioritizedTasks.add(task);
        prioritizedTasks.add(task2);

        HttpResponse<String> response = sendGETRequest("prioritized");

        assertEquals(200, response.statusCode());


        List<Task> prioritizedTasksFromRequest = gson.fromJson(response.body(), new TaskListTypeToken().getType());

        assertArrayEquals(prioritizedTasks.toArray(), prioritizedTasksFromRequest.toArray());
    }
}