package me.earth.phobos.features.modules.player;

import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import me.earth.phobos.util.InventoryUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemEnderPearl;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import org.lwjgl.input.Mouse;

public class MCP extends Module {
  private final Setting<Mode> mode = register(new Setting("Mode", Mode.MIDDLECLICK));
  
  private final Setting<Boolean> stopRotation = register(new Setting("Rotation", Boolean.valueOf(true)));
  
  private final Setting<Boolean> antiFriend = register(new Setting("AntiFriend", Boolean.valueOf(true)));
  
  private final Setting<Integer> rotation = register(new Setting("Delay", Integer.valueOf(10), Integer.valueOf(0), Integer.valueOf(100), v -> ((Boolean)this.stopRotation.getValue()).booleanValue()));
  
  private boolean clicked = false;
  
  public MCP() {
    super("MCP", "Throws a pearl", Module.Category.PLAYER, false, false, false);
  }
  
  public void onEnable() {
    if (!fullNullCheck() && this.mode.getValue() == Mode.TOGGLE) {
      throwPearl();
      disable();
    } 
  }
  
  public void onTick() {
    if (this.mode.getValue() == Mode.MIDDLECLICK)
      if (Mouse.isButtonDown(2)) {
        if (!this.clicked)
          throwPearl(); 
        this.clicked = true;
      } else {
        this.clicked = false;
      }  
  }
  
  private void throwPearl() {
    Entity entity;
    RayTraceResult result;
    if (((Boolean)this.antiFriend.getValue()).booleanValue() && (result = mc.objectMouseOver) != null && result.typeOfHit == RayTraceResult.Type.ENTITY && entity = result.entityHit instanceof EntityPlayer)
      return; 
    int pearlSlot = InventoryUtil.findHotbarBlock(ItemEnderPearl.class);
    boolean offhand = (mc.player.getHeldItemOffhand().getItem() == Items.ENDER_PEARL), bl = offhand;
    if (pearlSlot != -1 || offhand) {
      int oldslot = mc.player.inventory.currentItem;
      if (!offhand)
        InventoryUtil.switchToHotbarSlot(pearlSlot, false); 
      mc.playerController.processRightClick((EntityPlayer)mc.player, (World)mc.world, offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND);
      if (!offhand)
        InventoryUtil.switchToHotbarSlot(oldslot, false); 
    } 
  }
  
  public enum Mode {
    TOGGLE, MIDDLECLICK;
  }
}
