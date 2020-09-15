/**
 * @author jspii
 */

/**
 * A deque implementation using a resizing Array
 */
public class ArrayDeque<T> {
    private int size;
    private T[] deque;
    private int nextFirst;
    private int nextLast;

    public ArrayDeque() {
        size = 0;
        deque = (T[]) new Object[8];
        nextFirst = 0;
        nextLast = 1;
    }

    public void addFirst(T item) {
        if (size == deque.length) {
            resizeUp();
        }
        deque[nextFirst] = item;
        nextFirst = minusOne(nextFirst);
        size++;
    }

    public void addLast(T item) {
        if (size == deque.length) {
            resizeUp();
        }
        deque[nextLast] = item;
        nextLast = plusOne(nextLast);
        size++;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    public void printDeque() {
        for (int i = 0; i < deque.length; i++) {
            if (deque[i] != null) {
                System.out.print(deque[i] + " ");
            }
        }
        System.out.println("");
    }

    public T removeFirst() {
        if (size == 0) {
            return null;
        }
        if ((float) size / deque.length <= 0.25 && deque.length > 8) {
            resizeDown();
        }
        nextFirst = plusOne(nextFirst);
        T res = deque[nextFirst];
        deque[nextFirst] = null;
        size--;
        return res;
    }

    public T removeLast() {
        if (size == 0) {
            return null;
        }
        if ((float) size / deque.length <= 0.25 && deque.length > 8) {
            resizeDown();
        }
        nextLast = minusOne(nextLast);
        T res = deque[nextLast];
        deque[nextLast] = null;
        size--;
        return res;
    }

    public T get(int index) {
        if (index < 0 || index > deque.length - 1) {
            return null;
        }

        return deque[(nextFirst + 1 + index) % deque.length];
    }

    private void resizeUp() {
        T[] newDeque = (T[]) new Object[deque.length * 2];
        System.arraycopy(deque, 0, newDeque, 1, size);
        deque = newDeque;
        nextFirst = 0;
        nextLast = size;
    }

    private void resizeDown() {
        T[] newDeque = (T[]) new Object[deque.length / 2];
        System.arraycopy(deque, Integer.min(minusOne(nextLast), plusOne(nextFirst)), newDeque, 1, size);
        deque = newDeque;
        nextFirst = 0;
        nextLast = size;
    }

    private int minusOne(int val) {
        return val - 1 < 0 ? deque.length - 1 : val - 1;
    }

    private int plusOne(int val) {
        return val + 1 > deque.length - 1 ? 0 : val + 1;
    }
}
