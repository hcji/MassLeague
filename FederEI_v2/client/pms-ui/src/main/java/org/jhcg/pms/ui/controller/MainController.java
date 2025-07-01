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

    //openedMSListView的子项列表
    ObservableList<String> openedMSItems = FXCollections.observableArrayList();

    @FXML
    private TextField filterTextField;


    //----------以下代码为控件绑定事件----------

    @FXML
    void openedMSListViewDragDropped(DragEvent event) {
        Dragboard dragboard = event.getDragboard();
        if (dragboard.hasFiles()) {
            //获取拖拽到窗口的文件列表，筛选出.mgf或.msp结尾的文件
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
                        //写入候选物文件代表化合物的inchikey
                        idToInchikeyContent.append(id + "," + inchikey + "\n");
                    }

                    //创建保存的文件
                    File idToInchikey = new File(saveDirectory.getAbsolutePath(), "IDToInchikey.csv");
                    FileWriter writer1 = new FileWriter(idToInchikey, true);

                    writer1.write(idToInchikeyContent.toString());
                    writer1.close();

                    System.out.println("IDToInchikey.csv over");

                    //搜索所有的保留物信息
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
                            // 如果不存在，则创建一个新的列表
                            idToResultMap.put(id, new ArrayList<>());
                        }

                        // 将记录添加到相应的列表中
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

                        //创建保存候选物信息的文件
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
            //1.获取已打开文件的列表
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

            //2.依次对每个文件调用服务
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
            //清空谱图
            ImageView msImageView = (ImageView) resultTabPane.lookup("#msImageView");
            //清空候选物列表
            Platform.runLater(()->{
                msImageView.setImage(null);
                CandidatesAnchorPaneSence.clearCandidatesTableRows();
                //清空质谱列表
                openedMSItems.clear();
            });
            //清空缓存
            ServiceCenter.clearCache();

        });
        thread.setDaemon(true);
        thread.start();
    }

    //----------以下代码是业务代码----------
    private void addMSFile(List<File> files) {
        SqliteUtil sqliteUtil = null;
        ResultSet resultSet = null;
        try {
            for (File file : files) {
//                //1.过滤已被添加的文件
                //2.调用文件上传，创建文件对应的数据库，并将质谱id，文件名，title存入数据库
                ServiceCenter.submitFile(file.getCanonicalPath());

//                //2.显示质谱的文件
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
            //2.显示质谱的文件
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
     * 初始化：
     * 1.为TabPane的Tab设置内容
     * 2.为openedMSListView的选中事件绑定监听
     * 3.给candidatesTableView列表项绑定选中事件
     */
    public void initialize() {
        //把resultTabPane列表绑定到全局Sence
        MainSence.setResultTabPane(resultTabPane);
        //1初始化TabPane
        addToTabPane("/fxml/MSAnchorPane.fxml", "MS", resultTabPane);
        addToTabPane("/fxml/CandidatesAnchorPane.fxml", "Candidates", resultTabPane);

        //2.为openedMSListView初始化相关内容
        //2.1绑定列表项对象
        openedMSListView.setItems(openedMSItems);
        //2.2为选中事件绑定监听：刷新显示界面
        openedMSListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                //获取选中值的文件名和id,并存储在MainSence的静态变量中
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
        //3.为filterTextField绑定
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
     * 添加Tab到TabPane
     *
     * @param path    fxml文件路径
     * @param tabName Tab的名称
     */
    public void addToTabPane(String path, String tabName, TabPane tabPane) {
        //1.1声明AnchorPane变量
        AnchorPane anchorPane = null;
        try {
            //1.2读取读取fxml文件并赋值给AnchorPane变量
            anchorPane = FXMLLoader.load(Objects.requireNonNull(MainSence.class.getResource(path)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //1.3用AnchorPane对象创建对应的Tab，再把Tab添加到TabPane
        Tab tab = new Tab(tabName, anchorPane);
        tab.setDisable(true);
        tab.setId(tabName + "Tab");
        tabPane.getTabs().add(tab);
    }

    /**
     * 响应ListView点击事件刷新UI界面
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

            //1.刷新MSTab
            //1.1通过id查找到ImageView对象
            ImageView msImageView = (ImageView) resultTabPane.lookup("#msImageView");
            Platform.runLater(() -> {
                msImageView.setImage(null);
                resultTabPane.getTabs().get(0).setDisable(true);
            });
            //1.2进行数据库操作
            String sql1 = "select mz,intensities from file_data where id=? and file_name=?;";
            PreparedStatement ps1 = sqliteUtil.getPreparedStatement(sql1);
            ps1.setInt(1, MainSence.getCurrentID());
            ps1.setString(2, MainSence.getCurrentFileName());
            resultSet = ps1.executeQuery();
            //1.3根据查询结果更新显示
            if (resultSet.next()) {
                byte[] msData = ServiceCenter.plotMS(resultSet.getBytes("mz"), resultSet.getBytes("intensities"));
                //设置MSTab为可点击
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

            //2.刷新CandidatesTab
            //2.0清空所有行
            Platform.runLater(() -> {
                CandidatesAnchorPaneSence.clearCandidatesTableRows();
                resultTabPane.getTabs().get(1).setDisable(true);
            });

            //2.1通过id查找到TableView对象
            TableView tableView = (TableView) resultTabPane.lookup("#candidatesTableView");
            //2.2获取表格列：TableColumn
            TableColumn<CandidatesTableRow, String> inchikeyColumn = (TableColumn) tableView.getColumns().get(0);
            TableColumn<CandidatesTableRow, String> smilesColumn = (TableColumn) tableView.getColumns().get(1);
            TableColumn<CandidatesTableRow, BigDecimal> mwColumn = (TableColumn) tableView.getColumns().get(2);
            TableColumn<CandidatesTableRow, BigDecimal> distanceColumn = (TableColumn) tableView.getColumns().get(3);
            TableColumn<CandidatesTableRow, Integer> rankColumn = (TableColumn) tableView.getColumns().get(4);
            //2.3设置列TableColumn与类CandidatesTableRow的关联性
            inchikeyColumn.setCellValueFactory(new PropertyValueFactory<>("inchikey"));
            smilesColumn.setCellValueFactory(new PropertyValueFactory<>("smiles"));
            mwColumn.setCellValueFactory(new PropertyValueFactory<>("mw"));
            distanceColumn.setCellValueFactory(new PropertyValueFactory<>("distance"));
            rankColumn.setCellValueFactory(new PropertyValueFactory<>("rank"));
            //2.4绑定TableView行与CandidatesAnchorPaneSence.getCandidatesTableRows()
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
                    // 添加表格行数据
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

