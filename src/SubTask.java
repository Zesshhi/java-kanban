public class SubTask extends Task {


    public SubTask(String name, String description, int id) {
        super(name, description, id);
        taskStatus = TaskStatuses.NEW;
    }

}
