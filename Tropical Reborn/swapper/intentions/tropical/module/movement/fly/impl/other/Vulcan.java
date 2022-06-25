package swapper.intentions.tropical.module.movement.fly.impl.other;

import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import swapper.intentions.tropical.event.impl.BlockCollisionEvent;
import swapper.intentions.tropical.event.impl.MoveEvent;
import swapper.intentions.tropical.event.impl.PacketEvent;
import swapper.intentions.tropical.event.impl.UpdateEvent;
import swapper.intentions.tropical.module.movement.fly.FlyMode;
import swapper.intentions.tropical.utils.MoveUtils;

public class Vulcan extends FlyMode {
	public double ticks = 0;
	public boolean hurt = false;
	
	public Vulcan() {
		super("Vulcan DMG LJ");
	}
	
	@Override
	public void onEnable() {
		ticks = 0;
		hurt = false;
	}
	
	@Override
	public void onUpdate(UpdateEvent e) {
		if(!e.isPre())
			return;
		ticks++;
		if(ticks == 20) {
			mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,mc.thePlayer.posY+3.25,mc.thePlayer.posZ,false));
			mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,mc.thePlayer.posY,mc.thePlayer.posZ,false));
			mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,mc.thePlayer.posY,mc.thePlayer.posZ,true));
			mc.thePlayer.jump();
			MoveUtils.strafe(9.5f);
		}
		if(ticks == 21) {
			MoveUtils.strafe(0.221f);
		}
	}
	
	@Override
	public void onMove(MoveEvent e) {
		if(ticks < 20) {
			e.setCancelled(true);
		}
	}

	@Override
	public void onPacket(PacketEvent e) {
		if(ticks < 20) {
			if(e.getPacket() instanceof C03PacketPlayer) {
				e.setCancelled(true);
			}
		}
		if(e.getPacket() instanceof S12PacketEntityVelocity)
			e.setCancelled(true);
	}

	@Override
	public void onBlockCollision(BlockCollisionEvent e) {
		// TODO Auto-generated method stub
		
	}
}
