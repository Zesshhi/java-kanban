package HttpTests;

import com.google.gson.Gson;
import exceptions.NotFoundException;
import http.deserializers.SubTaskListTypeToken;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.SubTask;
import task.TaskStatuses;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HttpSubTaskManagerTest extends HttpManagerTest {

    public HttpSubTaskManagerTest() {
    }


    public Epic createEpic(LocalDateTime localDateTime) {
        return new Epic("Epic 1", "Epic description 1", new ArrayList<>(0), TaskStatuses.NEW, Duration.ofSeconds(60), localDateTime);
    }


    public SubTask createSubTask(LocalDateTime localDateTime, int epicId) {
        return new SubTask("SubTask 1", "SubTask description 1", epicId, TaskStatuses.NEW, Duration.ofSeconds(60), localDateTime);
    }

    @Test
    public void should_create_subtask() throws IOException, InterruptedException {
        Epic epic = createEpic(LocalDateTime.now().minusDays(5).withNano(0));
        sendPOSTRequest(epic, "epics");
        epic.setId(1);

        SubTask subTask = createSubTask(LocalDateTime.now().minusDays(6).withNano(0), epic.getId());
        HttpResponse<String> response = sendPOSTRequest(subTask, "subtasks");

        assertEquals(201, response.statusCode());

        subTask.setId(2);

        SubTask createdSubTask = taskManager.getSubTask(subTask.getId());
        assertEquals(subTask, createdSubTask, "ПодЗадача не создалась");

    }

    @Test
    public void should_update_subtask() throws IOException, InterruptedException {
        Epic epic = createEpic(LocalDateTime.now().minusDays(5).withNano(0));
        sendPOSTRequest(epic, "epics");
        epic.setId(1);

        SubTask subTask = createSubTask(LocalDateTime.now().minusDays(6).withNano(0), epic.getId());
        sendPOSTRequest(subTask, "subtasks");
        subTask.setId(2);

        subTask.setName("Updated SubTask 1 Name");
        subTask.setDescription("Updated SubTask 1 description");
        subTask.setTaskStatus(TaskStatuses.DONE);
        sendPOSTRequest(subTask, "subtasks");

        SubTask updatedSubTask = taskManager.getSubTask(subTask.getId());
        assertEquals(subTask, updatedSubTask, "ПодЗадача не создалась");

    }

    @Test
    public void should_delete_subtask() throws IOException, InterruptedException {
        Epic epic = createEpic(LocalDateTime.now().minusDays(5).withNano(0));
        sendPOSTRequest(epic, "epics");
        epic.setId(1);

        SubTask subTask = createSubTask(LocalDateTime.now().minusDays(6).withNano(0), epic.getId());
        sendPOSTRequest(subTask, "subtasks");
        subTask.setId(2);

        HttpResponse<String> response = sendDELETERequest("subtasks/" + subTask.getId());

        assertEquals(200, response.statusCode());
        try {
            assertNull(taskManager.getSubTask(subTask.getId()));
        } catch (NotFoundException ignore) {
        }
    }

    @Test
    public void should_return_subtask() throws IOException, InterruptedException {
        Gson gson = httpTaskServer.getGson();

        Epic epic = createEpic(LocalDateTime.now().minusDays(5).withNano(0));
        sendPOSTRequest(epic, "epics");
        epic.setId(1);

        SubTask subTask = createSubTask(LocalDateTime.now().minusDays(6).withNano(0), epic.getId());
        sendPOSTRequest(subTask, "subtasks");
        subTask.setId(2);

        HttpResponse<String> response = sendGETRequest("subtasks/" + subTask.getId());
        assertEquals(200, response.statusCode());


        SubTask createdSubTask = gson.fromJson(response.body(), SubTask.class);

        assertEquals(subTask, createdSubTask, "ПодЗадача не создалась");
    }


    @Test
    public void should_return_subtasks() throws IOException, InterruptedException {
        Gson gson = httpTaskServer.getGson();

        Epic epic = createEpic(LocalDateTime.now().minusDays(5).withNano(0));
        sendPOSTRequest(epic, "epics");
        epic.setId(1);

        SubTask subTask = createSubTask(LocalDateTime.now().minusDays(6).withNano(0), epic.getId());
        sendPOSTRequest(subTask, "subtasks");
        subTask.setId(2);

        SubTask subTask2 = createSubTask(LocalDateTime.now().minusDays(7).withNano(0), epic.getId());
        sendPOSTRequest(subTask2, "subtasks");
        subTask2.setId(3);

        SubTask subTask3 = createSubTask(LocalDateTime.now().minusDays(8).withNano(0), epic.getId());
        sendPOSTRequest(subTask3, "subtasks");
        subTask3.setId(4);

        HttpResponse<String> response = sendGETRequest("subtasks");

        assertEquals(200, response.statusCode());

        List<SubTask> createdSubTask = new ArrayList<>(3);

        createdSubTask.add(subTask);
        createdSubTask.add(subTask2);
        createdSubTask.add(subTask3);

        List<SubTask> subTasksFromRequest = gson.fromJson(response.body(), new SubTaskListTypeToken().getType());

        assertArrayEquals(createdSubTask.toArray(), subTasksFromRequest.toArray());

    }
}