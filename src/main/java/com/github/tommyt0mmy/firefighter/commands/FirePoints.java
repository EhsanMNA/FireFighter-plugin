package com.github.tommyt0mmy.firefighter.commands;

import com.github.tommyt0mmy.firefighter.FireFighter;
import com.github.tommyt0mmy.firefighter.utility.Permissions;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import static com.github.tommyt0mmy.firefighter.FireFighter.colorize;

public class FirePoints implements CommandExecutor {

    private final FireFighter plugin = FireFighter.getInstance();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.messages.formattedMessage("&c", "only_players_command"));
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            sendUsage(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "toppoints":
                handleTopPoints(player, args);
                break;
            case "points":
                handlePoints(player, args);
                break;
            case "resetpoints":
                handleResetPoints(player);
                break;
            case "addpoints":
                handleAddPoints(player, args);
                break;
            default:
                sendUsage(player);
                break;
        }

        return true;
    }

    private void sendUsage(Player player) {
        player.sendMessage(colorize(plugin.messages.formattedMessage("&c", "usage_firepoints")));
        // Assume added to messages: "usage_firepoints" = "/firepoints <toppoints|points|resetpoints|addpoints>"
    }

    private void handleTopPoints(Player player, String[] args) {
        if (!player.hasPermission(Permissions.POINTS_TOP.getNode())) {
            player.sendMessage(colorize(plugin.messages.getMessage("points_no_permission")));
            return;
        }

        int page = 1;
        if (args.length > 1) {
            try {
                page = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                player.sendMessage(colorize(plugin.messages.getMessage("page_not_found")));
                return;
            }
        }

        int pageSize = plugin.getConfig().getInt("points.page_size", 10);
        List<Entry<UUID, Integer>> top = plugin.pointsManager.getTopPoints(page, pageSize);
        int totalPages = plugin.pointsManager.getTotalPages(pageSize);

        if (page < 1 || page > totalPages) {
            player.sendMessage(colorize(plugin.messages.getMessage("page_not_found")));
            return;
        }

        player.sendMessage(colorize(plugin.messages.getMessage("points_top_header")
                .replace("<page>", String.valueOf(page))
                .replace("<total_pages>", String.valueOf(totalPages))));

        if (top.isEmpty()) {
            player.sendMessage(colorize(plugin.messages.getMessage("points_top_no_data")));
            return;
        }

        int rank = (page - 1) * pageSize + 1;
        for (Entry<UUID, Integer> entry : top) {
            String name = plugin.pointsManager.getPlayerName(entry.getKey());
            player.sendMessage(colorize(plugin.messages.getMessage("points_top_entry")
                    .replace("<rank>", String.valueOf(rank++))
                    .replace("<name>", name)
                    .replace("<points>", String.valueOf(entry.getValue()))));
        }
    }

    private void handlePoints(Player player, String[] args) {
        if (!player.hasPermission(Permissions.POINTS_VIEW.getNode())) {
            player.sendMessage(colorize(plugin.messages.getMessage("points_no_permission")));
            return;
        }

        UUID targetUUID;
        String targetName;
        if (args.length > 1) {
            OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
            if (!target.hasPlayedBefore()) {
                player.sendMessage(colorize(plugin.messages.getMessage("points_player_not_found")));
                return;
            }
            targetUUID = target.getUniqueId();
            targetName = target.getName();
            player.sendMessage(colorize(plugin.messages.getMessage("points_other_points")
                    .replace("<player>", targetName)
                    .replace("<points>", String.valueOf(plugin.pointsManager.getPoints(targetUUID)))));
        } else {
            targetUUID = player.getUniqueId();
            player.sendMessage(colorize(plugin.messages.getMessage("points_your_points")
                    .replace("<points>", String.valueOf(plugin.pointsManager.getPoints(targetUUID)))));
        }
    }

    private void handleResetPoints(Player player) {
        if (!player.hasPermission(Permissions.POINTS_RESET.getNode())) {
            player.sendMessage(colorize(plugin.messages.getMessage("points_no_permission")));
            return;
        }

        plugin.pointsManager.resetAllPoints();
        player.sendMessage(colorize(plugin.messages.getMessage("points_reset_success")));
    }

    private void handleAddPoints(Player player, String[] args) {
        if (!player.hasPermission(Permissions.POINTS_ADD.getNode())) {
            player.sendMessage(colorize(plugin.messages.getMessage("points_no_permission")));
            return;
        }

        if (args.length != 3) {
            player.sendMessage(colorize(plugin.messages.formattedMessage("&c", "usage_addpoints"))); // Assume added: "usage_addpoints" = "/firepoints addpoints <player> <amount>"
            return;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
        if (!target.hasPlayedBefore()) {
            player.sendMessage(colorize(plugin.messages.getMessage("points_player_not_found")));
            return;
        }

        int amount;
        try {
            amount = Integer.parseInt(args[2]);
            if (amount <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            player.sendMessage(colorize(plugin.messages.getMessage("points_invalid_amount")));
            return;
        }

        plugin.pointsManager.addPoints(target.getUniqueId(), amount);
        player.sendMessage(colorize(plugin.messages.getMessage("points_added_success")
                .replace("<amount>", String.valueOf(amount))
                .replace("<player>", target.getName())));
    }
}