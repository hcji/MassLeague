package org.jhcg.pms.ui.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.jhcg.pms.service.ServiceCenter;
import org.jhcg.pms.ui.model.CandidatesTableRow;
import org.jhcg.pms.ui.sence.CandidatesAnchorPaneSence;
import org.jhcg.pms.ui.sence.MainSence;
import org.jhcg.pms.util.SqliteUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FilterAnchorPaneController {

    @FXML
    private Button cancelButton;

    @FXML
    private TextField leftValueTextField;

    @FXML
    private Button okButton;
    @FXML
    private Button resetButton;


    @FXML
    private Text origValueText;

    @FXML
    private TextField rightValueTextField;

    @FXML
    void okButtonOnMouseClicked(MouseEvent event) {
        Thread thread = new Thread(() -> {

            TabPane resultTabPane = MainSence.getResultTabPane();
            SqliteUtil sqliteUtil = null;
            ResultSet resultSet = null;
            try {
                sqliteUtil = new SqliteUtil(ServiceCenter.getDbPath() + "candidate.db");

                //2.刷新CandidatesTab
                //2.0清空所有行
                CandidatesAnchorPaneSence.clearCandidatesTableRows();

                String sql2 = "select pid,inchikey,smiles,mw,distance,rank from candidate_message where id=? and file_name=? and mw >= ? and mw <= ?;";
                PreparedStatement ps2 = sqliteUtil.getPreparedStatement(sql2);
                ps2.setInt(1, MainSence.getCurrentID());
                ps2.setString(2, MainSence.getCurrentFileName());
                ps2.setFloat(3, Float.valueOf(leftValueTextField.getText()));
                ps2.setFloat(4, Float.valueOf(rightValueTextField.getText()));
                resultSet = ps2.executeQuery();
                if (resultSet.next()) {
                    do {
                        int pid = resultSet.getInt("pid");
                        String inchikey = resultSet.getString("inchikey");
                        String smiles = resultSet.getString("smiles");
                        double mw = resultSet.getDouble("mw");
                        BigDecimal bigMW = new BigDecimal(mw).setScale(3, RoundingMode.HALF_UP);
                        double distance = resultSet.getDouble("distance");
                        BigDecimal bigDistance = new BigDecimal(distance).setScale(4,RoundingMode.HALF_UP);
                        int rank = resultSet.getInt("rank");

                        // 添加表格行数据
                        CandidatesAnchorPaneSence.addCandidatesTableRow(new CandidatesTableRow(pid, inchikey, smiles, bigMW, bigDistance, rank));
                        resultTabPane.getTabs().get(1).setDisable(false);

                    } while (resultSet.next());

                } else {
                    CandidatesAnchorPaneSence.clearCandidatesTableRows();
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
                if (sqliteUtil != null) {
                    sqliteUtil.destroyed();
                }
            }

        });
        thread.setDaemon(true);
        thread.start();

        Stage stage = (Stage) okButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    void cancelButtonOnMouseClicked(MouseEvent event) {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    void resetButtonOnMouseClicked(MouseEvent event) {
        Thread thread = new Thread(() -> {

            TabPane resultTabPane = MainSence.getResultTabPane();
            SqliteUtil sqliteUtil = null;
            ResultSet resultSet = null;
            try {
                sqliteUtil = new SqliteUtil(ServiceCenter.getDbPath() + "candidate.db");

                //2.刷新CandidatesTab
                //2.0清空所有行
                CandidatesAnchorPaneSence.clearCandidatesTableRows();

                String sql2 = "select pid,inchikey,smiles,mw,distance,rank from candidate_message where id=? and file_name=?;";
                PreparedStatement ps2 = sqliteUtil.getPreparedStatement(sql2);
                ps2.setInt(1, MainSence.getCurrentID());
                ps2.setString(2, MainSence.getCurrentFileName());
                resultSet = ps2.executeQuery();
                if (resultSet.next()) {
                    do {
                        int pid = resultSet.getInt("pid");
                        String inchikey = resultSet.getString("inchikey");
                        String smiles = resultSet.getString("smiles");
                        double mw = resultSet.getDouble("mw");
                        BigDecimal bigMW = new BigDecimal(mw).setScale(3, RoundingMode.HALF_UP);
                        double distance = resultSet.getDouble("distance");
                        BigDecimal bigDistance = new BigDecimal(distance).setScale(4,RoundingMode.HALF_UP);
                        int rank = resultSet.getInt("rank");

                        // 添加表格行数据
                        CandidatesAnchorPaneSence.addCandidatesTableRow(new CandidatesTableRow(pid, inchikey, smiles, bigMW, bigDistance, rank));
                        resultTabPane.getTabs().get(1).setDisable(false);

                    } while (resultSet.next());

                } else {
                    CandidatesAnchorPaneSence.clearCandidatesTableRows();
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
                if (sqliteUtil != null) {
                    sqliteUtil.destroyed();
                }
            }

        });
        thread.setDaemon(true);
        thread.start();
    }

    public void initialize() {
        //1.设置未知化合物的分子质量
        SqliteUtil sqliteUtil = null;
        ResultSet resultSet = null;
        try {
            sqliteUtil = new SqliteUtil(ServiceCenter.getDbPath() + "cache.db");

            String sql1 = "select mw from file_data where id=? and file_name=?;";
            PreparedStatement ps1 = sqliteUtil.getPreparedStatement(sql1);
            ps1.setInt(1, MainSence.getCurrentID());
            ps1.setString(2, MainSence.getCurrentFileName());

            resultSet = ps1.executeQuery();
            while (resultSet.next()) {
                float mw = resultSet.getFloat("mw");
                origValueText.setText(String.valueOf(mw));
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
            if (sqliteUtil != null) {
                sqliteUtil.destroyed();
            }
        }

    }
}
