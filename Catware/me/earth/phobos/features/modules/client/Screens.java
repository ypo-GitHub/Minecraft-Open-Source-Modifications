package me.earth.phobos.features.modules.client;

import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;

public class Screens extends Module {
  public static Screens INSTANCE;
  
  public Setting<Boolean> mainScreen = register(new Setting("MainScreen", Boolean.valueOf(true)));
  
  public Screens() {
    super("Screens", "Controls custom screens used by the client", Module.Category.CLIENT, true, false, false);
    INSTANCE = this;
  }
  
  public void onTick() {}
}
