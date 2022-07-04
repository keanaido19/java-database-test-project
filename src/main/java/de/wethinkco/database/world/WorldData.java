package de.wethinkco.database.world;

public class WorldData {
    private int width;
    private int height;
    private int repair_time;
    private int reload_time;
    private int mine_time;
    private int max_shield;

    public WorldData() {}

    public WorldData(
            int width,
            int height,
            int repair_time,
            int reload_time,
            int mine_time,
            int max_shield
    ) {
        this.width = width;
        this.height = height;
        this.repair_time = repair_time;
        this.reload_time = reload_time;
        this.mine_time = mine_time;
        this.max_shield = max_shield;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getRepair_time() {
        return repair_time;
    }

    public void setRepair_time(int repair_time) {
        this.repair_time = repair_time;
    }

    public int getReload_time() {
        return reload_time;
    }

    public void setReload_time(int reload_time) {
        this.reload_time = reload_time;
    }

    public int getMine_time() {
        return mine_time;
    }

    public void setMine_time(int mine_time) {
        this.mine_time = mine_time;
    }

    public int getMax_shield() {
        return max_shield;
    }

    public void setMax_shield(int max_shield) {
        this.max_shield = max_shield;
    }
}
