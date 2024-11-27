import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TaskManager {
    private HashMap<Integer, Task> tasks = new HashMap<>(5);
    private HashMap<Integer, Epic> epics = new HashMap<>(5);
    private HashMap<Integer, SubTask> subTasks = new HashMap<>(5);


    public void createTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public void createEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    public void createSubTask(SubTask subTask) {
        subTasks.put(subTask.getId(), subTask);
    }

    public List<Task> getTasks() {
        return new ArrayList<Task>(tasks.values());
    }

    public List<Epic> getEpics() {
        return new ArrayList<Epic>(epics.values());
    }

    public List<SubTask> getSubTasks() {
        return new ArrayList<SubTask>(subTasks.values());
    }

    public void updateTask(Task task) {
        tasks.replace(task.getId(), task);
    }

    public void updateEpic(Epic epic) {
        epics.replace(epic.getId(), epic);
    }

    public void updateSubTask(SubTask subTask) {
        subTasks.replace(subTask.getId(), subTask);
        for (Epic epic : epics.values()) {
            for (SubTask subtask : epic.getSubTasks()) {
                if (subtask.getId() == subTask.getId()) {
                    subtask.setName(subTask.getName());
                    subtask.setDescription(subTask.getDescription());
                    subtask.setTaskStatus(subTask.getTaskStatus());
                    epic.setTaskStatus();
                }
            }
        }
    }


    public void deleteTask(int id) {
        tasks.remove(id);
    }


    public void deleteEpic(int id) {
        epics.remove(id);
        ArrayList<SubTask> epicSubTasks = epics.get(id).getSubTasks();
        for (SubTask subTask : epicSubTasks) {
            subTasks.remove(subTask.getId());
        }
    }

    public void deleteSubTask(int id) {
        SubTask subTask = subTasks.get(id);
        subTasks.remove(id);
        for (Epic epic : epics.values()) {
            epic.getSubTasks().remove(subTask);
        }
    }

}
