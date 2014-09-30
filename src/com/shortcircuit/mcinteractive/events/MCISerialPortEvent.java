package com.shortcircuit.mcinteractive.events;

import gnu.io.SerialPortEvent;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author ShortCircuit908
 * 
 */
public class MCISerialPortEvent extends Event{
    private static final HandlerList handlers = new HandlerList();
    private SerialPortEvent event;
    
    public MCISerialPortEvent(SerialPortEvent event) {
        this.event = event;
    }
    
    public int getEventType() {
        return event.getEventType();
    }
    
    public boolean getNewValue() {
        return event.getNewValue();
    }
    
    public boolean getOldValue() {
        return event.getOldValue();
    }
    
    /*
    public Object getSource() {
        return event.getSource();
    }
    */
    
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
    
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
