/**
 * @author jspii
 */

/**
 * A deque implementation using a resizing Array
 */
public class ArrayDeque<T> {
    private T[] deque;
    private int size;
    private int nextFirst;
    private int nextLast;

    /** deque has to be instantiated using a cast since Java doesn't allow Generic Arrays */
    public ArrayDeque() {
        deque = (T[]) new Object[8];
        size = 0;
        nextFirst = 3; // middle left
        nextLast = 4; // middle right
    }

    /** Get item from Deque at given index. Return's null if that item does not exist. */
    public T get(int index) {
        if (index < 0 || index >= deque.length) {
            return null;
        }
        return deque[index];
    }

    /** Adds an item at the front of the deque. Mutative. */
    public void addFirst(T item) {
        if (size == deque.length) {
            resizeUp();
        }
        deque[nextFirst] = item;
        incrementFirst();
        size++;
    }

    /** Removes and returns an item from the front of the deque. Mutative. */
    public T removeFirst() {
        if (size == 0) {
            return null;
        }
        // minimum size is 8 even if the deque is empty
        if (size == deque.length / 4 && deque.length > 8) {
            resizeDown();
        }
        T item = deque[decrementFirst()];
        deque[nextFirst] = null;
        size--;
        return item;
    }

    /** Adds an item to the end of the deque. Mutative. */
    public void addLast(T item) {
        if (size == deque.length) {
            resizeUp();
        }
        deque[nextLast] = item;
        incrementLast();
        size++;
    }

    /** Removes and returns an item from the end of the deque. Mutative. */
    public T removeLast() {
        if (size == 0) {
            return null;
        }
        // minimum size is 8 even if the deque is empty
        if (size == deque.length / 4 && deque.length > 8) {
            resizeDown();
        }
        T item = deque[decrementLast()];
        deque[nextLast] = null;
        size--;
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
        for (int i = 0; i < deque.length; i++) {
            if (deque[i] != null) {
                System.out.println(deque[i]);
            }
        }
    }

    /** Moves first forward one position (visually, it moves backwards).
     * Will handle wrap around.
     */
    private void incrementFirst() {
        if (--nextFirst < 0) {
            nextFirst = deque.length - 1;
        }
    }

    /** Moves first 'back' one position (visually, it moves forwards).
     * Will handle wrap around.
     */
    private int decrementFirst() {
        if (++nextFirst >= deque.length) {
            nextFirst = 0;
        }
        return nextFirst;
    }

    /** Moves last forward one position. Will handle wrap around. */
    private void incrementLast() {
        if (++nextLast >= deque.length) {
            nextLast = 0;
        }
    }

    /** Moves last back one position. Will handle wrap around. */
    private int decrementLast() {
        if (--nextLast < 0) {
            nextLast = deque.length - 1;
        }
        return nextLast;
    }

    /** General purpose index obtain method. Will handle wrap around. */
    private int minusOne(int val) {
        return Math.max(val - 1, 0);
    }

    /** General purpose index obtain method. Will handle wrap around. */
    private int plusOne(int val) {
        return val + 1 >= deque.length ? 0 : val + 1;
    }

    /** Doubles an array's size. Amortized complexity of linear. */
    private void resizeUp() {
        int currentSize = deque.length;
        T[] newDeque = (T[]) new Object[currentSize * 2];
        System.arraycopy(deque, 0, newDeque, currentSize / 2, currentSize);

        setPointersAndDeque(currentSize * 2, newDeque);
    }

    /** Reduces an array's size by half.
     * This is called when an Array is 1/4 full to prevent thrashing
     */
    private void resizeDown() {
        int currentSize = deque.length;
        T[] newDeque = (T[]) new Object[currentSize / 2];
        System.arraycopy(
            deque,
            Math.min(plusOne(nextFirst),
            minusOne(nextLast)),
            newDeque, currentSize / 8, currentSize / 4
        );

        setPointersAndDeque(currentSize / 2, newDeque);
    }

    /** set new pointers and new deque */
    private void setPointersAndDeque(int cs, T[] newDeque) {
        nextFirst = (cs / 4) - 1;
        nextLast = (cs / 4) * 3;
        deque = newDeque;
    }
}
