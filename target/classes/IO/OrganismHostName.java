package IO;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * |https://www.uniprot.org/help/taxonomy
 * a number of species commonly encountered in UniProtKB,
 * we use self-explanatory codes. There are 16 of those codes:
 * <p>
 * <p>
 * HUMAN for Homo sapiens
 * RAT for Rat
 * MOUSE for Mouse
 * YEAST for Baker's yeast (Saccharomyces cerevisiae)
 * WHEAT for Wheat (Triticum aestivum)
 * SHEEP for Sheep
 * PIG for Pig
 * PEA for Garden pea (Pisum sativum)
 * MAIZE for Maize (Zea mays)
 * HORSE for Horse
 * VIRUS
 */
public enum OrganismHostName {
    HUMAN("HUMAN"), RAT("RAT"), YEAST("YEAST"),
    WHEAT("WHEAT"), SHEEP("SHEEP"),
    PIG("PIG"), PEA("PEA"), MAIZE("MAIZE"), HORSE("HORSE");

    public String name;

    public static ArrayList<OrganismHostName> getAll() {
        return new ArrayList<>(Arrays.asList(HUMAN, RAT, YEAST,
                WHEAT, SHEEP,
                PIG, PEA, MAIZE, HORSE));
    }

    OrganismHostName(String s) {
        this.name = s;
    }
}
