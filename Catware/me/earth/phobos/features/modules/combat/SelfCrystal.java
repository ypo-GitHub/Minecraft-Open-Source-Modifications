package me.earth.phobos.features.modules.combat;

import me.earth.phobos.features.modules.Module;
import net.minecraft.entity.player.EntityPlayer;

public class SelfCrystal extends Module {
  public SelfCrystal() {
    super("SelfCrystal", "Best module", Module.Category.COMBAT, true, true, false);
  }
  
  public void onTick() {
    if (AutoCrystal.getInstance().isEnabled())
      AutoCrystal.target = (EntityPlayer)mc.player; 
  }
}
