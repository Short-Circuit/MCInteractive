package com.shortcircuit.mcinteractive.events;

import gnu.io.CommPortIdentifier;

/**
 * @author ShortCircuit908
 * 
 */
public class MCIMessageReceivedEvent extends MCIEvent{
    private String message;
    
    public MCIMessageReceivedEvent(CommPortIdentifier port_id, String message) {
        super(port_id);
        this.message = message;
    }
    
    public String getMessage() {
        return message;
    }
    
}
