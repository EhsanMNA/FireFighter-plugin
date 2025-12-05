package com.github.tommyt0mmy.firefighter.model;

import com.github.tommyt0mmy.firefighter.FireFighter;
import com.github.tommyt0mmy.firefighter.utility.Permissions;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
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

        NamespacedKey key = getNpcKey();
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
        int maxY = Math.max(mission.getFirstY(), mission.getSecondY());
        int minY = Math.min(mission.getFirstY(), mission.getSecondY());
        int maxZ = Math.max(mission.getFirstZ(), mission.getSecondZ());

        Random random = new Random();
        int attempts = 0;
        int maxAttempts = totalNPCs * 10; // Try up to 10 times per NPC

        for (int i = 0; i < totalNPCs; i++) {
            Location loc = null;
            boolean locationFound = false;

            // Try to find a valid location
            for (int attempt = 0; attempt < 10 && !locationFound; attempt++) {
                int x = minX + random.nextInt(maxX - minX + 1);
                int z = minZ + random.nextInt(maxZ - minZ + 1);

                // Method 1: Find highest safe block
                loc = findSafeSpawnLocation(world, x, z, maxY, minY);

                if (loc != null && isValidSpawnLocation(loc)) {
                    locationFound = true;
                }

                attempts++;
                if (attempts >= maxAttempts) break;
            }

            if (loc == null || !locationFound) {
                plugin.getLogger().log(Level.WARNING, "Could not find safe location for NPC #{0}, skipping", i + 1);
                continue;
            }

            // Spawn the NPC
            EntityType npcType = EntityType.valueOf(plugin.getConfig().getString("rescue_system.npc_type", "VILLAGER"));
            LivingEntity npc = (LivingEntity) world.spawnEntity(loc, npcType);

            // Configure NPC
            configureNPC(npc, key);
            activeNPCs.add(npc.getUniqueId());

            // Add slight random offset to prevent stacking
            if (plugin.getConfig().getBoolean("rescue_system.prevent_stacking", true)) {
                npc.teleport(npc.getLocation().add(
                        (random.nextDouble() - 0.5) * 0.5, // ±0.25 blocks
                        0,
                        (random.nextDouble() - 0.5) * 0.5
                ));
            }
        }

        plugin.getLogger().log(Level.INFO, "Spawned {0} NPCs with {1} attempts",
                new Object[]{activeNPCs.size(), attempts});
    }

    private Location findSafeSpawnLocation(World world, int x, int z, int maxY, int minY) {
        // Start from world height and go down to find highest solid block
        int y = maxY - 1;

        while (y > minY) {
            Block block = world.getBlockAt(x, y, z);
            Block blockAbove = world.getBlockAt(x, y + 1, z);
            Block block2Above = world.getBlockAt(x, y + 2, z);

            // Check if this is a valid spawn location
            boolean isSolidFloor = block.getType().isSolid();
            boolean isPassableAbove = blockAbove.getType().isAir() || blockAbove.isPassable();
            boolean isPassable2Above = block2Above.getType().isAir() || block2Above.isPassable();
            boolean isNotHazardous = !isHazardousMaterial(blockAbove.getType()) &&
                    !isHazardousMaterial(block2Above.getType());

            if (isSolidFloor && isPassableAbove && isPassable2Above && isNotHazardous) {
                // Add 0.5 to center in block and ensure not inside floor
                return new Location(world, x + 0.5, y + 1.0, z + 0.5);
            }
            y--;
        }

        return null;
    }

    private boolean isValidSpawnLocation(Location loc) {
        Block block = loc.getBlock();
        Block blockBelow = loc.clone().subtract(0, 1, 0).getBlock();

        // Check for hazardous materials
        if (isHazardousMaterial(block.getType()) || isHazardousMaterial(blockBelow.getType())) {
            return false;
        }

        // Check if enough space (at least 2 blocks tall)
        Block blockAbove = loc.clone().add(0, 1, 0).getBlock();
        if (!blockAbove.isPassable() && !blockAbove.getType().isAir()) {
            return false;
        }

        return true;
    }

    private boolean isHazardousMaterial(Material material) {
        return material == Material.FIRE ||
                material == Material.LAVA ||
                material == Material.CACTUS ||
                material == Material.SWEET_BERRY_BUSH ||
                material == Material.MAGMA_BLOCK ||
                material == Material.CAMPFIRE ||
                material == Material.SOUL_CAMPFIRE;
    }

    private void configureNPC(LivingEntity npc, NamespacedKey key) {
        npc.setCustomName(FireFighter.colorize(plugin.getConfig().getString("rescue_system.npc_name", "&cVictim")));
        npc.setCustomNameVisible(plugin.getConfig().getBoolean("rescue_system.show_name", false));
        npc.setInvulnerable(plugin.getConfig().getBoolean("rescue_system.fire_resistant", true));
        npc.setAI(false);
        npc.setSilent(true);
        npc.setGlowing(plugin.getConfig().getBoolean("rescue_system.glowing", true));

        // Prevent despawning
        npc.setRemoveWhenFarAway(false);

        // Set persistence
        npc.setPersistent(true);

        // 30% chance to make it a baby
        if (new Random().nextDouble() < 0.3) {
            if (npc instanceof Villager villager) {
                villager.setBaby();
                villager.setAgeLock(true); // Prevent growing up
            }
        }

        // Add NBT tag
        PersistentDataContainer data = npc.getPersistentDataContainer();
        data.set(key, PersistentDataType.BYTE, (byte) 1);
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

        addRescueContribution(player, plugin.getConfig().getInt("rescue_system.rescue_points",30));

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
        for (Entity entity : getAllEntitiesByType(Villager.class)) if (isNPC(entity)) entity.remove();
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

    private static <T extends Entity> List<T> getAllEntitiesByType(Class<T> entityType) {
        List<T> filteredEntities = new ArrayList<>();

        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (entityType.isInstance(entity)) {
                    filteredEntities.add(entityType.cast(entity));
                }
            }
        }

        return filteredEntities;
    }
}