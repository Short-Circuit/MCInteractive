package com.shortcircuit.mcinteractive;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

/**
 * @author ShortCircuit908
 * 
 */
public class MCIPlayer implements ConfigurationSerializable{
    protected boolean track_movement;
    protected boolean track_actions;
    protected boolean track_projectiles;
    protected boolean track_chat;
    
    public MCIPlayer(boolean track_movements, boolean track_actions, boolean track_projectiles,
            boolean track_chat) {
        this.track_movement = track_movements;
        this.track_actions = track_actions;
        this.track_projectiles = track_projectiles;
        this.track_chat = track_chat;
    }
    public MCIPlayer(Map<String, Object> serialized) {
        track_projectiles = (boolean)serialized.get("track_projectiles");
        track_movement = (boolean)serialized.get("track_movement");
        track_actions = (boolean)serialized.get("track_actions");
        track_chat = (boolean)serialized.get("track_chat");
    }
    
    public boolean isTrackingMovement() {
        return track_movement;
    }
    
    public boolean isTrackingActions() {
        return track_actions;
    }
    
    public boolean isTrackingProjectiles() {
        return track_projectiles;
    }
    
    public boolean isTrackingChat() {
        return track_chat;
    }
    
    @Override
    public Map<String, Object> serialize(){
        Map<String, Object> serialized = new HashMap<String, Object>();
        serialized.put("track_movement", track_movement);
        serialized.put("track_actions", track_actions);
        serialized.put("track_projectiles", track_projectiles);
        serialized.put("track_chat", track_chat);
        return serialized;
    }
    
    public static MCIPlayer deserialize(Map<String, Object> serialized) {
        return new MCIPlayer((boolean)serialized.get("track_movement"), (boolean)serialized.get("track_actions"),
                (boolean)serialized.get("track_projectiles"), (boolean)serialized.get("track_chat"));
    }
    public static MCIPlayer valueOf(Map<String, Object> serialized) {
        return new MCIPlayer((boolean)serialized.get("track_movement"), (boolean)serialized.get("track_actions"),
                (boolean)serialized.get("track_projectiles"), (boolean)serialized.get("track_chat"));    }
}
