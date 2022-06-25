package swapper.intentions.tropical.module.movement.speed.impl;

import swapper.intentions.tropical.Tropical;
import swapper.intentions.tropical.event.impl.MoveEvent;
import swapper.intentions.tropical.event.impl.PacketEvent;
import swapper.intentions.tropical.event.impl.UpdateEvent;
import swapper.intentions.tropical.module.movement.Speed;
import swapper.intentions.tropical.module.movement.speed.SpeedMode;
import swapper.intentions.tropical.utils.MoveUtils;

public class BHop extends SpeedMode {
	public BHop() {
		super("BHop");
	}
	
	@Override
	public void onUpdate(UpdateEvent e) {
		Speed speedModule = ((Speed) Tropical.instance.moduleManager.getModuleByName("Speed"));
		mc.thePlayer.motionX = mc.thePlayer.motionZ = 0.0;
		double speed = speedModule.speed.getValue();
    	MoveUtils.strafe(speed);
    	if(mc.thePlayer.onGround && MoveUtils.isMoving()) {
    		mc.thePlayer.jump();
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
