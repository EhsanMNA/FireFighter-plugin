package com.github.tommyt0mmy.firefighter.events;

import com.github.tommyt0mmy.firefighter.FireFighter;
import com.github.tommyt0mmy.firefighter.model.Mission;
import com.github.tommyt0mmy.firefighter.model.MissionManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class FireAreaEffects extends BukkitRunnable {
    private final FireFighter plugin = FireFighter.getInstance();
    private final Set<UUID> affectedPlayers = new HashSet<>();  // Tracks players who have received effects

    public FireAreaEffects() {
        // Start the runnable (called in FireFighter.onEnable)
        this.runTaskTimer(plugin, 0, 20); // Run every second (20 ticks)
    }

    @Override
    public void run() {
        if (!plugin.startedMission) {
            affectedPlayers.clear(); // Reset when no mission is active
            return;
        }

        Mission mission = MissionManager.getMission(plugin.missionName);
        if (mission == null) return;

        int minX = mission.getRegion().getMinX();
        int maxX = mission.getRegion().getMaxX();
        int minZ = mission.getRegion().getMinZ();
        int maxZ = mission.getRegion().getMaxZ();
        int y = mission.getAltitude();

        for (Player player : Bukkit.getOnlinePlayers()) {
            Location loc = player.getLocation();
            boolean inArea = loc.getWorld().getName().equals(mission.getWorldName()) &&
                    loc.getBlockX() >= minX && loc.getBlockX() <= maxX &&
                    loc.getBlockZ() >= minZ && loc.getBlockZ() <= maxZ &&
                    Math.abs(loc.getBlockY() - y) <= 10; // Vertical range

            ItemStack item = player.getInventory().getHelmet();
            if (item != null && item.getType() != Material.AIR){
                if (FireFighter.helmet.isFireHelmet(item)) continue;
            }

//            if (inArea && !affectedPlayers.contains(player.getUniqueId())) {
//                applyEffects(player);
//                affectedPlayers.add(player.getUniqueId());
//            } else if (!inArea && affectedPlayers.contains(player.getUniqueId())) {
//                affectedPlayers.remove(player.getUniqueId()); // Allow reapplication if they re-enter
//            }

            if (inArea) applyEffects(player);

        }
    }

    private void applyEffects(Player player) {
        for (String effectStr : plugin.getConfig().getStringList("fire_area_effects")) {
            String[] parts = effectStr.split(":");
            if (parts.length == 3) {
                PotionEffectType type = PotionEffectType.getByName(parts[0].toUpperCase());
                if (type != null) {
                    int duration = Integer.parseInt(parts[1]) * 20; // seconds to ticks
                    int amplifier = Integer.parseInt(parts[2]);
                    player.addPotionEffect(new PotionEffect(type, duration, amplifier));
                }
            }
        }
    }
}