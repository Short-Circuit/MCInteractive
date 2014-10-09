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

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import com.shortcircuit.mcinteractive.events.MCIMessageSentEvent;
import com.shortcircuit.mcinteractive.events.MCISerialConnectEvent;
import com.shortcircuit.mcinteractive.events.MCISerialDisconnectEvent;
import com.shortcircuit.mcinteractive.listeners.SerialListener;


/**
 * @author ShortCircuit908
 * 
 */
public class SerialManager {
    protected SerialPort serial_port;
    protected InputStream input_stream;
    protected OutputStream output_stream;
    protected int baud;
    protected CommPortIdentifier port_id;
    protected SerialListener serial_listener;
    protected String delimiter;
    
    public SerialManager(String delimiter) {
        this.delimiter = delimiter;
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
            e.printStackTrace();
        }
    }

    /*
     * TODO: Initialize the serial port with the desired settings
     */
    protected void initialize() throws UnsupportedCommOperationException, PortInUseException, IOException {
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
        output_stream = serial_port.getOutputStream();
        input_stream = serial_port.getInputStream();
        String message = ChatColor.AQUA + "[MCInteractive] Connected to " + ChatColor.LIGHT_PURPLE
                + port_id.getName();
        alert(message);
        // Attempt to register the top-level serial port event listener
        try {
            serial_listener = new SerialListener(port_id, input_stream, delimiter);
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
        Bukkit.getPluginManager().callEvent(new MCISerialConnectEvent(port_id));
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
        input_stream.close();
        output_stream.close();
        serial_port.close();
        serial_port = null;
        Bukkit.getPluginManager().callEvent(new MCISerialDisconnectEvent(port_id));
        alert(message);
    }

    public boolean isConnected() {
        return serial_port != null;
    }

    public void write(String value) throws NullPointerException, IOException{
        if(isConnected()) {
            if(!value.endsWith(delimiter)) {
                value += delimiter;
            }
            output_stream.write(value.getBytes());
            output_stream.flush();
            Bukkit.getPluginManager().callEvent(new MCIMessageSentEvent(port_id,
                    StringUtils.removeEnd(value, delimiter)));
        }
    }

    public CommPortIdentifier getCommPortIdentifier() {
        return port_id;
    }

    protected void alert(String message) {
        Bukkit.getServer().broadcast(message, "mcinteractive.serial");
        Bukkit.getConsoleSender().sendMessage(message);
    }
}
