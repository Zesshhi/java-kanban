import managers.HistoryManager;
import managers.InMemoryHistoryManager;
import managers.Managers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.SubTask;
import task.Task;
import managers.TaskManager;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HistoryManagerTest {
    static TaskManager inMemoryTaskManager;
    static HistoryManager inMemoryHistoryManager;
    static int currentIdOfTask;

    @BeforeEach
    public void beforeEach() {

        inMemoryHistoryManager = Managers.getDefaultHistory();
        inMemoryTaskManager = Managers.getDefault(inMemoryHistoryManager);
        currentIdOfTask = 0;
    }

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

    @Test
    public void should_add_task_to_history() {
        Task task = createTask();

        inMemoryTaskManager.getTask(task.getId());

        assertEquals(inMemoryHistoryManager.getHistory().getLast(), task, "Задача не попала в историю");
    }

    @Test
    public void should_add_epic_to_history() {
        Epic epic = createEpic();

        inMemoryTaskManager.getEpic(epic.getId());

        assertEquals(inMemoryHistoryManager.getHistory().getLast(), epic, "Задача не попала в историю");
    }

    @Test
    public void should_add_subtask_to_history() {
        Epic epic = createEpic();
        SubTask subTask = createSubTask(epic);

        inMemoryTaskManager.getSubTask(subTask.getId());

        assertEquals(inMemoryHistoryManager.getHistory().getLast(), subTask, "Задача не попала в историю");

    }

    @Test
    public void should_add_task_to_last_and_delete_if_exists() {
        Task task = createTask();
        Epic epic = createEpic();

        List<Task> watchedHistory = new ArrayList<>();

        inMemoryTaskManager.getTask(task.getId());

        inMemoryTaskManager.getEpic(epic.getId());
        watchedHistory.add(epic);

        inMemoryTaskManager.getTask(task.getId());
        watchedHistory.add(task);

        assertArrayEquals(watchedHistory.toArray(), inMemoryHistoryManager.getHistory().toArray());
    }


    @Test
    public void should_save_only_unique_tasks_to_history() {
        Task task = createTask();
        Epic epic = createEpic();
        SubTask subTask = createSubTask(epic);
        ArrayList<Task> watchedHistory = new ArrayList<>(10);

        for (int i = 0; i < 3; i++) {
            inMemoryTaskManager.getTask(task.getId());
            inMemoryTaskManager.getEpic(epic.getId());
            inMemoryTaskManager.getSubTask(subTask.getId());
        }

        watchedHistory.add(task);
        watchedHistory.add(epic);
        watchedHistory.add(subTask);

        assertArrayEquals(watchedHistory.toArray(), inMemoryHistoryManager.getHistory().toArray());
    }

    @Test
    public void should_remove_task_from_history() {
        Task task = createTask();

        inMemoryTaskManager.getTask(task.getId());
        assertEquals(inMemoryHistoryManager.getHistory().getLast(), task, "Задача не попала в историю");

        inMemoryHistoryManager.remove(task.getId());

        assertArrayEquals(new ArrayList<>().toArray(), inMemoryHistoryManager.getHistory().toArray());
    }

    @Test
    public void should_remove_from_beginning() {
        List<Task> watchedHistory = new ArrayList<>();

        Task task = createTask();
        inMemoryTaskManager.getTask(task.getId());

        Epic epic = createEpic();
        inMemoryTaskManager.getEpic(epic.getId());
        watchedHistory.add(epic);

        inMemoryHistoryManager.remove(task.getId());

        assertArrayEquals(watchedHistory.toArray(), inMemoryHistoryManager.getHistory().toArray());

    }

    @Test
    public void should_remove_from_middle() {
        List<Task> watchedHistory = new ArrayList<>();

        Task task = createTask();
        inMemoryTaskManager.getTask(task.getId());
        watchedHistory.add(task);

        Epic epic = createEpic();
        inMemoryTaskManager.getEpic(epic.getId());

        Task task2 = createTask();
        inMemoryTaskManager.getTask(task2.getId());
        watchedHistory.add(task2);

        inMemoryHistoryManager.remove(epic.getId());

        assertArrayEquals(watchedHistory.toArray(), inMemoryHistoryManager.getHistory().toArray());
    }

    @Test
    public void should_remove_from_end() {
        List<Task> watchedHistory = new ArrayList<>();

        Task task = createTask();
        inMemoryTaskManager.getTask(task.getId());
        watchedHistory.add(task);

        Epic epic = createEpic();
        inMemoryTaskManager.getEpic(epic.getId());
        watchedHistory.add(epic);

        Task task2 = createTask();
        inMemoryTaskManager.getTask(task2.getId());

        inMemoryHistoryManager.remove(task2.getId());

        assertArrayEquals(watchedHistory.toArray(), inMemoryHistoryManager.getHistory().toArray());
    }
}
