package Algorithms.Alignment.IO;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.ac.ebi.uniprot.dataservice.client.QueryResult;
import uk.ac.ebi.uniprot.dataservice.client.uniprot.UniProtData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class WebServicesSpec {
    @Test
    @DisplayName("IO test")
    void IO() {
        ArrayList<String> list = new ArrayList<>(Arrays.asList("DPM1",
                "C1orf112", "FGR", "FUCA2", "GCLC"));
        WebServices.Uniport uniportService = new WebServices.Uniport(list,"HUMAN");
        HashMap<String, QueryResult<UniProtData>> map = WebServices.getInfoMap();
        map.forEach((k,v)->System.out.println(k + v));
    }

}