package com.github.tommyt0mmy.firefighter.model;

import com.github.tommyt0mmy.firefighter.FireFighter;
import com.github.tommyt0mmy.firefighter.utility.Permissions;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class AnimalRescueManager {
    private FireFighter plugin = FireFighter.getInstance();
    private NamespacedKey animalKey;
    private UUID activeAnimalUUID;
    private boolean missionActive = false;
    private int reward;
    private String placeName;
    private long endTime;
    private BukkitTask missionTimer;
    private List<AnimalLocation> locations = new ArrayList<>();
    public static class AnimalLocation {
        public World world;
        public double x, y, z;
        public String placeName;
        public AnimalLocation(World world, double x, double y, double z, String placeName) {
            this.world = world;
            this.x = x;
            this.y = y;
            this.z = z;
            this.placeName = placeName;
        }
        public Location toLocation() {
            return new Location(world, x, y, z);
        }
    }
    public AnimalRescueManager() {
        loadLocations();
        animalKey = new NamespacedKey(plugin, "animal_rescue");
    }
    private void loadLocations() {
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("animal_rescue.locations");
        if (section == null) return;
        for (String key : section.getKeys(false)) {
            String worldName = section.getString(key + ".world");
            World world = Bukkit.getWorld(worldName);
            if (world == null) continue;
            double x = section.getDouble(key + ".x");
            double y = section.getDouble(key + ".y");
            double z = section.getDouble(key + ".z");
            String place = section.getString(key + ".place_name", key);
            locations.add(new AnimalLocation(world, x, y, z, place));
        }
    }
    public void startMission() {
        if (locations.isEmpty() || missionActive) return;
        Random random = new Random();
        AnimalLocation loc = locations.get(random.nextInt(locations.size()));
        EntityType animalType = EntityType.valueOf(plugin.getConfig().getString("animal_rescue.animal_type", "CAT").toUpperCase());
        LivingEntity animal = (LivingEntity) loc.world.spawnEntity(loc.toLocation(), animalType);
        animal.setCustomName(ChatColor.RED + "Stuck Animal");
        animal.setCustomNameVisible(true);
        animal.setInvulnerable(true);
        animal.setAI(false);
        animal.setGlowing(true);
        animal.setSilent(true);
        PersistentDataContainer data = animal.getPersistentDataContainer();
        data.set(animalKey, PersistentDataType.BYTE, (byte) 1);
        activeAnimalUUID = animal.getUniqueId();
        missionActive = true;
        placeName = loc.placeName;
        reward = random.nextInt(plugin.getConfig().getInt("animal_rescue.reward_max", 500) - plugin.getConfig().getInt("animal_rescue.reward_min", 100) + 1) + plugin.getConfig().getInt("animal_rescue.reward_min", 100);
        endTime = System.currentTimeMillis() + (plugin.getConfig().getInt("animal_rescue.duration_seconds", 300) * 1000L);
        broadcastStartMessage();
// Start timer
        missionTimer = new BukkitRunnable() {
            @Override
            public void run() {
                if (System.currentTimeMillis() >= endTime) {
                    endMission(false);
                }
            }
        }.runTaskTimer(plugin, 20L, 20L); // Check every second
    }

    private void broadcastStartMessage() {
        String message = plugin.messages.getMessage("animal_mission_start")
                .replace("<place>", placeName)
                .replace("", String.valueOf(plugin.getConfig().getInt("animal_rescue.duration_seconds", 300)))
                .replace("<reward>", String.valueOf(reward));
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission(Permissions.ON_DUTY.getNode())) {
                player.sendMessage(FireFighter.colorize(message));
            }
        }
    }

    public boolean isRescueAnimal(Entity entity) {
        if (entity == null || activeAnimalUUID == null || !entity.getUniqueId().equals(activeAnimalUUID)) return false;
        PersistentDataContainer data = entity.getPersistentDataContainer();
        return data.has(animalKey, PersistentDataType.BYTE);
    }
    public void rescueAnimal(Player player) {
        if (!missionActive) return;
        endMission(true, player);
    }
    public void endMission(boolean success) {
        endMission(success, null);
    }
    private void endMission(boolean success, Player savior) {
        if (!missionActive) return;
        Entity animal = Bukkit.getEntity(activeAnimalUUID);
        if (animal != null) animal.remove();
        missionActive = false;
        activeAnimalUUID = null;
        if (missionTimer != null) missionTimer.cancel();
        String messageKey = success ? "animal_mission_success" : "animal_mission_fail";
        String message = plugin.messages.getMessage(messageKey);
        if (success) {
            message = message.replace("<reward>", String.valueOf(reward));
            // Give reward
            String rewardCommand = plugin.getConfig().getString("animal_rescue.reward_command", "eco give <player> <reward>")
                    .replace("<player>", savior.getName())
                    .replace("<reward>", String.valueOf(reward));
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), rewardCommand);
            int animalPoints = plugin.getConfig().getInt("animal_rescue.points_reward", 50);
            plugin.pointsManager.addPoints(savior.getUniqueId(), animalPoints);
        }
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission(Permissions.ON_DUTY.getNode())) {
                player.sendMessage(FireFighter.colorize(message));
            }
        }
    }
    public boolean isMissionActive() {
        return missionActive;
    }
}