public class SubTask extends Task {
    private Epic epic;

    public SubTask(String name, String description, int id, Epic epic) {
        super(name, description, id);
        this.epic = epic;
        taskStatus = TaskStatuses.NEW;
    }

    public Epic getEpic() {
        return epic;
    }

    public void setEpic(Epic epic) {
        this.epic = epic;
    }

    @Override
    public String toString() {
        return ("id=" + getId() + " name=" + getName() + " description=" + getDescription() + " taskStatus=" + taskStatus + " epicId=" + getEpic().getId());
    }
}
