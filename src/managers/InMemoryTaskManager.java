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


    private void addTaskToTreeSet(Task task) {
        sortedTasks.add(task);
    }

    private void updateTaskInTreeSet(Task oldTask, Task newTask) {
        sortedTasks.remove(oldTask);
        addTaskToTreeSet(newTask);
    }

    public List<Task> getPrioritizedTasks() {
        return sortedTasks.stream().toList();
    }

    private boolean isTimeOverlap(Task task) {
        return sortedTasks.stream().anyMatch(t -> !t.equals(task) && (t.getStartTime().equals(task.getEndTime()) || t.getStartTime().isBefore(task.getEndTime())) && (task.getStartTime().isBefore(t.getEndTime()) || task.getStartTime().equals(t.getEndTime())));
    }

    @Override
    public void createTask(Task task) {
        if (task.getStartTime() != null && !isTimeOverlap(task)) {
            tasks.put(task.getId(), task);
            addTaskToTreeSet(task);
        } else if (task.getStartTime() == null) {
            tasks.put(task.getId(), task);
        }
    }

    @Override
    public void createEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    @Override
    public void createSubTask(SubTask subTask) {
        int subTaskId = subTask.getId();
        int epicId = subTask.getEpicId();

        if (subTask.getStartTime() != null && !isTimeOverlap(subTask)) {
            addSubTask(subTask, subTaskId, epicId);
            addTaskToTreeSet(subTask);
        } else if (subTask.getStartTime() == null) {
            addSubTask(subTask, subTaskId, epicId);
        }

    }

    private void addSubTask(SubTask subTask, int subTaskId, int epicId) {
        subTasks.put(subTaskId, subTask);

        Epic epic = epics.get(epicId);

        epic.addSubTaskId(subTaskId);
        epic.setTaskStatus(getEpicSubTasks(epicId));
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
        Task oldTask = tasks.get(task.getId());

        if (task.getStartTime() != null && !isTimeOverlap(task)) {
            tasks.replace(task.getId(), task);
            updateTaskInTreeSet(oldTask, task);
        } else if (task.getStartTime() == null) {
            tasks.replace(task.getId(), task);
        } else if (isTimeOverlap(task)) {
            tasks.remove(oldTask.getId());
            sortedTasks.remove(oldTask);
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        epics.replace(epic.getId(), epic);
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        SubTask oldSubTask = subTasks.get(subTask.getId());

        sortedTasks.remove(oldSubTask);

        if (subTask.getStartTime() != null && !isTimeOverlap(subTask)) {
            replaceSubTask(oldSubTask, subTask);
            updateTaskInTreeSet(oldSubTask, subTask);
        } else if (subTask.getStartTime() == null) {
            replaceSubTask(oldSubTask, subTask);
            sortedTasks.remove(oldSubTask);
        }
    }

    private void replaceSubTask(SubTask oldSubTask, SubTask subTask) {
        Epic epic = epics.get(oldSubTask.getEpicId());

        if (!epic.getSubTasksIds().contains(oldSubTask.getId())) {
            List<Integer> newSubTasks = epic.getSubTasksIds();
            newSubTasks.add(oldSubTask.getId());
            epic.setSubTasksIds(newSubTasks);
        }

        epic.setTaskStatus(getEpicSubTasks(oldSubTask.getEpicId()));

        updateTaskInTreeSet(oldSubTask, subTask);
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


    public List<SubTask> getEpicSubTasks(int epicId) {
        // Получение всех подзадач Эпика
        return epics.get(epicId).getSubTasksIds().stream().map(subTasks::get).collect(Collectors.toCollection(ArrayList::new));
    }

}
