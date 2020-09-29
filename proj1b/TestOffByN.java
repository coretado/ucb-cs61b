import org.junit.Test;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class TestOffByN {
    static CharacterComparator offByTwo = new OffByN(2);
    static CharacterComparator offByFive = new OffByN(5);

    @Test
    public void testOffByTwo() {
        assertFalse(offByTwo.equalChars('a', 'd'));
        assertFalse(offByTwo.equalChars('d', 'a'));
        assertFalse(offByTwo.equalChars('%', '&'));
        assertTrue(offByTwo.equalChars('a', 'c'));
        assertTrue(offByTwo.equalChars('%', '\''));
        assertTrue(offByTwo.equalChars('\'', '%'));
    }

    @Test
    public void testOffByFive() {
        assertFalse(offByFive.equalChars('a', 'd'));
        assertFalse(offByFive.equalChars('d', 'd'));
        assertFalse(offByFive.equalChars('%', '!'));
        assertTrue(offByFive.equalChars('a', 'f'));
        assertTrue(offByFive.equalChars('%', '*'));
        assertTrue(offByFive.equalChars('*', '%'));
    }
}
