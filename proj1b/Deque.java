public interface Deque<T> {
    public boolean isEmpty();

    /** Returns the number of elements in the Deque */
    public int size();

    /** Prints out all the items present in the deque */
    public void printDeque();

    /** Returns the item at the given index if it exists.
     * Returns null if there is no such index or if the list is empty.
     */
    public T get(int index);

    /** Adds the item onto the front of the deque */
    public void addFirst(T item);

    /** Adds the item onto the back of the deque */
    public void addLast(T item);

    /** Removes the item from the front of the deque.
     *  Returns null if the deque is empty.
     */
    public T removeFirst();

    /** Removes the item from the back of the deque.
     *  Returns null if the deque is empty.
     */
    public T removeLast();
}
