package swapper.intentions.tropical.module.movement.fly.impl.other;

import swapper.intentions.tropical.Tropical;
import swapper.intentions.tropical.event.impl.BlockCollisionEvent;
import swapper.intentions.tropical.event.impl.MoveEvent;
import swapper.intentions.tropical.event.impl.PacketEvent;
import swapper.intentions.tropical.event.impl.UpdateEvent;
import swapper.intentions.tropical.module.movement.Fly;
import swapper.intentions.tropical.module.movement.fly.FlyMode;
import swapper.intentions.tropical.utils.MoveUtils;

public class Custom extends FlyMode {
	public double ticks; //double bcuz might be testing more stuff and too lazy to restart
	public Custom() {
		super("Custom");
	}
	
	@Override
	public void onUpdate(UpdateEvent e) {
		Fly fly = (Fly) Tropical.instance.moduleManager.getModuleByName("Flight");
		mc.timer.timerSpeed = fly.cTimer.getValue().floatValue();
		if(fly.cAlways.getValue() && fly.cSet.getValue())
			MoveUtils.strafe(fly.speed.getValue());
		if(!e.isPre() && !fly.cPost.getValue())
			return;
		if(fly.cPost.getValue() && e.isPre())
			return;
		if(mc.thePlayer.ticksExisted % Math.floor(fly.cTicks.getValue()) == 0) {
			mc.thePlayer.motionY = fly.cY.getValue();
			if(fly.cSet.getValue())
				MoveUtils.strafe(fly.speed.getValue());
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

	@Override
	public void onBlockCollision(BlockCollisionEvent e) {

	}
}
