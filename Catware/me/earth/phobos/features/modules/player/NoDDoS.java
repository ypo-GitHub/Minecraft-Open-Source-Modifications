package me.earth.phobos.features.modules.player;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import me.earth.phobos.Phobos;
import me.earth.phobos.event.events.ClientEvent;
import me.earth.phobos.features.command.Command;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class NoDDoS extends Module {
  private static NoDDoS instance;
  
  public final Setting<Boolean> full = register(new Setting("Full", Boolean.valueOf(false)));
  
  private final Map<String, Setting> servers = new ConcurrentHashMap<>();
  
  public Setting<String> newIP = register(new Setting("NewServer", "Add Server...", v -> !((Boolean)this.full.getValue()).booleanValue()));
  
  public Setting<Boolean> showServer = register(new Setting("ShowServers", Boolean.valueOf(false), v -> !((Boolean)this.full.getValue()).booleanValue()));
  
  public NoDDoS() {
    super("AntiDDoS", "Prevents DDoS attacks", Module.Category.PLAYER, false, true, true);
    instance = this;
  }
  
  public static NoDDoS getInstance() {
    if (instance == null)
      instance = new NoDDoS(); 
    return instance;
  }
  
  @SubscribeEvent
  public void onSettingChange(ClientEvent event) {
    if (Phobos.configManager.loadingConfig || Phobos.configManager.savingConfig)
      return; 
    if (event.getStage() == 2 && event.getSetting() != null && event.getSetting().getFeature() != null && event.getSetting().getFeature().equals(this))
      if (event.getSetting().equals(this.newIP) && !shouldntPing((String)this.newIP.getPlannedValue()) && !event.getSetting().getPlannedValue().equals(event.getSetting().getDefaultValue())) {
        Setting setting = register(new Setting((String)this.newIP.getPlannedValue(), Boolean.valueOf(true), v -> (((Boolean)this.showServer.getValue()).booleanValue() && !((Boolean)this.full.getValue()).booleanValue())));
        registerServer(setting);
        Command.sendMessage("<NoDDoS> Added new Server: " + (String)this.newIP.getPlannedValue());
        event.setCanceled(true);
      } else {
        Setting setting = event.getSetting();
        if (setting.equals(this.enabled) || setting.equals(this.drawn) || setting.equals(this.bind) || setting.equals(this.newIP) || setting.equals(this.showServer) || setting.equals(this.full))
          return; 
        if (setting.getValue() instanceof Boolean && !((Boolean)setting.getPlannedValue()).booleanValue()) {
          this.servers.remove(setting.getName().toLowerCase());
          unregister(setting);
          event.setCanceled(true);
        } 
      }  
  }
  
  public void registerServer(Setting setting) {
    this.servers.put(setting.getName().toLowerCase(), setting);
  }
  
  public boolean shouldntPing(String ip) {
    return (!isOff() && (((Boolean)this.full.getValue()).booleanValue() || this.servers.get(ip.toLowerCase()) != null));
  }
}
