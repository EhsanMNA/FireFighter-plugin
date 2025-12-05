package com.github.tommyt0mmy.firefighter.model;

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
            mission.setFirstX(section.getInt(missionId + ".first_position.x"));
            mission.setFirstZ(section.getInt(missionId + ".first_position.z"));
            mission.setSecondX(section.getInt(missionId + ".second_position.x"));
            mission.setSecondZ(section.getInt(missionId + ".second_position.z"));
            mission.setFirstY(section.getInt(missionId + ".second_position.y", 64));
            mission.setSecondY(section.getInt(missionId + ".second_position.y", 92));
            addMission(mission);
        }
    }

}
