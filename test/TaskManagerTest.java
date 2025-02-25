import managers.TaskManager;
import task.Epic;
import task.SubTask;
import task.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public abstract class TaskManagerTest<T extends TaskManager> {
    LocalDateTime currentDate = LocalDateTime.now();
    static int currentIdOfTask;

    public Task createTask() {
        currentIdOfTask++;
        Task task = new Task("Task 1", "Task description 1", currentIdOfTask, Duration.ofSeconds(10), LocalDateTime.now());
        return task;
    }

    public Epic createEpic() {
        currentIdOfTask++;
        Epic epic = new Epic("Epic 1", "Epic description 1", currentIdOfTask, new ArrayList<>(0), Duration.ofSeconds(10), LocalDateTime.now());
        return epic;
    }

    public SubTask createSubTask(Epic epic) {
        currentIdOfTask++;
        SubTask subTask = new SubTask("SubTask 1", "SubTask description 1", currentIdOfTask, epic.getId(), Duration.ofSeconds(10), LocalDateTime.now());
        return subTask;
    }

    public Task createTaskWithSameDate() {
        currentIdOfTask++;
        Task task = new Task("Task 1", "Task description 1", currentIdOfTask, Duration.ofSeconds(10), currentDate);
        return task;
    }

    public Task createEpicWithSameDate() {
        currentIdOfTask++;
        Epic epic = new Epic("Epic 1", "Epic description 1", currentIdOfTask, new ArrayList<>(0), Duration.ofSeconds(10), currentDate);
        return epic;
    }

    public Task createSubTaskWithSameDate(Epic epic) {
        currentIdOfTask++;
        SubTask subTask = new SubTask("SubTask 1", "SubTask description 1", currentIdOfTask, epic.getId(), Duration.ofSeconds(10), currentDate);
        return subTask;
    }


}
