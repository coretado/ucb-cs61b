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
        if (word == null) {
            return false;
        }
        if (word.length() < 2) {
            return true;
        }
        return isPalindromeHelper(word, wordToDeque(word));
    }

    private boolean isPalindromeHelper(String word, Deque<Character> load) {
        if (word.length() < 2) {
            return true;
        } else {
            if (!load.removeLast().equals(word.charAt(0))) {
                return false;
            }
            return isPalindromeHelper(word.substring(1, word.length() - 1), load);
        }
    }

    private boolean isPalindromeHelper(String word, Deque<Character> load, CharacterComparator cc) {
        if (word.length() < 2) {
            return true;
        } else {
            if (!cc.equalChars(load.removeLast(), word.charAt(0))) {
                return false;
            }
            return isPalindromeHelper(word.substring(1, word.length() - 1), load, cc);
        }
    }

    public boolean isPalindrome(String word, CharacterComparator cc) {
        if (word == null) {
            return false;
        }
        if (word.length() < 2) {
            return true;
        }
        return isPalindromeHelper(word, wordToDeque(word), cc);
    }
}
