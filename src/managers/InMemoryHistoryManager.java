package managers;

import taskclasses.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private Node<Task> head;
    private Node<Task> tail;
    private int size = 0;

    public Map<Integer, Node<Task>> getIdConnectedToNodes() {
        return idConnectedToNodes;
    }

    Map<Integer, Node<Task>> idConnectedToNodes;

    @Override
    public void addToHistory(Task task) {
        if (idConnectedToNodes.containsKey(task.getId())) {
            removeTask(task.getId());
        }
        final Node<Task> oldTail = tail;
        final Node<Task> newNode = new Node<>(oldTail, task, null);
        tail = newNode;
        if (oldTail == null) {
            head = newNode;
        } else {
            oldTail.next = newNode;
        }
        idConnectedToNodes.put(task.getId(),newNode);
        size++;
    }

    @Override
    public List<Task> getHistory() {
        List<Task> history = new ArrayList<>();
        if (head != null) {
            history.add(head.data);
            Node<Task> nextNode;
            if (head.next != null) {
                nextNode = head.next;
                while (true) {
                    history.add(nextNode.data);
                    if (nextNode.next != null) {
                        nextNode = nextNode.next;
                    } else {
                        break;
                    }
                }
            }
        }
        return history;
    }

    public void removeNode(Node<Task> taskNode) {
        Node<Task> prevNode = taskNode.prev;
        Node<Task> nextNode = taskNode.next;
        if (prevNode != null) {
            prevNode.next = nextNode;
        } else {
            head = taskNode.next;
        }
        if (nextNode != null) {
            nextNode.prev = prevNode;
        } else {
            tail = taskNode.prev;
        }
    }

    @Override
    public void removeTask(int id) {
        if (idConnectedToNodes.containsKey(id)) {
            Node<Task> taskToRemove = idConnectedToNodes.get(id);
            removeNode(taskToRemove);
            idConnectedToNodes.remove(id);
        }
    }

    public InMemoryHistoryManager() {
        idConnectedToNodes = new HashMap<>();
    }

    static class Node<E> {
        public E data;
        public Node<E> next;
        public Node<E> prev;

        public Node(Node<E> prev, E data, Node<E> next) {
            this.data = data;
            this.next = next;
            this.prev = prev;
        }
    }
}