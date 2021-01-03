package hw3.hash;

import java.util.List;

public class OomageTestUtility {
    public static boolean haveNiceHashCodeSpread(List<Oomage> oomages, int M) {
        int[] bucketFill = new int[M];
        double N = oomages.size();
        oomages.forEach(oomage -> bucketFill[calcBucketPos(oomage.hashCode(), M)] += 1);
        for (int i = 0; i < M; i += 1) {
            double size = bucketFill[i];
            double lowerBound = N / 50;
            double upperBound = N / 2.5;
            if (size < lowerBound || size > upperBound) {
                return false;
            }
        }
        return true;
    }

    private static int calcBucketPos(int hashcode, int M) {
        return (hashcode & 0x7FFFFFFF) % M;
    }
}
