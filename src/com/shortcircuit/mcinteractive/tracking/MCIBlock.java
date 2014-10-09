package com.shortcircuit.mcinteractive.tracking;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

/**
 * @author ShortCircuit908
 * 
 */
public class MCIBlock implements ConfigurationSerializable{
    protected final String message_on;
    protected final String message_off;
    
    public MCIBlock(String message_on, String message_off) {
        this.message_on = message_on;
        this.message_off = message_off;
    }
    
    public MCIBlock(Map<String, Object> serialized) {
        this.message_on = (String)serialized.get("message_on");
        this.message_off = (String)serialized.get("message_off");
    }
    
    public String getOnMessage() {
        return message_on;
    }
    
    public String getOffMessage() {
        return message_off;
    }
    
    public Map<String, Object> serialize(){
        Map<String, Object> serialized = new HashMap<String, Object>();
        serialized.put("message_on", message_on);
        serialized.put("message_off", message_off);
        return serialized;
    }
    
    public static MCIBlock deserialize(Map<String, Object> serialized) {
        return new MCIBlock((String)serialized.get("message_on"), (String)serialized.get("message_off"));
    }
    
    public static MCIBlock valueOf(Map<String, Object> serialized) {
        return new MCIBlock((String)serialized.get("message_on"), (String)serialized.get("message_off"));
    }
}
