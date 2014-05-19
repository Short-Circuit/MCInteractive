package com.shortcircuit.mcinteractive.listeners;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.Plugin;

import com.shortcircuit.mcinteractive.MCInteractive;

public class RedstoneListener implements Listener{
    //private Plugin plugin;
    private MCInteractive main;
    public RedstoneListener(Plugin plugin, MCInteractive main){
        //this.plugin = plugin;
        this.main = main;
    }
    @EventHandler (priority = EventPriority.MONITOR)
    public void onRedstone(final BlockRedstoneEvent event){
        boolean oldState = event.getOldCurrent() > 0;
        boolean newState = event.getNewCurrent() > 0;
        if(oldState != newState){
            Block block = null;
            for(BlockFace face : BlockFace.values()){
                block = event.getBlock().getRelative(face);
                if(block.hasMetadata("BreakoutOn") && block.hasMetadata("BreakoutOff")){
                    break;
                }
            }
            if(block.hasMetadata("BreakoutOn") && block.hasMetadata("BreakoutOff")){
                try{
                    String state = "BreakoutOff";
                    if(newState){
                        state = "BreakoutOn";
                    }
                    main.pWriter.write(block.getMetadata(state).get(0).asString());
                    Bukkit.getLogger().info(block.getMetadata(state).get(0).asString());
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
    @EventHandler (priority = EventPriority.MONITOR)
    public void onPlayer(final PlayerMoveEvent event){
    }
}
