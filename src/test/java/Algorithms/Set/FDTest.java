package Algorithms.Set;

import org.jgrapht.alg.util.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;

import static org.assertj.core.api.BDDAssertions.then;
import static org.junit.jupiter.api.Assertions.*;

class FDTest {
    FD fdValid;

    @BeforeEach
    void init() throws Exception {
        fdValid = new FD(" AB  -> C ,BCM->DA");
    }

    @Test
    void setRegex() {
        try {
            FD fdInvalid = new FD("AM->");
        } catch (Exception e) {
            String expectedMessage = "Your input is not valid.";
            String actualMessage = e.getMessage();
            assertTrue(actualMessage.contains(expectedMessage));
        }
    }

    @Test
    void functionMap() {
        HashSet<Pair<String, String>> functions = fdValid.getFunctions();
        HashSet<Pair<String, String>> res = new HashSet<>(Arrays.asList(
                new Pair<>("AB", "C"),
                new Pair<>("BCM", "DA")));
        assertEquals(res,functions);
    }

    @Test
    void splitRight() {
        fdValid.splitRight();
        HashSet<Pair<String, String>> functions = fdValid.getFunctions();
        HashSet<Pair<String, String>> res = new HashSet<>(Arrays.asList(
                new Pair<>("AB", "C"),
                new Pair<>("BCM", "D"),
                new Pair<>("BCM", "A")));
        assertEquals(res,functions);

    }

    @Test
    void getAttributesSet() throws Exception {
        fdValid = new FD(" AB  -> C ,AB->M,M->CA");
        HashSet<Character> attributesSet = fdValid.getAttributesSetExceptFunction(new Pair<>("AB", "C"));
        HashSet<Character> res = new HashSet<>(Arrays.asList(
                'A', 'B', 'C','M'));
        assertEquals(res, attributesSet);

    }

    @Test
    void moveRedundant() throws Exception {
        fdValid = new FD(" AB  -> C ,AB->M,M->CA");
        fdValid.splitRight();
        fdValid.removeRedundant();
        HashSet<Pair<String, String>> functions = fdValid.getFunctions();
        HashSet<Pair<String, String>> res = new HashSet<>(Arrays.asList(
                new Pair<>("AB", "M"),
                new Pair<>("M", "C"),
                new Pair<>("M", "A")));
        assertEquals(res, functions);
    }

    @Test
    void moveLeft() throws Exception {
        fdValid = new FD(" A->B,AB  -> C ,AB->M,M->CA");
        fdValid.splitRight();
        fdValid.removeLeft();
        HashSet<Pair<String, String>> functions = fdValid.getFunctions();
        HashSet<Pair<String, String>> res = new HashSet<>(Arrays.asList(
                new Pair<>("A", "B"),
                new Pair<>("A", "C"),
                new Pair<>("M", "A"),
                new Pair<>("M", "C"),
                new Pair<>("A", "M")));
        assertEquals(res, functions);
    }

    @Test
    void finalTest() throws Exception {
        // homework
        // ,CG->B,
        fdValid = new FD(" AB->C,C->A,BC->D,ACD->B,D->E,D->G,BE->C,CG->B,CG->D,CE->A,CE->G");
        fdValid.run();
        HashSet<Pair<String,String>> functions = fdValid.getFunctions();
        HashSet<Pair<String, String>> res = new HashSet<>(Arrays.asList(
                new Pair<>("BE", "C"),
                new Pair<>("AB", "C"),
                new Pair<>("D", "E"),
                new Pair<>("D", "G"),
                new Pair<>("BC", "D"),
                new Pair<>("CE","G"),
                new Pair<>("CG","B"),
                new Pair<>("C","A")));
        assertEquals(res,functions);
    }


}