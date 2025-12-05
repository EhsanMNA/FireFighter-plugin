
package com.github.tommyt0mmy.firefighter.utility;

public enum Permissions {
    HELP_MENU("help"),
    GET_EXTINGUISHER("firetool.get"),
    USE_EXTINGUISHER("firetool.use"),
    FREEZE_EXTINGUISHER("firetool.freeze-durability"),
    FIRESET("fireset"),
    CHAT("firefighter.chat"),
    CHAT_TOGGLE("firefighter.chattoggle"),
    START_MISSION("fireset.startmission"),
    SET_REWARDS("fireset.rewardset"),
    SET_WAND("fireset.setwand"),
    ON_DUTY("onduty"),
    RESCUE_VICTIMS("firefighter.rescue"),
    POINTS_VIEW("firefighter.points.view"),
    POINTS_TOP("firefighter.points.top"),
    POINTS_ADD("firefighter.points.add"),
    POINTS_RESET("firefighter.points.reset");

    private String node;

    Permissions(String node) {
        this.node = node;
    }

    public String getNode() {
        return "firefighter." + node;
    }
}
