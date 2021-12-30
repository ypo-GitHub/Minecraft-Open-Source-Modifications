package me.earth.phobos.features.command.commands;

import java.io.IOException;
import me.earth.phobos.features.command.Command;
import me.earth.phobos.features.modules.client.IRC;

public class IRCCommand extends Command {
  public IRCCommand() {
    super("IRC");
  }
  
  public void execute(String[] commands) {
    if (commands.length == 1) {
      sendMessage(IRC.INSTANCE.status ? "§aIRC is connected." : "§cIRC is not connected.");
    } else if (commands.length == 2) {
      if (commands[0].equalsIgnoreCase("connect")) {
        sendMessage("§aConnecting to the PhobosClient IRC...");
        try {
          IRC.INSTANCE.connect();
        } catch (IOException e) {
          e.printStackTrace();
        } 
      } else if (commands[0].equalsIgnoreCase("disconnect")) {
        sendMessage("§aDisconnecting from the PhobosClient IRC...");
        try {
          IRC.INSTANCE.disconnect();
        } catch (IOException e) {
          e.printStackTrace();
        } 
      } else if (commands[0].equalsIgnoreCase("friendall")) {
        sendMessage("§aFriending all...");
        try {
          IRC.INSTANCE.friendAll();
        } catch (IOException e) {
          e.printStackTrace();
        } 
      } else if (commands[0].equalsIgnoreCase("list")) {
        sendMessage("§aListing PhobosClient Users...");
        try {
          IRC.INSTANCE.list();
        } catch (IOException e) {
          e.printStackTrace();
        } 
      } 
    } else if (commands.length >= 3) {
      if (commands[0].equalsIgnoreCase("say")) {
        sendMessage("§aSending message to the PhobosClient chat server...");
        StringBuilder builder = new StringBuilder();
        for (int i = 1; i < commands.length - 1; i++)
          builder.append(commands[i]).append(" "); 
        String message = builder.toString();
        try {
          IRC.say(message);
        } catch (IOException e) {
          e.printStackTrace();
        } 
      } else if (commands[0].equalsIgnoreCase("cockt")) {
        sendMessage("§acockkk");
        try {
          IRC.cockt(Integer.parseInt(commands[1]));
        } catch (IOException e) {
          e.printStackTrace();
        } 
      } 
    } 
  }
}
