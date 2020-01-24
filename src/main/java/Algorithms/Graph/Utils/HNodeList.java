package Algorithms.Graph.Utils;

import Algorithms.Graph.Network.NodeList;

import java.util.Objects;

/**
 * This class is aimed to save nodes with a signName as
 * a edgeList defined in Network.NodeList
 */
public class HNodeList extends NodeList {
    // target protein
    //----------keys-----------
    protected String signName;
    protected int ID;
    //---------
    protected int score;

    public HNodeList(String signName){
        this.signName = signName;
        this.ID = -1;
        this.score = -1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        HNodeList nodes = (HNodeList) o;
        return ID == nodes.ID &&
                signName.equals(nodes.signName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), signName, ID);
    }

   //---------------PUBLIC------------------

    public String getSignName() {
        return signName;
    }

    public int getID() {
        return ID;
    }

    public int getScore() {
        return score;
    }
    //-----------------SET-----------------

    public void setSignName(String signName) {
        this.signName = signName;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
