package com.shortcircuit.mcinteractive;

import gnu.io.CommPortIdentifier;

import java.io.File;
import java.io.IOException;
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
import com.shortcircuit.mcinteractive.listeners.ThingeyListener;
import com.shortcircuit.mcinteractive.serial.SerialManager;

public class MCInteractive extends JavaPlugin{
    private SerialManager serial_manager;
    public void onLoad(){

    }
    public void onEnable(){
        if(!verifyRXTX()) {
            Bukkit.getLogger().severe("[MCInteractive] Cannot proceed with enabling\n"
                    + "\tRXTX library not found\n"
                    + "\tThe library may be downloaded at mfizz.com/oss/rxtx-for-java\n"
                    + "\tDisabling...");
            setEnabled(false);
            return;
        }
        else {
            Bukkit.getLogger().info("[MCInteractive] RXTX library located");
        }
        Bukkit.getPluginManager().registerEvents(new ThingeyListener(), this);
        Bukkit.getLogger().info("[MCInteractive] MCInteractive by ShortCircuit908");
        Bukkit.getPluginManager().registerEvents(new RedstoneListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerListener(this), this);
        saveDefaultConfig();
        serial_manager = new SerialManager();
        if(!getConfig().getString("port").equals("")) {
            serial_manager.connect(getConfig().getString("port"), getConfig().getInt("baud"));
        }
        Bukkit.getLogger().info("[MCInteractive] MCInteractive enabled");
    }
    public void onDisable(){
        serial_manager.disconnectSilently();
        Bukkit.getLogger().info("[MCInteractive] MCInteractive disabled");
    }
    
    /*
     * TODO: Make this pretty
     */
    @SuppressWarnings({"unchecked", "deprecation"})
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
        if(commandLabel.equalsIgnoreCase("serial")) {
            if(args.length == 0) {
                return getServer().dispatchCommand(sender, "help serial");
            }
            if(args[0].equalsIgnoreCase("connect")) {
                if(sender.hasPermission("mcinteractive.serial")) {
                    if(args.length >= 2) {
                        getConfig().set("port", args[1].toUpperCase());
                    }
                    else {
                        return getServer().dispatchCommand(sender, "help serial");
                    }
                    if(args.length >= 3) {
                        getConfig().set("baud", Integer.parseInt(args[2]));
                    }
                    saveConfig();
                    int baud = getConfig().getInt("baud");
                    serial_manager.connect(args[1].toUpperCase(), baud);
                }
                else {
                    sender.sendMessage(ChatColor.RED + "Insufficient permissions");
                }
            }
            else if(args[0].equalsIgnoreCase("disconnect")) {
                if(sender.hasPermission("mcinteractive.serial")) {
                    getConfig().set("port", "");
                    serial_manager.disconnectSilently();
                    saveConfig();
                }
                else {
                    sender.sendMessage(ChatColor.RED + "Insufficient permissions");
                }
            }
            else if(args[0].equalsIgnoreCase("list")) {
                if(sender.hasPermission("mcinteractive.serial.list")) {
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
                else {
                    sender.sendMessage(ChatColor.RED + "Insufficient permissions");
                }
            }
            else if(args[0].equalsIgnoreCase("sendmessage")) {
                if(sender.hasPermission("mcinteractive.serial.sendmessage")) {
                    if(args.length >= 2) {
                        String message = args[1];
                        if(args.length >= 3) {
                            for(int i = 2; i < args.length; i++) {
                                message += " " + args[i];
                            }
                        }
                        try {
                            serial_manager.write(message);
                        }
                        catch(IOException e) {
                            e.printStackTrace();
                        }
                    }
                    else {
                        return getServer().dispatchCommand(sender, "help serial");
                    }
                }
                else {
                    sender.sendMessage(ChatColor.RED + "Insufficient permissions");
                }
            }
            else {
                return getServer().dispatchCommand(sender, "help serial");
            }
            return true;
        }
        else if(commandLabel.equalsIgnoreCase("breakout")) {
            if(sender instanceof Player){
                Player player = (Player)sender;
                if(player.hasPermission("mcinteractive.trigger.create")){
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
        else if(commandLabel.equalsIgnoreCase("removetrigger")) {
            if(sender instanceof Player){
                Player player = (Player)sender;
                if(player.hasPermission("mcinteractive.trigger.remove")){
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
        else if(commandLabel.equalsIgnoreCase("trackplayer")) {
            if(sender.hasPermission("mcinteractive.trigger.player")){
                String name = getParam(args, "player");
                if(!name.equalsIgnoreCase("")){
                    Player player = Bukkit.getPlayer(name);
                    if(player != null){
                        if(player.hasMetadata("isTracking")){
                            player.setMetadata("isTracking", new EntityMetadata(this,
                                    !player.getMetadata("isTracking").get(0).asBoolean()));
                        }
                        else{
                            player.setMetadata("isTracking", new EntityMetadata(this, true));
                        }
                        sender.sendMessage(ChatColor.AQUA + "Set tracking for " + ChatColor.YELLOW
                                + player.getName() + ChatColor.AQUA + " to "
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
    public boolean verifyRXTX() {
        String java_home = System.getProperty("java.home");
        File rxtx_jar = new File(java_home + "/lib/ext/RXTXcomm.jar");
        File rxtx_parallel = new File(java_home + "/bin/rxtxParallel.dll");
        File rxtx_serial = new File(java_home + "/bin/rxtxSerial.dll");
        return (rxtx_jar.exists() && rxtx_parallel.exists() && rxtx_serial.exists());
    }
    public SerialManager getSerialManager() {
        return serial_manager;
    }
    private String getParam(String[] args, String argument){
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
}
