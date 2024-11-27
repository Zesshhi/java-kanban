import java.util.ArrayList;

public class Epic extends Task {

    private ArrayList<SubTask> subTasks;

    public Epic(String name, String description, int id, ArrayList<SubTask> subTasks) {
        super(name, description, id);
        this.subTasks = subTasks;
        this.taskStatus = getTaskStatus();
    }

    public ArrayList<SubTask> getSubTasks() {
        return subTasks;
    }

    public void setSubTasks(ArrayList<SubTask> subTasks) {
        this.subTasks = subTasks;
    }

    public void setTaskStatus() {
        ArrayList<TaskStatuses> subTaskStatuses = new ArrayList<>();
        for (SubTask subtask : subTasks) {
            subTaskStatuses.add(subtask.taskStatus);
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
        return ("id=" + getId() + " name=" + getName() + " description=" + getDescription() + " taskStatus=" + taskStatus + " subtasks=" + subTasks.toString());
    }
}
