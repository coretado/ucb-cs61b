package synthesizer;

import java.util.Iterator;

public class ArrayRingBuffer<T> extends AbstractBoundedQueue<T> {
    /* Index for the next dequeue or peek. */
    private int first;            // index for the next dequeue or peek
    /* Index for the next enqueue. */
    private int last;
    /* Array for storing the buffer data. */
    private T[] rb;

    /**
     * Create a new ArrayRingBuffer with the given capacity.
     */
    public ArrayRingBuffer(int capacity) {
        // dealing with Abstract variables first
        super();
        this.capacity = capacity;
        this.fillCount = 0;
        // dealing with local variables
        first = 0;
        last = 0;
        rb = (T[]) new Object[capacity];
    }

    private int incrementHelper(int val) {
        return val + 1 == this.capacity ? 0 : val + 1;
    }

    /**
     * Adds x to the end of the ring buffer. If there is no room, then
     * throw new RuntimeException("Ring buffer overflow"). Exceptions
     * covered Monday.
     */
    public void enqueue(T x) {
        if (fillCount == capacity) {
            throw new RuntimeException("Ring Buffer Overflow");
        }
        rb[last] = x;
        last = incrementHelper(last);
        this.fillCount++;
    }

    /**
     * Dequeue oldest item in the ring buffer. If the buffer is empty, then
     * throw new RuntimeException("Ring buffer underflow"). Exceptions
     * covered Monday.
     */
    public T dequeue() {
        if (fillCount == 0) {
            throw new RuntimeException("Ring Buffer Underflow");
        }
        T peekaboo = rb[first];
        rb[first] = null;
        first = incrementHelper(first);
        this.fillCount--;
        return peekaboo;
    }

    /**
     * Return oldest item, but don't remove it.
     */
    public T peek() {
        return rb[first];
    }

    public Iterator<T> iterator() {
        return new RBIterator();
    }

    private class RBIterator implements Iterator<T> {
        private int ptr;

        public RBIterator() {
            ptr = 0;
        }

        @Override
        public boolean hasNext() {
            return ptr != fillCount;
        }

        @Override
        public T next() {
            T res = rb[ptr];
            ptr += 1;
            return res;
        }
    }
}
