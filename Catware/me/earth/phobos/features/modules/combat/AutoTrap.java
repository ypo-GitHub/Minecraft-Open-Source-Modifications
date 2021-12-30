package me.earth.phobos.features.modules.combat;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import me.earth.phobos.Phobos;
import me.earth.phobos.event.events.Render3DEvent;
import me.earth.phobos.event.events.UpdateWalkingPlayerEvent;
import me.earth.phobos.features.command.Command;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.modules.client.ClickGui;
import me.earth.phobos.features.modules.client.Colors;
import me.earth.phobos.features.modules.client.ServerModule;
import me.earth.phobos.features.modules.player.BlockTweaks;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.BlockUtil;
import me.earth.phobos.util.EntityUtil;
import me.earth.phobos.util.InventoryUtil;
import me.earth.phobos.util.MathUtil;
import me.earth.phobos.util.RenderUtil;
import me.earth.phobos.util.Timer;
import net.minecraft.block.BlockEndPortalFrame;
import net.minecraft.block.BlockObsidian;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AutoTrap extends Module {
  public static boolean isPlacing = false;
  
  private final Setting<Boolean> server = register(new Setting("Server", Boolean.valueOf(false)));
  
  private final Setting<Integer> delay = register(new Setting("Delay/Place", Integer.valueOf(50), Integer.valueOf(0), Integer.valueOf(250)));
  
  private final Setting<Integer> blocksPerPlace = register(new Setting("Block/Place", Integer.valueOf(8), Integer.valueOf(1), Integer.valueOf(30)));
  
  private final Setting<Double> targetRange = register(new Setting("TargetRange", Double.valueOf(10.0D), Double.valueOf(0.0D), Double.valueOf(20.0D)));
  
  private final Setting<Double> range = register(new Setting("PlaceRange", Double.valueOf(6.0D), Double.valueOf(0.0D), Double.valueOf(10.0D)));
  
  private final Setting<TargetMode> targetMode = register(new Setting("Target", TargetMode.CLOSEST));
  
  private final Setting<InventoryUtil.Switch> switchMode = register(new Setting("Switch", InventoryUtil.Switch.NORMAL));
  
  private final Setting<Boolean> rotate = register(new Setting("Rotate", Boolean.valueOf(true)));
  
  private final Setting<Boolean> raytrace = register(new Setting("Raytrace", Boolean.valueOf(false)));
  
  private final Setting<Pattern> pattern = register(new Setting("Pattern", Pattern.STATIC));
  
  private final Setting<Integer> extend = register(new Setting("Extend", Integer.valueOf(4), Integer.valueOf(1), Integer.valueOf(4), v -> (this.pattern.getValue() != Pattern.STATIC), "Extending the Trap."));
  
  private final Setting<Boolean> antiScaffold = register(new Setting("AntiScaffold", Boolean.valueOf(false)));
  
  private final Setting<Boolean> antiStep = register(new Setting("AntiStep", Boolean.valueOf(false)));
  
  private final Setting<Boolean> face = register(new Setting("Face", Boolean.valueOf(true)));
  
  private final Setting<Boolean> legs = register(new Setting("Legs", Boolean.valueOf(false), v -> (this.pattern.getValue() != Pattern.OPEN)));
  
  private final Setting<Boolean> platform = register(new Setting("Platform", Boolean.valueOf(false), v -> (this.pattern.getValue() != Pattern.OPEN)));
  
  private final Setting<Boolean> antiDrop = register(new Setting("AntiDrop", Boolean.valueOf(false)));
  
  private final Setting<Double> speed = register(new Setting("Speed", Double.valueOf(10.0D), Double.valueOf(0.0D), Double.valueOf(30.0D)));
  
  private final Setting<Boolean> antiSelf = register(new Setting("AntiSelf", Boolean.valueOf(false)));
  
  private final Setting<Integer> eventMode = register(new Setting("Updates", Integer.valueOf(3), Integer.valueOf(1), Integer.valueOf(3)));
  
  private final Setting<Boolean> freecam = register(new Setting("Freecam", Boolean.valueOf(false)));
  
  private final Setting<Boolean> info = register(new Setting("Info", Boolean.valueOf(false)));
  
  private final Setting<Boolean> entityCheck = register(new Setting("NoBlock", Boolean.valueOf(true)));
  
  private final Setting<Boolean> noScaffoldExtend = register(new Setting("NoScaffoldExtend", Boolean.valueOf(false)));
  
  private final Setting<Boolean> disable = register(new Setting("TSelfMove", Boolean.valueOf(false)));
  
  private final Setting<Boolean> packet = register(new Setting("Packet", Boolean.valueOf(false)));
  
  private final Setting<Boolean> airPacket = register(new Setting("AirPacket", Boolean.valueOf(false), v -> ((Boolean)this.packet.getValue()).booleanValue()));
  
  private final Setting<Integer> retryer = register(new Setting("Retries", Integer.valueOf(4), Integer.valueOf(1), Integer.valueOf(15)));
  
  private final Setting<Boolean> endPortals = register(new Setting("EndPortals", Boolean.valueOf(false)));
  
  private final Setting<Boolean> render = register(new Setting("Render", Boolean.valueOf(true)));
  
  public final Setting<Boolean> colorSync = register(new Setting("Sync", Boolean.valueOf(false), v -> ((Boolean)this.render.getValue()).booleanValue()));
  
  public final Setting<Boolean> box = register(new Setting("Box", Boolean.valueOf(false), v -> ((Boolean)this.render.getValue()).booleanValue()));
  
  public final Setting<Boolean> outline = register(new Setting("Outline", Boolean.valueOf(true), v -> ((Boolean)this.render.getValue()).booleanValue()));
  
  public final Setting<Boolean> customOutline = register(new Setting("CustomLine", Boolean.valueOf(false), v -> (((Boolean)this.outline.getValue()).booleanValue() && ((Boolean)this.render.getValue()).booleanValue())));
  
  private final Setting<Integer> red = register(new Setting("Red", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> ((Boolean)this.render.getValue()).booleanValue()));
  
  private final Setting<Integer> green = register(new Setting("Green", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> ((Boolean)this.render.getValue()).booleanValue()));
  
  private final Setting<Integer> blue = register(new Setting("Blue", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> ((Boolean)this.render.getValue()).booleanValue()));
  
  private final Setting<Integer> alpha = register(new Setting("Alpha", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> ((Boolean)this.render.getValue()).booleanValue()));
  
  private final Setting<Integer> boxAlpha = register(new Setting("BoxAlpha", Integer.valueOf(125), Integer.valueOf(0), Integer.valueOf(255), v -> (((Boolean)this.box.getValue()).booleanValue() && ((Boolean)this.render.getValue()).booleanValue())));
  
  private final Setting<Float> lineWidth = register(new Setting("LineWidth", Float.valueOf(1.0F), Float.valueOf(0.1F), Float.valueOf(5.0F), v -> (((Boolean)this.outline.getValue()).booleanValue() && ((Boolean)this.render.getValue()).booleanValue())));
  
  private final Setting<Integer> cRed = register(new Setting("OL-Red", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> (((Boolean)this.customOutline.getValue()).booleanValue() && ((Boolean)this.outline.getValue()).booleanValue() && ((Boolean)this.render.getValue()).booleanValue())));
  
  private final Setting<Integer> cGreen = register(new Setting("OL-Green", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> (((Boolean)this.customOutline.getValue()).booleanValue() && ((Boolean)this.outline.getValue()).booleanValue() && ((Boolean)this.render.getValue()).booleanValue())));
  
  private final Setting<Integer> cBlue = register(new Setting("OL-Blue", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> (((Boolean)this.customOutline.getValue()).booleanValue() && ((Boolean)this.outline.getValue()).booleanValue() && ((Boolean)this.render.getValue()).booleanValue())));
  
  private final Setting<Integer> cAlpha = register(new Setting("OL-Alpha", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> (((Boolean)this.customOutline.getValue()).booleanValue() && ((Boolean)this.outline.getValue()).booleanValue() && ((Boolean)this.render.getValue()).booleanValue())));
  
  private final Timer timer = new Timer();
  
  private final Map<BlockPos, Integer> retries = new HashMap<>();
  
  private final Timer retryTimer = new Timer();
  
  private final Map<BlockPos, IBlockState> toAir = new HashMap<>();
  
  public EntityPlayer target;
  
  private boolean didPlace = false;
  
  private boolean switchedItem;
  
  private boolean isSneaking;
  
  private int lastHotbarSlot;
  
  private int placements = 0;
  
  private boolean smartRotate = false;
  
  private BlockPos startPos = null;
  
  private List<Vec3d> currentPlaceList = new ArrayList<>();
  
  public AutoTrap() {
    super("AutoTrap", "Traps other players", Module.Category.COMBAT, true, false, false);
  }
  
  public void onEnable() {
    if (fullNullCheck()) {
      disable();
      return;
    } 
    this.toAir.clear();
    this.startPos = EntityUtil.getRoundedBlockPos((Entity)mc.player);
    this.lastHotbarSlot = mc.player.inventory.currentItem;
    this.retries.clear();
    if (shouldServer()) {
      mc.player.connection.sendPacket((Packet)new CPacketChatMessage("@Serverprefix" + (String)(ClickGui.getInstance()).prefix.getValue()));
      mc.player.connection.sendPacket((Packet)new CPacketChatMessage("@Server" + (String)(ClickGui.getInstance()).prefix.getValue() + "module AutoTrap set Enabled true"));
    } 
  }
  
  public void onLogout() {
    disable();
  }
  
  public void onTick() {
    if (((Integer)this.eventMode.getValue()).intValue() == 3) {
      this.smartRotate = false;
      doTrap();
    } 
  }
  
  @SubscribeEvent
  public void onUpdateWalkingPlayer(UpdateWalkingPlayerEvent event) {
    if (event.getStage() == 0 && ((Integer)this.eventMode.getValue()).intValue() == 2) {
      this.smartRotate = (((Boolean)this.rotate.getValue()).booleanValue() && ((Integer)this.blocksPerPlace.getValue()).intValue() == 1);
      doTrap();
    } 
  }
  
  public void onUpdate() {
    if (((Integer)this.eventMode.getValue()).intValue() == 1) {
      this.smartRotate = false;
      doTrap();
    } 
  }
  
  public String getDisplayInfo() {
    if (((Boolean)this.info.getValue()).booleanValue() && this.target != null)
      return this.target.getName(); 
    return null;
  }
  
  public void onDisable() {
    if (fullNullCheck())
      return; 
    if (shouldServer()) {
      mc.player.connection.sendPacket((Packet)new CPacketChatMessage("@Serverprefix" + (String)(ClickGui.getInstance()).prefix.getValue()));
      mc.player.connection.sendPacket((Packet)new CPacketChatMessage("@Server" + (String)(ClickGui.getInstance()).prefix.getValue() + "module AutoTrap set Enabled false"));
      return;
    } 
    isPlacing = false;
    this.isSneaking = EntityUtil.stopSneaking(this.isSneaking);
    switchItem(true);
  }
  
  public void onRender3D(Render3DEvent event) {
    if (((Boolean)this.render.getValue()).booleanValue() && this.currentPlaceList != null)
      for (Vec3d vec : this.currentPlaceList) {
        BlockPos pos = new BlockPos(vec);
        if (!(mc.world.getBlockState(pos).getBlock() instanceof net.minecraft.block.BlockAir))
          continue; 
        RenderUtil.drawBoxESP(pos, ((Boolean)this.colorSync.getValue()).booleanValue() ? Colors.INSTANCE.getCurrentColor() : new Color(((Integer)this.red.getValue()).intValue(), ((Integer)this.green.getValue()).intValue(), ((Integer)this.blue.getValue()).intValue(), ((Integer)this.alpha.getValue()).intValue()), ((Boolean)this.customOutline.getValue()).booleanValue(), new Color(((Integer)this.cRed.getValue()).intValue(), ((Integer)this.cGreen.getValue()).intValue(), ((Integer)this.cBlue.getValue()).intValue(), ((Integer)this.cAlpha.getValue()).intValue()), ((Float)this.lineWidth.getValue()).floatValue(), ((Boolean)this.outline.getValue()).booleanValue(), ((Boolean)this.box.getValue()).booleanValue(), ((Integer)this.boxAlpha.getValue()).intValue(), false);
      }  
  }
  
  private boolean shouldServer() {
    return (ServerModule.getInstance().isConnected() && ((Boolean)this.server.getValue()).booleanValue());
  }
  
  private void doTrap() {
    if (shouldServer() || check())
      return; 
    switch ((Pattern)this.pattern.getValue()) {
      case STATIC:
        doStaticTrap();
        break;
      case SMART:
      case OPEN:
        doSmartTrap();
        break;
    } 
    if (((Boolean)this.packet.getValue()).booleanValue() && ((Boolean)this.airPacket.getValue()).booleanValue()) {
      for (Map.Entry<BlockPos, IBlockState> entry : this.toAir.entrySet())
        mc.world.setBlockState(entry.getKey(), entry.getValue()); 
      this.toAir.clear();
    } 
    if (this.didPlace)
      this.timer.reset(); 
  }
  
  private void doSmartTrap() {
    List<Vec3d> placeTargets = EntityUtil.getUntrappedBlocksExtended(((Integer)this.extend.getValue()).intValue(), this.target, ((Boolean)this.antiScaffold.getValue()).booleanValue(), ((Boolean)this.antiStep.getValue()).booleanValue(), ((Boolean)this.legs.getValue()).booleanValue(), ((Boolean)this.platform.getValue()).booleanValue(), ((Boolean)this.antiDrop.getValue()).booleanValue(), ((Boolean)this.raytrace.getValue()).booleanValue(), ((Boolean)this.noScaffoldExtend.getValue()).booleanValue(), ((Boolean)this.face.getValue()).booleanValue());
    placeList(placeTargets);
    this.currentPlaceList = placeTargets;
  }
  
  private void doStaticTrap() {
    List<Vec3d> placeTargets = EntityUtil.targets(this.target.getPositionVector(), ((Boolean)this.antiScaffold.getValue()).booleanValue(), ((Boolean)this.antiStep.getValue()).booleanValue(), ((Boolean)this.legs.getValue()).booleanValue(), ((Boolean)this.platform.getValue()).booleanValue(), ((Boolean)this.antiDrop.getValue()).booleanValue(), ((Boolean)this.raytrace.getValue()).booleanValue(), ((Boolean)this.face.getValue()).booleanValue());
    placeList(placeTargets);
    this.currentPlaceList = placeTargets;
  }
  
  private void placeList(List<Vec3d> list) {
    list.sort((vec3d, vec3d2) -> Double.compare(mc.player.getDistanceSq(vec3d2.x, vec3d2.y, vec3d2.z), mc.player.getDistanceSq(vec3d.x, vec3d.y, vec3d.z)));
    list.sort(Comparator.comparingDouble(vec3d -> vec3d.y));
    for (Vec3d vec3d3 : list) {
      BlockPos position = new BlockPos(vec3d3);
      int placeability = BlockUtil.isPositionPlaceable(position, ((Boolean)this.raytrace.getValue()).booleanValue());
      if (((Boolean)this.entityCheck.getValue()).booleanValue() && placeability == 1 && (this.switchMode.getValue() == InventoryUtil.Switch.SILENT || (BlockTweaks.getINSTANCE().isOn() && ((Boolean)(BlockTweaks.getINSTANCE()).noBlock.getValue()).booleanValue())) && (this.retries.get(position) == null || ((Integer)this.retries.get(position)).intValue() < ((Integer)this.retryer.getValue()).intValue())) {
        placeBlock(position);
        this.retries.put(position, Integer.valueOf((this.retries.get(position) == null) ? 1 : (((Integer)this.retries.get(position)).intValue() + 1)));
        this.retryTimer.reset();
        continue;
      } 
      if (placeability != 3 || (((Boolean)this.antiSelf.getValue()).booleanValue() && MathUtil.areVec3dsAligned(mc.player.getPositionVector(), vec3d3)))
        continue; 
      placeBlock(position);
    } 
  }
  
  private boolean check() {
    isPlacing = false;
    this.didPlace = false;
    this.placements = 0;
    int obbySlot = -1;
    if (((Boolean)this.endPortals.getValue()).booleanValue()) {
      obbySlot = InventoryUtil.findHotbarBlock(BlockEndPortalFrame.class);
      if (obbySlot == -1)
        obbySlot = InventoryUtil.findHotbarBlock(BlockObsidian.class); 
    } else {
      obbySlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
    } 
    if (isOff())
      return true; 
    if (((Boolean)this.disable.getValue()).booleanValue() && this.startPos != null && !this.startPos.equals(EntityUtil.getRoundedBlockPos((Entity)mc.player))) {
      disable();
      return true;
    } 
    if (this.retryTimer.passedMs(2000L)) {
      this.retries.clear();
      this.retryTimer.reset();
    } 
    if (obbySlot == -1) {
      if (this.switchMode.getValue() != InventoryUtil.Switch.NONE) {
        if (((Boolean)this.info.getValue()).booleanValue())
          Command.sendMessage("<" + getDisplayName() + "> §cYou are out of Obsidian."); 
        disable();
      } 
      return true;
    } 
    if (mc.player.inventory.currentItem != this.lastHotbarSlot && mc.player.inventory.currentItem != obbySlot)
      this.lastHotbarSlot = mc.player.inventory.currentItem; 
    switchItem(true);
    this.isSneaking = EntityUtil.stopSneaking(this.isSneaking);
    this.target = getTarget(((Double)this.targetRange.getValue()).doubleValue(), (this.targetMode.getValue() == TargetMode.UNTRAPPED));
    return (this.target == null || (Phobos.moduleManager.isModuleEnabled("Freecam") && !((Boolean)this.freecam.getValue()).booleanValue()) || !this.timer.passedMs(((Integer)this.delay.getValue()).intValue()) || (this.switchMode.getValue() == InventoryUtil.Switch.NONE && mc.player.inventory.currentItem != InventoryUtil.findHotbarBlock(BlockObsidian.class)));
  }
  
  private EntityPlayer getTarget(double range, boolean trapped) {
    EntityPlayer target = null;
    double distance = Math.pow(range, 2.0D) + 1.0D;
    for (EntityPlayer player : mc.world.playerEntities) {
      if (EntityUtil.isntValid((Entity)player, range) || (this.pattern.getValue() == Pattern.STATIC && trapped && EntityUtil.isTrapped(player, ((Boolean)this.antiScaffold.getValue()).booleanValue(), ((Boolean)this.antiStep.getValue()).booleanValue(), ((Boolean)this.legs.getValue()).booleanValue(), ((Boolean)this.platform.getValue()).booleanValue(), ((Boolean)this.antiDrop.getValue()).booleanValue(), ((Boolean)this.face.getValue()).booleanValue())) || (this.pattern.getValue() != Pattern.STATIC && trapped && EntityUtil.isTrappedExtended(((Integer)this.extend.getValue()).intValue(), player, ((Boolean)this.antiScaffold.getValue()).booleanValue(), ((Boolean)this.antiStep.getValue()).booleanValue(), ((Boolean)this.legs.getValue()).booleanValue(), ((Boolean)this.platform.getValue()).booleanValue(), ((Boolean)this.antiDrop.getValue()).booleanValue(), ((Boolean)this.raytrace.getValue()).booleanValue(), ((Boolean)this.noScaffoldExtend.getValue()).booleanValue(), ((Boolean)this.face.getValue()).booleanValue())) || (EntityUtil.getRoundedBlockPos((Entity)mc.player).equals(EntityUtil.getRoundedBlockPos((Entity)player)) && ((Boolean)this.antiSelf.getValue()).booleanValue()) || Phobos.speedManager.getPlayerSpeed(player) > ((Double)this.speed.getValue()).doubleValue())
        continue; 
      if (target == null) {
        target = player;
        distance = mc.player.getDistanceSq((Entity)player);
        continue;
      } 
      if (mc.player.getDistanceSq((Entity)player) >= distance)
        continue; 
      target = player;
      distance = mc.player.getDistanceSq((Entity)player);
    } 
    return target;
  }
  
  private void placeBlock(BlockPos pos) {
    if (this.placements < ((Integer)this.blocksPerPlace.getValue()).intValue() && mc.player.getDistanceSq(pos) <= MathUtil.square(((Double)this.range.getValue()).doubleValue()) && switchItem(false)) {
      isPlacing = true;
      if (((Boolean)this.airPacket.getValue()).booleanValue() && ((Boolean)this.packet.getValue()).booleanValue())
        this.toAir.put(pos, mc.world.getBlockState(pos)); 
      this.isSneaking = this.smartRotate ? BlockUtil.placeBlockSmartRotate(pos, EnumHand.MAIN_HAND, true, (!((Boolean)this.airPacket.getValue()).booleanValue() && ((Boolean)this.packet.getValue()).booleanValue()), this.isSneaking) : BlockUtil.placeBlock(pos, EnumHand.MAIN_HAND, ((Boolean)this.rotate.getValue()).booleanValue(), (!((Boolean)this.airPacket.getValue()).booleanValue() && ((Boolean)this.packet.getValue()).booleanValue()), this.isSneaking);
      this.didPlace = true;
      this.placements++;
    } 
  }
  
  private boolean switchItem(boolean back) {
    boolean[] value = InventoryUtil.switchItem(back, this.lastHotbarSlot, this.switchedItem, (InventoryUtil.Switch)this.switchMode.getValue(), (((Boolean)this.endPortals.getValue()).booleanValue() && InventoryUtil.findHotbarBlock(BlockEndPortalFrame.class) != -1) ? BlockEndPortalFrame.class : BlockObsidian.class);
    this.switchedItem = value[0];
    return value[1];
  }
  
  public enum TargetMode {
    CLOSEST, UNTRAPPED;
  }
  
  public enum Pattern {
    STATIC, SMART, OPEN;
  }
}
