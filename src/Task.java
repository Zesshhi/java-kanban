public class Task {
    protected String name;
    protected String description;
    protected int id;
    protected TaskStatuses taskStatus;

    public Task(String name, String description, int id) {
        this.name = name;
        this.description = description;
        this.id = id;
        taskStatus = TaskStatuses.NEW;
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

    @Override
    public String toString() {
        return ("id=" + getId() + " name=" + getName() + " description=" + getDescription() + " taskStatus=" + taskStatus);
    }
}


