package de.wethinkco.database;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.wethinkco.database.world.World;
import de.wethinkco.database.world.WorldData;
import de.wethinkco.database.world.WorldObject;

import java.sql.SQLException;
import java.util.List;

public class Main {
    public static void main(String[] args) throws JsonProcessingException, SQLException {
        ObjectMapper objectMapper = new ObjectMapper();

        WorldData worldData = new WorldData(1, 1, 3, 3, 3, 5);
        WorldObject obstacle = new WorldObject(5, 5, 1, 1);
        World world = new World(worldData, List.of(obstacle));
//        {"objectData":{"size":5,"position":{"x":0,"y":1}}}
        String jsonString = objectMapper.writeValueAsString(world);
        JsonNode jsonNode = objectMapper.readTree("{\"objectData\":{\"size\":5,\"position\":{\"x\":0,\"y\":1}}}");
        DbData dbData = new DbData("world", jsonNode);

        System.out.println(jsonString);

        SQLiteConnector sqliteConnector = new SQLiteConnector("testdb.db");
        sqliteConnector.saveData(dbData);
    }
}
