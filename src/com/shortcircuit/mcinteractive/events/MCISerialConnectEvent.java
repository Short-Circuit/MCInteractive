package com.shortcircuit.mcinteractive.events;

import gnu.io.CommPortIdentifier;

/**
 * @author ShortCircuit908
 * 
 */
public class MCISerialConnectEvent extends MCIEvent{    
    
    public MCISerialConnectEvent(CommPortIdentifier port_id) {
        super(port_id);
    }
}
