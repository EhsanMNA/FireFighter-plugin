package com.github.tommyt0mmy.firefighter.model;

import com.github.tommyt0mmy.firefighter.FireFighter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

public class PointsManager {

    private final FireFighter plugin;
    private final Map<UUID, Integer> points = new HashMap<>();
    private final File pointsFile;

    public PointsManager(FireFighter plugin) {
        this.plugin = plugin;
        this.pointsFile = new File(plugin.getDataFolder(), "points.txt");
        loadPoints();
    }

    private void loadPoints() {
        if (!pointsFile.exists()) {
            try {
                pointsFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create points.txt!");
                return;
            }
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(pointsFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    try {
                        UUID uuid = UUID.fromString(parts[0].trim());
                        int pts = Integer.parseInt(parts[1].trim());
                        points.put(uuid, pts);
                    } catch (IllegalArgumentException ignored) {
                        // Skip invalid lines
                    }
                }
            }
        } catch (IOException e) {
            plugin.getLogger().severe("Could not load points.txt!");
        }
    }

    public void savePoints() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(pointsFile))) {
            for (Entry<UUID, Integer> entry : points.entrySet()) {
                writer.write(entry.getKey().toString() + ":" + entry.getValue());
                writer.newLine();
            }
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save points.txt!");
        }
    }

    public int getPoints(UUID uuid) {
        return points.getOrDefault(uuid, 0);
    }

    public void addPoints(UUID uuid, int amount) {
        int current = getPoints(uuid);
        points.put(uuid, current + amount);
        OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
        if (player.isOnline()) {
            ((Player) player).sendMessage(plugin.messages.formattedMessage("&a", "points_added_notification").replace("<amount>", String.valueOf(amount)));
        }
    }

    public void resetAllPoints() {
        points.clear();
        savePoints();
    }

    public List<Entry<UUID, Integer>> getTopPoints(int page, int pageSize) {
        List<Entry<UUID, Integer>> sorted = new ArrayList<>(points.entrySet());
        sorted.sort((a, b) -> b.getValue().compareTo(a.getValue()));
        int start = (page - 1) * pageSize;
        int end = Math.min(start + pageSize, sorted.size());
        return sorted.subList(start, end);
    }

    public int getTotalPages(int pageSize) {
        return (int) Math.ceil((double) points.size() / pageSize);
    }

    public String getPlayerName(UUID uuid) {
        OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);
        return op.getName() != null ? op.getName() : "Unknown";
    }
}