import history.HistoryManager;
import managers.Managers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.SubTask;
import task.Task;
import task.TaskManager;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HistoryManagerTest {
    static TaskManager inMemoryTaskManager;
    static HistoryManager inMemoryHistoryManager;
    static int currentIdOfTask;

    @BeforeEach
    public void beforeEach() {
        Managers managers = new Managers();

        inMemoryTaskManager = managers.getDefault();
        inMemoryHistoryManager = managers.getDefaultHistory();
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
    public void should_change_first_value_if_full() {
        Task task = createTask();
        Epic epic = createEpic();

        for (int i = 0; i < 10; i++) {
            inMemoryTaskManager.getTask(task.getId());
        }
        inMemoryTaskManager.getEpic(epic.getId());


        assertEquals(inMemoryHistoryManager.getHistory().getFirst(), epic, "Задача не попала в историю");
    }


}
