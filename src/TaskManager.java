import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

public class TaskManager {
    private Map<Integer, Task> tasks = new HashMap<>(5);
    private Map<Integer, Epic> epics = new HashMap<>(5);
    private Map<Integer, SubTask> subTasks = new HashMap<>(5);


    public void createTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public void createEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    public void createSubTask(SubTask subTask) {
        Integer subTaskId = subTask.getId();
        Integer epicId = subTask.getEpicId();

        subTasks.put(subTaskId, subTask);

        Epic epic = epics.get(epicId);

        epic.addSubTaskId(subTaskId);
        epic.setTaskStatus(getEpicSubTasks(epicId));
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
        Epic updatedEpic = epics.get(epic.getId());
        updatedEpic.setName(epic.getName());
        updatedEpic.setDescription(epic.getDescription());

        epics.replace(epic.getId(), updatedEpic);
    }

    public void updateSubTask(SubTask subTask) {
        SubTask updatedSubTask = subTasks.get(subTask.getId());
        updatedSubTask.setName(subTask.getName());
        updatedSubTask.setDescription(subTask.getDescription());
        updatedSubTask.setTaskStatus(subTask.getTaskStatus());

        subTasks.replace(subTask.getId(), subTask);

        Epic epic = epics.get(subTask.getEpicId());

        epic.addSubTaskId(subTask.getId());
        epic.setTaskStatus(getEpicSubTasks(subTask.getEpicId()));
    }


    public void deleteTask(int id) {
        tasks.remove(id);
    }


    public void deleteEpic(int id) {
        ArrayList<SubTask> epicSubTasks = getEpicSubTasks(id);
        for (SubTask subTask : epicSubTasks) {
            subTasks.remove(subTask.getId());
        }

        epics.remove(id);
    }

    public void deleteSubTask(int id) {
        SubTask subTask = subTasks.get(id);

        Epic epic = epics.get(subTask.getEpicId());

        epic.removeSubTaskId(id);
        epic.setTaskStatus(getEpicSubTasks(subTask.getEpicId()));

        subTasks.remove(id);
    }

    public ArrayList<SubTask> getEpicSubTasks(int epicId) {
        // Получение всех подзадач Эпика
        ArrayList<SubTask> epicSubTasks = new ArrayList<>(5);
        for (SubTask subTask : subTasks.values()) {
            if (subTask.getEpicId() == epicId) {
                epicSubTasks.add(subTask);
            }

        }
        return epicSubTasks;
    }


}
