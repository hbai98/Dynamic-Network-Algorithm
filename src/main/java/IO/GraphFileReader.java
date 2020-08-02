package IO;

import DS.Network.UndirectedGraph;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;

import static org.apache.commons.lang3.StringUtils.isNumeric;



/**
 * <p>This class is mean to create a FileReader aimed to read file with the format like the adjacent list
 * or create a matrix.
 * </p>
 * <br>
 * <p>
 * source Node : target Node(i) weight(i)
 * </p>
 *
 * <p>
 * where the weight means the edge weight between node(i) and source node.
 * </p>
 * <br>
 */
@SuppressWarnings("unchecked")
public class GraphFileReader<V, E> extends AbstractFileReader {
    private UndirectedGraph<V, E> udG;
    Class<V> vertexClass;
    Class<E> edgeClass;

    public GraphFileReader(String inputFilePath, Class<V> vertexClass, Class<E> edgeClass) throws FileNotFoundException {
        this.vertexClass = vertexClass;
        this.edgeClass = edgeClass;
        udG = new UndirectedGraph<>(edgeClass);
        setInputFilePath(inputFilePath);
    }

    private UndirectedGraph<V, E> readToUndirectedGraph(boolean closeWhenFinished) throws IOException {
        // matches sequence of one or more whitespace characters.
        setSplitter("\\s+");
        Vector<V> sifLine = new Vector<>();
        String line;
        while ((line = reader.readLine()) != null) {
            String[] tokens = splitter.split(line);
            if (tokens.length == 0) continue;
            //  it will be handled in pareLine()
            // which will throw an IOException if not the right case.
            for (String token : tokens) {
                if (token.length() != 0) {
                    sifLine.add((V) token);
                }
            }
            parseForGraph(udG, sifLine);
            // clean for each line
            sifLine.clear();
        }
        if (closeWhenFinished) {
            reader.close();
        }
        return udG;
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
     * <p>NOTICE:add() -> use sortAdd()</p>
     *
     * @param simMat  EdgeList to contain result
     * @param sifLine result very line
     */
    private void parseForSimMat(SimMat simMat, Vector<String> sifLine) throws IOException {

        int sifSize = sifLine.size();
        if (sifSize == 0) {
            throw new IOException("Nothing has been input!.");
        }
        if (sifSize == 2) {
            // node1 node1 val1 // a circle
            String name = sifLine.get(0);
            if (isNumeric(sifLine.get(1))) {
                double weight = Double.parseDouble(sifLine.get(1));
                if (weight != 0) {
                    simMat.put(name, name, weight);
                }
                // record if required
                record(name, name, weight);
            }
            // node1 node2
            else {
                String targetName = sifLine.get(1);
                if (isIdentifier(targetName)) {
                    // record if required
                    record(name, targetName, 0.);
                } else {
                    throw new IOException("nodes' name are not correct.");
                }
            }
        } else if ((sifSize - 1) % 2 != 0 || sifSize == 1) {
            throw new IOException("The file reader format is not correct.");
        } else {
            V src = sifLine.get(0);
            for (int index = 1; index < sifSize; index += 2) {
                // name
                V tgt = sifLine.get(1);
                V val = sifLine.get(index + 1);
                graph.addVertex(src);
                graph.addVertex(tgt);
                graph.addEdge(src, tgt);
                if (!isNumeric((String) val)) {
                    graph.setEdgeWeight(src, tgt, Double.parseDouble((String) val));
                } else {
                    throw new IOException("The file reader format is not correct. Plus: some name-value pairs are incorrect!");
                }
            }
        }
    }

}
