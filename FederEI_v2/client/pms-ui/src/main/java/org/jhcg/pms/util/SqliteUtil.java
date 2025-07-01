package org.jhcg.pms.util;


import java.sql.*;

/**
 * sqlite帮助类，直接创建该类示例，并调用相应的接口即可对sqlite数据库进行操作
 *
 * @since JDK
 */
public class SqliteUtil {

    private Connection connection;
    private Statement statement;
    PreparedStatement preparedStatement;
    private String dbFilePath;

    /**
     * 构造函数
     *
     * @param dbFilePath sqlite db 文件路径
     */
    public SqliteUtil(String dbFilePath) throws ClassNotFoundException, SQLException {
        this.dbFilePath = dbFilePath;
        connection = getConnection(dbFilePath);
    }


    /**
     * 获取数据库连接
     *
     * @param dbFilePath db文件路径
     * @return java.sql.Connection
     */
    private Connection getConnection(String dbFilePath) throws ClassNotFoundException, SQLException {
        Connection conn = null;
        Class.forName("org.sqlite.JDBC");
        conn = DriverManager.getConnection("jdbc:sqlite:" + dbFilePath);
        return conn;
    }

    private Connection getConnection() throws ClassNotFoundException, SQLException {
        if (null == connection) connection = getConnection(dbFilePath);
        return connection;
    }

    /**
     * 获取preparedStatement对象，使用要记得调用destroyed()释放资源，查询时要释放ResultSet
     *
     * @param
     * @return java.sql.PreparedStatement
     */
    public PreparedStatement getPreparedStatement(String sql) throws SQLException, ClassNotFoundException {
        if(preparedStatement != null){
            destroyed();
        }
        preparedStatement = getConnection().prepareStatement(sql);
        return preparedStatement;
    }

    public Statement getStatement() throws SQLException, ClassNotFoundException {
        if (statement == null) statement = getConnection().createStatement();
        return statement;
    }

    /**
     * 数据库资源关闭和释放
     */
    public void destroyed() {
        try {

            if (preparedStatement != null) {
                preparedStatement.close();
                preparedStatement = null;
            }

            if (statement != null) {
                statement.close();
                statement = null;
            }

            if (connection != null) {
                connection.close();
                connection = null;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}