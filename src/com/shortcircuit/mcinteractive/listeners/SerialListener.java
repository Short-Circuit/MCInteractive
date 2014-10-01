package com.shortcircuit.mcinteractive.listeners;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.bukkit.Bukkit;

import com.shortcircuit.mcinteractive.events.MCIMessageReceivedEvent;
import com.shortcircuit.mcinteractive.events.MCISerialPortEvent;

/**
 * @author ShortCircuit908
 * 
 */
public class SerialListener implements SerialPortEventListener{
    protected BufferedReader reader;
    protected CommPortIdentifier port_id;
    
    public SerialListener(CommPortIdentifier port_id, InputStream input_stream) {
        this.port_id = port_id;
        this.reader = new BufferedReader(new InputStreamReader(input_stream));
    }
    
    /*
     * Called on each SerialPortEvent
     */
    @Override
    public void serialEvent(SerialPortEvent event) {
        // Read the available data
        if(event.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
            readSerial();
        }
        // Fire a new MCISerialPortEvent
        Bukkit.getPluginManager().callEvent(new MCISerialPortEvent(port_id, event));
    }
    
    /*
     * TODO: Read the data from the serial port
     */
    private void readSerial() {
        try {
            String message = reader.readLine();
            Bukkit.getPluginManager().callEvent(new MCIMessageReceivedEvent(port_id, message));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
