package resources.TestModule.COV19;

import Algorithms.Graph.Dynamic.Diffusion_Kernel.DK;
import DS.Matrix.StatisticsMatrix;
import DS.Network.UndirectedGraph;
import IO.Reader.GraphFileReader;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class Drug_repurposing {
    private UndirectedGraph<String, DefaultWeightedEdge> host;
    private UndirectedGraph<String, DefaultWeightedEdge> humanPPI;

    @BeforeEach
    void setup() throws IOException {
        GraphFileReader<String, DefaultWeightedEdge> reader = new GraphFileReader<>(String.class,DefaultWeightedEdge.class);
        host = reader.readToUndirectedGraph("src/test/java/resources/TestModule/COV19/host_cf_greater_0.7", false);
        humanPPI = reader.readToUndirectedGraph("src/test/java/resources/TestModule/COV19/human_cf_greater_0.7.txt", false);

    }
    @Test
    void find_drug_by_hga(){
        // build the disease module
        DK<String, DefaultWeightedEdge> dk = new DK<>(host.vertexSet(),humanPPI,0.5);
        dk.run();
        StatisticsMatrix res = dk.getResult();
    }
}
