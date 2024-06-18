package com.github.tommyt0mmy.firefighter.commands;

import com.github.tommyt0mmy.firefighter.FireFighter;
import com.github.tommyt0mmy.firefighter.utility.Permissions;
import com.github.tommyt0mmy.firefighter.utility.titles.ActionBar;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

public class FireTool implements CommandExecutor {

    private final FireFighter fireFighterClass = FireFighter.getInstance();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(fireFighterClass.messages.formattedMessage("", "only_players_command")); //only pl
            return true;
        }
        Player p = (Player) sender;
        if (!(p.hasPermission(Permissions.GET_EXTINGUISHER.getNode()) || p.isOp())) {
            p.sendMessage(fireFighterClass.messages.formattedMessage("§c", "invalid_permissions"));
            return true;
        }

        Inventory inventory = p.getInventory();
        inventory.addItem(fireFighterClass.getFireExtinguisher());
        ActionBar.sendActionBar(p,FireFighter.colorize("&e&i"+fireFighterClass.messages.formattedMessage("", "hold_right_click")));

        return true;
    }

}