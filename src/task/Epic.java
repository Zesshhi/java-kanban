package task;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {

    private List<Integer> subTasksIds;

    public Epic(String name, String description, int id, List<Integer> subTasksIds) {
        super(name, description, id);
        this.subTasksIds = subTasksIds;
        this.taskStatus = getTaskStatus();
    }

    public Epic(int id, String name, TaskStatuses taskStatus, String description, List<Integer> subTasksIds) {
        super(id, name, taskStatus, description);
        this.subTasksIds = subTasksIds;
    }

    public List<Integer> getSubTasksIds() {
        return subTasksIds;
    }

    public void setSubTasksIds(List<Integer> subTasks) {
        this.subTasksIds = subTasks;
    }

    public void addSubTaskId(Integer subTaskId) {
        this.subTasksIds.add(subTaskId);
    }

    public void removeSubTaskId(Integer subTaskId) {
        this.subTasksIds.remove(subTaskId);
    }

    public void setTaskStatus(List<SubTask> subTasks) {
        ArrayList<TaskStatuses> subTaskStatuses = new ArrayList<>();
        for (SubTask subTask : subTasks) {
            subTaskStatuses.add(subTask.taskStatus);
        }
        if (subTaskStatuses.stream().allMatch(element -> element.equals(TaskStatuses.NEW))) {
            System.out.println("Все подзадачи равны NEW");
            this.taskStatus = TaskStatuses.NEW;
        } else if ((subTaskStatuses.stream().allMatch(element -> element.equals(TaskStatuses.DONE)))) {
            System.out.println("Все подзадачи равны DONE");
            this.taskStatus = TaskStatuses.DONE;
        } else {
            this.taskStatus = TaskStatuses.IN_PROGRESS;
        }
    }

    @Override
    public String toString() {
        return String.format("%s,%s,%s,%s,%s", getId(), getClass().getSimpleName(), getName(), taskStatus, getDescription());
    }

    public static Epic fromString(String[] taskData) {
        return new Epic(Integer.parseInt(taskData[0]), taskData[2], TaskStatuses.valueOf(taskData[3]), taskData[4], new ArrayList<>(0));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Epic epic = (Epic) o;
        if (id == epic.id) return true;
        return Objects.equals(name, epic.name) && taskStatus == epic.taskStatus && Objects.equals(description, epic.description) && subTasksIds == epic.subTasksIds;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, taskStatus, description, subTasksIds);
    }
}
