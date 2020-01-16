package IO;

import Network.Edge;
import Network.EdgeList;
import Network.Node;
import loci.formats.in.SIFReader;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;
import java.util.regex.Pattern;

/**
 * <p>This class is mean to create a FileReader aimed to read file with the format like the adjacent list</p>
 * <br>
 * <p>
 * source Node : target Node(i) weight(i)
 * </p>
 *
 * <p>
 * where the weight means the edge weight between node(i) and source node.
 * </p>
 * <br>
 * <p>
 * refer to http://manual.cytoscape.org/en/stable/Supported_Network_File_Formats.html
 * * && https://github.com/svn2github/cytoscape/blob/master/csplugins/trunk/ucsd/slotia/oiler/src/oiler/io/SIFReader.java
 * </p>
 */
public class AdjListFileReader {

    /**
     * Parses the file given by <code>inputFilePath</code>.
     */
    public static EdgeList read(String inputFilePath) throws IOException, FileNotFoundException {
        return read(new BufferedReader(new FileReader(inputFilePath)), true);
    }

    /**
     * Parses the file given by <code>input</code>. It will
     * close the reader when finished parsing.
     */
    public static EdgeList read(BufferedReader input) throws IOException {
        return read(input, true);
    }

    /**
     * Parses a SIF file.
     *
     * @param input             the reader to read the SIF file from
     * @param closeWhenFinished if true, this method will close
     *                          the reader when finished reading; otherwise, it will
     *                          not close it.
     */
    public static EdgeList read(BufferedReader input, boolean closeWhenFinished) throws IOException {
        EdgeList graph = new EdgeList();
        // matches sequence of one or more whitespace characters.
        Pattern splitter = Pattern.compile("\\s+");
        Vector<String> sifLine = new Vector<>();
        String line;

        while ((line = input.readLine()) != null) {
            String[] tokens = splitter.split(line);
            //  it will be handled in pareLine()
            // which will throw an IOException if not the right case.
            for (String token : tokens) {
                if (token.length() != 0) {
                    sifLine.add(token);
                }
                parseLine(graph, sifLine);
            }
        }
        if (closeWhenFinished) {
            input.close();
        }
        return graph;
    }


    /**
     * <ol>
     *     <li>node1 node2 value12</li>
     *     <li>node2 node3 value23 node4 value24 node5 value25</>
     *     <li>node0 value00</li>
     *  </ol>
     * <p>
     *     The first line identifies two nodes, called node1 and node2, and the weight of the edge between node1 node2. The second line specifies three new nodes, node3, node4, and node5; here “node2” refers to the same node as in the first line.
     *     The second line also specifies three relationships, all of the individual weight and with node2 as the source, with node3, node4, and node5 as the targets.
     *     This second form is simply shorthand for specifying multiple relationships of the same type with the same source node.
     *     The third line indicates how to specify a node that has no relationships with other nodes.
     * </p>
     *
     * @param graph   EdgeList to contain result
     * @param sifLine result very line
     */
    private static void parseLine(EdgeList graph, Vector<String> sifLine) throws IOException {
        // 0 -> nothing changes
        // 2 - > self-loop
        if (sifLine.size() == 2) {
            String name = sifLine.get(0);
            double weight = Double.parseDouble(sifLine.get(1));
            graph.add(new Edge(name, name, weight));
        } else if ((sifLine.size() - 1) % 2 != 0 || sifLine.size() == 1) {
            throw new IOException("The file input format is not correct.");
        } else {
            String srcName = sifLine.get(0);
            // name value ... and it has already checked (sifLine.size()-1) % 2 == 0
            for (int index = 1; index < sifLine.size(); index += 2) {
                // name
                String tgtName = sifLine.get(index);
                if (isNumeric(tgtName)){
                    throw new IOException("The file input format is not correct. Plus: some name-value pairs are incorrect!");
                }
                // value
                String input = sifLine.get(index+1);
                if (!isNumeric(input)) {
                    throw new IOException("The file input format is not correct. Plus: some name-value pairs are incorrect!");
                }
                double weight = Double.parseDouble(input);
                // create edge & add
                graph.add(new Edge(srcName,tgtName,weight));
            }
        }
    }

    public static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

}
