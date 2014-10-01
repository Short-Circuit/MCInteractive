package com.shortcircuit.mcinteractive.events;

import gnu.io.CommPortIdentifier;

/**
 * @author ShortCircuit908
 * 
 */
public class MCISerialDisconnectEvent extends MCIEvent{
    
    public MCISerialDisconnectEvent(CommPortIdentifier port_id) {
        super(port_id);
    }
}
