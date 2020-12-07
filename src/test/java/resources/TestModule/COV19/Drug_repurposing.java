package resources.TestModule.COV19;

import Algorithms.Graph.Dynamic.Diffusion_Kernel.DK;
import DS.Matrix.StatisticsMatrix;
import DS.Network.UndirectedGraph;
import IO.Reader.GraphFileReader;
import IO.Writer.AbstractFileWriter;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

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
    void DK() throws FileNotFoundException {
        // build the disease module
        DK<String, DefaultWeightedEdge> dk = new DK<>(host.vertexSet(),humanPPI,0.5);
        dk.run();
        StatisticsMatrix res = dk.getResult();
        AbstractFileWriter writer = new AbstractFileWriter() {
            // set path before using it
            @Override
            public void write(Vector<String> context, boolean close) {
                super.write(context, close);
            }
        };
        writer.setPath("src/test/java/resources/cov19/Cytoscape/nCoV_host_DK_score.txt");
        Vector<String> context = new Vector<>();
        AtomicInteger i = new AtomicInteger(0);
        // vertex set order will not change
        host.vertexSet().forEach(t->{
            double score = res.get(i.get(),0);
            i.addAndGet(1);
            context.add(t+" "+ score+"\n");
        });
        // output
        writer.write(context, true);
    }

    @Test
    void find_drug_by_hga() throws FileNotFoundException {

    }
}
