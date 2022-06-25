package swapper.intentions.tropical.module.movement.fly;

import net.minecraft.client.Minecraft;
import swapper.intentions.tropical.event.impl.BlockCollisionEvent;
import swapper.intentions.tropical.event.impl.MoveEvent;
import swapper.intentions.tropical.event.impl.PacketEvent;
import swapper.intentions.tropical.event.impl.UpdateEvent;

public abstract class FlyMode {
	private String name;
	public Minecraft mc = Minecraft.getMinecraft();
	
	public FlyMode(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public abstract void onEnable();
	
	public abstract void onUpdate(UpdateEvent e);
	
	public abstract void onMove(MoveEvent e);

	public abstract void onPacket(PacketEvent e);

	public abstract void onBlockCollision(BlockCollisionEvent e);
}
