package hw3.hash;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;


public class TestSimpleOomage {

    @Test
    public void testHashCodeDeterministic() {
        SimpleOomage so = SimpleOomage.randomSimpleOomage();
        int hashCode = so.hashCode();
        for (int i = 0; i < 100; i += 1) {
            assertEquals(hashCode, so.hashCode());
        }
    }

    @Test
    public void testHashCodePerfect() {
        SimpleOomage ooA = new SimpleOomage(5, 10, 20);
        SimpleOomage ooB = new SimpleOomage(10, 5, 20);
        assertNotEquals(ooA.hashCode(), ooB.hashCode());
        SimpleOomage ooC = new SimpleOomage(5, 10, 20);
        assertNotEquals(ooA, ooB);
        assertEquals(ooA, ooC);
        assertEquals(ooA.hashCode(), ooC.hashCode());
        SimpleOomage ooD = new SimpleOomage(0, 5, 0);
        SimpleOomage ooE = new SimpleOomage(0, 0, 155);
        assertNotEquals(ooD.hashCode(), ooE.hashCode());
        SimpleOomage ooF = new SimpleOomage(0, 0, 95);
        assertNotEquals(ooD.hashCode(), ooF.hashCode());
        SimpleOomage ooG = new SimpleOomage(0, 0, 185);
        SimpleOomage ooH = new SimpleOomage(0, 205, 0);
        assertNotEquals(ooG.hashCode(), ooH.hashCode());
        SimpleOomage ooI = new SimpleOomage(0, 175, 0);
        SimpleOomage ooJ = new SimpleOomage(10, 0, 85);
        assertNotEquals(ooI.hashCode(), ooJ.hashCode());
    }

    @Test
    public void testEquals() {
        SimpleOomage ooA = new SimpleOomage(5, 10, 20);
        SimpleOomage ooA2 = new SimpleOomage(5, 10, 20);
        SimpleOomage ooB = new SimpleOomage(50, 50, 50);
        assertEquals(ooA, ooA2);
        assertNotEquals(ooA, ooB);
        assertNotEquals(ooA2, ooB);
        assertNotEquals(ooA, "ketchup");
    }

    @Test
    public void testHashCodeAndEqualsConsistency() {
        SimpleOomage ooA = new SimpleOomage(5, 10, 20);
        SimpleOomage ooA2 = new SimpleOomage(5, 10, 20);
        HashSet<SimpleOomage> hashSet = new HashSet<>();
        hashSet.add(ooA);
        assertTrue(hashSet.contains(ooA2));
    }

    @Test
    public void testRandomOomagesHashCodeSpread() {
        List<Oomage> oomages = new ArrayList<>();
        int N = 10000;

        for (int i = 0; i < N; i += 1) {
            oomages.add(SimpleOomage.randomSimpleOomage());
        }

        assertTrue(OomageTestUtility.haveNiceHashCodeSpread(oomages, 10));
    }

    /** Calls tests for SimpleOomage. */
    public static void main(String[] args) {
        jh61b.junit.textui.runClasses(TestSimpleOomage.class);
    }
}
