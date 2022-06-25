package swapper.intentions.tropical.module.movement.speed;

import net.minecraft.client.Minecraft;
import swapper.intentions.tropical.event.impl.MoveEvent;
import swapper.intentions.tropical.event.impl.PacketEvent;
import swapper.intentions.tropical.event.impl.StrafeEvent;
import swapper.intentions.tropical.event.impl.UpdateEvent;

public abstract class SpeedMode {
	private String name;
	public Minecraft mc = Minecraft.getMinecraft();
	
	public SpeedMode(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void onEnable() {}
	
	public void onUpdate(UpdateEvent e) {}
	
	public void onMove(MoveEvent e) {}

	public void onPacket(PacketEvent e) {}

	public void onStrafeEvent(StrafeEvent e) {}
}
