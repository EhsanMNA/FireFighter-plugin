package com.github.tommyt0mmy.firefighter.model;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

public class MissionManager {

    public static List<Mission> missions = new ArrayList<>();



    public static void addMission(Mission mission) {
        missions.add(mission);
    }

    public static void removeMission(Mission mission) {
        missions.remove(mission);
    }

    public static boolean containsMission(String missionId) {
        for (Mission mission : missions)
            if (mission.getId().equalsIgnoreCase(missionId)) return true;
        return false;
    }

    public static Mission getMission(String missionId) {
        for (Mission mission : missions)
            if (mission.getId().equalsIgnoreCase(missionId)) return mission;
        return null;
    }

    public static List<Mission> getMissions() {
        return missions;
    }

    public static void loadMissions(ConfigurationSection section){
        for (String missionId : section.getKeys(false)) {
            Mission mission = new Mission(missionId);
            mission.setDescription(section.getString(missionId + ".description"));
            mission.setWorldName(section.getString(missionId + ".world"));
            mission.setAltitude(section.getInt(missionId + ".altitude"));
            mission.setRegion(new Region(missionId,
                    new Location(Bukkit.getWorld(section.getString(missionId + ".world")),
                            section.getInt(missionId + ".first_position.x"),
                            section.getInt(missionId + ".first_position.y"),
                            section.getInt(missionId + ".first_position.z")),
                    new Location(Bukkit.getWorld(section.getString(missionId + ".world")),
                            section.getInt(missionId + ".second_position.x"),
                            section.getInt(missionId + ".second_position.y"),
                            section.getInt(missionId + ".second_position.z"))));
            addMission(mission);
        }
    }

}
