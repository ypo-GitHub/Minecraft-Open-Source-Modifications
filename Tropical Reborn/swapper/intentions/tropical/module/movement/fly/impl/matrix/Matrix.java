package swapper.intentions.tropical.module.movement.fly.impl.matrix;

import net.minecraft.item.ItemEnderPearl;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import swapper.intentions.tropical.event.impl.BlockCollisionEvent;
import swapper.intentions.tropical.event.impl.MoveEvent;
import swapper.intentions.tropical.event.impl.PacketEvent;
import swapper.intentions.tropical.event.impl.UpdateEvent;
import swapper.intentions.tropical.module.movement.fly.FlyMode;
import swapper.intentions.tropical.utils.ChatUtils;
import swapper.intentions.tropical.utils.MoveUtils;

public class Matrix extends FlyMode {
	public double ticks = 0;
	public boolean hasPearl;
	public boolean tp;
	
	public Matrix() {
		super("Matrix");
	}
	
	@Override
	public void onEnable() {
		ticks = 0;
		hasPearl = true;
		tp = false;
		if(mc.thePlayer.getHeldItem() == null || mc.thePlayer.getHeldItem() != null && !(mc.thePlayer.getHeldItem().getItem() instanceof ItemEnderPearl)) {
			ChatUtils.clientMessage("You must hold a pearl in your hand.", "Fly");
			hasPearl = false;
		}
	}
	
	@Override
	public void onUpdate(UpdateEvent e) {
		if(!e.isPre() || !hasPearl)
			return;
		ticks--;
		if(ticks == -1 && !tp) {
			e.setRotationPitch(19.25f);
			mc.thePlayer.sendQueue.addToSendQueueNoEvent(new C08PacketPlayerBlockPlacement(mc.thePlayer.getHeldItem()));
			tp = false;
		}
		if(tp) {
			if(ticks > 10) {
				if(mc.thePlayer.onGround) {
					mc.thePlayer.jump();
				}
				MoveUtils.strafe(2.5f - (ticks - 10)*0.15);
				ChatUtils.clientMessage("THE BOJAXHIUS ARE COMING FOR YOU", "VASTCAST");
			}else if(ticks > 3) {
				mc.thePlayer.motionY = 0.0;
			}
		}
	}
	
	@Override
	public void onMove(MoveEvent e) {
	}

	@Override
	public void onPacket(PacketEvent e) {
		if(e.getPacket() instanceof S12PacketEntityVelocity) {
			e.setCancelled(true);
		}
		if(e.getPacket() instanceof S08PacketPlayerPosLook) {
			if(!tp) {
				tp = true;
				ticks = 20;
			}
		}
	}

	@Override
	public void onBlockCollision(BlockCollisionEvent e) {
		// TODO Auto-generated method stub
		
	}
}
