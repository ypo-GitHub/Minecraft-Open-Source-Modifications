package swapper.intentions.tropical.module.player;

import com.google.common.eventbus.Subscribe;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import swapper.intentions.tropical.event.PacketDirection;
import swapper.intentions.tropical.event.impl.PacketEvent;
import swapper.intentions.tropical.event.impl.UpdateEvent;
import swapper.intentions.tropical.module.Module;
import swapper.intentions.tropical.settings.settings.ModeSetting;
import swapper.intentions.tropical.utils.PacketUtil;

import java.util.ArrayList;

public class Blink extends Module {
	public ModeSetting mode;
	public ArrayList<Packet> packets = new ArrayList<>();
	public ArrayList<S12PacketEntityVelocity> velocityPackets = new ArrayList<>();

	public Blink() {
		super("Blink", "drip clicktp", 0x0, Category.PLAYER);
	}

	@Override
	protected void onEnable() {

	}
	
	/**
	 * @param e
	 */
	@Subscribe
	public void onUpdate(UpdateEvent e) {
		if(!e.isPre())
			return;
		setSuffix("Client: " + packets.size() + " / Server: " + velocityPackets.size());
	}
	
	@Subscribe
	public void onPacket(PacketEvent e) {
		if(mc.getNetHandler() == null || !mc.getNetHandler().doneLoadingTerrain)
			return;
		if(e.getPacketDirection() == PacketDirection.OUTBOUND) {
			packets.add(e.getPacket());
			e.setCancelled(true);
		}else {
			if(e.getPacket() instanceof S12PacketEntityVelocity) {
				if(((S12PacketEntityVelocity) e.getPacket()).getEntityID() == mc.thePlayer.getEntityId()){
					e.setCancelled(true);
					velocityPackets.add(e.getPacket());
				}
			}
		}
	}
	
	@Override
	protected void onDisable() {
		for(Packet p : packets) {
			PacketUtil.sendPacketNoEvent(p);
		}
		packets.clear();
		for(S12PacketEntityVelocity p : velocityPackets) {
			mc.getNetHandler().handleEntityVelocity(p);
		}
		velocityPackets.clear();
	}
}
