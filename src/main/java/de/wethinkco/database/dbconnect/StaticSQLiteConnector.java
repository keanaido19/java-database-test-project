package de.wethinkco.database.dbconnect;

import java.sql.*;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class StaticSQLiteConnector {
    private final Connection dbConnection;

    public StaticSQLiteConnector(String dbUrl) throws SQLException {
        this.dbConnection =
                DriverManager.getConnection("jdbc:sqlite:" + dbUrl);
    }

    private void createWorldDataTable() throws SQLException {
        Statement sqlStatement = dbConnection.createStatement();
        sqlStatement.executeUpdate(
                "CREATE TABLE IF NOT EXISTS worldData(" +
                        "width INTEGER NOT NULL, " +
                        "height INTEGER NOT NULL, " +
                        "repairTime INTEGER NOT NULL, " +
                        "reloadTime INTEGER NOT NULL, " +
                        "mineTime INTEGER NOT NULL, " +
                        "maxShield INTEGER NOT NULL, " +
                        "world_id INTEGER UNIQUE NOT NULL, " +
                        "FOREIGN KEY (world_id) REFERENCES " +
                        "world (world_id) ON DELETE CASCADE ON UPDATE CASCADE)"
        );
    }

    private void createObjectTable(String tableName) throws SQLException {
        Statement sqlStatement = dbConnection.createStatement();
        sqlStatement.executeUpdate(
                MessageFormat.format(
                        "CREATE TABLE IF NOT EXISTS {0}(" +
                                "{0}_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                "width INTEGER NOT NULL, " +
                                "height INTEGER NOT NULL, " +
                                "x INTEGER NOT NULL, " +
                                "y INTEGER NOT NULL, " +
                                "world_id INTEGER NOT NULL, " +
                                "FOREIGN KEY (world_id) REFERENCES world" +
                                "(world_id))",
                        tableName
                )
        );
    }

    private void createWorldTable() throws SQLException {
        Statement sqlStatement = dbConnection.createStatement();
        sqlStatement.executeUpdate(
                "CREATE TABLE IF NOT EXISTS world(" +
                        "world_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "name TEXT UNIQUE NOT NULL)"
        );
    }

    private void createTables() throws SQLException {
        createWorldTable();
        createWorldDataTable();
        createObjectTable("obstacles");
        createObjectTable("mines");
        createObjectTable("pits");
    }

    private int getLastTableIndex(String tableName) throws SQLException {
        Statement statement = dbConnection.createStatement();
        ResultSet result =
                statement.executeQuery(
                        MessageFormat.format(
                                "SELECT * FROM {0} WHERE {0}_id = " +
                                        "(SELECT MAX({0}_id) FROM {0})",
                                tableName
                        )
                );
        if (result.isClosed()) return 0;
        return result.getInt(tableName + "_id");
    }

    private void saveWorldName(String worldName) throws SQLException {
        Statement statement = dbConnection.createStatement();
        String sqlStatement;
        if (worldName == null) {
            sqlStatement = MessageFormat.format(
                    "INSERT INTO world(world_id, name) " +
                            "VALUES({0}, {0})",
                    getLastTableIndex("world") + 1
            );
        } else {
            sqlStatement = String.format(
                    "INSERT INTO world(name) VALUES(%s)",
                    worldName
            );
        }
        statement.executeUpdate(sqlStatement);
    }

    private void saveObjects(
            String tableName,
            List<WorldObjectDbData> objects,
            int world_id
    ) throws SQLException {
        Statement statement = dbConnection.createStatement();
        for (WorldObjectDbData object : objects) {
            statement.executeUpdate(
                    MessageFormat.format(
                            "INSERT INTO {0}" +
                                    "(width, height, x, y, world_id) " +
                                    "VALUES({1}, {2}, {3}, {4}, {5})",
                            tableName,
                            object.getWidth(),
                            object.getHeight(),
                            object.getX(),
                            object.getY(),
                            world_id
                    )
            );
        }
    }

    private void saveObstacles(List<WorldObjectDbData> obstacles, int world_id)
            throws SQLException {
        saveObjects("obstacles", obstacles, world_id);
    }

    private void savePits(List<WorldObjectDbData> pits, int world_id)
            throws SQLException {
        saveObjects("pits", pits, world_id);
    }

    private void saveMines(List<WorldObjectDbData> mines, int world_id)
            throws SQLException {
        saveObjects("mines", mines, world_id);
    }

    public void saveWorld(String worldName, WorldDbObject world)
            throws SQLException {
        createTables();
        saveWorldName(worldName);
        int world_id = getLastTableIndex("world");
        saveObstacles(world.getObstacles(), world_id);
        savePits(world.getPits(), world_id);
        saveMines(world.getMines(), world_id);
    }

    private int getWorldID(String worldName) throws SQLException {
        Statement statement = dbConnection.createStatement();
        ResultSet resultSet =
                statement.executeQuery(
                        String.format(
                                "SELECT * FROM world WHERE name = %s",
                                worldName
                        )
                );
        if (resultSet.isClosed())
            throw new SQLException("Name does not exist!");

        return resultSet.getInt("world_id");
    }

    private ResultSet getResultsLinkedToWorldID(
            String tableName,
            int world_id
    ) throws SQLException {
        Statement statement = dbConnection.createStatement();
        return statement.executeQuery(
                MessageFormat.format(
                        "SELECT * FROM {0} WHERE world_id = {1}",
                        tableName,
                        world_id
                )
        );
    }

    private WorldDataDbObject getWorldData(int world_id) throws SQLException {
        ResultSet resultSet =
                getResultsLinkedToWorldID("worldData", world_id);
        if (resultSet.isClosed()) return new WorldDataDbObject();
        return new WorldDataDbObject(
                resultSet.getInt("width"),
                resultSet.getInt("height"),
                resultSet.getInt("repairTime"),
                resultSet.getInt("reloadTime"),
                resultSet.getInt("mineTime"),
                resultSet.getInt("maxShield")
        );
    }

    private List<WorldObjectDbData> getWorldObjects(
            String tableName,
            int world_id
    ) throws SQLException {
        ArrayList<WorldObjectDbData> returnList = new ArrayList<>();

        ResultSet resultSet =
                getResultsLinkedToWorldID(tableName, world_id);

        while (!resultSet.isClosed() && resultSet.next()) {
            returnList.add(
                    new WorldObjectDbData(
                            resultSet.getInt("width"),
                            resultSet.getInt("height"),
                            resultSet.getInt("x"),
                            resultSet.getInt("y")
                    )
            );
        }

        return returnList;
    }

    public WorldDbObject restoreWorld(String worldName) throws SQLException {
        int world_id = getWorldID(worldName);
        return new WorldDbObject(
                getWorldData(world_id),
                getWorldObjects("obstacles", world_id),
                getWorldObjects("pits", world_id),
                getWorldObjects("mines", world_id)
        );
    }
}
