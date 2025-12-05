package com.github.tommyt0mmy.firefighter;

import org.bukkit.scheduler.BukkitRunnable;

public class AnimalMissionsHandler extends BukkitRunnable {

    private FireFighter plugin = FireFighter.getInstance();
    private long nextStart = System.currentTimeMillis() + (plugin.getConfig().getInt("animal_rescue.interval_seconds", 1800) * 1000L);

    @Override
    public void run() {
        if (!plugin.getConfig().getBoolean("animal_rescue.enabled", true)) return;
        if (System.currentTimeMillis() >= nextStart) {
            plugin.animalRescueManager.startMission();
            nextStart = System.currentTimeMillis() + (plugin.getConfig().getInt("animal_rescue.interval_seconds", 1800) * 1000L);
        }
    }

}