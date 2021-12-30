package me.earth.phobos.features.modules.movement;

import java.util.HashMap;
import java.util.Map;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.HoleUtil;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;

public class Anchor extends Module {
  private final Setting<Boolean> guarantee = register(new Setting("Guarantee Hole", Boolean.valueOf(true)));
  
  private final Setting<Integer> activateHeight = register(new Setting("Activate Height", Integer.valueOf(2), Integer.valueOf(1), Integer.valueOf(5)));
  
  private BlockPos playerPos;
  
  public Anchor() {
    super("Anchor", "Pulls you to a safe hole", Module.Category.MOVEMENT, true, false, false);
  }
  
  public void onUpdate() {
    if (mc.player == null)
      return; 
    if (mc.player.posY < 0.0D)
      return; 
    double blockX = Math.floor(mc.player.posX);
    double blockZ = Math.floor(mc.player.posZ);
    double offsetX = Math.abs(mc.player.posX - blockX);
    double offsetZ = Math.abs(mc.player.posZ - blockZ);
    if (((Boolean)this.guarantee.getValue()).booleanValue() && (offsetX < 0.30000001192092896D || offsetX > 0.699999988079071D || offsetZ < 0.30000001192092896D || offsetZ > 0.699999988079071D))
      return; 
    this.playerPos = new BlockPos(blockX, mc.player.posY, blockZ);
    if (mc.world.getBlockState(this.playerPos).getBlock() != Blocks.AIR)
      return; 
    BlockPos currentBlock = this.playerPos.down();
    for (int i = 0; i < ((Integer)this.activateHeight.getValue()).intValue(); i++) {
      currentBlock = currentBlock.down();
      if (mc.world.getBlockState(currentBlock).getBlock() != Blocks.AIR) {
        HashMap<HoleUtil.BlockOffset, HoleUtil.BlockSafety> sides = HoleUtil.getUnsafeSides(currentBlock.up());
        sides.entrySet().removeIf(entry -> (entry.getValue() == HoleUtil.BlockSafety.RESISTANT));
        if (sides.size() == 0) {
          mc.player.motionX = 0.0D;
          mc.player.motionZ = 0.0D;
        } 
      } 
    } 
  }
}
