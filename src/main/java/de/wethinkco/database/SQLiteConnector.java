package de.wethinkco.database;

import com.fasterxml.jackson.databind.JsonNode;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;

public class SQLiteConnector implements DatabaseConnectorInterface {
    private final Connection dbConnection;

    public SQLiteConnector(String dbUrl) throws SQLException {
        this.dbConnection =
                DriverManager.getConnection("jdbc:sqlite:" + dbUrl);
    }

    @Override
    public void saveData(DbData dbData) {
        try {
            createDb(dbData);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private void createDb(DbData dbData) throws SQLException {
        createTable(
                "reference_" + dbData.reference,
                dbData.data,
                null
        );
    }

    private String getTableCreationStatement(String tableName) {
        return MessageFormat.format(
                "CREATE TABLE IF NOT EXISTS \"{0}\" " +
                        "({0}_id INTEGER PRIMARY KEY AUTOINCREMENT",
                tableName
        );
    }

    private String getForeignKeyReference(String foreignTableName) {
        return MessageFormat.format(
                ", FOREIGN KEY ({0}_id) " +
                        "REFERENCES {0} ({0}_id) ON UPDATE CASCADE " +
                        "ON DELETE CASCADE",
                foreignTableName
        );
    }

    private boolean checkIsReferenceTable(String tableName) {
        Pattern pattern = Pattern.compile("^reference_.*$");
        return pattern.matcher(tableName).find();
    }

    private String getTableReference(String tableName) {
        if (checkIsReferenceTable(tableName)) {
            String[] referenceTableNameArray =
                    tableName.split("reference_", 2);
            return referenceTableNameArray[referenceTableNameArray.length - 1];
        }
        return tableName;
    }

    private ColumnStatement getSubTableColumnStatement(
            String columnName,
            JsonNode tableData,
            String parentTableName
    ) throws SQLException {
        String subTableName = getTableReference(parentTableName) + columnName;
        String foreignTableName = null;
        String tempColumnName = subTableName;

        boolean isForeignKey = true;

        if (checkIsReferenceTable(parentTableName)) {
            foreignTableName = parentTableName;
            tempColumnName = null;
            isForeignKey = false;
        }

        createTable(
                subTableName,
                tableData,
                foreignTableName
        );

        return new ColumnStatement(
                tempColumnName,
                "INTEGER",
                isForeignKey
        );
    }

    private ColumnStatement getColumnStatement(
            String columnName,
            JsonNode columnValue,
            String parentTableName
    ) throws SQLException {

        String dataType = "INTEGER";

        switch (columnValue.getNodeType()) {
            case NUMBER:
                break;
            case ARRAY:
                return getSubTableColumnStatement(
                        columnName,
                        columnValue.elements().next(),
                        parentTableName
                );
            case OBJECT:
                return getSubTableColumnStatement(
                        columnName,
                        columnValue,
                        parentTableName
                );
            default:
                dataType = "TEXT";
        }

        return new ColumnStatement(columnName, dataType, false);
    }

    private void createTable(
            String tableName,
            JsonNode tableData,
            String foreignTableName
    ) throws SQLException {

        ArrayList<String> foreignKeys = new ArrayList<>();

        StringBuilder statementBuilder = new StringBuilder();

        statementBuilder.append(getTableCreationStatement(tableName));

        for (
                Iterator<Map.Entry<String, JsonNode>> it = tableData.fields();
                it.hasNext();
        ) {
            Map.Entry<String, JsonNode> jsonNodeMap = it.next();
            ColumnStatement columnStatement =
                    getColumnStatement(
                            jsonNodeMap.getKey(),
                            jsonNodeMap.getValue(),
                            tableName
                    );
            statementBuilder.append(columnStatement.getStatement());
            if (columnStatement.isForeignKey())
                foreignKeys.add(columnStatement.getForeignTableName());
        }

        if (null != foreignTableName) {
            statementBuilder.append(
                    String.format(
                            ", %s_id INTEGER NOT NULL",
                            foreignTableName
                    )
            );
            foreignKeys.add(foreignTableName);
        }

        for (String foreignKey : foreignKeys) {
            statementBuilder.append(getForeignKeyReference(foreignKey));
        }

        statementBuilder.append(")");
        System.out.println(statementBuilder);
        Statement statement = dbConnection.createStatement();
        statement.executeUpdate(statementBuilder.toString());
    }
}
