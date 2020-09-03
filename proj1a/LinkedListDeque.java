/**
 * @author jspii
 */

/**
 * A Deque implementation using a double linked list with sentinel nodes
 */
public class LinkedListDeque<Type> {
    private int size;
    private Node sentinel;

    private class Node {
        Type value;
        Node head;
        Node tail;
    }

    public LinkedListDeque() {
        size = 0;
        sentinel = new Node();
    }

    /** must be constant time operation - mutative */
    public void addFirst(Type item) {
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
    public void addLast(Type item) {
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

    public Type removeFirst() {
        if (sentinel.tail == null)
            return null;
        if (size == 1) {
            Type first = sentinel.tail.value;
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

    public Type removeLast() {
        if (sentinel.head == null)
            return null;
        if (size == 1) {
            Type last = sentinel.head.value;
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

    public Type get(int index) {
        if (index < 0 || index > size)
            return null;
        Node res = sentinel.head;
        while (index > 0) {
            res = res.head;
            index--;
        }
        return res.value;
    }

    /** recursive version of get */
    public Type getRecursive(int index) {
        if (index < 0 || index > size)
            return null;
        return getRecursive(index, sentinel.head);
    }

    /** private helper for getRecursive */
    private Type getRecursive(int index, Node node) {
        if (index > 0)
            return getRecursive(--index, node.head);
        return node.value;
    }
}
