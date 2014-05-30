package me.RegalMachine.NeoChat;


import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.earth2me.essentials.Essentials;

public class NeoChat extends JavaPlugin implements CommandExecutor, Listener { 
	
	public static Essentials ess;
	
	private static CommandMap cmap;
	
	public static FileConfiguration fc;
	
	@Override
	public void onEnable() {
		
		if(!loadEssentials()){
			getServer().getPluginManager().disablePlugin(this);
		}
		
		this.saveDefaultConfig();
		
		fc= getConfig();
		
		generateWorldAutoToggleConfig();
		
		Bukkit.getLogger().info("Enabling NeoChat!");
		
		Set<String> chats = fc.getConfigurationSection("chat-channels").getKeys(false);
		
		for(String chatName: chats){
			
			String command = "";
			
			if(fc.contains("chat-channels." + chatName + ".command"))
				command = fc.getString("chat-channels." + chatName + ".command");
			else{
				Bukkit.getLogger().info(chatName + " not enabled, command not found in config!");
				continue;
			}
			//Check to see if the command is already registered.
			
			if(Chat.labels.contains(command)){
				System.err.print("Command " + command + " already registered! " + chatName + " Not Registered!");
				continue;
			}
			
			String prefix = "";
			if(fc.contains("chat-channels." + chatName + ".prefix")){
				prefix = fc.getString("chat-channels." + chatName + ".prefix");
			}
			
			String autoFormat = "&f";
			if(fc.contains("chat-channels." + chatName + ".auto-format")){
				autoFormat = fc.getString("chat-channels." + chatName + ".auto-format");
			}
			
			boolean hideNames = false;
			if(fc.contains("chat-channels." + chatName + ".show-names")){
				hideNames = !fc.getBoolean("chat-channels." + chatName + ".show-names");
			}
			
			Chat.newChat(chatName, prefix, command, autoFormat, hideNames);
			
		}
		
		//Reflection for registering all the commands
		try {
			
				final Field f = Bukkit.getServer().getClass().getDeclaredField("commandMap");
				f.setAccessible(true);
				cmap = (CommandMap)f.get(Bukkit.getServer()); 
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		for(String s: Chat.labels){
			MCCommand cmd = new MCCommand(s);
			cmap.register("", cmd);
			cmd.setExecutor(this); 
		}
		
		//add the listeners!
		getServer().getPluginManager().registerEvents(this, this);
		getServer().getLogger().info("[NeoChat] Sucessfully Enabled!");
	}
	
	
	private boolean loadEssentials() {
		ess = (Essentials) getServer().getPluginManager().getPlugin("Essentials");
		if(ess != null)
			return true;
		return false;
	}


	private void generateWorldAutoToggleConfig() {
		//Get the list of worlds and check in the Auto-Toggle section if each world is in the config, If a world is config, read in the chat toggle (set the auto toggle equal to the chat in that world if the chat exists, else
		//set the chat equal to normal) else create the config section for the auto toggle of that world, and set it equal to false.
		
		List<World> worlds = Bukkit.getWorlds();
		
		for(World world: worlds){
			String worldName = world.getName();
			UUID worldID = world.getUID();
			
			
			if(fc.contains("Auto-Toggle." + worldID.toString())){
				ConfigurationSection cs = fc.getConfigurationSection("Auto-Toggle." + worldID.toString());
				if(!cs.getString("Name").equals(world.getName())){
					cs.set("Name", world.getName());
					saveConfig();
				}
				if(cs.getBoolean("Toggle") == true){
					String chatName = cs.getString("Chat");
					Chat.autoToggleWorldnameName.put(world, chatName);
				}
				
			}else{
				fc.createSection("Auto-Toggle." + worldID.toString());
				fc.addDefault("Auto-Toggle." + worldID.toString() + ".Name", worldName);
				fc.addDefault("Auto-Toggle." + worldID.toString() + ".Toggle", false);
				fc.addDefault("Auto-Toggle." + worldID.toString() + ".Chat", "none");
				fc.options().copyDefaults(true);
				saveConfig();
			}
		}
	}


	@Override
	public void onDisable() {
		Chat.autoToggleWorldnameName.clear();
		Chat.chatsWithNoNameShown.clear();
		Chat.labels.clear();
		Chat.nameFormat.clear();
		Chat.nameLabel.clear();
		Chat.namePrefix.clear();
		MultiChatCommandHandler.toggledPlayers.clear();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(command.isRegistered() && Chat.labels.contains(label)){
			if(sender.hasPermission("neochat." + Chat.getNameFromLabel(label) + ".use") && !ess.getUser((Player) sender).getMuted())
				MultiChatCommandHandler.runCommand(sender, command, label, args);
			else{
				if(ess.getUser((Player) sender).getMuted())
					sender.sendMessage(ChatColor.GRAY + "[NeoChat]" + ChatColor.RED + "You are currently muted!");
				else
					sender.sendMessage(ChatColor.GRAY + "[NeoChat]" + ChatColor.RED + "You dont permission to use chat: " + Chat.getNameFromLabel(label));
			}
			return true;
		}
		
		if(label.equalsIgnoreCase("format")){
			if(sender.hasPermission("neochat.command.format"))
				sender.sendMessage(ChatColor.GRAY + "[NeoChat]" + ChatColor.RESET + "l-" + ChatColor.BOLD + "Bold" + ChatColor.RESET + " o-" + ChatColor.ITALIC + "Slant" + ChatColor.RESET + " n-" + ChatColor.UNDERLINE + "ULine" + ChatColor.RESET + " m-" + ChatColor.STRIKETHROUGH + "STRIKE" + ChatColor.RESET + " r-reset k-" + ChatColor.MAGIC + "Magic");
			else
				sender.sendMessage(ChatColor.GRAY + "[NeoChat]" + ChatColor.RED + "You dont have permission for /format.");
			return true;
		}									//Collin join mumble!!!!
		
		if(label.equalsIgnoreCase("color")){
			if(sender.hasPermission("neochat.command.color"))
			sender.sendMessage(ChatColor.GRAY + "[NeoChat]" + ChatColor.DARK_BLUE + "1" + ChatColor.DARK_GREEN  + "2" + ChatColor.DARK_AQUA + "3" + ChatColor.DARK_RED + "4" + ChatColor.DARK_PURPLE + "5" + ChatColor.GOLD  + "6" + ChatColor.GRAY + "7" + ChatColor.DARK_GRAY + "8" + ChatColor.BLUE + "9" + ChatColor.BLACK + "0" + ChatColor.GREEN +"a" + 
		ChatColor.AQUA + "b" + ChatColor.RED + "c" + ChatColor.LIGHT_PURPLE + "d" + ChatColor.YELLOW + "e" + ChatColor.WHITE + "f");
			return true;
		}
		
		if(label.equalsIgnoreCase("chats")){
			if(sender.hasPermission("neochat.command.chats")){
				sender.sendMessage(ChatColor.GRAY + "---NeoChat---");
				if(sender.hasPermission("neochat.command.color"))
				sender.sendMessage("/color - Prints the color codes to your screen.");
				if(sender.hasPermission("neochat.command.format"))
				sender.sendMessage("/format - Prints the format codes to your screen.");
				for(String s: Chat.labels){
					if(sender.hasPermission("neochat." + Chat.getNameFromLabel(s) + ".use")){
						sender.sendMessage("/" + s + " - Interaction with the chat: " + Chat.getNameFromLabel(s) + ".");
					}
				}
			}else{
				sender.sendMessage(ChatColor.GRAY + "[NeoChat]" + ChatColor.RED + "You don't have permission to use /neochat");
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
	
	//On Player Join - Check to see if the world they are in has an auto toggle, and toggle accordingly.
	@EventHandler
	public void onPlayerLogin(PlayerJoinEvent e){
		Player player = e.getPlayer();
		if(!player.hasPermission("neochat.exempttoggle")){
			if(Chat.autoToggleWorldnameName.containsKey(player.getWorld())){
				MultiChatCommandHandler.toggledPlayers.put(player, Chat.autoToggleWorldnameName.get(player.getWorld()));
			}
		}
	}
	
	
	//On Player Chat
	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent e){
		//Do nothing if the event is canceled
		if(!e.isCancelled()){
			//Do nothing if the players value in players is "None"
			if(MultiChatCommandHandler.toggledPlayers.containsKey(e.getPlayer())){
				//If it is, send the message to the method in MultiChatCommandHandler and cancel the event
				if(!ess.getUser(e.getPlayer()).getMuted())
					MultiChatCommandHandler.playerChatEvent(e.getPlayer(), e.getMessage());
				else
					e.getPlayer().sendMessage(ChatColor.GRAY + "[NeoChat] Stop talking while muted!");
				e.setCancelled(true);
			}
		}
	}
	
	//On World Switch
	@EventHandler
	public void onWorldSwitch(PlayerChangedWorldEvent e){
		
		World newWorld = e.getPlayer().getWorld();
		
		if(Chat.autoToggleWorldnameName.containsKey(e.getFrom()) && !e.getPlayer().hasPermission("neochat.exempttoggle")){
			//Remove player from chat unless permission says otherwise.
			MultiChatCommandHandler.toggledPlayers.remove(e.getPlayer());
		}
		
		if(Chat.autoToggleWorldnameName.containsKey(newWorld) && !e.getPlayer().hasPermission("neochat.exempttoggle")){
			MultiChatCommandHandler.toggledPlayers.put(e.getPlayer(), Chat.autoToggleWorldnameName.get(newWorld));
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
        message = message.replaceAll("&o", ChatColor.ITALIC + "");
        message = message.replaceAll("&n", ChatColor.UNDERLINE + "");
        message = message.replaceAll("&l", ChatColor.BOLD + "");
        message = message.replaceAll("&r", ChatColor.RESET + "");
        message = message.replaceAll("&m", ChatColor.STRIKETHROUGH + "");
        return message;
	}
}
	