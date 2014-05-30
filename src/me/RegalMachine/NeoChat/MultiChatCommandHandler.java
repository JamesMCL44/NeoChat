package me.RegalMachine.NeoChat;


import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
//import org.bukkit.event.player.AsyncPlayerChatEvent;


import com.earth2me.essentials.User;

public class MultiChatCommandHandler {
	
	public static Map<Player, String> toggledPlayers = new HashMap<>();
	//String is the chatName
	
	@SuppressWarnings("deprecation")
	public static void runCommand(CommandSender sender, Command command, String label, String[] args) {
		
		Player player = Bukkit.getPlayer(sender.getName());
		User user = NeoChat.ess.getUser(player);
		
		//Get the chat we are currently toggled into, 'None' if none
		String chatCurrentlyToggledInto = "None";
		if(toggledPlayers.containsKey(player)){
			chatCurrentlyToggledInto = toggledPlayers.get(player);
		}
		if(!user.getMuted()){
			String chatName = Chat.getNameFromLabel(label);
			
			if(args.length == 0){
				//Toggle the player
				if(player.hasPermission("neochat." + chatName + ".toggle")){
					togglePlayer(sender, chatName);
				}
			}else{
				
				if(sender.hasPermission("neochat.quickMessage")){
				
				//if the player is not in a chat, then send the message directly to the chat
				//if the player is in a chat, and the command isnt the same one for the chat currently in, send the message to that other chat
				//if the player is in a chat, and the command is the same one as for the chat the player is currently in, chat to everyone
				
				//Compile the message, do NOT fix the format or color and do NOT add prefix. Prefix and Format
				String playerMessage = "";
				for(String s: args){
					playerMessage = playerMessage + " " + s;
				}
				
				if(chatCurrentlyToggledInto.equalsIgnoreCase("none")){
					//Player is not in any chat, so simply send the message to everyone in that chat
					if(player.hasPermission("neochat." + chatName + ".use")){
						//fix color and format
						if(player.hasPermission("neochat." + chatName  + ".color")){
							playerMessage = NeoChat.fixColor(playerMessage);
						}
						if(player.hasPermission("neochat." + chatName + ".format")){
							playerMessage = NeoChat.fixFormat(playerMessage);
						}
							playerMessage = NeoChat.fixColor(NeoChat.fixFormat(Chat.nameFormat.get(chatName))) + playerMessage;
							
						String finalMessage = NeoChat.fixColor(NeoChat.fixFormat(Chat.namePrefix.get(chatName)));
						if(!Chat.chatsWithNoNameShown.contains(chatName))
							finalMessage = finalMessage + player.getDisplayName() + ":";
						finalMessage = finalMessage + playerMessage;
						
						//Broadcast to everyone in that chat
						for(Player p: Bukkit.getOnlinePlayers()){
							if(p.hasPermission("neochat." + chatName + ".use") || p.hasPermission("neochat." + chatName + ".see")){
								p.sendMessage(finalMessage);
							}
						}
					}else{
						player.sendMessage(ChatColor.GRAY + "[NeoChat]" + ChatColor.RED + "You dont have permission to use /" + label);
					}
				}else{
					//Player is in a chat
					if(chatName.equalsIgnoreCase(chatCurrentlyToggledInto)){ //Is the chat we are sending to equal to the chat we are currently in?
						//print to everyone
						if(player.hasPermission("essentials.chat.color"))
							playerMessage = NeoChat.fixColor(playerMessage);
						if(player.hasPermission("essentials.chat.format"))
							playerMessage = NeoChat.fixFormat(playerMessage);
						playerMessage = player.getDisplayName() + ":" + playerMessage;
						for(Player p: Bukkit.getOnlinePlayers()){
							p.sendMessage(playerMessage);
						}
					}else{
						//print to people in other chat
						if(player.hasPermission("neochat." + chatName + ".use")){
							if(player.hasPermission("neochat." + chatName  + ".color")){
								playerMessage = NeoChat.fixColor(playerMessage);
							}
							if(player.hasPermission("neochat." + chatName + ".format")){
								playerMessage = NeoChat.fixFormat(playerMessage);
							}
							
								playerMessage = NeoChat.fixColor(NeoChat.fixFormat(Chat.nameFormat.get(chatName))) + playerMessage;
							
							String finalMessage = NeoChat.fixColor(NeoChat.fixFormat(Chat.namePrefix.get(chatName)));
							
							if(!Chat.chatsWithNoNameShown.contains(chatName))
								finalMessage = finalMessage + player.getDisplayName() + ":";
							finalMessage = finalMessage + playerMessage;
							
							//Broadcast to everyone in that chat
							for(Player p: Bukkit.getOnlinePlayers()){
								if(p.hasPermission("neochat." + chatName + ".use")  || p.hasPermission("neochat." + chatName + ".see")){
									p.sendMessage(finalMessage);
								}
							}
						}	
					}	
				}
				}else{
					sender.sendMessage(ChatColor.GRAY + "[NeoChat]" + ChatColor.RED  + "You aren't allowed to use QuickMessage!");
				}
			}
		}else{
			sender.sendMessage(ChatColor.GRAY + "[NeoChat]" + ChatColor.RED + "Stop trying to talk while muted!");
		}
	}
	
	public static void togglePlayer(CommandSender toggling, String chatName){
		//Insert logic to toggle player
		
		//If the player is in the hashmap and their string matches the one in the hashmap
		//	Remove them from the hashmap
		//if the player is in the hashmap and the string is NOT the same as the one in the hashmap
		//  Remove them from the hashmap and re-add them under the current chat
		//if the player is in no hashmap
		//  Add the player to said hashmap
		
		Player player = (Player) toggling;
		if(toggledPlayers.containsKey((Player)toggling)){
			if(toggledPlayers.get((Player)toggling).equalsIgnoreCase(chatName)){
				toggledPlayers.remove((Player)toggling);
				
				if(!player.hasPermission("neochat.exempttoggle") && Chat.autoToggleWorldnameName.containsKey(player.getWorld())){
					toggledPlayers.put(player, Chat.autoToggleWorldnameName.get(player.getWorld()));
					toggling.sendMessage("You are now in " + Chat.autoToggleWorldnameName.get(player.getWorld()));
				}else{
					player.sendMessage("You are now in normal chat.");
				}
				
			}else{
				toggledPlayers.remove((Player)toggling);
				toggledPlayers.put((Player)toggling, chatName);
				toggling.sendMessage("You are now in " + chatName);
			}
		}else{
			toggledPlayers.put((Player)toggling, chatName);
			toggling.sendMessage("You are now in " + chatName);
		}
		
		
	}
	

	public static void playerChatEvent(Player player, String message) {
		
		String chatName = toggledPlayers.get(player);
		String prefix = Chat.namePrefix.get(chatName);
		String format = Chat.nameFormat.get(chatName);
		String cmessage = message;
		boolean hideNames = Chat.chatsWithNoNameShown.contains(chatName);
		
		String finalMessage = NeoChat.fixColor(NeoChat.fixFormat(prefix));
		
		if(!hideNames)
			finalMessage = finalMessage + player.getDisplayName() + ": ";
		else
			finalMessage = finalMessage + " ";
		
		if(player.hasPermission("neochat." + chatName + ".color"))
			cmessage = NeoChat.fixColor(cmessage);
		
		if(player.hasPermission("neochat." + chatName + ".format"))
			cmessage = NeoChat.fixFormat(cmessage);
		
		finalMessage = finalMessage + NeoChat.fixColor(NeoChat.fixFormat(format)) + cmessage;
		
		for(Player p: Bukkit.getOnlinePlayers()){
			if(p.hasPermission("neochat." + chatName + ".use") || p.hasPermission("neochat." + chatName + ".see"))
				p.sendMessage(finalMessage);
		}
	}
}
