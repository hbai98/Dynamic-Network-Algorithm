package Algorithms.Alignment.IO;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntry;
import uk.ac.ebi.uniprot.dataservice.client.Client;
import uk.ac.ebi.uniprot.dataservice.client.QueryResult;
import uk.ac.ebi.uniprot.dataservice.client.ServiceFactory;
import uk.ac.ebi.uniprot.dataservice.client.exception.ServiceException;
import uk.ac.ebi.uniprot.dataservice.client.uniprot.UniProtData;
import uk.ac.ebi.uniprot.dataservice.client.uniprot.UniProtQueryBuilder;
import uk.ac.ebi.uniprot.dataservice.client.uniprot.UniProtService;
import uk.ac.ebi.uniprot.dataservice.query.Query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class WebServicesSpec {
    private HashMap<String, List<String>> map;
    UniProtService uniprotService;
    @BeforeEach
    void init() {
        ServiceFactory serviceFactoryInstance = Client.getServiceFactoryInstance();
        uniprotService = serviceFactoryInstance.getUniProtQueryService();
        uniprotService.start();
    }

    @Test
    void organism() throws ServiceException {

        for (OrganismName organismName : OrganismName.getAll()) {
            Query query = UniProtQueryBuilder.organismName(organismName.name);
            QueryResult<UniProtData> results = uniprotService.getResults(query, UniProtData.ComponentType.COMMENTS, UniProtData.ComponentType.PROTEIN_NAMES);
            assertTrue(results.getNumberOfHits() != 0);
        }
        uniprotService.stop();
    }

    @Test
    void virus() throws ServiceException {

        for (OrganismHostName organismName : OrganismHostName.getAll()) {
            Query query = UniProtQueryBuilder.organismHostName(organismName.name);
            QueryResult<UniProtData> results = uniprotService.getResults(query, UniProtData.ComponentType.COMMENTS, UniProtData.ComponentType.PROTEIN_NAMES);
            assertTrue(results.getNumberOfHits() != 0);
        }
        uniprotService.stop();
    }

    @Test
    void single() throws ServiceException {
        Query query = UniProtQueryBuilder.proteinName("ASPHD2");
        QueryResult<UniProtEntry> entries = uniprotService.getEntries(query);
    }


    @Test
    @DisplayName("IO test")
    void IO() {
        ArrayList<String> list = new ArrayList<>(Arrays.asList(
                "spike"

        ));
        WebServices.isVirus = true;
        WebServices.Uniprot res = new WebServices.Uniprot(list);
    }


}