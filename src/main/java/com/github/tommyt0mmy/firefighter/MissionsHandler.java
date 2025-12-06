package com.github.tommyt0mmy.firefighter;

import com.github.tommyt0mmy.firefighter.model.Mission;
import com.github.tommyt0mmy.firefighter.model.MissionManager;
import com.github.tommyt0mmy.firefighter.model.MissionStorage;
import com.github.tommyt0mmy.firefighter.model.RescueManager;
import com.github.tommyt0mmy.firefighter.utility.JalaliCalendar;
import com.github.tommyt0mmy.firefighter.utility.Permissions;
import com.github.tommyt0mmy.firefighter.utility.XMaterial;
import com.github.tommyt0mmy.firefighter.utility.titles.ActionBar;
import com.github.tommyt0mmy.firefighter.utility.titles.Titles;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class MissionsHandler extends BukkitRunnable {

    private FireFighter FireFighterClass = FireFighter.getInstance();

    public MissionsHandler() {
        config = FireFighterClass.getConfig();
    }

    private Boolean firstRun = true;
    private FileConfiguration config;
    private List<Block> setOnFire = new ArrayList<>();
    private final RescueManager rescueManager = FireFighter.getInstance().rescueManager;
    static Material fire = XMaterial.FIRE.parseMaterial();
    static int fireKeepTimer = 0;
    static int fireSpreadTimer = 0; // New timer for spreading fires

    public static Sound notifSound = Sound.BLOCK_ANVIL_BREAK;
    public static float notifSoundV = 10;
    public static float notifSoundF = 1.0f;

    public static HashMap<UUID, Integer> delays = new HashMap<>();

    @Override
    public void run() {
        for (UUID id : delays.keySet()) {
            int i = delays.get(id) - 1;
            if (i == 0) delays.remove(id);
            else delays.put(id, i);
        }

        if (firstRun) {
            firstRun = false;
            return;
        }
        if (MissionManager.getMissions().isEmpty()) {
            FireFighterClass.console.info("There are no missions! Start setting up new missions by typing in-game '/firefighter fireset 2'");
            return;
        }

        int fire_lasting_ticks = MissionStorage.fire_lasting_seconds * 20;
        Random random = new Random();
        Mission mission = MissionManager.getMissions().get(random.nextInt(MissionManager.getMissions().size()));
        if (FireFighterClass.missionName != null && !FireFighterClass.missionName.isEmpty())
            mission = MissionManager.getMission(FireFighterClass.missionName);


        if (FireFighterClass.startedMission) {
            // Keeping the fire on
            fireKeepTimer++;

            // Check if it's time to spawn smoke
            int smokeFrequency = FireFighterClass.getConfig().getInt("fire_effects.smoke_frequency_ticks", 5);
            boolean shouldSpawnSmoke = (fireKeepTimer % smokeFrequency) == 0;

            if (fireKeepTimer > 20){
                if ((fireKeepTimer % 5) == 0) {
                    if (fireKeepTimer >= fire_lasting_ticks / 100 || setOnFire.size() < mission.getRegion().getLength()) {
                        // TURNING OFF THE MISSION
                        fireKeepTimer = 0;
                        Bukkit.getWorld(mission.getWorldName()).setGameRule(GameRule.DO_FIRE_TICK, true);
                        turnOffInstructions(mission); // Pass mission for end message
                        return;
                    }
                    else {
                        for (int i = 0; i < setOnFire.size(); i++) {
                            Block currBlock = setOnFire.get(i);

                            // Check if fire is still burning
                            if (!currBlock.getType().equals(fire)) {
                                // Random chance to re-ignite
                                if (random.nextInt(3) == 1) { // 33% chance
                                    currBlock.setType(fire);
                                } else {
                                    setOnFire.remove(i);
                                    i--;
                                    continue;
                                }
                            }

                            // Spawn enhanced smoke effects
                            if (shouldSpawnSmoke) {
                                if (random.nextInt(3) == 1) spawnEnhancedSmoke(currBlock);

                                // Also spawn fire particles
                                if (FireFighterClass.getConfig().getBoolean("fire_effects.flame_particles", true)) {
                                    if (random.nextInt(3) == 1) spawnFireParticles(currBlock);
                                }
                            }
                        }
                    }

                }

                fireSpreadTimer++;
                int spreadInterval = config.getInt("fire_spread_interval_ticks", 3);
                if (fireSpreadTimer >= spreadInterval) {
                    fireSpreadTimer = 0;
                    spreadFires(mission);
                }
            }
        }


        if (System.currentTimeMillis() < FireFighterClass.nextMissionStart && !FireFighterClass.programmedStart) return;
        if (!FireFighterClass.missionsIntervalState && !FireFighterClass.programmedStart) return;

        // Check if any players with ON_DUTY permission are online before starting
        boolean hasDutyPlayers = false;
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission(Permissions.ON_DUTY.getNode())) {
                hasDutyPlayers = true;
                break;
            }
        }
        if (!hasDutyPlayers) {
//            FireFighterClass.console.info("No players on duty, skipping mission start.");
            return;
        }

        // Start mission
        FireFighterClass.startedMission = true;
        if (!FireFighterClass.programmedStart) {
            FireFighterClass.missionName = mission.getId();
            FireFighterClass.nextMissionStart = System.currentTimeMillis() + (MissionStorage.missions_interval * 1000L) + (MissionStorage.fire_lasting_seconds * 1000L);
        } else {
            FireFighterClass.programmedStart = false;
        }

        rescueManager.startRescue(mission);

        FireFighterClass.PlayerContribution.clear();
        // Broadcast message
        if (mission.getWorldName() == null) {
            turnOffInstructions(mission);
            cancel();
        }
        World world = Bukkit.getWorld(mission.getWorldName());
        if (world == null) return;

        String title = ChatColor.translateAlternateColorCodes('&', FireFighterClass.messages.getMessage("startedmission_title")
                .replaceAll("<mission_description>", mission.getDescription())
                .replaceAll("<mission_name>", mission.getId())
                .replaceAll("<coordinates>", getMediumCoord(mission.getId())));
        String subtitle = ChatColor.translateAlternateColorCodes('&', FireFighterClass.messages.getMessage("startedmission_subtitle")
                .replaceAll("<mission_description>", mission.getDescription())
                .replaceAll("<mission_name>", mission.getId())
                .replaceAll("<coordinates>", getMediumCoord(mission.getId())));
        String hotBar = ChatColor.translateAlternateColorCodes('&', FireFighterClass.messages.getMessage("startedmission_hotbar")
                .replaceAll("<mission_name>", mission.getId())
                .replaceAll("<mission_description>", mission.getDescription())
                .replaceAll("<coordinates>", getMediumCoord(mission.getId())));
        String chat = ChatColor.translateAlternateColorCodes('&', FireFighterClass.messages.getMessage("startedmission_chat")
                .replaceAll("<mission_name>", mission.getId())
                .replaceAll("<mission_description>", mission.getDescription())
                .replaceAll("<coordinates>", getMediumCoord(mission.getId())));

        Broadcast(world, title, subtitle, hotBar, Permissions.ON_DUTY.getNode());
        Broadcast(world, chat, Permissions.ON_DUTY.getNode());

        FireFighterClass.console.info("[" + world.getName() + "] Started '" + mission.getId() + "' mission");
        Bukkit.getWorld(mission.getWorldName()).setGameRule(GameRule.DO_FIRE_TICK, false); // Prevent natural spread/extinguish
        // Starting fire
        int y = mission.getAltitude();
        int x1 = mission.getRegion().getPoint1().getBlockX();
        int z1 = mission.getRegion().getPoint1().getBlockZ();
        int x2 = mission.getRegion().getPoint2().getBlockX();
        int z2 = mission.getRegion().getPoint2().getBlockZ();
        for (int x = Math.min(x1, x2); x <= Math.max(x1, x2); x++)
            for (int z = Math.min(z1, z2); z <= Math.max(z1, z2); z++) {
                Location currLocation = new Location(world, x, y, z);
                int n = 0;
                while (!currLocation.getBlock().getType().equals(Material.AIR)) {
                    currLocation.add(0, 1, 0);
                    n++;
                    if (n == 30) break;
                }

                if (random.nextBoolean()) continue;

                currLocation.subtract(0, 1, 0);
                if (currLocation.getBlock().getType().equals(Material.AIR)) continue;

                currLocation.add(0, 1, 0);
                Block currBlock = currLocation.getBlock();
                assert fire != null;
                currBlock.setType(fire);
                setOnFire.add(currBlock);
            }
    }

    // New method to spread fires
    private void spreadFires(Mission mission) {
        Random random = new Random();
        World world = Bukkit.getWorld(mission.getWorldName());
        if (world == null) return;

        int spreadCount = config.getInt("fire_spread_count_per_interval", 15); // Configurable: how many new fires to add
        int y = mission.getAltitude();
        int minX = mission.getRegion().getMinX();
        int maxX = mission.getRegion().getMaxX();
        int minZ = mission.getRegion().getMinZ();
        int maxZ = mission.getRegion().getMaxZ();
        int minY = mission.getRegion().getMinY();
        int maxY = mission.getRegion().getMaxY();

        for (int i = 0; i < spreadCount; i++) {
            int x = minX + random.nextInt(maxX - minX + 1);
            int z = minZ + random.nextInt(maxZ - minZ + 1);
            int yr = minY + random.nextInt(maxY - minY + 1);
            Location currLocation = new Location(world, x, y, z);
            int n = 0;
            while (!currLocation.getBlock().getType().equals(Material.AIR)) {
                currLocation.add(0, 1, 0);
                n++;
                if (n == 30) break;
            }

            currLocation.subtract(0, 1, 0);
            if (currLocation.getBlock().getType().equals(Material.AIR)) continue;

            currLocation.add(0, 1, 0);

            if (!mission.getRegion().contains(currLocation)) continue;

            Block currBlock = currLocation.getBlock();
            if (currBlock.getType() != fire) {
                currBlock.setType(fire);
                setOnFire.add(currBlock);
            }
        }

        for (int i = 0; i < setOnFire.size(); i++) {
            Block currBlock = setOnFire.get(i);
            if (currBlock.getType().equals(fire) && !currBlock.getType().equals(Material.AIR)) continue;
            if (random.nextInt(2) == 1) {
                setOnFire.remove(i);
                continue;
            }
            currBlock.setType(fire);
            currBlock.getWorld().spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, currBlock.getLocation().add(0.5, 1, 0.5), 1, 0.2, 0.5, 0.2, 0.01);
        }
    }

    private void Broadcast(World w, String title, String subtitle, String hotbar, String permission) {
        if (w == null) return;

        for (Player dest : w.getPlayers()) {
            if (!dest.hasPermission(permission)) continue;
            Titles.sendTitle(dest, 10, 100, 20, title, subtitle);
            try {
                new BukkitRunnable() {
                    int timer = 0;

                    public void run() {
                        timer++;
                        ActionBar.sendActionBar(dest, hotbar);
                        if (timer >= 4) cancel();
                    }
                }.runTaskTimer(FireFighterClass, 0, 50);
            } catch (Exception ignored) {}
        }
    }

    private void Broadcast(World w, String message, String permission) {
        if (w == null) return;
        for (Player dest : w.getPlayers())
            if (dest.hasPermission(permission)){
                dest.sendMessage(message);
                dest.playSound(dest.getLocation(), notifSound,notifSoundV,notifSoundF);
            }
    }

    private String getMediumCoord(String missionName) {
        String res = "";
        String missionPath = "missions." + missionName;
        res += (((Integer.parseInt(config.get(missionPath + ".first_position.x").toString()) + Integer.parseInt(config.get(missionPath + ".second_position.x").toString())) / 2) + ""); //X
        res += " ";
        res += (config.get(missionPath + ".altitude").toString()); // Y
        res += " ";
        res += (((Integer.parseInt(config.get(missionPath + ".first_position.z").toString()) + Integer.parseInt(config.get(missionPath + ".second_position.z").toString())) / 2) + ""); // Z
        return res;
    }

    private void turnOffInstructions(Mission mission) {
        FireFighterClass.console.info("Mission ended");
        FireFighterClass.startedMission = false;
        FireFighterClass.missionName = "";

        // turn of fire
        for (Block block : setOnFire) block.setType(Material.AIR);
        setOnFire.clear();

        // Broadcast end message with top firefighters
        broadcastEndMessage(mission);

        double multiplier = FireFighterClass.getConfig().getDouble("points.per_contribution", 1.0);
        for (Map.Entry<UUID, Double> entry : FireFighterClass.PlayerContribution.entrySet()) {
            int pointsToAdd = (int) Math.round(entry.getValue() * multiplier);
            FireFighterClass.pointsManager.addPoints(entry.getKey(), pointsToAdd);
        }

        rescueManager.cleanup();

        FireFighterClass.PlayerContribution.clear();
    }

    private void broadcastEndMessage(Mission mission) {
        JalaliCalendar jalaliCalendar = new JalaliCalendar();

        int year = jalaliCalendar.get(JalaliCalendar.YEAR);
        int month = jalaliCalendar.get(JalaliCalendar.MONTH) + 1;
        int day = jalaliCalendar.get(JalaliCalendar.DAY_OF_MONTH);

        // Format as yyyy-mm-dd
        String formattedDate = String.format("%04d-%02d-%02d", year, month, day);

        String header = FireFighterClass.messages.getMessage("mission_end_header").replace("<location_id>", mission.getId())
                .replace("<time>", formattedDate + ", " + LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
        String footer = FireFighterClass.messages.getMessage("mission_end_footer");
        String topHeader = FireFighterClass.messages.getMessage("mission_end_top_firefighters");

        // Calculate participation percentage
        int totalFirefighters = 0;
        int participatingFirefighters = FireFighterClass.PlayerContribution.size();

        // Count total players with ON_DUTY permission in the mission world
        World world = Bukkit.getWorld(mission.getWorldName());
        if (world != null) {
            for (Player player : world.getPlayers()) {
                if (player.hasPermission(Permissions.ON_DUTY.getNode())) {
                    totalFirefighters++;
                }
            }
        }

        // Calculate participation percentage
        double participationPercentage = 0;
        if (totalFirefighters > 0) {
            participationPercentage = (double) participatingFirefighters / totalFirefighters * 100;
        }

        // Format participation message
        String participationMsg = FireFighterClass.messages.getMessage("mission_end_participation")
                .replace("<participating>", String.valueOf(participatingFirefighters))
                .replace("<total>", String.valueOf(totalFirefighters))
                .replace("<percentage>", String.format("%.1f", participationPercentage));

        // Sort contributions descending
        List<Map.Entry<UUID, Double>> sortedContributions = new ArrayList<>(FireFighterClass.PlayerContribution.entrySet());
        sortedContributions.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        // Build top 3
        StringBuilder topList = new StringBuilder();
        for (int i = 0; i < Math.min(10, sortedContributions.size()); i++) {
            Map.Entry<UUID, Double> entry = sortedContributions.get(i);
            Player player = Bukkit.getPlayer(entry.getKey());
            String name = (player != null) ? player.getName() : "Unknown";
            if (!player.hasPermission(Permissions.ON_DUTY.getNode())) continue;
            topList.append(FireFighterClass.messages.getMessage("mission_end_top_entry")
                            .replace("<rank>", String.valueOf(i + 1))
                            .replace("<name>", name)
                            .replace("<score>", String.format("%.1f", entry.getValue())))
                    .append("\n");
        }

        // If no one participated
        if (sortedContributions.isEmpty()) {
            topList.append(FireFighterClass.messages.getMessage("mission_end_no_participants")).append("\n");
        }

        // Broadcast to all with ON_DUTY permission
        if (world != null) {
            for (Player dest : world.getPlayers()) {
                if (dest.hasPermission(Permissions.ON_DUTY.getNode())) {
                    dest.sendMessage(FireFighter.colorize(header));
                    dest.sendMessage(FireFighter.colorize(participationMsg));
                    dest.sendMessage(FireFighter.colorize(topHeader));
                    dest.sendMessage(FireFighter.colorize(topList.toString()));
                    dest.sendMessage(FireFighter.colorize(footer));
                }
            }
        }
    }

    private void spawnEnhancedSmoke(Block fireBlock) {
        if (!FireFighterClass.getConfig().getBoolean("fire_effects.smoke_enabled", true)) {
            return;
        }

        Random random = new Random();

        // Check chance to spawn smoke
        if (random.nextDouble() > FireFighterClass.getConfig().getDouble("fire_effects.smoke_chance", 0.8)) {
            return;
        }

        World world = fireBlock.getWorld();
        Location smokeLocation = fireBlock.getLocation().add(0.5, 0.2, 0.5);

        // Get configuration values
        int particleCount = FireFighterClass.getConfig().getInt("fire_effects.smoke_particle_count", 3);
        double particleSize = FireFighterClass.getConfig().getDouble("fire_effects.smoke_particle_size", 0.5);
        double particleSpeed = FireFighterClass.getConfig().getDouble("fire_effects.smoke_particle_speed", 0.1);
        double spreadHorizontal = FireFighterClass.getConfig().getDouble("fire_effects.smoke_spread_horizontal", 0.3);
        double spreadVertical = FireFighterClass.getConfig().getDouble("fire_effects.smoke_spread_vertical", 0.5);

        // Spawn multiple particle types for more realistic smoke
        for (int i = 0; i < particleCount; i++) {
            // Main smoke particles
            world.spawnParticle(Particle.CAMPFIRE_COSY_SMOKE,
                    smokeLocation.getX() + (random.nextDouble() - 0.5) * spreadHorizontal,
                    smokeLocation.getY(),
                    smokeLocation.getZ() + (random.nextDouble() - 0.5) * spreadHorizontal,
                    0,  // Count (0 for extraData-based)
                    spreadHorizontal, spreadVertical, spreadHorizontal, particleSpeed);

            // Add some CLOUD particles for thicker smoke
            if (random.nextDouble() < 0.1) {
                world.spawnParticle(Particle.CLOUD,
                        smokeLocation.getX() + (random.nextDouble() - 0.5) * spreadHorizontal * 0.5,
                        smokeLocation.getY() + random.nextDouble() * 0.2,
                        smokeLocation.getZ() + (random.nextDouble() - 0.5) * spreadHorizontal * 0.5,
                        0,
                        spreadHorizontal * 0.7, spreadVertical * 0.8, spreadHorizontal * 0.7,
                        particleSpeed * 0.8);
            }

            // Add some white ash particles occasionally
            if (random.nextDouble() < 0.1) {
                world.spawnParticle(Particle.WHITE_ASH,
                        smokeLocation.getX() + (random.nextDouble() - 0.5) * spreadHorizontal,
                        smokeLocation.getY() + 0.1,
                        smokeLocation.getZ() + (random.nextDouble() - 0.5) * spreadHorizontal,
                        0,
                        spreadHorizontal * 0.5, spreadVertical * 0.3, spreadHorizontal * 0.5,
                        particleSpeed * 1.5);
            }
        }

        // Add upward rising smoke effect
        spawnRisingSmokeColumn(fireBlock);
    }

    private void spawnRisingSmokeColumn(Block fireBlock) {
        if (!FireFighterClass.getConfig().getBoolean("fire_effects.smoke_column_enabled", true)) {
            return;
        }

        World world = fireBlock.getWorld();
        Location baseLocation = fireBlock.getLocation().add(0.5, 0.2, 0.5);
        Random random = new Random();

        int columnHeight = FireFighterClass.getConfig().getInt("fire_effects.smoke_column_height", 5);
        int particlesPerLevel = FireFighterClass.getConfig().getInt("fire_effects.smoke_column_particles", 2);

        for (int y = 0; y < columnHeight; y++) {
            for (int i = 0; i < particlesPerLevel; i++) {
                double offsetX = (random.nextDouble() - 0.5) * 0.2;
                double offsetZ = (random.nextDouble() - 0.5) * 0.2;

                // Smoke gets wider as it rises
                double spread = 0.1 + (y * 0.05);

                world.spawnParticle(Particle.CAMPFIRE_COSY_SMOKE,
                        baseLocation.getX() + offsetX,
                        baseLocation.getY() + y + (random.nextDouble() * 0.3),
                        baseLocation.getZ() + offsetZ,
                        0,
                        spread, 0.1, spread,
                        0.01);
            }
        }
    }

    private void spawnFireParticles(Block fireBlock) {
        World world = fireBlock.getWorld();
        Location particleLocation = fireBlock.getLocation().add(0.5, 0.2, 0.5);
        Random random = new Random();

        // Spawn flame particles
        int flameCount = FireFighterClass.getConfig().getInt("fire_effects.flame_particle_count", 2);

        for (int i = 0; i < flameCount; i++) {
            world.spawnParticle(Particle.FLAME,
                    particleLocation.getX() + (random.nextDouble() - 0.5) * 0.3,
                    particleLocation.getY() + random.nextDouble() * 0.5,
                    particleLocation.getZ() + (random.nextDouble() - 0.5) * 0.3,
                    0,
                    0.1, 0.1, 0.1,
                    0.02);
        }

        // Spawn some lava particles for bigger fires
        if (random.nextDouble() < 0.2) {
            world.spawnParticle(Particle.DRIP_LAVA,
                    particleLocation.getX(),
                    particleLocation.getY() + 0.1,
                    particleLocation.getZ(),
                    0,
                    0, 0.1, 0,
                    0.1);
        }
    }
}