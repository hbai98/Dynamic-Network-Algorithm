package Internal.Algorithms.IO;

import Internal.Algorithms.Graph.Network.AbstractAdjList;
import Internal.Algorithms.Graph.Utils.AdjList.DirectedGraph;
import Internal.Algorithms.Graph.Utils.AdjList.SimList;
import Internal.Algorithms.Graph.Utils.AdjList.UndirectedGraph;
import Internal.Algorithms.Graph.Utils.SimMat;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
public class GraphFileReader extends AbstractFileReader {
    private HashSet<String> sourceNodesSet;
    private HashSet<String> targetNodesSet;
    // get neighbors information
    private HashMap<String, HashSet<String>> graphNeighbors;
    // non zeros
    private HashMap<String, HashSet<String>> nonZerosMap;
    //--------------------
    private boolean recordNeighbors;
    private boolean recordSrcAndTarget;
    private boolean recordNonZeros;
    private boolean updateNonZerosForRow;
    // for a row

    public GraphFileReader(boolean recordSrcAndTarget, boolean getNeighbors, boolean recordNonZeros) {
        super();
        this.recordSrcAndTarget = recordSrcAndTarget;
        this.recordNeighbors = getNeighbors;
        this.recordNonZeros = recordNonZeros;
    }
    //-------------------------AdjNodeList【homoGeneMap】 return type has been added in switch choices------------------------

    public DirectedGraph readToDirectedGraph(String inputFilePath) throws IOException {
        return readToDirectedGraph(new BufferedReader(new FileReader(inputFilePath)), true);
    }

    public DirectedGraph readToDirectedGraph(String inputFilePath, boolean closeWhenFinished) throws IOException {
        return readToDirectedGraph(new BufferedReader(new FileReader(inputFilePath)), closeWhenFinished);
    }

    public UndirectedGraph readToUndirectedGraph(String inputFilePath, boolean closeWhenFinished) throws IOException {
        return readToUndirectedGraph(new BufferedReader(new FileReader(inputFilePath)), closeWhenFinished);
    }

    public SimMat readToSimMat(String inputFilePath, HashSet<String> graph1, HashSet<String> graph2, boolean closeWhenFinished) throws IOException {
        return readToSimMat(new BufferedReader(new FileReader(inputFilePath)), graph1, graph2, closeWhenFinished);
    }

    public SimMat readToSimMat(File file, HashSet<String> graph1, HashSet<String> graph2, boolean closeWhenFinished) throws IOException {
        return readToSimMat(new BufferedReader(new FileReader(file)), graph1, graph2, closeWhenFinished);
    }

    public SimList readToSimList(String inputFilePath) throws IOException {

        return readToSimList(new BufferedReader(new FileReader(inputFilePath)), true);
    }

    public SimList readToSimList(String inputFilePath, boolean closeWhenFinished) throws IOException {

        return readToSimList(new BufferedReader(new FileReader(inputFilePath)), closeWhenFinished);
    }
    public SimMat readToSimMatExcel(File file, HashSet<String> graph1, HashSet<String> graph2) throws IOException {
        return readToSimMatExcel(new FileInputStream(file),graph1,graph2);
    }
    public SimMat readToSimMatExcel(String filePath, HashSet<String> graph1, HashSet<String> graph2) throws IOException {
        return readToSimMatExcel(new FileInputStream(new File(filePath)),graph1,graph2);
    }
    private SimMat readToSimMatExcel(FileInputStream fis, HashSet<String> graph1, HashSet<String> graph2) throws IOException {
        XSSFWorkbook wb = new XSSFWorkbook(fis);
        SimMat simMat = new SimMat(graph1,graph2);
        XSSFSheet sheet = wb.getSheetAt(0);
        for (Row row : sheet) {   //iteration over row using for each loop
            Vector<String> sifLine = new Vector<>();
            for (Cell cell : row)    //iteration over cell using for each loop
            {
                sifLine.add(cell.getStringCellValue());
                sifLine.add(cell.getStringCellValue());
                sifLine.add(cell.getStringCellValue());
            }
            parseForSimMatExcel(simMat,sifLine);
            cleanLine();
        }
        return simMat;
    }
    /**
     * Only for name1 name2 and blastp value
     *
     * @param simMat  result
     * @param sifLine line data
     */
    private void parseForSimMatExcel(SimMat simMat, Vector<String> sifLine) throws IOException {
        init();
        int sifSize = sifLine.size();
        if (sifSize != 3) {
            throw new IOException("The file input format is not correct.");
        }
        double val = Double.parseDouble(sifLine.get(2));
        double eValue = 1 / (1 - 1 / Math.log(val));
        simMat.put(sifLine.get(0), sifLine.get(1), eValue);
    }

    /**
     * Parses a arrayList format file to ArrayList.
     *
     * @param input             the reader to read the SIF file from
     * @param closeWhenFinished if true, this method will close
     *                          the reader when finished reading; otherwise, it will
     *                          not close it.
     */
    private SimList readToSimList(BufferedReader input, boolean closeWhenFinished) throws IOException {
        init();
        SimList simList = new SimList();
        // matches sequence of one or more whitespace characters.
        setSplitter("\\s+");
        Vector<String> sifLine = new Vector<>();
        String line;
        while ((line = input.readLine()) != null) {
            String[] tokens = splitter.split(line);
            if (tokens.length == 0) continue;
            //  it will be handled in pareLine()
            // which will throw an IOException if not the right case.
            for (String token : tokens) {
                if (token.length() != 0) {
                    sifLine.add(token);
                }
            }
            parseForSimList(simList, sifLine);
            // clean for each line
            cleanLine();
        }
        if (closeWhenFinished) {
            input.close();
        }
        // simList setting up

        return simList;
    }

    /**
     * include checking whether simMat contains all nodes
     */
    private SimMat readToSimMat(BufferedReader input, HashSet<String> graph1Nodes, HashSet<String> graph2Nodes, boolean closeWhenFinished) throws IOException {
        init();
        SimMat simMat = new SimMat(graph1Nodes, graph2Nodes);
        simMat.setNonZerosIndexMap(nonZerosMap);
        // matches sequence of one or more whitespace characters.
        setSplitter("\\s+");
        Vector<String> sifLine = new Vector<>();
        String line;
        while ((line = input.readLine()) != null) {
            String[] tokens = splitter.split(line);
            if (tokens.length == 0) continue;
            //  it will be handled in pareLine()
            // which will throw an IOException if not the right case.
            for (String token : tokens) {
                if (token.length() != 0) {
                    sifLine.add(token);
                }
            }
            parseForSimMat(simMat, sifLine);
            // clean for each line
            cleanLine();
        }
        if (closeWhenFinished) {
            input.close();
        }
        // set up simMat
        if (recordNonZeros) {
            simMat.setNonZerosIndexMap(nonZerosMap);
        }
        return simMat;
    }


    /**
     * Parses a arrayList format file to ArrayList.
     *
     * @param input             the reader to read the SIF file from
     * @param closeWhenFinished if true, this method will close
     *                          the reader when finished reading; otherwise, it will
     *                          not close it.
     */
    private DirectedGraph readToDirectedGraph(BufferedReader input, boolean closeWhenFinished) throws IOException {
        init();
        DirectedGraph directedGraph = new DirectedGraph();
        // matches sequence of one or more whitespace characters.
        setSplitter("\\s+");
        Vector<String> sifLine = new Vector<>();
        String line;
        while ((line = input.readLine()) != null) {
            String[] tokens = splitter.split(line);
            if (tokens.length == 0) continue;
            //  it will be handled in pareLine()
            // which will throw an IOException if not the right case.
            for (String token : tokens) {
                if (token.length() != 0) {
                    sifLine.add(token);
                }
            }
            parseForGraph(directedGraph, sifLine);
            // clean for each line
            cleanLine();
        }
        if (closeWhenFinished) {
            input.close();
        }
        // set up graph
        directedGraph.setNeighborMap(graphNeighbors);
        return directedGraph;
    }

    /**
     * Parses a arrayList format file to ArrayList.
     *
     * @param input             the reader to read the SIF file from
     * @param closeWhenFinished if true, this method will close
     *                          the reader when finished reading; otherwise, it will
     *                          not close it.
     */
    private UndirectedGraph readToUndirectedGraph(BufferedReader input, boolean closeWhenFinished) throws IOException {
        init();
        UndirectedGraph undGraph = new UndirectedGraph();
        // matches sequence of one or more whitespace characters.
        setSplitter("\\s+");
        Vector<String> sifLine = new Vector<>();
        String line;
        while ((line = input.readLine()) != null) {
            String[] tokens = splitter.split(line);
            if (tokens.length == 0) continue;
            //  it will be handled in pareLine()
            // which will throw an IOException if not the right case.
            for (String token : tokens) {
                if (token.length() != 0) {
                    sifLine.add(token);
                }
            }
            parseForGraph(undGraph, sifLine);
            // clean for each line
            cleanLine();
        }
        if (closeWhenFinished) {
            input.close();
        }
        return undGraph;
    }

    /**
     * <ol>
     *     <li>node1 node2 value12</li>
     *     <li>node2 node3 value23 node4 value24 node5 value25</>
     *     <li>node0 value00</li>
     *     <li>node0 ...</li>
     *  </ol>
     * <p>
     *     The first line identifies two nodes, called node1 and node2, and the weight of the edge between node1 node2. The second line specifies three new nodes, node3, node4, and node5; here “node2” refers to the same node as in the first line.
     *     The second line also specifies three relationships, all of the individual weight and with node2 as the source, with node3, node4, and node5 as the targets.
     *     This second form is simply shorthand for specifying multiple relationships of the same type with the same source node.
     *     The third line indicates how to specify a node that has no relationships with other nodes.
     *     The forth one represents only nodes without value.
     * </p>
     *
     * <p>NOTICE:add() -> use sortAdd()</p>
     *
     * @param graph   result
     * @param sifLine result very line
     */
    private void parseForGraph(AbstractAdjList graph, Vector<String> sifLine) throws IOException {
        int sifSize = sifLine.size();
        if (sifSize == 0) {
            throw new IOException("Nothing has been input!.");
        }
        if (sifSize == 2) {
            // node1 node1 val1 // a circle
            String name = sifLine.get(0);
            if (isNumeric(sifLine.get(1))) {
                double weight = Double.parseDouble(sifLine.get(1));
                graph.addOneNode(name, name, weight);
                // record if required
                record(name, name, weight);
            }
            // node1 node2
            else {
                String targetName = sifLine.get(1);
                if (isIdentifier(targetName)) {
                    graph.addOneNode(name, targetName, 0.);
                    // record if required
                    record(name, targetName, 0.);
                } else {
                    throw new IOException("nodes' name are not correct.");
                }
            }
        } else if ((sifSize - 1) % 2 != 0 || sifSize == 1) {
            throw new IOException("The file input format is not correct.");
        } else {
            String srcName = sifLine.get(0);
            // name value ... and it has already checked (sifSize -1) % 2 == 0
            for (int index = 1; index < sifSize; index += 2) {
                // name
                String tgtName = sifLine.get(index);
                if (!isIdentifier(tgtName)) {
                    throw new IOException("nodes' name are not correct.");
                }
                // value
                String input = sifLine.get(index + 1);
                if (!isNumeric(input)) {
                    // all nodes name
                    if (isIdentifier(tgtName)) {
                        graph.addOneNode(srcName, tgtName, 0.);
                        // record if required
                        record(srcName, tgtName, 0.);
                        continue;
                    } else {
                        throw new IOException("The file input format is not correct. Plus: some name-value pairs are incorrect!");
                    }
                }
                double weight = Double.parseDouble(input);
                graph.addOneNode(srcName, tgtName, weight);
                // record if required
                record(srcName, tgtName, weight);
            }
        }
        sifLine.clear();
    }


    private void cleanLine() {
    }

    private void init() {
        sourceNodesSet = null;
        targetNodesSet = null;
        graphNeighbors = null;
        nonZerosMap = null;
        // row index
        // clean or init
        if (recordSrcAndTarget) {
            sourceNodesSet = new HashSet<>();
            targetNodesSet = new HashSet<>();
        }
        if (recordNeighbors) {
            graphNeighbors = new HashMap<>();
        }
        if (recordNonZeros) {
            nonZerosMap = new HashMap<>();
            updateNonZerosForRow = true;
        }
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
     * @param simList EdgeList to contain result
     * @param sifLine result very line
     */
    private void parseForSimList(SimList simList, Vector<String> sifLine) throws IOException {
        // row index

        int sifSize = sifLine.size();
        if (sifSize == 0) {
            throw new IOException("Nothing has been input!.");
        }
        if (sifSize == 2) {
            // node1 node1 val1 // a circle
            String name = sifLine.get(0);
            if (isNumeric(sifLine.get(1))) {
                double weight = Double.parseDouble(sifLine.get(1));
                simList.sortAddOneNode(name, name, weight);
                // record if required
                record(name, name, weight);
            }
            // node1 node2
            else {
                String targetName = sifLine.get(1);
                if (isIdentifier(targetName)) {
                    simList.sortAddOneNode(name, targetName, 0);
                    // record if required
                    record(name, targetName, 0.);
                } else {
                    throw new IOException("nodes' name are not correct.");
                }
            }
        } else if ((sifSize - 1) % 2 != 0 || sifSize == 1) {
            throw new IOException("The file input format is not correct.");
        } else {
            String srcName = sifLine.get(0);
            // name value ... and it has already checked (sifSize -1) % 2 == 0
            for (int index = 1; index < sifSize; index += 2) {
                // name
                String tgtName = sifLine.get(index);
                if (!isIdentifier(tgtName)) {
                    throw new IOException("nodes' name are not correct.");
                }

                // value
                String input = sifLine.get(index + 1);
                if (!isNumeric(input)) {
                    throw new IOException("The file input format is not correct. Plus: some name-value pairs are incorrect!");
                }
                double weight = Double.parseDouble(input);
                // create edge & add
                // Contain the lexicographical order to prevent the case of same edges.
                simList.sortAddOneNode(srcName, tgtName, weight);
                // record if required
                record(srcName, tgtName, weight);
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
        } else if ((sifSize - 1) % 2 != 0) {
            throw new IOException("The file input format is not correct.");
        } else {
            String srcName = sifLine.get(0);
            // name value ... and it has already checked (sifSize -1) % 2 == 0
            for (int index = 1; index < sifSize; index += 2) {
                // name
                String tgtName = sifLine.get(index);
                if (!isIdentifier(tgtName)) {
                    throw new IOException("nodes' name are not correct.");
                }
                // value
                String input = sifLine.get(index + 1);
                if (!isNumeric(input)) {
                    throw new IOException("The file input format is not correct. Plus: some name-value pairs are incorrect!");
                }
                double weight = Double.parseDouble(input);
                // make sure only nodes in selection will be put into the simMat
                if (weight != 0 && simMat.getRowMap().containsKey(srcName) && simMat.getColMap().containsKey(tgtName)) {
                    simMat.put(srcName, tgtName, weight);
                }
                // record if required
                record(srcName, tgtName, weight);
            }
        }
        sifLine.clear();
    }


    private void record(String node1, String node2, double val) {
        recordSourceAndTarget(node1, node2);
        recordNeighbors(node1, node2);
        recordNoneZeros(node1, node2, val);
    }

    private void recordNoneZeros(String node1, String node2, double val) {
        if (recordNonZeros) {
            if (val != 0) {
                HashSet<String> tgt;
                if (nonZerosMap.containsKey(node1)) {
                    tgt = nonZerosMap.get(node1);
                } else {
                    tgt = new HashSet<>();
                }
                tgt.add(node2);
                nonZerosMap.put(node1, tgt);
            }
            // all zeros
            else if (!nonZerosMap.containsKey(node1)) {
                nonZerosMap.put(node1, new HashSet<>());
            }
        }
    }

    private void recordSourceAndTarget(String node1, String node2) {
        if (recordSrcAndTarget) {
            sourceNodesSet.add(node1);
            targetNodesSet.add(node2);
        }
    }

    private void recordNeighbors(String node1, String node2) {
        if (recordNeighbors) {
            recordMapCheck(node2, node1);
            recordMapCheck(node1, node2);
        }
    }

    private void recordMapCheck(String node1, String node2) {
        if (graphNeighbors.containsKey(node2)) {
            HashSet<String> nodes = graphNeighbors.get(node2);
            nodes.add(node1);
            graphNeighbors.put(node2, nodes);
        } else {
            graphNeighbors.put(node2, new HashSet<>(Collections.singleton(node1)));
        }
    }


    private boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isIdentifier(String str) {
        Pattern p = Pattern.compile("[a-zA-Z][[0-9]|[a-zA-Z]]*");
        Matcher m = p.matcher(str);
        return m.find();
    }

    //------------------PUBLIC-----------------------------

    /**
     * Please execute read instruction before calling this method.
     *
     * @return HashSet of nodes' names in graph_1
     */
    public HashSet<String> getSourceNodesSet() {
        assert (sourceNodesSet != null);
        return sourceNodesSet;
    }

    /**
     * Please execute read instruction before calling this method.
     *
     * @return HashSet of nodes' names in graph_2
     */
    public HashSet<String> getTargetNodesSet() {
        assert (targetNodesSet != null);
        return targetNodesSet;
    }

    public HashMap<String, HashSet<String>> getGraphNeighbors() {
        return graphNeighbors;
    }


    public void setRecordNeighbors(boolean recordNeighbors) {
        this.recordNeighbors = recordNeighbors;
    }

    public void setRecordSrcAndTarget(boolean recordSrcAndTarget) {
        this.recordSrcAndTarget = recordSrcAndTarget;
    }

    public void setRecordNonZeros(boolean recordNonZeros) {
        this.recordNonZeros = recordNonZeros;
    }
}
