package com.shortcircuit.mcinteractive.listeners;

import java.io.IOException;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;

import com.shortcircuit.mcinteractive.MCInteractive;

public class RedstoneListener implements Listener{
    protected final BlockFace[] dirs = {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST,
            BlockFace.UP, BlockFace.DOWN, BlockFace.SELF};
    protected MCInteractive mc_interactive;
    
    public RedstoneListener(MCInteractive mc_interactive){
        this.mc_interactive = mc_interactive;
    }
    @EventHandler (priority = EventPriority.MONITOR)
    public void onRedstone(final BlockRedstoneEvent event){
        boolean oldState = event.getOldCurrent() > 0;
        boolean newState = event.getNewCurrent() > 0;
        if(oldState != newState){
            Block block = null;
            for(BlockFace face : dirs){
                block = event.getBlock().getRelative(face);
                if(mc_interactive.getTrackingManager().isTracking(block)) {
                    try {
                        if(newState) {
                            mc_interactive.getSerialManager().write(mc_interactive.getTrackingManager()
                                    .getMCIBlock(block).getOnMessage());
                        }
                        else {
                            mc_interactive.getSerialManager().write(mc_interactive.getTrackingManager()
                                    .getMCIBlock(block).getOffMessage());
                        }
                    }
                    catch(IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }
    }
}
