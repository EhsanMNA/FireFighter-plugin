package com.github.tommyt0mmy.firefighter.model;

public class Mission {

    String id;
    String description;
    String worldName;
    int altitude;

    int firstX;
    int secondX;
    int firstZ;
    int secondZ;
    int firstY;
    int secondY;

    public Mission(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getWorldName() {
        return worldName;
    }

    public void setWorldName(String worldName) {
        this.worldName = worldName;
    }

    public int getAltitude() {
        return altitude;
    }

    public void setAltitude(int altitude) {
        this.altitude = altitude;
    }

    public int getFirstX() {
        return firstX;
    }

    public void setFirstX(int firstX) {
        this.firstX = firstX;
    }

    public int getSecondX() {
        return secondX;
    }

    public void setSecondX(int secondX) {
        this.secondX = secondX;
    }

    public int getFirstZ() {
        return firstZ;
    }

    public void setFirstZ(int firstZ) {
        this.firstZ = firstZ;
    }

    public int getSecondZ() {
        return secondZ;
    }

    public void setSecondZ(int secondZ) {
        this.secondZ = secondZ;
    }

    public int getFirstY() {
        return firstY;
    }

    public void setFirstY(int firstY) {
        this.firstY = firstY;
    }

    public int getSecondY() {
        return secondY;
    }

    public void setSecondY(int secondY) {
        this.secondY = secondY;
    }
}
