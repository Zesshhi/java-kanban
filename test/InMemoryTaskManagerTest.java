import managers.HistoryManager;
import managers.InMemoryTaskManager;
import managers.Managers;
import managers.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.*;

import java.util.List;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTaskManagerTest extends TaskManagerTest {

    static TaskManager inMemoryTaskManager;

    @Override
    public Task createTask() {
        Task task = super.createTask();
        inMemoryTaskManager.createTask(task);
        return task;
    }

    @Override
    public Epic createEpic() {
        Epic epic = super.createEpic();
        inMemoryTaskManager.createEpic(epic);
        return epic;
    }

    @Override
    public SubTask createSubTask(Epic epic) {
        SubTask subTask = super.createSubTask(epic);
        inMemoryTaskManager.createSubTask(subTask);
        return subTask;
    }


    @BeforeEach
    public void beforeEach() {

        HistoryManager inMemoryHistoryManager = Managers.getDefaultHistory();
        inMemoryTaskManager = Managers.getDefault(inMemoryHistoryManager);
        currentIdOfTask = 0;

    }

    @Test
    public void should_create_and_return_task() {
        Task task = createTask();

        Task createdTask = inMemoryTaskManager.getTask(currentIdOfTask);
        assertEquals(task, createdTask, "Задача не создалась");
    }

    @Test
    public void should_create_and_return_epic() {
        Epic epic = createEpic();

        Epic createdEpic = inMemoryTaskManager.getEpic(currentIdOfTask);
        assertEquals(epic, createdEpic, "Эпик не создался");
    }

    @Test
    public void should_create_and_return_subTask() {
        Epic epic = createEpic();
        SubTask subTask = createSubTask(epic);

        SubTask createdSubTask = inMemoryTaskManager.getSubTask(currentIdOfTask);
        assertEquals(subTask, createdSubTask, "Задача не создалась");
    }

    @Test
    public void should_return_list_of_tasks() {
        Task task1 = createTask();

        Task task2 = createTask();

        Task task3 = createTask();

        List<Task> createdTasks = new ArrayList<>(3);
        createdTasks.add(task1);
        createdTasks.add(task2);
        createdTasks.add(task3);

        assertArrayEquals(createdTasks.toArray(), inMemoryTaskManager.getTasks().toArray());
    }

    @Test
    public void should_return_list_of_epics() {
        Epic epic1 = createEpic();

        Epic epic2 = createEpic();

        Epic epic3 = createEpic();

        List<Epic> createdEpics = new ArrayList<>(3);
        createdEpics.add(epic1);
        createdEpics.add(epic2);
        createdEpics.add(epic3);

        assertArrayEquals(createdEpics.toArray(), inMemoryTaskManager.getEpics().toArray());
    }

    @Test
    public void should_return_list_of_subtasks() {
        Epic mainEpic = createEpic();

        SubTask subTask1 = createSubTask(mainEpic);

        SubTask subTask2 = createSubTask(mainEpic);

        SubTask subTask3 = createSubTask(mainEpic);

        List<SubTask> createdSubTasks = new ArrayList<>(3);
        createdSubTasks.add(subTask1);
        createdSubTasks.add(subTask2);
        createdSubTasks.add(subTask3);

        assertArrayEquals(createdSubTasks.toArray(), inMemoryTaskManager.getSubTasks().toArray());
    }

    @Test
    public void should_update_task() {
        Task task = createTask();

        task.setName("Updated Task 1 Name");
        task.setDescription("Updated Task 1 description");
        task.setTaskStatus(TaskStatuses.DONE);

        inMemoryTaskManager.updateTask(task);

        assertEquals(task, inMemoryTaskManager.getTask(task.getId()));
    }

    @Test
    public void should_update_epic() {
        Epic epic = createEpic();

        SubTask subTask = createSubTask(epic);
        ArrayList<Integer> subTasks = new ArrayList<>(1);
        ArrayList<SubTask> subTasksData = new ArrayList<>(1);
        subTasks.add(subTask.getId());
        subTasksData.add(subTask);

        epic.setName("Updated Task 1 Name");
        epic.setDescription("Updated Task 1 description");
        epic.setSubTasksIds(new ArrayList<>(1));

        epic.setEndTime(subTasksData);

        inMemoryTaskManager.updateEpic(epic);

        assertEquals(epic, inMemoryTaskManager.getEpic(epic.getId()));
    }

    @Test
    public void should_update_subtasks_and_update_epic_task_status_and_return_done() {
        Epic mainEpic = createEpic();

        Epic newEpic = createEpic();

        SubTask subTask = createSubTask(mainEpic);
        SubTask subTask2 = createSubTask(mainEpic);

        subTask.setName("SubTask Updated Name 1");
        subTask.setDescription("SubTask Updated Description 1");
        subTask.setTaskStatus(TaskStatuses.DONE);
        subTask2.setTaskStatus(TaskStatuses.DONE);
        subTask.setEpicId(newEpic.getId());
        subTask2.setEpicId(newEpic.getId());

        inMemoryTaskManager.updateSubTask(subTask);

        assertEquals(subTask, inMemoryTaskManager.getSubTask(subTask.getId()));
        assertEquals(TaskStatuses.DONE, newEpic.getTaskStatus());
    }

    @Test
    public void should_update_subtasks_and_update_epic_task_status_in_progress() {
        Epic mainEpic = createEpic();

        Epic newEpic = createEpic();

        SubTask subTask = createSubTask(mainEpic);
        SubTask subTask2 = createSubTask(mainEpic);

        subTask.setName("SubTask Updated Name 1");
        subTask.setDescription("SubTask Updated Description 1");
        subTask.setTaskStatus(TaskStatuses.NEW);
        subTask2.setTaskStatus(TaskStatuses.DONE);
        subTask.setEpicId(newEpic.getId());
        subTask2.setEpicId(newEpic.getId());

        inMemoryTaskManager.updateSubTask(subTask);
        assertEquals(subTask, inMemoryTaskManager.getSubTask(subTask.getId()));
        assertEquals(TaskStatuses.NEW, newEpic.getTaskStatus());
    }

    @Test
    public void should_update_subtasks_and_update_epic_task_status_and_return_new() {
        Epic mainEpic = createEpic();

        Epic newEpic = createEpic();

        SubTask subTask = createSubTask(mainEpic);
        SubTask subTask2 = createSubTask(mainEpic);

        subTask.setName("SubTask Updated Name 1");
        subTask.setDescription("SubTask Updated Description 1");
        subTask.setTaskStatus(TaskStatuses.NEW);
        subTask2.setTaskStatus(TaskStatuses.NEW);
        subTask.setEpicId(newEpic.getId());
        subTask2.setEpicId(newEpic.getId());

        inMemoryTaskManager.updateSubTask(subTask);

        assertEquals(subTask, inMemoryTaskManager.getSubTask(subTask.getId()));
        assertEquals(TaskStatuses.NEW, newEpic.getTaskStatus());
    }

    @Test
    public void should_update_subtask_and_update_epic_task_status_and_return_in_progress() {
        Epic mainEpic = createEpic();

        Epic newEpic = createEpic();

        SubTask subTask = createSubTask(mainEpic);

        subTask.setName("SubTask Updated Name 1");
        subTask.setDescription("SubTask Updated Description 1");
        subTask.setTaskStatus(TaskStatuses.IN_PROGRESS);
        subTask.setEpicId(newEpic.getId());

        inMemoryTaskManager.updateSubTask(subTask);

        assertEquals(subTask, inMemoryTaskManager.getSubTask(subTask.getId()));
        assertEquals(TaskStatuses.IN_PROGRESS, newEpic.getTaskStatus());
    }

    @Test
    public void should_delete_task() {
        Task task = createTask();

        inMemoryTaskManager.deleteTask(task.getId());

        assertNull(inMemoryTaskManager.getTask(task.getId()));
    }

    @Test
    public void should_delete_epic() {
        Epic epic = createEpic();

        inMemoryTaskManager.deleteEpic(epic.getId());

        assertNull(inMemoryTaskManager.getEpic(epic.getId()));
    }

    @Test
    public void should_delete_subtask() {
        Epic epic = createEpic();
        SubTask subTask = createSubTask(epic);

        inMemoryTaskManager.deleteSubTask(subTask.getId());

        assertNull(inMemoryTaskManager.getSubTask(subTask.getId()));
    }


    @Test
    public void should_delete_all_tasks() {
        for (int i = 0; i < 10; i++) {
            Task task = createTask();
        }
        inMemoryTaskManager.deleteAllTasks();

        assertEquals(0, inMemoryTaskManager.getTasks().toArray().length);
    }

    @Test
    public void should_delete_all_epics() {
        for (int i = 0; i < 10; i++) {
            Epic epic = createEpic();
        }
        inMemoryTaskManager.deleteAllEpics();

        assertEquals(0, inMemoryTaskManager.getTasks().toArray().length);
    }


    @Test
    public void should_delete_all_subtasks() {
        Epic epic = createEpic();
        for (int i = 0; i < 10; i++) {
            SubTask subTask = createSubTask(epic);
        }
        inMemoryTaskManager.deleteAllSubTasks();

        assertEquals(0, inMemoryTaskManager.getTasks().toArray().length);
    }

    @Test
    public void should_not_add_overlap_task() {
        Task task = super.createTaskWithSameDate();
        Task task2 = super.createTaskWithSameDate();

        InMemoryTaskManager taskManager = (InMemoryTaskManager) inMemoryTaskManager;
        taskManager.createTask(task);
        taskManager.createTask(task2);
        System.out.println(taskManager.getPrioritizedTasks());
        assertEquals(1, taskManager.getPrioritizedTasks().size());
    }
}
