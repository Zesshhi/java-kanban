package task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class SubTask extends Task {
    private Integer epicId;

    public SubTask(String name, String description, int id, Integer epicId, Duration duration, LocalDateTime startTime) {
        super(name, description, id, duration, startTime);
        this.epicId = epicId;
        taskStatus = TaskStatuses.NEW;
    }

    public SubTask(int id, String name, TaskStatuses taskStatus, String description, int epicId, Duration duration, LocalDateTime startTime) {
        super(id, name, taskStatus, description, duration, startTime);
        this.epicId = epicId;
    }

    public SubTask(int id, String name, TaskStatuses taskStatus, String description, int epicId) {
        super(id, name, taskStatus, description);
        this.epicId = epicId;
    }


    public Integer getEpicId() {
        return epicId;
    }

    public void setEpicId(Integer epicId) {
        this.epicId = epicId;
    }


    @Override
    public String toString() {
        return String.format("%s,%s,%s,%s,%s,%s,%s,%s", getId(), getClass().getSimpleName(), getName(), taskStatus, getDescription(), getEpicId(), getDuration(), getStartTime());
    }

    public static SubTask fromString(String[] taskData) {
        return new SubTask(
                Integer.parseInt(taskData[0]),
                taskData[2],
                TaskStatuses.valueOf(taskData[3]),
                taskData[4],
                Integer.parseInt(taskData[5]),
                Duration.parse(taskData[6]),
                LocalDateTime.parse(taskData[7])
        );
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubTask subTask = (SubTask) o;
        if (id == subTask.id) return true;
        return Objects.equals(id, subTask.id) && Objects.equals(name, subTask.name) && taskStatus == subTask.taskStatus && Objects.equals(description, subTask.description) && epicId == subTask.epicId && duration.equals(subTask.duration) && startTime.equals(subTask.startTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, taskStatus, description, epicId);
    }
}
