package me.earth.phobos.features.modules.movement;

import me.earth.phobos.features.modules.Module;

public class ReverseStep extends Module {
  public ReverseStep() {
    super("ReverseStep", "Screams chinese words and teleports you", Module.Category.MOVEMENT, true, false, false);
  }
  
  public void onUpdate() {
    if (mc.player.onGround)
      mc.player.motionY--; 
  }
}
