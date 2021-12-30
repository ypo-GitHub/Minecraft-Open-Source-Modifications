package me.earth.phobos;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;
import me.earth.phobos.features.modules.misc.RPC;
import net.minecraft.client.Minecraft;

public class DiscordPresence {
  private static final DiscordRPC rpc;
  
  public static DiscordRichPresence presence;
  
  private static Thread thread;
  
  private static int index = 1;
  
  static {
    rpc = DiscordRPC.INSTANCE;
    presence = new DiscordRichPresence();
  }
  
  public static void start() {
    DiscordEventHandlers handlers = new DiscordEventHandlers();
    rpc.Discord_Initialize("833383689488302100", handlers, true, "");
    presence.startTimestamp = System.currentTimeMillis() / 1000L;
    presence.details = ((Minecraft.getMinecraft()).currentScreen instanceof net.minecraft.client.gui.GuiMainMenu) ? "In the main menu." : ("Playing " + (((Minecraft.getMinecraft()).currentServerData != null) ? (((Boolean)RPC.INSTANCE.showIP.getValue()).booleanValue() ? ("on " + (Minecraft.getMinecraft()).currentServerData.serverIP + ".") : " multiplayer.") : " singleplayer."));
    presence.state = (String)RPC.INSTANCE.state.getValue();
    presence.largeImageKey = "catware";
    presence.largeImageText = "https://discord.gg/9trHgf3Vmz";
    rpc.Discord_UpdatePresence(presence);
    thread = new Thread(() -> {
          while (!Thread.currentThread().isInterrupted()) {
            rpc.Discord_RunCallbacks();
            presence.details = ((Minecraft.getMinecraft()).currentScreen instanceof net.minecraft.client.gui.GuiMainMenu) ? "In the main menu." : ("Playing " + (((Minecraft.getMinecraft()).currentServerData != null) ? (((Boolean)RPC.INSTANCE.showIP.getValue()).booleanValue() ? ("on " + (Minecraft.getMinecraft()).currentServerData.serverIP + ".") : " multiplayer.") : " singleplayer."));
            presence.state = (String)RPC.INSTANCE.state.getValue();
            if (((Boolean)RPC.INSTANCE.catMode.getValue()).booleanValue()) {
              if (index == 16)
                index = 1; 
              presence.largeImageKey = "catcat" + index;
              index++;
            } 
            rpc.Discord_UpdatePresence(presence);
            try {
              Thread.sleep(2000L);
            } catch (InterruptedException interruptedException) {}
          } 
        }"RPC-Callback-Handler");
    thread.start();
  }
  
  public static void stop() {
    if (thread != null && !thread.isInterrupted())
      thread.interrupt(); 
    rpc.Discord_Shutdown();
  }
}
