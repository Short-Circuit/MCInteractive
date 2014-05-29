package com.shortcircuit.mcinteractive.listeners;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;

import com.shortcircuit.mcinteractive.MCInteractive;

public class RedstoneListener implements Listener{
    final BlockFace[] dirs = {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST,
            BlockFace.UP, BlockFace.DOWN, BlockFace.SELF};
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
            for(BlockFace face : dirs){
                block = event.getBlock().getRelative(face);
                if(block.hasMetadata("BreakoutOn") && block.hasMetadata("BreakoutOff")){
                    break;
                }
            }
            if(block.hasMetadata("BreakoutOn") && block.hasMetadata("BreakoutOff")){
                String state = "BreakoutOff";
                if(newState){
                    state = "BreakoutOn";
                }
                main.pWriter.write(block.getMetadata(state).get(0).asString());
            }
        }
    }
    /*
    @EventHandler (priority = EventPriority.MONITOR)
    public void onRedstone(final BlockRedstoneEvent event){
        boolean oldState = event.getOldCurrent() > 0;
        boolean newState = event.getNewCurrent() > 0;
        if(oldState != newState){
            Block block = event.getBlock();
            Block trigger = null;
            if(block.hasMetadata("BreakoutOn") && block.hasMetadata("BreakoutOff")){
                Location loc = block.getLocation();
                BlockFace dir = null;
                int yaw = (int)(Math.atan2(loc.getDirection().getX(), loc.getDirection().getZ()) * 180 / Math.PI);
                int pitch = (int)(Math.atan2(loc.getDirection().getX(), loc.getDirection().getY()) * 180 / Math.PI);
                if(yaw > 45 && yaw <= 135){
                   dir = BlockFace.NORTH;
                }
                else if(yaw > 135 && yaw <= 225){
                    dir = BlockFace.EAST;
                }
                else if(yaw > 225 && yaw <= 315){
                    dir = BlockFace.SOUTH;
                }
                else{
                    dir = BlockFace.WEST;
                }
                try{
                    if((!oldState && block.isBlockPowered()) || (!newState && !block.isBlockPowered())){
                        String state = "BreakoutOff";
                        if(newState){
                            state = "BreakoutOn";
                        }
                        main.pWriter.write(block.getMetadata(state).get(0).asString());
                    }
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
     */
}
