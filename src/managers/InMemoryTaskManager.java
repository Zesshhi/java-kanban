package managers;

import task.Epic;
import task.SubTask;
import task.Task;

import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    private final Map<Integer, Task> tasks;
    private final Map<Integer, Epic> epics;
    private final Map<Integer, SubTask> subTasks;
    private final HistoryManager historyManager;
    private final TreeSet<Task> sortedTasks;

    public InMemoryTaskManager(HistoryManager historyManager) {
        tasks = new HashMap<>(5);
        epics = new HashMap<>(5);
        subTasks = new HashMap<>(5);
        this.historyManager = historyManager;
        sortedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));
    }


    public void addTaskToTreeSet(Task task) {
        if (task.getStartTime() != null && !isTimeOverlap(task))
            sortedTasks.add(task);
    }

    public void updateTaskInTreeSet(Task task) {
        sortedTasks.remove(task);
        addTaskToTreeSet(task);
    }

    public List<Task> getPrioritizedTasks() {
        return sortedTasks.stream().toList();
    }

    public boolean isTimeOverlap(Task task) {
        if (!sortedTasks.isEmpty())
            return sortedTasks.stream().allMatch(t -> task.getStartTime().isBefore(t.getStartTime()) && task.getEndTime().isAfter(t.getEndTime()));
        return false;
    }

    @Override
    public void createTask(Task task) {
        tasks.put(task.getId(), task);
        addTaskToTreeSet(task);
    }

    @Override
    public void createEpic(Epic epic) {
        epics.put(epic.getId(), epic);
        addTaskToTreeSet(epic);
    }

    @Override
    public void createSubTask(SubTask subTask) {
        Integer subTaskId = subTask.getId();
        Integer epicId = subTask.getEpicId();

        subTasks.put(subTaskId, subTask);

        Epic epic = epics.get(epicId);

        epic.addSubTaskId(subTaskId);
        epic.setTaskStatus(getEpicSubTasks(epicId));

        addTaskToTreeSet(subTask);
    }

    @Override
    public Task getTask(int id) {
        Task task = tasks.get(id);
        if (task != null) {
            historyManager.add(task);
            return task;
        } else {
            return null;
        }
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            historyManager.add(epic);
            return epic;
        } else {
            return null;
        }
    }

    @Override
    public SubTask getSubTask(int id) {
        SubTask subTask = subTasks.get(id);
        if (subTask != null) {
            historyManager.add(subTask);
            return subTask;
        } else {
            return null;
        }
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

    @Override
    public void updateTask(Task task) {
        tasks.replace(task.getId(), task);

        updateTaskInTreeSet(task);
    }

    @Override
    public void updateEpic(Epic epic) {
        Epic updatedEpic = epics.get(epic.getId());
        updatedEpic.setName(epic.getName());
        updatedEpic.setDescription(epic.getDescription());

        epics.replace(epic.getId(), epic);
        updateTaskInTreeSet(epic);
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
            List<Integer> newSubTasks = epic.getSubTasksIds();
            newSubTasks.add(updatedSubTask.getId());
            epic.setSubTasksIds(newSubTasks);
        }

        epic.setTaskStatus(getEpicSubTasks(updatedSubTask.getEpicId()));

        updateTaskInTreeSet(subTask);
    }

    @Override
    public void deleteTask(int id) {
        sortedTasks.remove(tasks.get(id));
        tasks.remove(id);
    }

    @Override
    public void deleteEpic(int id) {
        getEpicSubTasks(id).forEach(subtask -> subTasks.remove(subtask.getId()));
        sortedTasks.remove(epics.get(id));
        epics.remove(id);
    }

    private void deleteSubTaskEpicConnection(int epicId, int subTaskId) {
        Epic epic = epics.get(epicId);

        epic.removeSubTaskId(subTaskId);
        epic.setTaskStatus(getEpicSubTasks(epicId));
    }

    @Override
    public void deleteSubTask(int id) {
        SubTask subTask = subTasks.get(id);

        deleteSubTaskEpicConnection(subTask.getEpicId(), subTask.getId());

        sortedTasks.remove(subTasks.get(subTask.getId()));
        subTasks.remove(subTask.getId());
    }


    @Override
    public void deleteAllTasks() {
        tasks.clear();
    }


    @Override
    public void deleteAllEpics() {
        epics.clear();
        subTasks.clear();
    }


    @Override
    public void deleteAllSubTasks() {
        subTasks.values().forEach(subtask -> deleteSubTaskEpicConnection(subtask.getEpicId(), subtask.getId()));
        subTasks.clear();
    }


    public ArrayList<SubTask> getEpicSubTasks(int epicId) {
        // Получение всех подзадач Эпика
        return epics.get(epicId).getSubTasksIds().stream().map(subTasks::get).collect(Collectors.toCollection(ArrayList::new));
    }

}
