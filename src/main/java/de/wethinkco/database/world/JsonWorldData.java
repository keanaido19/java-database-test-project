package de.wethinkco.database.world;

import java.util.List;

public class JsonWorldData {
    private World world;
    private List<WorldObject> obstacles;

    public JsonWorldData() {
    }

    public JsonWorldData(World world, List<WorldObject> obstacles) {
        this.world = world;
        this.obstacles = obstacles;
    }

    public World getWorld() {
        return world;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public List<WorldObject> getObstacles() {
        return obstacles;
    }

    public void setObstacles(List<WorldObject> obstacles) {
        this.obstacles = obstacles;
    }
}
