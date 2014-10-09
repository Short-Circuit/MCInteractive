package com.shortcircuit.mcinteractive.events;

import gnu.io.CommPortIdentifier;

/**
 * @author ShortCircuit908
 * 
 */
public class MCIMessageSentEvent extends MCIEvent{
    protected final String message;
    
    public MCIMessageSentEvent(CommPortIdentifier port_id, String message) {
        super(port_id);
        this.message = message;
    }
    
    public String getMessage() {
        return message;
    }
    
}
