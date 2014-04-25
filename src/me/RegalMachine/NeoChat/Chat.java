package me.RegalMachine.NeoChat;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Chat {
	
	public static Map<String, String> namePrefix = new HashMap<>();
	public static Map<String, String> nameLabel = new HashMap<>();
	
	public static Map<String, String> nameColor = new HashMap<>();
	
	public static List<String> labels = new ArrayList<String>();
	
	public static void newChat(String name, String prefix, String label){
		namePrefix.put(name, prefix);
		nameLabel.put(name, label);
		labels.add(label);
	}
	public static void newChat(String name, String prefix, String label, String color){
		namePrefix.put(name, prefix);
		nameLabel.put(name, label);
		nameColor.put(name, color);
		labels.add(label);
		
	}
	
	public static String getNameFromPrefix(String prefix){
		String name = "";
		for(Map.Entry<String, String> entry: namePrefix.entrySet()){
			if((prefix.equals(null) && entry.getValue() == null) || (!prefix.equals(null) && prefix.equals(entry.getValue()))){
				name = entry.getKey();
				break;
			}
		}
		return name;
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
	public static boolean hasDefaultColor(String chatName) {
		// TODO Auto-generated method stub
		if(nameColor.containsKey(chatName))
			return true;
		return false;
	}
	public static String getChatColor(String chatName) {
		// TODO Auto-generated method stub
		String chatColor = nameColor.get(chatName);
		return chatColor;
	}
	
}
