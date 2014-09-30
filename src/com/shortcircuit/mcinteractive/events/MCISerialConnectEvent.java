package com.shortcircuit.mcinteractive.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author ShortCircuit908
 * 
 */
public class MCISerialConnectEvent extends Event{
    private static final HandlerList handlers = new HandlerList();
    
    public MCISerialConnectEvent() {
        
    }
    
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
    
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
