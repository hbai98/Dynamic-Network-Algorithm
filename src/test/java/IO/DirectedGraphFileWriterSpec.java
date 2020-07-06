package IO;

import Algorithms.Graph.Utils.AdjList.SimList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
@DisplayName("The writer is able to ")
class DirectedGraphFileWriterSpec {
    private GraphFileWriter writer;
    @BeforeEach
    void init(){
        writer = new GraphFileWriter();
    }
    @Test
    @DisplayName("replace all spaces with '_' in a String")
    void replaceTest(){
       String test = "  AADFSD dfdfd lllll ";
       String s = writer.replaceSpace_(test);
       assertEquals("AADFSD_dfdfd_lllll",s);
    }
//    @Test
//    @DisplayName("write to txt.")
//    void writeToTxt() throws IOException {
//        GraphFileReader reader = new GraphFileReader();
//        SimList list = reader.readToSimList("src/test/java/resources/IOTest/mediumGraph.txt");
//        writer.writeToTxt(list,"src/test/java/resources/IOTest/mediumGraph.txt");
//    }

}