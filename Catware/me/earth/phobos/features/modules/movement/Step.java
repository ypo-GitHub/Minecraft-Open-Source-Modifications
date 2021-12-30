package me.earth.phobos.features.modules.movement;

import me.earth.phobos.event.events.StepEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import net.minecraft.block.material.Material;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Step extends Module {
  private static Step instance;
  
  public Setting<Boolean> vanilla = register(new Setting("Vanilla", Boolean.valueOf(false)));
  
  public Setting<Integer> stepHeight = register(new Setting("Height", Integer.valueOf(2), Integer.valueOf(1), Integer.valueOf(2)));
  
  public Setting<Boolean> turnOff = register(new Setting("Disable", Boolean.valueOf(false)));
  
  public Step() {
    super("Step", "Allows you to step up blocks", Module.Category.MOVEMENT, true, false, false);
    instance = this;
  }
  
  public static Step getInstance() {
    if (instance == null)
      instance = new Step(); 
    return instance;
  }
  
  @SubscribeEvent
  public void onStep(StepEvent event) {
    if (mc.player.onGround && !mc.player.isInsideOfMaterial(Material.WATER) && !mc.player.isInsideOfMaterial(Material.LAVA) && mc.player.collidedVertically && mc.player.fallDistance == 0.0F && !mc.gameSettings.keyBindJump.pressed && !mc.player.isOnLadder()) {
      event.setHeight(((Integer)this.stepHeight.getValue()).intValue());
      double rheight = (mc.player.getEntityBoundingBox()).minY - mc.player.posY;
      if (rheight >= 0.625D) {
        if (!((Boolean)this.vanilla.getValue()).booleanValue())
          ncpStep(rheight); 
        if (((Boolean)this.turnOff.getValue()).booleanValue())
          disable(); 
      } 
    } else {
      event.setHeight(0.6F);
    } 
  }
  
  private void ncpStep(double height) {
    double posX = mc.player.posX;
    double posZ = mc.player.posZ;
    double y = mc.player.posY;
    if (height >= 1.1D) {
      if (height < 1.6D) {
        double[] offset;
        for (double off : offset = new double[] { 0.42D, 0.33D, 0.24D, 0.083D, -0.078D })
          mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(posX, y += off, posZ, false)); 
      } else if (height < 2.1D) {
        double[] heights;
        for (double off : heights = new double[] { 0.425D, 0.821D, 0.699D, 0.599D, 1.022D, 1.372D, 1.652D, 1.869D })
          mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(posX, y + off, posZ, false)); 
      } else {
        double[] heights;
        for (double off : heights = new double[] { 0.425D, 0.821D, 0.699D, 0.599D, 1.022D, 1.372D, 1.652D, 1.869D, 2.019D, 1.907D })
          mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(posX, y + off, posZ, false)); 
      } 
      return;
    } 
    double first = 0.42D;
    double d1 = 0.75D;
    if (height != 1.0D) {
      first *= height;
      d1 *= height;
      if (first > 0.425D)
        first = 0.425D; 
      if (d1 > 0.78D)
        d1 = 0.78D; 
      if (d1 < 0.49D)
        d1 = 0.49D; 
    } 
    mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(posX, y + first, posZ, false));
    if (y + d1 < y + height)
      mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(posX, y + d1, posZ, false)); 
  }
}
