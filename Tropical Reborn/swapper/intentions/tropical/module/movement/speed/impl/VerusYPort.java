	package swapper.intentions.tropical.module.movement.speed.impl;

	import swapper.intentions.tropical.event.impl.MoveEvent;
	import swapper.intentions.tropical.event.impl.PacketEvent;
	import swapper.intentions.tropical.event.impl.UpdateEvent;
	import swapper.intentions.tropical.module.movement.speed.SpeedMode;
	import swapper.intentions.tropical.utils.MoveUtils;

    public class VerusYPort extends SpeedMode {
	public double ticks; //double bcuz might be testing more stuff and too lazy to restart
	public VerusYPort() {
		super("VerusY");
	}
	
	@Override
	public void onUpdate(UpdateEvent e) {
		MoveUtils.strafe(0.37484F);
		if(mc.thePlayer.hurtTime != 0)
			MoveUtils.strafe(0.4F);
		if(!e.isPre())
			return;
		ticks++;
	}

	@Override
	public void onMove(MoveEvent e) {
		if(!MoveUtils.isMoving()) {
			mc.thePlayer.motionX = 0.0;
			mc.thePlayer.motionZ = 0.0;
			return;
		}
		if(mc.thePlayer.onGround) {
			mc.thePlayer.jump();
			e.setMotionY(.42F);
			if(!mc.gameSettings.keyBindJump.isKeyDown())
				mc.thePlayer.motionY = 0.0;
			MoveUtils.strafe(e, 0.6745F);
		}
	}

	@Override
	public void onPacket(PacketEvent e) {
	}

	@Override
	public void onEnable() {
		ticks = 0;
	}
}
