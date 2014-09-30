package com.shortcircuit.mcinteractive.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.shortcircuit.mcinteractive.events.MCIMessageReceivedEvent;
import com.shortcircuit.mcinteractive.events.MCISerialConnectEvent;
import com.shortcircuit.mcinteractive.events.MCISerialDisconnectEvent;
import com.shortcircuit.mcinteractive.events.MCISerialPortEvent;

/**
 * @author ShortCircuit908
 * 
 */
public class ThingeyListener implements Listener{
    
    public ThingeyListener() {
        
    }
    
    @EventHandler
    public void onMessageReceived(final MCIMessageReceivedEvent event) {
        Bukkit.getLogger().info(event.getMessage());
    }
    @EventHandler
    public void onSerialConnect(final MCISerialConnectEvent event) {
        Bukkit.getLogger().info("Connected to thigummy");
    }
    @EventHandler
    public void onSerialDisconnect(final MCISerialDisconnectEvent event) {
        Bukkit.getLogger().info("Disconnected from thigummy");
    }
    @EventHandler
    public void onSerialEvent(final MCISerialPortEvent event) {
    }
}
