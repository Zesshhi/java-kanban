import java.util.ArrayList;

public class Epic extends Task {

    private ArrayList<Integer> subTasksIds;

    public Epic(String name, String description, int id, ArrayList<Integer> subTasksIds) {
        super(name, description, id);
        this.subTasksIds = subTasksIds;
        this.taskStatus = getTaskStatus();
    }

    public ArrayList<Integer> getSubTasksIds() {
        return subTasksIds;
    }

    public void setSubTasksIds(ArrayList<Integer> subTasks) {
        this.subTasksIds = subTasks;
    }

    public void addSubTaskId(Integer subTaskId){
        this.subTasksIds.add(subTaskId);
    }

    public void removeSubTaskId(Integer subTaskId){
        this.subTasksIds.remove(subTaskId);
    }

    public void setTaskStatus(ArrayList<SubTask> subTasks) {
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
        return ("id=" + getId() + " name=" + getName() + " description=" + getDescription() + " taskStatus=" + taskStatus + " subTasksIds=" + getSubTasksIds());
    }
}
