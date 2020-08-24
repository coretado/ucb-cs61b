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

    }

    /** must be constant time operation - mutative */
    public void addLast(Type item) {

    }

    public boolean isEmpty() {
        return size == 0;
    }

    /** must be constant time operation */
    public int size() {
        return size;
    }

    public void printDeque() {
        Node node = sentinel.head;
        while (node != sentinel) {
            System.out.println(node.value + " ");
            node = node.head;
        }
    }

    public Type removeFirst() {

    }

    public Type removeLast() {

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
