package me.RegalMachine.NeoChat;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.World;

public class Chat {
	
	public static Map<String, String> namePrefix = new HashMap<>();
	public static Map<String, String> nameLabel = new HashMap<>();
	
	public static Map<String, String> nameFormat = new HashMap<>();
	
	public static List<String> labels = new ArrayList<String>();
	public static List<String> chatsWithNoNameShown = new ArrayList<String>();
	
	public static Map<World, String> autoToggleWorldnameName = new HashMap<>();
	

	public static void newChat(String name, String prefix, String label, String color, boolean hideNames){
		String message = "New chat: " + name;
		namePrefix.put(name, prefix);
		message = message + " registered with prefix: " + prefix; 
		nameLabel.put(name, label);
		message = message + " with command: " + label;
		nameFormat.put(name, color);
		message = message + " with default format: " + color;
		labels.add(label);
		if(hideNames){
			chatsWithNoNameShown.add(name);
			message = message + " with names not shown";
		}
		Bukkit.getServer().getLogger().info("[NeoChat]" + message);
		
	}
	
	public static String getNameFromLabel(String label){
		String name = "";
		
		for(Map.Entry<String, String> entry: nameLabel.entrySet()){
			if((label.equals(null) && entry.getValue() == null) || (!label.equals(null) && label.equals(entry.getValue()))){
				name = entry.getKey();
				break;
			}
		}
		return name;
	}
	
	
	
}
