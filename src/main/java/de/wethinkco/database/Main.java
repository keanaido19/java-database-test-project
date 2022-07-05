package de.wethinkco.database;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.wethinkco.database.world.World;
import de.wethinkco.database.world.WorldData;
import de.wethinkco.database.world.WorldObject;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws JsonProcessingException, SQLException {
        ObjectMapper objectMapper = new ObjectMapper();

        WorldData worldData = new WorldData(1, 1, 3, 3, 3, 5);
        WorldObject obstacle = new WorldObject(5, 5, 1, 1);
        World world = new World(worldData, List.of(obstacle));

        String jsonString = objectMapper.writeValueAsString(world);
        JsonNode jsonNode = objectMapper.readTree(jsonString);
        DbData dbData = new DbData("world", jsonNode);

        System.out.println(jsonString);

        SQLite3 sqlite3 = new SQLite3("testdb.db");
        sqlite3.createDb(dbData);
    }
}
