package me.earth.phobos.mixin.mixins;

import me.earth.phobos.features.modules.movement.NoSlowDown;
import net.minecraft.block.BlockEndPortalFrame;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({BlockEndPortalFrame.class})
public class MixinBlockEndPortalFrame {
  @Shadow
  @Final
  protected static AxisAlignedBB AABB_BLOCK;
  
  @Inject(method = {"getBoundingBox"}, at = {@At("HEAD")}, cancellable = true)
  public void getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos, CallbackInfoReturnable<AxisAlignedBB> info) {
    if (NoSlowDown.getInstance().isOn() && ((Boolean)(NoSlowDown.getInstance()).endPortal.getValue()).booleanValue())
      info.setReturnValue(new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D)); 
  }
}
