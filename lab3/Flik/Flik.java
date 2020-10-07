import static org.junit.Assert.*;
import org.junit.Test;

/** An Integer tester created by Flik Enterprises. */
public class Flik {
    /**
     * The bug in this code is that Integer classes were being compared using
     * a == instead of int primitives. Checking for == equality between two
     * classes will inevitably fail because you are comparing them by
     * reference instead of value. You could fix this code by changing the
     * function signature, but that might break the code of other users of
     * this library in this hypothetical situation. So the best solution
     * might be to use use Integer.equals() instead of changing the function
     * signature to (int a, int b). Why it always fails specifically at the
     * value of 128, I have no idea.
     */
    public static boolean isSameNumber(Integer a, Integer b) {
        return a.equals(b);
    }

    @Test
    public void testFlikEasy() {
        assertTrue(isSameNumber(2, 2));
        assertTrue(isSameNumber((int) 2, (int) 2));
        assertTrue(isSameNumber(Integer.valueOf(2), Integer.valueOf(2)));
        assertFalse(isSameNumber(2, 3));
        assertFalse(isSameNumber((int) 2, (int) 3));
        assertFalse(isSameNumber(Integer.valueOf(2), Integer.valueOf(3)));
    }

}
