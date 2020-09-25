public class Palindrome {
    public Deque<Character> wordToDeque(String word) {
        Deque<Character> palindrome = new LinkedListDeque<>();
        int wordLength = word.length();
        for (int i = 0; i < wordLength; i++) {
            palindrome.addLast(word.charAt(i));
        }
        return palindrome;
    }
}
