package com.shortcircuit.mcinteractive.listeners;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import com.shortcircuit.mcinteractive.EntityMetadata;
import com.shortcircuit.mcinteractive.MCInteractive;

public class PlayerListener implements Listener{
    private MCInteractive main;
    public PlayerListener(MCInteractive main){
        this.main = main;
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
                main.getPortWriter().write("Name:" + player.getName());
                main.getPortWriter().write("Yaw:" + (newYaw - oldYaw));
                main.getPortWriter().write("Pitch:" + (newPitch - oldPitch));
                main.getPortWriter().write("X:" + (newX - oldX));
                main.getPortWriter().write("Y:" + (newY - oldY));
                main.getPortWriter().write("z:" + (newZ - oldZ));
            }
        }
        else{
            player.setMetadata("isTracking", new EntityMetadata(main, false));
        }
    }
    @EventHandler (priority = EventPriority.MONITOR)
    public void onInteract(final PlayerInteractEvent event){
        Player player = event.getPlayer();
        if(player.hasMetadata("isTracking")){
            if(player.getMetadata("isTracking").get(0).asBoolean()){
                main.getPortWriter().write("Name:" + player.getName());
                main.getPortWriter().write("Action:" + event.getAction());
            }
        }
    }
    @EventHandler (priority = EventPriority.MONITOR)
    public void onChat(final AsyncPlayerChatEvent event){
        Player player = event.getPlayer();
        if(player.hasMetadata("isTracking")){
            if(player.getMetadata("isTracking").get(0).asBoolean()){
                main.getPortWriter().write("Name:" + player.getName());
                main.getPortWriter().write("Message:" + event.getMessage());
            }
        }
    }
    @EventHandler (priority = EventPriority.MONITOR)
    public void onEgg(final EntityShootBowEvent event){
        if(event.getEntity() instanceof Player){
            Player player = (Player)event.getEntity();
            if(player.hasMetadata("isTracking")){
                if(player.getMetadata("isTracking").get(0).asBoolean()){
                    main.getPortWriter().write("Name:" + player.getName());
                    main.getPortWriter().write("Projectile:ARROW");
                }
            }
        } 
    }
}
