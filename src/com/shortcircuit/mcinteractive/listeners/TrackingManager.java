package com.shortcircuit.mcinteractive.listeners;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.shortcircuit.mcinteractive.MCIBlock;
import com.shortcircuit.mcinteractive.MCIPlayer;

/**
 * @author ShortCircuit908
 * 
 */
public class TrackingManager {
    protected FileConfiguration block_tracking;
    protected FileConfiguration player_tracking;
    protected File block_file;
    protected File player_file;
    protected HashMap<String, MCIBlock> blocks = new HashMap<String, MCIBlock>();
    protected HashMap<String, MCIPlayer> players = new HashMap<String, MCIPlayer>();

    public TrackingManager(String directory) {
        new File(directory + "/Tracking").mkdirs();
        block_file = new File(directory + "/Tracking/Blocks.yml");
        player_file = new File(directory + "/Tracking/Players.yml");
        try {
            block_file.createNewFile();
            player_file.createNewFile();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
        initialize();
    }

    public void load() {
        block_tracking = YamlConfiguration.loadConfiguration(block_file);
        player_tracking = YamlConfiguration.loadConfiguration(player_file);
    }

    public boolean isTracking(Player player) {
        return players.containsKey(player.getName());
    }

    public boolean isTracking(Block block) {
        return blocks.containsKey(getNameFromBlock(block));
    }

    public MCIBlock getMCIBlock(Block block) {
        return blocks.get(getNameFromBlock(block));
    }

    public MCIPlayer getMCIPlayer(Player player) {
        return players.get(player.getName());
    }

    public void addTracking(Player player) {
        players.put(player.getName(), new MCIPlayer(true, true, true, true));
    }

    public void addTracking(Block block, String on_message, String off_message) {
        blocks.put(getNameFromBlock(block), new MCIBlock(on_message, off_message));
    }

    public void removeTracking(Player player) {
        players.remove(player.getName());
    }

    public void removeTracking(Block block) {
        blocks.remove(getNameFromBlock(block));
    }

    public void save() {
        try {
            block_tracking.save(block_file);
            player_tracking.save(player_file);
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

    protected void initialize() {
        load();
        for(String key : block_tracking.getKeys(false)) {
            blocks.put(key, (MCIBlock)block_tracking.get(key));
        }
        for(String key : player_tracking.getKeys(false)) {
            players.put(key, (MCIPlayer)player_tracking.get(key));
        }
    }

    public void close() {
        try {
            block_file.delete();
            block_file.createNewFile();
            player_file.delete();
            player_file.createNewFile();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
        load();
        for(String key : blocks.keySet()) {
            block_tracking.set(key, blocks.get(key));
        }
        for(String key : players.keySet()) {
            player_tracking.set(key, players.get(key));
        }
        save();
    }

    private String getNameFromBlock(Block block) {
        Location location = block.getLocation();
        return location.getWorld().getName() + "," + (int)location.getX() + "," + (int)location.getY()
                + "," + (int)location.getZ();
    }
    /*
    private Block getBlockFromName(String name) {
        String[] loc = name.split(",");
        return Bukkit.getWorld(loc[0]).getBlockAt(Integer.parseInt(loc[1]), Integer.parseInt(loc[2]),
                Integer.parseInt(loc[3]));
    }
    */
}
