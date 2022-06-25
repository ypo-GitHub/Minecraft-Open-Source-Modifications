package swapper.intentions.tropical.module.movement.fly.impl.other;

import net.minecraft.network.play.server.S12PacketEntityVelocity;
import swapper.intentions.tropical.event.impl.BlockCollisionEvent;
import swapper.intentions.tropical.event.impl.MoveEvent;
import swapper.intentions.tropical.event.impl.PacketEvent;
import swapper.intentions.tropical.event.impl.UpdateEvent;
import swapper.intentions.tropical.module.movement.fly.FlyMode;
import swapper.intentions.tropical.utils.MoveUtils;

public class Zonecraft extends FlyMode {
	public double ticks; //double bcuz might be testing more stuff and too lazy to restart
	public Zonecraft() {
		super("Zonecraft");
	}
	
	@Override
	public void onUpdate(UpdateEvent e) {
		if(!e.isPre())
			return;
	}

	@Override
	public void onMove(MoveEvent e) {
		if(mc.thePlayer.onGround) {
			mc.thePlayer.jump();
			e.setMotionY(.42F);
		}else {
			mc.thePlayer.motionY = 0.0;
			e.setMotionY(mc.thePlayer.ticksExisted % 2 != 0 ? 0 : 1.0E-32);
			if(mc.thePlayer.ticksExisted % 20 < 10)
				mc.timer.timerSpeed = 1.25f;
			else
				mc.timer.timerSpeed = 0.8f;
			if(mc.thePlayer.ticksExisted % 20 == 9)
				MoveUtils.strafe(MoveUtils.getSpeed()*1.125);
			if(mc.thePlayer.ticksExisted % 20 == 1)
				MoveUtils.strafe(0.2783*1.2);
		}
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

	}
}
