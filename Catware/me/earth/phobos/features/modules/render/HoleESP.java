package me.earth.phobos.features.modules.render;

import java.awt.Color;
import java.util.Random;
import me.earth.phobos.Phobos;
import me.earth.phobos.event.events.Render3DEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.RenderUtil;
import me.earth.phobos.util.RotationUtil;
import net.minecraft.util.math.BlockPos;

public class HoleESP extends Module {
  private static HoleESP INSTANCE = new HoleESP();
  
  public Setting<Boolean> ownHole = register(new Setting("OwnHole", Boolean.valueOf(false)));
  
  public Setting<Boolean> box = register(new Setting("Box", Boolean.valueOf(true)));
  
  public Setting<Boolean> gradientBox = register(new Setting("GradientBox", Boolean.valueOf(false), v -> ((Boolean)this.box.getValue()).booleanValue()));
  
  public Setting<Boolean> pulseAlpha = register(new Setting("PulseAlpha", Boolean.valueOf(false), v -> ((Boolean)this.gradientBox.getValue()).booleanValue()));
  
  public Setting<Boolean> pulseOutline = register(new Setting("PulseOutline", Boolean.valueOf(true), v -> ((Boolean)this.gradientBox.getValue()).booleanValue()));
  
  public Setting<Boolean> invertGradientBox = register(new Setting("InvertGradientBox", Boolean.valueOf(false), v -> ((Boolean)this.gradientBox.getValue()).booleanValue()));
  
  public Setting<Boolean> outline = register(new Setting("Outline", Boolean.valueOf(true)));
  
  public Setting<Boolean> gradientOutline = register(new Setting("GradientOutline", Boolean.valueOf(false), v -> ((Boolean)this.outline.getValue()).booleanValue()));
  
  public Setting<Boolean> invertGradientOutline = register(new Setting("InvertGradientOutline", Boolean.valueOf(false), v -> ((Boolean)this.gradientOutline.getValue()).booleanValue()));
  
  public Setting<Double> height = register(new Setting("Height", Double.valueOf(0.0D), Double.valueOf(-2.0D), Double.valueOf(2.0D)));
  
  public Setting<Boolean> safeColor = register(new Setting("SafeColor", Boolean.valueOf(false)));
  
  public Setting<Boolean> customOutline = register(new Setting("CustomLine", Boolean.valueOf(false), v -> ((Boolean)this.outline.getValue()).booleanValue()));
  
  private final Setting<Integer> holes = register(new Setting("Holes", Integer.valueOf(3), Integer.valueOf(1), Integer.valueOf(500)));
  
  private final Setting<Integer> minPulseAlpha = register(new Setting("MinPulse", Integer.valueOf(10), Integer.valueOf(0), Integer.valueOf(255), v -> ((Boolean)this.pulseAlpha.getValue()).booleanValue()));
  
  private final Setting<Integer> maxPulseAlpha = register(new Setting("MaxPulse", Integer.valueOf(40), Integer.valueOf(0), Integer.valueOf(255), v -> ((Boolean)this.pulseAlpha.getValue()).booleanValue()));
  
  private final Setting<Integer> pulseSpeed = register(new Setting("PulseSpeed", Integer.valueOf(10), Integer.valueOf(1), Integer.valueOf(50), v -> ((Boolean)this.pulseAlpha.getValue()).booleanValue()));
  
  private final Setting<Integer> red = register(new Setting("Red", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255)));
  
  private final Setting<Integer> green = register(new Setting("Green", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255)));
  
  private final Setting<Integer> blue = register(new Setting("Blue", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255)));
  
  private final Setting<Integer> alpha = register(new Setting("Alpha", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255)));
  
  private final Setting<Integer> boxAlpha = register(new Setting("BoxAlpha", Integer.valueOf(125), Integer.valueOf(0), Integer.valueOf(255), v -> ((Boolean)this.box.getValue()).booleanValue()));
  
  private final Setting<Float> lineWidth = register(new Setting("LineWidth", Float.valueOf(1.0F), Float.valueOf(0.1F), Float.valueOf(5.0F), v -> ((Boolean)this.outline.getValue()).booleanValue()));
  
  private final Setting<Integer> safeRed = register(new Setting("SafeRed", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> ((Boolean)this.safeColor.getValue()).booleanValue()));
  
  private final Setting<Integer> safeGreen = register(new Setting("SafeGreen", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> ((Boolean)this.safeColor.getValue()).booleanValue()));
  
  private final Setting<Integer> safeBlue = register(new Setting("SafeBlue", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> ((Boolean)this.safeColor.getValue()).booleanValue()));
  
  private final Setting<Integer> safeAlpha = register(new Setting("SafeAlpha", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> ((Boolean)this.safeColor.getValue()).booleanValue()));
  
  private final Setting<Integer> cRed = register(new Setting("OL-Red", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> (((Boolean)this.customOutline.getValue()).booleanValue() && ((Boolean)this.outline.getValue()).booleanValue())));
  
  private final Setting<Integer> cGreen = register(new Setting("OL-Green", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> (((Boolean)this.customOutline.getValue()).booleanValue() && ((Boolean)this.outline.getValue()).booleanValue())));
  
  private final Setting<Integer> cBlue = register(new Setting("OL-Blue", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> (((Boolean)this.customOutline.getValue()).booleanValue() && ((Boolean)this.outline.getValue()).booleanValue())));
  
  private final Setting<Integer> cAlpha = register(new Setting("OL-Alpha", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> (((Boolean)this.customOutline.getValue()).booleanValue() && ((Boolean)this.outline.getValue()).booleanValue())));
  
  private final Setting<Integer> safecRed = register(new Setting("OL-SafeRed", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> (((Boolean)this.customOutline.getValue()).booleanValue() && ((Boolean)this.outline.getValue()).booleanValue() && ((Boolean)this.safeColor.getValue()).booleanValue())));
  
  private final Setting<Integer> safecGreen = register(new Setting("OL-SafeGreen", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> (((Boolean)this.customOutline.getValue()).booleanValue() && ((Boolean)this.outline.getValue()).booleanValue() && ((Boolean)this.safeColor.getValue()).booleanValue())));
  
  private final Setting<Integer> safecBlue = register(new Setting("OL-SafeBlue", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> (((Boolean)this.customOutline.getValue()).booleanValue() && ((Boolean)this.outline.getValue()).booleanValue() && ((Boolean)this.safeColor.getValue()).booleanValue())));
  
  private final Setting<Integer> safecAlpha = register(new Setting("OL-SafeAlpha", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> (((Boolean)this.customOutline.getValue()).booleanValue() && ((Boolean)this.outline.getValue()).booleanValue() && ((Boolean)this.safeColor.getValue()).booleanValue())));
  
  private boolean pulsing = false;
  
  private boolean shouldDecrease = false;
  
  private int pulseDelay = 0;
  
  private int currentPulseAlpha;
  
  private int currentAlpha = 0;
  
  public HoleESP() {
    super("HoleESP", "Shows safe spots.", Module.Category.RENDER, false, false, false);
    setInstance();
  }
  
  public static HoleESP getInstance() {
    if (INSTANCE == null)
      INSTANCE = new HoleESP(); 
    return INSTANCE;
  }
  
  private void setInstance() {
    INSTANCE = this;
  }
  
  public void onRender3D(Render3DEvent event) {
    int drawnHoles = 0;
    if (!this.pulsing && ((Boolean)this.pulseAlpha.getValue()).booleanValue()) {
      Random rand = new Random();
      this.currentPulseAlpha = rand.nextInt(((Integer)this.maxPulseAlpha.getValue()).intValue() - ((Integer)this.minPulseAlpha.getValue()).intValue() + 1) + ((Integer)this.minPulseAlpha.getValue()).intValue();
      this.pulsing = true;
      this.shouldDecrease = false;
    } 
    if (this.pulseDelay == 0) {
      if (this.pulsing && ((Boolean)this.pulseAlpha.getValue()).booleanValue() && !this.shouldDecrease) {
        this.currentAlpha++;
        if (this.currentAlpha >= this.currentPulseAlpha)
          this.shouldDecrease = true; 
      } 
      if (this.pulsing && ((Boolean)this.pulseAlpha.getValue()).booleanValue() && this.shouldDecrease)
        this.currentAlpha--; 
      if (this.currentAlpha <= 0) {
        this.pulsing = false;
        this.shouldDecrease = false;
      } 
      this.pulseDelay++;
    } else {
      this.pulseDelay++;
      if (this.pulseDelay == 51 - ((Integer)this.pulseSpeed.getValue()).intValue())
        this.pulseDelay = 0; 
    } 
    if (!((Boolean)this.pulseAlpha.getValue()).booleanValue() || !this.pulsing)
      this.currentAlpha = 0; 
    for (BlockPos pos : Phobos.holeManager.getSortedHoles()) {
      if (drawnHoles >= ((Integer)this.holes.getValue()).intValue())
        break; 
      if ((pos.equals(new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ)) && !((Boolean)this.ownHole.getValue()).booleanValue()) || !RotationUtil.isInFov(pos))
        continue; 
      if (((Boolean)this.safeColor.getValue()).booleanValue() && Phobos.holeManager.isSafe(pos)) {
        RenderUtil.drawBoxESP(pos, new Color(((Integer)this.safeRed.getValue()).intValue(), ((Integer)this.safeGreen.getValue()).intValue(), ((Integer)this.safeBlue.getValue()).intValue(), ((Integer)this.safeAlpha.getValue()).intValue()), ((Boolean)this.customOutline.getValue()).booleanValue(), new Color(((Integer)this.safecRed.getValue()).intValue(), ((Integer)this.safecGreen.getValue()).intValue(), ((Integer)this.safecBlue.getValue()).intValue(), ((Integer)this.safecAlpha.getValue()).intValue()), ((Float)this.lineWidth.getValue()).floatValue(), ((Boolean)this.outline.getValue()).booleanValue(), ((Boolean)this.box.getValue()).booleanValue(), ((Integer)this.boxAlpha.getValue()).intValue(), true, ((Double)this.height.getValue()).doubleValue(), ((Boolean)this.gradientBox.getValue()).booleanValue(), ((Boolean)this.gradientOutline.getValue()).booleanValue(), ((Boolean)this.invertGradientBox.getValue()).booleanValue(), ((Boolean)this.invertGradientOutline.getValue()).booleanValue(), this.currentAlpha);
      } else {
        RenderUtil.drawBoxESP(pos, new Color(((Integer)this.red.getValue()).intValue(), ((Integer)this.green.getValue()).intValue(), ((Integer)this.blue.getValue()).intValue(), ((Integer)this.alpha.getValue()).intValue()), ((Boolean)this.customOutline.getValue()).booleanValue(), new Color(((Integer)this.cRed.getValue()).intValue(), ((Integer)this.cGreen.getValue()).intValue(), ((Integer)this.cBlue.getValue()).intValue(), ((Integer)this.cAlpha.getValue()).intValue()), ((Float)this.lineWidth.getValue()).floatValue(), ((Boolean)this.outline.getValue()).booleanValue(), ((Boolean)this.box.getValue()).booleanValue(), ((Integer)this.boxAlpha.getValue()).intValue(), true, ((Double)this.height.getValue()).doubleValue(), ((Boolean)this.gradientBox.getValue()).booleanValue(), ((Boolean)this.gradientOutline.getValue()).booleanValue(), ((Boolean)this.invertGradientBox.getValue()).booleanValue(), ((Boolean)this.invertGradientOutline.getValue()).booleanValue(), this.currentAlpha);
      } 
      drawnHoles++;
    } 
  }
}
