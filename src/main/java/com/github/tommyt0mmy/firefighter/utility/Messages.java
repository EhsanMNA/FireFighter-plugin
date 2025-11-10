/* CUSTOM MESSAGES CLASS */

package com.github.tommyt0mmy.firefighter.utility;

import com.github.tommyt0mmy.firefighter.FireFighter;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

public class Messages
{

    public Messages()
    {
        loadMessagesFile();
    }

    private FireFighter FireFighterClass = FireFighter.getInstance();

    private FileConfiguration messagesConfig;
    private File messagesConfigFile;
    private final String fileName = "messages.yml";

    private HashMap<String, String> messagesMap = new HashMap<String, String>() {
        {
            //GENERAL MESSAGES

            put("messages.only_players_command", "Only players can execute this command");
            put("messages.fire_extinguisher", "Fire Extinguisher");
            put("messages.ingame_prefix", "[FireFighter]");
            put("messages.invalid_permissions", "Invalid permissions!");
            put("messages.page_not_found", "Page not found!");
            put("messages.hold_right_click", "Hold Right Click");
            put("messages.unknown_command", "Unknown command. Type '/help' for help.");
            put("messages.received_reward", "&aCongratulations you received a reward for being the best firefighter!");
            put("messages.fireset_wand", "Fireset Wand");
            put("messages.fireset_wand_instructions", "Left click: select pos #1; Right click: select pos #2");
            put("messages.fireset_wand_set", "New wand set!");
            put("messages.fireset_first_position_set", "First position set at <x>, <y>, <z>");
            put("messages.fireset_second_position_set", "Second position set at <x>, <y>, <z>");
            put("messages.fireset_mission_not_found", "Mission not found!");
            put("messages.fireset_delete", "Mission successfully deleted!");
            put("messages.fireset_invalid_selection", "Invalid selection!");
            put("messages.fireset_added_mission", "Mission successfully added!");
            put("messages.fireset_missions_header", "&c&l+ - - - &e&l&oMissions list&c&l - - - +&r");
            put("messages.fireset_missions_footer", "&c&l+ - &r&epage <current page>/<total>&c&l- - - - - - - - - - - - +&r");
            put("messages.fireset_missions_name", "&7-&e<mission>");
            put("messages.fireset_missions_world", "&7world:&e <world>");
            put("messages.fireset_missions_position", "&7position:&e x <x> y <y> z <z>");
            put("messages.fireset_another_mission_started", "&cAnother mission started!");
            put("messages.fireset_started_mission", "&aMission started successfully!");
            put("messages.startedmission_title", "&4Fire alert");
            put("messages.startedmission_subtitle", "<mission_description>");
            put("messages.startedmission_hotbar", "&eAt coordinates <coordinates>");
            put("messages.startedmission_chat", "&4&lFire alert at coordinates &r&e<coordinates>");
            put("messages.firechat_toggle_on", "&4&lFireFighter chat has been enabled.");
            put("messages.firechat_toggle_off", "&4&lFireFighter chat has been disabled.");
            put("messages.firechat_125", "&e%player% &4&lFireFighter &7-  = &6سلام اینجا اتیش گرفته");
            put("messages.firechat_125_empty", "&4&lFireFighter: &eYou can not send empty messages");
            put("messages.firechat_125_send", "&4&lFireFighter: &f Your 125 request has sent, wait for firefighters");
            put("messages.firechat_125_delay", "&4&lFireFighter:&f Please wait <time> seconds to send a new request");
            put("messages.mission_end_header", "--------------\\nThe <location_id> fire has been turned off!");
            put("messages.mission_end_top_firefighters", "Top firefighters:");
            put("messages.mission_end_participation", "&6Participation: &e<participating>&6/&e<total> &6firefighters (&e<percentage>%&6) helped in the mission");
            put("messages.mission_end_top_entry", "&e<rank>. &6<name> &7- &e<score> points");
            put("messages.mission_end_no_participants", "&cNo firefighters participated in this mission");
            put("messages.mission_end_footer", "---------------");
            put("messages.picked_up", "&4&lFireFighter &aYou picked up a victim! Carry them to safety.");
            put("messages.rescued", "&4&lFireFighter &aVictim rescued! <rescued>/<total> saved.");
            put("messages.all_rescued", "&4&lFireFighter &aAll victims rescued!");
            put("messages.mission_end_requires_rescue", "&4&lFireFighter &cMission not over—rescue all victims!");
            put("messages.inside_area", "&4&lFireFighter &cYou must leave the fire area to rescue the victim!");

            //COMMANDS DESCRIPTIONS

            put("description.firefighter", "Shows the help menu");
            put("description.firetool", "Gives you a fire extinguisher");
            put("description.fireset", "Controls missions");

            //HELP PAGES

            //page 1

            put("help.page1.line1", "&c&l+ - - - &e&l&oFireFighter help&c&l - - - +&r");
            put("help.page1.line2", "<arrow2>&cCommands overall");
            put("help.page1.line3", "&7&o/firefighter [command name] [page #] &e&oTo get a more detailed description of the command");
            put("help.page1.line4", "&7&o/firefighter permissions &e&oTo get a list of the permissions");
            put("help.page1.line5", "<arrow1>&7fireset&e <fireset_description>");
            put("help.page1.line6", "<arrow1>&7firetool&e <firetool_description>");
            put("help.page1.line7", "<arrow1>&7firefighter [command|page] ...&e <firefighter_description>");
            put("help.page1.line8", "&c&l+ - &r&epage 1/1&c&l- - - - - - - - - - - - +&r");

            //fireset 1

            put("help.fireset1.line1", "&c&l+ - - - &e&l&oFireFighter help&c&l - - - +&r");
            put("help.fireset1.line2", "<arrow2>&c'fireset' Command");
            put("help.fireset1.line3", "<arrow1>&eUsage: &7/fireset [setwand|addmission|editmission|deletemission] ...");
            put("help.fireset1.line4", "<arrow1>&ePermission node: &7<fireset_permission>");
            put("help.fireset1.line5", "<arrow1>&eDescription: &7Sets a new point that will catch at a random time on fire, firefighters ");
            put("help.fireset1.line6", "&7should extinguish the fire to get a reward.&8 &oN.B. Only admins should have access to this command");
            put("help.fireset1.line7", "&7You can change the wand with &o/fireset setwand&7 with the permission <setwand_permission>");
            put("help.fireset1.line8", "&c&l+ - &r&epage 1/6&c&l- - - - - - - - - - - - +&r");

            //fireset 2

            put("help.fireset2.line1", "&c&l+ - - - &e&l&oFireFighter help&c&l - - - +&r");
            put("help.fireset2.line2", "<arrow2>&c'fireset' Command");
            put("help.fireset2.line3", "<arrow1>&e - How to add a mission - ");
            put("help.fireset2.line4", "&e1)&7Select with the wand (&o/fireset&7) the area of the mission that will be set on fire");
            put("help.fireset2.line5", "&e2)&7Create a new mission with &o/fireset addmission <name> [description]");
            put("help.fireset2.line6", "<arrow1>&ename &7The name that identifies the mission");
            put("help.fireset2.line7", "<arrow1>&edescription &7The message that will be broadcasted");
            put("help.fireset2.line8", "&c&l+ - &r&epage 2/6&c&l- - - - - - - - - - - - +&r");

            //fireset 3

            put("help.fireset3.line1", "&c&l+ - - - &e&l&oFireFighter help&c&l - - - +&r");
            put("help.fireset3.line2", "<arrow2>&c'fireset' Command");
            put("help.fireset3.line3", "<arrow1>&e - How to edit a mission - ");
            put("help.fireset3.line4", "&7&o/fireset editmission <name> <name|description> <new value>");
            put("help.fireset3.line5", "<arrow1>&ename &7The name that identifies the mission");
            put("help.fireset3.line6", "<arrow1>&ename|description &7The parameter that will be modified");
            put("help.fireset3.line7", "<arrow1>&enew value &7The new value of the parameter");
            put("help.fireset3.line8", "&c&l+ - &r&epage 3/6&c&l- - - - - - - - - - - - +&r");

            //fireset 4

            put("help.fireset4.line1", "&c&l+ - - - &e&l&oFireFighter help&c&l - - - +&r");
            put("help.fireset4.line2", "<arrow2>&c'fireset' Command");
            put("help.fireset4.line3", "<arrow1>&e - How to add/edit rewards of a mission - ");
            put("help.fireset4.line4", "&7&o/fireset editmission <name> rewards");
            put("help.fireset4.line5", "<arrow1>&ename &7The name that identifies the mission");
            put("help.fireset4.line6", "<arrow1>&7The GUI that contains rewards will open, ");
            put("help.fireset4.line7", "&7if there are no rewards set for that specific mission the GUI will be empty");
            put("help.fireset4.line8", "&c&l+ - &r&epage 4/6&c&l- - - - - - - - - - - - +&r");

            //fireset 5

            put("help.fireset5.line1", "&c&l+ - - - &e&l&oFireFighter help&c&l - - - +&r");
            put("help.fireset5.line2", "<arrow2>&c'fireset' Command");
            put("help.fireset5.line3", "<arrow1>&e - How to show a list of every mission - ");
            put("help.fireset5.line4", "&7&o/fireset missions [page]");
            put("help.fireset5.line5", "<arrow1>&epage &7Specify the number of the page");
            put("help.fireset5.line6", "<arrow1>&7A menu will be shown, two missions per page, ");
            put("help.fireset5.line7", "&7the menu gives informations about the position of every mission");
            put("help.fireset5.line8", "&c&l+ - &r&epage 5/6&c&l- - - - - - - - - - - - +&r");

            //fireset 6

            put("help.fireset6.line1", "&c&l+ - - - &e&l&oFireFighter help&c&l - - - +&r");
            put("help.fireset6.line2", "<arrow2>&c'fireset' Command");
            put("help.fireset6.line3", "<arrow1>&e - How to manually start a mission - ");
            put("help.fireset6.line4", "&7&o/fireset startmission <name>");
            put("help.fireset6.line5", "<arrow1>&emission &7The name that identifies the mission");
            put("help.fireset6.line6", "<arrow1>&7The command will trigger the mission selected, ");
            put("help.fireset6.line7", "&7only one mission per time is supported");
            put("help.fireset6.line8", "&c&l+ - &r&epage 6/6&c&l- - - - - - - - - - - - +&r");

            //firetool 1

            put("help.firetool1.line1", "&c&l+ - - - &e&l&oFireFighter help&c&l - - - +&r");
            put("help.firetool1.line2", "<arrow2>&c'firefirghter' Command");
            put("help.firetool1.line3", "<arrow1>&eUsage: &7/firefighter [command|page] ...");
            put("help.firetool1.line4", "<arrow1>&ePermission node: &7<firefighter_permission>");
            put("help.firetool1.line5", "<arrow1>&eDescription: &7Shows the help menu, pressing tab while typing &o/firefighter &r");
            put("help.firetool1.line6", "&7will autocomplete the command showing every existent page that you can have access to.");
            put("help.firetool1.line7", "&c&l+ - - - - - - - - - - - - - - - - - +&r");

            //firefighter 1

            put("help.firefighter1.line1", "&c&l+ - - - &e&l&oFireFighter help&c&l - - - +&r");
            put("help.firefighter1.line2", "<arrow2>&c'firefirghter' Command");
            put("help.firefighter1.line3", "<arrow1>&eUsage: &7/firefighter [command|page] ...");
            put("help.firefighter1.line4", "<arrow1>&ePermission node: &7<firefighter_permission>");
            put("help.firefighter1.line5", "<arrow1>&eDescription: &7Shows the help menu, pressing tab while typing &o/firefighter &r");
            put("help.firefighter1.line6", "&7will autocomplete the command showing every existent page that you can have access to.");
            put("help.firefighter1.line7", "&c&l+ - - - - - - - - - - - - - - - - - +&r");

            //permissions 1

            put("help.permissions1.line1", "&c&l+ - - - &e&l&oFireFighter help&c&l - - - +&r");
            put("help.permissions1.line2", "<arrow2>&cPermissions list");
            put("help.permissions1.line3", "<arrow1>&7<firefighter_permission>&e To execute the &7&o/firefighter&e command");
            put("help.permissions1.line4", "<arrow1>&7<get-extinguisher_permission>&e To get a fire extinguisher (&7&o/firetool&e)");
            put("help.permissions1.line5", "<arrow1>&7<use-extinguisher_permission>&e To utilize a fire extinguisher");
            put("help.permissions1.line6", "<arrow1>&7<freeze.extinguisher_permission>&e To freeze the usage of fire extinguishers");
            put("help.permissions1.line7", "<arrow1>&7<fireset_permission>&e To execute the &7&o/fireset&e command (&oadd/edit/delete missions&7)");
            put("help.permissions1.line8", "<arrow1>&7<setrewards_permission>&e To edit the rewards list of a mission");
            put("help.permissions1.line9", "<arrow1>&7<setwand_permission>&e To execute the &7&o/firetool setwand&e command");
            put("help.permissions1.line10", "<arrow1>&7<onduty_permission>&e To receive missions");
            put("help.permissions1.line11", "&c&l+ - - - - - - - - - - - - - - - - - +&r");

        }
    };

    private void loadMessagesFile() { //loading messages.yml
        messagesConfigFile = new File(FireFighterClass.dataFolder, fileName);
        if (!messagesConfigFile.exists()) {
            messagesConfigFile.getParentFile().mkdirs();
            FireFighterClass.saveResource(fileName, false);
            FireFighterClass.console.info("Created messages.yml");
            FireFighterClass.console.info("To modify ingame messages edit messages.yml and reload the plugin");
        }

        messagesConfig = new YamlConfiguration();
        try {
            messagesConfig.load(messagesConfigFile);
            loadMessages();
        } catch (Exception e) {
            FireFighterClass.console.severe("Couldn't load messages.yml file properly!");
        }
    }

    private void loadMessages() {

        boolean needsRewrite = false; //A rewrite is needed when loaded on the server there is a older version of messages.yml, without newer messages

        for (String messageKey : messagesMap.keySet()) {
            boolean result = loadMessage(messageKey);
            needsRewrite = needsRewrite || result;
        }

        //Once every message is loaded on the messagesMap, if needsRewrite is true, messages.yml gets closed, deleted, and rewritten with every message
        if (needsRewrite) {
            try {
                if (messagesConfigFile.delete()) {
                    messagesConfigFile.getParentFile().mkdirs(); //creating file
                    messagesConfigFile.createNewFile();
                    messagesConfig.load(messagesConfigFile);
                    for (String messageKey : messagesMap.keySet()) messagesConfig.set(messageKey, messagesMap.get(messageKey));

                    messagesConfig.save(messagesConfigFile);
                } else FireFighterClass.console.severe("Couldn't load messages.yml file properly!");

            } catch (Exception e) {FireFighterClass.console.severe("Couldn't load messages.yml file properly!");}
        }

        FireFighterClass.console.info("Loaded custom messages");
    }

    private boolean loadMessage(String messageName) { //returns true if the message is not found, letting loadMessages() know if a rewrite of the file is needed or not
        boolean returnValue = false;

        String path = messageName;
        if (messagesConfig.getString(path, null) == null) returnValue = true;
        if (messagesConfig.getString(path) == null) return true;

        messagesMap.put(messageName, messagesConfig.getString(messageName)); //loading messages into messagesMap
        return returnValue;
    }

    public String getMessage(String messageName) {
        return messagesMap.getOrDefault("messages." + messageName, "A null message");
    }

    public String formattedMessage(String color, String messageName) { //Automatically puts the prefix and the color to the message
        return String.format("%s%s %s", color, getMessage("ingame_prefix"), getMessage(messageName));
    }

    public String formattedText(String color, String message) { //Shouldn't be used, very similar to formattedMessage() but returns a formatted version of a non customizable message, for a better user experience every message should be customizable
        return String.format("%s%s %s", color, getMessage("ingame_prefix"), message);
    }

    public String getDescription(String commandName) {
        return messagesMap.get("description." + commandName);
    }

    public String getHelpPage(String pageName) {
        final String arrow1 = "&e&l>";
        final String arrow2 = "&c&l>";
        String path = "help." + pageName;
        ConfigurationSection pageSection = messagesConfig.getConfigurationSection(path);
        StringBuilder page = new StringBuilder();

        //Sorting keys by alphabetical order
        Set<String> keysSet = pageSection.getKeys(false);
        String[] lineKeys = new String[keysSet.size()];
        lineKeys = keysSet.toArray(lineKeys);
        Arrays.sort(lineKeys);

        for (String lineKey : lineKeys) {
            String lineContext = messagesMap.get(path + "." + lineKey) + "\n";

            lineContext = lineContext.replaceAll("<arrow1>", arrow1);
            lineContext = lineContext.replaceAll("<arrow2>", arrow2);

            //descriptions
            lineContext = lineContext.replaceAll("<firefighter_description>", getDescription("firefighter"));
            lineContext = lineContext.replaceAll("<firetool_description>", getDescription("firetool"));
            lineContext = lineContext.replaceAll("<fireset_description>", getDescription("fireset"));

            //permissions
            lineContext = lineContext.replaceAll("<fireset_permission>", Permissions.FIRESET.getNode());
            lineContext = lineContext.replaceAll("<onduty_permission>", Permissions.ON_DUTY.getNode());
            lineContext = lineContext.replaceAll("<firefighter_permission>", Permissions.HELP_MENU.getNode());
            lineContext = lineContext.replaceAll("<helpmenu_permission>", Permissions.HELP_MENU.getNode());
            lineContext = lineContext.replaceAll("<setwand_permission>", Permissions.SET_WAND.getNode());
            lineContext = lineContext.replaceAll("<setrewards_permission>", Permissions.SET_REWARDS.getNode());
            lineContext = lineContext.replaceAll("<firetool_permission>", Permissions.GET_EXTINGUISHER.getNode());
            lineContext = lineContext.replaceAll("<get-extinguisher_permission>", Permissions.GET_EXTINGUISHER.getNode());
            lineContext = lineContext.replaceAll("<use-extinguisher_permission>", Permissions.USE_EXTINGUISHER.getNode());
            lineContext = lineContext.replaceAll("<freeze-extinguisher_permission>", Permissions.FREEZE_EXTINGUISHER.getNode());

            //chat colors and formatting
            lineContext = ChatColor.translateAlternateColorCodes('&', lineContext);

            page.append(lineContext);
        }

        return page.toString();
    }
}
