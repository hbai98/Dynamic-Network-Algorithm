package BLAST;

import java.io.File;
import java.io.IOException;

/**
 * Reference : https://www.biostars.org/p/2887/
 * This class is used to do protein blastp to get E-VALUE.
 * <br>
 * <p>
 *     use ProcessBuilder to send a query to blast and use javax.xml.bind.JAXBContext and
 *     javax.xml.bind.Unmarshaller to parse the XML result without any external library.
 * </p>
 * <br>
 * <p>
 *     Requirement: download the BLAST in computers before using this class.
 * </p>
 */
public class LocalBlast {
    private final String blastAllPath = "E:\\environment\\blast-2.10.0+\\bin";
    private final String blastDB = "";
    protected void runBlast() throws IOException {
        File fasta = File.createTempFile("blastInput",".fa");
        File blast = File.createTempFile("blastResult",".xml");
    }
}
