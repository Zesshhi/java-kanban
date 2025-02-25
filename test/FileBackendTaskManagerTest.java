import exceptions.ManagerSaveException;
import managers.FileBackendTaskManager;
import managers.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.SubTask;
import task.Task;
import task.TaskTypes;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileBackendTaskManagerTest extends TaskManagerTest {
    static FileBackendTaskManager fileManager;

    File temporaryFile;


    @Override
    public Task createTask() {
        Task task = super.createTask();
        fileManager.createTask(task);
        return task;
    }

    @Override
    public Epic createEpic() {
        Epic epic = super.createEpic();
        fileManager.createEpic(epic);
        return epic;
    }

    @Override
    public SubTask createSubTask(Epic epic) {
        SubTask subTask = super.createSubTask(epic);
        fileManager.createSubTask(subTask);
        return subTask;
    }

    @BeforeEach
    public void beforeEach() throws IOException {
        currentIdOfTask = 0;
        temporaryFile = File.createTempFile("tasksFile", ".csv");
        fileManager = new FileBackendTaskManager(temporaryFile);
        String a = "123";
    }

    @AfterEach
    public void afterEach() {
        fileManager.getTasksFile().deleteOnExit();
    }

    @Test
    public void check_if_temp_file_is_empty() {
        Assertions.assertEquals(fileManager.getTasksFile().length(), 0);
    }

    @Test
    public void check_if_all_tasks_saved_in_file() {
        Task task1 = createTask();
        Task task2 = createTask();

        Epic epic1 = createEpic();
        Epic epic2 = createEpic();

        SubTask subTask1 = createSubTask(epic1);
        SubTask subTask2 = createSubTask(epic1);
        SubTask subTask3 = createSubTask(epic2);

        List<Task> tasksList = new ArrayList<>();
        tasksList.add(task1);
        tasksList.add(task2);
        tasksList.add(epic1);
        tasksList.add(epic2);
        tasksList.add(subTask1);
        tasksList.add(subTask2);
        tasksList.add(subTask3);

        List<Task> tasksListFromFile = new ArrayList<>();

        try (FileReader reader = new FileReader(temporaryFile); BufferedReader bufferedReader = new BufferedReader(reader)) {
            while (bufferedReader.ready()) {
                try {
                    String line = bufferedReader.readLine();
                    String[] taskData = line.split(",");
                    switch (TaskTypes.valueOf(taskData[1].toUpperCase())) {
                        case TaskTypes.TASK:
                            Task task = Task.fromString(taskData);
                            tasksListFromFile.add(task);
                            continue;
                        case TaskTypes.EPIC:
                            Epic epic = Epic.fromString(taskData);
                            tasksListFromFile.add(epic);
                            continue;
                        case TaskTypes.SUBTASK:
                            SubTask subTask = SubTask.fromString(taskData);
                            tasksListFromFile.add(subTask);
                            continue;
                        default:
                            System.out.println("Такого типа задачи не существует");
                    }
                } catch (IllegalArgumentException ignored) {
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Assertions.assertArrayEquals(tasksList.toArray(), tasksListFromFile.toArray());
    }

    @Test
    public void check_if_all_tasks_loaded_from_file() {
        Task task1 = createTask();

        Epic epic1 = createEpic();

        SubTask subTask1 = createSubTask(epic1);
        SubTask subTask2 = createSubTask(epic1);

        try (FileWriter writer = new FileWriter(temporaryFile)) {

            writer.write(task1.toString() + "\n");

            writer.write(epic1.toString() + "\n");

            writer.write(subTask1.toString() + "\n");
            writer.write(subTask2.toString() + "\n");


        } catch (IOException exception) {
            throw new ManagerSaveException("Ошибка работы менеджера", exception, temporaryFile);
        }

        FileBackendTaskManager temporaryFileManager = FileBackendTaskManager.loadFromFile(temporaryFile);

        Assertions.assertEquals(temporaryFileManager.getTask(task1.getId()), task1);

        Assertions.assertEquals(temporaryFileManager.getEpic(epic1.getId()), epic1);

        Assertions.assertEquals(temporaryFileManager.getSubTask(subTask1.getId()), subTask1);
        Assertions.assertEquals(temporaryFileManager.getSubTask(subTask2.getId()), subTask2);
    }


}
