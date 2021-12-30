package me.earth.phobos.features.modules.render;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.EntityUtil2;
import me.earth.phobos.util.VectorUtils;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ImageESP extends Module {
  public Setting<Boolean> noRenderPlayers = register(new Setting("No Render Players", Boolean.valueOf(false)));
  
  private ResourceLocation waifu;
  
  private ICamera camera;
  
  public ImageESP() {
    super("ImageESP", "just a nice module kek", Module.Category.RENDER, true, true, false);
    this.camera = (ICamera)new Frustum();
  }
  
  private <T> BufferedImage getImage(T source, ThrowingFunction<T, BufferedImage> readFunction) {
    try {
      return readFunction.apply(source);
    } catch (IOException ex) {
      ex.printStackTrace();
      return null;
    } 
  }
  
  private boolean shouldDraw(EntityLivingBase entity) {
    return (!entity.equals(mc.player) && entity.getHealth() > 0.0F && EntityUtil2.isPlayer((Entity)entity));
  }
  
  @SubscribeEvent(priority = EventPriority.LOWEST)
  public void onRenderGameOverlayEvent(RenderGameOverlayEvent.Text event) {
    if (this.waifu == null)
      return; 
    double d3 = mc.player.lastTickPosX + (mc.player.posX - mc.player.lastTickPosX) * event.getPartialTicks();
    double d4 = mc.player.lastTickPosY + (mc.player.posY - mc.player.lastTickPosY) * event.getPartialTicks();
    double d5 = mc.player.lastTickPosZ + (mc.player.posZ - mc.player.lastTickPosZ) * event.getPartialTicks();
    this.camera.setPosition(d3, d4, d5);
    List<EntityPlayer> players = new ArrayList<>(mc.world.playerEntities);
    players.sort(Comparator.<EntityPlayer, Comparable>comparing(entityPlayer -> Float.valueOf(mc.player.getDistance((Entity)entityPlayer))).reversed());
    for (EntityPlayer player : players) {
      if (player != mc.getRenderViewEntity() && player.isEntityAlive() && this.camera.isBoundingBoxInFrustum(player.getEntityBoundingBox())) {
        EntityPlayer entityPlayer = player;
        Vec3d bottomVec = EntityUtil2.getInterpolatedPos((Entity)entityPlayer, event.getPartialTicks());
        Vec3d topVec = bottomVec.add(new Vec3d(0.0D, (player.getRenderBoundingBox()).maxY - player.posY, 0.0D));
        VectorUtils.ScreenPos top = VectorUtils._toScreen(topVec.x, topVec.y, topVec.z);
        VectorUtils.ScreenPos bot = VectorUtils._toScreen(bottomVec.x, bottomVec.y, bottomVec.z);
        if (!top.isVisible && !bot.isVisible)
          continue; 
        int width = bot.y - top.y, height = width;
        int x = (int)(top.x - width / 1.8D);
        int y = top.y;
        mc.renderEngine.bindTexture(this.waifu);
        GlStateManager.color(255.0F, 255.0F, 255.0F);
        Gui.drawScaledCustomSizeModalRect(x, y, 0.0F, 0.0F, width, height, width, height, width, height);
      } 
    } 
  }
  
  @SubscribeEvent
  public void onRenderPlayer(RenderPlayerEvent.Pre event) {
    if (((Boolean)this.noRenderPlayers.getValue()).booleanValue() && !event.getEntity().equals(mc.player))
      event.setCanceled(true); 
  }
  
  private InputStream getFile(String string) {
    return ImageESP.class.getResourceAsStream(string);
  }
  
  private enum CachedImage {
    CAT("/images/cat.png"),
    STEVEN("/images/stevengg.png"),
    JUICE("/images/juicee.png");
    
    String name;
    
    CachedImage(String name) {
      this.name = name;
    }
    
    public String getName() {
      return this.name;
    }
  }
  
  @FunctionalInterface
  private static interface ThrowingFunction<T, R> {
    R apply(T param1T) throws IOException;
  }
}
