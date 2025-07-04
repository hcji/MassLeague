package org.jhcg.pms.ui.controller;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.jhcg.pms.service.ServiceCenter;
import org.jhcg.pms.ui.model.CandidatesTableRow;
import org.jhcg.pms.ui.sence.CandidatesAnchorPaneSence;
import org.jhcg.pms.ui.sence.MainSence;
import org.jhcg.pms.util.SqliteUtil;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.Provider;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

/**
 * @author Charles
 * @since JDK
 */
public class CandidatesAnchorPaneController {
    @FXML
    private ImageView againstMSImageView;
    @FXML
    private ImageView molStrucImageView;
    @FXML
    private TableView<CandidatesTableRow> candidatesTableView;
    @FXML
    private TableColumn<CandidatesTableRow, Double> mwColumn;

    @FXML
    private TableColumn<CandidatesTableRow, Integer> rankColumn;

    @FXML
    private TableColumn<CandidatesTableRow, Double> distanceColumn;

    @FXML
    private TableColumn<CandidatesTableRow, String> smilesColumn;

    @FXML
    private TableColumn<CandidatesTableRow, String> inchikeyColumn;

    public ImageView getAgainstMSImageView() {
        return againstMSImageView;
    }

    public ImageView getMolStrucImageView() {
        return molStrucImageView;
    }

    public void initialize() {

        //Bind the selection event to the items of the candidatesTableView list.
        candidatesTableView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<CandidatesTableRow>() {
            @Override
            public void changed(ObservableValue<? extends CandidatesTableRow> observable, CandidatesTableRow oldValue, CandidatesTableRow newValue) {
                Thread thread = new Thread(() -> {
                    if (newValue != null) {
                        int pid = newValue.getPid();
                        System.out.println("pid:" + pid);
                        SqliteUtil sqliteUtil1 = null;
                        SqliteUtil sqliteUtil2 = null;
                        ResultSet resultSet = null;
                        try {
                            Platform.runLater(() -> {
                                molStrucImageView.setImage(null);
                                againstMSImageView.setImage(null);
                            });
                            //Obtain the mass-to-charge ratio and intensity of the input spectrum
                            sqliteUtil1 = new SqliteUtil(ServiceCenter.getDbPath() + "cache.db");
                            sqliteUtil2 = new SqliteUtil(ServiceCenter.getDbPath() + "candidate.db");
                            String sql1 = "select mz,intensities from file_data where id=? and file_name=?;";
                            PreparedStatement ps1 = sqliteUtil1.getPreparedStatement(sql1);
                            ps1.setInt(1, MainSence.getCurrentID());
                            ps1.setString(2, MainSence.getCurrentFileName());
                            resultSet = ps1.executeQuery();
                            if (resultSet.next()) {
                                byte[] origMz = resultSet.getBytes("mz");
                                byte[] origIntensities = resultSet.getBytes("intensities");

                                //Search for the mass-to-charge ratio, intensity, and smiles of the candidates,
                                // and draw the comparison mass spectrum and molecular structure diagrams.
                                String sql2 = "select smiles,candi_index from candidate_message where pid=?;";
                                PreparedStatement ps2 = sqliteUtil2.getPreparedStatement(sql2);
                                ps2.setInt(1, newValue.getPid());
                                resultSet = ps2.executeQuery();
                                if (resultSet.next()) {
                                    String smiles = resultSet.getString("smiles");
                                    int candiIndex = resultSet.getInt("candi_index");

                                    //plotMOlStruc
                                    byte[] molStrucData = ServiceCenter.plotMolStruc(smiles);
                                    ByteArrayInputStream bais1 = new ByteArrayInputStream(molStrucData);
                                    //molStrucImageView.setImage(new Image(bais1));

                                    //plotMSAgainst
                                    byte[] msAgainstData = ServiceCenter.plotMSAgainst(origMz, origIntensities, candiIndex);
                                    ByteArrayInputStream bais2 = new ByteArrayInputStream(msAgainstData);
                                    //againstMSImageView.setImage(new Image(bais2));

                                    Platform.runLater(() -> {
                                        molStrucImageView.setImage(new Image(bais1));
                                        againstMSImageView.setImage(new Image(bais2));
                                    });
                                }
                            }

                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        } catch (ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        } finally {
                            if (resultSet != null) {
                                try {
                                    resultSet.close();
                                } catch (SQLException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                            if (sqliteUtil1 != null) {
                                sqliteUtil1.destroyed();
                            }
                            if (sqliteUtil2 != null) {
                                sqliteUtil2.destroyed();
                            }
                        }
                    } else {
                        Platform.runLater(() -> {
                            molStrucImageView.setImage(null);
                            againstMSImageView.setImage(null);
                        });
                    }
                });
                thread.setDaemon(true);
                thread.start();
            }
        });

        //2.Add a right-click event to mw
        // 2.1Create right-click menu item
        ContextMenu contextMenu = new ContextMenu();
        MenuItem menuItem1 = new MenuItem("Filter          ");
        contextMenu.getItems().addAll(menuItem1);
        // 2.2Add a right-click event to mwColumn
        mwColumn.setContextMenu(contextMenu);
        // 2.3Set the click event for the MenuItem
        menuItem1.setOnAction(event -> {
            AnchorPane filterAnchorPane = null;
            try {
                filterAnchorPane = FXMLLoader.load(Objects.requireNonNull(MainSence.class.getResource("/fxml/FilterAnchorPane.fxml")));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            // Create a new Stage as a pop-up window
            Stage filterStage = new Stage();
            filterStage.setTitle("Filter");
            filterStage.setResizable(false);

            // Set the window mode to modal. This way, the user must close this window
            // before returning to the main window.
            filterStage.initModality(Modality.APPLICATION_MODAL);

            Scene scene = new Scene(filterAnchorPane);
            filterStage.setScene(scene);
            filterStage.showAndWait(); // Display the window and wait for the user to close it.

        });
    }
}
