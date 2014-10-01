package com.shortcircuit.mcinteractive.events;

import gnu.io.CommPortIdentifier;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author ShortCircuit908
 * 
 */
public abstract class MCIEvent extends Event{
    private static final HandlerList handlers = new HandlerList();
    private final CommPortIdentifier comm_port;
    
    public MCIEvent(CommPortIdentifier comm_port) {
        this.comm_port = comm_port;
    }
    
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
        
    public static HandlerList getHandlerList() {
        return handlers;
    }
    
    public CommPortIdentifier getCommPortIdentifier() {
        return comm_port;
    }
}
