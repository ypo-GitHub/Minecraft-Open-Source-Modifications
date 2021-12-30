package me.earth.phobos.features.modules.movement;

import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.MathUtil;

public class VanillaSpeed extends Module {
  public Setting<Double> speed = register(new Setting("Speed", Double.valueOf(1.0D), Double.valueOf(1.0D), Double.valueOf(10.0D)));
  
  public VanillaSpeed() {
    super("VanillaSpeed", "ec.me", Module.Category.MOVEMENT, true, false, false);
  }
  
  public void onUpdate() {
    if (mc.player == null || mc.world == null)
      return; 
    double[] calc = MathUtil.directionSpeed(((Double)this.speed.getValue()).doubleValue() / 10.0D);
    mc.player.motionX = calc[0];
    mc.player.motionZ = calc[1];
  }
}
