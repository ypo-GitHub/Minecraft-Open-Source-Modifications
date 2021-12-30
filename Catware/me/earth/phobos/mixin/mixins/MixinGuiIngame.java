package me.earth.phobos.mixin.mixins;

import me.earth.phobos.Phobos;
import me.earth.phobos.features.gui.custom.GuiCustomNewChat;
import me.earth.phobos.features.modules.client.HUD;
import me.earth.phobos.features.modules.render.NoRender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.gui.ScaledResolution;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({GuiIngame.class})
public class MixinGuiIngame extends Gui {
  @Shadow
  @Final
  public GuiNewChat persistantChatGUI;
  
  @Inject(method = {"<init>"}, at = {@At("RETURN")})
  public void init(Minecraft mcIn, CallbackInfo ci) {
    this.persistantChatGUI = (GuiNewChat)new GuiCustomNewChat(mcIn);
  }
  
  @Inject(method = {"renderPortal"}, at = {@At("HEAD")}, cancellable = true)
  protected void renderPortalHook(float n, ScaledResolution scaledResolution, CallbackInfo info) {
    if (NoRender.getInstance().isOn() && ((Boolean)(NoRender.getInstance()).portal.getValue()).booleanValue())
      info.cancel(); 
  }
  
  @Inject(method = {"renderPumpkinOverlay"}, at = {@At("HEAD")}, cancellable = true)
  protected void renderPumpkinOverlayHook(ScaledResolution scaledRes, CallbackInfo info) {
    if (NoRender.getInstance().isOn() && ((Boolean)(NoRender.getInstance()).pumpkin.getValue()).booleanValue())
      info.cancel(); 
  }
  
  @Inject(method = {"renderPotionEffects"}, at = {@At("HEAD")}, cancellable = true)
  protected void renderPotionEffectsHook(ScaledResolution scaledRes, CallbackInfo info) {
    if (Phobos.moduleManager != null && !((Boolean)(HUD.getInstance()).potionIcons.getValue()).booleanValue())
      info.cancel(); 
  }
}
