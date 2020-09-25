import org.junit.Test;
import static org.junit.Assert.*;

public class TestPalindrome {
    // You must use this palindrome, and not instantiate
    // new Palindromes, or the autograder might be upset.
    static Palindrome palindrome = new Palindrome();

    @Test
    public void testWordToDeque() {
        Deque d = palindrome.wordToDeque("persiflage");
        String actual = "";
        for (int i = 0; i < "persiflage".length(); i++) {
            actual += d.removeFirst();
        }
        assertEquals("persiflage", actual);
    }

    @Test
    public void testEmptyPalindrome() {
        assertTrue(palindrome.isPalindrome(""));
    }

    @Test
    public void testOnePalindrome() {
        assertTrue(palindrome.isPalindrome("a"));
    }

    @Test
    public void testVariousWords() {
        assertFalse(palindrome.isPalindrome(null));
        assertTrue(palindrome.isPalindrome("racecar"));
        assertTrue(palindrome.isPalindrome("tattarrattat"));
        assertFalse(palindrome.isPalindrome("Racecar"));
        assertFalse(palindrome.isPalindrome("entry"));
        assertFalse(palindrome.isPalindrome("petrichor"));
    }
}
