package task;

import java.util.Objects;

public class SubTask extends Task {
    private Integer epicId;

    public SubTask(String name, String description, int id, Integer epicId) {
        super(name, description, id);
        this.epicId = epicId;
        taskStatus = TaskStatuses.NEW;
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
        return String.format("%s,%s,%s,%s,%s,%s", getId(), getClass().getSimpleName(), getName(), taskStatus, getDescription(), getEpicId());
    }

    public static SubTask fromString(String[] taskData) {
        return new SubTask(Integer.parseInt(taskData[0]), taskData[2], TaskStatuses.valueOf(taskData[3]), taskData[4], Integer.parseInt(taskData[5]));
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubTask subTask = (SubTask) o;
        if (id == subTask.id) return true;
        return Objects.equals(name, subTask.name) && taskStatus == subTask.taskStatus && Objects.equals(description, subTask.description) && epicId == subTask.epicId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, taskStatus, description, epicId);
    }
}
