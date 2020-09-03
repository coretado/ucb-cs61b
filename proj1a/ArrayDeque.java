/**
 * @author jspii
 */

/**
 * A deque implementation using a resizing Array
 */
public class ArrayDeque<Item> {
    private Item[] deque;
    private int size;
    private int nextFirst;
    private int nextLast;

    /** deque has to be instantiated using a cast since Java doesn't allow Generic Arrays */
    public ArrayDeque() {
        deque = (Item[]) new Object[8];
        size = 0;
        nextFirst = 3;
        nextLast = 4;
    }

    /** Get item from Deque at given index. Return's null if that item does not exist. */
    public Item get(int index) {
        if (index < 0 || index >= size) return null;
        return deque[index];
    }

    /** Adds an item at the front of the deque. Mutative. */
    public void addFirst(Item item) {
        deque[nextFirst] = item;
        moveFirst();
    }

    /** Removes and returns an item from the front of the deque. Mutative. */
    public Item removeFirst() {
        Item item = deque[nextFirst--];
        deque[nextFirst] = null;
        return item;
    }

    /** Adds an item to the end of the deque. Mutative. */
    public void addLast(Item item) {
        deque[nextLast] = item;
        moveLast();
    }

    /** Removes and returns an item from the end of the deque. Mutative. */
    public Item removeLast() {
        Item item = deque[nextLast--];
        deque[nextLast] = null;
        return item;
    }

    /** Returns true if the deque has no items or false if the deque has items. */
    public boolean isEmpty() {
        return size == 0;
    }

    /** Returns number of items in the deque. */
    public int size() {
        return size;
    }

    /** Prints the deque in sequential order. */
    public void printDeque() {
        for (int i = 0; i < size; i++) {
            if (deque[i] != null)
                System.out.println(deque[i]);
        }
    }

    /** Moves first forward one position (visually, it moves backwards). Will handle wrap around. */
    private void moveFirst() {
        if (--nextFirst < 0) nextFirst = size - 1;
    }

    /** Moves last forward one position. */
    private int moveLast() {
        if (++nextLast >= deque.length) nextLast = 0;
    }

    /** Doubles an array's size. Amortized complexity of linear. */
    private void resizeUp() {
        Item[] newDeque = (Item[]) new Object[size * 2];
        System.arraycopy(deque, 0, newDeque, 0, size);
        size *= 2;
        deque = newDeque;
    }

    /** Reduces an array's size by half. This is called when an Array is 1/4 full to prevent thrashing */
    private void resizeDown() {
        Item[] newDeque = (Item[]) new Object[size / 2];
        System.arraycopy(deque, 0, newDeque, 0, size / 4);
        size /= 2;
        deque = newDeque;
    }
}
