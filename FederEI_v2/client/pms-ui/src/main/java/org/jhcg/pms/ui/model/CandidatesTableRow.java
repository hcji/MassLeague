package org.jhcg.pms.ui.model;

import java.math.BigDecimal;

/**
 * @author Charles
 * @since JDK
 */
public class CandidatesTableRow {
    //主键
    private int pid;
    private String inchikey;
    private String smiles;
    private BigDecimal mw;
    private BigDecimal distance;
    private int rank;

    public CandidatesTableRow(int pid,String inchikey, String smiles, BigDecimal mw, BigDecimal distance, int rank) {
        this.pid = pid;
        this.inchikey = inchikey;
        this.smiles = smiles;
        this.mw = mw;
        this.distance = distance;
        this.rank = rank;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public String getInchikey() {
        return inchikey;
    }

    public void setInchikey(String inchikey) {
        this.inchikey = inchikey;
    }

    public String getSmiles() {
        return smiles;
    }

    public void setSmiles(String smiles) {
        this.smiles = smiles;
    }

    public BigDecimal getMw() {
        return mw;
    }

    public void setMw(BigDecimal mw) {
        this.mw = mw;
    }

    public BigDecimal getDistance() {
        return distance;
    }

    public void setDistance(BigDecimal distance) {
        this.distance = distance;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }
}
