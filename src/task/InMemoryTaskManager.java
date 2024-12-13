package task;

import history.HistoryManager;
import history.InMemoryHistoryManager;
import managers.Managers;

import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private final Map<Integer, Task> tasks;
    private final Map<Integer, Epic> epics;
    private final Map<Integer, SubTask> subTasks;
    private final HistoryManager historyManager;

    public InMemoryTaskManager(HistoryManager historyManager) {
        tasks = new HashMap<>(5);
        epics = new HashMap<>(5);
        subTasks = new HashMap<>(5);
        this.historyManager = historyManager;
    }

    @Override
    public void createTask(Task task) {
        tasks.put(task.getId(), task);
    }

    @Override
    public void createEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    @Override
    public void createSubTask(SubTask subTask) {
        Integer subTaskId = subTask.getId();
        Integer epicId = subTask.getEpicId();

        subTasks.put(subTaskId, subTask);

        Epic epic = epics.get(epicId);

        epic.addSubTaskId(subTaskId);
        epic.setTaskStatus(getEpicSubTasks(epicId));
    }

    @Override
    public Task getTask(int id) {
        Task task = tasks.get(id);
        historyManager.add(task);
        return task;
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = epics.get(id);
        historyManager.add(epic);
        return epic;
    }

    @Override
    public SubTask getSubTask(int id) {
        SubTask subTask = subTasks.get(id);
        historyManager.add(subTask);
        return subTask;
    }


    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<SubTask> getSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    /// Тесты пока дошли до сюдова

    @Override
    public void updateTask(Task task) {
        tasks.replace(task.getId(), task);
    }

    @Override
    public void updateEpic(Epic epic) {
        Epic updatedEpic = epics.get(epic.getId());
        updatedEpic.setName(epic.getName());
        updatedEpic.setDescription(epic.getDescription());

        epics.replace(epic.getId(), epic);
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        SubTask updatedSubTask = subTasks.get(subTask.getId());
        updatedSubTask.setName(subTask.getName());
        updatedSubTask.setDescription(subTask.getDescription());
        updatedSubTask.setTaskStatus(subTask.getTaskStatus());
        updatedSubTask.setEpicId(subTask.getEpicId());

        Epic epic = epics.get(updatedSubTask.getEpicId());

        if (!epic.getSubTasksIds().contains(updatedSubTask.getId())) {
            ArrayList<Integer> newSubTasks = epic.getSubTasksIds();
            newSubTasks.add(updatedSubTask.getId());
            epic.setSubTasksIds(newSubTasks);
        }

        epic.setTaskStatus(getEpicSubTasks(updatedSubTask.getEpicId()));
    }

    @Override
    public void deleteTask(int id) {
        tasks.remove(id);
    }

    @Override
    public void deleteEpic(int id) {
        ArrayList<SubTask> epicSubTasks = getEpicSubTasks(id);
        for (SubTask subTask : epicSubTasks) {
            subTasks.remove(subTask.getId());
        }

        epics.remove(id);
    }

    @Override
    public void deleteSubTask(int id) {
        SubTask subTask = subTasks.get(id);

        Epic epic = epics.get(subTask.getEpicId());

        epic.removeSubTaskId(id);
        epic.setTaskStatus(getEpicSubTasks(subTask.getEpicId()));

        subTasks.remove(id);
    }

    public ArrayList<SubTask> getEpicSubTasks(int epicId) {
        // Получение всех подзадач Эпика
        ArrayList<SubTask> epicSubTasks = new ArrayList<>(1);
        Epic epic = epics.get(epicId);

        for (Integer subTaskId : epic.getSubTasksIds()) {
            epicSubTasks.add(subTasks.get(subTaskId));

        }
        return epicSubTasks;
    }

}
