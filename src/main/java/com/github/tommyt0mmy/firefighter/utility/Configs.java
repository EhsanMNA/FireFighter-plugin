package com.github.tommyt0mmy.firefighter.utility;

import com.github.tommyt0mmy.firefighter.FireFighter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;

public class Configs {

    public Configs() {
        loadConfigsFile();
    }

    private final FireFighter FireFighterClass = FireFighter.getInstance();

    private FileConfiguration configsConfiguration;
    private File configsFile;
    private final String fileName = "config.yml";

    private void loadConfigsFile() {
        //loading configs.yml
        configsFile = new File(FireFighterClass.dataFolder, fileName);
        if (!configsFile.exists()) {
            configsFile.getParentFile().mkdirs();
            FireFighterClass.saveResource(fileName, false);
            FireFighterClass.console.info("Created config.yml");
            FireFighterClass.console.info("To modify the plugin's behaviour edit config.yml and reload the plugin");
        }

        configsConfiguration = new YamlConfiguration();

        try {
            configsConfiguration.load(configsFile);
            loadConfigs();
            configsConfiguration.save(configsFile);
        }catch (Exception e) {
            logError();
        }
    }

    public void loadConfigs() {
        try { //LOADING
            configsConfiguration.load(configsFile);
        }catch (Exception e) {
            logError();
        }

        configsConfiguration.set("points.per_contribution", 1.0);
        configsConfiguration.set("animal_rescue.points_reward", 50);
        configsConfiguration.set("points.page_size", 10);

        int fire_lasting_seconds = configsConfiguration.getInt("fire_lasting_seconds", -1);
        int missions_interval = configsConfiguration.getInt("missions_interval", -1);
        FireFighterClass.missionsIntervalState = configsConfiguration.getBoolean("allow_missions_interval", false);
        if (fire_lasting_seconds == -1) {
            fire_lasting_seconds = 300;
            configsConfiguration.set("fire_lasting_seconds", fire_lasting_seconds);
        }
        if (missions_interval == -1) {
            missions_interval = 3600;
            configsConfiguration.set("missions_interval", missions_interval);
        }
        if (configsConfiguration.getItemStack("fireset.wand", null) == null) {
            ItemStack wand = new ItemStack(Material.STICK);
            ItemMeta wandMeta = wand.getItemMeta();
            wandMeta.setDisplayName(FireFighterClass.messages.formattedMessage(ChatColor.YELLOW.toString(), "fireset_wand"));
            wand.setItemMeta(wandMeta);
            configsConfiguration.set("fireset.wand", wand);
        }

        try {
            //SAVING
            configsConfiguration.save(configsFile);
        }catch (IOException e) {
            logError();
        }
    }

    public void set(String path, Object object)
    {
        configsConfiguration.set(path, object);
    }

    public FileConfiguration getConfig() {
        loadConfigs();
        return configsConfiguration;
    }

    public boolean saveToFile() {
        boolean returnValue = true;
        try {
            configsConfiguration.save(configsFile);
        }catch (IOException e) {
            returnValue = false;
        }

        return returnValue;
    }

    private void logError()
    {
        FireFighterClass.console.severe("Couldn't load config.yml properly!");
    }

}
