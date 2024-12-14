package managers;

import history.HistoryManager;
import task.TaskManager;

public class Managers {
    private static TaskManager taskManager;
    private static HistoryManager historyManager;

    public Managers() {
        historyManager = new InMemoryHistoryManager();
        taskManager = new InMemoryTaskManager(historyManager);
    }

    public TaskManager getDefault() {
        return taskManager;
    }

    public HistoryManager getDefaultHistory() {
        return historyManager;
    }
}
