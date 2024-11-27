import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

public class TaskManager {
    public HashMap<Integer, Task> tasks = new HashMap<>(5);
    public HashMap<Integer, Epic> epics = new HashMap<>(5);
    public HashMap<Integer, SubTask> subTasks = new HashMap<>(5);


    public HashMap<Integer, Object> getAllTasks(Optional<String> className) {
        // Формирование ответа по всем задачам с возможностью фильтрации
        HashMap<Integer, Object> tasksToShow = new HashMap<>(5);
        if (className.isPresent()) {
            tasksToShow = (HashMap<Integer, Object>) getTaskField(className.get());
        } else {
            tasksToShow.putAll(tasks);
            tasksToShow.putAll(epics);
            tasksToShow.putAll(subTasks);
        }

        for (Integer id : tasksToShow.keySet()) {
            Object task = tasksToShow.get(id);
            System.out.println("id задачи: " + id + " Задача: " + task.getClass() + " " + task);
        }
        return tasksToShow;
    }

    public HashMap<Integer, ?> getTaskField(String className) {
        // Получение доступа к конкретному полю HashMap по типу задачи
        if (className.equals("Task")) {
            return tasks;
        } else if (className.equals("Epic")) {
            return epics;
        } else {
            return subTasks;
        }

    }

    public ArrayList<String> taskNames() {
        // Получение всех имён типов задач
        ArrayList<String> taskNames = new ArrayList<>(3);
        taskNames.add(Task.class.getSimpleName());
        taskNames.add(Epic.class.getSimpleName());
        taskNames.add(SubTask.class.getSimpleName());
        return taskNames;
    }


    public void deleteAllTasks() {
        tasks.clear();
        epics.clear();
        subTasks.clear();
    }

    public Task getTaskById(int id) {
        // Получение задачи по id

        for (String taskName : taskNames()) {
            HashMap<Integer, Object> taskObjects = (HashMap<Integer, Object>) getTaskField(taskName);
            if (taskObjects.containsKey(id)) {
                return (Task) taskObjects.get(id);
            }
        }
        return null;
    }

    public void createTask(Task task) {
        HashMap<Integer, Object> taskObjects = (HashMap<Integer, Object>) getTaskField(task.getClass().getSimpleName());
        taskObjects.put(task.getId(), task);
    }

    public void updateTask(Task updatedTask) {
        HashMap<Integer, Object> taskObjects = (HashMap<Integer, Object>) getTaskField(updatedTask.getClass().getSimpleName());
        taskObjects.replace(updatedTask.getId(), updatedTask);

        Task task = getTaskById(updatedTask.getId());

        if (task.getClass().getSimpleName().equals("SubTask")) {
            // Если это SubTask то меняем все значения и в Epic в котором есть ссылка на SubTask
            for (Epic epic : epics.values()) {
                for (SubTask subtask : epic.subTasks) {
                    if (subtask.getId() == updatedTask.getId()) {
                        subtask.setName(updatedTask.getName());
                        subtask.setDescription(updatedTask.getDescription());
                        subtask.setTaskStatus(updatedTask.getTaskStatus());
                        epic.setTaskStatus();
                    }
                }
            }
        }
    }

    public void deleteTask(int id) {
        // Удаляем конкретную задачу

        Task task = getTaskById(id);
        HashMap<Integer, Object> taskObjects = (HashMap<Integer, Object>) getTaskField(task.getClass().getSimpleName());

        if (task.getClass().getSimpleName().equals("Epic")) {
            // Если у класс Epic удаляем все его подзадачи
            ArrayList<SubTask> epicSubTasks = getEpicSubTasks(id);
            for (SubTask subTask : epicSubTasks) {
                subTasks.remove(subTask.getId());
            }
        } else if (task.getClass().getSimpleName().equals("SubTask")) {
            //  Если класс SubTask удаляем ссылку на подзадачу из Epic
            for (Epic epic : epics.values()) {
                if (epic.getSubTasks().contains(task)) {
                    epic.subTasks.remove(task);
                }
            }
        }

        taskObjects.remove(id);
    }

    public ArrayList<SubTask> getEpicSubTasks(int id) {
        // Получение всех подзадач Эпика
        Epic epic = (Epic) getTaskById(id);

        System.out.println(epic.getSubTasks());

        return epic.getSubTasks();
    }


}
