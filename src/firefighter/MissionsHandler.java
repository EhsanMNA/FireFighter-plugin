package firefighter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import firefighter.utility.TitleActionBarUtil;
import firefighter.utility.XMaterial;

public class MissionsHandler extends BukkitRunnable {
    private Main mainClass;
    public MissionsHandler(Main mainClass) {
        this.mainClass = mainClass;
        config = mainClass.getConfig();
    }

    private Boolean firstRun = true;
    private FileConfiguration config;
    private List < Block > setOnFire = new ArrayList < > ();

    @Override
    public void run() {
        if (firstRun) {
            firstRun = false;
            return;
        }
        if (!config.contains("missions")) {
            mainClass.console.info("There are no missions! Start setting up new missions by typing in-game '/firefighter fireset 2'");
            return;
        }
        if (mainClass.startedMission) {
            return;
        }

        int fire_lasting_ticks = Integer.valueOf(mainClass.getConfig().get("fire_lasting_seconds").toString()) * 20;
        mainClass.startedMission = true;
        mainClass.loadConfigs();
        //selecting random mission
        Random random = new Random();
        List < String > missions = new ArrayList < > (((MemorySection) config.get("missions")).getKeys(false));
        if (missions.size() < 1) {
            mainClass.console.info("There are no missions! Start setting up new missions by typing in-game '/firefighter fireset 2'");
            return;
        }
        String missionName = missions.get(random.nextInt(missions.size()));
        String missionPath = "missions." + missionName;
        //broadcast message
        World world = getWorldByName(config.get(missionPath + ".world").toString());
        Broadcast(world, "§4Fire alert", config.get(missionPath + ".description").toString(), "§eAt coordinates " + getMediumCoord(missionName), mainClass.getPermission("onduty"));
        Broadcast(world, "§4§lFire alert at coordinates §r§e" + getMediumCoord(missionName), mainClass.getPermission("onduty"));
        mainClass.console.info("[" + world.getName() + "] Started '" + missionName + "' mission");
        //starting fire
        int y = Integer.valueOf(config.get(missionPath + ".altitude").toString());
        int x1 = Integer.valueOf(config.get(missionPath + ".first_position.x").toString());
        int z1 = Integer.valueOf(config.get(missionPath + ".first_position.z").toString());
        int x2 = Integer.valueOf(config.get(missionPath + ".second_position.x").toString());
        int z2 = Integer.valueOf(config.get(missionPath + ".second_position.z").toString());
        for (int x = Math.min(x1, x2); x <= Math.max(x1, x2); x++) {
            for (int z = Math.min(z1, z2); z <= Math.max(z1, z2); z++) {
                Location currLocation = new Location(world, x, y, z);
                while (!currLocation.getBlock().getType().equals(Material.AIR)) {
                    currLocation.add(0, 1, 0);
                }
                if (random.nextInt(2) == 1) { //randomizing the spawning of the fire
                    continue;
                }
                currLocation.subtract(0, 1, 0);
                if (currLocation.getBlock().getType().equals(Material.AIR)) {
                    continue;
                }
                currLocation.add(0, 1, 0);
                Block currBlock = currLocation.getBlock();
                currBlock.setType(XMaterial.FIRE.parseMaterial());
                setOnFire.add(currBlock);
            }
        }

        //keeping the fire on
        new BukkitRunnable() {
            int timer = 0;
            public void run() {
                //mainClass.console.info("FLAG" + timer); debug
                timer++;
                if (timer >= fire_lasting_ticks / 100) {
                    cancel();
                }
                for (int i = 0; i < setOnFire.size(); i++) {
                    Block currBlock = setOnFire.get(i);
                    if (currBlock.getType().equals(XMaterial.FIRE.parseMaterial()) && !currBlock.getType().equals(Material.AIR)) {
                        continue;
                    }
                    if (random.nextInt(2) == 1) { //randomizing the respawn of the fire
                        setOnFire.remove(i);
                        continue;
                    }
                    /*TODO for (int j = 0; j < mainClass.toExtinguish.size(); j++) {
                    	if (currBlock.getLocation().equals(mainClass.toExtinguish.get(j))) {
                    		mainClass.toExtinguish.remove(j);
                    		if (random.nextInt(3) == 1) { //randomizing the respawn of the fire
                    			setOnFire.remove(i);
                    			continue;
                    		}
                    	}
                    }*/
                    currBlock.setType(XMaterial.FIRE.parseMaterial());
                }
                //mainClass.toExtinguish.clear();
            }

        }.runTaskTimer(mainClass, 0, 100);

        //TURNING OFF THE MISSION

        new BukkitRunnable() {
            public void run() {
                mainClass.console.info("Mission ended");
                mainClass.startedMission = false;
                mainClass.missionName = "";
                //mainClass.toExtinguish.clear();
                setOnFire.clear();
                cancel();
            }
        }.runTaskTimer(mainClass, (long)(fire_lasting_ticks * (1.5)), 1);
    }

    private World getWorldByName(String name) {
        World result = null;
        for (World currWorld: mainClass.getServer().getWorlds()) {
            if (currWorld.getName().equals(name)) {
                result = currWorld;
                break;
            }
        }
        return result;
    }

    private void Broadcast(World w, String title, String subtitle, String hotbar, String permission) {
        for (Player dest: w.getPlayers()) {
            if (!dest.hasPermission(permission)) {
                continue;
            }
            TitleActionBarUtil.sendTitle(dest, title, 10, 100, 20);
            TitleActionBarUtil.sendSubTitle(dest,  subtitle, 10, 100, 20);
            try {
                new BukkitRunnable() {
                    int timer = 0;
                    public void run() {
                        timer++;
                        TitleActionBarUtil.sendActionBarMessage(dest, hotbar);
                        if (timer >= 4) {
                            cancel();
                        }
                    }
                }.runTaskTimer(mainClass, 0, 50);
            } catch (Exception e) {}
        }
    }

    private void Broadcast(World w, String message, String permission) {
        for (Player dest: w.getPlayers()) {
            if (dest.hasPermission(permission)) {
                dest.sendMessage(message);
            }
        }
    }

    private String getMediumCoord(String missionName) { //returns the medium position of the mission
        String res = "";
        String missionPath = "missions." + missionName;
        res += (((Integer.valueOf(config.get(missionPath + ".first_position.x").toString()) + Integer.valueOf(config.get(missionPath + ".second_position.x").toString())) / 2) + ""); //X
        res += " ";
        res += (config.get(missionPath + ".altitude").toString()); // Y
        res += " ";
        res += (((Integer.valueOf(config.get(missionPath + ".first_position.z").toString()) + Integer.valueOf(config.get(missionPath + ".second_position.z").toString())) / 2) + ""); // Z
        return res;
    }

}