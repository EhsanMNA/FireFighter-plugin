package com.github.tommyt0mmy.firefighter.events;

import com.github.tommyt0mmy.firefighter.FireFighter;
import com.github.tommyt0mmy.firefighter.model.MissionManager;
import com.github.tommyt0mmy.firefighter.model.RescueManager;
import com.github.tommyt0mmy.firefighter.utility.Permissions;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

public class RescueEvents implements Listener {
    private final FireFighter plugin = FireFighter.getInstance();
    private final RescueManager rescueManager = new RescueManager();  // Instance or inject

    @EventHandler
    public void onInteractEntity(PlayerInteractEntityEvent e) {
        if (!plugin.startedMission) return;
        Player p = e.getPlayer();
        if (!p.hasPermission(Permissions.ON_DUTY.getNode())) return;  // Firefighters only
        if (rescueManager.isNPC(e.getRightClicked())) {
            e.setCancelled(true);
            rescueManager.pickUpNPC(p, e.getRightClicked());
        }
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent e) {
        if (!plugin.startedMission || !e.isSneaking()) return;
        Player p = e.getPlayer();
        if (!p.hasPermission(Permissions.ON_DUTY.getNode())) return;

        rescueManager.rescueNPC(p);
    }
}