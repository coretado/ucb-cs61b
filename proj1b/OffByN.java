public class OffByN implements CharacterComparator {
    private final int N;

    public OffByN(int inc) {
        this.N = inc;
    }

    @Override
    public boolean equalChars(char x, char y) {
        return Math.abs(x - y) == N;
    }
}
