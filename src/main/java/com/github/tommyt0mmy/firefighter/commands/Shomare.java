package com.github.tommyt0mmy.firefighter.commands;

import com.github.tommyt0mmy.firefighter.FireFighter;
import com.github.tommyt0mmy.firefighter.MissionsHandler;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class Shomare implements CommandExecutor {

    public static Sound notifSound = Sound.BLOCK_ANVIL_BREAK;
    public static float notifSoundV = 10;
    public static float notifSoundF = 1.0f;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (sender instanceof Player player){
            if (player.hasPermission("firefighter.125.send")){
                StringBuilder message = new StringBuilder();
                if(args.length == 0) {
                    player.sendMessage(FireFighter.colorize(FireFighter.getInstance().messages.getMessage("firechat_125_empty")));
                    return false;
                }
                if (MissionsHandler.delays.containsKey(player.getUniqueId())){
                    player.sendMessage(FireFighter.colorize(FireFighter.getInstance().messages.getMessage("firechat_125_delay")
                            .replace("<time>",MissionsHandler.delays.get(player.getUniqueId())+"")));
                    return false;
                }
                if (message.length() > 1024) return false;
                for (String ss : args) message.append(ss).append(" ");
                for (Player p : Bukkit.getOnlinePlayers()){
                    if (p.hasPermission("firefighter.125.receive")){
                        p.sendMessage(FireFighter.colorize(FireFighter.getInstance().messages.getMessage("firechat_125").replace("<player>",player.getDisplayName())+ message));
                        p.playSound(p.getLocation(), notifSound,notifSoundV,notifSoundF);
                    }
                }
                MissionsHandler.delays.put(player.getUniqueId(),360);
            }
        }
        return false;
    }
}
