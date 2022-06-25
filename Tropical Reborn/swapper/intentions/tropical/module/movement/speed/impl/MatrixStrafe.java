package swapper.intentions.tropical.module.movement.speed.impl;

import swapper.intentions.tropical.Tropical;
import swapper.intentions.tropical.event.impl.MoveEvent;
import swapper.intentions.tropical.event.impl.PacketEvent;
import swapper.intentions.tropical.event.impl.UpdateEvent;
import swapper.intentions.tropical.module.movement.Speed;
import swapper.intentions.tropical.module.movement.speed.SpeedMode;
import swapper.intentions.tropical.utils.MoveUtils;

public class MatrixStrafe extends SpeedMode {
	public MatrixStrafe() {
		super("Matrix Strafe");
	}
	
	@Override
	public void onUpdate(UpdateEvent e) {
		if(!e.isPre())
			return;
		if(mc.thePlayer.onGround && MoveUtils.isMoving()) {
			mc.thePlayer.jump();
			mc.timer.timerSpeed = 0.85f;
		}else {
			if(mc.timer.timerSpeed < 1.2)
				mc.timer.timerSpeed += mc.timer.timerSpeed/16;
			if(mc.thePlayer.fallDistance > 0.6 && mc.timer.timerSpeed > 0.2)
				mc.timer.timerSpeed -= mc.timer.timerSpeed/9;
		}
		if(MoveUtils.getSpeed() < 0.177) {
			MoveUtils.strafe(0.221);
		}
	}

	@Override
	public void onMove(MoveEvent e) {

	}

	@Override
	public void onPacket(PacketEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onEnable() {
		// TODO Auto-generated method stub
		
	}
}
