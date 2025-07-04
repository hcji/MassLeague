package org.jhcg.pms.ui.sence;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;
import org.jhcg.pms.ui.model.CandidatesTableRow;

/**
 * Candidate Object Panel Scene
 *
 * @author Charles
 * @since JDK
 */
public class CandidatesAnchorPaneSence {
    private static ObservableList<CandidatesTableRow> candidatesTableRows = FXCollections.observableArrayList();


    public static ObservableList<CandidatesTableRow> getCandidatesTableRows() {
        return candidatesTableRows;
    }

    public static void addCandidatesTableRow(CandidatesTableRow candidatesTableRow) {
        CandidatesAnchorPaneSence.candidatesTableRows.add(candidatesTableRow);
    }
    public static void clearCandidatesTableRows(){
        CandidatesAnchorPaneSence.candidatesTableRows.clear();
    }
}
