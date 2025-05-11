package HttpTests;

import com.google.gson.Gson;
import exceptions.NotFoundException;
import http.deserializers.EpicListTypeToken;
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

public class HttpEpicManagerTest extends HttpManagerTest {

    public HttpEpicManagerTest() {
    }

    public Epic createEpic(LocalDateTime localDateTime) {
        return new Epic("Epic 1", "Epic description 1", new ArrayList<>(0), TaskStatuses.NEW, Duration.ofSeconds(60), localDateTime);
    }


    public SubTask createSubTask(LocalDateTime localDateTime, int epicId) {
        return new SubTask("SubTask 1", "SubTask description 1", epicId, TaskStatuses.NEW, Duration.ofSeconds(60), localDateTime);
    }

    @Test
    public void should_create_epic() throws IOException, InterruptedException {
        Epic epic = createEpic(LocalDateTime.now().minusDays(5).withNano(0));
        HttpResponse<String> response = sendPOSTRequest(epic, "epics");

        assertEquals(201, response.statusCode());

        epic.setId(1);

        Epic createdEpic = taskManager.getEpic(epic.getId());
        assertEquals(epic, createdEpic, "Эпик не создался");

    }

    @Test
    public void should_update_epic() throws IOException, InterruptedException {
        Epic epic = createEpic(LocalDateTime.now().minusDays(5).withNano(0));
        sendPOSTRequest(epic, "epics");
        epic.setId(1);

        SubTask subTask = createSubTask(LocalDateTime.now().minusDays(6).withNano(0), epic.getId());
        sendPOSTRequest(subTask, "subtasks");
        ArrayList<Integer> subTasks = new ArrayList<>(1);
        ArrayList<SubTask> subTasksData = new ArrayList<>(1);
        subTasks.add(subTask.getId());
        subTasksData.add(subTask);


        epic.setName("Updated Epic Name");
        epic.setDescription("Updated Epic 1 description");
        epic.setSubTasksIds(subTasks);
        epic.setEndTime(subTasksData);

        sendPOSTRequest(epic, "epics");

        Epic updatedEpic = taskManager.getEpic(epic.getId());
        assertEquals(epic, updatedEpic, "Эпик не создался");
    }

    @Test
    public void should_delete_epic() throws IOException, InterruptedException {
        Epic epic = createEpic(LocalDateTime.now().minusDays(5).withNano(0));
        sendPOSTRequest(epic, "tasks");
        epic.setId(1);

        HttpResponse<String> response = sendDELETERequest("epics/" + epic.getId());

        assertEquals(200, response.statusCode());
        try {
            assertNull(taskManager.getEpic(epic.getId()));
        } catch (NotFoundException ignore) {
        }
    }

    @Test
    public void should_return_epic() throws IOException, InterruptedException {
        Gson gson = httpTaskServer.getGson();

        Epic epic = createEpic(LocalDateTime.now().minusDays(5).withNano(0));
        sendPOSTRequest(epic, "epics");
        epic.setId(1);

        HttpResponse<String> response = sendGETRequest("epics/" + epic.getId());
        assertEquals(200, response.statusCode());


        Epic createdEpic = gson.fromJson(response.body(), Epic.class);

        assertEquals(epic, createdEpic, "Эпик не создался");
    }


    @Test
    public void should_return_epics() throws IOException, InterruptedException {
        Gson gson = httpTaskServer.getGson();

        Epic epic = createEpic(LocalDateTime.now().minusDays(5).withNano(0));
        sendPOSTRequest(epic, "epics");
        epic.setId(1);

        Epic epic2 = createEpic(LocalDateTime.now().minusDays(6).withNano(0));
        sendPOSTRequest(epic2, "epics");
        epic2.setId(2);

        Epic epic3 = createEpic(LocalDateTime.now().minusDays(7).withNano(0));
        sendPOSTRequest(epic3, "epics");
        epic3.setId(3);

        HttpResponse<String> response = sendGETRequest("epics");

        assertEquals(200, response.statusCode());

        List<Epic> createdEpics = new ArrayList<>(3);

        createdEpics.add(epic);
        createdEpics.add(epic2);
        createdEpics.add(epic3);

        List<Epic> epicsFromRequest = gson.fromJson(response.body(), new EpicListTypeToken().getType());

        assertArrayEquals(createdEpics.toArray(), epicsFromRequest.toArray());
    }

    @Test
    public void should_return_epic_subtasks() throws IOException, InterruptedException {
        Gson gson = httpTaskServer.getGson();

        Epic epic = createEpic(LocalDateTime.now().minusDays(5).withNano(0));
        sendPOSTRequest(epic, "epics");
        epic.setId(1);

        SubTask subTask = createSubTask(LocalDateTime.now().minusDays(6).withNano(0), epic.getId());
        sendPOSTRequest(subTask, "subtasks");
        subTask.setId(2);
        ArrayList<Integer> subTasks = new ArrayList<>(1);
        ArrayList<SubTask> subTasksData = new ArrayList<>(1);
        subTasks.add(subTask.getId());
        subTasksData.add(subTask);

        epic.setSubTasksIds(subTasks);
        epic.setEndTime(subTasksData);

        sendPOSTRequest(epic, "epics");

        HttpResponse<String> response = sendGETRequest("epics/" + epic.getId() + "/subtasks");

        assertEquals(200, response.statusCode());

        List<SubTask> subTasksFromRequest = gson.fromJson(response.body(), new SubTaskListTypeToken().getType());

        assertArrayEquals(subTasksData.toArray(), subTasksFromRequest.toArray());
    }
}