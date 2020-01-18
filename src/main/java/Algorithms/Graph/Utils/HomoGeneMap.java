package Algorithms.Graph.Utils;

import Algorithms.Graph.Network.NodeList;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

/**
 * This class is meant to make a graph based on adjacent list
 * NodeList has already been defined in Network, and the next step is to
 * provide another LinkList to wrap NodeLists.
 */
public class HomoGeneMap extends LinkedList<HomoGene> {
    /**
     * add a list with to graph, if it exists(same head name),combine two of them; else add the list to the graph.
     * @param homoGene a list to be added
     * @return true for already exist.
     */
    @Override
    public boolean add(HomoGene homoGene) {
        int index = Collections.binarySearch(this,homoGene, Comparator.comparing(o -> o.proteinName));

        if(index >= 0){
            this.get(index).addAll(homoGene);
            return true;
        }
        else{
            add(-index-1,homoGene);
            return false;
        }
    }

    /**
     * find protein's all homologous genes.
     * @param geneToSearch target gene
     */
    public HomoGene get(String geneToSearch){
        for(HomoGene homoGene: this){
            if(homoGene.proteinName.equals(geneToSearch)){
                return homoGene;
            }
        }
        return null;
    }

    /**
     * remove tgtHG from list(tgtHead).
     * @param tgtHead headName of the list.
     * @param tgtHG name of the homoGene to be removed.
     */
    public void removeOneHG(String tgtHead,String tgtHG){
        int index = Collections.binarySearch(this,new HomoGene(tgtHead), Comparator.comparing(o -> o.proteinName));
        this.get(index).remove(tgtHG);
    }
    /**
     * add tgtHG to list(tgtHead).
     * @param tgtHead headName of the list.
     * @param tgtHG name of the homoGene to be removed.
     */
    public void addOneHG(String tgtHead,String tgtHG){
        int index = Collections.binarySearch(this,new HomoGene(tgtHead), Comparator.comparing(o -> o.proteinName));
        this.get(index).add(tgtHG);
    }
}
