package taskmanager.service;

import taskmanager.model.Task;
import taskmanager.model.Node;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

    private final CustomLinkedList history = new CustomLinkedList();

    @Override
    public List<Task> getHistory() {
        return history.getTasks(history.head);
    }

    @Override
    public void add(Task task) {
        if (task != null) {
            if (history.getNodesMap().containsKey(task.getId())) {
                remove(task.getId());
            }
            history.linkLast(task);
        } else {
            System.out.println("Задача не передана.");
        }
    }

    @Override
    public void remove(int id) {
        history.removeNode(history.getNodesMap().getOrDefault(id, null));
    }

    private static class CustomLinkedList {

        private final Map<Integer, Node<Task>> nodesById = new HashMap<>();

        private Node<Task> head;

        private Node<Task> tail;

        public Map<Integer, Node<Task>> getNodesMap() {
            return nodesById;
        }

        public void linkLast(Task task) {
            final Node<Task> oldTail = tail;
            final Node<Task> newNode = new Node<>(oldTail, task, null);
            getNodesMap().put(task.getId(), newNode); // обновляем узел мапы под заданным id
            tail = newNode;
            if (oldTail == null) {
                head = newNode;
            } else {
                oldTail.next = newNode;
            }
        }

        public void removeNode(Node<Task> node) {
            if (node != null) {
                if (node.prev != null) {
                    node.prev.next = node.next;
                } else {
                    head = node.next;
                }
                if (node.next != null) {
                    node.next.prev = node.prev;
                } else {
                    tail = node.prev;
                }
            } else {
                System.out.println("Задачи нет в истории просмотров.");
            }
        }

        public List<Task> getTasks(Node<Task> head) {
            List<Task> historyArrayList = new ArrayList<>();
            Node<Task> temp = head;
            while (temp != null) {
                historyArrayList.add(temp.data);
                temp = temp.next;
            }
            return historyArrayList;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;
            CustomLinkedList tasks = (CustomLinkedList) o;
            return Objects.equals(nodesById, tasks.nodesById) && Objects.equals(head, tasks.head) && Objects.equals(tail, tasks.tail);
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), nodesById, head, tail);
        }
    }
}

