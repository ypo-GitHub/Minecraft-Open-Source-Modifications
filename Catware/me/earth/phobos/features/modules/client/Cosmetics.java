package me.earth.phobos.features.modules.client;

import me.earth.phobos.Phobos;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.util.EntityUtil;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

public class Cosmetics extends Module {
  public static Cosmetics INSTANCE;
  
  public final ModelBetterPhysicsCape betterPhysicsCape = new ModelBetterPhysicsCape();
  
  public final ModelCloutGoggles cloutGoggles = new ModelCloutGoggles();
  
  public final ModelPhyscisCapes capesModel = new ModelPhyscisCapes();
  
  public final ModelSquidFlag flag = new ModelSquidFlag();
  
  public final TopHatModel hatModel = new TopHatModel();
  
  public final GlassesModel glassesModel = new GlassesModel();
  
  public final SantaHatModel santaHatModel = new SantaHatModel();
  
  public final ModelHatFez fezModel = new ModelHatFez();
  
  public final ModelSquidLauncher squidLauncher = new ModelSquidLauncher();
  
  private final HatGlassesModel hatGlassesModel = new HatGlassesModel();
  
  private final ResourceLocation hatTexture = new ResourceLocation("textures/tophat.png");
  
  private final ResourceLocation fezTexture = new ResourceLocation("textures/fez.png");
  
  private final ResourceLocation glassesTexture = new ResourceLocation("textures/sunglasses.png");
  
  private final ResourceLocation santaHatTexture = new ResourceLocation("textures/santahat.png");
  
  private final ResourceLocation capeTexture = new ResourceLocation("textures/cape.png");
  
  private final ResourceLocation squidTexture = new ResourceLocation("textures/squid.png");
  
  private final ResourceLocation cloutGoggleTexture = new ResourceLocation("textures/cloutgoggles.png");
  
  private final ResourceLocation squidLauncherTexture = new ResourceLocation("textures/squidlauncher.png");
  
  public Cosmetics() {
    super("Cosmetics", "Bitch", Module.Category.CLIENT, true, true, false);
    INSTANCE = this;
  }
  
  @SubscribeEvent
  public void onRenderPlayer(RenderPlayerEvent.Post event) {
    if (!Phobos.cosmeticsManager.hasCosmetics(event.getEntityPlayer()) || EntityUtil.isFakePlayer(event.getEntityPlayer()))
      return; 
    for (ModelBase model : Phobos.cosmeticsManager.getRenderModels(event.getEntityPlayer())) {
      GlStateManager.pushMatrix();
      RenderManager renderManager = mc.getRenderManager();
      GlStateManager.translate(event.getX(), event.getY(), event.getZ());
      double scale = 1.0D;
      double rotate = interpolate((event.getEntityPlayer()).prevRotationYawHead, (event.getEntityPlayer()).rotationYawHead, event.getPartialRenderTick());
      double rotate1 = interpolate((event.getEntityPlayer()).prevRotationPitch, (event.getEntityPlayer()).rotationPitch, event.getPartialRenderTick());
      double rotate3 = event.getEntityPlayer().isSneaking() ? 22.0D : 0.0D;
      float limbSwingAmount = interpolate((event.getEntityPlayer()).prevLimbSwingAmount, (event.getEntityPlayer()).limbSwingAmount, event.getPartialRenderTick());
      float rotate2 = MathHelper.cos((event.getEntityPlayer()).limbSwing * 0.6662F + 3.1415927F) * 1.4F * limbSwingAmount / 1.0F;
      GL11.glScaled(-scale, -scale, scale);
      GL11.glTranslated(0.0D, -((event.getEntityPlayer()).height - (event.getEntityPlayer().isSneaking() ? 0.25D : 0.0D) - 0.38D) / scale, 0.0D);
      GL11.glRotated(180.0D + rotate, 0.0D, 1.0D, 0.0D);
      if (!(model instanceof ModelSquidLauncher))
        GL11.glRotated(rotate1, 1.0D, 0.0D, 0.0D); 
      if (model instanceof ModelSquidLauncher)
        GL11.glRotated(rotate3, 1.0D, 0.0D, 0.0D); 
      GlStateManager.translate(0.0D, -0.45D, 0.0D);
      if (model instanceof ModelSquidLauncher) {
        GlStateManager.translate(0.15D, 1.3D, 0.0D);
        for (ModelRenderer renderer : this.squidLauncher.boxList)
          renderer.rotateAngleX = rotate2; 
      } 
      if (model instanceof TopHatModel) {
        mc.getTextureManager().bindTexture(this.hatTexture);
        this.hatModel.render(event.getEntity(), 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
        mc.getTextureManager().deleteTexture(this.hatTexture);
      } else if (model instanceof GlassesModel) {
        if (event.getEntityPlayer().isWearing(EnumPlayerModelParts.HAT)) {
          mc.getTextureManager().bindTexture(this.glassesTexture);
          this.hatGlassesModel.render(event.getEntity(), 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
          mc.getTextureManager().deleteTexture(this.glassesTexture);
        } else {
          mc.getTextureManager().bindTexture(this.glassesTexture);
          this.glassesModel.render(event.getEntity(), 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
          mc.getTextureManager().deleteTexture(this.glassesTexture);
        } 
      } else if (model instanceof SantaHatModel) {
        mc.getTextureManager().bindTexture(this.santaHatTexture);
        this.santaHatModel.render(event.getEntity(), 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
        mc.getTextureManager().deleteTexture(this.santaHatTexture);
      } else if (model instanceof ModelCloutGoggles) {
        mc.getTextureManager().bindTexture(this.cloutGoggleTexture);
        this.cloutGoggles.render(event.getEntity(), 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
        mc.getTextureManager().deleteTexture(this.cloutGoggleTexture);
      } else if (model instanceof ModelSquidFlag) {
        mc.getTextureManager().bindTexture(this.squidTexture);
        this.flag.render(event.getEntity(), 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
        mc.getTextureManager().deleteTexture(this.squidTexture);
      } else if (model instanceof ModelSquidLauncher) {
        mc.getTextureManager().bindTexture(this.squidLauncherTexture);
        this.squidLauncher.render(event.getEntity(), 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0325F);
        mc.getTextureManager().deleteTexture(this.squidLauncherTexture);
      } 
      GlStateManager.popMatrix();
    } 
  }
  
  public float interpolate(float yaw1, float yaw2, float percent) {
    float rotation = (yaw1 + (yaw2 - yaw1) * percent) % 360.0F;
    if (rotation < 0.0F)
      rotation += 360.0F; 
    return rotation;
  }
  
  public static class ModelHatFez extends ModelBase {
    private final ModelRenderer baseLayer;
    
    private final ModelRenderer topLayer;
    
    private final ModelRenderer stringLayer;
    
    private final ModelRenderer danglingStringLayer;
    
    private final ModelRenderer otherDanglingStringLayer;
    
    public ModelHatFez() {
      this.textureWidth = 64;
      this.textureHeight = 32;
      this.baseLayer = new ModelRenderer(this, 1, 1);
      this.baseLayer.addBox(-3.0F, 0.0F, -3.0F, 6, 4, 6);
      this.baseLayer.setRotationPoint(0.0F, -4.0F, 0.0F);
      this.baseLayer.setTextureSize(this.textureWidth, this.textureHeight);
      this.baseLayer.mirror = true;
      setRotation(this.baseLayer, 0.0F, 0.12217305F, 0.0F);
      this.topLayer = new ModelRenderer(this, 1, 1);
      this.topLayer.addBox(0.0F, 0.0F, 0.0F, 1, 1, 1);
      this.topLayer.setRotationPoint(-0.5F, -4.75F, -0.5F);
      this.topLayer.setTextureSize(this.textureWidth, this.textureHeight);
      this.topLayer.mirror = true;
      setRotation(this.topLayer, 0.0F, 0.0F, 0.0F);
      this.stringLayer = new ModelRenderer(this, 25, 1);
      this.stringLayer.addBox(-0.5F, -0.5F, -0.5F, 3, 1, 1);
      this.stringLayer.setRotationPoint(0.5F, -3.75F, 0.0F);
      this.stringLayer.setTextureSize(this.textureWidth, this.textureHeight);
      this.stringLayer.mirror = true;
      setRotation(this.stringLayer, 0.7853982F, 0.0F, 0.0F);
      this.danglingStringLayer = new ModelRenderer(this, 41, 1);
      this.danglingStringLayer.addBox(-0.5F, -0.5F, -0.5F, 3, 1, 1);
      this.danglingStringLayer.setRotationPoint(3.0F, -3.5F, 0.0F);
      this.danglingStringLayer.setTextureSize(this.textureWidth, this.textureHeight);
      this.danglingStringLayer.mirror = true;
      setRotation(this.danglingStringLayer, 0.2268928F, 0.7853982F, 1.2042772F);
      this.otherDanglingStringLayer = new ModelRenderer(this, 33, 9);
      this.otherDanglingStringLayer.addBox(-0.5F, -0.5F, -0.5F, 3, 1, 1);
      this.otherDanglingStringLayer.setRotationPoint(3.0F, -3.5F, 0.0F);
      this.otherDanglingStringLayer.setTextureSize(this.textureWidth, this.textureHeight);
      this.otherDanglingStringLayer.mirror = true;
      setRotation(this.otherDanglingStringLayer, 0.2268928F, -0.9250245F, 1.2042772F);
    }
    
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
      super.render(entity, f, f1, f2, f3, f4, f5);
      setRotationAngles(f, f1, f2, f3, f4, f5);
      this.baseLayer.render(f5);
      this.topLayer.render(f5);
      this.stringLayer.render(f5);
      this.danglingStringLayer.render(f5);
      this.otherDanglingStringLayer.render(f5);
    }
    
    private void setRotation(ModelRenderer model, float x, float y, float z) {
      model.rotateAngleX = x;
      model.rotateAngleY = y;
      model.rotateAngleZ = z;
    }
    
    public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5) {
      setRotationAngles(f, f1, f2, f3, f4, f5, null);
    }
  }
  
  public class ModelBetterPhysicsCape extends ModelBase {
    public ModelRenderer segment1;
    
    public ModelBetterPhysicsCape() {
      for (int i = 0; i < 160; i++) {
        ModelRenderer segment = new ModelRenderer(this, 0, i);
        segment.setRotationPoint(0.0F, 0.0F, 0.0F);
        segment.addBox(-5.0F, 0.0F + i, 0.0F, 10, 1, 1, 0.0F);
        this.boxList.add(segment);
      } 
    }
    
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
      for (ModelRenderer model : this.boxList) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(model.offsetX, model.offsetY, model.offsetZ);
        GlStateManager.translate(model.rotationPointX * f5, model.rotationPointY * f5, model.rotationPointZ * f5);
        GlStateManager.scale(1.0D, 0.1D, 1.0D);
        GlStateManager.translate(-model.offsetX, -model.offsetY, -model.offsetZ);
        GlStateManager.translate(-model.rotationPointX * f5, -model.rotationPointY * f5, -model.rotationPointZ * f5);
        model.render(f5);
        GlStateManager.popMatrix();
      } 
    }
    
    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
      modelRenderer.rotateAngleX = x;
      modelRenderer.rotateAngleY = y;
      modelRenderer.rotateAngleZ = z;
    }
  }
  
  public class ModelCloutGoggles extends ModelBase {
    public ModelRenderer leftGlass;
    
    public ModelRenderer topLeftFrame;
    
    public ModelRenderer bottomLeftFrame;
    
    public ModelRenderer leftLeftFrame;
    
    public ModelRenderer rightLeftFrame;
    
    public ModelRenderer rightGlass;
    
    public ModelRenderer topRightFrame;
    
    public ModelRenderer bottomLeftFrame_1;
    
    public ModelRenderer leftRightFrame;
    
    public ModelRenderer rightRightFrame;
    
    public ModelRenderer leftEar;
    
    public ModelRenderer rightEar;
    
    public ModelCloutGoggles() {
      this.textureWidth = 64;
      this.textureHeight = 32;
      this.rightLeftFrame = new ModelRenderer(this, 18, 0);
      this.rightLeftFrame.setRotationPoint(-3.0F, 3.0F, -4.0F);
      this.rightLeftFrame.addBox(0.0F, 2.0F, 0.0F, 2, 1, 1, 0.0F);
      this.bottomLeftFrame_1 = new ModelRenderer(this, 26, 5);
      this.bottomLeftFrame_1.setRotationPoint(-3.0F, 3.0F, -4.0F);
      this.bottomLeftFrame_1.addBox(4.0F, 2.0F, 0.0F, 2, 1, 1, 0.0F);
      this.leftLeftFrame = new ModelRenderer(this, 10, 5);
      this.leftLeftFrame.setRotationPoint(-3.0F, 3.0F, -4.0F);
      this.leftLeftFrame.addBox(2.0F, 0.0F, 0.0F, 1, 2, 1, 0.0F);
      this.rightGlass = new ModelRenderer(this, 18, 5);
      this.rightGlass.setRotationPoint(-3.0F, 3.0F, -4.0F);
      this.rightGlass.addBox(4.0F, 0.0F, 0.0F, 2, 2, 1, 0.0F);
      this.rightRightFrame = new ModelRenderer(this, 10, 11);
      this.rightRightFrame.setRotationPoint(3.0F, 3.0F, -4.0F);
      this.rightRightFrame.addBox(0.0F, 0.0F, 0.0F, 1, 2, 1, 0.0F);
      this.leftEar = new ModelRenderer(this, 18, 11);
      this.leftEar.setRotationPoint(-3.0F, 3.0F, -4.0F);
      this.leftEar.addBox(-1.2F, 0.0F, 0.0F, 1, 1, 3, 0.0F);
      this.topRightFrame = new ModelRenderer(this, 26, 0);
      this.topRightFrame.setRotationPoint(1.0F, 3.0F, -4.0F);
      this.topRightFrame.addBox(0.0F, -1.0F, 0.0F, 2, 1, 1, 0.0F);
      this.topLeftFrame = new ModelRenderer(this, 0, 5);
      this.topLeftFrame.setRotationPoint(-3.0F, 3.0F, -4.0F);
      this.topLeftFrame.addBox(-1.0F, 0.0F, 0.0F, 1, 2, 1, 0.0F);
      this.rightEar = new ModelRenderer(this, 28, 11);
      this.rightEar.setRotationPoint(-3.0F, 3.0F, -4.0F);
      this.rightEar.addBox(6.2F, 0.0F, 0.0F, 1, 1, 3, 0.0F);
      this.leftGlass = new ModelRenderer(this, 0, 0);
      this.leftGlass.setRotationPoint(-3.0F, 3.0F, -4.0F);
      this.leftGlass.addBox(0.0F, 0.0F, 0.0F, 2, 2, 1, 0.0F);
      this.bottomLeftFrame = new ModelRenderer(this, 10, 0);
      this.bottomLeftFrame.setRotationPoint(-3.0F, 3.0F, -4.0F);
      this.bottomLeftFrame.addBox(0.0F, -1.0F, 0.0F, 2, 1, 1, 0.0F);
      this.leftRightFrame = new ModelRenderer(this, 0, 11);
      this.leftRightFrame.setRotationPoint(-3.0F, 3.0F, -4.0F);
      this.leftRightFrame.addBox(3.0F, 0.0F, 0.0F, 1, 2, 1, 0.0F);
    }
    
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
      this.rightLeftFrame.render(f5);
      this.bottomLeftFrame_1.render(f5);
      this.leftLeftFrame.render(f5);
      this.rightGlass.render(f5);
      this.rightRightFrame.render(f5);
      this.leftEar.render(f5);
      this.topRightFrame.render(f5);
      this.topLeftFrame.render(f5);
      this.rightEar.render(f5);
      this.leftGlass.render(f5);
      this.bottomLeftFrame.render(f5);
      this.leftRightFrame.render(f5);
    }
    
    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
      modelRenderer.rotateAngleX = x;
      modelRenderer.rotateAngleY = y;
      modelRenderer.rotateAngleZ = z;
    }
  }
  
  public class ModelCosmetic extends ModelBase {
    public ResourceLocation texture;
  }
  
  public class ModelSquidLauncher extends ModelBase {
    public ModelRenderer barrel;
    
    public ModelRenderer squid;
    
    public ModelRenderer secondBarrel;
    
    public ModelRenderer barrelSide1;
    
    public ModelRenderer barrelSide2;
    
    public ModelRenderer barrelSide3;
    
    public ModelRenderer barrelSide4;
    
    public ModelRenderer stock;
    
    public ModelRenderer stockEnd;
    
    public ModelRenderer trigger;
    
    public ModelSquidLauncher() {
      this.textureWidth = 64;
      this.textureHeight = 32;
      this.barrelSide4 = new ModelRenderer(this, 0, 0);
      this.barrelSide4.setRotationPoint(0.5F, 0.0F, 0.0F);
      this.barrelSide4.addBox(0.0F, -2.0F, 0.2F, 4, 5, 1, 0.0F);
      setRotateAngle(this.barrelSide4, 0.091106184F, 0.0F, 0.0F);
      this.stock = new ModelRenderer(this, 0, 24);
      this.stock.setRotationPoint(0.0F, 0.0F, 0.0F);
      this.stock.addBox(1.5F, 3.0F, 1.5F, 2, 4, 2, 0.0F);
      this.squid = new ModelRenderer(this, 0, 16);
      this.squid.setRotationPoint(0.0F, 0.0F, 0.0F);
      this.squid.addBox(1.2F, -11.5F, 0.8F, 3, 4, 3, 0.0F);
      setRotateAngle(this.squid, 0.0F, -0.091106184F, 0.0F);
      this.barrelSide2 = new ModelRenderer(this, 18, 14);
      this.barrelSide2.setRotationPoint(0.0F, 0.0F, 0.0F);
      this.barrelSide2.addBox(3.8F, -2.5F, 0.5F, 1, 5, 4, 0.0F);
      setRotateAngle(this.barrelSide2, 0.0F, 0.0F, 0.091106184F);
      this.secondBarrel = new ModelRenderer(this, 32, 14);
      this.secondBarrel.setRotationPoint(0.0F, 0.0F, 0.0F);
      this.secondBarrel.addBox(0.5F, -2.0F, 0.5F, 4, 5, 4, 0.0F);
      this.stockEnd = new ModelRenderer(this, 18, 26);
      this.stockEnd.setRotationPoint(0.0F, 0.0F, 0.0F);
      this.stockEnd.addBox(2.0F, 7.0F, 1.5F, 1, 1, 4, 0.0F);
      this.barrelSide1 = new ModelRenderer(this, 18, 14);
      this.barrelSide1.setRotationPoint(0.0F, 0.0F, 0.0F);
      this.barrelSide1.addBox(0.2F, -2.0F, 0.5F, 1, 5, 4, 0.0F);
      setRotateAngle(this.barrelSide1, 0.0F, 0.0F, -0.091106184F);
      this.barrelSide3 = new ModelRenderer(this, 0, 0);
      this.barrelSide3.setRotationPoint(0.0F, 0.0F, 0.0F);
      this.barrelSide3.addBox(0.5F, -2.5F, 3.8F, 4, 5, 1, 0.0F);
      setRotateAngle(this.barrelSide3, -0.091106184F, 0.0F, 0.0F);
      this.trigger = new ModelRenderer(this, 40, 0);
      this.trigger.setRotationPoint(0.0F, 0.0F, 0.0F);
      this.trigger.addBox(12.0F, 6.6F, 5.4F, 1, 1, 1, 0.0F);
      this.barrel = new ModelRenderer(this, 18, 0);
      this.barrel.setRotationPoint(0.0F, 0.0F, 0.0F);
      this.barrel.addBox(0.0F, -8.0F, 0.0F, 5, 6, 5, 0.0F);
      this.boxList.add(this.barrel);
      this.boxList.add(this.squid);
      this.boxList.add(this.secondBarrel);
      this.boxList.add(this.barrelSide1);
      this.boxList.add(this.barrelSide2);
      this.boxList.add(this.barrelSide3);
      this.boxList.add(this.barrelSide4);
      this.boxList.add(this.stock);
      this.boxList.add(this.stockEnd);
      this.boxList.add(this.trigger);
    }
    
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
      this.stock.render(f5);
      this.barrelSide1.render(f5);
      this.stockEnd.render(f5);
      this.secondBarrel.render(f5);
      this.barrelSide3.render(f5);
      this.squid.render(f5);
      this.barrelSide4.render(f5);
      this.barrel.render(f5);
      this.barrelSide2.render(f5);
      GlStateManager.pushMatrix();
      GlStateManager.translate(this.trigger.offsetX, this.trigger.offsetY, this.trigger.offsetZ);
      GlStateManager.translate(this.trigger.rotationPointX * f5, this.trigger.rotationPointY * f5, this.trigger.rotationPointZ * f5);
      GlStateManager.scale(0.2D, 1.0D, 0.8D);
      GlStateManager.translate(-this.trigger.offsetX, -this.trigger.offsetY, -this.trigger.offsetZ);
      GlStateManager.translate(-this.trigger.rotationPointX * f5, -this.trigger.rotationPointY * f5, -this.trigger.rotationPointZ * f5);
      this.trigger.render(f5);
      GlStateManager.popMatrix();
    }
    
    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
      modelRenderer.rotateAngleX = x;
      modelRenderer.rotateAngleY = y;
      modelRenderer.rotateAngleZ = z;
    }
  }
  
  public class ModelSquidFlag extends ModelBase {
    public ModelRenderer flag;
    
    public ModelSquidFlag() {
      this.textureWidth = 64;
      this.textureHeight = 32;
      this.flag = new ModelRenderer(this, 0, 0);
      this.flag.setRotationPoint(0.0F, 0.0F, 0.0F);
      this.flag.addBox(-5.0F, -16.0F, 0.0F, 10, 16, 1, 0.0F);
    }
    
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
      this.flag.render(f5);
    }
    
    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
      modelRenderer.rotateAngleX = x;
      modelRenderer.rotateAngleY = y;
      modelRenderer.rotateAngleZ = z;
    }
  }
  
  public class ModelPhyscisCapes extends ModelBase {
    public ModelRenderer shape1;
    
    public ModelRenderer shape2;
    
    public ModelRenderer shape3;
    
    public ModelRenderer shape4;
    
    public ModelRenderer shape5;
    
    public ModelRenderer shape6;
    
    public ModelRenderer shape7;
    
    public ModelRenderer shape8;
    
    public ModelRenderer shape9;
    
    public ModelRenderer shape10;
    
    public ModelRenderer shape11;
    
    public ModelRenderer shape12;
    
    public ModelRenderer shape13;
    
    public ModelRenderer shape14;
    
    public ModelRenderer shape15;
    
    public ModelRenderer shape16;
    
    public ModelPhyscisCapes() {
      this.textureWidth = 64;
      this.textureHeight = 32;
      this.shape9 = new ModelRenderer(this, 0, 8);
      this.shape9.setRotationPoint(-5.0F, 8.0F, -1.0F);
      this.shape9.addBox(0.0F, 0.0F, 0.0F, 10, 1, 1, 0.0F);
      this.shape15 = new ModelRenderer(this, 0, 14);
      this.shape15.setRotationPoint(-5.0F, 14.0F, -1.0F);
      this.shape15.addBox(0.0F, 0.0F, 0.0F, 10, 1, 1, 0.0F);
      this.shape3 = new ModelRenderer(this, 0, 2);
      this.shape3.setRotationPoint(-5.0F, 2.0F, -1.0F);
      this.shape3.addBox(0.0F, 0.0F, 0.0F, 10, 1, 1, 0.0F);
      this.shape7 = new ModelRenderer(this, 0, 6);
      this.shape7.setRotationPoint(-5.0F, 6.0F, -1.0F);
      this.shape7.addBox(0.0F, 0.0F, 0.0F, 10, 1, 1, 0.0F);
      this.shape1 = new ModelRenderer(this, 0, 0);
      this.shape1.setRotationPoint(-5.0F, 0.0F, -1.0F);
      this.shape1.addBox(0.0F, 0.0F, 0.0F, 10, 1, 1, 0.0F);
      this.shape6 = new ModelRenderer(this, 0, 5);
      this.shape6.setRotationPoint(-5.0F, 5.0F, -1.0F);
      this.shape6.addBox(0.0F, 0.0F, 0.0F, 10, 1, 1, 0.0F);
      this.shape14 = new ModelRenderer(this, 0, 13);
      this.shape14.setRotationPoint(-5.0F, 13.0F, -1.0F);
      this.shape14.addBox(0.0F, 0.0F, 0.0F, 10, 1, 1, 0.0F);
      this.shape10 = new ModelRenderer(this, 0, 9);
      this.shape10.setRotationPoint(-5.0F, 9.0F, -1.0F);
      this.shape10.addBox(0.0F, 0.0F, 0.0F, 10, 1, 1, 0.0F);
      this.shape13 = new ModelRenderer(this, 0, 12);
      this.shape13.setRotationPoint(-5.0F, 12.0F, -1.0F);
      this.shape13.addBox(0.0F, 0.0F, 0.0F, 10, 1, 1, 0.0F);
      this.shape4 = new ModelRenderer(this, 0, 3);
      this.shape4.setRotationPoint(-5.0F, 3.0F, -1.0F);
      this.shape4.addBox(0.0F, 0.0F, 0.0F, 10, 1, 1, 0.0F);
      this.shape8 = new ModelRenderer(this, 0, 7);
      this.shape8.setRotationPoint(-5.0F, 7.0F, -1.0F);
      this.shape8.addBox(0.0F, 0.0F, 0.0F, 10, 1, 1, 0.0F);
      this.shape16 = new ModelRenderer(this, 0, 15);
      this.shape16.setRotationPoint(-5.0F, 15.0F, -1.0F);
      this.shape16.addBox(0.0F, 0.0F, 0.0F, 10, 1, 1, 0.0F);
      this.shape12 = new ModelRenderer(this, 0, 11);
      this.shape12.setRotationPoint(-5.0F, 11.0F, -1.0F);
      this.shape12.addBox(0.0F, 0.0F, 0.0F, 10, 1, 1, 0.0F);
      this.shape5 = new ModelRenderer(this, 0, 4);
      this.shape5.setRotationPoint(-5.0F, 4.0F, -1.0F);
      this.shape5.addBox(0.0F, 0.0F, 0.0F, 10, 1, 1, 0.0F);
      this.shape11 = new ModelRenderer(this, 0, 10);
      this.shape11.setRotationPoint(-5.0F, 10.0F, -1.0F);
      this.shape11.addBox(0.0F, 0.0F, 0.0F, 10, 1, 1, 0.0F);
      this.shape2 = new ModelRenderer(this, 0, 1);
      this.shape2.setRotationPoint(-5.0F, 1.0F, -1.0F);
      this.shape2.addBox(0.0F, 0.0F, 0.0F, 10, 1, 1, 0.0F);
      this.boxList.add(this.shape1);
      this.boxList.add(this.shape2);
      this.boxList.add(this.shape3);
      this.boxList.add(this.shape4);
      this.boxList.add(this.shape5);
      this.boxList.add(this.shape6);
      this.boxList.add(this.shape7);
      this.boxList.add(this.shape8);
      this.boxList.add(this.shape9);
      this.boxList.add(this.shape10);
      this.boxList.add(this.shape11);
      this.boxList.add(this.shape12);
      this.boxList.add(this.shape13);
      this.boxList.add(this.shape14);
      this.boxList.add(this.shape15);
      this.boxList.add(this.shape16);
    }
    
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
      this.shape9.render(f5);
      this.shape15.render(f5);
      this.shape3.render(f5);
      this.shape7.render(f5);
      this.shape1.render(f5);
      this.shape6.render(f5);
      this.shape14.render(f5);
      this.shape10.render(f5);
      this.shape13.render(f5);
      this.shape4.render(f5);
      this.shape8.render(f5);
      this.shape16.render(f5);
      this.shape12.render(f5);
      this.shape5.render(f5);
      this.shape11.render(f5);
      this.shape2.render(f5);
    }
    
    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
      modelRenderer.rotateAngleX = x;
      modelRenderer.rotateAngleY = y;
      modelRenderer.rotateAngleZ = z;
    }
  }
  
  public class SantaHatModel extends ModelBase {
    public ModelRenderer baseLayer;
    
    public ModelRenderer baseRedLayer;
    
    public ModelRenderer midRedLayer;
    
    public ModelRenderer topRedLayer;
    
    public ModelRenderer lastRedLayer;
    
    public ModelRenderer realFinalLastLayer;
    
    public ModelRenderer whiteLayer;
    
    public SantaHatModel() {
      this.textureWidth = 64;
      this.textureHeight = 32;
      this.topRedLayer = new ModelRenderer(this, 46, 0);
      this.topRedLayer.setRotationPoint(0.5F, -8.4F, -1.5F);
      this.topRedLayer.addBox(0.0F, 0.0F, 0.0F, 3, 2, 3, 0.0F);
      setRotateAngle(this.topRedLayer, 0.0F, 0.0F, 0.5009095F);
      this.baseLayer = new ModelRenderer(this, 0, 0);
      this.baseLayer.setRotationPoint(-4.0F, -1.0F, -4.0F);
      this.baseLayer.addBox(0.0F, 0.0F, 0.0F, 8, 2, 8, 0.0F);
      this.midRedLayer = new ModelRenderer(this, 28, 0);
      this.midRedLayer.setRotationPoint(-1.2F, -6.8F, -2.0F);
      this.midRedLayer.addBox(0.0F, 0.0F, 0.0F, 4, 3, 4, 0.0F);
      setRotateAngle(this.midRedLayer, 0.0F, 0.0F, 0.22759093F);
      this.realFinalLastLayer = new ModelRenderer(this, 46, 8);
      this.realFinalLastLayer.setRotationPoint(4.0F, -10.4F, 0.0F);
      this.realFinalLastLayer.addBox(0.0F, 0.0F, 0.0F, 1, 3, 1, 0.0F);
      setRotateAngle(this.realFinalLastLayer, 0.0F, 0.0F, 1.0016445F);
      this.lastRedLayer = new ModelRenderer(this, 34, 8);
      this.lastRedLayer.setRotationPoint(2.0F, -9.4F, 0.0F);
      this.lastRedLayer.addBox(0.0F, 0.0F, 0.0F, 2, 2, 2, 0.0F);
      setRotateAngle(this.lastRedLayer, 0.0F, 0.0F, 0.8196066F);
      this.whiteLayer = new ModelRenderer(this, 0, 22);
      this.whiteLayer.setRotationPoint(4.1F, -9.7F, -0.5F);
      this.whiteLayer.addBox(0.0F, 0.0F, 0.0F, 2, 2, 2, 0.0F);
      setRotateAngle(this.whiteLayer, -0.091106184F, 0.0F, 0.18203785F);
      this.baseRedLayer = new ModelRenderer(this, 0, 11);
      this.baseRedLayer.setRotationPoint(-3.0F, -4.0F, -3.0F);
      this.baseRedLayer.addBox(0.0F, 0.0F, 0.0F, 6, 3, 6, 0.0F);
      setRotateAngle(this.baseRedLayer, 0.0F, 0.0F, 0.045553092F);
    }
    
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
      this.topRedLayer.render(f5);
      this.baseLayer.render(f5);
      this.midRedLayer.render(f5);
      this.realFinalLastLayer.render(f5);
      this.lastRedLayer.render(f5);
      this.whiteLayer.render(f5);
      this.baseRedLayer.render(f5);
    }
    
    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
      modelRenderer.rotateAngleX = x;
      modelRenderer.rotateAngleY = y;
      modelRenderer.rotateAngleZ = z;
    }
  }
  
  public class HatGlassesModel extends ModelBase {
    public final ResourceLocation glassesTexture = new ResourceLocation("textures/sunglasses.png");
    
    public ModelRenderer firstLeftFrame;
    
    public ModelRenderer firstRightFrame;
    
    public ModelRenderer centerBar;
    
    public ModelRenderer farLeftBar;
    
    public ModelRenderer farRightBar;
    
    public ModelRenderer leftEar;
    
    public ModelRenderer rightEar;
    
    public HatGlassesModel() {
      this.textureWidth = 64;
      this.textureHeight = 64;
      this.farLeftBar = new ModelRenderer(this, 0, 13);
      this.farLeftBar.setRotationPoint(-4.0F, 3.5F, -5.0F);
      this.farLeftBar.addBox(0.0F, 0.0F, 0.0F, 1, 1, 1, 0.0F);
      this.rightEar = new ModelRenderer(this, 10, 0);
      this.rightEar.setRotationPoint(3.2F, 3.5F, -5.0F);
      this.rightEar.addBox(0.0F, 0.0F, 0.0F, 1, 1, 3, 0.0F);
      this.centerBar = new ModelRenderer(this, 0, 9);
      this.centerBar.setRotationPoint(-1.0F, 3.5F, -5.0F);
      this.centerBar.addBox(0.0F, 0.0F, 0.0F, 2, 1, 1, 0.0F);
      this.firstLeftFrame = new ModelRenderer(this, 0, 0);
      this.firstLeftFrame.setRotationPoint(-3.0F, 3.0F, -5.0F);
      this.firstLeftFrame.addBox(0.0F, 0.0F, 0.0F, 2, 2, 1, 0.0F);
      this.firstRightFrame = new ModelRenderer(this, 0, 5);
      this.firstRightFrame.setRotationPoint(1.0F, 3.0F, -5.0F);
      this.firstRightFrame.addBox(0.0F, 0.0F, 0.0F, 2, 2, 1, 0.0F);
      this.leftEar = new ModelRenderer(this, 20, 0);
      this.leftEar.setRotationPoint(-4.2F, 3.5F, -5.0F);
      this.leftEar.addBox(0.0F, 0.0F, 0.0F, 1, 1, 3, 0.0F);
      this.farRightBar = new ModelRenderer(this, 0, 17);
      this.farRightBar.setRotationPoint(3.0F, 3.5F, -5.0F);
      this.farRightBar.addBox(0.0F, 0.0F, 0.0F, 1, 1, 1, 0.0F);
    }
    
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
      this.farLeftBar.render(f5);
      this.rightEar.render(f5);
      this.centerBar.render(f5);
      this.firstLeftFrame.render(f5);
      this.firstRightFrame.render(f5);
      this.leftEar.render(f5);
      this.farRightBar.render(f5);
    }
    
    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
      modelRenderer.rotateAngleX = x;
      modelRenderer.rotateAngleY = y;
      modelRenderer.rotateAngleZ = z;
    }
  }
  
  public class GlassesModel extends ModelBase {
    public final ResourceLocation glassesTexture = new ResourceLocation("textures/sunglasses.png");
    
    public ModelRenderer firstLeftFrame;
    
    public ModelRenderer firstRightFrame;
    
    public ModelRenderer centerBar;
    
    public ModelRenderer farLeftBar;
    
    public ModelRenderer farRightBar;
    
    public ModelRenderer leftEar;
    
    public ModelRenderer rightEar;
    
    public GlassesModel() {
      this.textureWidth = 64;
      this.textureHeight = 64;
      this.farLeftBar = new ModelRenderer(this, 0, 13);
      this.farLeftBar.setRotationPoint(-4.0F, 3.5F, -4.0F);
      this.farLeftBar.addBox(0.0F, 0.0F, 0.0F, 1, 1, 1, 0.0F);
      this.rightEar = new ModelRenderer(this, 10, 0);
      this.rightEar.setRotationPoint(3.2F, 3.5F, -4.0F);
      this.rightEar.addBox(0.0F, 0.0F, 0.0F, 1, 1, 3, 0.0F);
      this.centerBar = new ModelRenderer(this, 0, 9);
      this.centerBar.setRotationPoint(-1.0F, 3.5F, -4.0F);
      this.centerBar.addBox(0.0F, 0.0F, 0.0F, 2, 1, 1, 0.0F);
      this.firstLeftFrame = new ModelRenderer(this, 0, 0);
      this.firstLeftFrame.setRotationPoint(-3.0F, 3.0F, -4.0F);
      this.firstLeftFrame.addBox(0.0F, 0.0F, 0.0F, 2, 2, 1, 0.0F);
      this.firstRightFrame = new ModelRenderer(this, 0, 5);
      this.firstRightFrame.setRotationPoint(1.0F, 3.0F, -4.0F);
      this.firstRightFrame.addBox(0.0F, 0.0F, 0.0F, 2, 2, 1, 0.0F);
      this.leftEar = new ModelRenderer(this, 20, 0);
      this.leftEar.setRotationPoint(-4.2F, 3.5F, -4.0F);
      this.leftEar.addBox(0.0F, 0.0F, 0.0F, 1, 1, 3, 0.0F);
      this.farRightBar = new ModelRenderer(this, 0, 17);
      this.farRightBar.setRotationPoint(3.0F, 3.5F, -4.0F);
      this.farRightBar.addBox(0.0F, 0.0F, 0.0F, 1, 1, 1, 0.0F);
    }
    
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
      this.farLeftBar.render(f5);
      this.rightEar.render(f5);
      this.centerBar.render(f5);
      this.firstLeftFrame.render(f5);
      this.firstRightFrame.render(f5);
      this.leftEar.render(f5);
      this.farRightBar.render(f5);
    }
    
    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
      modelRenderer.rotateAngleX = x;
      modelRenderer.rotateAngleY = y;
      modelRenderer.rotateAngleZ = z;
    }
  }
  
  public class TopHatModel extends ModelBase {
    public final ResourceLocation hatTexture = new ResourceLocation("textures/tophat.png");
    
    public ModelRenderer bottom;
    
    public ModelRenderer top;
    
    public TopHatModel() {
      this.textureWidth = 64;
      this.textureHeight = 32;
      this.top = new ModelRenderer(this, 0, 10);
      this.top.addBox(0.0F, 0.0F, 0.0F, 4, 10, 4, 0.0F);
      this.top.setRotationPoint(-2.0F, -11.0F, -2.0F);
      this.bottom = new ModelRenderer(this, 0, 0);
      this.bottom.addBox(0.0F, 0.0F, 0.0F, 8, 1, 8, 0.0F);
      this.bottom.setRotationPoint(-4.0F, -1.0F, -4.0F);
    }
    
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
      this.top.render(f5);
      this.bottom.render(f5);
    }
    
    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
      modelRenderer.rotateAngleX = x;
      modelRenderer.rotateAngleY = y;
      modelRenderer.rotateAngleZ = z;
    }
  }
}
