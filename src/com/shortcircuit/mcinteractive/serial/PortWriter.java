package com.shortcircuit.mcinteractive.serial;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
@SuppressWarnings("rawtypes")
public class PortWriter{
    private Enumeration ports = null;
    private CommPortIdentifier portID = null;
    private OutputStream stream = null;
    private InputStream inStream = null;
    private SerialPort serial = null;
    private String port = null;
    private CommandSender sender = null;
    public PortWriter(String port, CommandSender sender){
        if(!port.equals("")){
            this.port = port;
            if(sender == null){
                sender = Bukkit.getConsoleSender();
            }
            this.sender = sender;
            try{
                if(initialize()){
                    if(!portID.isCurrentlyOwned()){
                        serial = (SerialPort)portID.open("MCInteractive",2000);
                        stream = serial.getOutputStream();
                        inStream = serial.getInputStream();
                        serial.setSerialPortParams(9600, SerialPort.DATABITS_8,
                                SerialPort.STOPBITS_1,
                                SerialPort.PARITY_NONE);
                        sender.sendMessage(ChatColor.AQUA + "[MCInteractive] Connected to " + port);
                    }
                    else{
                        sender.sendMessage(ChatColor.RED + "[MCInteractive] " + port + " is in use");
                    }
                }
                else{
                    sender.sendMessage(ChatColor.RED + "[MCInteractive] Failed to open " + port);
                }
            }
            catch(Exception e){
                sender.sendMessage(ChatColor.RED + "[MCInteractive] Failed to open " + port);
            }
        }
    }    

    public boolean initialize(){
        ports = CommPortIdentifier.getPortIdentifiers();
        while(ports.hasMoreElements()){
            portID = (CommPortIdentifier)ports.nextElement();
            if(portID.getPortType() == CommPortIdentifier.PORT_SERIAL){
                if(portID.getName().equals(port)){
                    sender.sendMessage(ChatColor.AQUA + "[MCInteractive] " + port + " found");
                    return true;
                }
            }
        }
        sender.sendMessage(ChatColor.RED + "[MCInteractive] " + port + " not found");
        return false;
    }
    public void write(String value){
        if(stream != null){
            try{
                stream.write(value.getBytes());
                stream.flush();
            }
            catch(Exception e){
                sender.sendMessage(ChatColor.RED + "[MCInteractive] Failed to write to " + port);
            }
        }
    }
    public void setSender(CommandSender sender){
        if(sender == null){
            sender = Bukkit.getConsoleSender();
        }
        this.sender = sender;
    }
    public void close(){
        try{
            if(serial != null){
                inStream.close();
                stream.close();
                serial.removeEventListener();
                serial.close();
                sender.sendMessage(ChatColor.AQUA + "[MCInteractive] Disconnected from " + port);
                port = null;
            }
        }
        catch(Exception e){
            sender.sendMessage(ChatColor.RED + "[MCInteractive] Failed to close " + port);
        }
    }
}