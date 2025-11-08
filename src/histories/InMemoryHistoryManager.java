package histories;

import exceptions.TaskNotExistsException;
import task.AbstractTask;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private Map<Integer, Node<AbstractTask>> history = new HashMap<>();
    private Node<AbstractTask> head;
    private Node<AbstractTask> tail;

    //добавляет task и в двусвязный список и в мапу
    @Override
    public void add(AbstractTask task) {
        int id = task.getId();

        if(history.containsKey(id)) {
            remove(id);
        }
        history.put(id, linkLast(task));
    }

    //удаляет и из двусвязного списка и из мапы
    @Override
    public void remove(int id) {
        if(!history.containsKey(id)) {
            return;
        }
        removeNode(history.get(id));
        history.remove(id);
    }


    //добавляет Node в конец двусвязного списка и мапы
    private Node<AbstractTask> linkLast(AbstractTask task) {
        Node<AbstractTask> node = new Node<>(task, tail, null);
        if(tail == null) {
            head = node;
        } else {
            tail.setNext(node);
        }
        tail = node;
        return node;
    }

    //вырезает Node из двусвязного списка
    private Node<AbstractTask> removeNode(Node<AbstractTask> node) {
        if(node == head && node.getNext() != null) {
            Node<AbstractTask> n = node.getNext();
            n.setPrev(null);
            head = n;
        } else if(node == head && node == tail) {
            head = null;
            tail = null;
        } else if (node == tail && (node.getPrev() != null)) {//ТУТ БЫЛА ОШИБКА
            Node<AbstractTask> n = node.getPrev();
            n.setNext(null);
            tail = n;
        } else if (node.getPrev() != null && node.getNext() != null) {
            node.getPrev().setNext(node.getNext());
            node.getNext().setPrev(node.getPrev());
        }
        return node;
    }

    //собирает все задачи двусвязного списка и возвращает ArrayList с ними СДЕЛАНО i != null
    private List<AbstractTask> getTasks() {
        List<AbstractTask> list = new ArrayList<>();

        for(Node<AbstractTask> i = head; i != null; i = i.getNext()) {
            list.add(i.getValue());
        }
        return list;
    }

    //вызывает метод getTasks
    @Override
    public List<AbstractTask> getHistory() {
        return getTasks();
    }
}
