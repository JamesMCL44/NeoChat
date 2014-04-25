package me.RegalMachine.NeoChat;


import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
//import org.bukkit.event.player.AsyncPlayerChatEvent;

public class MultiChatCommandHandler {
	
	public static Map<Player, String> toggledPlayers = new HashMap<>();
	//String is the chatName
	
	public static void runCommand(CommandSender sender, Command command, String label, String[] args) {
		// TODO Auto-generated method stub
		
		
		Player player = Bukkit.getPlayer(sender.getName());
		//Get the chat we are currently toggled into, 'None' if none
		String chatCurrentlyToggledInto = "None";
		if(toggledPlayers.containsKey(player)){
			chatCurrentlyToggledInto = toggledPlayers.get(player);
		}
		
		String chatName = Chat.getNameFromLabel(label);
		
		if(args.length == 0){
			//Toggle the player
			if(player.hasPermission("neochat." + chatName + ".toggle")){
				togglePlayer(sender, chatName);
			}
		}else{
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
					if(Chat.hasDefaultColor(chatName)){
						playerMessage = Chat.getChatColor(chatName) + playerMessage;
					}
					String finalMessage = NeoChat.fixColor(NeoChat.fixFormat(Chat.namePrefix.get(chatName))) + player.getDisplayName() +": "+ playerMessage;
					
					//Broadcast to everyone in that chat
					for(Player p: Bukkit.getOnlinePlayers()){
						if(p.hasPermission("neochat." + chatName + ".use")){
							p.sendMessage(finalMessage);
						}
					}
				}
			}else{
				//Player is in a chat
				if(chatName.equalsIgnoreCase(chatCurrentlyToggledInto)){ //Is the chat we are sending to equal to the chat we are currently in?
					//print to everyone
					playerMessage = player.getDisplayName() + ":" + NeoChat.fixColor(NeoChat.fixFormat(playerMessage));
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
						if(Chat.hasDefaultColor(chatName)){
							playerMessage = Chat.getChatColor(chatName) + playerMessage;
						}
						String finalMessage = NeoChat.fixColor(NeoChat.fixFormat(Chat.namePrefix.get(chatName))) + player.getDisplayName()+":" + playerMessage;
						
						//Broadcast to everyone in that chat
						for(Player p: Bukkit.getOnlinePlayers()){
							if(p.hasPermission("neochat." + chatName + ".use")){
								p.sendMessage(finalMessage);
							}
						}
					}
					
				}
				
			}
			
			
			
		}
		
		
		
		
	}
	
	
	
	
	
	
	//Unused
	public static void togglePlayer(CommandSender toggler, Player togglee, String chatName){
		//insert logic for one player to toggle another player
		
	}
	
	public static void togglePlayer(CommandSender toggling, String chatName){
		//Insert logic to toggle player
		
		//If the player is in the hashmap and their string matches the one in the hashmap
		//	Remove them from the hashmap
		//if the player is in the hashmap and the string is NOT the same as the one in the hashmap
		//  Remove them from the hashmap and re-add them under the current chat
		//if the player is in no hashmap
		//  Add the player to said hashmap
		
		if(toggledPlayers.containsKey((Player)toggling)){
			if(toggledPlayers.get((Player)toggling).equalsIgnoreCase(chatName)){
				toggledPlayers.remove((Player)toggling);
				toggling.sendMessage("You are now in normal chat");
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
		// TODO Auto-generated method stub
		String chatName = toggledPlayers.get(player);
		String prefix = Chat.namePrefix.get(chatName);
		String fixedMessage = NeoChat.fixColor(NeoChat.fixFormat(message));
		
		if(Chat.hasDefaultColor(chatName))
			fixedMessage = Chat.getChatColor(chatName) + fixedMessage;
		
		
		String finalMessage = NeoChat.fixColor(NeoChat.fixFormat(prefix)) + ChatColor.RESET + player.getDisplayName() + ChatColor.RESET + ": " + fixedMessage;
		
		for(Player p: Bukkit.getOnlinePlayers()){
			if(p.hasPermission("neochat." + chatName + ".use"))
				p.sendMessage(finalMessage);
		}
	}
}
