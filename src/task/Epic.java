package task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {

    private List<Integer> subTasksIds;
    private LocalDateTime endTime;

    public Epic(String name, String description, int id, List<Integer> subTasksIds, Duration duration, LocalDateTime startTime) {
        super(name, description, id, duration, startTime);
        this.subTasksIds = subTasksIds;
        this.taskStatus = getTaskStatus();
        setEndTime(new ArrayList<>());
    }

    public Epic(int id, String name, TaskStatuses taskStatus, String description, List<Integer> subTasksIds, Duration duration, LocalDateTime startTime) {
        super(id, name, taskStatus, description, duration, startTime);
        this.subTasksIds = subTasksIds;
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
        List<TaskStatuses> subTaskStatuses = subTasks.stream().map(subTask -> subTask.taskStatus).toList();

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
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(List<SubTask> subTasks) {
        if (subTasks.size() == 0)
            this.endTime = LocalDateTime.now();
        else {
            for (SubTask subTask : subTasks) {
                LocalDateTime subTaskEndTime = subTask.getEndTime();
                if (endTime == null || endTime.isBefore(subTaskEndTime)) {
                    endTime = subTaskEndTime;
                }
            }

        }
    }

    @Override
    public String toString() {
        return String.format("%s,%s,%s,%s,%s,%s,%s", getId(), getClass().getSimpleName(), getName(), taskStatus, getDescription(), getDuration(), getStartTime());
    }

    public static Epic fromString(String[] taskData) {
        return new Epic(
                Integer.parseInt(taskData[0]),
                taskData[2],
                TaskStatuses.valueOf(taskData[3]),
                taskData[4],
                new ArrayList<>(0),
                Duration.parse(taskData[5]),
                LocalDateTime.parse(taskData[6])
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Epic epic = (Epic) o;
        if (id == epic.id) return true;
        return Objects.equals(name, epic.name) && taskStatus == epic.taskStatus && Objects.equals(description, epic.description) && subTasksIds == epic.subTasksIds && duration.equals(epic.duration) && startTime.equals(epic.startTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, taskStatus, description, subTasksIds);
    }
}
