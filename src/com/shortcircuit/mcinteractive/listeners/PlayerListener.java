package com.shortcircuit.mcinteractive.listeners;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
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
                double oldYaw = from.getYaw();
                double newYaw = to.getYaw();
                double oldPitch = from.getPitch();
                double newPitch = to.getPitch();
                main.pWriter.write("Name:" + event.getPlayer().getName());
                main.pWriter.write("Yaw:" + (newYaw - oldYaw));
                main.pWriter.write("Pitch:" + (newPitch - oldPitch));
                main.pWriter.write("X:" + (newX - oldX));
                main.pWriter.write("Y:" + (newY - oldY));
                main.pWriter.write("z:" + (newZ - oldZ));
            }
        }
        else{
            player.setMetadata("isTracking", new EntityMetadata(main, false));
        }
    }
}
