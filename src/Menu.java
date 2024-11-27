import java.util.ArrayList;
import java.util.Optional;
import java.util.Scanner;

import java.lang.ClassCastException;

public class Menu {

    Scanner scanner = new Scanner(System.in);
    TaskManager taskManager = new TaskManager();
    public static int currentIdOfTask = 0;

    void startMenu() {
        // Запуск меню программы

        while (true) {
            printMenu();

            int command = scanner.nextInt();

            if (command == 1) {
                handleFirstCommand();
            } else if (command == 2) {
                handleSecondCommand();
            } else if (command == 3) {
                handleThirdCommand();
            } else if (command == 4) {
                handleFourthCommand();
            } else if (command == 0) {
                break;
            } else {
                handleUnknownCommand();
                break;
            }
        }
    }


    static void printMenu() {
        // Вывод в консоль выбора меню
        System.out.println("Что вы хотите сделать?");
        System.out.println("1 - Создать задачу");
        System.out.println("2 - Получить задачу");
        System.out.println("3 - Удалить задачу");
        System.out.println("4 - Обновить задачу");
        System.out.println("0 - Выход");
    }

    void handleFirstCommand() {
        // Обработка первой команды
        System.out.println("Выберете тип задачи [1: Task, 2: Epic, 3: SubTask, 0: Выход]: ");
        int taskType = scanner.nextInt();

        // Закомментированные поля используются для удобного и быстрого создания объектов

        while (true) {
            if (taskType == 1) {
                currentIdOfTask++;
                System.out.println("Введите название задачи: ");
                String name = scanner.next();
//                String name = "Task name " + currentIdOfTask;
                System.out.println("Введите описание задачи: ");
                String description = scanner.next();
//                String description = "Task description " + currentIdOfTask;
                Task task = new Task(name, description, currentIdOfTask);
                taskManager.createTask(task);
                System.out.println("Id новой задачи: " + task.getId());
                break;
            } else if (taskType == 2) {
                currentIdOfTask++;
                System.out.println("Введите название эпика: ");
                String name = scanner.next();
//                String name = "Epic name " + currentIdOfTask;
                System.out.println("Введите описание эпика: ");
                String description = scanner.next();
//                String description = "Epic description " + currentIdOfTask;
                Epic epic = new Epic(name, description, currentIdOfTask, new ArrayList<>());
                taskManager.createTask(epic);
                System.out.println("Id нового эпика: " + epic.getId());
                break;
            } else if (taskType == 3) {
                currentIdOfTask++;
                System.out.println("Введите название подзадачи: ");
                String name = scanner.next();
//                String name = "SubTask name " + currentIdOfTask;
                System.out.println("Введите описание подзадача: ");
                String description = scanner.next();
//                String description = "SubTask description " + currentIdOfTask;
                SubTask subTask = new SubTask(name, description, currentIdOfTask);
                taskManager.createTask(subTask);
                System.out.println("Id новой подзадачи: " + subTask.getId());


                while (true) {
                    try {
                        System.out.println("Введите id эпика с которым связать подзадачу: ");
                        int epicId = scanner.nextInt();
                        Epic epic = (Epic) taskManager.getTaskById(epicId);

                        if (epic != null) {
                            epic.subTasks.add(subTask);
                            break;

                        }
                    } catch (ClassCastException e) {
                        System.out.println("Такого эпика не существует, попробуйте еще раз");
                    }
                }
                break;

            } else if (taskType == 0) {
                break;
            } else {
                handleUnknownCommand();
                break;
            }
        }

    }

    void handleSecondCommand() {
        // Обработка второй команды

        System.out.println(
                "Выберете тип задачи которые необходимо вывести " +
                        "[1: Task, 2: Epic, 3: SubTask, 4: Вывести все задачи, 0: Выход]:"
        );
        int taskType = scanner.nextInt();
        while (true) {
            if (taskType == 1) {
                taskManager.getAllTasks(Optional.of(Task.class.getName()));
                break;
            } else if (taskType == 2) {
                taskManager.getAllTasks(Optional.of(Epic.class.getName()));
                break;
            } else if (taskType == 3) {
                taskManager.getAllTasks(Optional.of(SubTask.class.getName()));
                break;
            } else if (taskType == 4) {
                taskManager.getAllTasks(Optional.empty());
                break;
            } else if (taskType == 0) {
                break;
            } else {
                handleUnknownCommand();
                break;
            }
        }

    }

    void handleThirdCommand() {
        // Обработка третьей команды
        System.out.println("Что вы хотите сделать?");
        System.out.println("1 - Удалить все задачи");
        System.out.println("2 - Удалить конкретную задачу");
        System.out.println("0 - Выход");
        int command = scanner.nextInt();
        while (true) {
            if (command == 1) {
                taskManager.deleteAllTasks();
                break;
            } else if (command == 2) {
                System.out.println("Введите id задачи: ");
                int taskId = scanner.nextInt();
                taskManager.deleteTask(taskId);
                break;
            } else if (command == 0) {
                break;
            } else {
                handleUnknownCommand();
                break;
            }
        }
    }

    void handleFourthCommand() {
        // Обработка четвертой команды

        System.out.println("Введите id задачи: ");
        int taskId = scanner.nextInt();

        System.out.println("Введите новое название задачи: ");
        String name = scanner.next();
        System.out.println("Введите новое описание задачи: ");
        String description = scanner.next();

        Task getTask = taskManager.getTaskById(taskId);

        Task task = new Task(name, description, taskId);

        if (!getTask.getClass().getSimpleName().equals("Epic")) {
            System.out.println("Введите новый статус задачи: ");
            String taskStatus = scanner.next();
            task.setTaskStatus(TaskStatuses.valueOf(taskStatus));
        }

        taskManager.updateTask(task);
    }

    void handleUnknownCommand() {
        // Обработка неизвестной команды
        System.out.println("Такой команды не существует, попробуйте другую!");
    }
}
