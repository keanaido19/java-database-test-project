package de.wethinkco.database;

public class ColumnStatement {
    private final String columnName;
    private final String dataType;
    private final String foreignTableName;
    private final boolean isForeignKey;

    public ColumnStatement(
            String columnName,
            String dataType,
            String foreignTableName,
            boolean isForeignKey
    ) {
        this.columnName = columnName;
        this.dataType = dataType;
        this.foreignTableName = foreignTableName;
        this.isForeignKey = isForeignKey;
    }

    public ColumnStatement(
            String columnName,
            String dataType,
            boolean isForeignKey
    ) {
        this(columnName, dataType, null, isForeignKey);
    }

    public String getColumnName() {
        return columnName;
    }

    public String getForeignTableName() {
        return foreignTableName;
    }

    public boolean isForeignKey() {
        return isForeignKey;
    }

    public String getStatement() {
        if (null == columnName) return "";
        return ", " + columnName + " " + dataType + " NOT NULL";
    }
}
