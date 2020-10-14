package synthesizer;
import org.junit.Test;
import static org.junit.Assert.*;

public class TestArrayRingBuffer {
    private static final String OVERFLOW_MESSAGE = "Ring Buffer Overflow";
    private static final String UNDERFLOW_MESSAGE = "Ring Buffer Underflow";
    private static final String EMPTY_MESSAGE = "Ring Buffer Empty";

    @Test
    public void testLoadAndUnload() {
        ArrayRingBuffer<Integer> arb = new ArrayRingBuffer<>(8);
        assertEquals(arb.capacity, 8);
        assertEquals(arb.fillCount, 0);

        for (int i = 0; i < 8; i += 1) {
            arb.enqueue(i);
        }
        assertEquals(arb.fillCount, 8);

        for (int i = 0; i < 8; i += 1) {
            assertEquals((int) arb.peek(), i);
            assertEquals((int) arb.dequeue(), i);
        }

        // testing if wrap around worked correctly
        for (int i = 0; i < 8; i += 1) {
            arb.enqueue(i);
        }
        assertEquals(arb.fillCount, 8);

        for (int i = 0; i < 8; i += 1) {
            assertEquals((int) arb.peek(), i);
            assertEquals((int) arb.dequeue(), i);
        }
    }

    @Test
    public void testOverflowException() {
        ArrayRingBuffer<Integer> arb = new ArrayRingBuffer<>(10);
        for (int i = 0; i < 10; i += 1) {
            arb.enqueue(i);
        }
        assertEquals(arb.fillCount(), 10);
        assertEquals((int) arb.peek(), 0);
        try {
            arb.enqueue(10);
        } catch (RuntimeException e) {
            assertEquals(e.getClass(), RuntimeException.class);
            assertEquals(e.getMessage(), OVERFLOW_MESSAGE);
        }
    }

    @Test
    public void testUnderflowException() {
        ArrayRingBuffer<Integer> arb = new ArrayRingBuffer<>(10);
        for (int i = 0; i < 10; i += 1) {
            arb.enqueue(i);
        }
        for (int i = 0; i < 10; i += 1) {
            assertEquals((int) arb.dequeue(), i);
        }
        try {
            arb.dequeue();
        } catch (RuntimeException e) {
            assertEquals(e.getClass(), RuntimeException.class);
            assertEquals(e.getMessage(), UNDERFLOW_MESSAGE);
        }
    }

    @Test
    public void testPeekException() {
        ArrayRingBuffer<Integer> arb = new ArrayRingBuffer<>(10);
        assertEquals(arb.fillCount(), 0);
        arb.enqueue(1);
        assertEquals(arb.fillCount(), 1);
        assertEquals((int) arb.peek(), 1);
        assertEquals((int) arb.dequeue(), 1);
        try {
            arb.peek();
        } catch (RuntimeException e) {
            assertEquals(e.getClass(), RuntimeException.class);
            assertEquals(e.getMessage(), EMPTY_MESSAGE);
        }
    }

    /** Calls tests for ArrayRingBuffer. */
    public static void main(String[] args) {
        jh61b.junit.textui.runClasses(TestArrayRingBuffer.class);
    }
} 
