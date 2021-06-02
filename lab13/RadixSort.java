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
        int index = 1;
        String[] sorted = new String[asciis.length];
        int max = 0;
        for (String s : asciis) {
            if (s.length() > max) {
                max = s.length();
            }
        }
        int[] count = new int[radixSize];
        for (int i = 0; i < max; i += 1) {
            if (i > 0) {
                reset(count);
            }
            for (String ascii : asciis) {
                if (ascii.length() - index >= 0) {
                    count[ascii.charAt(ascii.length() - index)] += 1;
                } else {
                    count[0] += 1;
                }
            }
            for (int ii = 0; ii < count.length - 2; ii += 1) {
                count[ii + 1] = count[ii] + count[ii + 1];
            }
            for (int ii = count.length - 1; ii > 0; ii -= 1) {
                int radixIndex = Math.max(asciis[ii].length() - index, 0);
                sorted[count[radixIndex]] = asciis[ii];
                count[radixIndex] -= 1;
            }
            index += 1;
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
}
