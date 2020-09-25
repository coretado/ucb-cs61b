public class Palindrome {
    public Deque<Character> wordToDeque(String word) {
        Deque<Character> palindrome = new LinkedListDeque<>();
        int wordLength = word.length();
        for (int i = 0; i < wordLength; i++) {
            palindrome.addLast(word.charAt(i));
        }
        return palindrome;
    }

    public boolean isPalindrome(String word) {
        if (word == null) return false;

        Deque<Character> load = wordToDeque(word);
        int last = word.length() - 1;
        for ( ; last > 0; last--) {
            if (!load.removeFirst().equals(word.charAt(last))) {
                return false;
            }
        }

        return true;
    }
}
