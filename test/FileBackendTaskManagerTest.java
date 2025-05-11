import exceptions.InvalidDataException;
import exceptions.ManagerSaveException;
import exceptions.NotFoundException;
import managers.FileBackendTaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.SubTask;
import task.Task;
import task.TaskTypes;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.fail;

public class FileBackendTaskManagerTest extends TaskManagerTest {
    static FileBackendTaskManager fileManager;

    File temporaryFile;

    @Override
    public Task createTaskWithInputDate(LocalDateTime inputDate) throws InvalidDataException {
        Task task = super.createTaskWithInputDate(inputDate);
        fileManager.createTask(task);
        return task;
    }

    @Override
    public Epic createEpicWithInputDate(LocalDateTime inputDate) {
        Epic epic = super.createEpicWithInputDate(inputDate);
        fileManager.createEpic(epic);
        return epic;
    }

    @Override
    public SubTask createSubTaskWithInputDate(Epic epic, LocalDateTime inputDate) throws InvalidDataException {
        SubTask subTask = super.createSubTaskWithInputDate(epic, inputDate);
        fileManager.createSubTask(subTask);
        return subTask;
    }


    @Override
    public Task createTask() throws InvalidDataException {
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
    public SubTask createSubTask(Epic epic) throws InvalidDataException {
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
        try {
            Task task1 = createTaskWithInputDate(LocalDateTime.now().minusDays(1));
            Task task2 = createTaskWithInputDate(LocalDateTime.now().minusDays(2));

            Epic epic1 = createEpicWithInputDate(LocalDateTime.now().minusDays(3));
            Epic epic2 = createEpicWithInputDate(LocalDateTime.now().minusDays(4));

            SubTask subTask1 = createSubTaskWithInputDate(epic1, LocalDateTime.now().minusDays(5));
            SubTask subTask2 = createSubTaskWithInputDate(epic1, LocalDateTime.now().minusDays(6));
            SubTask subTask3 = createSubTaskWithInputDate(epic2, LocalDateTime.now().minusDays(7));


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
        } catch (InvalidDataException ignore) {
            fail("Неправильные данные");
        }
    }

    @Test
    public void check_if_all_tasks_loaded_from_file() {
        try {
            Task task1 = createTask();

            Epic epic1 = createEpic();

            SubTask subTask1 = createSubTaskWithInputDate(epic1, LocalDateTime.now().minusDays(2));
            SubTask subTask2 = createSubTaskWithInputDate(epic1, LocalDateTime.now().minusDays(3));

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
        } catch (InvalidDataException ignore) {
            fail("Неправильные данные");
        } catch (NotFoundException ignore){
            fail("Задача была найдена");
        }
    }


}
