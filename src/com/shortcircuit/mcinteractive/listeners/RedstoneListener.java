package com.shortcircuit.mcinteractive.listeners;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;

import com.shortcircuit.mcinteractive.MCInteractive;

public class RedstoneListener implements Listener{
    private MCInteractive main;
    public RedstoneListener(MCInteractive main){
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
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
}
