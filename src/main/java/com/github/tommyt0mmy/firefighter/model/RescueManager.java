package com.github.tommyt0mmy.firefighter.model;

import com.github.tommyt0mmy.firefighter.FireFighter;
import com.github.tommyt0mmy.firefighter.utility.Permissions;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Level;

public class RescueManager {
    private FireFighter plugin = FireFighter.getInstance();
    private static final List<UUID> activeNPCs = new ArrayList<>();  // Track NPC UUIDs for the current mission
    private static int rescuedCount = 0;
    private static int totalNPCs = 0;
    private static NamespacedKey npcKey;


    private NamespacedKey getNpcKey() {
        if (npcKey == null) {
            npcKey = new NamespacedKey(plugin, "rescue_system");
        }
        return npcKey;
    }

    public void startRescue(Mission mission) {
        if (plugin == null) plugin = FireFighter.getInstance();
        if (!plugin.getConfig().getBoolean("rescue_system.enabled")) return;

        NamespacedKey key = getNpcKey(); // Use the getter to ensure consistency
        plugin.getLogger().log(Level.INFO, "RescueManager initialized with NBT key: {0}", key.toString());

        World world = Bukkit.getWorld(mission.getWorldName());
        if (world == null) return;

        totalNPCs = plugin.getConfig().getInt("rescue_system.npcs_per_mission", 3);
        rescuedCount = 0;
        activeNPCs.clear();
        plugin.getLogger().log(Level.INFO, "Starting rescue for mission {0}, spawning {1} NPCs",
                new Object[]{mission.getId(), totalNPCs});

        int minX = Math.min(mission.getFirstX(), mission.getSecondX());
        int maxX = Math.max(mission.getFirstX(), mission.getSecondX());
        int minZ = Math.min(mission.getFirstZ(), mission.getSecondZ());
        int maxZ = Math.max(mission.getFirstZ(), mission.getSecondZ());
        int y = mission.getAltitude();

        Random random = new Random();
        for (int i = 0; i < totalNPCs; i++) {
            int x = minX + random.nextInt(maxX - minX + 1);
            int z = minZ + random.nextInt(maxZ - minZ + 1);
            Location loc = new Location(world, x, y, z);

            // Find safe spawn spot (above ground, not in fire)
            while (!loc.getBlock().isPassable() && loc.getY() < world.getMaxHeight()) {
                loc.add(0, 1, 0);
            }
            if (loc.getBlock().getType() == Material.FIRE) {
                loc.add(0, 1, 0);  // Avoid spawning in fire
            }

            EntityType npcType = EntityType.valueOf(plugin.getConfig().getString("rescue_system.npc_type", "VILLAGER"));
            LivingEntity npc = (LivingEntity) world.spawnEntity(loc, npcType);
            npc.setCustomName(FireFighter.colorize(plugin.getConfig().getString("rescue_system.npc_name", "&cVictim")));
            npc.setCustomNameVisible(false);
            npc.setInvulnerable(plugin.getConfig().getBoolean("rescue_system.fire_resistant", true));
            npc.setAI(false);  // Static, no movement
            npc.setSilent(true);  // No sounds
            npc.setGlowing(true);

            PersistentDataContainer data = npc.getPersistentDataContainer();
            data.set(key, PersistentDataType.BYTE, (byte) 1);
            activeNPCs.add(npc.getUniqueId());

            // Enhanced logging to check all NBT data
//            plugin.getLogger().log(Level.INFO, "Spawned NPC #{0} at {1}, UUID: {2}",
//                    new Object[]{i + 1, loc.toString(), npc.getUniqueId()});

            // Log all NBT keys for debugging
            logAllNBTData(npc, "After spawning NPC #" + (i + 1));

            // Verify tag was set
//            if (data.has(key, PersistentDataType.BYTE)) {
//                plugin.getLogger().log(Level.INFO, "Confirmed NPC #{0} has NBT tag {1}",
//                        new Object[]{i + 1, key.toString()});
//            } else {
//                plugin.getLogger().log(Level.WARNING, "Failed to set NBT tag {0} on NPC #{1}",
//                        new Object[]{key.toString(), i + 1});
//            }
        }
    }

    public boolean isNPC(Entity entity) {
        if (entity == null) {
            plugin.getLogger().warning("isNPC: Entity is null");
            return false;
        }

        NamespacedKey key = getNpcKey(); // Use the same getter for consistency

        PersistentDataContainer data = entity.getPersistentDataContainer();

        // Log all NBT data for debugging
        logAllNBTData(entity, "Checking entity in isNPC");

        boolean hasTag = data.has(key, PersistentDataType.BYTE);
//        plugin.getLogger().log(Level.INFO, "Checking entity {0} (Type: {1}, UUID: {2}) for NPC tag {3}: {4}",
//                new Object[]{entity.getName(), entity.getType(), entity.getUniqueId(), key.toString(), hasTag});
        return hasTag;
    }

    /**
     * Utility method to log all NBT data from an entity for debugging
     */
    private void logAllNBTData(Entity entity, String context) {
        return;
//        try {
//            PersistentDataContainer data = entity.getPersistentDataContainer();
//            plugin.getLogger().log(Level.INFO, "=== NBT Data Debug - {0} ===", context);
//            plugin.getLogger().log(Level.INFO, "Entity: {0} (Type: {1}, UUID: {2})",
//                    new Object[]{entity.getName(), entity.getType(), entity.getUniqueId()});
//
//            // Get all persistent data keys (this requires reflection in some versions)
//            // For now, we'll log the specific key we're looking for and do a general check
//            NamespacedKey key = getNpcKey();
//            boolean hasOurTag = data.has(key, PersistentDataType.BYTE);
//            plugin.getLogger().log(Level.INFO, "Has our tag ({0}): {1}",
//                    new Object[]{key.toString(), hasOurTag});
//
//            // Try to get the value if it exists
//            if (hasOurTag) {
//                Byte value = data.get(key, PersistentDataType.BYTE);
//                plugin.getLogger().log(Level.INFO, "Our tag value: {0}", value);
//            }
//
//            plugin.getLogger().info("=== End NBT Data Debug ===");
//        } catch (Exception e) {
//            plugin.getLogger().log(Level.WARNING, "Error while logging NBT data: {0}", e.getMessage());
//        }
    }

    public void pickUpNPC(Player player, Entity npc) {
        if (player.getPassengers().size() > 0) return;  // Can't carry multiple

        // Log NBT data before pickup for debugging
        logAllNBTData(npc, "Before picking up NPC");

        player.addPassenger(npc);
        player.sendMessage(FireFighter.colorize(plugin.messages.getMessage("picked_up")));
    }

    public void rescueNPC(Player player) {
        if (player.getPassengers().isEmpty()) return;
        Entity passenger = player.getPassengers().get(0);

        // Log NBT data before rescue check
        logAllNBTData(passenger, "Before rescuing NPC");

        if (!isNPC(passenger)) return;

        // Check if player is outside the mission area
        String missionName = plugin.missionName;
        if (missionName.isEmpty()) return;  // No active mission

        String missionPath = "missions." + missionName;
        World missionWorld = Bukkit.getWorld(plugin.getConfig().getString(missionPath + ".world"));
        if (missionWorld == null) return;

        int minX = Math.min(plugin.getConfig().getInt(missionPath + ".first_position.x"),
                plugin.getConfig().getInt(missionPath + ".second_position.x"));
        int maxX = Math.max(plugin.getConfig().getInt(missionPath + ".first_position.x"),
                plugin.getConfig().getInt(missionPath + ".second_position.x"));
        int minZ = Math.min(plugin.getConfig().getInt(missionPath + ".first_position.z"),
                plugin.getConfig().getInt(missionPath + ".second_position.z"));
        int maxZ = Math.max(plugin.getConfig().getInt(missionPath + ".first_position.z"),
                plugin.getConfig().getInt(missionPath + ".second_position.z"));

        Location playerLoc = player.getLocation();
        if (playerLoc.getWorld().equals(missionWorld) &&
                playerLoc.getBlockX() >= minX && playerLoc.getBlockX() <= maxX &&
                playerLoc.getBlockZ() >= minZ && playerLoc.getBlockZ() <= maxZ) {
            player.sendMessage(FireFighter.colorize(plugin.messages.getMessage("inside_area")));
            return;
        }

        // Proceed with rescue
        passenger.remove();
        activeNPCs.remove(passenger.getUniqueId());
        rescuedCount++;

        addRescueContribution(player, 30);

        String msg = plugin.messages.getMessage("rescued")
                .replace("<rescued>", String.valueOf(rescuedCount))
                .replace("<total>", String.valueOf(totalNPCs));
        player.sendMessage(FireFighter.colorize(msg));

        if (isAllRescued()) {
            for (Player p : Bukkit.getOnlinePlayers()){
                if (!p.hasPermission(Permissions.CHAT.getNode())) continue;
                p.sendMessage(FireFighter.colorize(plugin.messages.getMessage("all_rescued")));
            }
        }
    }

    public boolean isAllRescued() {
        return rescuedCount >= totalNPCs;
    }

    public void cleanup() {
        for (UUID uuid : activeNPCs) {
            Entity npc = Bukkit.getEntity(uuid);
            if (npc != null) npc.remove();
        }
        activeNPCs.clear();
        rescuedCount = 0;
        totalNPCs = 0;
    }

    private void addRescueContribution(Player player, double points) {
        if (!plugin.startedMission || plugin.missionName.isEmpty()) {
            return;
        }

        // Check if player is in the mission world
        String missionPath = "missions." + plugin.missionName;
        World missionWorld = Bukkit.getWorld(plugin.getConfig().getString(missionPath + ".world"));
        if (missionWorld == null || !player.getWorld().equals(missionWorld)) return;

        // Add contribution points
        if (plugin.PlayerContribution.containsKey(player.getUniqueId())) {
            double currentPoints = plugin.PlayerContribution.get(player.getUniqueId());
            plugin.PlayerContribution.put(player.getUniqueId(), currentPoints + points);
        } else {
            plugin.PlayerContribution.put(player.getUniqueId(), points);
        }

//        plugin.getLogger().log(Level.INFO, "Added {0} rescue points to player {1}, total: {2}",
//                new Object[]{points, player.getName(), plugin.PlayerContribution.get(player.getUniqueId())});
    }

}