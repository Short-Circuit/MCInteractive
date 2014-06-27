package com.shortcircuit.mcinteractive;

import gnu.io.CommPortIdentifier;

import java.util.Enumeration;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CommandBlock;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_7_R3.command.CraftBlockCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.shortcircuit.mcinteractive.listeners.PlayerListener;
import com.shortcircuit.mcinteractive.listeners.RedstoneListener;
import com.shortcircuit.mcinteractive.serial.PortWriter;

public class MCInteractive extends JavaPlugin{
    public PortWriter pWriter;
    public void onEnable(){
        Bukkit.getLogger().info("[MCInteractive] MCInteractive by ShortCircuit908");
        Bukkit.getPluginManager().registerEvents(new RedstoneListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerListener(this), this);
        saveDefaultConfig();
        try{
            pWriter = new PortWriter(getConfig().getString("port"), null);
        }
        catch(Exception e){
            e.printStackTrace();
        }
        Bukkit.getLogger().info("[MCInteractive] MCInteractive enabled");
    }
    public void onDisable(){
        pWriter.close();
    }
    public String getParam(String[] args, String argument){
        boolean collecting = false;
        argument = argument.toLowerCase();
        String output = "";
        for(String arg : args){
            if(collecting){
                if(!arg.contains(":")){
                    output += " " + arg;
                }
                else{
                    break;
                }
            }
            if(arg.toLowerCase().startsWith(argument + ":")){
                output = arg.toLowerCase().replace(argument + ":", "");
                collecting = true;
            }
        }
        return output;
    }
    @SuppressWarnings({"deprecation", "unchecked"})
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
        if(commandLabel.equalsIgnoreCase("breakout")){
            if(sender instanceof Player){
                Player player = (Player)sender;
                if(player.hasPermission("MCInteractive.Trigger.Create")){
                    if(args.length >= 2){
                        String triggerOn = getParam(args, "on");
                        String triggerOff = getParam(args, "off");
                        if(!triggerOn.equalsIgnoreCase("") && !triggerOff.equalsIgnoreCase("")){
                            Block block = player.getTargetBlock(null, 5);
                            if(block.getType() != Material.AIR){
                                player.sendMessage(ChatColor.AQUA + "Successfully set the trigger block!");
                                block.setMetadata("BreakoutOn", new EntityMetadata(this, triggerOn));
                                block.setMetadata("BreakoutOff", new EntityMetadata(this, triggerOff));
                            }
                            else{
                                player.sendMessage(ChatColor.RED + "No block in sight!");
                            }
                        }
                        else{
                            player.sendMessage(ChatColor.RED + "You must provide the parameters <on> and <off>");
                        }
                    }
                    else{
                        player.sendMessage(ChatColor.RED + "Insufficient arguments");
                    }
                }
                else{
                    player.sendMessage(ChatColor.RED + "Insufficient permissions");
                }
            }
            else if(sender instanceof CraftBlockCommandSender){
                CommandBlock block = (CommandBlock)((CraftBlockCommandSender)sender).getBlock().getState();
                block.setCommand("");
                block.update();
            }
            else{
                sender.sendMessage(ChatColor.RED + "This is a player-only command");
            }
            return true;
        }
        else if(commandLabel.equalsIgnoreCase("removetrigger")){
            if(sender instanceof Player){
                Player player = (Player)sender;
                if(player.hasPermission("MCInteractive.Trigger.Remove")){
                    Block block = player.getTargetBlock(null, 5);
                    if(block.getType() != Material.AIR){
                        if(block.hasMetadata("BreakoutOn") && block.hasMetadata("BreakoutOff")){
                            player.sendMessage(ChatColor.AQUA + "Successfully removed the trigger block!");
                            block.removeMetadata("BreakoutOn", this);
                            block.removeMetadata("BreakoutOff", this);
                        }
                        else{
                            player.sendMessage(ChatColor.RED + "The targeted block is not a trigger");
                        }
                    }
                    else{
                        player.sendMessage(ChatColor.RED + "No block in sight!");
                    }
                }
                else{
                    player.sendMessage(ChatColor.RED + "Insufficient permissions");
                }
            }
            else if(sender instanceof CraftBlockCommandSender){
                CommandBlock block = (CommandBlock)((CraftBlockCommandSender)sender).getBlock().getState();
                block.setCommand("");
                block.update();
            }
            else{
                sender.sendMessage(ChatColor.RED + "This is a player-only command");
            }
            return true;
        }
        else if(commandLabel.equalsIgnoreCase("serial")){
            if(sender.hasPermission("MCInteractive.Serial")){
                String action = getParam(args, "action");
                String port = getParam(args, "port").toUpperCase();
                if(action.equalsIgnoreCase("connect")){
                    if(!port.equalsIgnoreCase("")){
                        if(!port.equalsIgnoreCase(getConfig().getString("port"))){
                            pWriter.setSender(sender);
                            pWriter.close();
                            getConfig().set("port", port);
                            saveConfig();
                            pWriter = new PortWriter(port, sender);
                        }
                        else{
                            sender.sendMessage(ChatColor.AQUA + "Already connected to " + port);
                        }
                    }
                    else{
                        sender.sendMessage(ChatColor.RED + "You must provide the paramater <port>");
                    }
                }
                else if(action.equalsIgnoreCase("disconnect")){
                    getConfig().set("port", "");
                    saveConfig();
                    pWriter.close();
                }
                else if(action.equalsIgnoreCase("list")){
                    sender.sendMessage(ChatColor.AQUA + "[MCInteractive] Available serial ports:");
                    Enumeration<CommPortIdentifier> ports = CommPortIdentifier.getPortIdentifiers();
                    while(ports.hasMoreElements()){
                        CommPortIdentifier portID = ports.nextElement();
                        if(portID.getPortType() == CommPortIdentifier.PORT_SERIAL){
                            String add = ChatColor.GREEN + "Available";
                            if(portID.isCurrentlyOwned()){
                                add = ChatColor.RED + "In use (" + portID.getCurrentOwner() + ")";
                            }
                            sender.sendMessage(ChatColor.AQUA + portID.getName() + ": " + add);
                        }
                    }
                }
                else if(action.equalsIgnoreCase("")){
                    sender.sendMessage(ChatColor.RED + "You must provide the paramater <action>");
                }
                else{
                    sender.sendMessage(ChatColor.RED + "Invalid action. Possible paramaters are \"connect\" and \"disconnect\"");
                }
            }
            else{
                sender.sendMessage(ChatColor.RED + "Insufficient permissions");
            }
            return true;
        }
        else if(commandLabel.equalsIgnoreCase("trackplayer")){
            if(sender.hasPermission("MCInteractive.Trigger.Player")){
                String name = getParam(args, "player");
                if(!name.equalsIgnoreCase("")){
                    Player player = Bukkit.getPlayer(name);
                    if(player != null){
                        if(player.hasMetadata("isTracking")){
                            player.setMetadata("isTracking", new EntityMetadata(this, !player.getMetadata("isTracking").get(0).asBoolean()));
                        }
                        else{
                            player.setMetadata("isTracking", new EntityMetadata(this, true));
                        }
                        sender.sendMessage(ChatColor.AQUA + "Set tracking for " + ChatColor.YELLOW + player.getName() + ChatColor.AQUA + " to "
                                + ChatColor.YELLOW + player.getMetadata("isTracking").get(0).asBoolean());
                    }
                    else{
                        sender.sendMessage(ChatColor.LIGHT_PURPLE + name + ChatColor.RED + " is not online");
                    }
                }
                else{
                    sender.sendMessage(ChatColor.RED + "You must provide the paramater <player>");
                }
            }
            else{
                sender.sendMessage(ChatColor.RED + "Insufficient permissions");
            }
            return true;
        }
        return false;
    }
    public PortWriter getPortWriter(){
        return pWriter;
    }
}
