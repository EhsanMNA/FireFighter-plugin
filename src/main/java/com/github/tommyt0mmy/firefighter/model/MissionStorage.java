package com.github.tommyt0mmy.firefighter.model;

import org.bukkit.configuration.ConfigurationSection;

public class MissionStorage {


    public static int missions_interval = 100;
    public static int fire_lasting_seconds = 60;
    public static boolean allow_missions_interval = true;


    public static void initialize(ConfigurationSection section){
        MissionStorage.allow_missions_interval = section.getBoolean("allow_missions_interval", true);
        MissionStorage.missions_interval = section.getInt("missions_interval", 100);
        MissionStorage.fire_lasting_seconds = section.getInt("fire_lasting_seconds", 300);
    }
}
