package me.earth.phobos.features.modules.combat;

import java.util.ArrayList;
import java.util.List;
import me.earth.phobos.event.events.PacketEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.BlockUtil;
import me.earth.phobos.util.DamageUtil;
import me.earth.phobos.util.EntityUtil;
import me.earth.phobos.util.MathUtil;
import me.earth.phobos.util.Timer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AntiCrystal extends Module {
  private final List<BlockPos> targets = new ArrayList<>();
  
  private final Timer timer = new Timer();
  
  private final Timer breakTimer = new Timer();
  
  private final Timer checkTimer = new Timer();
  
  public Setting<Float> range = register(new Setting("Range", Float.valueOf(6.0F), Float.valueOf(0.0F), Float.valueOf(10.0F)));
  
  public Setting<Float> wallsRange = register(new Setting("WallsRange", Float.valueOf(3.5F), Float.valueOf(0.0F), Float.valueOf(10.0F)));
  
  public Setting<Float> minDmg = register(new Setting("MinDmg", Float.valueOf(6.0F), Float.valueOf(0.0F), Float.valueOf(100.0F)));
  
  public Setting<Float> selfDmg = register(new Setting("SelfDmg", Float.valueOf(2.0F), Float.valueOf(0.0F), Float.valueOf(10.0F)));
  
  public Setting<Integer> placeDelay = register(new Setting("PlaceDelay", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(500)));
  
  public Setting<Integer> breakDelay = register(new Setting("BreakDelay", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(500)));
  
  public Setting<Integer> checkDelay = register(new Setting("CheckDelay", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(500)));
  
  public Setting<Integer> wasteAmount = register(new Setting("WasteAmount", Integer.valueOf(1), Integer.valueOf(1), Integer.valueOf(5)));
  
  public Setting<Boolean> switcher = register(new Setting("Switch", Boolean.valueOf(true)));
  
  public Setting<Boolean> rotate = register(new Setting("Rotate", Boolean.valueOf(true)));
  
  public Setting<Boolean> packet = register(new Setting("Packet", Boolean.valueOf(true)));
  
  public Setting<Integer> rotations = register(new Setting("Spoofs", Integer.valueOf(1), Integer.valueOf(1), Integer.valueOf(20)));
  
  private float yaw = 0.0F;
  
  private float pitch = 0.0F;
  
  private boolean rotating = false;
  
  private int rotationPacketsSpoofed = 0;
  
  private Entity breakTarget;
  
  public AntiCrystal() {
    super("AntiCrystal", "Hacker shit", Module.Category.COMBAT, true, true, false);
  }
  
  public void onToggle() {
    this.rotating = false;
  }
  
  private Entity getDeadlyCrystal() {
    Entity bestcrystal = null;
    float highestDamage = 0.0F;
    for (Entity crystal : mc.world.loadedEntityList) {
      float damage;
      if (!(crystal instanceof net.minecraft.entity.item.EntityEnderCrystal) || mc.player.getDistanceSq(crystal) > 169.0D || (damage = DamageUtil.calculateDamage(crystal, (Entity)mc.player)) < ((Float)this.minDmg.getValue()).floatValue())
        continue; 
      if (bestcrystal == null) {
        bestcrystal = crystal;
        highestDamage = damage;
        continue;
      } 
      if (damage <= highestDamage)
        continue; 
      bestcrystal = crystal;
      highestDamage = damage;
    } 
    return bestcrystal;
  }
  
  private int getSafetyCrystals(Entity deadlyCrystal) {
    int count = 0;
    for (Entity entity : mc.world.loadedEntityList) {
      float damage;
      if (entity instanceof net.minecraft.entity.item.EntityEnderCrystal || (damage = DamageUtil.calculateDamage(entity, (Entity)mc.player)) > 2.0F || deadlyCrystal.getDistanceSq(entity) > 144.0D)
        continue; 
      count++;
    } 
    return count;
  }
  
  private BlockPos getPlaceTarget(Entity deadlyCrystal) {
    BlockPos closestPos = null;
    float smallestDamage = 10.0F;
    for (BlockPos pos : BlockUtil.possiblePlacePositions(((Float)this.range.getValue()).floatValue())) {
      float damage = DamageUtil.calculateDamage(pos, (Entity)mc.player);
      if (damage > 2.0F || deadlyCrystal.getDistanceSq(pos) > 144.0D || (mc.player.getDistanceSq(pos) >= MathUtil.square(((Float)this.wallsRange.getValue()).floatValue()) && BlockUtil.rayTracePlaceCheck(pos, true, 1.0F)))
        continue; 
      if (closestPos == null) {
        smallestDamage = damage;
        closestPos = pos;
        continue;
      } 
      if (damage >= smallestDamage && (damage != smallestDamage || mc.player.getDistanceSq(pos) >= mc.player.getDistanceSq(closestPos)))
        continue; 
      smallestDamage = damage;
      closestPos = pos;
    } 
    return closestPos;
  }
  
  @SubscribeEvent
  public void onPacketSend(PacketEvent.Send event) {
    if (event.getStage() == 0 && ((Boolean)this.rotate.getValue()).booleanValue() && this.rotating) {
      if (event.getPacket() instanceof CPacketPlayer) {
        CPacketPlayer packet = (CPacketPlayer)event.getPacket();
        packet.yaw = this.yaw;
        packet.pitch = this.pitch;
      } 
      this.rotationPacketsSpoofed++;
      if (this.rotationPacketsSpoofed >= ((Integer)this.rotations.getValue()).intValue()) {
        this.rotating = false;
        this.rotationPacketsSpoofed = 0;
      } 
    } 
  }
  
  public void onTick() {
    if (!fullNullCheck() && this.checkTimer.passedMs(((Integer)this.checkDelay.getValue()).intValue())) {
      Entity deadlyCrystal = getDeadlyCrystal();
      if (deadlyCrystal != null) {
        BlockPos placeTarget = getPlaceTarget(deadlyCrystal);
        if (placeTarget != null)
          this.targets.add(placeTarget); 
        placeCrystal(deadlyCrystal);
        this.breakTarget = getBreakTarget(deadlyCrystal);
        breakCrystal();
      } 
      this.checkTimer.reset();
    } 
  }
  
  public Entity getBreakTarget(Entity deadlyCrystal) {
    Entity smallestCrystal = null;
    float smallestDamage = 10.0F;
    for (Entity entity : mc.world.loadedEntityList) {
      float damage;
      if (!(entity instanceof net.minecraft.entity.item.EntityEnderCrystal) || (damage = DamageUtil.calculateDamage(entity, (Entity)mc.player)) > ((Float)this.selfDmg.getValue()).floatValue() || entity.getDistanceSq(deadlyCrystal) > 144.0D || (mc.player.getDistanceSq(entity) > MathUtil.square(((Float)this.wallsRange.getValue()).floatValue()) && EntityUtil.rayTraceHitCheck(entity, true)))
        continue; 
      if (smallestCrystal == null) {
        smallestCrystal = entity;
        smallestDamage = damage;
        continue;
      } 
      if (damage >= smallestDamage && (smallestDamage != damage || mc.player.getDistanceSq(entity) >= mc.player.getDistanceSq(smallestCrystal)))
        continue; 
      smallestCrystal = entity;
      smallestDamage = damage;
    } 
    return smallestCrystal;
  }
  
  private void placeCrystal(Entity deadlyCrystal) {
    boolean offhand = (mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL), bl = offhand;
    if (this.timer.passedMs(((Integer)this.placeDelay.getValue()).intValue()) && (((Boolean)this.switcher.getValue()).booleanValue() || mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL || offhand) && !this.targets.isEmpty() && getSafetyCrystals(deadlyCrystal) <= ((Integer)this.wasteAmount.getValue()).intValue()) {
      if (((Boolean)this.switcher.getValue()).booleanValue() && mc.player.getHeldItemMainhand().getItem() != Items.END_CRYSTAL && !offhand)
        doSwitch(); 
      rotateToPos(this.targets.get(this.targets.size() - 1));
      BlockUtil.placeCrystalOnBlock(this.targets.get(this.targets.size() - 1), offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, true, true);
      this.timer.reset();
    } 
  }
  
  private void doSwitch() {
    int crystalSlot = (mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL) ? mc.player.inventory.currentItem : -1, n = crystalSlot;
    if (crystalSlot == -1)
      for (int l = 0; l < 9; ) {
        if (mc.player.inventory.getStackInSlot(l).getItem() != Items.END_CRYSTAL) {
          l++;
          continue;
        } 
        crystalSlot = l;
      }  
    if (crystalSlot != -1)
      mc.player.inventory.currentItem = crystalSlot; 
  }
  
  private void breakCrystal() {
    if (this.breakTimer.passedMs(((Integer)this.breakDelay.getValue()).intValue()) && this.breakTarget != null && DamageUtil.canBreakWeakness((EntityPlayer)mc.player)) {
      rotateTo(this.breakTarget);
      EntityUtil.attackEntity(this.breakTarget, ((Boolean)this.packet.getValue()).booleanValue(), true);
      this.breakTimer.reset();
      this.targets.clear();
    } 
  }
  
  private void rotateTo(Entity entity) {
    if (((Boolean)this.rotate.getValue()).booleanValue()) {
      float[] angle = MathUtil.calcAngle(mc.player.getPositionEyes(mc.getRenderPartialTicks()), entity.getPositionVector());
      this.yaw = angle[0];
      this.pitch = angle[1];
      this.rotating = true;
    } 
  }
  
  private void rotateToPos(BlockPos pos) {
    if (((Boolean)this.rotate.getValue()).booleanValue()) {
      float[] angle = MathUtil.calcAngle(mc.player.getPositionEyes(mc.getRenderPartialTicks()), new Vec3d((pos.getX() + 0.5F), (pos.getY() - 0.5F), (pos.getZ() + 0.5F)));
      this.yaw = angle[0];
      this.pitch = angle[1];
      this.rotating = true;
    } 
  }
}
