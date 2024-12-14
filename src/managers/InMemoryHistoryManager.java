package managers;

import history.HistoryManager;
import task.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final List<Task> history;

    public InMemoryHistoryManager() {
        history = new ArrayList<>(10);
    }

    @Override
    public void add(Task task) {
        history.add(task);
        if (history.size() > 10) {
            history.removeFirst();
            history.removeLast();
            history.addFirst(task);
        }
    }

    @Override
    public List<Task> getHistory() {
        return history;
    }

}
