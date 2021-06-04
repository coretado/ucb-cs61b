import java.util.Arrays;

/**
 * Class for doing Radix sort
 *
 * @author Akhil Batra, Alexander Hwang
 *
 */
public class RadixSort {
    private static final int radixSize = 256;

    /**
     * Does LSD radix sort on the passed in array with the following restrictions:
     * The array can only have ASCII Strings (sequence of 1 byte characters)
     * The sorting is stable and non-destructive
     * The Strings can be variable length (all Strings are not constrained to 1 length)
     *
     * @param asciis String[] that needs to be sorted
     *
     * @return String[] the sorted array
     */
    public static String[] sort(String[] asciis) {
        // TODO: Implement LSD Sort
        int index = 0;
        int length = asciis.length;
        String[] sorted = new String[length];
        System.arraycopy(asciis, 0, sorted, 0, length);
        int max = 0;

        for (String s : asciis) {
            if (s.length() > max) {
                max = s.length();
            }
        }

        int[] count = new int[radixSize];
        int[] start = new int[radixSize];

        for (int i = 0; i < max; i += 1) {
            String[] hold = new String[length];
            System.arraycopy(sorted, 0, hold, 0, length);
            if (i > 0) {
                reset(count);
            }

            for (String h : hold) {
                if (index < h.length()) {
                    count[h.charAt(index)] += 1;
                } else {
                    count[0] += 1;
                }
            }

            int pos = 0;
            for (int ii = 0; ii < radixSize; ii += 1) {
                start[ii] = pos;
                pos += count[ii];
            }

            for (String s : sorted) {
                // this has potential for trouble; I need to find out what the equivalent
                // of 0 is for a Radix-256
                int radixIndex = index < s.length() ? (int) s.charAt(index) : 0;
                hold[start[radixIndex]] = s;
                start[radixIndex] += 1;
            }
            index += 1;
            System.arraycopy(hold, 0, sorted, 0, length);
        }
        return sorted;
    }

    private static void reset(int[] count) {
        Arrays.fill(count, 0);
    }

    /**
     * LSD helper method that performs a destructive counting sort the array of
     * Strings based off characters at a specific index.
     * @param asciis Input array of Strings
     * @param index The position to sort the Strings on.
     */
    private static void sortHelperLSD(String[] asciis, int index) {
        // Optional LSD helper method for required LSD radix sort
        return;
    }

    /**
     * MSD radix sort helper function that recursively calls itself to achieve the sorted array.
     * Destructive method that changes the passed in array, asciis.
     *
     * @param asciis String[] to be sorted
     * @param start int for where to start sorting in this method (includes String at start)
     * @param end int for where to end sorting in this method (does not include String at end)
     * @param index the index of the character the method is currently sorting on
     *
     **/
    private static void sortHelperMSD(String[] asciis, int start, int end, int index) {
        // Optional MSD helper method for optional MSD radix sort
        return;
    }

    public static void main(String[] args) {
        String[] test = new String[]{"the", "quick", "brown", "fox", "jumped", "over", "the", "lazy", "dog"};
        String[] sorted = sort(test);
        for (String s : sorted) {
            System.out.println(s);
        }
    }
}
