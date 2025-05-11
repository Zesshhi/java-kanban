package managers;

import exceptions.InvalidDataException;
import exceptions.NotFoundException;
import task.Epic;
import task.SubTask;
import task.Task;

import java.util.List;

public interface TaskManager {

    Task getTask(int id) throws NotFoundException;

    Epic getEpic(int id) throws NotFoundException;

    SubTask getSubTask(int id) throws NotFoundException;

    void createTask(Task task) throws InvalidDataException;

    void createEpic(Epic epic);

    void createSubTask(SubTask subTask) throws InvalidDataException;

    void updateTask(Task task) throws InvalidDataException;

    void updateEpic(Epic epic);

    void updateSubTask(SubTask subTask) throws InvalidDataException;

    void deleteTask(int id);

    void deleteEpic(int id);

    void deleteSubTask(int id);

    List<Task> getTasks();

    List<Epic> getEpics();

    List<SubTask> getSubTasks();

    void deleteAllTasks();

    void deleteAllEpics();

    void deleteAllSubTasks();

    List<SubTask> getEpicSubTasks(int id) throws NotFoundException;

    List<Task> getPrioritizedTasks();
}