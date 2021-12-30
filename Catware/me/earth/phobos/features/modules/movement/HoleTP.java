package me.earth.phobos.features.modules.movement;

import me.earth.phobos.event.events.UpdateWalkingPlayerEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.util.BlockUtil;
import me.earth.phobos.util.EntityUtil;
import net.minecraft.block.material.Material;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class HoleTP extends Module {
  private static HoleTP INSTANCE = new HoleTP();
  
  private final double[] oneblockPositions = new double[] { 0.42D, 0.75D };
  
  private int packets;
  
  private boolean jumped = false;
  
  public HoleTP() {
    super("HoleTP", "Teleports you in a hole.", Module.Category.MOVEMENT, true, true, false);
    setInstance();
  }
  
  public static HoleTP getInstance() {
    if (INSTANCE == null)
      INSTANCE = new HoleTP(); 
    return INSTANCE;
  }
  
  private void setInstance() {
    INSTANCE = this;
  }
  
  @SubscribeEvent
  public void onUpdateWalkingPlayer(UpdateWalkingPlayerEvent event) {
    if (event.getStage() == 1 && (Speed.getInstance().isOff() || (Speed.getInstance()).mode.getValue() == Speed.Mode.INSTANT) && Strafe.getInstance().isOff() && LagBlock.getInstance().isOff()) {
      if (!mc.player.onGround) {
        if (mc.gameSettings.keyBindJump.isKeyDown())
          this.jumped = true; 
      } else {
        this.jumped = false;
      } 
      if (!this.jumped && mc.player.fallDistance < 0.5D && BlockUtil.isInHole() && mc.player.posY - BlockUtil.getNearestBlockBelow() <= 1.125D && mc.player.posY - BlockUtil.getNearestBlockBelow() <= 0.95D && !EntityUtil.isOnLiquid() && !EntityUtil.isInLiquid()) {
        if (!mc.player.onGround)
          this.packets++; 
        if (!mc.player.onGround && !mc.player.isInsideOfMaterial(Material.WATER) && !mc.player.isInsideOfMaterial(Material.LAVA) && !mc.gameSettings.keyBindJump.isKeyDown() && !mc.player.isOnLadder() && this.packets > 0) {
          BlockPos blockPos = new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);
          for (double position : this.oneblockPositions)
            mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position((blockPos.getX() + 0.5F), mc.player.posY - position, (blockPos.getZ() + 0.5F), true)); 
          mc.player.setPosition((blockPos.getX() + 0.5F), BlockUtil.getNearestBlockBelow() + 0.1D, (blockPos.getZ() + 0.5F));
          this.packets = 0;
        } 
      } 
    } 
  }
}
