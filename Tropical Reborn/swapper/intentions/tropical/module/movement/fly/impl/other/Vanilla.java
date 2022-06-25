package swapper.intentions.tropical.module.movement.fly.impl.other;

import swapper.intentions.tropical.Tropical;
import swapper.intentions.tropical.event.impl.BlockCollisionEvent;
import swapper.intentions.tropical.event.impl.MoveEvent;
import swapper.intentions.tropical.event.impl.PacketEvent;
import swapper.intentions.tropical.event.impl.UpdateEvent;
import swapper.intentions.tropical.module.movement.Fly;
import swapper.intentions.tropical.module.movement.fly.FlyMode;
import swapper.intentions.tropical.utils.MoveUtils;

public class Vanilla extends FlyMode {
	public Vanilla() {
		super("Vanilla");
	}
	
	@Override
	public void onUpdate(UpdateEvent e) {
		Fly fly = ((Fly) Tropical.instance.moduleManager.getModuleByName("Flight"));
		mc.thePlayer.motionX = mc.thePlayer.motionZ = 0.0;
		double speed = fly.speed.getValue();
    	mc.thePlayer.motionY = (mc.gameSettings.keyBindJump.isKeyDown() ? speed :
    							mc.gameSettings.keyBindSneak.isKeyDown() ? -speed :
    							0.0);
    	MoveUtils.strafe(speed);
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

	@Override
	public void onBlockCollision(BlockCollisionEvent e) {
		// TODO Auto-generated method stub
		
	}
}
