/**
 * @author jspii
 */

/**
 * A Deque implementation using a double linked list with sentinel nodes
 */
public class LinkedListDeque<T> {
    private int size;
    private Node sentinel;

    private class Node {
        T value;
        Node head;
        Node tail;
    }

    public LinkedListDeque() {
        size = 0;
        sentinel = new Node();
    }

    /** must be constant time operation - mutative */
    public void addFirst(T item) {
        Node add = new Node();
        add.value = item;

        if (size == 0) {
            sentinel.head = add;
            sentinel.tail = add;
            add.head = sentinel;
            add.tail = sentinel;
            ++size;
            return;
        }

        Node previousFirst = sentinel.tail;
        previousFirst.head = add;
        sentinel.tail = add;
        add.head = sentinel;
        add.tail = previousFirst;
        ++size;
    }

    /** must be constant time operation - mutative */
    public void addLast(T item) {
        Node last = new Node();
        last.value = item;

        if (size == 0) {
            sentinel.head = last;
            sentinel.tail = last;
            last.head = sentinel;
            last.tail = sentinel;
            ++size;
            return;
        }

        Node previousLast = sentinel.head;
        previousLast.tail = last;
        sentinel.head = last;
        last.head = previousLast;
        last.tail = sentinel;
        ++size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    /** must be constant time operation */
    public int size() {
        return size;
    }

    public void printDeque() {
        Node node = sentinel.tail;
        while (node != sentinel) {
            System.out.println(node.value + " ");
            node = node.tail;
        }
    }

    public T removeFirst() {
        if (sentinel.tail == null) {
            return null;
        }
        if (size == 1) {
            T first = sentinel.tail.value;
            sentinel.head = null;
            sentinel.tail = null;
            --size;
            return first;
        }
        Node first = sentinel.tail;
        Node next = first.tail;
        sentinel.tail = next;
        next.head = sentinel;
        --size;
        return first.value;
    }

    public T removeLast() {
        if (sentinel.head == null) {
            return null;
        }
        if (size == 1) {
            T last = sentinel.head.value;
            sentinel.head = null;
            sentinel.tail = null;
            --size;
            return last;
        }
        Node last = sentinel.head;
        Node prev = last.head;
        sentinel.head = prev;
        prev.tail = sentinel;
        --size;
        return last.value;
    }

    public T get(int index) {
        if (index < 0 || index > size) {
            return null;
        }
        Node res = sentinel.tail;
        while (index > 0) {
            res = res.tail;
            index--;
        }
        return res.value;
    }

    /** recursive version of get */
    public T getRecursive(int index) {
        if (index < 0 || index > size) {
            return null;
        }
        return getRecursive(index, sentinel.tail);
    }

    /** private helper for getRecursive */
    private T getRecursive(int index, Node node) {
        if (index > 0) {
            return getRecursive(--index, node.tail);
        }
        return node.value;
    }
}
