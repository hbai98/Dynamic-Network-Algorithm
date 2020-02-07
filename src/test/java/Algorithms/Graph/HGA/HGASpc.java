package Algorithms.Graph.HGA;

import Algorithms.Graph.IO.GraphFileReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;
@DisplayName("The HGA is able to ")
class HGASpc {
    HGA hga;
    @BeforeEach
    void init(){
        GraphFileReader
        hga = new HGA();
    }
}