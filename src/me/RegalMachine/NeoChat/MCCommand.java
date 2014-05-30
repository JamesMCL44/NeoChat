package me.RegalMachine.NeoChat;


import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class MCCommand extends Command{
	
	private CommandExecutor exe = null;
	
	protected MCCommand(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		// TODO Auto-generated method stub
		if(exe != null){
			exe.onCommand(sender, this, commandLabel, args);
			//return true;
		}
		return false;
		
	}
	public void setExecutor(CommandExecutor exe){
		this.exe = exe;
	}
	
}
