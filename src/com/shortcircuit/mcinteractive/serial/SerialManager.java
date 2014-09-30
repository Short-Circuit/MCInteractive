package com.shortcircuit.mcinteractive.serial;

import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.TooManyListenersException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import com.shortcircuit.mcinteractive.events.MCISerialConnectEvent;
import com.shortcircuit.mcinteractive.events.MCISerialDisconnectEvent;
import com.shortcircuit.mcinteractive.listeners.SerialListener;


/**
 * @author ShortCircuit908
 * 
 */
public class SerialManager {
    private SerialPort serial_port;
    private InputStream in_stream;
    private OutputStream out_stream;
    private int baud;
    private CommPortIdentifier port_id;
    private SerialListener serial_listener;
    
    public SerialManager() {  
    }
    
    /*
     * TODO: Attempt to connect to a serial port with the given BAUD rate
     */
    public void connect(String port, int baud) {
        disconnectSilently();
        String message = null;
        this.baud = baud;
        // Attempt to get the port identifier
        try {
            port_id = CommPortIdentifier.getPortIdentifier(port);
            // Check to make sure the port is a serial port
            if(port_id.getPortType() == CommPortIdentifier.PORT_SERIAL) {
                initialize();
            }
        }
        // The port does not exist
        catch(NoSuchPortException e) {
            message = ChatColor.RED + "[MCInteractive] " + ChatColor.LIGHT_PURPLE + port.toUpperCase()
                    + ChatColor.RED + " not found";
            disconnectSilently();
        }
        // I don't know this exception means, but it can't be good
        catch (UnsupportedCommOperationException e) {
            message = ChatColor.RED + "[MCInteractive] Error connecting to " + ChatColor.LIGHT_PURPLE
                    + port.toUpperCase();
            disconnectSilently();
        }
        // The port is already in use
        catch (PortInUseException e) {
            message = ChatColor.RED + "[MCInteractive] " + ChatColor.LIGHT_PURPLE + port.toUpperCase()
                    + ChatColor.RED + " is already in use (" + ChatColor.LIGHT_PURPLE
                    + port_id.getCurrentOwner() + ChatColor.RED + ")";
            disconnectSilently();
        }
        // General IOException
        catch(IOException e) {
            message = ChatColor.RED + "[MCInteractive] Error opening streams for " + ChatColor.LIGHT_PURPLE
                    + port.toUpperCase();
            disconnectSilently();
        }
        if(message != null) {
            alert(message);
        }
    }
    
    /*
     * TODO: Disconnect from the serial port without throwing exceptions
     */
    public void disconnectSilently(){
        try {
            disconnect();
        }
        catch(Exception e) {
        }
    }
    
    /*
     * TODO: Initialize the serial port with the desired settings
     */
    private void initialize() throws UnsupportedCommOperationException, PortInUseException, IOException {
        // Open the port
        serial_port = (SerialPort)port_id.open("MCInteractive", 2000);
        // Make sure serial events are fired
        serial_port.notifyOnDataAvailable(true);
        // Set the port parameters
        serial_port.setSerialPortParams(baud, 
                SerialPort.DATABITS_8, 
                SerialPort.STOPBITS_1,
                SerialPort.PARITY_NONE);
        // Get the input and output streams
        out_stream = serial_port.getOutputStream();
        in_stream = serial_port.getInputStream();
        String message = ChatColor.AQUA + "[MCInteractive] Connected to " + ChatColor.LIGHT_PURPLE
                + port_id.getName();
        alert(message);
        // Attempt to register the top-level serial port event listener
        try {
            serial_listener = new SerialListener(getInputStream());
            serial_port.addEventListener(serial_listener);
            Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "[MCInteractive] Top-level listener "
                    + "registered to " + ChatColor.LIGHT_PURPLE + port_id.getName());
        }
        catch(TooManyListenersException e) {
            Bukkit.getLogger().warning("[MCInteractive] Error registering event listener\n"
                    + "\tThe plugin will still function unhindered. However,\n"
                    + "\tneither this nor other plugins will be able to listen\n"
                    + "\tfor input from the serial port.");
        }
        Bukkit.getPluginManager().callEvent(new MCISerialConnectEvent());
    }
    
    /*
     * Cleanly disconnect from the serial port
     */
    public void disconnect() throws NullPointerException, IOException {
        String message  = ChatColor.AQUA + "[MCInteractive] Disconnected from " + ChatColor.LIGHT_PURPLE
                + port_id.getName();
        // Remove the top-level listener
        serial_port.removeEventListener();
        // Close the streams and port
        in_stream.close();
        out_stream.close();
        serial_port.close();
        serial_port = null;
        Bukkit.getPluginManager().callEvent(new MCISerialDisconnectEvent());
        alert(message);
    }
    
    public boolean isConnected() {
        return serial_port != null;
    }
    public InputStream getInputStream() {
        return in_stream;
    }
    public OutputStream getOutputStream() {
        return out_stream;
    }
    public void write(String value) throws NullPointerException, IOException{
        if(!value.endsWith("\\n")) {
            value += "\\n";
        }
        out_stream.write(value.getBytes());
        out_stream.flush();
    }
    public String getPortName() {
        return serial_port.getName();
    }
    private void alert(String message) {
        Bukkit.getServer().broadcast(message, "MCInteractive.Serial");
        Bukkit.getConsoleSender().sendMessage(message);
    }
}
