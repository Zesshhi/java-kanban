import exceptions.InvalidDataException;
import exceptions.NotFoundException;
import managers.HistoryManager;
import managers.InMemoryTaskManager;
import managers.Managers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.SubTask;
import task.Task;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryHistoryManagerTest extends TaskManagerTest {
    static InMemoryTaskManager inMemoryTaskManager;
    static HistoryManager inMemoryHistoryManager;

    @BeforeEach
    public void beforeEach() {

        inMemoryHistoryManager = Managers.getDefaultHistory();
        inMemoryTaskManager = Managers.getDefault(inMemoryHistoryManager);
        currentIdOfTask = 0;
    }

    @Override
    public Task createTaskWithInputDate(LocalDateTime inputDate) throws InvalidDataException {
        Task task = super.createTaskWithInputDate(inputDate);
        inMemoryTaskManager.createTask(task);
        return task;
    }

    @Override
    public Epic createEpicWithInputDate(LocalDateTime inputDate) {
        Epic epic = super.createEpicWithInputDate(inputDate);
        inMemoryTaskManager.createEpic(epic);
        return epic;
    }

    @Override
    public SubTask createSubTaskWithInputDate(Epic epic, LocalDateTime inputDate) throws InvalidDataException {
        SubTask subTask = super.createSubTaskWithInputDate(epic, inputDate);
        inMemoryTaskManager.createSubTask(subTask);
        return subTask;
    }


    @Override
    public Task createTask() throws InvalidDataException {
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
    public SubTask createSubTask(Epic epic) throws InvalidDataException {
        SubTask subTask = super.createSubTask(epic);
        inMemoryTaskManager.createSubTask(subTask);
        return subTask;
    }


    @Test
    public void should_add_task_to_history() {
        try {
            Task task = createTask();

            inMemoryTaskManager.getTask(task.getId());

            assertEquals(inMemoryHistoryManager.getHistory().getLast(), task, "Задача не попала в историю");
        } catch (InvalidDataException ignore) {
            fail("Неправильные данные");
        } catch (NotFoundException ignore) {
            fail("Задача не найдена");
        }
    }

    @Test
    public void should_add_epic_to_history() {
        try {
            Epic epic = createEpic();

            inMemoryTaskManager.getEpic(epic.getId());

            assertEquals(inMemoryHistoryManager.getHistory().getLast(), epic, "Задача не попала в историю");
        } catch (NotFoundException ignore) {
            fail("Задача не найдена");
        }
    }

    @Test
    public void should_add_subtask_to_history() {
        try {
            Epic epic = createEpic();
            SubTask subTask = createSubTask(epic);

            inMemoryTaskManager.getSubTask(subTask.getId());

            assertEquals(inMemoryHistoryManager.getHistory().getLast(), subTask, "Задача не попала в историю");
        } catch (InvalidDataException ignore) {
            fail("Неправильные данные");
        } catch (NotFoundException ignore) {
            fail("Задача не найдена");
        }

    }

    @Test
    public void should_add_task_to_last_and_delete_if_exists() {
        try {
            Task task = createTask();
            Epic epic = createEpic();

            List<Task> watchedHistory = new ArrayList<>();

            inMemoryTaskManager.getTask(task.getId());

            inMemoryTaskManager.getEpic(epic.getId());
            watchedHistory.add(epic);

            inMemoryTaskManager.getTask(task.getId());
            watchedHistory.add(task);

            assertArrayEquals(watchedHistory.toArray(), inMemoryHistoryManager.getHistory().toArray());
        } catch (InvalidDataException ignore) {
            fail("Неправильные данные");
        } catch (NotFoundException ignore) {
            fail("Задача не найдена");
        }
    }


    @Test
    public void should_save_only_unique_tasks_to_history() {
        try {
            Task task = createTask();
            Epic epic = createEpic();
            SubTask subTask = createSubTaskWithInputDate(epic, LocalDateTime.now().minusDays(1));
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
        } catch (InvalidDataException ignore) {
            fail("Неправильные данные");
        } catch (NotFoundException ignore) {
            fail("Задача не найдена");
        }
    }

    @Test
    public void should_remove_task_from_history() {
        try {
            Task task = createTask();

            inMemoryTaskManager.getTask(task.getId());
            assertEquals(inMemoryHistoryManager.getHistory().getLast(), task, "Задача не попала в историю");

            inMemoryHistoryManager.remove(task.getId());

            assertArrayEquals(new ArrayList<>().toArray(), inMemoryHistoryManager.getHistory().toArray());
        } catch (InvalidDataException ignore) {
            fail("Неправильные данные");
        } catch (NotFoundException ignore) {
            fail("Задача не найдена");
        }

    }

    @Test
    public void should_remove_from_beginning() {
        try {
            List<Task> watchedHistory = new ArrayList<>();

            Task task = createTask();
            inMemoryTaskManager.getTask(task.getId());

            Epic epic = createEpic();
            inMemoryTaskManager.getEpic(epic.getId());
            watchedHistory.add(epic);

            inMemoryHistoryManager.remove(task.getId());

            assertArrayEquals(watchedHistory.toArray(), inMemoryHistoryManager.getHistory().toArray());
        } catch (InvalidDataException ignore) {
            fail("Неправильные данные");
        } catch (NotFoundException ignore) {
            fail("Задача не найдена");
        }
    }

    @Test
    public void should_remove_from_middle() {
        try {
            List<Task> watchedHistory = new ArrayList<>();

            Task task = createTaskWithInputDate(LocalDateTime.now().minusDays(1));
            inMemoryTaskManager.getTask(task.getId());
            watchedHistory.add(task);

            Epic epic = createEpic();
            inMemoryTaskManager.getEpic(epic.getId());

            Task task2 = createTaskWithInputDate(LocalDateTime.now().minusDays(2));
            inMemoryTaskManager.getTask(task2.getId());
            watchedHistory.add(task2);

            inMemoryHistoryManager.remove(epic.getId());

            assertArrayEquals(watchedHistory.toArray(), inMemoryHistoryManager.getHistory().toArray());
        } catch (InvalidDataException ignore) {
            fail("Неправильные данные");
        } catch (NotFoundException ignore) {
            fail("Задача не найдена");
        }
    }

    @Test
    public void should_remove_from_end() {
        try {
            List<Task> watchedHistory = new ArrayList<>();

            Task task = createTaskWithInputDate(LocalDateTime.now().minusDays(1));
            inMemoryTaskManager.getTask(task.getId());
            watchedHistory.add(task);

            Epic epic = createEpic();
            inMemoryTaskManager.getEpic(epic.getId());
            watchedHistory.add(epic);

            Task task2 = createTaskWithInputDate(LocalDateTime.now().minusDays(2));
            inMemoryTaskManager.getTask(task2.getId());

            inMemoryHistoryManager.remove(task2.getId());

            assertArrayEquals(watchedHistory.toArray(), inMemoryHistoryManager.getHistory().toArray());
        } catch (InvalidDataException ignore) {
            fail("Неправильные данные");
        } catch (NotFoundException ignore) {
            fail("Задача не найдена");
        }
    }
}
