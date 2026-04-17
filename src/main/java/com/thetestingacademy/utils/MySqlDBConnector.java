package com.thetestingacademy.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class MySqlDBConnector {

    private static final String DEFAULT_HOST = "localhost";
    private static final String DEFAULT_PORT = "3306";
    private static final String DEFAULT_JDBC_PARAMS =
            "useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";

    private MySqlDBConnector() {
    }

    public static Connection getConnection() {
        String databaseName = EnvUtil.getRequired("MYSQL_DATABASE");
        return getConnection(
                buildJdbcUrl(databaseName),
                EnvUtil.getRequired("MYSQL_USERNAME"),
                EnvUtil.getRequired("MYSQL_PASSWORD")
        );
    }

    public static Connection getConnection(String databaseName) {
        return getConnection(
                buildJdbcUrl(databaseName),
                EnvUtil.getRequired("MYSQL_USERNAME"),
                EnvUtil.getRequired("MYSQL_PASSWORD")
        );
    }

    public static Connection getConnection(String jdbcUrl, String username, String password) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(jdbcUrl, username, password);
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException("Unable to establish MySQL database connection.", e);
        }
    }

    public static String buildJdbcUrl(String databaseName) {
        String host = EnvUtil.getOptional("MYSQL_HOST", DEFAULT_HOST);
        String port = EnvUtil.getOptional("MYSQL_PORT", DEFAULT_PORT);
        String jdbcParams = EnvUtil.getOptional("MYSQL_JDBC_PARAMS", DEFAULT_JDBC_PARAMS);
        return String.format("jdbc:mysql://%s:%s/%s?%s", host, port, databaseName, jdbcParams);
    }

    public static boolean isConnectionValid() {
        try (Connection connection = getConnection()) {
            return connection.isValid(2);
        } catch (SQLException e) {
            throw new RuntimeException("Unable to validate MySQL database connection.", e);
        }
    }

    public static List<Map<String, Object>> executeSelect(String query, Object... params) {
        try (Connection connection = getConnection()) {
            return executeSelect(connection, query, params);
        } catch (SQLException e) {
            throw new RuntimeException("Unable to close MySQL database connection.", e);
        }
    }

    public static List<Map<String, Object>> executeSelect(Connection connection, String query, Object... params) {
        try (PreparedStatement statement = prepareStatement(connection, query, params);
             ResultSet resultSet = statement.executeQuery()) {
            return mapRows(resultSet);
        } catch (SQLException e) {
            throw new RuntimeException("Unable to execute select query: " + query, e);
        }
    }

    public static int executeUpdate(String query, Object... params) {
        try (Connection connection = getConnection()) {
            return executeUpdate(connection, query, params);
        } catch (SQLException e) {
            throw new RuntimeException("Unable to close MySQL database connection.", e);
        }
    }

    public static int executeUpdate(Connection connection, String query, Object... params) {
        try (PreparedStatement statement = prepareStatement(connection, query, params)) {
            return statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Unable to execute update query: " + query, e);
        }
    }

    public static PreparedStatement prepareStatement(Connection connection, String query, Object... params) {
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            bindParameters(statement, params);
            return statement;
        } catch (SQLException e) {
            throw new RuntimeException("Unable to prepare MySQL statement: " + query, e);
        }
    }

    private static void bindParameters(PreparedStatement statement, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            statement.setObject(i + 1, params[i]);
        }
    }

    private static List<Map<String, Object>> mapRows(ResultSet resultSet) throws SQLException {
        List<Map<String, Object>> rows = new ArrayList<>();
        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();

        while (resultSet.next()) {
            Map<String, Object> row = new LinkedHashMap<>();
            for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
                row.put(metaData.getColumnLabel(columnIndex), resultSet.getObject(columnIndex));
            }
            rows.add(row);
        }

        return rows;
    }
}
