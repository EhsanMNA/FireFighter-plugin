package com.github.tommyt0mmy.firefighter.model;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Objects;

public class Region {
    private final String name;
    private Location point1;
    private Location point2;
    private final World world;

    private int minX;
    private int minY;
    private int minZ;
    private int maxX;
    private int maxY;
    private int maxZ;

    public Region(String name, Location point1, Location point2) {
        this.name = name;

        // Ensure both locations are in the same world
        if (!Objects.equals(point1.getWorld(), point2.getWorld())) {
            throw new IllegalArgumentException("Both locations must be in the same world!");
        }

        this.world = point1.getWorld();

        // Calculate min and max points
        minX = Math.min(point1.getBlockX(), point2.getBlockX());
        minY = Math.min(point1.getBlockY(), point2.getBlockY());
        minZ = Math.min(point1.getBlockZ(), point2.getBlockZ());

        maxX = Math.max(point1.getBlockX(), point2.getBlockX());
        maxY = Math.max(point1.getBlockY(), point2.getBlockY());
        maxZ = Math.max(point1.getBlockZ(), point2.getBlockZ());
//
//        this.minPoint = new Location(world, minX, minY, minZ);
//        this.maxPoint = new Location(world, maxX, maxY, maxZ);
        this.point1 = point1;
        this.point2 = point2;
    }

    /**
     * Check if a location is inside this region
     */
    public boolean contains(Location location) {
        if (!location.getWorld().equals(world)) {
            return false;
        }

        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();

        return x >= minX && x <= maxX && y >= minY && y <= maxY && z >= minZ && z <= maxZ;
    }

    /**
     * Check if a player is inside this region
     */
    public boolean contains(Player player) {
        return contains(player.getLocation());
    }

    /**
     * Get the center point of the region
     */
    public Location getCenter() {
        double centerX = (minX + maxX) / 2;
        double centerY = (minY + maxY) / 2;
        double centerZ = (minZ + maxZ) / 2;

        return new Location(world, centerX, centerY, centerZ);
    }

    /**
     * Get the volume of the region (in blocks)
     */
    public long getVolume() {
        int width = getWidth();
        int height = getHeight();
        int length = getLength();

        return (long) width * height * length;
    }

    /**
     * Get region width (X-axis size)
     */
    public int getWidth() {
        return maxX - minX + 1;
    }

    /**
     * Get region height (Y-axis size)
     */
    public int getHeight() {
        return maxY - minY + 1;
    }

    /**
     * Get region length (Z-axis size)
     */
    public int getLength() {
        return maxZ - minZ + 1;
    }

    // Getters
    public String getName() {
        return name;
    }

    public Location getPoint1() {
        return point1;
    }

    public Location getPoint2() {
        return point2;
    }

    public World getWorld() {
        return world;
    }

    public int getMinX() {
        return minX;
    }

    public int getMinY() {
        return minY;
    }

    public int getMinZ() {
        return minZ;
    }

    public int getMaxX() {
        return maxX;
    }

    public int getMaxY() {
        return maxY;
    }

    public int getMaxZ() {
        return maxZ;
    }

    public void setPoint1(Location point1) {
        this.point1 = point1;
    }

    public void setPoint2(Location point2) {
        this.point2 = point2;
    }

    @Override
    public String toString() {
        return String.format("Region{name='%s', world='%s', min=[%d,%d,%d], max=[%d,%d,%d]}",
                name, world.getName(),
                point1.getBlockX(), point1.getBlockY(), point1.getBlockZ(),
                point2.getBlockX(), point2.getBlockY(), point2.getBlockZ());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Region region = (Region) obj;
        return name.equals(region.name) &&
                world.equals(region.world) &&
                point1.equals(region.point1) &&
                point2.equals(region.point2);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + world.hashCode();
        result = 31 * result + point1.hashCode();
        result = 31 * result + point2.hashCode();
        return result;
    }
}
