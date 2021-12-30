package me.earth.phobos.features.modules.misc;

import me.earth.phobos.event.events.PacketEvent;
import me.earth.phobos.features.modules.Module;
import me.earth.phobos.features.setting.Setting;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class BuildHeight extends Module {
  private final Setting<Integer> height = register(new Setting("Height", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255)));
  
  public BuildHeight() {
    super("BuildHeight", "Allows you to place at build height", Module.Category.MISC, true, true, false);
  }
  
  @SubscribeEvent
  public void onPacketSend(PacketEvent.Send event) {
    CPacketPlayerTryUseItemOnBlock packet;
    if (event.getStage() == 0 && event.getPacket() instanceof CPacketPlayerTryUseItemOnBlock && (packet = (CPacketPlayerTryUseItemOnBlock)event.getPacket()).getPos().getY() >= ((Integer)this.height.getValue()).intValue() && packet.getDirection() == EnumFacing.UP)
      packet.placedBlockDirection = EnumFacing.DOWN; 
  }
}
