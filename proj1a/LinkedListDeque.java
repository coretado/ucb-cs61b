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

    }

    public Type removeFirst() {

    }

    public Type removeLast() {

    }

    public Type get(int index) {

    }

    /** recursive version of get */
    public Type getRecursive(int index) {

    }
}
