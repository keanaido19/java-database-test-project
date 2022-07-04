package de.wethinkco.database.world;

import java.util.List;

public class World {
    private WorldData worldData;
    private List<WorldObject> obstacles;

    public World() {
    }

    public World(WorldData worldData, List<WorldObject> obstacles) {
        this.worldData = worldData;
        this.obstacles = obstacles;
    }

    public WorldData getWorldData() {
        return worldData;
    }

    public void setWorldData(WorldData worldData) {
        this.worldData = worldData;
    }

    public List<WorldObject> getObstacles() {
        return obstacles;
    }

    public void setObstacles(List<WorldObject> obstacles) {
        this.obstacles = obstacles;
    }
}
