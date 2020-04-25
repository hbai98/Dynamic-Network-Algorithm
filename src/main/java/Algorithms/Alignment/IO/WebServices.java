package Algorithms.Alignment.IO;

import uk.ac.ebi.kraken.interfaces.common.Sequence;
import uk.ac.ebi.kraken.interfaces.uniprot.UniProtEntry;
import uk.ac.ebi.uniprot.dataservice.client.Client;
import uk.ac.ebi.uniprot.dataservice.client.QueryResult;
import uk.ac.ebi.uniprot.dataservice.client.ServiceFactory;
import uk.ac.ebi.uniprot.dataservice.client.exception.ServiceException;
import uk.ac.ebi.uniprot.dataservice.client.uniprot.UniProtQueryBuilder;
import uk.ac.ebi.uniprot.dataservice.client.uniprot.UniProtService;
import uk.ac.ebi.uniprot.dataservice.query.Query;

import java.util.*;

/**
 * https://www.uniprot.org/help/programmatic_access
 * https://www.ebi.ac.uk/uniprot/japi/javadoc/index.html
 */
public class WebServices {
    // record all proteins info
    static HashMap<String, HashMap<String, String>> infoMap;
    static HashMap<String, Sequence> seqMap;
    static boolean isVirus;
    static int MAX_everyEntry = 5;
    static class Uniprot {
        // info
        UniProtService uniprotService;

        Uniprot(Collection<String> identifiers) {
            infoMap = new HashMap<>();
            seqMap = new HashMap<>();
            ServiceFactory serviceFactoryInstance = Client.getServiceFactoryInstance();
            // UniProtService
            uniprotService = serviceFactoryInstance.getUniProtQueryService();
            uniprotService.start();
            // record
            record(identifiers);
            uniprotService.stop();
        }


        private void record(Collection<String> identifiers) {
            // search for gene names, synonyms, Ordered Locus Names or
            // ORFs via the gene(String name) method
            try {

                for (String identifier : identifiers) {
                    accept(identifier);
                }
            } catch (ServiceException ex) {
                ex.printStackTrace();
            }
        }

        private void accept(String s) throws ServiceException {
            Query query = UniProtQueryBuilder.proteinName(s);
            QueryResult<UniProtEntry> results = uniprotService.getEntries(query);
            HashMap<String, String> info = new HashMap<>();
            int toShow;
            // limit
            if(results.getNumberOfHits()> MAX_everyEntry){
                toShow = MAX_everyEntry;
            }
            else{
                toShow =(int) results.getNumberOfHits();
            }
            for (int i = 0; i < toShow; i++) {
                UniProtEntry data = results.getFirstResult();
                info.put("UniprotID", data.getUniProtId().toString());
                info.put("Name", s);
                if(isVirus){
                    info.put("Description", "Host:"+data.getOrganismHosts().toString());
                }
                else{
                    info.put("Description", data.getOrganism().toString());
                }
                seqMap.put(data.getUniProtId().toString(), data.getSequence());
                infoMap.put(s, info);
            }


        }


        public static HashMap<String, HashMap<String, String>> getInfoMap() {
            return infoMap;
        }

        public static void main(String[] args) {


        }
    }
}
