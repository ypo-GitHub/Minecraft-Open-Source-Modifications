package me.earth.phobos.features.modules.movement;

import me.earth.phobos.features.modules.Module;

public class StairSpeed extends Module {
  public StairSpeed() {
    super("StairSpeed", "Great module", Module.Category.MOVEMENT, true, true, false);
  }
  
  public void onUpdate() {
    if (mc.player.onGround && mc.player.posY - Math.floor(mc.player.posY) > 0.0D && mc.player.moveForward != 0.0F)
      mc.player.jump(); 
  }
}
