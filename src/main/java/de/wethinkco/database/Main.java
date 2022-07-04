package de.wethinkco.database;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.wethinkco.database.world.JsonWorldData;
import de.wethinkco.database.world.World;
import de.wethinkco.database.world.WorldObject;

import java.util.List;

public class Main {
    public static void main(String[] args) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();

        World world = new World(1, 1, 3, 3, 3, 5);
        WorldObject obstacle = new WorldObject(5, 5, 1, 1);
        JsonWorldData worldData = new JsonWorldData(world, List.of(obstacle));

        String jsonString = objectMapper.writeValueAsString(worldData);
        JsonNode jsonNode = objectMapper.readTree(jsonString);

        System.out.println(jsonString);
        for (JsonNode node : jsonNode) {
            System.out.println(node.getNodeType());
        }
    }
}
