package de.wethinkco.database;

import com.fasterxml.jackson.databind.JsonNode;

import java.sql.*;
import java.text.MessageFormat;
import java.util.*;
import java.util.regex.Pattern;

public class SQLiteConnector implements DatabaseConnectorInterface {
    private final Connection dbConnection;

    public SQLiteConnector(String dbUrl) throws SQLException {
        this.dbConnection =
                DriverManager.getConnection("jdbc:sqlite:" + dbUrl);
    }

    private String getTableCreationStatement(String tableName) {
        return MessageFormat.format(
                "CREATE TABLE IF NOT EXISTS \"{0}\" " +
                        "({0}_id INTEGER PRIMARY KEY AUTOINCREMENT",
                tableName
        );
    }

    private String getForeignKeyReference(
            String columnName,
            String foreignTableName
    ) {
        return MessageFormat.format(
                ", FOREIGN KEY ({0}) " +
                        "REFERENCES {1} ({1}_id) " +
                        "ON UPDATE CASCADE ON DELETE CASCADE",
                columnName,
                foreignTableName
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
        String foreignTableName = null;
        String tempColumnName = columnName;

        boolean isForeignKey = true;

        if (checkIsReferenceTable(parentTableName)) {
            foreignTableName = parentTableName;
            tempColumnName = null;
            isForeignKey = false;
        }

        String subTableName = getTableReference(parentTableName) + columnName;

        createTable(subTableName, tableData, foreignTableName);

        return new ColumnStatement(
                tempColumnName,
                "INTEGER",
                subTableName,
                isForeignKey
        );
    }

    private ColumnStatement getColumnStatement(
            String columnName,
            JsonNode columnValue,
            String tableName
    ) throws SQLException {

        String dataType = "INTEGER";

        switch (columnValue.getNodeType()) {
            case NUMBER:
                break;
            case ARRAY:
                return getSubTableColumnStatement(
                        columnName,
                        columnValue.elements().next(),
                        tableName
                );
            case OBJECT:
                return getSubTableColumnStatement(
                        columnName,
                        columnValue,
                        tableName
                );
            default:
                dataType = "TEXT";
        }

        return new ColumnStatement(columnName, dataType,false);
    }

    private List<ColumnStatement> getColumnStatements(
            JsonNode tableData,
            String tableName
    ) throws SQLException {
        List<ColumnStatement> returnList = new ArrayList<>();
        for (
                Iterator<Map.Entry<String, JsonNode>> it = tableData.fields();
                it.hasNext();
        ) {
            Map.Entry<String, JsonNode> jsonNodeMap = it.next();
            returnList.add(
                    getColumnStatement(
                            jsonNodeMap.getKey(),
                            jsonNodeMap.getValue(),
                            tableName
                    )
            );
        }
        return returnList;
    }

    private void createTable(
            String tableName,
            JsonNode tableData,
            String parentTableName
    ) throws SQLException {

        Map<String, String> foreignKeys = new HashMap<>();

        StringBuilder statementBuilder = new StringBuilder();

        statementBuilder.append(getTableCreationStatement(tableName));

        for (
                ColumnStatement columnStatement :
                getColumnStatements(tableData, tableName)
        ) {
            statementBuilder.append(columnStatement.getStatement());

            if (columnStatement.isForeignKey())
            {
                foreignKeys.put(
                        "_" + columnStatement.getColumnName(),
                        columnStatement.getForeignTableName()
                );
            }
        }

        if (null != parentTableName) {
            statementBuilder.append(
                    String.format(
                            ", %s_id INTEGER NOT NULL",
                            parentTableName
                    )
            );
            foreignKeys.put(parentTableName + "_id", parentTableName);
        }

        for (Map.Entry<String, String> entry : foreignKeys.entrySet()) {
            statementBuilder.append(
                    getForeignKeyReference(entry.getKey(), entry.getValue())
            );
        }

        statementBuilder.append(")");
        System.out.println(statementBuilder);
        Statement statement = dbConnection.createStatement();
        statement.executeUpdate(statementBuilder.toString());
    }

    private void createDb(DbData dbData) throws SQLException {
        createTable(
                "reference_" + dbData.reference,
                dbData.data,
                null
        );
    }

    private void updateReferenceTable(String reference) throws SQLException {
        Statement statement = dbConnection.createStatement();
        statement.executeUpdate(
                String.format(
                        "INSERT INTO reference_%s DEFAULT VALUES",
                        reference
                )
        );
    }

    private int getReferenceTableLastIndex(String reference)
            throws SQLException {
        Statement statement = dbConnection.createStatement();
        ResultSet result =
                statement.executeQuery(
                        MessageFormat.format(
                                "SELECT * FROM reference_{0} WHERE " +
                                        "reference_{0}_id = (" +
                                        "SELECT MAX(reference_{0}_id) FROM " +
                                        "reference_{0})",
                                reference
                        )
                );
        return result.getInt("reference_" + reference + "_id");
    }

    @Override
    public void saveData(DbData dbData) throws Exception {
        createDb(dbData);

        String reference = dbData.reference;

        updateReferenceTable(reference);
        System.out.println(getReferenceTableLastIndex(reference));
    }

    public void lol() throws SQLException {
        Statement statement = dbConnection.createStatement();
        statement.execute("SELECT * FROM sqlite_master WHERE type='table'");
        ResultSet resultSet = statement.getResultSet();
        while (resultSet.next()) {
            System.out.println(resultSet.getString("name"));
        }
    }
}
