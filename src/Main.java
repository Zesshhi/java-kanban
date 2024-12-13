import managers.Managers;
import task.*;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {


        Managers managers = new Managers();
        TaskManager taskManager = managers.getDefault();
        int currentIdOfTask = 0;

//        currentIdOfTask++;
//        Task task = new Task("task.Task 1", "task.Task description 1", currentIdOfTask);
//
//        currentIdOfTask++;
//        Task task2 = new Task("task.Task 1", "task.Task description 1", currentIdOfTask);
//
//        taskManager.createTask(task);
//        taskManager.createTask(task2);
//
//        System.out.println(taskManager.getTasks().toString());
//
//        taskManager.deleteTask(task2.getId());

//        task.setName("Updated task.Task 1 Name");
//        taskManager.updateTask(task);
//
//        System.out.println(taskManager.getTasks());
//
//        System.out.println("TASKS " + "=".repeat(50));

        currentIdOfTask++;
        Epic epic = new Epic("task.Epic 1", "task.Epic description 1", currentIdOfTask, new ArrayList<>(0));
        taskManager.createEpic(epic);


        currentIdOfTask++;
        SubTask subTask = new SubTask("task.SubTask 1", "task.SubTask description 1", currentIdOfTask, epic.getId());
        taskManager.createSubTask(subTask);

//        currentIdOfTask++;
//        SubTask subTask2 = new SubTask("task.SubTask 2", "task.SubTask description 2", currentIdOfTask, epic.getId());
//        taskManager.createSubTask(subTask2);

        subTask.setTaskStatus(TaskStatuses.DONE);
        subTask.setDescription("Абоб");
        taskManager.updateSubTask(subTask);

//        subTask2.setTaskStatus(TaskStatuses.DONE);
//        subTask2.setDescription("Абоб 2");
//        taskManager.updateSubTask(subTask2);

//        System.out.println(taskManager.getEpics().toString());
//        System.out.println(taskManager.getSubTasks().toString());

//        taskManager.deleteSubTask(subTask2.getId());
//
//        subTask.setTaskStatus(TaskStatuses.valueOf("DONE"));
//        taskManager.updateSubTask(subTask);
//
//        taskManager.deleteEpic(epic.getId());

//        System.out.println(taskManager.getEpics().toString());
//        System.out.println(taskManager.getSubTasks().toString());
    }
}
