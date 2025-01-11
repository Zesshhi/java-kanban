package managers;

import task.Task;
import utils.Node;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node<Task>> history = new HashMap<>();
    private Node<Task> head;
    private Node<Task> tail;
    private int size = 0;


    @Override
    public void add(Task task) {
        linkLast(task);
    }

    @Override
    public void remove(int id) {
        removeNode(history.get(id));
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    private void removeNode(Node<Task> node) {
        Node<Task> nextNode = node.getNext();
        Node<Task> previousNode = node.getPrevious();

        history.remove(node.getData().getId());

        node.setData(null);

        if (node == head && node == tail) {
            head = null;
            tail = null;
        } else if (node == head) {
            head = nextNode;
            nextNode.setPrevious(null);
        } else if (node == tail) {
            tail = previousNode;
            previousNode.setNext(null);
        } else {
            previousNode.setNext(nextNode);
            nextNode.setPrevious(previousNode);
        }


    }

    private void linkLast(Task task) {
        Integer taskId = task.getId();
        if (history.containsKey(taskId)) {
            removeNode(history.get(taskId));
        }
        Node<Task> oldTail = tail;
        Node<Task> newNode = new Node<>(task, tail, null);
        tail = newNode;
        history.put(taskId, newNode);

        if (oldTail != null) {
            oldTail.setNext(newNode);
        } else {
            head = newNode;
        }
        size++;

    }


    private List<Task> getTasks() {
        List<Task> tasksList = new ArrayList<>();

        for (Node<Task> currNode = head; currNode != null; currNode = currNode.getNext()) {
            tasksList.add(currNode.getData());
        }

        return tasksList;
    }

    public int getSize() {
        return size;
    }
}
