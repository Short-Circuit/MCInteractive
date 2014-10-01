package com.shortcircuit.mcinteractive.listeners;

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
public class MCIEventListener implements Listener{
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
    public void onMessage(final MCIMessageReceivedEvent event) {
    }
}
