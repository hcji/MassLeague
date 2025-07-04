package org.jhcg.pms.ui.controller;

/**
 * @author Charles
 * @since JDK
 */

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import org.jhcg.pms.service.ServiceCenter;
import org.jhcg.pms.ui.model.CandidatesTableRow;
import org.jhcg.pms.ui.model.SaveCandidateData;
import org.jhcg.pms.ui.sence.CandidatesAnchorPaneSence;
import org.jhcg.pms.ui.sence.MainSence;
import org.jhcg.pms.util.SqliteUtil;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class MainController {

    @FXML
    private AnchorPane menuPane;

    @FXML
    private AnchorPane contentPane;

    @FXML
    private TabPane resultTabPane;

    @FXML
    private ListView<String> openedMSListView;

    //The sub-lists of the opened MSListView
    ObservableList<String> openedMSItems = FXCollections.observableArrayList();

    @FXML
    private TextField filterTextField;


    //----------The following code is for binding events to the control.----------

    @FXML
    void openedMSListViewDragDropped(DragEvent event) {
        Dragboard dragboard = event.getDragboard();
        if (dragboard.hasFiles()) {
            //Get the list of files dragged to the window
            // and filter out the files with extensions of .mgf or .msp.
            List<File> files = dragboard.getFiles().stream()
                    .filter(f -> f.getName().matches(".*\\.m(gf|sp)$"))
                    .collect(Collectors.toList());

            Thread thread = new Thread(() -> {
                addMSFile(files);
                showMSFile("select * from file_data;");
            });
            thread.setDaemon(true);
            thread.start();
        }
    }

    @FXML
    void openedMSListViewDragOver(DragEvent event) {
        event.acceptTransferModes(TransferMode.ANY);
    }

    @FXML
    void buttonLeft1MouseClicked(MouseEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select the mgf or msp file!");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("MGF&MSP", "*.mgf", "*.msp"),
                new FileChooser.ExtensionFilter("MGF", "*.mgf"),
                new FileChooser.ExtensionFilter("MSP", "*.msp"));
        List<File> files = fileChooser.showOpenMultipleDialog(menuPane.getScene().getWindow());

        Thread thread = new Thread(() -> {
            if (files != null && !files.isEmpty())
                addMSFile(files);
            showMSFile("select * from file_data;");
        });
        thread.setDaemon(true);
        thread.start();

    }

    @FXML
    void buttonLeft2MouseClicked(MouseEvent event) {
        DirectoryChooser dc = new DirectoryChooser();
        dc.setTitle("Save Files");
        dc.setInitialDirectory(new File(System.getProperty("user.home")));
        File saveDirectory = dc.showDialog(menuPane.getScene().getWindow());
//        System.out.println(saveDirectory);
        if (saveDirectory != null) {

            Thread thread = new Thread(() -> {
                SqliteUtil sqliteUtil1 = null;
                SqliteUtil sqliteUtil2 = null;
                ResultSet resultSet1 = null;
                ResultSet resultSet2 = null;
                try {
                    sqliteUtil1 = new SqliteUtil(ServiceCenter.getDbPath() + "cache.db");
                    sqliteUtil2 = new SqliteUtil(ServiceCenter.getDbPath() + "candidate.db");

                    String sql1 = "select distinct id,inchikey from file_data;";
                    PreparedStatement ps1 = sqliteUtil1.getPreparedStatement(sql1);
                    resultSet1 = ps1.executeQuery();


                    StringBuffer idToInchikeyContent = new StringBuffer();
                    idToInchikeyContent.append("id,inchikey\n");

                    int idNumber = 0;
                    while (resultSet1.next()) {
                        idNumber++;
                        int id = resultSet1.getInt("id");
                        String inchikey = resultSet1.getString("inchikey");
                        //Write the inchikey into the candidate file
                        idToInchikeyContent.append(id + "," + inchikey + "\n");
                    }

                    //Create a file for persistence purposes
                    File idToInchikey = new File(saveDirectory.getAbsolutePath(), "IDToInchikey.csv");
                    FileWriter writer1 = new FileWriter(idToInchikey, true);

                    writer1.write(idToInchikeyContent.toString());
                    writer1.close();

                    System.out.println("IDToInchikey.csv over");

                    //Traverse all the candidate
                    String sql2 = "select id,inchikey,smiles,mw,distance,rank from candidate_message;";
                    PreparedStatement ps2 = sqliteUtil2.getPreparedStatement(sql2);
                    resultSet2 = ps2.executeQuery();

                    Map<Integer, List<SaveCandidateData>> idToResultMap = new HashMap<>();
                    while (resultSet2.next()) {
                        int id = resultSet2.getInt("id");
                        String ik = resultSet2.getString("inchikey");
                        String smiles = resultSet2.getString("smiles");
                        double mw = resultSet2.getDouble("mw");
                        double distance = resultSet2.getDouble("distance");
                        int rank = resultSet2.getInt("rank");


                        if (!idToResultMap.containsKey(id)) {
                            // If it does not exist, then create a new list
                            idToResultMap.put(id, new ArrayList<>());
                        }

                        // Add the records to the corresponding list
                        SaveCandidateData saveCandidateData = new SaveCandidateData(ik, smiles, mw, distance, rank);
                        idToResultMap.get(id).add(saveCandidateData);

                    }
                    System.out.println("search database over");

                    for (int i = 0; i < idNumber; i++) {

                        StringBuffer csvContent = new StringBuffer();
                        csvContent.append(",inchikey,smiles,mw,distance,rank\n");

                        int candidateNumber = 0;
                        List<SaveCandidateData> saveCandidateDataList = idToResultMap.get(i);
                        for (int j = 0; j < saveCandidateDataList.size(); j++) {
                            SaveCandidateData data = saveCandidateDataList.get(j);

                            BigDecimal bigMW = new BigDecimal(data.getMw()).setScale(3, RoundingMode.HALF_UP);
                            BigDecimal bigDistance = new BigDecimal(data.getDistance()).setScale(4, RoundingMode.HALF_UP);

                            csvContent.append(candidateNumber++ + "," + data.getInchikey()
                                    + "," + data.getSmiles() + "," + bigMW + "," + bigDistance + "," + data.getRank() + "\n");
                        }

                        //Create a file to store the information of the candidates
                        File csvFile = new File(saveDirectory.getAbsolutePath(), i + ".csv");
                        FileWriter writer2 = new FileWriter(csvFile, true);
                        writer2.write(csvContent.toString());
                        writer2.close();
                    }
                    System.out.println("save over");
//                    for (int i = 0; i < idNumber; i++) {
//                        String sql2 = "select inchikey,smiles,mw,distance,rank from candidate_message where id=?;";
//                        PreparedStatement ps2 = sqliteUtil1.getPreparedStatement(sql2);
//                        ps2.setInt(1, i);
//                        resultSet2 = ps2.executeQuery();
//                        //创建保存候选物信息的文件
//                        File csvFile = new File(saveDirectory.getAbsolutePath(), i + ".csv");
//                        FileWriter writer2 = new FileWriter(csvFile, true);
//
//                        StringBuffer csvContent = new StringBuffer();
//                        csvContent.append(",inchikey,smiles,mw,distance,rank\n");
//
//                        int candidateNumber = 0;
//                        while (resultSet2.next()) {
//                            String ik = resultSet2.getString("inchikey");
//                            String smiles = resultSet2.getString("smiles");
//                            double mw = resultSet2.getDouble("mw");
//                            BigDecimal bigMW = new BigDecimal(mw).setScale(3, RoundingMode.HALF_UP);
//                            double distance = resultSet2.getDouble("distance");
//                            BigDecimal bigDistance = new BigDecimal(distance).setScale(4, RoundingMode.HALF_UP);
//                            int rank = resultSet2.getInt("rank");
//
//                            csvContent.append(candidateNumber++ + "," + ik + "," + smiles + "," + bigMW + "," + bigDistance + "," + rank + "\n");
//                        }
//                        writer2.write(csvContent.toString());
//                        writer2.close();
//                    }

                } catch (SQLException e) {
                    throw new RuntimeException(e);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } finally {
                    if (resultSet1 != null) {
                        try {
                            resultSet1.close();
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    if (resultSet2 != null) {
                        try {
                            resultSet2.close();
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
            });
            thread.setDaemon(true);
            thread.start();
        }
    }

    @FXML
    void buttonRight1MouseClicked(MouseEvent event) {
        //TODO 每次提交文件数据库表会删除重建
        Thread thread = new Thread(() -> {
            //1.Obtain the list of opened files
            SqliteUtil sqliteUtil = null;
            ResultSet resultSet = null;
            ArrayList<String> fileNames = new ArrayList<>();
            try {
                sqliteUtil = new SqliteUtil(ServiceCenter.getDbPath() + "cache.db");
                String sql = "select distinct file_name from file_data;";
                PreparedStatement ps = sqliteUtil.getPreparedStatement(sql);
                resultSet = ps.executeQuery();

                while (resultSet.next()) {
//                System.out.println(resultSet.getString("file_name"));
                    fileNames.add(resultSet.getString("file_name"));
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

            //2.Call the service for each file in sequence
            for (String fileName : fileNames) {
                ServiceCenter.getCandidateMessage(fileName, resultTabPane);
            }
        });

        thread.setDaemon(true);
        thread.start();

    }

    @FXML
    void buttonRight2MouseClicked(MouseEvent event) {

    }

    @FXML
    void buttonRight3MouseClicked(MouseEvent event) {
        Thread thread = new Thread(() -> {
            //Clear the spectrum graph
            ImageView msImageView = (ImageView) resultTabPane.lookup("#msImageView");
            //Clear the list of candidates
            Platform.runLater(()->{
                msImageView.setImage(null);
                CandidatesAnchorPaneSence.clearCandidatesTableRows();
                //Clear the mass spectrometry list
                openedMSItems.clear();
            });
            //clear cache
            ServiceCenter.clearCache();

        });
        thread.setDaemon(true);
        thread.start();
    }

    //----------The following code is the business code.----------
    private void addMSFile(List<File> files) {
        SqliteUtil sqliteUtil = null;
        ResultSet resultSet = null;
        try {
            for (File file : files) {
//                //1.Filter out the added files
                //2.upload files
                ServiceCenter.submitFile(file.getCanonicalPath());

//                //2.Display mass spectrometry file
//                sqliteUtil = new SqliteUtil(ServiceCenter.getDbPath() + "cache.db");
//                String sql2 = "select * from file_data;";
//                PreparedStatement ps2 = sqliteUtil.getPreparedStatement(sql2);
//                resultSet = ps2.executeQuery();
//                while (resultSet.next()) {
//                    int id = resultSet.getInt("id");
//                    String fileName = resultSet.getString("file_name");
//                    String title = resultSet.getString("title");
//                    float mw = resultSet.getFloat("mw");
//                    Platform.runLater(() -> {
//                        openedMSItems.add(title + "\nmw:" + mw + "\nfile name:" + fileName + "\tid:" + id);
//                    });

//                    System.out.println("id:" + id);
//                    System.out.println("file_name:" + fileName);
//                    System.out.println("title:" + title);
//                }

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        } catch (ClassNotFoundException e) {
//            throw new RuntimeException(e);
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

    private void showMSFile(String sql) {
        SqliteUtil sqliteUtil = null;
        ResultSet resultSet = null;
        try {
            //2.Display mass spectrometry file
            Platform.runLater(() -> {
                openedMSItems.clear();
            });
            sqliteUtil = new SqliteUtil(ServiceCenter.getDbPath() + "cache.db");
            PreparedStatement ps2 = sqliteUtil.getPreparedStatement(sql);
            resultSet = ps2.executeQuery();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String fileName = resultSet.getString("file_name");
                String title = resultSet.getString("title");
                float mw = resultSet.getFloat("mw");
                Platform.runLater(() -> {
                    openedMSItems.add(title + "\nmw:" + mw + "\nfile name:" + fileName + "\tid:" + id);
                });

//                    System.out.println("id:" + id);
//                    System.out.println("file_name:" + fileName);
//                    System.out.println("title:" + title);
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

    /**
     * init：
     * 1.Set the content for the tabs of the TabPane
     * 2.Bind the listener for the selection event of openedMSListView
     * 3.Bind the selection event to the items of the candidatesTableView list.
     */
    public void initialize() {
        //Bind the resultTabPane list to the global Sense
        MainSence.setResultTabPane(resultTabPane);
        //1Initialize TabPane
        addToTabPane("/fxml/MSAnchorPane.fxml", "MS", resultTabPane);
        addToTabPane("/fxml/CandidatesAnchorPane.fxml", "Candidates", resultTabPane);

        //2.Initialize the relevant contents for openedMSListView
        //2.1Bind the list item object
        openedMSListView.setItems(openedMSItems);
        //2.2Bind the listener for the selected event: Refresh the display interface
        openedMSListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                //Obtain the file name and ID of the selected value,
                // and store them in the static variables of MainSence.
                String temp[] = newValue.split("\n")[2].split("\t");
                MainSence.setCurrentFileName(temp[0].split(":")[1]);
                MainSence.setCurrentID(Integer.valueOf(temp[1].split(":")[1]));
                //刷新resultTabPane
                refreshResultTabPane(resultTabPane);
            } else {
                MainSence.setCurrentFileName(null);
                MainSence.setCurrentID(-1);
            }

//            System.out.println(MainSence.getCurrentFileName());
//            System.out.println(MainSence.getCurrentID());
        });
        //3.Bind to filterTextField
        filterTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (newValue.equals("")) {
                    showMSFile("select * from file_data;");
                } else {
                    showMSFile("select * from file_data where title like " + "'%" + newValue + "%';");
                }
            }
        });

    }

    /**
     * Add a Tab to the TabPane
     *
     * @param path    fxml file path
     * @param tabName The name of the tab
     */
    public void addToTabPane(String path, String tabName, TabPane tabPane) {
        //1.1Declare the AnchorPane variable
        AnchorPane anchorPane = null;
        try {
            //1.2Read and load the fxml file and assign it to the AnchorPane variable
            anchorPane = FXMLLoader.load(Objects.requireNonNull(MainSence.class.getResource(path)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //1.3Create the corresponding Tab using the AnchorPane object,
        // and then add the Tab to the TabPane
        Tab tab = new Tab(tabName, anchorPane);
        tab.setDisable(true);
        tab.setId(tabName + "Tab");
        tabPane.getTabs().add(tab);
    }

    /**
     * Respond to the click event of the ListView and refresh the UI interface
     */
    public void refreshResultTabPane(TabPane resultTabPane) {
        Thread thread1 = new Thread(() -> {
            refreshMSTab(resultTabPane);
        });
        Thread thread2 = new Thread(() -> {
            refreshCandidatesTab(resultTabPane);
        });

        thread1.setDaemon(true);
        thread2.setDaemon(true);
        thread1.start();
        thread2.start();
    }

    public void refreshMSTab(TabPane resultTabPane) {
        SqliteUtil sqliteUtil = null;
        ResultSet resultSet = null;
        try {
            sqliteUtil = new SqliteUtil(ServiceCenter.getDbPath() + "cache.db");

            //1.Refresh MSTab
            //1.1Find the ImageView object by using the id.
            ImageView msImageView = (ImageView) resultTabPane.lookup("#msImageView");
            Platform.runLater(() -> {
                msImageView.setImage(null);
                resultTabPane.getTabs().get(0).setDisable(true);
            });
            //1.2Perform database operations
            String sql1 = "select mz,intensities from file_data where id=? and file_name=?;";
            PreparedStatement ps1 = sqliteUtil.getPreparedStatement(sql1);
            ps1.setInt(1, MainSence.getCurrentID());
            ps1.setString(2, MainSence.getCurrentFileName());
            resultSet = ps1.executeQuery();
            //1.3Update the display based on the query results
            if (resultSet.next()) {
                byte[] msData = ServiceCenter.plotMS(resultSet.getBytes("mz"), resultSet.getBytes("intensities"));
                //Set MSTab as clickable
                ByteArrayInputStream bais = new ByteArrayInputStream(msData);
                Platform.runLater(() -> {
                    msImageView.setImage(new Image(bais));
                    resultTabPane.getTabs().get(0).setDisable(false);
                });
            } else {
                Platform.runLater(() -> {
                    msImageView.setImage(null);
                    resultTabPane.getTabs().get(0).setDisable(true);
                });
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


    public void refreshCandidatesTab(TabPane resultTabPane) {
        SqliteUtil sqliteUtil = null;
        ResultSet resultSet = null;
        try {
            sqliteUtil = new SqliteUtil(ServiceCenter.getDbPath() + "candidate.db");

            //2.Refresh CandidatesTab
            //2.0Clear all rows
            Platform.runLater(() -> {
                CandidatesAnchorPaneSence.clearCandidatesTableRows();
                resultTabPane.getTabs().get(1).setDisable(true);
            });

            //2.1Find the TableView object by using the id.
            TableView tableView = (TableView) resultTabPane.lookup("#candidatesTableView");
            //2.2Get table column: TableColumn
            TableColumn<CandidatesTableRow, String> inchikeyColumn = (TableColumn) tableView.getColumns().get(0);
            TableColumn<CandidatesTableRow, String> smilesColumn = (TableColumn) tableView.getColumns().get(1);
            TableColumn<CandidatesTableRow, BigDecimal> mwColumn = (TableColumn) tableView.getColumns().get(2);
            TableColumn<CandidatesTableRow, BigDecimal> distanceColumn = (TableColumn) tableView.getColumns().get(3);
            TableColumn<CandidatesTableRow, Integer> rankColumn = (TableColumn) tableView.getColumns().get(4);
            //2.3Establish the association between the TableColumn and the class CandidatesTableRow
            inchikeyColumn.setCellValueFactory(new PropertyValueFactory<>("inchikey"));
            smilesColumn.setCellValueFactory(new PropertyValueFactory<>("smiles"));
            mwColumn.setCellValueFactory(new PropertyValueFactory<>("mw"));
            distanceColumn.setCellValueFactory(new PropertyValueFactory<>("distance"));
            rankColumn.setCellValueFactory(new PropertyValueFactory<>("rank"));
            //2.4Bind the rows of the TableView to CandidatesAnchorPaneSense.getCandidatesTableRows()
            tableView.setItems(CandidatesAnchorPaneSence.getCandidatesTableRows());

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
                    BigDecimal bigDistance = new BigDecimal(distance).setScale(4, RoundingMode.HALF_UP);
                    int rank = resultSet.getInt("rank");
                    // Add data to the table rows
                    Platform.runLater(() -> {
                        CandidatesAnchorPaneSence.addCandidatesTableRow(new CandidatesTableRow(pid, inchikey, smiles, bigMW, bigDistance, rank));
                    });


                } while (resultSet.next());

                Platform.runLater(() -> {
                    resultTabPane.getTabs().get(1).setDisable(false);
                });

            } else {
                Platform.runLater(() -> {
                    CandidatesAnchorPaneSence.clearCandidatesTableRows();
                    resultTabPane.getTabs().get(1).setDisable(true);
                });

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

