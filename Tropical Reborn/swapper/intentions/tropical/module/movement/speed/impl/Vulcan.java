package swapper.intentions.tropical.module.movement.speed.impl;

import swapper.intentions.tropical.event.impl.MoveEvent;
import swapper.intentions.tropical.event.impl.PacketEvent;
import swapper.intentions.tropical.event.impl.UpdateEvent;
import swapper.intentions.tropical.module.movement.speed.SpeedMode;
import swapper.intentions.tropical.utils.MoveUtils;

public class Vulcan extends SpeedMode {
	public double ticks; //double bcuz might be testing more stuff and too lazy to restart
	public Vulcan() {
		super("Vulcan");
	}
	
	@Override
	public void onUpdate(UpdateEvent e) {
		if(!e.isPre())
			return;
		if(MoveUtils.isMoving()) {
			if (mc.thePlayer.onGround) {
				ticks = 0;
				mc.thePlayer.jump();
				MoveUtils.strafe(MoveUtils.getSpeed()*1.007);
			} else {
				ticks++;
				if(MoveUtils.getSpeed() < 0.177 + Math.random()*0.008)
					MoveUtils.strafe(0.201 + Math.random()*0.04);
				if(ticks == 4) {
					mc.thePlayer.motionY -= 0.178;
					if(MoveUtils.getSpeed() < 0.275)
						MoveUtils.strafe(MoveUtils.getSpeed());
				}
			}
		}
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
}
