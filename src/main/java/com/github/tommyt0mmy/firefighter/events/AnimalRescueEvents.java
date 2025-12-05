package com.github.tommyt0mmy.firefighter.events;

import com.github.tommyt0mmy.firefighter.FireFighter;
import com.github.tommyt0mmy.firefighter.model.AnimalRescueManager;
import com.github.tommyt0mmy.firefighter.utility.Permissions;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
public class AnimalRescueEvents implements Listener {
    private final FireFighter plugin = FireFighter.getInstance();
    private final AnimalRescueManager manager = plugin.animalRescueManager;



    @EventHandler
    public void onInteract(PlayerInteractEntityEvent e) {
        if (!manager.isMissionActive()) return;
        Player p = e.getPlayer();
        if (!p.hasPermission(Permissions.ON_DUTY.getNode())) return;
        if (manager.isRescueAnimal(e.getRightClicked())) {
            e.setCancelled(true);
            manager.rescueAnimal(p);
        }
    }
}