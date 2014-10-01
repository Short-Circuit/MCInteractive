package com.shortcircuit.mcinteractive.events;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPortEvent;

/**
 * @author ShortCircuit908
 * 
 */
public class MCISerialPortEvent extends MCIEvent{
    private SerialPortEvent event;
    
    public MCISerialPortEvent(CommPortIdentifier port_id, SerialPortEvent event) {
        super(port_id);
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
}
