package com.shortcircuit.mcinteractive.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.shortcircuit.mcinteractive.events.MCIMessageReceivedEvent;
import com.shortcircuit.mcinteractive.events.MCIMessageSentEvent;
import com.shortcircuit.mcinteractive.events.MCISerialConnectEvent;
import com.shortcircuit.mcinteractive.events.MCISerialDisconnectEvent;
import com.shortcircuit.mcinteractive.events.MCISerialPortEvent;

/**
 * @author ShortCircuit908
 * 
 */
public class MCIEventListener implements Listener{
    protected boolean log_to_console = true;
    
    public MCIEventListener(boolean log_to_console) {
        this.log_to_console = log_to_console;
    }
    
    @EventHandler
    public void onConnect(final MCISerialConnectEvent event) {
    }
    
    @EventHandler
    public void onDisconnect(final MCISerialDisconnectEvent event) {
    }
    
    @EventHandler
    public void onEvent(final MCISerialPortEvent event) {
    }
    
    @EventHandler
    public void onReceiveMessage(final MCIMessageReceivedEvent event) {
        Bukkit.getLogger().info("[" + event.getCommPortIdentifier().getName() + " -> MCI] "
                + event.getMessage());
    }
    
    @EventHandler
    public void onSendMessage(final MCIMessageSentEvent event) {
        Bukkit.getLogger().info("[MCI -> " + event.getCommPortIdentifier().getName() + "] "
                + event.getMessage());
    }
}
