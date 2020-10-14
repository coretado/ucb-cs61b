package synthesizer;
import org.junit.Test;
import static org.junit.Assert.*;

public class TestArrayRingBuffer {
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

    /** Calls tests for ArrayRingBuffer. */
    public static void main(String[] args) {
        jh61b.junit.textui.runClasses(TestArrayRingBuffer.class);
    }
} 
