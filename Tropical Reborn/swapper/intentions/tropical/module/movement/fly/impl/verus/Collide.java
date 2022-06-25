package swapper.intentions.tropical.module.movement.fly.impl.verus;

import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.util.AxisAlignedBB;
import swapper.intentions.tropical.event.impl.BlockCollisionEvent;
import swapper.intentions.tropical.event.impl.MoveEvent;
import swapper.intentions.tropical.event.impl.PacketEvent;
import swapper.intentions.tropical.event.impl.UpdateEvent;
import swapper.intentions.tropical.module.movement.fly.FlyMode;

public class Collide extends FlyMode {
	public double ticks; //double bcuz might be testing more stuff and too lazy to restart
	public Collide() {
		super("Collide");
	}
	
	@Override
	public void onUpdate(UpdateEvent e) {
		if(!e.isPre())
			return;
	}

	@Override
	public void onMove(MoveEvent e) {
	}

	@Override
	public void onPacket(PacketEvent e) {

	}

	@Override
	public void onEnable() {
		ticks = 0;
	}

	@Override
	public void onBlockCollision(BlockCollisionEvent e) {
		if(!mc.thePlayer.movementInput.sneak)
			e.setAxisAlignedBB(AxisAlignedBB.fromBounds(mc.thePlayer.posX, mc.thePlayer.posY-1, mc.thePlayer.posZ, mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ));
	}
}
