package com.shortcircuit.mcinteractive.serial;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

import java.io.OutputStream;
import java.util.Enumeration;

import org.bukkit.Bukkit;
@SuppressWarnings("rawtypes")
public class PortWriter{
    private Enumeration ports = null;
    private CommPortIdentifier pID = null;
    private OutputStream outStream = null;
    private SerialPort serPort = null;
    private String port = null;
    public PortWriter(String port){
        this.port = port;
        try{
            if(initialize()){
                if(!pID.isCurrentlyOwned()){
                    serPort = (SerialPort)pID.open("MCInteractive",2000);
                    outStream = serPort.getOutputStream();
                    serPort.setSerialPortParams(9600, SerialPort.DATABITS_8,
                            SerialPort.STOPBITS_1,
                            SerialPort.PARITY_NONE);
                    Bukkit.getLogger().info("[MCInteractive] Connected to " + port);
                }
                else{
                    Bukkit.getLogger().severe("[MCInteractive] " + port + " is in use");
                }
            }
            else{
                Bukkit.getLogger().severe("[MCInteractive] Failed to open " + port);
            }
        }
        catch(Exception e){
            Bukkit.getLogger().severe("[MCInteractive] Failed to open " + port);
            e.printStackTrace();
        }
    }    

    public boolean initialize(){
        ports = CommPortIdentifier.getPortIdentifiers();
        while(ports.hasMoreElements()){
            pID = (CommPortIdentifier)ports.nextElement();
            if(pID.getPortType() == CommPortIdentifier.PORT_SERIAL){
                if(pID.getName().equals(port)){
                    Bukkit.getLogger().info("[MCInteractive] " + port + " found");
                    return true;
                }
            }
        }
        Bukkit.getLogger().severe("[MCInteractive] " + port + " not found");
        return false;
    }
    public void write(String value){
        try{
            outStream.write(value.getBytes());
            outStream.flush();
            Bukkit.getLogger().info(value);
        }
        catch(Exception e){
            Bukkit.getLogger().severe("[MCInteractive] Failed to write to " + port);
        }
    }
    public void close(){
        try{
            if(outStream != null){
                outStream.close();
            }
            if(serPort != null){
                serPort.removeEventListener();
                serPort.close();
            }
            pID = null;
            ports = null;
            outStream = null;
            serPort = null;
            Bukkit.getLogger().info("[MCInteractive] Disconnected from " + port);
            port = null;
        }
        catch(Exception e){
            Bukkit.getLogger().severe("[MCInteractive] Failed to close " + port);
        }
    }
}