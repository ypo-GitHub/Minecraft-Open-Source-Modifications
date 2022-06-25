package swapper.intentions.tropical.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;

public class ChatUtils {
	static Minecraft mc = Minecraft.getMinecraft();
	
	public static void clientMessage(String text) {
		if(mc.thePlayer == null)
			return;
		mc.thePlayer.addChatComponentMessage(new ChatComponentText("\247d[Tropical] \247f" + text));
	}
	
	public static void clientMessage(String text, String from) {
		if(mc.thePlayer == null)
			return;
		mc.thePlayer.addChatComponentMessage(new ChatComponentText("\247d[Tropical: " + from + "] \247f" + text));
	}
}
