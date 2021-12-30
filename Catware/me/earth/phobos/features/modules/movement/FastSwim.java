package me.earth.phobos.features.modules.movement;

import me.earth.phobos.event.events.MoveEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class FastSwim extends Module {
  public Setting<Double> waterHorizontal = register(new Setting("WaterHorizontal", Double.valueOf(3.0D), Double.valueOf(1.0D), Double.valueOf(20.0D)));
  
  public Setting<Double> waterVertical = register(new Setting("WaterVertical", Double.valueOf(3.0D), Double.valueOf(1.0D), Double.valueOf(20.0D)));
  
  public Setting<Double> lavaHorizontal = register(new Setting("LavaHorizontal", Double.valueOf(4.0D), Double.valueOf(1.0D), Double.valueOf(20.0D)));
  
  public Setting<Double> lavaVertical = register(new Setting("LavaVertical", Double.valueOf(4.0D), Double.valueOf(1.0D), Double.valueOf(20.0D)));
  
  public FastSwim() {
    super("FastSwim", "Swim fast", Module.Category.MOVEMENT, true, false, false);
  }
  
  @SubscribeEvent
  public void onMove(MoveEvent event) {
    if (mc.player.isInLava() && !mc.player.onGround) {
      event.setX(event.getX() * ((Double)this.lavaHorizontal.getValue()).doubleValue());
      event.setZ(event.getZ() * ((Double)this.lavaHorizontal.getValue()).doubleValue());
      event.setY(event.getY() * ((Double)this.lavaVertical.getValue()).doubleValue());
    } else if (mc.player.isInWater() && !mc.player.onGround) {
      event.setX(event.getX() * ((Double)this.waterHorizontal.getValue()).doubleValue());
      event.setZ(event.getZ() * ((Double)this.waterHorizontal.getValue()).doubleValue());
      event.setY(event.getY() * ((Double)this.waterVertical.getValue()).doubleValue());
    } 
  }
}
