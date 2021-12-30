package me.earth.phobos.features.modules.client;

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import me.earth.phobos.Phobos;
import me.earth.phobos.event.events.ClientEvent;
import me.earth.phobos.event.events.Render2DEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.modules.misc.ToolTips;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.manager.TextManager;
import me.earth.phobos.util.ColorUtil;
import me.earth.phobos.util.EntityUtil;
import me.earth.phobos.util.MathUtil;
import me.earth.phobos.util.RenderUtil;
import me.earth.phobos.util.Timer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class HUD extends Module {
  private static final ResourceLocation box = new ResourceLocation("textures/gui/container/shulker_box.png");
  
  private static final ItemStack totem = new ItemStack(Items.TOTEM_OF_UNDYING);
  
  private static final ResourceLocation codHitmarker = new ResourceLocation("earthhack", "cod_hitmarker");
  
  public static final SoundEvent COD_EVENT = new SoundEvent(codHitmarker);
  
  private static final ResourceLocation csgoHitmarker = new ResourceLocation("earthhack", "csgo_hitmarker");
  
  public static final SoundEvent CSGO_EVENT = new SoundEvent(csgoHitmarker);
  
  private static HUD INSTANCE = new HUD();
  
  private final Setting<Boolean> renderingUp = register(new Setting("RenderingUp", Boolean.valueOf(false), "Orientation of the HUD-Elements."));
  
  private final Setting<WaterMark> watermark = register(new Setting("Logo", WaterMark.NONE, "WaterMark"));
  
  private final Setting<String> customWatermark = register(new Setting("WatermarkName", "catware on top!"));
  
  private final Setting<Boolean> modeVer = register(new Setting("Version", Boolean.valueOf(false), v -> (this.watermark.getValue() != WaterMark.NONE)));
  
  private final Setting<Boolean> arrayList = register(new Setting("ActiveModules", Boolean.valueOf(false), "Lists the active modules."));
  
  private final Setting<Boolean> moduleColors = register(new Setting("ModuleColors", Boolean.valueOf(false), v -> ((Boolean)this.arrayList.getValue()).booleanValue()));
  
  private final Setting<Boolean> alphabeticalSorting = register(new Setting("AlphabeticalSorting", Boolean.valueOf(false), v -> ((Boolean)this.arrayList.getValue()).booleanValue()));
  
  private final Setting<Boolean> serverBrand = register(new Setting("ServerBrand", Boolean.valueOf(false), "Brand of the server you are on."));
  
  private final Setting<Boolean> ping = register(new Setting("Ping", Boolean.valueOf(false), "Your response time to the server."));
  
  private final Setting<Boolean> tps = register(new Setting("TPS", Boolean.valueOf(false), "Ticks per second of the server."));
  
  private final Setting<Boolean> fps = register(new Setting("FPS", Boolean.valueOf(false), "Your frames per second."));
  
  private final Setting<Boolean> coords = register(new Setting("Coords", Boolean.valueOf(false), "Your current coordinates"));
  
  private final Setting<Boolean> direction = register(new Setting("Direction", Boolean.valueOf(false), "The Direction you are facing."));
  
  private final Setting<Boolean> speed = register(new Setting("Speed", Boolean.valueOf(false), "Your Speed"));
  
  private final Setting<Boolean> potions = register(new Setting("Potions", Boolean.valueOf(false), "Active potion effects"));
  
  private final Setting<Boolean> altPotionsColors = register(new Setting("AltPotionColors", Boolean.valueOf(false), v -> ((Boolean)this.potions.getValue()).booleanValue()));
  
  private final Setting<Boolean> armor = register(new Setting("Armor", Boolean.valueOf(false), "ArmorHUD"));
  
  private final Setting<Boolean> durability = register(new Setting("Durability", Boolean.valueOf(false), "Durability"));
  
  private final Setting<Boolean> percent = register(new Setting("Percent", Boolean.valueOf(true), v -> ((Boolean)this.armor.getValue()).booleanValue()));
  
  private final Setting<Boolean> totems = register(new Setting("Totems", Boolean.valueOf(false), "TotemHUD"));
  
  private final Setting<Boolean> queue = register(new Setting("2b2tQueue", Boolean.valueOf(false), "Shows the 2b2t queue."));
  
  private final Setting<Greeter> greeter = register(new Setting("Greeter", Greeter.NONE, "Greets you."));
  
  private final Setting<String> spoofGreeter = register(new Setting("GreeterName", "Cat On Top!", v -> (this.greeter.getValue() == Greeter.CUSTOM)));
  
  private final Setting<LagNotify> lag = register(new Setting("Lag", LagNotify.GRAY, "Lag Notifier"));
  
  private final Setting<Boolean> hitMarkers = register(new Setting("HitMarkers", Boolean.valueOf(true)));
  
  private final Setting<Sound> sound = register(new Setting("Sound", Sound.NONE, v -> ((Boolean)this.hitMarkers.getValue()).booleanValue()));
  
  private final Setting<Boolean> grayNess = register(new Setting("FutureColour", Boolean.valueOf(true)));
  
  private final Timer timer = new Timer();
  
  private final Timer moduleTimer = new Timer();
  
  public Setting<Boolean> colorSync = register(new Setting("Sync", Boolean.valueOf(false), "Universal colors for hud."));
  
  public Setting<Boolean> rainbow = register(new Setting("Rainbow", Boolean.valueOf(false), "Rainbow hud."));
  
  public Setting<Integer> factor = register(new Setting("Factor", Integer.valueOf(1), Integer.valueOf(0), Integer.valueOf(20), v -> ((Boolean)this.rainbow.getValue()).booleanValue()));
  
  public Setting<Boolean> rolling = register(new Setting("Rolling", Boolean.valueOf(false), v -> ((Boolean)this.rainbow.getValue()).booleanValue()));
  
  public Setting<Boolean> staticRainbow = register(new Setting("Static", Boolean.valueOf(false), v -> ((Boolean)this.rainbow.getValue()).booleanValue()));
  
  public Setting<Integer> rainbowSpeed = register(new Setting("RSpeed", Integer.valueOf(20), Integer.valueOf(0), Integer.valueOf(100), v -> ((Boolean)this.rainbow.getValue()).booleanValue()));
  
  public Setting<Integer> rainbowSaturation = register(new Setting("Saturation", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> ((Boolean)this.rainbow.getValue()).booleanValue()));
  
  public Setting<Integer> rainbowBrightness = register(new Setting("Brightness", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> ((Boolean)this.rainbow.getValue()).booleanValue()));
  
  public Setting<Boolean> potionIcons = register(new Setting("PotionIcons", Boolean.valueOf(true), "Draws Potion Icons."));
  
  public Setting<Boolean> shadow = register(new Setting("Shadow", Boolean.valueOf(false), "Draws the text with a shadow."));
  
  public Setting<Integer> animationHorizontalTime = register(new Setting("AnimationHTime", Integer.valueOf(500), Integer.valueOf(1), Integer.valueOf(1000), v -> ((Boolean)this.arrayList.getValue()).booleanValue()));
  
  public Setting<Integer> animationVerticalTime = register(new Setting("AnimationVTime", Integer.valueOf(50), Integer.valueOf(1), Integer.valueOf(500), v -> ((Boolean)this.arrayList.getValue()).booleanValue()));
  
  public Setting<Boolean> textRadar = register(new Setting("TextRadar", Boolean.valueOf(false), "A TextRadar"));
  
  public Setting<Boolean> time = register(new Setting("Time", Boolean.valueOf(false), "The time"));
  
  public Setting<Integer> hudRed = register(new Setting("Red", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> !((Boolean)this.rainbow.getValue()).booleanValue()));
  
  public Setting<Integer> hudGreen = register(new Setting("Green", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> !((Boolean)this.rainbow.getValue()).booleanValue()));
  
  public Setting<Integer> hudBlue = register(new Setting("Blue", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> !((Boolean)this.rainbow.getValue()).booleanValue()));
  
  public Setting<Boolean> potions1 = register(new Setting("LevelPotions", Boolean.valueOf(false), v -> ((Boolean)this.potions.getValue()).booleanValue()));
  
  public Setting<Boolean> MS = register(new Setting("ms", Boolean.valueOf(false), v -> ((Boolean)this.ping.getValue()).booleanValue()));
  
  public Map<Module, Float> moduleProgressMap = new HashMap<>();
  
  public Map<Integer, Integer> colorMap = new HashMap<>();
  
  private Map<String, Integer> players = new HashMap<>();
  
  private final Map<Potion, Color> potionColorMap = new HashMap<>();
  
  private int color;
  
  private boolean shouldIncrement;
  
  private int hitMarkerTimer;
  
  public HUD() {
    super("HUD", "HUD Elements rendered on your screen", Module.Category.CLIENT, true, false, false);
    setInstance();
    this.potionColorMap.put(MobEffects.SPEED, new Color(124, 175, 198));
    this.potionColorMap.put(MobEffects.SLOWNESS, new Color(90, 108, 129));
    this.potionColorMap.put(MobEffects.HASTE, new Color(217, 192, 67));
    this.potionColorMap.put(MobEffects.MINING_FATIGUE, new Color(74, 66, 23));
    this.potionColorMap.put(MobEffects.STRENGTH, new Color(147, 36, 35));
    this.potionColorMap.put(MobEffects.INSTANT_HEALTH, new Color(67, 10, 9));
    this.potionColorMap.put(MobEffects.INSTANT_DAMAGE, new Color(67, 10, 9));
    this.potionColorMap.put(MobEffects.JUMP_BOOST, new Color(34, 255, 76));
    this.potionColorMap.put(MobEffects.NAUSEA, new Color(85, 29, 74));
    this.potionColorMap.put(MobEffects.REGENERATION, new Color(205, 92, 171));
    this.potionColorMap.put(MobEffects.RESISTANCE, new Color(153, 69, 58));
    this.potionColorMap.put(MobEffects.FIRE_RESISTANCE, new Color(228, 154, 58));
    this.potionColorMap.put(MobEffects.WATER_BREATHING, new Color(46, 82, 153));
    this.potionColorMap.put(MobEffects.INVISIBILITY, new Color(127, 131, 146));
    this.potionColorMap.put(MobEffects.BLINDNESS, new Color(31, 31, 35));
    this.potionColorMap.put(MobEffects.NIGHT_VISION, new Color(31, 31, 161));
    this.potionColorMap.put(MobEffects.HUNGER, new Color(88, 118, 83));
    this.potionColorMap.put(MobEffects.WEAKNESS, new Color(72, 77, 72));
    this.potionColorMap.put(MobEffects.POISON, new Color(78, 147, 49));
    this.potionColorMap.put(MobEffects.WITHER, new Color(53, 42, 39));
    this.potionColorMap.put(MobEffects.HEALTH_BOOST, new Color(248, 125, 35));
    this.potionColorMap.put(MobEffects.ABSORPTION, new Color(37, 82, 165));
    this.potionColorMap.put(MobEffects.SATURATION, new Color(248, 36, 35));
    this.potionColorMap.put(MobEffects.GLOWING, new Color(148, 160, 97));
    this.potionColorMap.put(MobEffects.LEVITATION, new Color(206, 255, 255));
    this.potionColorMap.put(MobEffects.LUCK, new Color(51, 153, 0));
    this.potionColorMap.put(MobEffects.UNLUCK, new Color(192, 164, 77));
  }
  
  public static HUD getInstance() {
    if (INSTANCE == null)
      INSTANCE = new HUD(); 
    return INSTANCE;
  }
  
  private void setInstance() {
    INSTANCE = this;
  }
  
  public void onUpdate() {
    for (Module module : Phobos.moduleManager.sortedModules) {
      if (module.isDisabled() && module.arrayListOffset == 0.0F)
        module.sliding = true; 
    } 
    if (this.timer.passedMs(((Integer)(Managers.getInstance()).textRadarUpdates.getValue()).intValue())) {
      this.players = getTextRadarPlayers();
      this.timer.reset();
    } 
    if (this.shouldIncrement)
      this.hitMarkerTimer++; 
    if (this.hitMarkerTimer == 10) {
      this.hitMarkerTimer = 0;
      this.shouldIncrement = false;
    } 
  }
  
  @SubscribeEvent
  public void onModuleToggle(ClientEvent event) {
    if (event.getFeature() instanceof Module)
      if (event.getStage() == 0) {
        for (float i = 0.0F; i <= this.renderer.getStringWidth(((Module)event.getFeature()).getDisplayName()); i += this.renderer.getStringWidth(((Module)event.getFeature()).getDisplayName()) / 500.0F) {
          if (this.moduleTimer.passedMs(1L))
            this.moduleProgressMap.put((Module)event.getFeature(), Float.valueOf(this.renderer.getStringWidth(((Module)event.getFeature()).getDisplayName()) - i)); 
          this.timer.reset();
        } 
      } else if (event.getStage() == 1) {
        for (float i = 0.0F; i <= this.renderer.getStringWidth(((Module)event.getFeature()).getDisplayName()); i += this.renderer.getStringWidth(((Module)event.getFeature()).getDisplayName()) / 500.0F) {
          if (this.moduleTimer.passedMs(1L))
            this.moduleProgressMap.put((Module)event.getFeature(), Float.valueOf(this.renderer.getStringWidth(((Module)event.getFeature()).getDisplayName()) - i)); 
          this.timer.reset();
        } 
      }  
  }
  
  public void onRender2D(Render2DEvent event) {
    int color;
    if (fullNullCheck())
      return; 
    int colorSpeed = 101 - ((Integer)this.rainbowSpeed.getValue()).intValue();
    float hue = ((Boolean)this.colorSync.getValue()).booleanValue() ? Colors.INSTANCE.hue : ((float)(System.currentTimeMillis() % (360 * colorSpeed)) / 360.0F * colorSpeed);
    int width = this.renderer.scaledWidth;
    int height = this.renderer.scaledHeight;
    float tempHue = hue;
    for (int i = 0; i <= height; i++) {
      if (((Boolean)this.colorSync.getValue()).booleanValue()) {
        this.colorMap.put(Integer.valueOf(i), Integer.valueOf(Color.HSBtoRGB(tempHue, ((Integer)Colors.INSTANCE.rainbowSaturation.getValue()).intValue() / 255.0F, ((Integer)Colors.INSTANCE.rainbowBrightness.getValue()).intValue() / 255.0F)));
      } else {
        this.colorMap.put(Integer.valueOf(i), Integer.valueOf(Color.HSBtoRGB(tempHue, ((Integer)this.rainbowSaturation.getValue()).intValue() / 255.0F, ((Integer)this.rainbowBrightness.getValue()).intValue() / 255.0F)));
      } 
      tempHue += 1.0F / height * ((Integer)this.factor.getValue()).intValue();
    } 
    GlStateManager.pushMatrix();
    if (((Boolean)this.rainbow.getValue()).booleanValue() && !((Boolean)this.rolling.getValue()).booleanValue()) {
      this.color = ((Boolean)this.colorSync.getValue()).booleanValue() ? Colors.INSTANCE.getCurrentColorHex() : Color.HSBtoRGB(hue, ((Integer)this.rainbowSaturation.getValue()).intValue() / 255.0F, ((Integer)this.rainbowBrightness.getValue()).intValue() / 255.0F);
    } else if (!((Boolean)this.rainbow.getValue()).booleanValue()) {
      this.color = ((Boolean)this.colorSync.getValue()).booleanValue() ? Colors.INSTANCE.getCurrentColorHex() : ColorUtil.toRGBA(((Integer)this.hudRed.getValue()).intValue(), ((Integer)this.hudGreen.getValue()).intValue(), ((Integer)this.hudBlue.getValue()).intValue());
    } 
    String grayString = ((Boolean)this.grayNess.getValue()).booleanValue() ? "" : "";
    switch ((WaterMark)this.watermark.getValue()) {
      case TIME:
        this.renderer.drawString("Catware" + (((Boolean)this.modeVer.getValue()).booleanValue() ? " v1.0" : ""), 2.0F, 2.0F, (((Boolean)this.rolling.getValue()).booleanValue() && ((Boolean)this.rainbow.getValue()).booleanValue()) ? ((Integer)this.colorMap.get(Integer.valueOf(2))).intValue() : this.color, true);
        break;
      case CHRISTMAS:
        this.renderer.drawString("Cathack" + (((Boolean)this.modeVer.getValue()).booleanValue() ? " v1.0" : ""), 2.0F, 2.0F, (((Boolean)this.rolling.getValue()).booleanValue() && ((Boolean)this.rainbow.getValue()).booleanValue()) ? ((Integer)this.colorMap.get(Integer.valueOf(2))).intValue() : this.color, true);
        break;
      case LONG:
        this.renderer.drawString((String)this.customWatermark.getValue() + (((Boolean)this.modeVer.getValue()).booleanValue() ? " v1.0" : ""), 2.0F, 2.0F, (((Boolean)this.rolling.getValue()).booleanValue() && ((Boolean)this.rainbow.getValue()).booleanValue()) ? ((Integer)this.colorMap.get(Integer.valueOf(2))).intValue() : this.color, true);
        break;
    } 
    if (((Boolean)this.textRadar.getValue()).booleanValue())
      drawTextRadar((ToolTips.getInstance().isOff() || !((Boolean)(ToolTips.getInstance()).shulkerSpy.getValue()).booleanValue() || !((Boolean)(ToolTips.getInstance()).render.getValue()).booleanValue()) ? 0 : ToolTips.getInstance().getTextRadarY()); 
    int j = ((Boolean)this.renderingUp.getValue()).booleanValue() ? 0 : ((mc.currentScreen instanceof net.minecraft.client.gui.GuiChat) ? 14 : 0);
    if (((Boolean)this.arrayList.getValue()).booleanValue())
      if (((Boolean)this.renderingUp.getValue()).booleanValue()) {
        for (int m = 0; m < (((Boolean)this.alphabeticalSorting.getValue()).booleanValue() ? Phobos.moduleManager.alphabeticallySortedModules.size() : Phobos.moduleManager.sortedModules.size()); m++) {
          Module module = ((Boolean)this.alphabeticalSorting.getValue()).booleanValue() ? Phobos.moduleManager.alphabeticallySortedModules.get(m) : Phobos.moduleManager.sortedModules.get(m);
          String text = module.getDisplayName() + "" + ((module.getDisplayInfo() != null) ? (" " + module.getDisplayInfo() + "") : "");
          Color moduleColor = (Color)Phobos.moduleManager.moduleColorMap.get(module);
          this.renderer.drawString(text, (width - 2 - this.renderer.getStringWidth(text)) + ((((Integer)this.animationHorizontalTime.getValue()).intValue() == 1) ? 0.0F : module.arrayListOffset), (2 + j * 10), (((Boolean)this.rolling.getValue()).booleanValue() && ((Boolean)this.rainbow.getValue()).booleanValue()) ? ((Integer)this.colorMap.get(Integer.valueOf(MathUtil.clamp(2 + j * 10, 0, height)))).intValue() : ((((Boolean)this.moduleColors.getValue()).booleanValue() && moduleColor != null) ? moduleColor.getRGB() : this.color), true);
          j++;
        } 
      } else {
        for (int m = 0; m < (((Boolean)this.alphabeticalSorting.getValue()).booleanValue() ? Phobos.moduleManager.alphabeticallySortedModules.size() : Phobos.moduleManager.sortedModules.size()); m++) {
          Module module = ((Boolean)this.alphabeticalSorting.getValue()).booleanValue() ? Phobos.moduleManager.alphabeticallySortedModules.get(Phobos.moduleManager.alphabeticallySortedModules.size() - 1 - m) : Phobos.moduleManager.sortedModules.get(m);
          String text = module.getDisplayName() + "" + ((module.getDisplayInfo() != null) ? (" " + module.getDisplayInfo() + "") : "");
          Color moduleColor = (Color)Phobos.moduleManager.moduleColorMap.get(module);
          TextManager renderer = this.renderer;
          String text5 = text;
          float x = (width - 2 - this.renderer.getStringWidth(text)) + ((((Integer)this.animationHorizontalTime.getValue()).intValue() == 1) ? 0.0F : module.arrayListOffset);
          int n = height;
          j += 10;
          renderer.drawString(text5, x, (n - j), (((Boolean)this.rolling.getValue()).booleanValue() && ((Boolean)this.rainbow.getValue()).booleanValue()) ? ((Integer)this.colorMap.get(Integer.valueOf(MathUtil.clamp(height - j, 0, height)))).intValue() : ((((Boolean)this.moduleColors.getValue()).booleanValue() && moduleColor != null) ? moduleColor.getRGB() : this.color), true);
        } 
      }  
    int k = ((Boolean)this.renderingUp.getValue()).booleanValue() ? ((mc.currentScreen instanceof net.minecraft.client.gui.GuiChat) ? 0 : 0) : 0;
    if (((Boolean)this.renderingUp.getValue()).booleanValue()) {
      if (((Boolean)this.serverBrand.getValue()).booleanValue()) {
        String text2 = grayString + "Server brand " + Phobos.serverManager.getServerBrand();
        TextManager renderer2 = this.renderer;
        String text6 = text2;
        float x2 = (width - this.renderer.getStringWidth(text2) + 2);
        int n2 = height - 2;
        k += 10;
        renderer2.drawString(text6, x2, (n2 - k), (((Boolean)this.rolling.getValue()).booleanValue() && ((Boolean)this.rainbow.getValue()).booleanValue()) ? ((Integer)this.colorMap.get(Integer.valueOf(height - k))).intValue() : this.color, true);
      } 
      if (((Boolean)this.potions.getValue()).booleanValue())
        for (PotionEffect effect : Phobos.potionManager.getOwnPotions()) {
          String text3 = ((Boolean)this.altPotionsColors.getValue()).booleanValue() ? Phobos.potionManager.getPotionString(effect) : Phobos.potionManager.getColoredPotionString(effect);
          TextManager renderer3 = this.renderer;
          String text7 = text3;
          float x3 = (width - this.renderer.getStringWidth(text3) + 2);
          int n3 = height - 2;
          k += 10;
          renderer3.drawString(text7, x3, (n3 - k), (((Boolean)this.rolling.getValue()).booleanValue() && ((Boolean)this.rainbow.getValue()).booleanValue()) ? ((Integer)this.colorMap.get(Integer.valueOf(height - k))).intValue() : (((Boolean)this.altPotionsColors.getValue()).booleanValue() ? ((Color)this.potionColorMap.get(effect.getPotion())).getRGB() : this.color), true);
        }  
      if (((Boolean)this.speed.getValue()).booleanValue()) {
        String text2 = grayString + "Speed " + Phobos.speedManager.getSpeedKpH() + " km/h";
        TextManager renderer4 = this.renderer;
        String text8 = text2;
        float x4 = (width - this.renderer.getStringWidth(text2) + 2);
        int n4 = height - 2;
        k += 10;
        renderer4.drawString(text8, x4, (n4 - k), (((Boolean)this.rolling.getValue()).booleanValue() && ((Boolean)this.rainbow.getValue()).booleanValue()) ? ((Integer)this.colorMap.get(Integer.valueOf(height - k))).intValue() : this.color, true);
      } 
      if (((Boolean)this.time.getValue()).booleanValue()) {
        String text2 = grayString + "Time " + (new SimpleDateFormat("h:mm a")).format(new Date());
        TextManager renderer5 = this.renderer;
        String text9 = text2;
        float x5 = (width - this.renderer.getStringWidth(text2) + 2);
        int n5 = height - 2;
        k += 10;
        renderer5.drawString(text9, x5, (n5 - k), (((Boolean)this.rolling.getValue()).booleanValue() && ((Boolean)this.rainbow.getValue()).booleanValue()) ? ((Integer)this.colorMap.get(Integer.valueOf(height - k))).intValue() : this.color, true);
      } 
      if (((Boolean)this.durability.getValue()).booleanValue()) {
        int itemDamage = mc.player.getHeldItemMainhand().getMaxDamage() - mc.player.getHeldItemMainhand().getItemDamage();
        if (itemDamage > 0) {
          String str1 = grayString + "Durability " + itemDamage;
          TextManager renderer6 = this.renderer;
          String text10 = str1;
          float x6 = (width - this.renderer.getStringWidth(str1) + 2);
          int n6 = height - 2;
          k += 10;
          renderer6.drawString(text10, x6, (n6 - k), (((Boolean)this.rolling.getValue()).booleanValue() && ((Boolean)this.rainbow.getValue()).booleanValue()) ? ((Integer)this.colorMap.get(Integer.valueOf(height - k))).intValue() : this.color, true);
        } 
      } 
      if (((Boolean)this.tps.getValue()).booleanValue()) {
        String text2 = grayString + "TPS " + Phobos.serverManager.getTPS();
        TextManager renderer7 = this.renderer;
        String text11 = text2;
        float x7 = (width - this.renderer.getStringWidth(text2) + 2);
        int n7 = height - 2;
        k += 10;
        renderer7.drawString(text11, x7, (n7 - k), (((Boolean)this.rolling.getValue()).booleanValue() && ((Boolean)this.rainbow.getValue()).booleanValue()) ? ((Integer)this.colorMap.get(Integer.valueOf(height - k))).intValue() : this.color, true);
      } 
      String fpsText = grayString + "FPS " + Minecraft.debugFPS;
      String text = grayString + "Ping " + (ServerModule.getInstance().isConnected() ? ServerModule.getInstance().getServerPing() : Phobos.serverManager.getPing()) + (((Boolean)this.MS.getValue()).booleanValue() ? "ms" : "");
      if (this.renderer.getStringWidth(text) > this.renderer.getStringWidth(fpsText)) {
        if (((Boolean)this.ping.getValue()).booleanValue()) {
          TextManager renderer8 = this.renderer;
          String text12 = text;
          float x8 = (width - this.renderer.getStringWidth(text) + 2);
          int n8 = height - 2;
          k += 10;
          renderer8.drawString(text12, x8, (n8 - k), (((Boolean)this.rolling.getValue()).booleanValue() && ((Boolean)this.rainbow.getValue()).booleanValue()) ? ((Integer)this.colorMap.get(Integer.valueOf(height - k))).intValue() : this.color, true);
        } 
        if (((Boolean)this.fps.getValue()).booleanValue()) {
          TextManager renderer9 = this.renderer;
          String text13 = fpsText;
          float x9 = (width - this.renderer.getStringWidth(fpsText) + 2);
          int n9 = height - 2;
          k += 10;
          renderer9.drawString(text13, x9, (n9 - k), (((Boolean)this.rolling.getValue()).booleanValue() && ((Boolean)this.rainbow.getValue()).booleanValue()) ? ((Integer)this.colorMap.get(Integer.valueOf(height - k))).intValue() : this.color, true);
        } 
      } else {
        if (((Boolean)this.fps.getValue()).booleanValue()) {
          TextManager renderer10 = this.renderer;
          String text14 = fpsText;
          float x10 = (width - this.renderer.getStringWidth(fpsText) + 2);
          int n10 = height - 2;
          k += 10;
          renderer10.drawString(text14, x10, (n10 - k), (((Boolean)this.rolling.getValue()).booleanValue() && ((Boolean)this.rainbow.getValue()).booleanValue()) ? ((Integer)this.colorMap.get(Integer.valueOf(height - k))).intValue() : this.color, true);
        } 
        if (((Boolean)this.ping.getValue()).booleanValue()) {
          TextManager renderer11 = this.renderer;
          String text15 = text;
          float x11 = (width - this.renderer.getStringWidth(text) + 2);
          int n11 = height - 2;
          k += 10;
          renderer11.drawString(text15, x11, (n11 - k), (((Boolean)this.rolling.getValue()).booleanValue() && ((Boolean)this.rainbow.getValue()).booleanValue()) ? ((Integer)this.colorMap.get(Integer.valueOf(height - k))).intValue() : this.color, true);
        } 
      } 
    } else {
      if (((Boolean)this.serverBrand.getValue()).booleanValue()) {
        String text2 = grayString + "Server brand " + Phobos.serverManager.getServerBrand();
        this.renderer.drawString(text2, (width - this.renderer.getStringWidth(text2) + 2), (2 + k++ * 10), (((Boolean)this.rolling.getValue()).booleanValue() && ((Boolean)this.rainbow.getValue()).booleanValue()) ? ((Integer)this.colorMap.get(Integer.valueOf(2 + k * 10))).intValue() : this.color, true);
      } 
      if (((Boolean)this.potions.getValue()).booleanValue())
        for (PotionEffect effect : Phobos.potionManager.getOwnPotions()) {
          String text3 = ((Boolean)this.altPotionsColors.getValue()).booleanValue() ? Phobos.potionManager.getPotionString(effect) : Phobos.potionManager.getColoredPotionString(effect);
          this.renderer.drawString(text3, (width - this.renderer.getStringWidth(text3) + 2), (2 + k++ * 10), (((Boolean)this.rolling.getValue()).booleanValue() && ((Boolean)this.rainbow.getValue()).booleanValue()) ? ((Integer)this.colorMap.get(Integer.valueOf(2 + k * 10))).intValue() : (((Boolean)this.altPotionsColors.getValue()).booleanValue() ? ((Color)this.potionColorMap.get(effect.getPotion())).getRGB() : this.color), true);
        }  
      if (((Boolean)this.speed.getValue()).booleanValue()) {
        String text2 = grayString + "Speed " + Phobos.speedManager.getSpeedKpH() + " km/h";
        this.renderer.drawString(text2, (width - this.renderer.getStringWidth(text2) + 2), (2 + k++ * 10), (((Boolean)this.rolling.getValue()).booleanValue() && ((Boolean)this.rainbow.getValue()).booleanValue()) ? ((Integer)this.colorMap.get(Integer.valueOf(2 + k * 10))).intValue() : this.color, true);
      } 
      if (((Boolean)this.time.getValue()).booleanValue()) {
        String text2 = grayString + "Time " + (new SimpleDateFormat("h:mm a")).format(new Date());
        this.renderer.drawString(text2, (width - this.renderer.getStringWidth(text2) + 2), (2 + k++ * 10), (((Boolean)this.rolling.getValue()).booleanValue() && ((Boolean)this.rainbow.getValue()).booleanValue()) ? ((Integer)this.colorMap.get(Integer.valueOf(2 + k * 10))).intValue() : this.color, true);
      } 
      if (((Boolean)this.durability.getValue()).booleanValue()) {
        int itemDamage = mc.player.getHeldItemMainhand().getMaxDamage() - mc.player.getHeldItemMainhand().getItemDamage();
        if (itemDamage > 0) {
          String str = grayString + "Durability " + itemDamage;
          this.renderer.drawString(str, (width - this.renderer.getStringWidth(str) + 2), (2 + k++ * 10), (((Boolean)this.rolling.getValue()).booleanValue() && ((Boolean)this.rainbow.getValue()).booleanValue()) ? ((Integer)this.colorMap.get(Integer.valueOf(2 + k * 10))).intValue() : this.color, true);
        } 
      } 
      if (((Boolean)this.tps.getValue()).booleanValue()) {
        String text2 = grayString + "TPS " + Phobos.serverManager.getTPS();
        this.renderer.drawString(text2, (width - this.renderer.getStringWidth(text2) + 2), (2 + k++ * 10), (((Boolean)this.rolling.getValue()).booleanValue() && ((Boolean)this.rainbow.getValue()).booleanValue()) ? ((Integer)this.colorMap.get(Integer.valueOf(2 + k * 10))).intValue() : this.color, true);
      } 
      String fpsText = grayString + "FPS " + Minecraft.debugFPS;
      String text = grayString + "Ping " + Phobos.serverManager.getPing();
      if (this.renderer.getStringWidth(text) > this.renderer.getStringWidth(fpsText)) {
        if (((Boolean)this.ping.getValue()).booleanValue())
          this.renderer.drawString(text, (width - this.renderer.getStringWidth(text) + 2), (2 + k++ * 10), (((Boolean)this.rolling.getValue()).booleanValue() && ((Boolean)this.rainbow.getValue()).booleanValue()) ? ((Integer)this.colorMap.get(Integer.valueOf(2 + k * 10))).intValue() : this.color, true); 
        if (((Boolean)this.fps.getValue()).booleanValue())
          this.renderer.drawString(fpsText, (width - this.renderer.getStringWidth(fpsText) + 2), (2 + k++ * 10), (((Boolean)this.rolling.getValue()).booleanValue() && ((Boolean)this.rainbow.getValue()).booleanValue()) ? ((Integer)this.colorMap.get(Integer.valueOf(2 + k * 10))).intValue() : this.color, true); 
      } else {
        if (((Boolean)this.fps.getValue()).booleanValue())
          this.renderer.drawString(fpsText, (width - this.renderer.getStringWidth(fpsText) + 2), (2 + k++ * 10), (((Boolean)this.rolling.getValue()).booleanValue() && ((Boolean)this.rainbow.getValue()).booleanValue()) ? ((Integer)this.colorMap.get(Integer.valueOf(2 + k * 10))).intValue() : this.color, true); 
        if (((Boolean)this.ping.getValue()).booleanValue())
          this.renderer.drawString(text, (width - this.renderer.getStringWidth(text) + 2), (2 + k++ * 10), (((Boolean)this.rolling.getValue()).booleanValue() && ((Boolean)this.rainbow.getValue()).booleanValue()) ? ((Integer)this.colorMap.get(Integer.valueOf(2 + k * 10))).intValue() : this.color, true); 
      } 
    } 
    boolean inHell = mc.world.getBiome(mc.player.getPosition()).getBiomeName().equals("Hell");
    int posX = (int)mc.player.posX;
    int posY = (int)mc.player.posY;
    int posZ = (int)mc.player.posZ;
    float nether = inHell ? 8.0F : 0.125F;
    int hposX = (int)(mc.player.posX * nether);
    int hposZ = (int)(mc.player.posZ * nether);
    if (((Boolean)this.renderingUp.getValue()).booleanValue()) {
      Phobos.notificationManager.handleNotifications(height - k + 16);
    } else {
      Phobos.notificationManager.handleNotifications(height - j + 16);
    } 
    k = (mc.currentScreen instanceof net.minecraft.client.gui.GuiChat) ? 14 : 0;
    String coordinates = grayString + "XYZ [" + posX + ", " + posY + ", " + posZ + " " + grayString + "" + hposX + ", " + hposZ + "" + grayString + "]";
    String text4 = (((Boolean)this.direction.getValue()).booleanValue() ? (Phobos.rotationManager.getDirection4D(false) + " ") : "") + (((Boolean)this.coords.getValue()).booleanValue() ? coordinates : "") + "";
    TextManager renderer12 = this.renderer;
    String text16 = text4;
    float x12 = 2.0F;
    int n12 = height;
    k += 10;
    float y = (n12 - k);
    if (((Boolean)this.rolling.getValue()).booleanValue() && ((Boolean)this.rainbow.getValue()).booleanValue()) {
      Map<Integer, Integer> colorMap = this.colorMap;
      int n13 = height;
      k += 10;
      color = ((Integer)colorMap.get(Integer.valueOf(n13 - k))).intValue();
    } else {
      color = this.color;
    } 
    renderer12.drawString(text16, 2.0F, y, color, true);
    if (((Boolean)this.armor.getValue()).booleanValue())
      renderArmorHUD(((Boolean)this.percent.getValue()).booleanValue()); 
    if (((Boolean)this.totems.getValue()).booleanValue())
      renderTotemHUD(); 
    if (this.greeter.getValue() != Greeter.NONE)
      renderGreeter(); 
    if (this.lag.getValue() != LagNotify.NONE)
      renderLag(); 
    if (((Boolean)this.hitMarkers.getValue()).booleanValue() && this.hitMarkerTimer > 0)
      drawHitMarkers(); 
    GlStateManager.popMatrix();
  }
  
  public Map<String, Integer> getTextRadarPlayers() {
    return EntityUtil.getTextRadarPlayers();
  }
  
  public void renderGreeter() {
    int width = this.renderer.scaledWidth;
    String text = "";
    switch ((Greeter)this.greeter.getValue()) {
      case TIME:
        text = text + MathUtil.getTimeOfDay() + mc.player.getDisplayNameString();
        break;
      case CHRISTMAS:
        text = text + "Merry Christmas " + mc.player.getDisplayNameString() + " :^)";
        break;
      case LONG:
        text = text + "Welcome to Catware 1.0 " + mc.player.getDisplayNameString() + " :^)";
        break;
      case CUSTOM:
        text = text + (String)this.spoofGreeter.getValue();
        break;
      default:
        text = text + "Welcome " + mc.player.getDisplayNameString();
        break;
    } 
    this.renderer.drawString(text, width / 2.0F - this.renderer.getStringWidth(text) / 2.0F + 2.0F, 2.0F, (((Boolean)this.rolling.getValue()).booleanValue() && ((Boolean)this.rainbow.getValue()).booleanValue()) ? ((Integer)this.colorMap.get(Integer.valueOf(2))).intValue() : this.color, true);
  }
  
  public void renderLag() {
    int width = this.renderer.scaledWidth;
    if (Phobos.serverManager.isServerNotResponding()) {
      String text = ((this.lag.getValue() == LagNotify.GRAY) ? "" : "c") + "Server not responding: " + MathUtil.round((float)Phobos.serverManager.serverRespondingTime() / 1000.0F, 1) + "s.";
      this.renderer.drawString(text, width / 2.0F - this.renderer.getStringWidth(text) / 2.0F + 2.0F, 20.0F, (((Boolean)this.rolling.getValue()).booleanValue() && ((Boolean)this.rainbow.getValue()).booleanValue()) ? ((Integer)this.colorMap.get(Integer.valueOf(20))).intValue() : this.color, true);
    } 
  }
  
  public void renderArrayList() {}
  
  public void renderTotemHUD() {
    int width = this.renderer.scaledWidth;
    int height = this.renderer.scaledHeight;
    int totems = mc.player.inventory.mainInventory.stream().filter(itemStack -> (itemStack.getItem() == Items.TOTEM_OF_UNDYING)).mapToInt(ItemStack::func_190916_E).sum();
    if (mc.player.getHeldItemOffhand().getItem() == Items.TOTEM_OF_UNDYING)
      totems += mc.player.getHeldItemOffhand().getCount(); 
    if (totems > 0) {
      GlStateManager.enableTexture2D();
      int i = width / 2;
      int iteration = 0;
      int y = height - 55 - ((mc.player.isInWater() && mc.playerController.gameIsSurvivalOrAdventure()) ? 10 : 0);
      int x = i - 189 + 180 + 2;
      GlStateManager.enableDepth();
      RenderUtil.itemRender.zLevel = 200.0F;
      RenderUtil.itemRender.renderItemAndEffectIntoGUI(totem, x, y);
      RenderUtil.itemRender.renderItemOverlayIntoGUI(mc.fontRenderer, totem, x, y, "");
      RenderUtil.itemRender.zLevel = 0.0F;
      GlStateManager.enableTexture2D();
      GlStateManager.disableLighting();
      GlStateManager.disableDepth();
      this.renderer.drawStringWithShadow(totems + "", (x + 19 - 2 - this.renderer.getStringWidth(totems + "")), (y + 9), 16777215);
      GlStateManager.enableDepth();
      GlStateManager.disableLighting();
    } 
  }
  
  public void renderArmorHUD(boolean percent) {
    int width = this.renderer.scaledWidth;
    int height = this.renderer.scaledHeight;
    GlStateManager.enableTexture2D();
    int i = width / 2;
    int iteration = 0;
    int y = height - 55 - ((mc.player.isInWater() && mc.playerController.gameIsSurvivalOrAdventure()) ? 10 : 0);
    for (ItemStack is : mc.player.inventory.armorInventory) {
      iteration++;
      if (is.isEmpty())
        continue; 
      int x = i - 90 + (9 - iteration) * 20 + 2;
      GlStateManager.enableDepth();
      RenderUtil.itemRender.zLevel = 200.0F;
      RenderUtil.itemRender.renderItemAndEffectIntoGUI(is, x, y);
      RenderUtil.itemRender.renderItemOverlayIntoGUI(mc.fontRenderer, is, x, y, "");
      RenderUtil.itemRender.zLevel = 0.0F;
      GlStateManager.enableTexture2D();
      GlStateManager.disableLighting();
      GlStateManager.disableDepth();
      String s = (is.getCount() > 1) ? (is.getCount() + "") : "";
      this.renderer.drawStringWithShadow(s, (x + 19 - 2 - this.renderer.getStringWidth(s)), (y + 9), 16777215);
      if (!percent)
        continue; 
      int dmg = 0;
      int itemDurability = is.getMaxDamage() - is.getItemDamage();
      float green = (is.getMaxDamage() - is.getItemDamage()) / is.getMaxDamage();
      float red = 1.0F - green;
      if (percent) {
        dmg = 100 - (int)(red * 100.0F);
      } else {
        dmg = itemDurability;
      } 
      this.renderer.drawStringWithShadow(dmg + "", (x + 8 - this.renderer.getStringWidth(dmg + "") / 2), (y - 11), ColorUtil.toRGBA((int)(red * 255.0F), (int)(green * 255.0F), 0));
    } 
    GlStateManager.enableDepth();
    GlStateManager.disableLighting();
  }
  
  public void drawHitMarkers() {
    ScaledResolution resolution = new ScaledResolution(mc);
    RenderUtil.drawLine(resolution.getScaledWidth() / 2.0F - 4.0F, resolution.getScaledHeight() / 2.0F - 4.0F, resolution.getScaledWidth() / 2.0F - 8.0F, resolution.getScaledHeight() / 2.0F - 8.0F, 1.0F, ColorUtil.toRGBA(255, 255, 255, 255));
    RenderUtil.drawLine(resolution.getScaledWidth() / 2.0F + 4.0F, resolution.getScaledHeight() / 2.0F - 4.0F, resolution.getScaledWidth() / 2.0F + 8.0F, resolution.getScaledHeight() / 2.0F - 8.0F, 1.0F, ColorUtil.toRGBA(255, 255, 255, 255));
    RenderUtil.drawLine(resolution.getScaledWidth() / 2.0F - 4.0F, resolution.getScaledHeight() / 2.0F + 4.0F, resolution.getScaledWidth() / 2.0F - 8.0F, resolution.getScaledHeight() / 2.0F + 8.0F, 1.0F, ColorUtil.toRGBA(255, 255, 255, 255));
    RenderUtil.drawLine(resolution.getScaledWidth() / 2.0F + 4.0F, resolution.getScaledHeight() / 2.0F + 4.0F, resolution.getScaledWidth() / 2.0F + 8.0F, resolution.getScaledHeight() / 2.0F + 8.0F, 1.0F, ColorUtil.toRGBA(255, 255, 255, 255));
  }
  
  public void drawTextRadar(int yOffset) {
    if (!this.players.isEmpty()) {
      int y = this.renderer.getFontHeight() + 7 + yOffset;
      for (Map.Entry<String, Integer> player : this.players.entrySet()) {
        String text = (String)player.getKey() + " ";
        int textheight = this.renderer.getFontHeight() + 1;
        this.renderer.drawString(text, 2.0F, y, (((Boolean)this.rolling.getValue()).booleanValue() && ((Boolean)this.rainbow.getValue()).booleanValue()) ? ((Integer)this.colorMap.get(Integer.valueOf(y))).intValue() : this.color, true);
        y += textheight;
      } 
    } 
  }
  
  public enum Greeter {
    NONE, NAME, TIME, CHRISTMAS, LONG, CUSTOM;
  }
  
  public enum LagNotify {
    NONE, RED, GRAY;
  }
  
  public enum WaterMark {
    NONE, CATWARE, CATHACK, CUSTOM;
  }
  
  public enum Sound {
    NONE, COD, CSGO;
  }
}
