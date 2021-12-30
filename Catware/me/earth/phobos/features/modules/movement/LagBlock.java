package me.earth.phobos.features.modules.movement;

import me.earth.phobos.event.events.UpdateWalkingPlayerEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.BlockUtil;
import me.earth.phobos.util.InventoryUtil;
import me.earth.phobos.util.RotationUtil;
import me.earth.phobos.util.Timer;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockObsidian;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class LagBlock extends Module {
  private static LagBlock INSTANCE;
  
  private final Timer timer = new Timer();
  
  private final Setting<Boolean> packet = register(new Setting("Packet", Boolean.valueOf(true)));
  
  private final Setting<Boolean> invalidPacket = register(new Setting("InvalidPacket", Boolean.valueOf(false)));
  
  private final Setting<Integer> rotations = register(new Setting("Rotations", Integer.valueOf(5), Integer.valueOf(1), Integer.valueOf(10)));
  
  private final Setting<Integer> timeOut = register(new Setting("TimeOut", Integer.valueOf(194), Integer.valueOf(0), Integer.valueOf(1000)));
  
  private BlockPos startPos;
  
  private int lastHotbarSlot = -1;
  
  private int blockSlot = -1;
  
  public LagBlock() {
    super("BlockLag", "Lags You back", Module.Category.MOVEMENT, true, true, false);
    INSTANCE = this;
  }
  
  public static LagBlock getInstance() {
    if (INSTANCE == null)
      INSTANCE = new LagBlock(); 
    return INSTANCE;
  }
  
  public void onEnable() {
    this.lastHotbarSlot = -1;
    this.blockSlot = -1;
    if (fullNullCheck()) {
      disable();
      return;
    } 
    this.blockSlot = findBlockSlot();
    this.startPos = new BlockPos(mc.player.getPositionVector());
    if (!BlockUtil.isElseHole(this.startPos) || this.blockSlot == -1) {
      disable();
      return;
    } 
    mc.player.jump();
    this.timer.reset();
  }
  
  @SubscribeEvent
  public void onUpdateWalkingPlayer(UpdateWalkingPlayerEvent event) {
    if (event.getStage() != 0 || !this.timer.passedMs(((Integer)this.timeOut.getValue()).intValue()))
      return; 
    this.lastHotbarSlot = mc.player.inventory.currentItem;
    InventoryUtil.switchToHotbarSlot(this.blockSlot, false);
    for (int i = 0; i < ((Integer)this.rotations.getValue()).intValue(); i++)
      RotationUtil.faceVector(new Vec3d((Vec3i)this.startPos), true); 
    BlockUtil.placeBlock(this.startPos, (this.blockSlot == -2) ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, false, ((Boolean)this.packet.getValue()).booleanValue(), mc.player.isSneaking());
    InventoryUtil.switchToHotbarSlot(this.lastHotbarSlot, false);
    if (((Boolean)this.invalidPacket.getValue()).booleanValue())
      mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, 1337.0D, mc.player.posZ, true)); 
    disable();
  }
  
  private int findBlockSlot() {
    int obbySlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
    if (obbySlot == -1) {
      if (InventoryUtil.isBlock(mc.player.getHeldItemOffhand().getItem(), BlockObsidian.class))
        return -2; 
      int echestSlot = InventoryUtil.findHotbarBlock(BlockEnderChest.class);
      if (echestSlot == -1 && InventoryUtil.isBlock(mc.player.getHeldItemOffhand().getItem(), BlockEnderChest.class))
        return -2; 
      return -1;
    } 
    return obbySlot;
  }
}
