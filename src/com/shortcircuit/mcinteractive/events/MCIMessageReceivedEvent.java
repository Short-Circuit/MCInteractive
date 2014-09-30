package com.shortcircuit.mcinteractive.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author ShortCircuit908
 * 
 */
public class MCIMessageReceivedEvent extends Event{
    private static final HandlerList handlers = new HandlerList();
    private String message;
    
    public MCIMessageReceivedEvent(String message) {
        this.message = message;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
    
    public String getMessage() {
        return message;
    }
    
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
