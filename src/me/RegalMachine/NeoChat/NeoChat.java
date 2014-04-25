package me.RegalMachine.NeoChat;


import java.lang.reflect.Field;
import java.util.List;
import java.util.Scanner;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_7_R1.CraftServer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
//import org.bukkit.event.player.PlayerJoinEvent;
//import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class NeoChat extends JavaPlugin implements CommandExecutor, Listener { 
	
	private static CommandMap cmap;
	
	@Override
	public void onEnable() {
		// TODO Auto-generated method stub
		this.saveDefaultConfig();
		List<String> chats = getConfig().getStringList("chats");
		for(String s: chats){
			Scanner scan = new Scanner(s);
			String chatname = scan.next();
			String chatpref = scan.next();
			String chatcmd = scan.next();
			String chatCustColor = null;
			boolean chatHasColor = false;
			if(scan.hasNext()){
				chatCustColor = scan.next();
				chatHasColor = true;
			}
			
			if(chatHasColor){
				Chat.newChat(chatname, chatpref, chatcmd, fixFormat(fixColor(chatCustColor)));
				getServer().getLogger().info("[NeoChat] New Chat " + chatname + " Registered with Command " + chatcmd + "with formatting " + chatCustColor);
			}else{
				Chat.newChat(chatname, chatpref, chatcmd);
				getServer().getLogger().info("[NeoChat] New Chat " + chatname + " Registered with Command " + chatcmd);
			}
			scan.close();
		}
		
		
		//Reflection for registering all the commands
		try {
			if(Bukkit.getServer() instanceof CraftServer){
				final Field f = CraftServer.class.getDeclaredField("commandMap");
				f.setAccessible(true);
				cmap = (CommandMap)f.get(Bukkit.getServer()); 
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		for(String s: Chat.labels){
			MCCommand cmd = new MCCommand(s);
			cmap.register("MultiChat", cmd);
			cmd.setExecutor(this);
		}
		
		//add the listeners!
		getServer().getPluginManager().registerEvents(this, this);
		getServer().getLogger().info("[NeoChat] Sucessfully Enabled!");
	}
	
	
	@Override
	public void onDisable() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		// TODO Auto-generated method stub
		if(command.isRegistered() && Chat.labels.contains(label)){
			MultiChatCommandHandler.runCommand(sender, command, label, args);
			return true;
		}
		
		if(label.equalsIgnoreCase("format")){
			sender.sendMessage(ChatColor.GRAY + "[NeoChat]" + ChatColor.RESET + "l-" + ChatColor.BOLD + "Bold" + ChatColor.RESET + " o-" + ChatColor.ITALIC + "Slant" + ChatColor.RESET + " n-" + ChatColor.UNDERLINE + "ULine" + ChatColor.RESET + " m-" + ChatColor.STRIKETHROUGH + "STRIKE" + ChatColor.RESET + " r-reset k-" + ChatColor.MAGIC + "Magic");
			return true;
		}
		
		if(label.equalsIgnoreCase("color")){
			sender.sendMessage(ChatColor.DARK_BLUE + "1" + ChatColor.DARK_GREEN  + "2" + ChatColor.DARK_AQUA + "3" + ChatColor.DARK_RED + "4" + ChatColor.DARK_PURPLE + "5" + ChatColor.GOLD  + "6" + ChatColor.GRAY + "7" + ChatColor.DARK_GRAY + "8" + ChatColor.BLUE + "9" + ChatColor.BLACK + "0" + ChatColor.GREEN +"a" + 
		ChatColor.AQUA + "b" + ChatColor.RED + "c" + ChatColor.LIGHT_PURPLE + "d" + ChatColor.YELLOW + "e" + ChatColor.WHITE + "f");
			return true;
		}
		
		if(label.equalsIgnoreCase("neochat")){
			sender.sendMessage(ChatColor.GRAY + "---NeoChat---");
			sender.sendMessage("/color - Prints the color codes to your screen.");
			sender.sendMessage("/format - Prints the format codes to your screen.");
			for(String s: Chat.labels){
				if(sender.hasPermission("neochat." + Chat.getNameFromLabel(s) + ".use")){
					sender.sendMessage("/" + s + " - Interaction with the chat: " + Chat.getNameFromLabel(s) + ".");
				}
			}
			return true;
		}
		
		
		
		
		
		return false;
	}
	
	//On Player Logout - Remove them from the players
	@EventHandler
	public void onPlayerLogout(PlayerQuitEvent e){
		Player p = e.getPlayer();
		if(MultiChatCommandHandler.toggledPlayers.containsKey(p))
			MultiChatCommandHandler.toggledPlayers.remove(p);
	}
	
	
	//On Player Chat
	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent e){
		//Do nothing if the event is canceled
		if(!e.isCancelled()){
			//Do nothing if the players value in players is "None"
			if(MultiChatCommandHandler.toggledPlayers.containsKey(e.getPlayer())){
				//If it is, send the message to the method in MultiChatCommandHandler and cancel the event
				MultiChatCommandHandler.playerChatEvent(e.getPlayer(), e.getMessage());
				e.setCancelled(true);
			}
		}
	}


		public static String fixColor(String s){
        String message = s;

        message = message.replaceAll("&0", ChatColor.BLACK + "");
        message = message.replaceAll("&1", ChatColor.DARK_BLUE + "");
        message = message.replaceAll("&2", ChatColor.DARK_GREEN + "");
        message = message.replaceAll("&3", ChatColor.DARK_AQUA + "");
        message = message.replaceAll("&4", ChatColor.DARK_RED + "");
        message = message.replaceAll("&5", ChatColor.DARK_PURPLE + "");
        message = message.replaceAll("&6", ChatColor.GOLD + "");
        message = message.replaceAll("&7", ChatColor.GRAY + "");
        message = message.replaceAll("&8", ChatColor.DARK_GRAY + "");
        message = message.replaceAll("&9", ChatColor.BLUE + "");
        message = message.replaceAll("&a", ChatColor.GREEN + "");
        message = message.replaceAll("&b", ChatColor.AQUA + "");
        message = message.replaceAll("&c", ChatColor.RED + "");
        message = message.replaceAll("&d", ChatColor.LIGHT_PURPLE + "");
        message = message.replaceAll("&e", ChatColor.YELLOW + "");
        message = message.replaceAll("&f", ChatColor.WHITE + "");
        message = message.replaceAll("&r", ChatColor.RESET + "");

        return message;
    }
	
	public static String fixFormat(String s){
		String message = s;
		message = message.replaceAll("&k", ChatColor.MAGIC + "");
        //message = message.replaceAll("&m", ChatColor.MAGIC + "");
        message = message.replaceAll("&o", ChatColor.ITALIC + "");
        message = message.replaceAll("&n", ChatColor.UNDERLINE + "");
        message = message.replaceAll("&l", ChatColor.BOLD + "");
        message = message.replaceAll("&r", ChatColor.RESET + "");
        message = message.replaceAll("&m", ChatColor.STRIKETHROUGH + "");
        return message;
	}
}
	