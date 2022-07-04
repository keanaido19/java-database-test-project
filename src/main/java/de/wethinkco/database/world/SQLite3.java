package de.wethinkco.database.world;

import com.fasterxml.jackson.databind.JsonNode;
import de.wethinkco.database.DatabaseInterface;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.Map;

public class SQLite3 implements DatabaseInterface {
    private final Connection dbConnection;

    public SQLite3(String dbUrl) throws SQLException {
        this.dbConnection =
                DriverManager.getConnection("jdbc:sqlite:" + dbUrl);
    }

    public void createDb(JsonNode dbData) throws SQLException {
        for (
                Iterator<Map.Entry<String, JsonNode>> it = dbData.fields();
                it.hasNext();
        ) {
            Map.Entry<String, JsonNode> jsonNodeMap = it.next();
            createTable(jsonNodeMap.getKey(), jsonNodeMap.getValue());
        }
    }

    private void createTable(
            String tableName,
            JsonNode tableData,
            String foreignTableName
    ) throws SQLException {

        StringBuilder statementBuilder = new StringBuilder();

        statementBuilder
                .append("CREATE TABLE IF NOT EXISTS ")
                .append(tableName)
                .append("(")
                .append(tableName)
                .append("_id INTEGER PRIMARY KEY AUTOINCREMENT");

        for (
                Iterator<Map.Entry<String, JsonNode>> it = tableData.fields();
                it.hasNext();
        ) {
            Map.Entry<String, JsonNode> jsonNodeMap = it.next();
            statementBuilder
                    .append(
                            getColumnStatement(
                                    jsonNodeMap.getKey(),
                                    jsonNodeMap.getValue(),
                                    tableName
                            )
                    );
        }

        if (null != foreignTableName) {
            statementBuilder
                    .append(", ")
                    .append(foreignTableName)
                    .append("_id INTEGER")
                    .append(", ")
                    .append("FOREIGN KEY (")
                    .append(foreignTableName)
                    .append("_id) REFERENCES ")
                    .append(foreignTableName)
                    .append(" (")
                    .append(foreignTableName)
                    .append("_id) ON UPDATE CASCADE ON DELETE CASCADE");
        }

        statementBuilder.append(")");
        System.out.println(statementBuilder);
        Statement statement = dbConnection.createStatement();
        statement.executeUpdate(statementBuilder.toString());
    }

    private void createTable(String tableName, JsonNode tableData)
            throws SQLException {
        createTable(tableName, tableData, null);
    }

    private String getColumnStatement(
            String columnName,
            JsonNode columnValue,
            String tableName
    ) throws SQLException {

        String dataType = "INTEGER";

        switch (columnValue.getNodeType()) {
            case NUMBER:
                break;
            case ARRAY:
                createTable(
                        columnName,
                        columnValue.elements().next(),
                        tableName
                );
                return "";
            case OBJECT:
                createTable(columnName, columnValue, tableName);
                return "";
            default:
                dataType = "TEXT";
        }

        return ", " + columnName + " " + dataType + " NOT NULL";
    }
}
