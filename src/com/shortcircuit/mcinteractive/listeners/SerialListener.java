package com.shortcircuit.mcinteractive.listeners;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import java.io.InputStream;
import java.util.Scanner;

import org.bukkit.Bukkit;

import com.shortcircuit.mcinteractive.events.MCIMessageReceivedEvent;
import com.shortcircuit.mcinteractive.events.MCISerialPortEvent;

/**
 * @author ShortCircuit908
 * 
 */
public class SerialListener implements SerialPortEventListener{
    protected Scanner scanner;
    protected CommPortIdentifier port_id;

    public SerialListener(CommPortIdentifier port_id, InputStream input_stream, String delimiter) {
        this.port_id = port_id;
        scanner = new Scanner(input_stream);
        scanner.useDelimiter(delimiter);
    }

    @Override
    public synchronized void serialEvent(SerialPortEvent event) {
        if(event.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
            readSerial();
        }
        Bukkit.getPluginManager().callEvent(new MCISerialPortEvent(port_id, event));
    }
    
    protected void readSerial() {
        String message = scanner.next();
        Bukkit.getPluginManager().callEvent(new MCIMessageReceivedEvent(port_id, message));
    }
}
