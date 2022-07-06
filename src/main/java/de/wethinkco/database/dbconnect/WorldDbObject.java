package de.wethinkco.database.dbconnect;

import java.util.List;

public class WorldDbObject {
    private final WorldDataDbObject worldData;
    private final List<WorldObjectDbData> obstacles, pits, mines;

    public WorldDbObject(
            WorldDataDbObject worldData,
            List<WorldObjectDbData> obstacles,
            List<WorldObjectDbData> pits,
            List<WorldObjectDbData> mines
    ) {
        this.worldData = worldData;
        this.obstacles = obstacles;
        this.pits = pits;
        this.mines = mines;
    }

    public WorldDataDbObject getWorldData() {
        return worldData;
    }

    public List<WorldObjectDbData> getObstacles() {
        return obstacles;
    }

    public List<WorldObjectDbData> getPits() {
        return pits;
    }

    public List<WorldObjectDbData> getMines() {
        return mines;
    }
}
