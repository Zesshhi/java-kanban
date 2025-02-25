package task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {
    protected String name;
    protected String description;
    protected int id;
    protected TaskStatuses taskStatus;
    protected Duration duration;
    protected LocalDateTime startTime;

    public Task(String name, String description, int id, Duration duration, LocalDateTime startTime) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.duration = duration;
        this.startTime = startTime;
        taskStatus = TaskStatuses.NEW;
    }

    public Task(int id, String name, TaskStatuses taskStatus, String description, Duration duration, LocalDateTime startTime) {
        this.id = id;
        this.name = name;
        this.taskStatus = taskStatus;
        this.description = description;
        this.duration = duration;
        this.startTime = startTime;
    }

    public Task(int id, String name, TaskStatuses taskStatus, String description) {
        this.id = id;
        this.name = name;
        this.taskStatus = taskStatus;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TaskStatuses getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(TaskStatuses taskStatus) {
        this.taskStatus = taskStatus;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return startTime.plus(duration);
    }

    @Override
    public String toString() {
        return String.format("%s,%s,%s,%s,%s,%s,%s", getId(), getClass().getSimpleName(), getName(), getTaskStatus(), getDescription(), getDuration(), getStartTime());
    }

    public static Task fromString(String[] taskData) {
        return new Task(
                Integer.parseInt(taskData[0]),
                taskData[2],
                TaskStatuses.valueOf(taskData[3]),
                taskData[4],
                Duration.parse(taskData[5]),
                LocalDateTime.parse(taskData[6])
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        if (id == task.id) return true;
        return Objects.equals(name, task.name) && taskStatus == task.taskStatus && Objects.equals(description, task.description) && duration.equals(task.duration) && startTime.equals(task.startTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, taskStatus, id);
    }
}


