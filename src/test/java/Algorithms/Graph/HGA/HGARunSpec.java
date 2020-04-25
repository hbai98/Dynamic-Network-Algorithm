package Algorithms.Graph.HGA;

import Algorithms.Alignment.align.Smith_Waterman;
import Algorithms.Graph.Network.AdjList;
import org.junit.jupiter.api.Test;

class HGARunSpec {
    @Test
    void S_protein_Test() throws Exception {
        AdjList simList = new Smith_Waterman().run("src/main/java/resources/Test/cov_S.fa","src/main/java/resources/Test/sars_S.fa");
        HGARun run = new HGARun(simList,"src/main/java/resources/Test/cov_S.txt","src/main/java/resources/Test/sars_S.txt");
        run.getHga().getMappingFinalResult().forEach( edge ->
                System.out.println("Edge:"+edge.getSource().getStrName()+" to "+edge.getTarget().getStrName()+" is matched.")
        );
    }
}
