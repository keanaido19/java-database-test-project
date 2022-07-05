package de.wethinkco.database;

import com.fasterxml.jackson.databind.JsonNode;

public class DbData {
    protected final String reference;
    protected final JsonNode data;

    public DbData(String reference, JsonNode data) {
        this.reference = reference;
        this.data = data;
    }
}
