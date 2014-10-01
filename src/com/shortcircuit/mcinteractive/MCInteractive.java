package com.shortcircuit.mcinteractive;

import gnu.io.CommPortIdentifier;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;

import net.minecraft.util.org.apache.commons.lang3.StringEscapeUtils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CommandBlock;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.craftbukkit.v1_7_R3.command.CraftBlockCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.shortcircuit.mcinteractive.listeners.MCIEventListener;
import com.shortcircuit.mcinteractive.listeners.PlayerListener;
import com.shortcircuit.mcinteractive.listeners.RedstoneListener;
import com.shortcircuit.mcinteractive.listeners.TrackingManager;
import com.shortcircuit.mcinteractive.serial.SerialManager;

public class MCInteractive extends JavaPlugin{
    protected SerialManager serial_manager;
    protected TrackingManager tracking_manager;
    
    public void onEnable(){
        ConfigurationSerialization.registerClass(MCIBlock.class);
        ConfigurationSerialization.registerClass(MCIPlayer.class);
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
        Bukkit.getLogger().info("[MCInteractive] MCInteractive by ShortCircuit908");
        Bukkit.getPluginManager().registerEvents(new MCIEventListener(), this);
        Bukkit.getPluginManager().registerEvents(new RedstoneListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerListener(this), this);
        saveDefaultConfig();
        serial_manager = new SerialManager(StringEscapeUtils.unescapeJava(getConfig().getString("delimiter")));
        tracking_manager = new TrackingManager(getDataFolder() + "");
        if(!getConfig().getString("port").equals("")) {
            serial_manager.connect(getConfig().getString("port"), getConfig().getInt("baud"));
        }
        Bukkit.getLogger().info("[MCInteractive] MCInteractive enabled");
    }
    public void onDisable(){
        serial_manager.disconnectSilently();
        tracking_manager.close();
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
                        if(!triggerOn.isEmpty() && !triggerOff.isEmpty()){
                            Block block = player.getTargetBlock(null, 5);
                            if(block.getType() != Material.AIR){
                                tracking_manager.addTracking(block, triggerOn, triggerOff);
                                player.sendMessage(ChatColor.AQUA + "Successfully set the trigger block!");
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
                            if(tracking_manager.isTracking(block)) {
                                tracking_manager.removeTracking(block);
                            }
                            player.sendMessage(ChatColor.AQUA + "Successfully removed the trigger block!");
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
                if(args.length >= 1){
                    Player player = Bukkit.getPlayer(args[0]);
                    if(player != null){
                        if(tracking_manager.isTracking(player)) {
                            tracking_manager.removeTracking(player);
                        }
                        else {
                            tracking_manager.addTracking(player);
                        }
                        sender.sendMessage(ChatColor.AQUA + "Set tracking for " + ChatColor.YELLOW
                                + player.getName() + ChatColor.AQUA + " to "
                                + ChatColor.YELLOW + tracking_manager.isTracking(player));
                    }
                    else{
                        sender.sendMessage(ChatColor.LIGHT_PURPLE + args[0] + ChatColor.RED + " is not online");
                    }
                }
                else{
                    sender.sendMessage(ChatColor.RED + "You must specify a player");
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
    public TrackingManager getTrackingManager() {
        return tracking_manager;
    }
    protected String getParam(String[] args, String argument){
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
