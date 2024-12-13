import managers.Managers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.*;

import java.util.List;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class TaskManagerTest {

    static TaskManager inMemoryTaskManager;
    static int currentIdOfTask;


    public Task createTask() {
        currentIdOfTask++;
        Task task = new Task("Task 1", "Task description 1", currentIdOfTask);
        inMemoryTaskManager.createTask(task);
        return task;
    }

    public Epic createEpic() {
        currentIdOfTask++;
        Epic epic = new Epic("Epic 1", "Epic description 1", currentIdOfTask, new ArrayList<>(0));
        inMemoryTaskManager.createEpic(epic);
        return epic;
    }

    public SubTask createSubTask(Epic epic) {
        currentIdOfTask++;
        SubTask subTask = new SubTask("SubTask 1", "SubTask description 1", currentIdOfTask, epic.getId());
        inMemoryTaskManager.createSubTask(subTask);

        return subTask;
    }


    @BeforeEach
    public void beforeEach() {
        Managers managers = new Managers();

        inMemoryTaskManager = managers.getDefault();
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
        subTasks.add(subTask.getId());

        epic.setName("Updated Task 1 Name");
        epic.setDescription("Updated Task 1 description");
        epic.setSubTasksIds(subTasks);

        inMemoryTaskManager.updateEpic(epic);

        assertEquals(epic, inMemoryTaskManager.getEpic(epic.getId()));
    }

    @Test
    public void should_update_subtask_and_update_epic_task_status() {
        Epic mainEpic = createEpic();

        Epic newEpic = createEpic();

        SubTask subTask = createSubTask(mainEpic);

        subTask.setName("SubTask Updated Name 1");
        subTask.setDescription("SubTask Updated Description 1");
        subTask.setTaskStatus(TaskStatuses.DONE);
        subTask.setEpicId(newEpic.getId());

        inMemoryTaskManager.updateSubTask(subTask);

        assertEquals(subTask, inMemoryTaskManager.getSubTask(subTask.getId()));
        assertEquals(TaskStatuses.DONE, newEpic.getTaskStatus());
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
}
