import org.junit.Test;
import static org.junit.Assert.*;

public class TestPalindrome {
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
    public void testOneCharacterPalindrome() {
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

    @Test
    public void testEmptyOffByOnePalindrome() {
        CharacterComparator offByOne = new OffByOne();
        assertTrue(palindrome.isPalindrome("", offByOne));
    }

    @Test
    public void testOneCharacterOffByOnePalindrome() {
        CharacterComparator offByOne = new OffByOne();
        assertTrue(palindrome.isPalindrome("a", offByOne));
    }

    @Test
    public void testVariousOffByOneWords() {
        CharacterComparator offByOne = new OffByOne();
        assertFalse(palindrome.isPalindrome(null, offByOne));
        assertTrue(palindrome.isPalindrome("flake", offByOne));
        assertTrue(palindrome.isPalindrome("qabecbr", offByOne));
        assertFalse(palindrome.isPalindrome("qacecaR", offByOne));
        assertFalse(palindrome.isPalindrome("Dntry", offByOne));
        assertFalse(palindrome.isPalindrome("qetrichos"));
    }
}
