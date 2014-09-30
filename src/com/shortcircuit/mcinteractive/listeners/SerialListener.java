package com.shortcircuit.mcinteractive.listeners;

import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import java.io.IOException;
import java.io.InputStream;

import org.bukkit.Bukkit;

import com.shortcircuit.mcinteractive.events.MCIMessageReceivedEvent;
import com.shortcircuit.mcinteractive.events.MCISerialPortEvent;

/**
 * @author ShortCircuit908
 * 
 */
public class SerialListener implements SerialPortEventListener{
    private byte[] read_buffer = new byte[400];
    private String received = "";
    private InputStream inStream;
    
    public SerialListener(InputStream inStream) {
        this.inStream = inStream;
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
        Bukkit.getPluginManager().callEvent(new MCISerialPortEvent(event));
    }
    
    /*
     * TODO: Read the data from the serial port
     */
    private void readSerial() {
        try {
            // Get the number of available bytes
            int available_bytes = inStream.available();
            if (available_bytes > 0) {
                // Read from the stream to the buffer
                inStream.read(read_buffer, 0, available_bytes);
                // Append the received data
                received += new String(read_buffer, 0, available_bytes);
                // Check for end-of-message
                if(received.endsWith("\n")) {
                    // Fire a new MCIMessageReceivedEvent
                    Bukkit.getPluginManager().callEvent(new MCIMessageReceivedEvent(received));
                    // Reset the string
                    received = "";
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
