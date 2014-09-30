package com.shortcircuit.mcinteractive.listeners;

import java.io.IOException;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.Plugin;

import com.shortcircuit.mcinteractive.EntityMetadata;
import com.shortcircuit.mcinteractive.MCInteractive;

public class PlayerListener implements Listener{
    private MCInteractive mc_interactive;
    public PlayerListener(MCInteractive mc_interactive){
        this.mc_interactive = mc_interactive;
    }
    @EventHandler (priority = EventPriority.MONITOR)
    public void onRedstone(final PlayerMoveEvent event){
        Player player = event.getPlayer();
        if(player.hasMetadata("isTracking")){
            if(player.getMetadata("isTracking").get(0).asBoolean()){
                Location from = event.getFrom();
                Location to = event.getTo();
                double oldX = from.getX();
                double newX = to.getX();
                double oldY = from.getY();
                double newY = to.getY();
                double oldZ = from.getZ();
                double newZ = to.getZ();
                int oldYaw = (int)(Math.atan2(from.getDirection().getX(), from.getDirection().getZ()) * 180 / Math.PI);
                int newYaw = (int)(Math.atan2(to.getDirection().getX(), to.getDirection().getZ()) * 180 / Math.PI);
                int oldPitch = (int)(Math.atan2(from.getDirection().getX(), from.getDirection().getY()) * 180 / Math.PI);
                int newPitch = (int)(Math.atan2(to.getDirection().getX(), to.getDirection().getY()) * 180 / Math.PI);
                try {
                    mc_interactive.getSerialManager().write("Name:" + player.getName());
                    mc_interactive.getSerialManager().write("Yaw:" + (newYaw - oldYaw));
                    mc_interactive.getSerialManager().write("Pitch:" + (newPitch - oldPitch));
                    mc_interactive.getSerialManager().write("X:" + (newX - oldX));
                    mc_interactive.getSerialManager().write("Y:" + (newY - oldY));
                    mc_interactive.getSerialManager().write("z:" + (newZ - oldZ));
                }
                catch(IOException e) {
                    e.printStackTrace();
                }
            }
        }
        else{
            player.setMetadata("isTracking", new EntityMetadata((Plugin)mc_interactive, false));
        }
    }
    @EventHandler (priority = EventPriority.MONITOR)
    public void onInteract(final PlayerInteractEvent event){
        Player player = event.getPlayer();
        if(player.hasMetadata("isTracking")){
            if(player.getMetadata("isTracking").get(0).asBoolean()){
                try {
                    mc_interactive.getSerialManager().write("Name:" + player.getName());
                    mc_interactive.getSerialManager().write("Action:" + event.getAction());
                }
                catch(IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    @EventHandler (priority = EventPriority.MONITOR)
    public void onChat(final AsyncPlayerChatEvent event){
        Player player = event.getPlayer();
        if(player.hasMetadata("isTracking")){
            if(player.getMetadata("isTracking").get(0).asBoolean()){
                try {
                    mc_interactive.getSerialManager().write("Name:" + player.getName());
                    mc_interactive.getSerialManager().write("Message:" + event.getMessage());
                }
                catch(IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    @EventHandler (priority = EventPriority.MONITOR)
    public void onShootBow(final EntityShootBowEvent event){
        if(event.getEntity() instanceof Player){
            Player player = (Player)event.getEntity();
            if(player.hasMetadata("isTracking")){
                if(player.getMetadata("isTracking").get(0).asBoolean()){
                    try {
                        mc_interactive.getSerialManager().write("Name:" + player.getName());
                        mc_interactive.getSerialManager().write("Projectile:ARROW");
                    }
                    catch(IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } 
    }
}
