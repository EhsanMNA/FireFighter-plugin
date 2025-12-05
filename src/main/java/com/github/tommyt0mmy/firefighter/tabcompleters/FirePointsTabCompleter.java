package com.github.tommyt0mmy.firefighter.tabcompleters;


import com.github.tommyt0mmy.firefighter.FireFighter;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class FirePointsTabCompleter implements TabCompleter {

    private final FireFighter plugin = FireFighter.getInstance();

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        // If no arguments, show main subcommands
        if (args.length == 1) {
            List<String> subCommands = new ArrayList<>();

            // Add all subcommands
            subCommands.add("toppoints");
            subCommands.add("points");

            // Check permissions for admin commands
            if (sender.hasPermission("firefighter.points.reset")) {
                subCommands.add("resetpoints");
            }
            if (sender.hasPermission("firefighter.points.add")) {
                subCommands.add("addpoints");
            }

            // Filter based on what the user has typed
            StringUtil.copyPartialMatches(args[0], subCommands, completions);
            Collections.sort(completions);
            return completions;
        }

        // Handle tab completion for each subcommand
        switch (args[0].toLowerCase()) {
            case "toppoints":
                return completeToppoints(sender, args);
            case "points":
                return completePoints(sender, args);
            case "addpoints":
                return completeAddpoints(sender, args);
            case "resetpoints":
                return completeResetpoints(sender, args);
        }

        return completions;
    }

    private List<String> completeToppoints(CommandSender sender, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 2) {
            // Suggest page numbers for toppoints
            int totalPages = plugin.pointsManager.getTotalPages(plugin.getConfig().getInt("points.page_size", 10));

            List<String> pages = new ArrayList<>();
            for (int i = 1; i <= Math.min(totalPages, 10); i++) { // Limit to first 10 pages
                pages.add(String.valueOf(i));
            }

            StringUtil.copyPartialMatches(args[1], pages, completions);
        }

        return completions;
    }

    private List<String> completePoints(CommandSender sender, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 2) {
            // Suggest player names (including offline players with points)
            List<String> playerNames = getPlayerSuggestions(sender);
            StringUtil.copyPartialMatches(args[1], playerNames, completions);
        }

        return completions;
    }

    private List<String> completeAddpoints(CommandSender sender, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 2) {
            // Suggest player names
            List<String> playerNames = getPlayerSuggestions(sender);
            StringUtil.copyPartialMatches(args[1], playerNames, completions);

        } else if (args.length == 3) {
            // Suggest common point amounts
            List<String> amounts = new ArrayList<>();
            amounts.add("10");
            amounts.add("50");
            amounts.add("100");
            amounts.add("500");
            amounts.add("1000");
            amounts.add("5000");

            // Also suggest negative amounts for deducting points
            if (sender.hasPermission("firefighter.points.add.negative")) {
                amounts.add("-10");
                amounts.add("-50");
                amounts.add("-100");
            }

            StringUtil.copyPartialMatches(args[2], amounts, completions);
        }

        return completions;
    }

    private List<String> completeResetpoints(CommandSender sender, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 2) {
            // Suggest confirmation or player names if resetting specific player
            List<String> suggestions = new ArrayList<>();
            suggestions.add("confirm");
            suggestions.addAll(getPlayerSuggestions(sender));
            StringUtil.copyPartialMatches(args[1], suggestions, completions);
        }

        return completions;
    }

    /**
     * Get list of player names for suggestions
     * Includes both online players and offline players with points
     */
    private List<String> getPlayerSuggestions(CommandSender sender) {
        List<String> playerNames = new ArrayList<>();

        // Add online players
        playerNames.addAll(Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .collect(Collectors.toList()));

        // Remove duplicates and sort
        playerNames = playerNames.stream()
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        return playerNames;
    }
}