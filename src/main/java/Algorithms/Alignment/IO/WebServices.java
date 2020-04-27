package Algorithms.Alignment.IO;

import org.jgrapht.alg.util.Pair;
import uk.ac.ebi.kraken.interfaces.common.Sequence;
import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntry;
import uk.ac.ebi.uniprot.dataservice.client.Client;
import uk.ac.ebi.uniprot.dataservice.client.QueryResult;
import uk.ac.ebi.uniprot.dataservice.client.ServiceFactory;
import uk.ac.ebi.uniprot.dataservice.client.exception.ServiceException;
import uk.ac.ebi.uniprot.dataservice.client.uniprot.UniProtQueryBuilder;
import uk.ac.ebi.uniprot.dataservice.client.uniprot.UniProtService;
import uk.ac.ebi.uniprot.dataservice.query.Query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * https://www.uniprot.org/help/programmatic_access
 * https://www.ebi.ac.uk/uniprot/japi/javadoc/index.html
 */
public class WebServices {
    // record all proteins info
    public static HashMap<String, List<Pair<String,String>>> infoMap;
    public static HashMap<String, Sequence> seqMap;
    public static String organismName;
    // above maintain together
    static int MAX_everyEntry = 5;

    public static class Uniprot {
        // info
        UniProtService uniprotService;

        public Uniprot(Collection<String> identifiers) {
            infoMap = new HashMap<>();
            seqMap = new HashMap<>();
            ServiceFactory serviceFactoryInstance = Client.getServiceFactoryInstance();
            // UniProtService
            uniprotService = serviceFactoryInstance.getUniProtQueryService();
            uniprotService.start();
            // record
            for (String identifier : identifiers) {
                record(identifier);
            }
            uniprotService.stop();
        }

        private void record(String identifier) {
            // search for gene names, synonyms, Ordered Locus Names or
            // ORFs via the gene(String name) method
            try {
                accept(identifier);
            } catch (ServiceException ex) {
                ex.printStackTrace();
            }
        }

        private void accept(String s) throws ServiceException {
            Query query;
            if (organismName != null) {
                query = UniProtQueryBuilder.proteinName(s)
                        .and(UniProtQueryBuilder.organismName(organismName));

            } else {
                query = UniProtQueryBuilder.proteinName(s);
            }
            QueryResult<UniProtEntry> results = uniprotService.getEntries(query);
            List<Pair<String,String>> info = new ArrayList<>();
            int toShow;
            // limit
            if (results.getNumberOfHits() > MAX_everyEntry) {
                toShow = MAX_everyEntry;
            } else {
                toShow = (int) results.getNumberOfHits();
            }
            for (int i = 0; i < toShow; i++) {
                UniProtEntry data = results.getFirstResult();
                Pair<String,String> pair = new Pair<>(data.getUniProtId().toString(),
                        data.getOrganism().toString());
                info.add(pair);
                seqMap.put(data.getUniProtId().toString(), data.getSequence());
            }
            // all proteins with the name s
            infoMap.put(s, info);

        }


    }
}
