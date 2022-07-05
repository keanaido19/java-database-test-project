package de.wethinkco.database;

public class ColumnStatement {
    private final String columnName;
    private final String dataType;
    private final boolean isForeignKey;

    public ColumnStatement(
            String columnName,
            String dataType,
            boolean isForeignKey
    ) {
        this.columnName = columnName;
        this.dataType = dataType;
        this.isForeignKey = isForeignKey;
    }

    public String getStatement() {
        if (null == columnName) return "";

        String spacer = " ";
        if (isForeignKey) spacer = "_id ";

        return ", " + columnName + spacer + dataType + " NOT NULL";
    }

    public String getForeignTableName() {
        return columnName;
    }

    public boolean isForeignKey() {
        return isForeignKey;
    }
}
