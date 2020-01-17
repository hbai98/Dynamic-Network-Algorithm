package Algorithms.Graph.Utils;

import Algorithms.Graph.Network.NodeList;

import java.util.Objects;

/**
 * This class is aimed to save one specific protein's homologous proteins as
 * a edgeList defined in Network.NodeList
 */
public class HomoGene extends NodeList {
    // target protein
    //----------keys-----------
    protected String proteinName;
    protected int ID;
    //---------
    protected int score;

    public HomoGene(String proteinName){
        this.proteinName = proteinName;
        this.ID = -1;
        this.score = -1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        HomoGene nodes = (HomoGene) o;
        return ID == nodes.ID &&
                proteinName.equals(nodes.proteinName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), proteinName, ID);
    }

   //---------------PUBLIC------------------

    public String getProteinName() {
        return proteinName;
    }

    public int getID() {
        return ID;
    }

    public int getScore() {
        return score;
    }
    //-----------------SET-----------------

    public void setProteinName(String proteinName) {
        this.proteinName = proteinName;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
