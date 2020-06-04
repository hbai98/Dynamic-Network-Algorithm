package IO;

import Algorithms.Graph.Network.Edge;
import Algorithms.Graph.Network.EdgeHasSet;
import Algorithms.Graph.Network.AdjList;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Vector;
import java.util.regex.Pattern;

/**
 * <p>This class is mean to create a FileReader aimed to read file with the format like the adjacent list
 *  or create a matrix.
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
public class GraphFileReader {
    private HashSet<String> headSet;
    private HashSet<String> listSet;
    //-------------------------
    private AdjList revAdjList;

    public GraphFileReader(){
        super();
        headSet = new HashSet<>();
        listSet = new HashSet<>();
        revAdjList = new AdjList();
    }

    /**
     * Parses the file given by <code>inputFilePath</code> to EdgeList.
     */
    public EdgeHasSet readToEL(String inputFilePath) throws IOException {

        return readToEL(new BufferedReader(new FileReader(inputFilePath)), true);
    }

    /**
     * Parses the file given by <code>input</code>to EdgeList. It will
     * close the reader when finished parsing.
     */
    public EdgeHasSet readToEL(BufferedReader input) throws IOException {
        return readToEL(input, true);
    }
    //-------------------------AdjNodeList【homoGeneMap】 return type has been added in switch choices------------------------

    public AdjList readToAdjL(String inputFilePath) throws IOException {

        return readToAdjL(new BufferedReader(new FileReader(inputFilePath)), true);
    }

    public AdjList readToAdjL(String inputFilePath,boolean closeWhenFinished) throws IOException {

        return readToAdjL(new BufferedReader(new FileReader(inputFilePath)), closeWhenFinished);
    }
    /**
     * Parses a arrayList format file to ArrayList.
     *
     * @param input             the reader to read the SIF file from
     * @param closeWhenFinished if true, this method will close
     *                          the reader when finished reading; otherwise, it will
     *                          not close it.
     */
    // TODO a bit of duplicated with readToEL()! find a better way to solve!
    private AdjList readToAdjL(BufferedReader input, boolean closeWhenFinished) throws IOException{
        init();
        AdjList graph = new AdjList();
        // matches sequence of one or more whitespace characters.
        Pattern splitter = Pattern.compile("\\s+");
        Vector<String> sifLine = new Vector<>();
        String line;

        while ((line = input.readLine()) != null) {
            String[] tokens = splitter.split(line);
            if(tokens.length == 0) continue;
            //  it will be handled in pareLine()
            // which will throw an IOException if not the right case.
            for (String token : tokens) {
                if (token.length() != 0) {
                    sifLine.add(token);
                }
            }
            parseLine(graph, sifLine);
        }
        if (closeWhenFinished) {
            input.close();
        }
        return graph;
    }

    private void init() {
        // clean hashSet
        headSet = new HashSet<>();
        listSet = new HashSet<>();
        // clean rev
        revAdjList = new AdjList();
    }

    /**
     * Parses a arrayList format file to EdgeList.
     *
     * @param input             the reader to read the SIF file from
     * @param closeWhenFinished if true, this method will close
     *                          the reader when finished reading; otherwise, it will
     *                          not close it.
     */
    public EdgeHasSet readToEL(BufferedReader input, boolean closeWhenFinished) throws IOException {
        EdgeHasSet graph = new EdgeHasSet();
        // matches sequence of one or more whitespace characters.
        Pattern splitter = Pattern.compile("\\s+");
        Vector<String> sifLine = new Vector<>();
        String line;

        while ((line = input.readLine()) != null) {
            String[] tokens = splitter.split(line);
            if(tokens.length == 0) continue;
            //  it will be handled in pareLine()
            // which will throw an IOException if not the right case.
            for (String token : tokens) {
                if (token.length() != 0) {
                    sifLine.add(token);
                }
            }
            parseLine(graph, sifLine);
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
     *     <li>node
     *  </ol>
     * <p>
     *     The first line identifies two nodes, called node1 and node2, and the weight of the edge between node1 node2. The second line specifies three new nodes, node3, node4, and node5; here “node2” refers to the same node as in the first line.<br/>
     *     The second line also specifies three relationships, all of the individual weight and with node2 as the source, with node3, node4, and node5 as the targets.
     *     This second form is simply shorthand for specifying multiple relationships of the same type with the same source node.
     *     The third line indicates how to specify a node that has no relationships with other nodes.
     * </p>
     *
     *
     * @param graph   EdgeList to contain result
     * @param sifLine result very line
     */
    private void parseLine(EdgeHasSet graph, Vector<String> sifLine) throws IOException {
        int sifSize = sifLine.size();
        if(sifSize == 0){
            throw new IOException("Nothing has been input!.");
        }
        // 2 - > self-loop
        if (sifSize == 2) {
            String name = sifLine.get(0);
            if (isNumeric(sifLine.get(1))){
                throw new IOException("The file input format is not correct. Plus: some name-value pairs are incorrect!");
            }
            double weight = Double.parseDouble(sifLine.get(1));
            graph.add(new Edge(name, name, weight));
        } else if ((sifSize - 1) % 2 != 0 || sifSize == 1) {
            throw new IOException("The file input format is not correct.");
        } else {
            String srcName = sifLine.get(0);
            // name value ... and it has already checked (sifSize-1) % 2 == 0
            for (int index = 1; index < sifSize ; index += 2) {
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
                addToNodeList(srcName,tgtName);


            }
        }
        sifLine.clear();
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
     * @param graph   EdgeList to contain result
     * @param sifLine result very line
     */
    private void parseLine(AdjList graph, Vector<String> sifLine) throws IOException {
        int sifSize = sifLine.size();
        if(sifSize == 0){
            throw new IOException("Nothing has been input!.");
        }
        if (sifSize == 2) {
            String name = sifLine.get(0);
            if (isNumeric(sifLine.get(1))){
                throw new IOException("The file input format is not correct. Plus: some name-value pairs are incorrect!");
            }
            double weight = Double.parseDouble(sifLine.get(1));
            graph.addOneNode(name,name,weight);
        } else if ((sifSize  - 1) % 2 != 0 || sifSize  == 1) {
            throw new IOException("The file input format is not correct.");
        } else {
            String srcName = sifLine.get(0);
            // name value ... and it has already checked (sifSize -1) % 2 == 0
            for (int index = 1; index < sifSize ; index += 2) {
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
                // Contain the lexicographical order to prevent the case of same edges.
                graph.sortAddOneNode(srcName,tgtName,weight);
                addToNodeList(srcName,tgtName);
                revAdjList.sortAddOneNode(tgtName,srcName,weight);
            }
        }
        sifLine.clear();
    }






    public boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void addToNodeList(String srcName,String tgtName){
        // record to nodeLists
        headSet.add(srcName);
        listSet.add(tgtName);
    }
    //------------------PUBLIC-----------------------------

    /**
     * Please execute read instruction before calling this method.
     * @return HashSet of nodes' names in graph_1
     */
    public HashSet<String> getHeadSet() {
        assert(headSet !=null);
        return headSet;
    }
    /**
     * Please execute read instruction before calling this method.
     * @return HashSet of nodes' names in graph_2
     */
    public HashSet<String> getListSet() {
        assert(listSet !=null);
        return listSet;
    }

    /**
     * Only used for
     * NOTICE: revList will not be synchronized.
     */
    public AdjList getRevAdjList() {
        assert (revAdjList!=null);
        return revAdjList;
    }

}
