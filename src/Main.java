import java.util.ArrayList;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) {

        TaskManager taskManager = new TaskManager();
        int currentIdOfTask = 0;

        currentIdOfTask++;
        Task task = new Task("Task 1", "Task description 1", currentIdOfTask);

        currentIdOfTask++;
        Task task2 = new Task("Task 1", "Task description 1", currentIdOfTask);

        taskManager.createTask(task);
        taskManager.createTask(task2);

        System.out.println(taskManager.getTasks().toString());

        currentIdOfTask++;
        Epic epic = new Epic("Epic 1", "Epic description 1", currentIdOfTask, new ArrayList<SubTask>(0));
        taskManager.createEpic(epic);

        System.out.println(taskManager.getEpics().toString());

        currentIdOfTask++;
        SubTask subTask = new SubTask("SubTask 1", "SubTask description 1", currentIdOfTask, epic);
        taskManager.createSubTask(subTask);

        currentIdOfTask++;
        SubTask subTask2 = new SubTask("SubTask 2", "SubTask description 2", currentIdOfTask, epic);
        taskManager.createSubTask(subTask2);

        System.out.println(taskManager.getSubTasks());

        epic.setSubTasks(new ArrayList<>(Arrays.asList(subTask, subTask2)));
        taskManager.updateEpic(epic);

        System.out.println(taskManager.getEpics().toString());

        taskManager.deleteTask(task2.getId());

        task.setName("Updated Task 1 Name");
        taskManager.updateTask(task);

        System.out.println(taskManager.getTasks());

        taskManager.deleteSubTask(subTask2.getId());

        subTask.setTaskStatus(TaskStatuses.valueOf("DONE"));
        taskManager.updateSubTask(subTask);
        System.out.println(taskManager.getSubTasks());
        System.out.println(taskManager.getEpics());


    }
}
