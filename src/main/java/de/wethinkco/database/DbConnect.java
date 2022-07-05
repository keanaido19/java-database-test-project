package de.wethinkco.database;

import com.fasterxml.jackson.databind.JsonNode;

public class DbConnect {
    private final JsonNode JsonNode;
    private final DatabaseConnectorInterface dbInterface;

    public DbConnect(JsonNode jsonNode, DatabaseConnectorInterface dbInterface) {
        JsonNode = jsonNode;
        this.dbInterface = dbInterface;
    }
}
