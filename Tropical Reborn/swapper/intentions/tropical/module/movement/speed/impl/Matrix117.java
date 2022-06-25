package swapper.intentions.tropical.module.movement.speed.impl;

import swapper.intentions.tropical.Tropical;
import swapper.intentions.tropical.event.impl.MoveEvent;
import swapper.intentions.tropical.event.impl.PacketEvent;
import swapper.intentions.tropical.event.impl.UpdateEvent;
import swapper.intentions.tropical.module.movement.Speed;
import swapper.intentions.tropical.module.movement.speed.SpeedMode;
import swapper.intentions.tropical.utils.MoveUtils;

public class Matrix117 extends SpeedMode {
	public Matrix117() {
		super("Matrix 1.17");
	}
	
	@Override
	public void onUpdate(UpdateEvent e) {
		if(!e.isPre())
			return;
	}

	@Override
	public void onMove(MoveEvent e) {
		Speed speedModule = ((Speed) Tropical.instance.moduleManager.getModuleByName("Speed"));
		double speed = speedModule.speed.getValue();
		if (mc.thePlayer.isMovingOnGround()) {
            if (mc.thePlayer.ticksExisted % 3 == 0) {
            	MoveUtils.strafe(speed);
                mc.thePlayer.motionY = 0.4;
                e.setMotionY(0.4);
            } else {
                e.setMotionX(0);
                e.setMotionZ(0);
                mc.thePlayer.motionX = mc.thePlayer.motionZ = 0.0;
            }
        }
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
