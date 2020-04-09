package Algorithms.Alignment.IO;

import org.biojava.nbio.core.sequence.ProteinSequence;
import uk.ac.ebi.kraken.interfaces.uniprot.organism.OrganismName;
import uk.ac.ebi.uniprot.dataservice.client.Client;
import uk.ac.ebi.uniprot.dataservice.client.QueryResult;
import uk.ac.ebi.uniprot.dataservice.client.ServiceFactory;
import uk.ac.ebi.uniprot.dataservice.client.exception.ServiceException;
import uk.ac.ebi.uniprot.dataservice.client.uniprot.UniProtData;
import uk.ac.ebi.uniprot.dataservice.client.uniprot.UniProtQueryBuilder;
import uk.ac.ebi.uniprot.dataservice.client.uniprot.UniProtService;
import uk.ac.ebi.uniprot.dataservice.query.Query;

import java.util.Collection;
import java.util.HashMap;

public class WebServices {
    // record all proteins info
    static HashMap<String, ProteinSequence> seqMap = new HashMap<>();
    static HashMap<String,QueryResult<UniProtData>> infoMap = new HashMap<>();

    static class Uniport {
        // info
        String orgName;
        UniProtService uniprotService;
        Uniport(Collection<String> identifiers,String organismName){
            ServiceFactory serviceFactoryInstance = Client.getServiceFactoryInstance();
            // UniProtService
            uniprotService = serviceFactoryInstance.getUniProtQueryService();
            uniprotService.start();
            orgName = organismName;
            // record seqs
            record(identifiers);
            uniprotService.stop();
        }

        private void record(Collection<String> identifiers) {
            // search for gene names, synonyms, Ordered Locus Names or
            // ORFs via the gene(String name) method
            try{
                for (String identifier : identifiers) {
                    accept(identifier);
                }
            }
            catch (ServiceException ex){
                ex.printStackTrace();
            }
        }

        private void accept(String i) throws ServiceException {
            Query query = UniProtQueryBuilder.organismName(orgName).and(UniProtQueryBuilder.proteinName(i));
            QueryResult<UniProtData> results = uniprotService.getResults(query, UniProtData.ComponentType.COMMENTS, UniProtData.ComponentType.PROTEIN_NAMES);
            infoMap.put(i,results);
        }
    }

    public static HashMap<String, QueryResult<UniProtData>> getInfoMap() {
        return infoMap;
    }

    public static HashMap<String, ProteinSequence> getSeqMap() {
        return seqMap;
    }

    public static void main(String[] args) {


    }
}
