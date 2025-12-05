package com.github.tommyt0mmy.firefighter.commands;

import com.github.tommyt0mmy.firefighter.FireFighter;
import com.github.tommyt0mmy.firefighter.utility.Permissions;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ChatToggle implements CommandExecutor {

    public static Set<UUID> chats = new HashSet<>();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!(sender instanceof Player p)) {
            sender.sendMessage(FireFighter.getInstance().messages.formattedMessage("", "only_players_command"));
            return true;
        }

        if (!p.hasPermission(Permissions.CHAT_TOGGLE.getNode())) {
            p.sendMessage(FireFighter.getInstance().messages.formattedMessage("§c", "invalid_permissions"));
            return true;
        }

        if (args.length == 0){
            if (chats.contains(p.getUniqueId())){
                chats.remove(p.getUniqueId());
                p.sendMessage(FireFighter.colorize(FireFighter.getInstance().messages.formattedMessage("§c", "firechat_toggle_off")));
            }else {
                chats.add(p.getUniqueId());
                p.sendMessage(FireFighter.colorize(FireFighter.getInstance().messages.formattedMessage("§c", "firechat_toggle_on")));
            }
        }else {
            for (Player player : Bukkit.getOnlinePlayers()){
                if (!player.hasPermission(Permissions.CHAT.getNode())) continue;
//                player.sendMessage(FireFighter.colorize("&f[&cFireFighterChat&f] "+p.getName()+"&7:" + Arrays.toString(args)));
                player.sendMessage(FireFighter.colorize(FireFighter.getInstance().messages.getMessage("ingamechat_prefix")
                        .replace("<name>",p.getName()).replace("<text>",Arrays.toString(args))));
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP,10,2);
            }
        }

        return true;
    }
}
