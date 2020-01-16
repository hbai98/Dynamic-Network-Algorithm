package Network;
// Author: Haotian Bai
// Shanghai University, department of computer science
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashSet;

/**
 * This class can be defined as HashSet
 * for the occurance of the same edges will not be allowed in lists
 */
public class EdgeList extends HashSet<Edge> {
    // file to store the edges
    protected String filename;

    /**
     * Add an edge if not existed
     *
     * @param edge edge to be added
     * @return true for success
     */
    @Override
    public boolean add(Edge edge) {
        // Note: equals() has been overloaded for the edge comparision,
        // so add(),remove(),contains() will all be changed.
        return super.add(edge);
    }

    public boolean add(String src, String tgt, double weight, Edge.Type type) {
        return add(new Edge(src, tgt, weight, type));
    }

    /**
     * Find edges contains {@code} node
     *
     * @param node node to be found
     * @return hasSet with all edges
     */
    public EdgeList getVetEdg(Node node) {
        EdgeList res = new EdgeList();
        forEach(edge -> {
            if(edge.source.equals(node)||edge.target.equals(node)){
                res.add(edge);
            }
        });
        return res;
    }

    public void readFromFile() throws FileNotFoundException {
        FileInputStream fis = new FileInputStream(filename);

    }
}
