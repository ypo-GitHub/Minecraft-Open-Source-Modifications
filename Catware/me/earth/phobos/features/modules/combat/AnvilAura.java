package me.earth.phobos.features.modules.combat;

import java.util.ArrayList;
import java.util.List;
import me.earth.phobos.event.events.PacketEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.BlockUtil;
import me.earth.phobos.util.InventoryUtil;
import me.earth.phobos.util.MathUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockObsidian;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AnvilAura extends Module {
  public Setting<Float> range = register(new Setting("Range", Float.valueOf(6.0F), Float.valueOf(0.0F), Float.valueOf(10.0F)));
  
  public Setting<Float> wallsRange = register(new Setting("WallsRange", Float.valueOf(3.5F), Float.valueOf(0.0F), Float.valueOf(10.0F)));
  
  public Setting<Integer> placeDelay = register(new Setting("PlaceDelay", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(500)));
  
  public Setting<Boolean> rotate = register(new Setting("Rotate", Boolean.valueOf(true)));
  
  public Setting<Boolean> packet = register(new Setting("Packet", Boolean.valueOf(true)));
  
  public Setting<Boolean> switcher = register(new Setting("Switch", Boolean.valueOf(true)));
  
  public Setting<Integer> rotations = register(new Setting("Spoofs", Integer.valueOf(1), Integer.valueOf(1), Integer.valueOf(20)));
  
  private float yaw = 0.0F;
  
  private float pitch = 0.0F;
  
  private boolean rotating = false;
  
  private int rotationPacketsSpoofed = 0;
  
  private EntityPlayer finalTarget;
  
  private BlockPos placeTarget;
  
  public AnvilAura() {
    super("AnvilAura", "Useless", Module.Category.COMBAT, true, true, false);
  }
  
  public void onTick() {
    doAnvilAura();
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
  
  public void doAnvilAura() {
    this.finalTarget = getTarget();
    if (this.finalTarget != null)
      this.placeTarget = getTargetPos((Entity)this.finalTarget); 
    if (this.placeTarget != null && this.finalTarget != null)
      placeAnvil(this.placeTarget); 
  }
  
  public void placeAnvil(BlockPos pos) {
    if (((Boolean)this.rotate.getValue()).booleanValue())
      rotateToPos(pos); 
    if (((Boolean)this.switcher.getValue()).booleanValue() && !isHoldingAnvil())
      doSwitch(); 
    BlockUtil.placeBlock(pos, EnumHand.MAIN_HAND, false, ((Boolean)this.packet.getValue()).booleanValue(), mc.player.isSneaking());
  }
  
  public boolean isHoldingAnvil() {
    int obbySlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
    return ((mc.player.getHeldItemMainhand().getItem() instanceof ItemBlock && ((ItemBlock)mc.player.getHeldItemMainhand().getItem()).getBlock() instanceof net.minecraft.block.BlockAnvil) || (mc.player.getHeldItemOffhand().getItem() instanceof ItemBlock && ((ItemBlock)mc.player.getHeldItemOffhand().getItem()).getBlock() instanceof net.minecraft.block.BlockAnvil));
  }
  
  public void doSwitch() {
    int obbySlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
    if (obbySlot == -1)
      for (int l = 0; l < 9; l++) {
        ItemStack stack = mc.player.inventory.getStackInSlot(l);
        Block block = ((ItemBlock)stack.getItem()).getBlock();
        if (block instanceof BlockObsidian)
          obbySlot = l; 
      }  
    if (obbySlot != -1)
      mc.player.inventory.currentItem = obbySlot; 
  }
  
  public EntityPlayer getTarget() {
    double shortestDistance = -1.0D;
    EntityPlayer target = null;
    for (EntityPlayer player : mc.world.playerEntities) {
      if (getPlaceableBlocksAboveEntity((Entity)player).isEmpty() || (shortestDistance != -1.0D && mc.player.getDistanceSq((Entity)player) >= MathUtil.square(shortestDistance)))
        continue; 
      shortestDistance = mc.player.getDistance((Entity)player);
      target = player;
    } 
    return target;
  }
  
  public BlockPos getTargetPos(Entity target) {
    double distance = -1.0D;
    BlockPos finalPos = null;
    for (BlockPos pos : getPlaceableBlocksAboveEntity(target)) {
      if (distance != -1.0D && mc.player.getDistanceSq(pos) >= MathUtil.square(distance))
        continue; 
      finalPos = pos;
      distance = mc.player.getDistance(pos.getX(), pos.getY(), pos.getZ());
    } 
    return finalPos;
  }
  
  public List<BlockPos> getPlaceableBlocksAboveEntity(Entity target) {
    BlockPos playerPos = new BlockPos(Math.floor(mc.player.posX), Math.floor(mc.player.posY), Math.floor(mc.player.posZ));
    ArrayList<BlockPos> positions = new ArrayList<>();
    BlockPos pos;
    for (int i = (int)Math.floor(mc.player.posY + 2.0D); i <= 256 && BlockUtil.isPositionPlaceable(pos = new BlockPos(Math.floor(mc.player.posX), i, Math.floor(mc.player.posZ)), false) != 0 && BlockUtil.isPositionPlaceable(pos, false) != -1 && BlockUtil.isPositionPlaceable(pos, false) != 2; i++)
      positions.add(pos); 
    return positions;
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
