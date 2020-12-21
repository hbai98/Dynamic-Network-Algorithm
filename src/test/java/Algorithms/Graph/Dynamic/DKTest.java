package Algorithms.Graph.Dynamic;

import Algorithms.Graph.Dynamic.Diffusion_Kernel.DK;
import DS.Matrix.DenseMatrix;
import DS.Matrix.SparseMatrix;
import DS.Matrix.StatisticsMatrix;
import DS.Network.UndirectedGraph;
import IO.Reader.GraphFileReader;
import IO.Writer.AbstractFileWriter;
import org.checkerframework.checker.units.qual.A;
import org.ejml.data.DMatrix;
import org.ejml.data.DMatrixSparse;
import org.ejml.data.DMatrixSparseCSC;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.text.BadLocationException;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

class DKTest {
    UndirectedGraph<String, DefaultWeightedEdge> graph;
    GraphFileReader<String, DefaultWeightedEdge> reader;

    @BeforeEach
    void init() throws IOException {
        reader = new GraphFileReader<>(String.class, DefaultWeightedEdge.class);
    }

    @Test
    void run() throws IOException {
        graph = reader.readToUndirectedGraph("src/test/java/resources/AlgTest/small/sGraph1.txt", true);
        Set<String> src = new HashSet<>(Arrays.asList("F","C"));
        DK<String, DefaultWeightedEdge> dk = new DK<>(src, graph, 0.5);
        dk.run();
        StatisticsMatrix res = dk.getResult();
        // normalize to percentage
        double sum = res.elementSum();
        res.convertToDense();
        Arrays.stream(res.getDDRM().data).forEach(e -> System.out.printf("%f.2\n", e / sum * 100));
    }


    @Test
    void test_nCov_host_diffuse() throws IOException {
        // get all data
        GraphFileReader<String, DefaultWeightedEdge> reader = new GraphFileReader<>(String.class, DefaultWeightedEdge.class);
        UndirectedGraph<String, DefaultWeightedEdge> host = reader.readToUndirectedGraph("src/test/java/resources/cov19/COV19_hosts_more_0.7_slice.txt", false);
        UndirectedGraph<String, DefaultWeightedEdge> human = reader.readToUndirectedGraph("src/test/java/resources/cov19/humanPPI_score_more_0.7_slice.txt", true);

        // set up writer
        AbstractFileWriter writer = new AbstractFileWriter() {
            // set path before using it
            @Override
            public void write(Vector<String> context, boolean close) {
                super.write(context, close);
            }
        };
        writer.setPath("src/test/java/resources/cov19/Cytoscape/nCoV_host_info.txt");
        Vector<String> context = new Vector<>();
        host.vertexSet().forEach(v-> {
            // preclude nodes that are not in 'high confidence' human PPI
            if(human.vertexSet().contains(v)) context.add(v+", ");
        });
        // output info about hostPPI
        writer.write(context, true);
    }


}