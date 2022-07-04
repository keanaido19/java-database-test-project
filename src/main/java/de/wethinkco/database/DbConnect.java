package de.wethinkco.database;

import com.fasterxml.jackson.databind.JsonNode;

public class DbConnect {
    private final JsonNode JsonNode;
    private final DatabaseInterface dbInterface;

    public DbConnect(JsonNode jsonNode, DatabaseInterface dbInterface) {
        JsonNode = jsonNode;
        this.dbInterface = dbInterface;
    }
}
