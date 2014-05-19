package com.shortcircuit.mcinteractive;

import java.io.File;

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

import com.shortcircuit.mcinteractive.listeners.RedstoneListener;
import com.shortcircuit.mcinteractive.serial.PortWriter;

public class MCInteractive extends JavaPlugin{
    public PortWriter pWriter;
    public void onEnable(){
        File file = new File("config.yml");
        if(!file.exists()){
            this.saveDefaultConfig();
        }
        try{
            pWriter = new PortWriter(this.getConfig().getString("port"));
        }
        catch(Exception e){
            e.printStackTrace();
        }
        Bukkit.getPluginManager().registerEvents(new RedstoneListener(this, this), this);
    }
    public void onDisable(){
        pWriter.close();
    }
    public String getParam(String[] args, String argument){
        boolean collecting = false;
        argument = argument.toLowerCase();
        String output = "";
        for(String arg : args){
            arg = arg.toLowerCase();
            if(collecting){
                if(!arg.contains(":")){
                    output += " " + arg;
                }
                else{
                    break;
                }
            }
            if(arg.startsWith(argument + ":")){
                output = arg.replace(argument + ":", "");
                collecting = true;
            }
        }
        return output;
    }
    @SuppressWarnings("deprecation")
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
        if(commandLabel.equalsIgnoreCase("breakout")){
            if(sender instanceof Player){
                Player player = (Player)sender;
                if(args.length > 0){
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
                        sender.sendMessage(ChatColor.RED + "You must provide the paramaters <on> and <off>");
                    }
                }
                else{
                    sender.sendMessage(ChatColor.RED + "Insufficient arguments");
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
        }
        return true;
    }
}
