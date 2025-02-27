package managers;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.stream.Stream;

import exceptions.ManagerSaveException;
import task.Task;
import task.SubTask;
import task.Epic;
import task.TaskTypes;

public class FileBackendTaskManager extends InMemoryTaskManager implements TaskManager {
    private final File tasksFile;

    public FileBackendTaskManager() {
        super(Managers.getDefaultHistory());
        this.tasksFile = new File("tasksFile.csv");
    }

    public FileBackendTaskManager(File file) {
        super(Managers.getDefaultHistory());
        this.tasksFile = file;
    }


    public void save() throws ManagerSaveException {
        String header = "id,type,name,status,description,epic";
        try (FileWriter writer = new FileWriter(tasksFile, StandardCharsets.UTF_8)) {
            writer.write(header + "\n");

            Stream.of(super.getTasks(), super.getEpics(), super.getSubTasks()).flatMap(Collection::stream)
                    .forEach(task -> {
                        try {
                            writer.write(task.toString() + "\n");
                        } catch (IOException e) {
                            throw new ManagerSaveException("Ошибка записи в файл", e);
                        }
                    });

        } catch (IOException exception) {
            throw new ManagerSaveException("Ошибка работы менеджера", exception, tasksFile);
        }
    }

    public static FileBackendTaskManager loadFromFile(File file) {
        FileBackendTaskManager fileManager = new FileBackendTaskManager();
        int currentIdOfTask = 0;

        try (FileReader reader = new FileReader(file); BufferedReader bufferedReader = new BufferedReader(reader)) {
            while (bufferedReader.ready()) {
                try {
                    String line = bufferedReader.readLine();
                    String[] taskData = line.split(",");
                    switch (TaskTypes.valueOf(taskData[1].toUpperCase())) {
                        case TaskTypes.TASK:
                            currentIdOfTask++;
                            taskData[0] = String.valueOf(currentIdOfTask);
                            Task task = Task.fromString(taskData);
                            fileManager.createTask(task);
                            continue;
                        case TaskTypes.EPIC:
                            currentIdOfTask++;
                            taskData[0] = String.valueOf(currentIdOfTask);
                            Epic epic = Epic.fromString(taskData);
                            fileManager.createEpic(epic);
                            continue;
                        case TaskTypes.SUBTASK:
                            currentIdOfTask++;
                            taskData[0] = String.valueOf(currentIdOfTask);
                            SubTask subTask = SubTask.fromString(taskData);
                            fileManager.createSubTask(subTask);
                            continue;
                        default:
                            System.out.println("Такого типа задачи не существует");
                    }
                } catch (IllegalArgumentException ignored) {
                }
            }
            return fileManager;
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка работы менеджера", e, file);
        }
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }

    @Override
    public void createSubTask(SubTask subtask) {
        super.createSubTask(subtask);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        super.updateSubTask(subTask);
        save();
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public void deleteEpic(int id) {
        super.deleteEpic(id);
        save();
    }


    @Override
    public void deleteSubTask(int id) {
        super.deleteSubTask(id);
        save();
    }


    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }


    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }


    @Override
    public void deleteAllSubTasks() {
        super.deleteAllSubTasks();
        save();
    }

    public File getTasksFile() {
        return tasksFile;
    }

}
