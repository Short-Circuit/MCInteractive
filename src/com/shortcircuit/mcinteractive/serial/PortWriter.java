package com.shortcircuit.mcinteractive.serial;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration;

import org.bukkit.Bukkit;
@SuppressWarnings("rawtypes")
public class PortWriter{
    private Enumeration ports;
    private CommPortIdentifier pID;
    private OutputStream outStream;
    private SerialPort serPort;
    private String port;
    public PortWriter(String port){
        this.port = port;
        try{
            initialize();
            serPort = (SerialPort)pID.open("MCInteractive",2000);
            outStream = serPort.getOutputStream();
            serPort.setSerialPortParams(9600, SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);
        }
        catch(Exception e){
            Bukkit.getLogger().severe("Failed to open com port!");
        }
    }    

    public void initialize(){
        ports = CommPortIdentifier.getPortIdentifiers();
        while(ports.hasMoreElements()){
            pID = (CommPortIdentifier)ports.nextElement();
            System.out.println("Port " + pID.getName());
            if (pID.getPortType() == CommPortIdentifier.PORT_SERIAL){
                if (pID.getName().equals(port)){
                    Bukkit.getLogger().info(port + " found");
                }
            }
        }
    }
    public void write(String value){
        try{
            outStream.write(value.getBytes());
        }
        catch(IOException e){
            Bukkit.getLogger().severe("Failed to write to com port!");
        }
    }
    public void close(){
        try{
            serPort.close();
            outStream.close();
        }
        catch(Exception e){
            Bukkit.getLogger().severe("Failed to close com port!");
        }
    }
}