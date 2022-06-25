package vestige.module.impl.misc;

import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition;
import net.minecraft.network.play.client.C03PacketPlayer.C06PacketPlayerPosLook;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import vestige.event.Event;
import vestige.event.impl.EventReceivePacket;
import vestige.module.Category;
import vestige.module.Module;
import vestige.util.network.PacketUtils;

public class NoRotate extends Module {

	public NoRotate() {
		super("NoRotate", Category.MISC);
	}
	
	public void onEvent(Event event) {
		if(event instanceof EventReceivePacket) {
			if(!mc.getNetHandler().doneLoadingTerrain) {
				return;
			}
			
			EventReceivePacket e = (EventReceivePacket) event;
			if(e.getPacket() instanceof S08PacketPlayerPosLook) {
				S08PacketPlayerPosLook packet = (S08PacketPlayerPosLook) e.getPacket();
				packet.setYaw(mc.thePlayer.rotationYaw);
				packet.setPitch(mc.thePlayer.rotationPitch);
				//e.setCancelled(true);
				//PacketUtils.sendPacketNoEvent(new C06PacketPlayerPosLook(packet.getX(), packet.getY(), packet.getZ(), packet.getYaw(), packet.getPitch(), false));
				//mc.thePlayer.setPosition(packet.getX(), packet.getY(), packet.getZ());
			}
		}
	}

}