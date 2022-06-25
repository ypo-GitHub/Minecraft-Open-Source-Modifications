package vestige.anticheat;

import com.mojang.realmsclient.gui.ChatFormatting;

import vestige.Vestige;
import vestige.util.base.AnticheatBaseUtil;

public class Check implements AnticheatBaseUtil {
	
	private final String name;
	protected final User user;
	private int vl;
	
	public Check(String name, User user) {
		this.name = name;
		this.user = user;
	}
	
	public String getName() {
		return name;
	}
	
	public void handle() {
		
	}
	
	public void violation() {
		if(Vestige.getModuleManager().getModuleByName("Anticheat").isEnabled()) {
			vl++;
			Vestige.addChatMessage(user.getPlayer().getGameProfile().getName() + " has failed " + this.getName());
		}
	}
	
	public void violation(String message) {
		if(Vestige.getModuleManager().getModuleByName("Anticheat").isEnabled()) {
			vl++;
			Vestige.addChatMessage(user.getPlayer().getGameProfile().getName() + " has failed " + this.getName() + ChatFormatting.GRAY + " | ");
		}
	}
	
}
