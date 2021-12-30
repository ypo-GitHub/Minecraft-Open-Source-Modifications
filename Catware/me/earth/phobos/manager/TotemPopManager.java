package me.earth.phobos.manager;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import me.earth.phobos.Phobos;
import me.earth.phobos.features.Feature;
import me.earth.phobos.features.command.Command;
import me.earth.phobos.features.modules.client.Notifications;
import net.minecraft.entity.player.EntityPlayer;

public class TotemPopManager extends Feature {
  private Notifications notifications;
  
  private Map<EntityPlayer, Integer> poplist = new ConcurrentHashMap<>();
  
  private final Set<EntityPlayer> toAnnounce = new HashSet<>();
  
  public void onUpdate() {
    if (this.notifications.totemAnnounce.passedMs(((Integer)this.notifications.delay.getValue()).intValue()) && this.notifications.isOn() && ((Boolean)this.notifications.totemPops.getValue()).booleanValue())
      for (EntityPlayer player : this.toAnnounce) {
        if (player == null)
          continue; 
        int playerNumber = 0;
        for (char character : player.getName().toCharArray()) {
          playerNumber += character;
          playerNumber *= 10;
        } 
        Command.sendOverwriteMessage("§c" + player.getName() + " popped §a" + getTotemPops(player) + "§c Totem" + ((getTotemPops(player) == 1) ? "" : "s") + ".", playerNumber, ((Boolean)this.notifications.totemNoti.getValue()).booleanValue());
        this.toAnnounce.remove(player);
        this.notifications.totemAnnounce.reset();
      }  
  }
  
  public void onLogout() {
    onOwnLogout(((Boolean)this.notifications.clearOnLogout.getValue()).booleanValue());
  }
  
  public void init() {
    this.notifications = Phobos.moduleManager.<Notifications>getModuleByClass(Notifications.class);
  }
  
  public void onTotemPop(EntityPlayer player) {
    popTotem(player);
    if (!player.equals(mc.player)) {
      this.toAnnounce.add(player);
      this.notifications.totemAnnounce.reset();
    } 
  }
  
  public void onDeath(EntityPlayer player) {
    if (getTotemPops(player) != 0 && !player.equals(mc.player) && this.notifications.isOn() && ((Boolean)this.notifications.totemPops.getValue()).booleanValue()) {
      int playerNumber = 0;
      for (char character : player.getName().toCharArray()) {
        playerNumber += character;
        playerNumber *= 10;
      } 
      Command.sendOverwriteMessage("§c" + player.getName() + " died after popping §a" + getTotemPops(player) + "§c Totem" + ((getTotemPops(player) == 1) ? "" : "s") + ".", playerNumber, ((Boolean)this.notifications.totemNoti.getValue()).booleanValue());
      this.toAnnounce.remove(player);
    } 
    resetPops(player);
  }
  
  public void onLogout(EntityPlayer player, boolean clearOnLogout) {
    if (clearOnLogout)
      resetPops(player); 
  }
  
  public void onOwnLogout(boolean clearOnLogout) {
    if (clearOnLogout)
      clearList(); 
  }
  
  public void clearList() {
    this.poplist = new ConcurrentHashMap<>();
  }
  
  public void resetPops(EntityPlayer player) {
    setTotemPops(player, 0);
  }
  
  public void popTotem(EntityPlayer player) {
    this.poplist.merge(player, Integer.valueOf(1), Integer::sum);
  }
  
  public void setTotemPops(EntityPlayer player, int amount) {
    this.poplist.put(player, Integer.valueOf(amount));
  }
  
  public int getTotemPops(EntityPlayer player) {
    Integer pops = this.poplist.get(player);
    if (pops == null)
      return 0; 
    return pops.intValue();
  }
  
  public String getTotemPopString(EntityPlayer player) {
    return "§f" + ((getTotemPops(player) <= 0) ? "" : ("-" + getTotemPops(player) + " "));
  }
}
