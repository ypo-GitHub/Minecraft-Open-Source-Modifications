package swapper.intentions.tropical.module.movement.speed.impl;

import net.minecraft.network.play.server.S12PacketEntityVelocity;
import swapper.intentions.tropical.Tropical;
import swapper.intentions.tropical.event.impl.MoveEvent;
import swapper.intentions.tropical.event.impl.PacketEvent;
import swapper.intentions.tropical.event.impl.UpdateEvent;
import swapper.intentions.tropical.module.movement.Speed;
import swapper.intentions.tropical.module.movement.speed.SpeedMode;
import swapper.intentions.tropical.utils.MoveUtils;

public class Hypixel extends SpeedMode {
	public int ticks = 0;
	public Hypixel() {
		super("Hypixel");
	}
	
	@Override
	public void onUpdate(UpdateEvent e) {
		if(!e.isPre())
			return;
		ticks++;
		if(MoveUtils.isMoving()) {
			if(mc.thePlayer.hurtTime > 3)
				MoveUtils.strafe(MoveUtils.getSpeed()*1.025);
			if(mc.thePlayer.onGround) {
				MoveUtils.strafe(MoveUtils.getSpeed()*1.08);
				MoveUtils.strafe(MoveUtils.getSpeed()*MoveUtils.getSpeedPotMultiplier(0.09));
				mc.thePlayer.jump();
				ticks = 0;
			}else {
				mc.thePlayer.jumpMovementFactor = 0.0254f*MoveUtils.getSpeedPotMultiplier(0.125);
				if(Speed.smoothStrafe.getValue()) {
					float yaw = (float) (MoveUtils.getDirection() / 180 * Math.PI);
					mc.thePlayer.motionX -= (mc.thePlayer.motionX - (-Math.sin(yaw) * MoveUtils.getSpeed())) * 0.91F;
					mc.thePlayer.motionZ -= (mc.thePlayer.motionZ - (Math.cos(yaw) * MoveUtils.getSpeed())) * 0.91F;
				}else {
					MoveUtils.strafe(MoveUtils.getSpeed());
				}
			}
		}else {
			mc.thePlayer.motionX = mc.thePlayer.motionZ = 0.0;
		}
	}

	@Override
	public void onMove(MoveEvent e) {

    }

	@Override
	public void onPacket(PacketEvent e) {
		if(e.getPacket() instanceof S12PacketEntityVelocity) {
			if(((S12PacketEntityVelocity) e.getPacket()).getEntityID() != mc.thePlayer.getEntityId())
				return;
			//ticks = (int) Math.hypot(((S12PacketEntityVelocity) e.getPacket()).getMotionX(), ((S12PacketEntityVelocity) e.getPacket()).getMotionZ());
		}
	}

	@Override
	public void onEnable() {
		ticks = 0;
	}
}
