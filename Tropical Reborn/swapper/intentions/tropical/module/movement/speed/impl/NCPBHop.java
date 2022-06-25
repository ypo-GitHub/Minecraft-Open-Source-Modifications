package swapper.intentions.tropical.module.movement.speed.impl;

import swapper.intentions.tropical.Tropical;
import swapper.intentions.tropical.event.impl.MoveEvent;
import swapper.intentions.tropical.event.impl.PacketEvent;
import swapper.intentions.tropical.event.impl.UpdateEvent;
import swapper.intentions.tropical.module.movement.Speed;
import swapper.intentions.tropical.module.movement.speed.SpeedMode;
import swapper.intentions.tropical.utils.MoveUtils;

public class NCPBHop extends SpeedMode {
	public NCPBHop() {
		super("NCP BHop");
	}
	
	@Override
	public void onUpdate(UpdateEvent e) {
		if(!e.isPre())
			return;
		if(MoveUtils.isMoving()) {
	    	if(mc.thePlayer.onGround) {
	    		if(mc.thePlayer.hurtTime != 0)
	        		MoveUtils.strafe(MoveUtils.getSpeed()*1.075);
	    		MoveUtils.strafe(MoveUtils.getSpeed()*1.08);
	        	MoveUtils.strafe(MoveUtils.getSpeed()*MoveUtils.getSpeedPotMultiplier(0.1));
	    		mc.thePlayer.jump();
	    	}else {
	    		mc.thePlayer.jumpMovementFactor = 0.0265f*MoveUtils.getSpeedPotMultiplier(0.15);
	    		MoveUtils.strafe(MoveUtils.getSpeed());
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onEnable() {
		// TODO Auto-generated method stub
		
	}
}
