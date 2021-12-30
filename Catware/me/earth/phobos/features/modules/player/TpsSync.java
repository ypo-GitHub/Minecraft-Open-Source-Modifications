package me.earth.phobos.features.modules.player;

import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;

public class TpsSync extends Module {
  private static TpsSync INSTANCE = new TpsSync();
  
  public Setting<Boolean> mining = register(new Setting("Mining", Boolean.valueOf(true)));
  
  public Setting<Boolean> attack = register(new Setting("Attack", Boolean.valueOf(false)));
  
  public TpsSync() {
    super("TpsSync", "Syncs your client with the TPS.", Module.Category.PLAYER, true, false, false);
    setInstance();
  }
  
  public static TpsSync getInstance() {
    if (INSTANCE == null)
      INSTANCE = new TpsSync(); 
    return INSTANCE;
  }
  
  private void setInstance() {
    INSTANCE = this;
  }
}
