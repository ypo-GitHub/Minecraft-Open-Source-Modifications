package vestige;

import org.lwjgl.opengl.Display;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.util.ChatComponentText;
import vestige.command.CommandManager;
import vestige.config.SaveLoad;
import vestige.font.FontUtil;
import vestige.module.ModuleManager;
import vestige.processor.impl.AuthentificationProcessor;
import vestige.processor.impl.PacketsProcessor;
import vestige.processor.impl.servers.HypixelProcessor;
import vestige.processor.impl.servers.RedeskyProcessor;
import vestige.util.base.BaseUtil;

public class Vestige implements BaseUtil {
	
	private static final Vestige instance = new Vestige();
	
	private static final String name = "Vestige";
	private static final String version = "beta 2.0";
	
	private static final String formattedName = "V" + ChatFormatting.WHITE + "estige";
	
	private static String username;
	
	private static ModuleManager moduleManager;
	private static CommandManager commandManager;
	
	private static SaveLoad config;
	
	private static AuthentificationProcessor authProcessor;
	
	private static PacketsProcessor packetsProcessor;
	private static HypixelProcessor hypixelProcessor;
	private static RedeskyProcessor redeskyProcessor;
	public static AuthentificationProcessor auth;
	
	public static void start() {
		System.out.println(getConsolePrefix() + "Loading Client");
		
		moduleManager = new ModuleManager();
		System.out.println(getConsolePrefix() + "Loaded Modules");
		
		config = new SaveLoad("default", true);
		config.load(true);
		System.out.println("Loaded Default Config");
		
		commandManager = new CommandManager();
		System.out.println(getConsolePrefix() + "Loaded Command Manager");
		
		authProcessor = new AuthentificationProcessor();
		
		packetsProcessor = new PacketsProcessor();
		hypixelProcessor = new HypixelProcessor();
		redeskyProcessor = new RedeskyProcessor();
		System.out.println(getConsolePrefix() + "Loaded Processors");
		
		try {
			FontUtil.bootstrap();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Loaded Custom FontRenderer Util");
		
		Display.setTitle(getFullName() + " | Minecraft 1.8.9");
		auth = new AuthentificationProcessor();
		auth.setAuthentificated(true);
	}
	
	public static void shutdown() {
		config.save();
	}
	
	public static Vestige getInstance() {
		return instance;
	}
	
	public static String getName() {
		return name;
	}
	
	public static String getVersion() {
		return version;
	}
	
	public static String getFormattedName() {
		return formattedName;
	}
	
	public static String getFullName() {
		return name + " " + version;
	}
	
	public static String getFullFormattedName() {
		return formattedName + " " + version;
	}
	
	public static ModuleManager getModuleManager() {
		return moduleManager;
	}
	
	public static CommandManager getCommandManager() {
		return commandManager;
	}
	
	public static AuthentificationProcessor getAuthProcessor() {
		return authProcessor;
	}
	
	public static PacketsProcessor getPacketsProcessor() {
		return packetsProcessor;
	}
	
	public static HypixelProcessor getHypixelProcessor() {
		return hypixelProcessor;
	}
	
	public static RedeskyProcessor getRedeskyProcessor() {
		return redeskyProcessor;
	}
	
	public static void addChatMessage(String message) {
		message = ChatFormatting.BLUE + "Vestige" + ChatFormatting.WHITE + " : " + message;
		
		mc.thePlayer.addChatMessage(new ChatComponentText(message));
	}
	
	public static String getConsolePrefix() {
		return "[" + getFullName() + "] : ";
	}
	
	public static String getUsername() {
		return username;
	}
	
	public static void setUsername(String username) {
		Vestige.username = username;
	}
	
}