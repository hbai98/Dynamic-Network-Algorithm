package Network;
/**
 Author: Haotian Bai
 Shanghai University, department of computer science
 */

/**
 * Node for Protein network
 */
public class Node implements Comparable<Node>{
    protected String strProteinName;
    protected double proteinValue;
    protected String strLayerCode;
    // position in similarity matrix
    protected int degree;
    protected int index;

    protected Node(String strProteinName,double proteinValue,String strLayerCode){
        this.strProteinName = strProteinName;
        this.proteinValue = proteinValue;
        this.strLayerCode = strLayerCode;
    }
    /***
     * methods for public access
     */

    public double getProteinValue() {
        return proteinValue;
    }

    public String getStrProteinName() {
        return strProteinName;
    }

    public String getStrLayerCode() {
        return strLayerCode;
    }

    public int getIndex() {
        return index;
    }

    public int getDegree() {
        return degree;
    }

    // ----------------------SET-------------------------------
    public void setProteinValue(double proteinValue) {
        this.proteinValue = proteinValue;
    }

    public void setStrProteinName(String strProteinName) {
        this.strProteinName = strProteinName;
    }

    public void setStrLayerCode(String strLayerCode) {
        this.strLayerCode = strLayerCode;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setDegree(int degree) {
        this.degree = degree;
    }

    @Override
    public int compareTo(Node o) {
        return strProteinName.compareTo(o.getStrProteinName());
    }
}
