package me.earth.phobos.mixin.mixins;

import me.earth.phobos.features.modules.movement.BoatFly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({EntityBoat.class})
public abstract class MixinEntityBoat {
  @Shadow
  public abstract double getMountedYOffset();
  
  @Inject(method = {"applyOrientationToEntity"}, at = {@At("HEAD")}, cancellable = true)
  public void applyOrientationToEntity(Entity entity, CallbackInfo ci) {
    if (BoatFly.INSTANCE.isEnabled())
      ci.cancel(); 
  }
  
  @Inject(method = {"controlBoat"}, at = {@At("HEAD")}, cancellable = true)
  public void controlBoat(CallbackInfo ci) {
    if (BoatFly.INSTANCE.isEnabled())
      ci.cancel(); 
  }
  
  @Inject(method = {"updatePassenger"}, at = {@At("HEAD")}, cancellable = true)
  public void updatePassenger(Entity passenger, CallbackInfo ci) {
    if (BoatFly.INSTANCE.isEnabled() && passenger == (Minecraft.getMinecraft()).player) {
      ci.cancel();
      float f = 0.0F;
      float f1 = (float)((((Entity)this).isDead ? 0.009999999776482582D : getMountedYOffset()) + passenger.getYOffset());
      Vec3d vec3d = (new Vec3d(f, 0.0D, 0.0D)).rotateYaw(-(((Entity)this).rotationYaw * 0.017453292F - 1.5707964F));
      passenger.setPosition(((Entity)this).posX + vec3d.x, ((Entity)this).posY + f1, ((Entity)this).posZ + vec3d.z);
    } 
  }
}
