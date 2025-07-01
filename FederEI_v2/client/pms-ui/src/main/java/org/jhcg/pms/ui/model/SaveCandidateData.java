package org.jhcg.pms.ui.model;

/**
 * @author Charles
 * @since JDK
 */
public class SaveCandidateData {
    private String inchikey;
    private String smiles;
    private double mw;
    private double distance;
    private int rank;

    public SaveCandidateData(String inchikey, String smiles, double mw, double distance, int rank) {
        this.inchikey = inchikey;
        this.smiles = smiles;
        this.mw = mw;
        this.distance = distance;
        this.rank = rank;
    }

    public String getInchikey() {
        return inchikey;
    }

    public String getSmiles() {
        return smiles;
    }

    public double getMw() {
        return mw;
    }

    public double getDistance() {
        return distance;
    }

    public int getRank() {
        return rank;
    }
}
